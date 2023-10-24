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

package dev.mbo.kraft.connector.restimport.download;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class DownloaderImpl implements Downloader {

    private final int initialBufferSize;

    public DownloaderImpl(final int initialBufferSize) {
        this.initialBufferSize = initialBufferSize;
    }

    @Override
    public String download(final String urlStr, final int expectedStatusCode) throws DownloadException {
        return download(urlStr, expectedStatusCode, Collections.emptyMap());
    }

    @Override
    public String download(final String urlStr, final int expectedStatusCode, final Map<String, String> headers) throws DownloadException {
        final var url = parseUrlStr(urlStr);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            addHeaders(connection, headers);
            return processResponse(connection, expectedStatusCode);
        } catch (final IOException exc) {
            throw new DownloadException("opening connection failed: " + urlStr, exc);
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }
    }

    private URL parseUrlStr(final String urlStr) {
        assert null != urlStr && !urlStr.isBlank();
        try {
            return new URL(urlStr);
        } catch (final MalformedURLException exc) {
            throw new DownloadException("bad formatted URL: " + urlStr, exc);
        }
    }

    private void addHeaders(final HttpURLConnection connection, final Map<String, String> headers) {
        if (null != headers) {
            for (final var key : headers.keySet()) {
                connection.setRequestProperty(key, headers.get(key));
            }
        }
    }

    private String processResponse(final HttpURLConnection connection, final int expectedStatusCode) {
        assert expectedStatusCode > 99 && expectedStatusCode < 1000;
        try {
            int responseCode = connection.getResponseCode();
            if (responseCode == expectedStatusCode) {
                return readResponseBody(connection);
            } else {
                throw new DownloadException("wrong http response code: " + responseCode);
            }
        } catch (final IOException exc) {
            throw new DownloadException("getting http status code failed", exc);
        }
    }

    private String readResponseBody(final HttpURLConnection connection) {
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = new ByteArrayOutputStream(initialBufferSize)
        ) {
            IOUtils.copy(inputStream, outputStream);
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (final IOException exc) {
            throw new DownloadException("reading response failed", exc);
        }
    }

}
