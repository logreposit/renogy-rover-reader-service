# renogy-rover-reader-service

| branch | CI build | test coverage |
|--------|:--------:|--------------:|
| master  | [![CircleCI](https://circleci.com/gh/logreposit/renogy-rover-reader-service/tree/master.svg?style=shield)](https://circleci.com/gh/logreposit/renogy-rover-reader-service/tree/master)   | [![codecov.io](https://codecov.io/gh/logreposit/renogy-rover-reader-service/branch/master/graphs/badge.svg)](https://codecov.io/gh/logreposit/renogy-rover-reader-service/branch/master/graphs/badge.svg)   |
| develop | [![CircleCI](https://circleci.com/gh/logreposit/renogy-rover-reader-service/tree/develop.svg?style=shield)](https://circleci.com/gh/logreposit/renogy-rover-reader-service/tree/develop) | [![codecov.io](https://codecov.io/gh/logreposit/renogy-rover-reader-service/branch/develop/graphs/badge.svg)](https://codecov.io/gh/logreposit/renogy-rover-reader-service/branch/develop/graphs/badge.svg) |

## Service Description

The renogy-rover-reader-service reads measurement and fault data from Renogy Rover compatible solar charge controllers 
using the Modbus RTU protocol over the RS232 interface and pushes it to the Logreposit API.

## Docker

The latest images can be found on [Dockerhub](https://hub.docker.com/r/logreposit/renogy-rover-reader-service/tags).

## Configuration

This service ships as a docker image and has to be configured via environment variables. 

|Environment Variable Name          | default value              |                      |
|-----------------------------------|----------------------------|----------------------|
| RENOGY_COMPORT                    | /dev/ttyUSB0               |                      |
| LOGREPOSIT_APIBASEURL             | https://api.logreposit.com |                      |
| LOGREPOSIT_DEVICETOKEN            | **INVALID**                | needs to be changed! | 
| LOGREPOSIT_SCRAPEINTERVALINMILLIS | 15000                      |                      |

