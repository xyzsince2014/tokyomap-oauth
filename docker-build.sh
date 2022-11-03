#!/bin/bash
docker image rm tokyomap.oauth:dev
docker build -t tokyomap.oauth:dev app
