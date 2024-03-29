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

package dev.mbo.kraft.connector.restimport.util;

public final class ConfigDefUtil {

    public static Integer negativeIntAsNull(final Integer val) {
        if (val < 0) {
            return null;
        }
        return val;
    }

    public static Short negativeShortAsNull(final Short val) {
        if (val < 0) {
            return null;
        }
        return val;
    }

    private ConfigDefUtil() {
        throw new IllegalAccessError();
    }

}
