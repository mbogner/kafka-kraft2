# Table of Contents

- [Overview](#overview)
- [Configuration](#configuration)

# Overview

This is a simple connector that allows to inject content from JSON endpoints into a kafka topic. You can configure the
path the expected array of data starts. Every entry needs an id which is also used as key in kafka wich is important for
partitioning.

# Configuration

Here a template for configuring this simple connect plugin:

```json
{
  "connector.class": "dev.mbo.kraft.connector.restimport.RestArraySourceConnector",
  "url": "https://your-endpoint",
  "topic": "topic-name",
  "path": "/",
  "id.path": "id",
  "format": "false",
  "poll.delay.ms": "10000",
  "poll.delay.timer.ms": "1000",
  "bootstrap.servers": "kraft1:9192,kraft2:9193,kraft3:9194"
}
```