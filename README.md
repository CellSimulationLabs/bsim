# BSim

BSim is an agent-based modelling tool designed to allow for the study of bacterial populations. By enabling the description of bacterial behaviours, it attempts to provide an environment in which to investigate how local interactions between individual bacterium leads to the emergence of population level features, such as cooperation and synchronisation.

## Case studies

### Emergent oscillations in a synthetic multicellular consortium

We adapted the model from Chen et al., Science, 2015 ([DOI: 10.1126/science.aaa3794](http://science.sciencemag.org/content/349/6251/986.long)).

The code for this case study can be found [here](https://github.com/bsim-bristol/bsim/examples/BSimChenOscillator).

A video of an example simulation can be found here:
https://www.youtube.com/watch?v=FpG7EgIC5yI

### Consortium controller

The implementation of the model from our paper 'In-Silico Analysis and Implementation of a Multicellular Feedback Control Strategy in a Synthetic Bacterial Consortium' (Fiore G, Matyjaszkiewicz A, et al., ACS Synth. Biol., 2017; [DOI: 10.1021/acssynbio.6b00220](http://pubs.acs.org/doi/abs/10.1021/acssynbio.6b00220)).

Code for this example can be found [here](https://github.com/bsim-bristol/bsim/examples/BSimConsortiumController).

A video of an example simulation can be found here:
https://www.youtube.com/watch?v=wBLXv9znhqE

## Build

BSim can be run from your favourite IDE (e.g., IntelliJ IDEA, eclipse, ...).

Alternatively, the whole source tree can be build from the command line using Ant:

`ant -f bsim-build-tree.xml`

The resulting compiled classes will reside in the directory `./out/*`.

## Citing BSim

If you have made use of BSim in academic or commercial work, the project can be referenced using the following paper:

[Gorochowski TE, Matyjaszkiewicz A, Todd T, Oak N, Kowalska K, et al. (2012) BSim: An Agent-Based Tool for Modeling Bacterial Populations in Systems and Synthetic Biology. *PLoS ONE* **7**(8): e42790. doi:10.1371/journal.pone.0042790](http://dx.plos.org/10.1371/journal.pone.0042790)

