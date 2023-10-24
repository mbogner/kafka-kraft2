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

package dev.mbo.kraft.connector.restimport.kafka;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ConnectAdminClient {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectAdminClient.class);

    private static Properties props = null;

    public static void createTopicIfMissing(final String bootstrapServers, final String topicName, final int numPartitions, final short replicationFactor) {
        initProperties(bootstrapServers);
        try (final Admin adminClient = Admin.create(props)) {
            if (!doesTopicExist(adminClient, topicName)) {
                createTopic(adminClient, topicName, numPartitions, replicationFactor);
            }
        }
    }

    private static synchronized boolean doesTopicExist(final Admin adminClient, final String topicName) {
        try {
            LOG.info("checking if topic {} exists", topicName);
            final KafkaFuture<Set<String>> existingTopics = adminClient.listTopics().names();
            final Set<String> topicNames = existingTopics.get();
            return topicNames.contains(topicName);
        } catch (final ExecutionException | InterruptedException exc) {
            throw new KafkaAdminException("checking for existing topics failed", exc);
        }
    }

    private static void createTopic(final Admin adminClient, String topicName, final int numPartitions, final short replicationFactor) {
        try {
            LOG.info("creating topic {} (numPartitions={}, replicationFactor={})", topicName, numPartitions, replicationFactor);

            final Optional<Integer> optionalNumPartitions = numPartitions < 1 ? Optional.empty() : Optional.of(numPartitions);
            final Optional<Short> optionalReplicationFactor = replicationFactor < 1 ? Optional.empty() : Optional.of(replicationFactor);

            final var newTopic = new NewTopic(topicName, optionalNumPartitions, optionalReplicationFactor);
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        } catch (final ExecutionException | InterruptedException exc) {
            throw new KafkaAdminException("creating topic " + topicName + " failed", exc);
        }
    }

    private static synchronized void initProperties(final String bootstrapServers) {
        if (null == props) {
            props = new Properties();
            LOG.info("using bootstrapServers for admin: {}", bootstrapServers);
            props.put("bootstrap.servers", bootstrapServers);
        }
    }

}
