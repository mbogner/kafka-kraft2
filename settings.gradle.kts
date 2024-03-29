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
rootProject.name = "kafka-kraft"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

val projectModules = mapOf(
    "sample-spring-boot" to "samples/spring-boot",
    "connector-rest-import" to "connectors/custom/rest-import",
)

projectModules.forEach {
    include(it.key)
    project(":${it.key}").projectDir = file(it.value)
}
