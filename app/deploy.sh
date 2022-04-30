#!/bin/bash

echo "execute $(pwd)/$0"

if [ $# != 1 ]; then
  echo "invalid number of args."
  exit 1
fi

buildWar() {
  # todo: build and deploy should be done by Jenkins or something
    mvn help:effective-pom -Doutput=.mvn/effective-pom.xml
    mvn help:effective-settings -Doutput=.mvn/effective-settings.xml
    mvn --settings .mvn/settings.xml -P test clean test

    #mvn --settings .mvn/settings.xml -P production -Dmaven.test.skip=true package # todo
    mvn --settings .mvn/settings.xml -P $1 -Dmaven.test.skip=true package

    echo "buildWar() completed."
}

deploy() {
  rm -rf ./tomcat/webapps
  mkdir -p ./tomcat/webapps
  cp ./target/ROOT.war ./tomcat/webapps
  echo "deploy() completed."
}

#todo
#buildImage () {
#  docker build -t <ECR Repo>:<ver> .
#  docker build -t test:dev .
#}

PROFILES=("develop" "test")

for PROFILE in ${PROFILES[@]}; do
  if [ $PROFILE != $1 ]; then
    continue;
  fi

  buildWar $1
  deploy
  #todo  buildImage

  exit 0
done

echo "invalid args."
exit 1
