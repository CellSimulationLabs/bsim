set DISTDIR=BSimGUI

mkdir %DISTDIR%

xcopy BSimGUI.java %DISTDIR%

xcopy /s "Final GUI text" "%DISTDIR%\Final GUI text"

xcopy ..\..\lib\BSim3.0.jar ".\lib"

javac %DISTDIR%\BSimGUI.java

jar -cmf MANIFEST.MF BSimGUI.jar %DISTDIR%

"C:\Program Files\WinRAR\"rar a BSimGUI.zip -apBSimGUI lib BSimGUI.jar README run_output_only.sh run_output_only.cmd run_all.cmd run_all.sh

del BSimGUI.jar
rd /s %DISTDIR%
rd /s .\lib