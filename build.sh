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
sed -i '' -e "s/.appname./.$DIRECTORY./g" $FILE

# fix the file tree
mkdir Soundboard/src/com/grilledcheeseapps/soundboard/$DIRECTORY
cp Soundboard/src/com/grilledcheeseapps/soundboard/appname/SoundboardActivity.java Soundboard/src/com/grilledcheeseapps/soundboard/$DIRECTORY/
rm -rf Soundboard/src/com/grilledcheeseapps/soundboard/appname
sed -i '' -e "s/.appname/.$DIRECTORY/g" Soundboard/src/com/grilledcheeseapps/soundboard/$DIRECTORY/SoundboardActivity.java

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
mkdir -p releases
mv Soundboard/bin/Soundboard-release.apk releases/$FILENAME

# copy to Dropbox for testing
mkdir -p ~/Dropbox/GrilledCheese
cp releases/$FILENAME ~/Dropbox/GrilledCheese/

# cleanup
echo "\ncleaning up...\n\n\n"
#rm -rf Soundboard

exit 0
