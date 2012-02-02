#!/bin/sh

#Version code
VC=4
#Version Name
VN=1.5

echo "\n"
echo "Building All Soundboards for version $VN \n"

./build.sh "Fantastic Mr. Fox Soundboard" fantastic_mr_fox $VC $VN

./build.sh "Scott Pilgrim Soundboard" scott_pilgrim $VC $VN

./build.sh "Napoleon Dynamite Soundboard" napoleon_dynamite $VC $VN

./build.sh "Nacho Libre Soundboard" nacho_libre $VC $VN

./build.sh "Austin Powers Soundboard" powers $VC $VN

./build.sh "Marcel The Shell Soundboard" shell $VC $VN
