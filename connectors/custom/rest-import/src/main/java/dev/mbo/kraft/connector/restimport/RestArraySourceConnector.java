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

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class RestArraySourceConnector extends SourceConnector {

    private static final Logger LOG = LoggerFactory.getLogger(RestArraySourceConnector.class);

    private RestArraySource restArraySource;

    @Override
    public void start(final Map<String, String> props) {
        restArraySource = RestArrayConfig.parseProps(props);
        LOG.info("starting rest source connector with config {}", restArraySource);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return RestArraySourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(final int maxTasks) {
        return restArraySource.taskConfigs();
    }

    @Override
    public void stop() {
        // Nothing to do since RestSourceConnector has no background monitoring.
    }

    @Override
    public ConfigDef config() {
        return RestArrayConfig.CONFIG_DEF;
    }

    @Override
    public String version() {
        return RestArrayConfig.VERSION;
    }
}
