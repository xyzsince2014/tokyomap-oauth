#!/bin/bash

docker container run -d \
  -p 8080:8080 \
  -v $(pwd)/app/tomcat/logs:/usr/share/tomcat/logs \
  --name oauth \
  --net network_tokyomap \
  --ip 192.168.56.110 \
  --rm \
  --privileged \
  tokyomap.oauth:dev
