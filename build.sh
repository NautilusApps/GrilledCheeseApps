#!/bin/sh

# soundboard properties
APP_NAME=$1
DIRECTORY=$2
VER_CODE=$3
VER_NAME=$4
echo "===================================================="
echo "App name: $APP_NAME"
echo "Resource directory: $DIRECTORY"
echo "Version code: $VER_CODE"
echo "Version name: $VER_NAME"
echo "====================================================\n"

# copy project into folder
cp -R Soundboard $DIRECTORY

# move into folder
cd $DIRECTORY

# ???
ls






# cleanup
echo "cleaning up...\n"
rm -rf Soundboard


exit 0
