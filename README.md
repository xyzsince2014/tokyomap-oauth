# tokyomap-oauth

<img alt="GitHub top language" src="https://img.shields.io/github/languages/top/xyzsince2014/tokyomap-oauth">
<img alt="GitHub tag (latest by date)" src="https://img.shields.io/github/v/tag/xyzsince2014/tokyomap-oauth">

The OIDC authorisation server for https://www.tokyomap.live

## How to dev
```bash
# run postgres and redis containers beforehand
cd app
./build-war.sh develop

cd ..
./docker-build.sh
./docker-run.sh
```
