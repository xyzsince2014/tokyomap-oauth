#!/bin/bash

docker container run -d \
  -p 8080:8080 \
  -v $(pwd)/app/tomcat/logs:/usr/share/tomcat/logs \
  --name auth \
  --net network_dev \
  --rm \
  --privileged \
  tokyomap.oauth:dev
