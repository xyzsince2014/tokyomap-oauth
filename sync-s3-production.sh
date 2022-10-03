#!/bin/bash

aws s3 sync --delete app/src/profiles/production/resources/conf/ s3://tokyomap-oauth/resources/conf/
aws s3 ls --recursive s3://tokyomap-oauth/resources/conf
