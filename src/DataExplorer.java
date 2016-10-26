import grafica.*;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.video.*;

import java.util.Arrays;
import java.util.Random;

public class DataExplorer extends PApplet {
	public double[][] sampleData;
	private String[] colNames;
	private DataViewer dataViewer;
	private int TICK = 50; // minimum time increment in miliseconds
	private int TICK_INC = 25; // increment to change tick size by
	private boolean superImposed;
	private String datafile, videofile;

	public static void runDataExplorer(double[][] data, String[] colNames, String videofile) {
		String[] a = { "Main" };
		PApplet.runSketch(a, new DataExplorer(data, colNames, videofile));
	}

//	public static void runDataExplorer(double[][] data, String videofile) {
//		String[] a = { "Main" };
//		
//		double[] time = ArrayHelper.extractColumn(data, 0);
//		String[] names = makeColNames(data[0].length, "col ");
//
//		PApplet.runSketch(a, new DataExplorer(data, names, videofile));
//	}

	public static void runDataExplorer(double[] time, double[] data, String videofile) {
		String[] a = { "Main" };
		
		String[] names = new String[] {"time", "data"};
		double[][] fulldata = ArrayHelper.combineAsColumns(time, data);
		
		PApplet.runSketch(a, new DataExplorer(fulldata, names, videofile));
	}

//	public static void runDataExplorer(double[] time, double[][] data, String videofile) {
//		String[] a = { "Main" };
//		
//		String[] names = makeColNames("time", data[0].length, "col ");
//		
//		PApplet.runSketch(a, new Main(data, videofile));
//	}

	private DataExplorer(double[][] data, String[] colNames, String videofile) {
		super();
		this.sampleData = data;
		this.videofile = videofile;
		this.colNames = colNames;
	}

	public void setup() {
		size(1800, 1000);

		dataViewer = new DataViewer((PApplet) this, videofile, sampleData, colNames, false);

		// Create a data viewer for that data synched with the video
		// NOTE: First column (index 0) MUST be time!
		// NOTE2: column names in string array must be unique!
		// dataViewer = new DataViewer((PApplet)this,
		// "data/WalkingSampleData.mp4", accelerationData, new String[] {"x",
		// "y", "z", "x2", "y2", "z2"}, false);

	}

	public void draw() {
		background(255);

		if (keyPressed && key == CODED) {
			if (keyCode == RIGHT)
				dataViewer.jumpBy(TICK);
			if (keyCode == LEFT)
				dataViewer.jumpBy(-TICK);
		}

		text("arrows move time by " + TICK + " ms", 10, height - 20);

		dataViewer.draw();
	}

	public void keyPressed() {
		// Reset the points if the user pressed the space bar
		if (key == ' ') {
			dataViewer.resetPoints();
		}

		if (key == 's') {
			superImposed = !superImposed;
			dataViewer.setDisplaySuperImposed(superImposed);
		}

		if (key == 'z') {
			dataViewer.setInitialTime();
		}

		if (key == '+') {
			TICK += TICK_INC;
		}

		if (key == '-') {
			TICK -= TICK_INC;
		}
	}

	private static String[] makeColNames(String firstCol, int length, String prefix) {
		String colNames = firstCol + ",";
		for (int i = 0; i < length; i++) {
			colNames += prefix + i + ",";
		}
		return colNames.split(",");
	}
	
	private static String[] makeColNames(int length, String prefix) {
		String colNames = "";
		for (int i = 0; i < length; i++) {
			colNames += prefix + i + ",";
		}
		return colNames.split(",");
	}
}