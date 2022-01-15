FROM eclipse-temurin:17-jre

MAINTAINER Dominic Miglar <dominic.miglar@netunix.at>

WORKDIR /opt/logreposit/renogy-rover-reader-service

COPY run.sh    /opt/logreposit/renogy-rover-reader-service/run.sh
COPY app.jar   /opt/logreposit/renogy-rover-reader-service/app.jar
RUN  chmod 755 /opt/logreposit/renogy-rover-reader-service/run.sh

ENTRYPOINT ["/opt/logreposit/renogy-rover-reader-service/run.sh"]
