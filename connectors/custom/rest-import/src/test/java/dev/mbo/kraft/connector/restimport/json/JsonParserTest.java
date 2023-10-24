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

package dev.mbo.kraft.connector.restimport.json;

import dev.mbo.kraft.connector.restimport.FileUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class JsonParserTest {

    private String testFile1Str() throws IOException {
        final var file = "sample-response-formatted.json";
        return FileUtil.resourceToString(file);
    }

    @Test
    void parse() throws Exception {
        final var result = JsonParser.parse(testFile1Str(), "/", false);
        assertThat(result).isNotEmpty();
    }

    @Test
    void parseToMap() throws Exception {
        final var result = JsonParser.parseToMap(testFile1Str(), "/", false, "id");
        assertThat(result).isNotEmpty();
    }

}