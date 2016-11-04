import java.util.ArrayList;

import javax.swing.JFrame;
import org.math.plot.Plot2DPanel;

public class CountStepsBlank {

	/***
	 * Counts the number of steps based on sensor data.
	 * 
	 * @param times
	 *            a 1d-array with the elapsed times in miliseconds for each row
	 *            in the sensorData array.
	 * 
	 * @param sensorData
	 *            a 2d-array where rows represent successive sensor data
	 *            samples, and the columns represent different sensors. We
	 *            assume there are 6 columns. Columns 0 - 2 are data from the x,
	 *            y, and z axes of an accelerometer, and 3-5 are data from the
	 *            x, y, and z axes of a gyro.
	 * 
	 * @return an int representing the number of steps
	 */
	public static boolean isPeak(double cur, double mean, double StandardDeviation)
	{
		if(cur>(mean+StandardDeviation))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public static boolean isPeak(double[] sample, int cur)
	{
		if(sample[cur]>sample[cur-1]&&sample[cur]<sample[cur+1])
		{
			return true;
		}
		return false;
	}
	public static boolean isTrough(double[] sample, int cur)
	{
		if(sample[cur]<sample[cur-1]&&sample[cur]>sample[cur+1])
		{
			return true;
		}
		return false;
	}
	public static int countSteps(double[] times, double[] sample) {
		double x=calculateMean(sample);
		double y=calculateStandardDeviation(sample, x);
		int finalAns=0;
		double trough=0;
		double peak=0;
		for(int i=1; i<(sample.length-1); i++)
		{
			if(isTrough(sample, i)&&(peak-sample[i])>(y/2))
			{
				trough=sample[i];
			}
			if(isPeak(sample, i))
			{
				peak=sample[i];
				if((peak-trough)>(y/2))
				{
					finalAns++;
				}
			}
		}
		return finalAns;
	}
	public static int countSteps(double[] times, double[][] sensorData) {
		double[] sample = calculateMagnitudesFor(sensorData);
		return countSteps(times, sample);
	}
	/***
	 * Calculate the magnitude for a vector with x, y, and z components.
	 * 
	 * @param x
	 *            the x component
	 * @param y
	 *            the y component
	 * @param z
	 *            the z component
	 * @return the magnitude of the vector
	 */
	public static double calculateMagnitude(double x, double y, double z) {
		return Math.sqrt(x*x + y*y + z*z);
	}

	/***
	 * Takes a 2d array with 3 columns representing the 3 axes of a sensor.
	 * Calculates the magnitude of the vector represented by each row. Returns a
	 * new array with the same number of rows where each element contains this
	 * magnitude.
	 * 
	 * @param sensorData
	 *            an array with n rows and 3 columns. Each row represents a
	 *            different measurement, and each column represents a different
	 *            axis of the sensor.
	 * 
	 * @return an array with n rows and each element is the magnitude of the
	 *         vector for the corresponding row in the sensorData array
	 */
	public static double[] calculateMagnitudesFor(double[][] sensorData) {
		double[] mags = new double[sensorData.length];
		
		for (int r = 0; r < mags.length; r++) {
			mags[r] = calculateMagnitude(sensorData[r][0], sensorData[r][1], sensorData[r][2]);
		}
		
		return mags;
	}

	/***
	 * Return the standard deviation of the data.
	 * 
	 * @param arr
	 *            the array of the data
	 * @param mean
	 *            the mean of the data (must be pre-calculated).
	 * @return the standard deviation of the data.
	 */
	public static double calculateStandardDeviation(double[] arr, double mean) {
		double ans=0;
		for(int i=0; i<arr.length; i++)
		{
			ans=ans+(mean-arr[i])*(mean-arr[i]);
		}
		ans=ans/arr.length;
		return Math.sqrt(ans);
	}

	/***
	 * Return the mean of the data in the array
	 * 
	 * @param arr
	 *            the array of values
	 * @return the mean of the data
	 */
	public static double calculateMean(double[] arr) {
		double ans=0;
		for(int i=0; i<arr.length; i++)
		{
			ans+=arr[i];
		}
		ans=ans/arr.length;
		return ans;
	}

	public static void displayJFrame(Plot2DPanel plot) {
		JFrame frame = new JFrame("Results");
		frame.setSize(800, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);
	}

}
