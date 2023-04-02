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

* cd to `docker/kraft` directory and run the `download.sh` script to get a local copy of kafka. This step was added to
  cache the file locally and speed up image build. Of course adding the download to the image build would be possible as
  well, but I wanted to avoid re-downloads.
* Then simply start the docker-compose setup: `docker compose up -d`

The setup also includes a health indicator by the use of netcat.

For connections from your dev machine you need to add `/etc/hosts` entries for kraft1, kraft2 and kraft3 targeting
127.0.0.1. Then you can use `kraft1:9192,kraft2:9193,kraft3:9194` as your broker connection string.