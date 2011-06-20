REM Compile the GUI
javac -classpath .;.\lib\core.jar;.\lib\vecmath.jar;.\lib\BSim3.0.jar BSimGUI.java 

REM Run the GUI
java  -classpath .;.\lib\core.jar;.\lib\vecmath.jar;.\lib\BSim3.0.jar BSimGUI 
REM now wait

REM On GUI exit, compile the generated simulation file
javac -classpath .;.\lib\core.jar;.\lib\vecmath.jar;.\lib\BSim3.0.jar MyBSimSimulation.java 

REM Run the generated simulation file
java  -classpath .;.\lib\core.jar;.\lib\vecmath.jar;.\lib\BSim3.0.jar MyBSimSimulation
