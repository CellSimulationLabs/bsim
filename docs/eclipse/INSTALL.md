#Introduction

The BSim package is available either as a standalone pre-compiled .jar file, an archive of the source code, or directly from the SVN repository. For further details of the differences between BSim versions, please see our summary page.

If you do not need to modify the BSim code in any way, pre-compiled versions of the code are available to download (see the summary page for further details). These can be compiled against in the same way as any other Java library, by simply using the BSim methods in your code and including the relevant .jar on the Java build path.

In many cases it it likely that the code may need to be extended for a specific purpose. In this case, archived source code is available (see the summary page for further details).

Additionally it is possible to obtain the source code from the SVN repository. At the time of writing, the "Trunk" corresponds to our working copy for the 2010 iGEM competition and it is therefore recommended that for the majority of users that a more extensively tested version be downloaded (one of the tagged versions). For users unfamiliar with SVN, we provide a brief overview here of setting up an IDE and downloading BSim. It is of course possible to use BSim with any Java IDE or standalone SVN clients, the method illustrated here is just the one with which I am most familiar.

Downloading BSim using SVN with Eclipse
Eclipse is a popular IDE for Java which includes subversion management. For the purposes of this tutorial, we will be using the "Eclipse IDE for Java Developers", with the "Subclipse" subversion plugin.

Obtaining Eclipse
The latest version of Eclipse can be downloaded from here. In this tutorial we use the "Eclipse IDE for Java Developers", please be aware that other versions (e.g. those specifically for web developers) may include an incompatible version of Java by default which is why we are using this version.

Note: When you start up Eclipse, it will ask for a workspace directory. This can be any directory of your choice but bear in mind that this is the directory where Eclipse will store all of your code in a session. You can create more than one workspace folder for different projects etc.

Setting up an SVN plugin
To get the code from Google, we need to install an SVN plugin, as Eclipse doesn't come with one included by default (yet). We will be using the Subclipse plugin.

Click on "Help -> Install New Software..." and a window should pop up called 'Available Software'.

In the 'Work with' box, paste in
	http://subclipse.tigris.org/update_1.6.x
click the 'add' button and call it Subclipse website, or something similar.

You should now have 3 new options in the main box, tick all 3 (SVNKit, JNA library, and Subclipse). Click through, accept the license, and Eclipse will download all the bits and pieces and install them.

Restart Eclipse when the install is done.

This concludes the Eclipse installation. Time to get some code....

Getting BSim code via SVN
Now that we have the plugin it is possible to just directly download all the code and keep it up to date through Eclipse.

Right click in the 'Package Explorer' tab and select 'Import'. Expand out 'SVN', and select 'Checkout projects from SVN'.

On the next page, select 'Create new repository location'.

For now, we just want to anonymously checkout the latest version of the code and download it to our local machine, so on the next page type in the URL box:
	http://bsim-bccs.googlecode.com/svn/trunk/
and click next to come up with a list of folders.

(Note: as mentioned previously, the Trunk version is currently our working version. Stable versions can be anonymously checked out in a similar manner, by changing "trunk/" in the above URL to the correct address in the repository, for example to "tags/2.0/" to download the 2009 version: http://bsim-bccs.googlecode.com/svn/tags/2.0/).

Note for Committers: Please login to Google code use the URL from the instructions found here to be able to commit code (Eclipse will ask for your password later on).

BSim uses external libraries in addition to our own code (which are on the SVN and will be downloaded by Eclipse), so we need to select the root (http://bsim-bccs.googlecode.com/svn/trunk/), then click through to the next screen.

This should all be OK by default. However for clarity, the options we want are as follows:

Click Finish.

We now need to create a new Eclipse project. In this window ('Select a wizard'), expand out 'Java', and select 'Java Project'.

On the next screen give the project a name. Everything should be fine with the default options, just make sure that 'create separate folders for sources and class files' is selected.

On the next screen, in the 'source' tab, select the folder 'src' and change the default output folder from 'project_name/bin' to 'project_name/build'.

We can't add the external libraries yet as they haven't been downloaded, so Finish and Eclipse will download the project code. This will result in a lot of errors as the libraries haven't been included yet.

Important: adding the libraries to the build path. Right click on the project in package explorer and select 'properties'. In the left part of the window select 'Java Build Path', and then open up the 'Libraries' tab on the right. Click on 'Add Jars...'. Expand out 'project_name -> Lib' and select both 'core.jar' and 'vecmath.jar'. Click OK and go back to the workspace, there should now be a new section in the package explorer called 'referenced libraries'.

And we're ready to roll. Your Eclipse workspace should look something like the following:

To test if everything is working OK, pick an example file and run it. (Best are the ones with 'sim.preview()' at the end of the file, rather than 'sim.export()', as you will get visual confirmation that something is happening). Examples can be found in 'src/bsim.example'.

In this case we choose the tutorial example (BSimTutorialExample). To open an example just double click on it in the package explorer and then run it using the green arrow icon in the top toolbar. It should just work straight off:

Congratulations, BSim has been successfully obtained through SVN. The 'manual.pdf' on our downloads page has some more in depth info on actually using different features of BSim.

