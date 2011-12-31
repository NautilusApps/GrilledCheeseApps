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

# customize manifest
FILE="Soundboard/AndroidManifest.xml"
sed -i '' -e "s/com.grilledcheeseapps.soundboard/com.grilledcheeseapps.soundboard.$DIRECTORY/g" $FILE
sed -i '' -e "s/android:versionCode=\"1\"/android:versionCode=\"$VER_CODE\"/g" $FILE
sed -i '' -e "s/VERSION_NAME/$VER_NAME/g" $FILE
sed -i '' -e "s/android:debuggable=\"true\"//g" $FILE
sed -i '' -e "s/APP_NAME/$APP_NAME/g" $FILE

# place res files
cp header.png Soundboard/res/drawable-hdpi/
cp icon.png Soundboard/res/drawable-hdpi/

# place asset files
cp -f settings.json Soundboard/assets/
cp -rf backgrounds Soundboard/assets/
cp -rf clips Soundboard/assets/

# build the project
cd Soundboard
ant release
cd ..

# copy results
FILENAME=$DIRECTORY-$VER_CODE-$VER_NAME.apk
mkdir releases
mv Soundboard/bin/Soundboard.apk releases/$FILENAME

# copy to Dropbox for testing
mkdir ~/Dropbox/GrilledCheese
cp releases/$FILENAME ~/Dropbox/GrilledCheese/

# cleanup
echo "cleaning up...\n"
rm -rf Soundboard

exit 0
