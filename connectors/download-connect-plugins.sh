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

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "${DIR}" || exit 1
BASEDIR=https://d1i4a15mxbxib1.cloudfront.net/api/plugins

FILES=()
# https://github.com/jcustenborder/kafka-connect-spooldir
FILES[1]="jcustenborder-kafka-connect-spooldir-2.0.65.zip;${BASEDIR}/jcustenborder/kafka-connect-spooldir/versions/2.0.65/jcustenborder-kafka-connect-spooldir-2.0.65.zip"
# https://github.com/confluentinc/kafka-connect-datagen
FILES[2]="confluentinc-kafka-connect-datagen-0.6.0.zip;${BASEDIR}/confluentinc/kafka-connect-datagen/versions/0.6.0/confluentinc-kafka-connect-datagen-0.6.0.zip"
# https://github.com/confluentinc/kafka-connect-jdbc
FILES[3]="confluentinc-kafka-connect-jdbc-10.7.1.zip;${BASEDIR}/confluentinc/kafka-connect-jdbc/versions/10.7.1/confluentinc-kafka-connect-jdbc-10.7.1.zip"

function die() {
  echo "$1"
  exit 1
}

rm -rf plugins

for file in ${FILES[*]}; do
  echo "checking for entry ${file}"
  IFS=';' read -r -a file_url <<<"$file"
  filename=${file_url[0]}
  download_url=${file_url[1]}
  if [[ -e $filename ]]; then
    echo "file ${filename} already exists"
  else
    echo "file ${filename} doesn't exist. downloading from ${download_url}"
    curl "${download_url}" --output "${filename}" || die "downloading ${filename} failed"
  fi
  unzip -d plugins "${filename}" >>/dev/null
done
