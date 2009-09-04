#!/bin/zsh

java -cp .:../lib/core.jar:../lib/PeasyCam.jar:../lib/vecmath.jar:../lib/video.jar -Xms512m -Xmx1024m -jar ../build/BSim.jar
