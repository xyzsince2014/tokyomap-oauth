#!/bin/bash

if [ $# != 1 ]; then
  echo "invalid number of args."
  exit 1
fi

echo "execute $(pwd)/$0"

report() {
  mvn help:effective-pom -Doutput=.mvn/effective-pom.xml
  mvn help:effective-settings -Doutput=.mvn/effective-settings.xml
}

test() {
  mvn --settings .mvn/settings.xml -P test clean test
  echo "test completed."
}

# todo: build and deploy should be done by Jenkins or something
build() {
  # todo: mvn --settings .mvn/settings.xml -P prod -Dmaven.test.skip=true package
  mvn --settings .mvn/settings.xml -P $1 -Dmaven.test.skip=true package
  echo "build completed."
}

deploy() {
  rm -rf ./tomcat/webapps
  mkdir -p ./tomcat/webapps
  cp ./target/ROOT.war ./tomcat/webapps
  echo "deploy completed."
}

# todo
#buildImage () {
#  docker build -t <ECR Repo>:<ver> .
#  docker build -t test:dev .
#}

PROFILES=("develop" "test" "production")

for PROFILE in ${PROFILES[@]}; do
  if [ $PROFILE != $1 ]; then
    continue;
  fi

  report
  test
  build $1
  deploy
  #todo  buildImage

  exit 0
done

echo "invalid args."
exit 1
