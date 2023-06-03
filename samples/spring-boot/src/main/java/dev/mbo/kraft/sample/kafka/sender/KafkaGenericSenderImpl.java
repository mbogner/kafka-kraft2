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

package dev.mbo.kraft.sample.kafka.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Simple implementation using JSON for key and value and sending it via KafkaTemplate.
 */
@Component
class KafkaGenericSenderImpl implements KafkaGenericSender {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaGenericSenderImpl(final KafkaTemplate<String, String> kafkaTemplate,
                                  final ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Async
    @Override
    public <K, V> CompletableFuture<SendResult<String, String>> send(
            final String topic,
            final K key,
            final V value
    ) {
        return send(topic, key, value, null);
    }

    @Async
    @Override
    public <K, V> CompletableFuture<SendResult<String, String>> send(
            final String topic,
            final K key,
            final V value,
            final Map<String, String> headers
    ) {
        try {
            final var keyJson = objectMapper.writeValueAsString(key);
            final var valueJson = objectMapper.writeValueAsString(value);

            final var record = new ProducerRecord<>(
                    topic,
                    null,
                    null,
                    keyJson,
                    valueJson,
                    toHeaderList(headers)
            );

            return kafkaTemplate.send(record);
        } catch (final JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private List<Header> toHeaderList(final Map<String, String> headers) {
        if (null == headers) {
            return Collections.emptyList();
        }
        final var result = new ArrayList<Header>(headers.size());
        for (final var key : headers.keySet()) {
            if (key == null || key.isBlank()) continue;
            final var val = headers.get(key);
            if (val == null || val.isBlank()) continue;
            result.add(new RecordHeader(key, headers.get(key).getBytes(StandardCharsets.UTF_8)));
        }
        return result;
    }
}
