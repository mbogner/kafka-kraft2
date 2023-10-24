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

package dev.mbo.kraft.connector.restimport.download;

import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;

class DownloaderImplTest {

    @Test
    void download() {
        final Downloader downloader = new DownloaderImpl(1024);
        final var result = downloader.download("https://api.genderize.io?name=peter", HttpURLConnection.HTTP_OK);
        assertThat(result).isNotBlank();
        assertThat(result).startsWith("{");
        assertThat(result).endsWith("}");
    }

}