#!/bin/zsh


cd ../../

javac -d ./build/ bsim/*.java 
javac -d ./build/ bsim/logic/*.java
javac -d ./build/ bsim/object/*.java
javac -d ./build/ bsim/physics/*.java
javac -d ./build/ bsim/export/*.java

cd build

jar cmf ./scripts/mainClass.txt BSim.jar bsim/

cd scripts
