#!/bin/bash

javac -cp .:../../lib/core.jar:../../lib/vecmath.jar:../../lib/objimport.jar:../../lib/bsim3.0.jar BSimMultiThreaded.java
java  -cp ..:../../lib/core.jar:../../lib/vecmath.jar:../../lib/objimport.jar:../../lib/bsim3.0.jar BSimMultiThreaded.BSimMultiThreaded
rm *.class
