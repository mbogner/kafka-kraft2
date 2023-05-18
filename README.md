# kafka kraft

This is a simple example of how to use apache kafka without zookeeper and any external dependencies to pre-built docker
containers. The setup contains of 3 instances of kafka in kraft mode:

- kraft1:9192
- kraft2:9193
- kraft3:9194

Ports were change in every instance for easier local cluster setup. All of them store data in the
volume `/data/kraft-combined-logs`. A compose file for starting the cluster is included - see `docker-compose.yml`.
The default EXPOSE 9092 was omitted as this can be easily changed in the server.properties file.

## Usage

* cd to `docker/kraft` directory and run the `download-kafka.sh` script to get a local copy of kafka. This step was
  added to cache the file locally and speed up image build. Of course adding the download to the image build would be
  possible as well, but I wanted to avoid re-downloads.
* cd to `connectors` and run `download-connect-plugins.sh` to download some sample plugins.
* run the script `docker-compose-build.sh`. This builds the used kafka container and avoids rebuilding the same image
  multiple times. You can of course also run `docker compose -f docker-compose-build.yml build` yourself which is what
  the script wraps for you.
* Then simply start the docker-compose setup: `docker compose up -d`

For connections from your dev machine you need to add `/etc/hosts` entries for kraft1, kraft2 and kraft3 targeting
127.0.0.1. Then you can use `kraft1:9192,kraft2:9193,kraft3:9194` as your broker connection string.

## Additional Services

### Kafdrop

Web UI for viewing Kafka topics and browsing consumer groups. Availbale under http://localhost:9000.

### Schema Registry

Next to the server a schema registry is started an mapped to http://localhost:8081.

### KSQLDB Server

ksqlDB enables you to build event streaming applications leveraging your familiarity with relational databases.

Server info: http://127.0.0.1:8088/info

To get a shell run this command:

```shell
docker exec -it ksqldb-server ksql
```

It will default to server under http://127.0.0.1:8088.

### Kafka Connect

Kafka Connect is a tool for scalably and reliably streaming data between Apache Kafka and other data systems.

REST API available via http://127.0.0.1:8083