# DEECo Playground

The DEECo Playgrond is a framework for simulation and visualization of scenarios for smart Cyber-Physical Systems. 

## Documentation

A step-by-step tutorial on the scenario creation with this framework can be found in the **Scenario Creation Guide**.
The **Implementation and Architecture** document explores the playground's internal implementation in detail.
The **manual.html** file contains descriptions of the formats of scenario files and configuration files.
It also provides an overview of the classes that user needs to use in the process of scenario creation, explaining the meaning and function of all their methods and attributes.

## Execution of example scenarios

In order to run the application you need to have JDK >=8 installed.

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


## Importing the project to IDE

The project directory can be imported into an IntelliJ IDEA directly. It will
contain two already prepared run configurations: Simulation and Visualization.

There are also `.project` and `.classpath` files in the project directory, that can
be used to import the project to Eclipse or NetBeans. The runnable classes are 
`cz.cuni.mff.d3s.deeco.playground.simulation.Main` and 
`cz.cuni.mff.d3s.deeco.playground.visualization.Main` for
Simulation and Visualization respectively.

## License

Our work is licensed under the [Apache 2 License](http://www.apache.org/licenses/LICENSE-2.0.html), 
meaning that you can use it free of charge, without strings attached in commercial and non-commercial projects. 
