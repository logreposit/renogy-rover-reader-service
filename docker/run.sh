#!/bin/sh

echo "Starting application ..."
java -Xmx256m -Djava.security.egd=file:/dev/./urandom -jar /opt/logreposit/renogy-rover-reader-service/app.jar
