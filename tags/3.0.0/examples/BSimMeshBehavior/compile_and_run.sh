#!/bin/zsh

javac -cp .:../Library/core.jar:../Library/vecmath.jar:../Library/objimport.jar:../Library/BSim3.0.jar BSimMeshBehavior.java 
java  -cp .:../Library/core.jar:../Library/vecmath.jar:../Library/objimport.jar:../Library/BSim3.0.jar BSimMeshBehavior
rm *.class


