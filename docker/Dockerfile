FROM openjdk:11-jre

MAINTAINER Dominic Miglar <dominic.miglar@netunix.at>

RUN apt-get -y update && \
    apt-get -y install \
      git \
      build-essential \
      cmake \
      pkg-config \
      default-libmysqlclient-dev \
      libssl-dev \
      libxml2-dev \
      libcurl4-openssl-dev \
      uuid-dev \
      libjansson-dev \
      libssl-dev

RUN cd /usr/src && \
    git clone https://libwebsockets.org/repo/libwebsockets && \
    cd libwebsockets && \
    git checkout v4.1-stable && \
    mkdir build && \
    cd build && \
    cmake .. && \
    make && \
    make install && \
    ldconfig

RUN echo "#!/bin/sh" > /usr/local/bin/systemctl && \
    chmod +x /usr/local/bin/systemctl

RUN cd /usr/src && \
    git clone https://github.com/horchi/linux-p4d/ && \
    cd linux-p4d && \
    git checkout 0.5.16 && \
    make clean all && \
    make install

WORKDIR /opt/logreposit/froeling-reader-service

COPY run.sh    /opt/logreposit/froeling-reader-service/run.sh
COPY app.jar   /opt/logreposit/froeling-reader-service/app.jar
RUN  chmod 755 /opt/logreposit/froeling-reader-service/run.sh

ENTRYPOINT ["/opt/logreposit/froeling-reader-service/run.sh"]
