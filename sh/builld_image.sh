#!/bin/bash

./deploy_war

docker build -t test:dev . # todo: docker build -t <ECR Repo>:<ver> .
