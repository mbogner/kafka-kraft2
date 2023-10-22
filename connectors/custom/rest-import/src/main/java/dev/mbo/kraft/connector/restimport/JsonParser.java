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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonParser {
    private static final Logger LOG = LoggerFactory.getLogger(JsonParser.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ARRAY_ROOT_PATH_SEPARATOR = "/";

    public static String[] parse(final String json, final String arrayRootPath, final boolean format) throws JsonProcessingException {
        var rootNode = OBJECT_MAPPER.readTree(json);

        // go down to configured root
        final var pathElements = arrayRootPath.split(ARRAY_ROOT_PATH_SEPARATOR);
        for (final var pathElement : pathElements) {
            rootNode = rootNode.path(pathElement);
        }

        // check that the element is an array
        if (!rootNode.isArray()) {
            LOG.warn("no array found under {}", arrayRootPath);
            return new String[]{};
        }

        // get all the elements from the array as separate strings
        final var size = rootNode.size();
        final var result = new String[size];
        final var writer = getWriter(format);
        for (int i = 0; i < size; i++) {
            result[i] = writer.writeValueAsString(rootNode.get(i));
        }

        return result;
    }

    private static ObjectWriter getWriter(final boolean format) {
        if (format) {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter();
        } else {
            return OBJECT_MAPPER.writer();
        }
    }
}
