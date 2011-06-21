set EXAMPLENAME="BSimTutorial"

javac -cp ../;../../lib/core.jar;../../lib/vecmath.jar;../../lib/objimport.jar;../../lib/bsim3.0.jar %EXAMPLENAME%.java
java  -cp ../;../../lib/core.jar;../../lib/vecmath.jar;../../lib/objimport.jar;../../lib/bsim3.0.jar %EXAMPLENAME%.%EXAMPLENAME%
del ".\*.class"
