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

package dev.mbo.kraft.connector.restimport;

import org.apache.kafka.common.utils.Time;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simple timer to have a delay in between repeated runs of something.
 */
public class Timer {

    private final Time time = Time.SYSTEM;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private int elapsedCycles;
    private final long frequency;
    private final long delayMs;
    private long lastUpdateMs = 0;

    /**
     * @param frequency Internal frequency how often the timer checks if it has waited long enough. The number of waited
     *                  cycles can be read by <code>getElapsedCycles()</code>
     * @param delayMs   How long to wait before repeated <code>await()</code> returns.
     */
    public Timer(final long frequency, final long delayMs) {
        assert frequency > 0;
        this.frequency = frequency;
        this.delayMs = delayMs;
    }

    /**
     * Method sleeps until <code>delayMs</code> is elapsed. First run has no wait time.
     */
    public void await() {
        assert 0 < delayMs && frequency < delayMs;
        elapsedCycles = 0;
        final var waitUntil = lastUpdateMs + delayMs;
        while (running.get()) {
            if (time.milliseconds() > waitUntil) {
                break;
            }
            elapsedCycles++;
            time.sleep(frequency);
        }
        lastUpdateMs = time.milliseconds();
    }

    public void stop() {
        synchronized (this) {
            running.set(false);
            this.notify();
        }
    }

    public int getElapsedCycles() {
        return elapsedCycles;
    }
}
