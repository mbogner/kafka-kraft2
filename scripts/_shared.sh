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

export CONNECT_SERVER=http://localhost:8083
export CONNECTOR_URL="${CONNECT_SERVER}/connectors"
export TS_FORMAT='+%Y-%m-%dT%H:%M:%SZ'

function die() {
  echo "$1"
  exit 99
}

function utc_ts() {
  TZ='UTC' date "$TS_FORMAT"
}

function import_connect() {
  fullpath=$1

  if [[ ! -f "$fullpath" ]]; then
    die "file $fullpath not found"
  fi

  filename=$(basename "$fullpath")
  extension="${filename##*.}"
  if [[ "json" != "$extension" ]]; then
    die "file $fullpath is not a json file"
  fi

  filename="${filename%.*}"
  target_url="${CONNECTOR_URL}/$filename/config"

  echo "importing $filename from json $fullpath to $target_url"

  curl -i -s -X PUT -H "Content-Type:application/json" -d @"$fullpath" "$target_url"
}
