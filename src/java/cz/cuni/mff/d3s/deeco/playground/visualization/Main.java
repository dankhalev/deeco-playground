package cz.cuni.mff.d3s.deeco.playground.visualization;

import java.io.IOException;

/**
 * This class contains a main() method that runs a visualization. It requires to receive a simulation logs file as its
 * first parameter. Visualization configuration file can be added as a second parameter.
 *
 * @author Danylo Khalyeyev
 */
public class Main {

	public static void main (String[] arg) throws IOException, VisualizationParametersException {
		if (arg.length < 1) {
			System.out.println("You need to provide a path to a simulation logs file to run a visualization");
		} else {
			Visualization visualization;
			if (arg.length == 1) {
				visualization = new Visualization(arg[0], null);
			} else {
				visualization = new Visualization(arg[0], arg[1]);
			}
			visualization.startVisualization();
		}
	}



}
