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
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.mbo.kraft.connector.restimport.RestArrayConfig.VERSION;

public class RestArraySourceTask extends SourceTask {

    private static final Logger LOG = LoggerFactory.getLogger(RestArraySourceTask.class);
    private RestArraySource restArraySource;
    private Timer timer;
    private Downloader downloader;

    @Override
    public String version() {
        return VERSION;
    }

    @Override
    public void start(Map<String, String> props) {
        restArraySource = RestArrayConfig.parseProps(props);
        timer = new Timer(restArraySource.pollDelayTimer(), restArraySource.pollDelay());
        downloader = new DownloaderImpl(restArraySource.downloaderBufferSize());
        LOG.info("starting connector task with config {}", restArraySource);
    }

    @Override
    public List<SourceRecord> poll() {
        timer.await();
        LOG.info("getting data for {}", restArraySource);
        final var jsonStr = downloader.download(restArraySource.url(), HttpURLConnection.HTTP_OK);
        final var data = JsonParser.parse(jsonStr, restArraySource.path(), restArraySource.format());
        final List<SourceRecord> result = new ArrayList<>(data.length);
        for (final var entry : data) {
            LOG.debug("creating record: {}", entry);
            final var record = new SourceRecord(
                    null, // sourcePartition
                    null, // sourceOffset
                    // Set the Kafka topic
                    restArraySource.topic(),
                    // Key - you can set the key for the record if needed
                    null, // key
                    // Value - set the entry as the value
                    entry // value
            );
            result.add(record);
        }
        return result;
    }

    @Override
    public void stop() {
        synchronized (this) {
            timer.stop();
            this.notify();
        }
    }
}
