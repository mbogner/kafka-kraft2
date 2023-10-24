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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JsonParser {
    private static final Logger LOG = LoggerFactory.getLogger(JsonParser.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ARRAY_ROOT_PATH_SEPARATOR = "/";

    public static Map<String, String> parseToMap(final String jsonStr, final String arrayRootPath, final boolean format, final String idPath) {
        try {
            var rootNode = findConfiguredRoot(jsonStr, arrayRootPath);

            // check that the element is an array
            if (!rootNode.isArray()) {
                LOG.warn("no array found under {}", arrayRootPath);
                return Collections.emptyMap();
            }

            // get all the elements from the array as separate strings
            final var size = rootNode.size();
            final Map<String, String> result = new HashMap<>(size);
            final var writer = getWriter(format);
            for (int i = 0; i < size; i++) {
                final var jsonObj = rootNode.get(i);
                final var id = jsonObj.path(idPath);
                result.put(writer.writeValueAsString(id), writer.writeValueAsString(jsonObj));
            }

            return result;
        } catch (final JsonProcessingException exc) {
            throw new JsonParserException("parsing string to json failed:\n" + jsonStr, exc);
        }
    }

    public static String[] parse(final String jsonStr, final String arrayRootPath, final boolean format) {
        try {
            var rootNode = findConfiguredRoot(jsonStr, arrayRootPath);

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
        } catch (final JsonProcessingException exc) {
            throw new JsonParserException("parsing string to json failed:\n" + jsonStr, exc);
        }
    }

    private static JsonNode findConfiguredRoot(final String jsonStr, final String arrayRootPath) {
        try {
            var rootNode = OBJECT_MAPPER.readTree(jsonStr);

            // go down to configured root
            final var pathElements = arrayRootPath.split(ARRAY_ROOT_PATH_SEPARATOR);
            for (final var pathElement : pathElements) {
                rootNode = rootNode.path(pathElement);
            }

            return rootNode;
        } catch (final JsonProcessingException exc) {
            throw new JsonParserException("parsing string to json failed:\n" + jsonStr, exc);
        }
    }

    private static ObjectWriter getWriter(final boolean format) {
        if (format) {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter();
        } else {
            return OBJECT_MAPPER.writer();
        }
    }
}
