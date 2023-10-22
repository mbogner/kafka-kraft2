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

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class RestArraySourceTask extends SourceTask {

    private static final Logger LOG = LoggerFactory.getLogger(RestArraySourceTask.class);
    private RestArraySource restArraySource;

    @Override
    public String version() {
        return RestArraySourceConnector.VERSION;
    }

    @Override
    public void start(Map<String, String> props) {
        restArraySource = RestArraySourceConnector.parseProps(props);
        LOG.info("starting connector task with config {}", restArraySource);
    }

    @Override
    public List<SourceRecord> poll() {
        LOG.info("getting data for {}", restArraySource);
        return null;
    }

    @Override
    public void stop() {
        synchronized (this) {
            // shutdown
            this.notify();
        }
    }
}
