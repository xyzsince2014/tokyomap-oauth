#!/bin/bash

# copy the war in the target into tomcat's webapps
rm -rf ../tomcat/webapps
mkdir -p ../tomcat/webapps
cp ../target/ROOT.war ../tomcat/webapps
