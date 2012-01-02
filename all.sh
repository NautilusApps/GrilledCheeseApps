#!/bin/sh

VERSION_CODE=1
VERSION_NAME=1.0

echo "\n"
echo "Building All Soundboards for version $VERSION_NAME \n"

./build.sh "Fantastic Mr. Fox Soundboard" fantastic_mr_fox $VERSION_CODE $VERSION_NAME

./build.sh "Scott Pilgrim Soundboard" scott_pilgrim $VERSION_CODE $VERSION_NAME
