FROM ubuntu:24.04

ADD kafka.tgz /opt/
RUN ln -s /opt/kafka_* /opt/kafka && \
    apt update && \
    apt install -y openjdk-21-jdk-headless netcat-traditional && \
    apt clean && apt autoclean && rm -rf /var/lib/apt/lists/*

COPY script/* /opt/kafka/

ENV KAFKA_HOME=/opt/kafka \
    KAFKA_KRAFT_CONFIG=/opt/kafka/config/kraft/server.properties
WORKDIR /opt/kafka
RUN useradd --home-dir /opt/kafka -ms /bin/bash kafka && \
    chown -R kafka:kafka /opt/kafka* && \
    chmod +x /opt/kafka/*.sh

CMD ["/opt/kafka/start.sh"]
VOLUME /data/kraft-combined-logs
