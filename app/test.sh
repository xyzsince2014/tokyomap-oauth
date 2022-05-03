#!/bin/bash

echo "execute $(pwd)/$0"

report() {
  mvn help:effective-pom -Doutput=.mvn/effective-pom.xml
  mvn help:effective-settings -Doutput=.mvn/effective-settings.xml
}

test() {
  mvn --settings .mvn/settings.xml -P test clean test
  echo "test completed."
}

report
test
