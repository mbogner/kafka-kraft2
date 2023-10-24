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
import org.apache.kafka.common.utils.AppInfoParser;

import java.util.Map;

final class RestArrayConfig {

    public static final String VERSION = AppInfoParser.getVersion();

    public static final String URL_CONFIG = "url";
    public static final String TOPIC_CONFIG = "topic";
    public static final String PATH_CONFIG = "path";
    public static final String FORMAT_CONFIG = "format";
    public static final String POLL_DELAY_CONFIG = "poll.delay.ms";
    public static final String POLL_DELAY_TIMER_CONFIG = "poll.delay.timer.ms";
    public static final String DOWNLOADER_BUFFER_SIZE_BYTES_CONFIG = "downloader.buffer.size.bytes";

    public static final String DEFAULT_PATH_CONFIG = "/";
    public static final boolean DEFAULT_FORMAT_CONFIG_CONFIG = false;
    public static final long DEFAULT_POLL_DELAY_CONFIG = 10_000L;
    public static final long DEFAULT_POLL_DELAY_TIMER_CONFIG = 1_000L;
    private static final int DEFAULT_DOWNLOADER_BUFFER_SIZE_BYTES_CONFIG = 20480; // 20kb

    static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(URL_CONFIG, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE, new ConfigDef.NonEmptyString(), ConfigDef.Importance.HIGH, "URL to download from. No Default!")
            .define(TOPIC_CONFIG, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE, new ConfigDef.NonEmptyString(), ConfigDef.Importance.HIGH, "The topic to publish data to. No default!")
            .define(PATH_CONFIG, ConfigDef.Type.STRING, DEFAULT_PATH_CONFIG, new ConfigDef.NonEmptyString(), ConfigDef.Importance.HIGH, "The path to the array containing data. Default: " + DEFAULT_PATH_CONFIG)
            .define(FORMAT_CONFIG, ConfigDef.Type.BOOLEAN, DEFAULT_FORMAT_CONFIG_CONFIG, ConfigDef.Importance.MEDIUM, "Format the json data in the topic. Doing so results in higher storage cost. Default: " + DEFAULT_FORMAT_CONFIG_CONFIG)
            .define(POLL_DELAY_CONFIG, ConfigDef.Type.LONG, DEFAULT_POLL_DELAY_CONFIG, ConfigDef.Importance.HIGH, "Time between GET requests to configured url in milliseconds. Default: " + DEFAULT_POLL_DELAY_CONFIG)
            .define(POLL_DELAY_TIMER_CONFIG, ConfigDef.Type.LONG, DEFAULT_POLL_DELAY_TIMER_CONFIG, ConfigDef.Importance.LOW, "Time between checks of the timer if it has waited enough in milliseconds. Higher number means more precision but also higher load. Default: " + DEFAULT_POLL_DELAY_TIMER_CONFIG)
            .define(DOWNLOADER_BUFFER_SIZE_BYTES_CONFIG, ConfigDef.Type.INT, DEFAULT_DOWNLOADER_BUFFER_SIZE_BYTES_CONFIG, ConfigDef.Importance.LOW, "Buffer size of the downloader. Should be slightly bigger than typical expected result. Default: " + DEFAULT_DOWNLOADER_BUFFER_SIZE_BYTES_CONFIG);

    public static RestArraySource parseProps(final Map<String, String> props) {
        final var config = new AbstractConfig(CONFIG_DEF, props);
        return new RestArraySource(
                config.getString(URL_CONFIG),
                config.getString(TOPIC_CONFIG),
                config.getString(PATH_CONFIG),
                config.getBoolean(FORMAT_CONFIG),
                config.getLong(POLL_DELAY_CONFIG),
                config.getLong(POLL_DELAY_TIMER_CONFIG),
                config.getInt(DOWNLOADER_BUFFER_SIZE_BYTES_CONFIG)
        );
    }

    private RestArrayConfig() {
        throw new IllegalAccessError();
    }
}
