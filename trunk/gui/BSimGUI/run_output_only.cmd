REM Only run the generated output file (not the GUI)

REM Compile the generated simulation
javac -classpath .;.\lib\core.jar;.\lib\vecmath.jar;.\lib\BSim3.0.jar MyBSimSimulation.java 

REM Run the generated simulation
java  -classpath .;.\lib\core.jar;.\lib\vecmath.jar;.\lib\BSim3.0.jar MyBSimSimulation
