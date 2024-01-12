# renogy-rover-reader-service

| branch | CI build | test coverage |
|--------|:--------:|--------------:|
| master  | [![CircleCI](https://circleci.com/gh/logreposit/renogy-rover-reader-service/tree/master.svg?style=shield)](https://circleci.com/gh/logreposit/renogy-rover-reader-service/tree/master)   | [![codecov.io](https://codecov.io/gh/logreposit/renogy-rover-reader-service/branch/master/graphs/badge.svg)](https://codecov.io/gh/logreposit/renogy-rover-reader-service/branch/master/graphs/badge.svg)   |
| develop | [![CircleCI](https://circleci.com/gh/logreposit/renogy-rover-reader-service/tree/develop.svg?style=shield)](https://circleci.com/gh/logreposit/renogy-rover-reader-service/tree/develop) | [![codecov.io](https://codecov.io/gh/logreposit/renogy-rover-reader-service/branch/develop/graphs/badge.svg)](https://codecov.io/gh/logreposit/renogy-rover-reader-service/branch/develop/graphs/badge.svg) |


## Service Description

The renogy-rover-reader-service reads measurement and fault data from Renogy Rover compatible solar 
charge controllers using the Modbus RTU protocol over the RS232 interface and pushes it to the 
Logreposit API.

The implementation has been done with a TOYO SR-2440 MPPT Solar Charge Controller. The name of the 
service `renogy-rover-reader-service` has been chosen because the [Modbus documentation](https://github.com/logreposit/renogy-rover-reader-service/blob/develop/doc/rover_modbus.pdf) 
is labeled with the Renogy brand (and I really could not find out which of those brands/companies is 
the actual manufacturer 😅).

Possible model descriptions are (list not finished):
- Renogy Rover 20/30/40/60/.. MPPT Solar Charge Controller
- TOYO SR-ML2420
- TOYO SR-ML2430
- TOYO SR-ML2440
- SRNE ML2420
- SRNE ML2430
- SRNE ML2440

The `renogy-rover-reader-service` is a Spring Boot project and the library [`com.ghgande.j2mod`](https://mvnrepository.com/artifact/com.ghgande/j2mod) 
is in use for the serial modbus communication.


## Configuration

This service ships as a docker image and has to be configured via environment variables. 

|Environment Variable Name          | default value              |                      |
|-----------------------------------|----------------------------|----------------------|
| RENOGY_COMPORT                    | /dev/ttyUSB0               |                      |
| LOGREPOSIT_APIBASEURL             | https://api.logreposit.com |                      |
| LOGREPOSIT_DEVICETOKEN            | **INVALID**                | needs to be changed! | 
| LOGREPOSIT_SCRAPEINTERVALINMILLIS | 15000                      |                      |


## Docker

The latest images can be found on [Dockerhub](https://hub.docker.com/r/logreposit/renogy-rover-reader-service/tags).

Place the following `docker-compose.yml` file in a new folder, then run `docker-compose up -d`.

```yaml
version: '2.4'

services:
  renogy-rover-reader-service:
    container_name: logreposit-renogy-rover-reader-service
    image: logreposit/renogy-rover-reader-service:v0.4.0
    restart: always
    devices:
      - "/dev/ttyUSB0:/dev/ttyUSB0"
    environment:
      LOGREPOSIT_DEVICETOKEN: your-logreposit-device-token
```
