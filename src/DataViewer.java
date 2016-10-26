import grafica.*;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.video.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class DataViewer {
	public GPlot plot;
	private PApplet window;

	private boolean isTimeDataElapsed;

	private int N = 10000;
	private int WINDOW_SIZE = 5; // in seconds
	private double[][] data;
	private String[] names;

	private ArrayList<Integer> colors;
	private int nextFreeColor = 0;

	private double currentTime_inSec;
	private int currentDataIndex = 0;
	private int time_inMs = 0;
	private int movieWidth = 0;
	private int initialElapsedTime_inMs;
	private boolean initTimeSet = false;

	private int vidX = 70;

	private PImage frame;
	private Movie movie;
	private int movieHeight;

	private DataTableDisplay dataDisplay;

	private boolean displaySuperImposed = false;

	public DataViewer(PApplet w, String moviePath, double[][] data, String[] names, boolean isTimeDataElapsed) {
		if (names.length != data[0].length)
			System.err.println("names array must have same length as data columns!");

		this.window = w;
		this.isTimeDataElapsed = isTimeDataElapsed;

		window.textAlign(window.CENTER, window.CENTER);

		colors = new ArrayList<Integer>();
		loadColors();

		this.data = data;
		this.names = names;

		movie = new Movie(window, moviePath);
		this.jumpToTime(0); // start at time 0
		this.movieWidth = movie.width;
		this.movieHeight = movie.height;

		int completely_clear = window.color(255, 255, 255, 0);
		int tinted = window.color(200, 200, 200, 100);
		int font_color = window.color(0, 255, 0);

		// Setup for the second plot
		plot = new GPlot(window);
		plot.setPos(0, movieHeight);
		plot.setDim(movieWidth, 400);
		plot.setBgColor(tinted);
		plot.setBoxBgColor(completely_clear);
		plot.setFontColor(font_color);
		plot.getTitle().setText("Data Display");
		plot.getXAxis().getAxisLabel().setText("time (ms)");
		plot.getYAxis().getAxisLabel().setText("y");
		plot.setXLim(0, WINDOW_SIZE);
		// plot2.activateZooming(1.5f);

		// Find min & max data values to pre-scale y-axis.
		double min = Double.MAX_VALUE, max = Double.MIN_VALUE;

		ArrayList<GPointsArray> pointArrays = new ArrayList<GPointsArray>();
		for (int col = 1; col < data[0].length; col++) {
			GPointsArray points = new GPointsArray();

			for (int row = 0; row < data.length; row++) {
				float time;
				if (isTimeDataElapsed) {
					time = (float) data[row][0];
				} else {
					time = (float) ((data[row][0] - data[0][0]) / 1000.0);
				}

				points.add(time, (float) data[row][col]);

				if (data[row][col] < min)
					min = data[row][col];
				if (data[row][col] > max)
					max = data[row][col];
			}

			pointArrays.add(points);
		}

		// set y axis scale
		plot.setYLim((float) min, (float) max);

		System.out.println("Points array size: " + pointArrays.size());
		for (int i = 0; i < pointArrays.size(); i++) {
			plot.addLayer(names[i+1], pointArrays.get(i));
			plot.getLayer(names[i+1]).setLineColor(getUniqueLineColor());
		}

		dataDisplay = new DataTableDisplay(window, movieWidth + 20, 0, 400, movieHeight, 7, names.length, data, names);

		dataDisplay.setInitialTime(data[0][0]);
	}

	public void setInitialTime() {
		this.currentDataIndex = 0;
		this.initialElapsedTime_inMs = this.time_inMs;
		this.initTimeSet = true;
	}

	private void loadColors() {
		colors.add(window.color(255, 0, 0)); // red
		colors.add(window.color(0, 150, 0)); // green
		colors.add(window.color(0, 0, 255)); // blue
		colors.add(window.color(255, 255, 0)); // something
		colors.add(window.color(255, 0, 255)); // something
	}

	private int getUniqueLineColor() {
		if (nextFreeColor < colors.size()) {
			int color = colors.get(nextFreeColor);
			nextFreeColor++;

			return color;
		}

		return window.color((float) Math.random() * 256, (float) Math.random() * 256, (float) Math.random() * 256);
	}

	public void resetPoints() {
		plot.setPoints(new GPointsArray());
	}

	public void jumpBy(int tick) {
		jumpToTime(time_inMs + tick);
	}

	public void jumpToTime(int newTime_inMs) {
		double elapsedTimeSinceInitial = (newTime_inMs - this.initialElapsedTime_inMs)/1000.0;
		double elapsedDataTimeSinceInitial = (data[currentDataIndex][0] - data[0][0]) / 1000.0;
		
		// adjust current data index
		if (this.currentTime_inSec < newTime_inMs / 1000.0) {

			while (this.currentDataIndex < data.length
					&& elapsedDataTimeSinceInitial <= elapsedTimeSinceInitial ){
				currentDataIndex++;
				elapsedDataTimeSinceInitial = (data[currentDataIndex][0] - data[0][0]) / 1000.0;
			}

			currentDataIndex--;
		} else if (this.currentTime_inSec > newTime_inMs / 1000.0) {

			while (this.currentDataIndex >= 0
					&& elapsedDataTimeSinceInitial >= elapsedTimeSinceInitial){
				elapsedDataTimeSinceInitial = (data[currentDataIndex][0] - data[0][0]) / 1000.0;
				currentDataIndex--;
			}
		}

		movie.jump((float) (newTime_inMs / 1000.0));
		movie.play();

		while (!movie.available()) { // There's certainly a better way to do
										// this
			System.out.print("."); // if need be; figure it out
		}
		
		movie.read();
		frame = movie.get();
		movie.pause();
		this.time_inMs = newTime_inMs;
		this.currentTime_inSec = newTime_inMs / 1000.0;
	}

	public void draw() {
		// Display video
		if (frame != null)
			window.image(frame, vidX, 0);

		if (initTimeSet == false) {
			window.fill(window.color(0, 0, 0));
			window.textAlign(window.CENTER, window.CENTER);
			window.text(
					"Use the arrow keys to move to the frame where you start recording data with your phone.  Then hit the 'z' key to zero it there. ",
					movieWidth / 2, (float) (movieHeight * 1.5));
		} else {
			// Draw the second plot
			plot.beginDraw();
			// plot.drawBackground();
			// plot.drawBox();
			// plot.drawXAxis();
			plot.drawYAxis();
			// plot.drawTitle();
			plot.drawGridLines(GPlot.Y);
			plot.drawLines();
			plot.endDraw();

			plot.setXLim((float) ((time_inMs-this.initialElapsedTime_inMs) / 1000.0f - WINDOW_SIZE),
					(float) (time_inMs-this.initialElapsedTime_inMs) / 1000.0f + WINDOW_SIZE);

			if (this.displaySuperImposed) {
				window.stroke(window.color(255, 255, 255));
				window.line(movieWidth / 2 + 70, 0, movieWidth / 2 + 70, movieHeight);
			} else {
				window.stroke(window.color(0, 0, 0));
				window.line(movieWidth / 2 + 70, movieHeight + 10, movieWidth / 2 + 70, 2 * movieHeight);
			}

			dataDisplay.jumpToRow(currentDataIndex);
			dataDisplay.draw();
			
			window.fill(window.color(0, 0, 0));
			window.textAlign(window.LEFT, window.TOP);
			window.text("Time since data start: " + (this.time_inMs - this.initialElapsedTime_inMs)/1000.0 + " sec.", 60, this.movieHeight + 25);
		}

		window.fill(window.color(0, 0, 0));
		window.textAlign(window.LEFT, window.TOP);
		window.text("time since vid start: " + this.currentTime_inSec + " sec.", 60, this.movieHeight + 10);
	}

	public void setDisplaySuperImposed(boolean superImposed) {
		if (superImposed)
			plot.setPos(0, 0);
		else
			plot.setPos(0, movieHeight);

		this.displaySuperImposed = superImposed;
	}
}