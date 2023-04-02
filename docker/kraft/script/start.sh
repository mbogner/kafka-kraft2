#!/bin/bash
#
# Copyright (c) 2023.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
if [[ "$KAFKA_CLUSTER_ID" == "" ]]; then
  echo "WARNING: KAFKA_CLUSTER_ID was not set in env. creating a random id."
  KAFKA_CLUSTER_ID="$(bin/kafka-storage.sh random-uuid)"
  export KAFKA_CLUSTER_ID
  echo "WARNING: generated cluster id is $KAFKA_CLUSTER_ID"
else
  echo "using cluster id $KAFKA_CLUSTER_ID"
fi
bin/kafka-storage.sh format -t "$KAFKA_CLUSTER_ID" -c "$KAFKA_KRAFT_CONFIG"
bin/kafka-server-start.sh "$KAFKA_KRAFT_CONFIG"