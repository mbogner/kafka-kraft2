/*
 * Copyright (c) 2023.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.mbo.kraft.connector.restimport;

import dev.mbo.kraft.connector.restimport.download.Downloader;
import dev.mbo.kraft.connector.restimport.download.DownloaderImpl;
import dev.mbo.kraft.connector.restimport.json.JsonParser;
import dev.mbo.kraft.connector.restimport.kafka.ConnectAdminClient;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static dev.mbo.kraft.connector.restimport.RestArrayConfig.VERSION;

public class RestArraySourceTask extends SourceTask {

    private static final Logger LOG = LoggerFactory.getLogger(RestArraySourceTask.class);
    private RestArraySource restArraySource;
    private Timer timer;
    private Downloader downloader;
    private static boolean initTopic = true;

    private final Schema keySchema = SchemaBuilder.string().optional().build();
    private final Schema valueSchema = SchemaBuilder.string().optional().build();


    @Override
    public String version() {
        return VERSION;
    }

    @Override
    public void start(Map<String, String> props) {
        restArraySource = RestArrayConfig.parseProps(props);
        timer = new Timer(restArraySource.pollDelayTimer(), restArraySource.pollDelay());
        downloader = new DownloaderImpl(restArraySource.downloaderBufferSize());

        if (initTopic) {
            initTopic = false;
            LOG.info("initialise topic {} if missing", restArraySource.topic());
            ConnectAdminClient.createTopicIfMissing(
                    restArraySource.bootstrapServers(),
                    restArraySource.topic(),
                    restArraySource.numPartitions(),
                    restArraySource.replicationFactor()
            );
        }

        LOG.info("starting connector task with config {}", restArraySource);
    }

    @Override
    public List<SourceRecord> poll() {
        timer.await();
        LOG.info("getting data for {}", restArraySource);
        final var jsonStr = downloader.download(restArraySource.url(), HttpURLConnection.HTTP_OK);
        final var data = JsonParser.parseToMap(jsonStr, restArraySource.path(), restArraySource.format(), restArraySource.idPath());

        final long importTs = Instant.now().toEpochMilli();
        final List<SourceRecord> result = new ArrayList<>(data.size());
        for (final var key : data.keySet()) {
            final var entry = data.get(key);
            LOG.debug("creating record id={}, entry={}", key, entry);
            final var record = new SourceRecord(
                    null, // sourcePartition
                    null, // sourceOffset
                    restArraySource.topic(),
                    null, // partition
                    keySchema, // keySchema
                    key, // key
                    valueSchema, // valueSchema
                    entry, // value,
                    importTs, // timestamp
                    Collections.emptyList() // headers
            );
            result.add(record);
        }
        return result;
    }

    @Override
    public void stop() {
        synchronized (this) {
            if (timer != null) {
                timer.stop();
            }
            this.notify();
        }
    }
}
