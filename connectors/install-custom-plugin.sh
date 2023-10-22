#!/usr/bin/env bash
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
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
GRADLE=../../../gradlew
NAME1="connector-rest-import"

cd "${DIR}" || exit 1
rm -rf ${NAME1}-*.zip plugins/${NAME1}-*

cd "${DIR}/custom/rest-import" || exit 2
$GRADLE clean connector
cp build/connector/${NAME1}-*.zip "$DIR"

cd "${DIR}" || exit 1
unzip -d plugins ${NAME1}-*.zip >>/dev/null
