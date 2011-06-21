set DISTDIR=BSimGUI

set RARDIR="C:\Program Files\WinRAR\"

mkdir %DISTDIR%

xcopy BSimGUI.java %DISTDIR%

xcopy /s "Final GUI text" "%DISTDIR%\Final GUI text"

xcopy ..\..\lib\BSim3.0.jar ".\lib"

javac %DISTDIR%\BSimGUI.java

jar -cmf release/resource/MANIFEST.MF BSimGUI.jar %DISTDIR%

cd docs
call build_latex.cmd
cd ..

del release\BSimGUI.zip

%RARDIR%rar a release/BSimGUI.zip -apBSimGUI lib 
%RARDIR%rar a release/BSimGUI.zip -apBSimGUI -ep @"release\resource\file_list"

del BSimGUI.jar
rd /s %DISTDIR%
rd /s .\lib
