#!/bin/sh

echo "Starting application ..."
java -Djava.security.egd=file:/dev/./urandom -jar /opt/logreposit/renogy-rover-reader-service/app.jar
