#!/bin/bash

javac -cp .:../../lib/core.jar:../../lib/vecmath.jar:../../lib/objimport.jar:../../lib/bsim3.0.jar BSimSimplest.java
java  -cp ..:../../lib/core.jar:../../lib/vecmath.jar:../../lib/objimport.jar:../../lib/bsim3.0.jar BSimSimplest.BSimSimplest
rm *.class
