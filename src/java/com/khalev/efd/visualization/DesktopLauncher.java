package com.khalev.efd.visualization;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Main class of the program. Requires to receive a visualization properties file as its 1st parameter. Visualization
 * configs
 */
public class DesktopLauncher {


	public static void main (String[] arg) {
		if (arg.length < 1) {
			System.out.println("Please provide a name of simulation logs file");
		} else {
			File logfile = new File(arg[0]);
			File configfile = (arg.length > 1 ? new File(arg[1]) : null);
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			setWindowSize(logfile, configfile, config);
			new LwjglApplication(new Visualizer(logfile, configfile), config);
		}
	}

	private static void setWindowSize(File logfile, File configfile, LwjglApplicationConfiguration config) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(logfile));
			String bitmapPath = reader.readLine();
			if (bitmapPath == null) {
				throw new RuntimeException("Simulation logs file is not correct");
			}
			int height, width, zoom;
			if (!"null".equals(bitmapPath)) {
				File bitmap = new File(bitmapPath);
				EnvironmentMap map = (new BitmapProcessor()).readBitmap(bitmap);
				width = map.getWidth();
				height = map.getHeight();
			} else {
				try {
					width = Integer.parseInt(reader.readLine());
					height = Integer.parseInt(reader.readLine());
				} catch (NumberFormatException ex) {
					throw new RuntimeException("Simulation logs file is not correct");
				}
			}
			reader.close();
			zoom = Visualizer.getZoom();
			if (configfile != null) {
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configfile);
				Node zoomAttribute = doc.getDocumentElement().getAttributes().getNamedItem("zoom");
				if (zoomAttribute != null) {
					zoom = Integer.parseInt(zoomAttribute.getTextContent());
				}
			}
			config.width = width * zoom;
			config.height = height * zoom;
		} catch (IOException | ParserConfigurationException |SAXException ex) {
			throw new RuntimeException(ex);
		}
	}
}
