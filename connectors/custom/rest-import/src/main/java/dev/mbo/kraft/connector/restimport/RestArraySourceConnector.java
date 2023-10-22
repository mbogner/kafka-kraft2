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

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class RestArraySourceConnector extends SourceConnector {

    private static final Logger LOG = LoggerFactory.getLogger(RestArraySourceConnector.class);
    public static final String VERSION = AppInfoParser.getVersion();

    public static final String URL_CONFIG = "url";
    public static final String TOPIC_CONFIG = "topic";
    public static final String PATH_CONFIG = "path";
    public static final String FORMAT_CONFIG = "format";

    static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(URL_CONFIG, Type.STRING, ConfigDef.NO_DEFAULT_VALUE, new ConfigDef.NonEmptyString(), Importance.HIGH, "URL to download from")
            .define(TOPIC_CONFIG, Type.STRING, ConfigDef.NO_DEFAULT_VALUE, new ConfigDef.NonEmptyString(), Importance.HIGH, "The topic to publish data to")
            .define(PATH_CONFIG, Type.STRING, "/", new ConfigDef.NonEmptyString(), Importance.HIGH, "The path to the array containing data")
            .define(FORMAT_CONFIG, Type.BOOLEAN, false, Importance.MEDIUM, "Format the json data in the topic. Doing so results in higher storage cost");

    private RestArraySource restArraySource;

    public static RestArraySource parseProps(final Map<String, String> props) {
        final var config = new AbstractConfig(CONFIG_DEF, props);
        return new RestArraySource(
                config.getString(URL_CONFIG),
                config.getString(TOPIC_CONFIG),
                config.getString(PATH_CONFIG),
                config.getBoolean(FORMAT_CONFIG)
        );
    }

    @Override
    public void start(final Map<String, String> props) {
        restArraySource = parseProps(props);
        LOG.info("starting rest source connector with config {}", restArraySource);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return RestArraySourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(final int maxTasks) {
        final var configs = new ArrayList<Map<String, String>>();

        final var config = new HashMap<String, String>();
        config.put(URL_CONFIG, restArraySource.url());
        config.put(TOPIC_CONFIG, restArraySource.topic());
        config.put(PATH_CONFIG, restArraySource.path());
        config.put(FORMAT_CONFIG, String.valueOf(restArraySource.format()));
        configs.add(config);

        return configs;
    }

    @Override
    public void stop() {
        // Nothing to do since RestSourceConnector has no background monitoring.
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public String version() {
        return VERSION;
    }
}
