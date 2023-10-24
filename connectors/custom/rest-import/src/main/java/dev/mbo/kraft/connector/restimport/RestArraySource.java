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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.mbo.kraft.connector.restimport.RestArrayConfig.FORMAT_CONFIG;
import static dev.mbo.kraft.connector.restimport.RestArrayConfig.PATH_CONFIG;
import static dev.mbo.kraft.connector.restimport.RestArrayConfig.POLL_DELAY_CONFIG;
import static dev.mbo.kraft.connector.restimport.RestArrayConfig.POLL_DELAY_TIMER_CONFIG;
import static dev.mbo.kraft.connector.restimport.RestArrayConfig.TOPIC_CONFIG;
import static dev.mbo.kraft.connector.restimport.RestArrayConfig.*;

record RestArraySource(
        String url,
        String topic,
        String path,
        boolean format,
        long pollDelay,
        long pollDelayTimer,
        int downloaderBufferSize
) {

    public List<Map<String, String>> taskConfigs() {
        final var configs = new ArrayList<Map<String, String>>();

        final var config = new HashMap<String, String>();
        config.put(URL_CONFIG, url);
        config.put(TOPIC_CONFIG, topic);
        config.put(PATH_CONFIG, path);
        config.put(FORMAT_CONFIG, String.valueOf(format));
        config.put(POLL_DELAY_CONFIG, String.valueOf(pollDelay));
        config.put(POLL_DELAY_TIMER_CONFIG, String.valueOf(pollDelayTimer));
        config.put(DOWNLOADER_BUFFER_SIZE_BYTES_CONFIG, String.valueOf(downloaderBufferSize));
        configs.add(config);

        return configs;
    }

}
