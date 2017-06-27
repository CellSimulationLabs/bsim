# Running the example

Assuming the BSim repository root directory is `$BSIM_SRC`.

First, build everything:
```
cd $BSIM_SRC

git pull

ant -f bsim-build-tree.xml
```

Then go to the build dir:
```
cd out/production/$BSIM_SRC/

bash ./BSimConsortiumController/run_from_out.sh
```

In the default case, a window should pop up with 6 controller and 6 target bacteria located on either side of a 20x25um chamber.

