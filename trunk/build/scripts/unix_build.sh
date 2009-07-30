#!/bin/zsh

javac bsim/*.java
javac bsim/logic/*.java
javac bsim/object/*.java
javac bsim/physics/*.java
javac bsim/export/*.java

jar cmf mainClass.txt BSim.jar bsim/
