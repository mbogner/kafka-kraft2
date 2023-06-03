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

import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Simple sender that take care of transformation of key and value.
 */
public interface KafkaGenericSender {

    @Async
    <K, V> CompletableFuture<SendResult<String, String>> send(
            final String topic,
            final K key,
            final V value
    );

    @Async
    <K, V> CompletableFuture<SendResult<String, String>> send(
            final String topic,
            final K key,
            final V value,
            final Map<String, String> headers
    );


}
