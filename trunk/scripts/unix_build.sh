#!/bin/zsh

# Delete and re-create the build directory
cd ..
rm -rf build
mkdir build

# Compile the source
cd src/
find bsim -name \*.java -print > file.list
javac -classpath ../lib/ -d ../build/ @file.list

# Copy resources to the new build
cp -R ./bsim/resource ../build/bsim/resource

# Generate the jar file
cd ../build
jar cmf ../scripts/mainClass.txt BSim.jar bsim/

# Return to calling directory
cd ../scripts
