set DISTDIR=BSimGUI

set RARDIR="C:\Program Files\WinRAR\"

mkdir %DISTDIR%

xcopy BSimGUI.java %DISTDIR%

xcopy /s "Final GUI text" "%DISTDIR%\Final GUI text"

xcopy ..\..\..\lib\BSim3.0.jar ".\lib"

javac %DISTDIR%\BSimGUI.java

jar -cmf release/resource/MANIFEST.MF BSimGUI.jar %DISTDIR%

cd docs
call build_latex.cmd
cd ..

del release\BSimGUI.zip

%RARDIR%winrar a -afzip -apBSimGUI release/BSimGUI lib 
%RARDIR%winrar a -afzip -apBSimGUI release/BSimGUI -ep @"release\resource\file_list"
pause
del BSimGUI.jar
rd /s %DISTDIR%
rd /s .\lib
