# DEECo Playground

The DEECo Playgrond is a framework for simulation and visualization of the smart 
Cyber-Physical Systems. A documentation on this framework can be found in the **manual.html**
file.

## 1. Execution of example scenarios

To run the application you need to have JDK >=8 installed.

There are 3 example scenarios which can be executed with corresponding shell 
(on Linux systems) or batch (on Windows) scripts. They can be found in the `demos-linux`
and `demos-windows` directories respectively. As the
Simulation and Visualization programs are separate parts of the application,
there are two executables for each scenario. First, the simulation script has to
be executed. In the process of execution it displays a number of cycle that is
currently simulated (all the example scenarios have the duration of 500 cycles).
When this program finishes, the visualization script can be executed.

All the scenarios can also be executed with the ant build file (you need to have
Apache Ant installed to do this). Simulation and Visualization programs can be
executed with the following commands:

```
ant run.simulation -Dargs="arguments" 
ant run.visualization -Dargs="arguments"
```

In the case of simulation the arguments are scenario files (more than one can be
specified). For visualization, the first argument has to be a simulation logs
file, the second one is an optional configuration file.

The ant build file also allows to generate javadoc with the command `ant doc`.


## 2. Importing the project to IDE

The project directory can be imported into an IntelliJ IDEA directly. It will
contain two already prepared run configurations: Simulation and Visualization.

There are also `.project` and `.classpath` files in the project directory, that can
be used to import the project to Eclipse or NetBeans. The runnable classes are 
`cz.cuni.mff.d3s.deeco.playground.simulation.Main` and 
`cz.cuni.mff.d3s.deeco.playground.visualization.Main` for
Simulation and Visualization respectively.
