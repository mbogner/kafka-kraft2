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

import dev.mbo.kraft.sample.kafka.model.SomePayload;
import dev.mbo.kraft.sample.kafka.model.SomePayloadRecord;
import dev.mbo.kraft.sample.kafka.sender.KafkaGenericSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class SampleSender {

    private static final Logger LOG = LoggerFactory.getLogger(SampleSender.class);
    private final KafkaGenericSender kafkaGenericSender;

    public SampleSender(final KafkaGenericSender kafkaGenericSender) {
        this.kafkaGenericSender = kafkaGenericSender;
    }

    /**
     * run every 5 seconds
     */
    @Scheduled(cron = "*/5 * * * * *")
    void send() {
        final var rnd = ThreadLocalRandom.current();
        final var cnt = rnd.nextInt(10, 31); // 10-30 records
        LOG.debug("sending {} records(s)", cnt);
        for (final var entry : data(rnd, cnt)) {
            kafkaGenericSender.send(KafkaTopics.SAMPLE_MESSAGES, entry.key(), entry.payload());
        }
    }

    private List<SomePayloadRecord> data(final ThreadLocalRandom rnd, final int cnt) {
        final var data = new ArrayList<SomePayloadRecord>(cnt);
        for (int i = 0; i < cnt; i++) {
            data.add(new SomePayloadRecord(
                    "key" + rnd.nextInt(0, 5), // 0-4
                    new SomePayload(
                            UUID.randomUUID().toString(),
                            rnd.nextDouble(0.0, 10.0), // 0.0-9.9
                            rnd.nextDouble(0.0, 10.0)  // 0.0-9.9
                    )
            ));
        }
        return data;
    }

}
