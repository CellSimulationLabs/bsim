#!/bin/bash

# Compile the GUI
javac -classpath .;./lib/core.jar;./lib/vecmath.jar;./lib/BSim3.0.jar BSimGUI.java 

# Run the GUI
java  -classpath .;./lib/core.jar;./lib/vecmath.jar;./lib/BSim3.0.jar BSimGUI 
# now wait

# On GUI exit, compile the generated simulation file
javac -classpath .;./lib/core.jar;./lib/vecmath.jar;./lib/BSim3.0.jar MyBSimSimulation.java 

# Run the generated simulation file
java  -classpath .;./lib/core.jar;./lib/vecmath.jar;./lib/BSim3.0.jar MyBSimSimulation
