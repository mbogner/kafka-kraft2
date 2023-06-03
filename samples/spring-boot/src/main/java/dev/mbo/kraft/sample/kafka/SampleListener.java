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

package dev.mbo.kraft.sample.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class SampleListener {

    private static final Logger LOG = LoggerFactory.getLogger(SampleListener.class);

    /**
     * Sample for batch processing of messages.
     *
     * @param records        The batch of messages.
     * @param acknowledgment Interface to ACK or NACK the records. If processing failed you can tell at which index it
     *                       failed so you ack the ones before and stop at the failed.
     */
    @KafkaListener(topics = {KafkaTopics.SAMPLE_MESSAGES})
    void onMessage(
            final List<ConsumerRecord<String, String>> records,
            final Acknowledgment acknowledgment) {
        LOG.debug("received {} entries", records.size());
        int i = 0;
        try {
            for (final var record : records) {
                process(record);
                i++; // keep track of success index
            }
            LOG.debug("successfully processed batch of {} entries", records.size());
            acknowledgment.acknowledge(); // all fine
        } catch (final Exception exc) {
            LOG.debug("failed at index {}", i);
            acknowledgment.nack(i, Duration.ofMillis(5000)); // NACK at index i
        }
    }

    private void process(final ConsumerRecord<String, String> record) {
        LOG.debug("received record: {}", record.value());
    }

}
