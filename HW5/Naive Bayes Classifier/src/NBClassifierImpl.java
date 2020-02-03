
/***************************************************************************************
  CS540 - Section 2
  Homework Assignment 5: Naive Bayes

  NBClassifierImpl.java
  This is the main class that implements functions for Naive Bayes Algorithm!
  ---------
  	*Free to modify anything in this file, except the class name 
  	You are required:
  		- To keep the class name as NBClassifierImpl for testing
  		- Not to import any external libraries
  		- Not to include any packages 
	*Notice: To use this file, you should implement 2 methods below.

	@author: TA 
	@date: April 2017
*****************************************************************************************/

import java.util.ArrayList;
import java.util.List;

public class NBClassifierImpl implements NBClassifier {

	// The number of features including the class
	private int nFeatures; 
	// Size of each features
	private int[] featureSize; 
	// Parameters of Naive Bayes
	private List<List<double[]>> logPosProbs; 
	// Number of negative training example
	private int negative;
	// Number of positive training example
	private int positive;

	/**
	 * Constructs a new classifier without any trained knowledge.
	 */
	public NBClassifierImpl() {

	}

	/**
	 * Construct a new classifier
	 * 
	 * @param int[]
	 *            sizes of all attributes
	 */
	public NBClassifierImpl(int[] features) {
		this.nFeatures = features.length;

		// initialize feature size
		this.featureSize = features.clone();

		this.logPosProbs = new ArrayList<List<double[]>>(this.nFeatures);
		this.negative = 0;
		this.positive = 0;
	}

	/**
	 * Read training data and learn parameters
	 * 
	 * @param int[][]
	 *            training data
	 */
	public void fit(int[][] data) {
		classCount(data);
		for (int i = 0; i < this.nFeatures - 1; i++) {
			conditional(data, i);
		}
	}

	/**
	 * Get the number of class values
	 * 
	 * @param data
	 *            training data
	 * @param array
	 *            store the number of class values
	 */
	private void classCount(int[][] data) {
		for (int i = 0; i < data.length; i++) {
			if (data[i][this.nFeatures - 1] == 0) {
				negative++;
			} else {
				positive++;
			}
		}
	}

	private void conditional(int[][] data, int attribute) {
		List<double[]> list = new ArrayList<double[]>();
		for (int i = 0; i < featureSize[attribute]; i++) {
			double[] count = new double[2];
			for (int j = 0; j < data.length; j++) {
				if (data[j][attribute] == i) {
					if (data[j][this.nFeatures - 1] == 0) {
						count[0]++;
					} else {
						count[1]++;
					}
				}
			}
			// Add-1 smoothing
			count[0] = Math.log(
					(count[0] + 1) / (this.negative + featureSize[attribute]));
			count[1] = Math.log(
					(count[1] + 1) / (this.positive + featureSize[attribute]));
			list.add(count);
		}
		logPosProbs.add(list);
	}

	/**
	 * Classify new dataset
	 * 
	 * @param int[][]
	 *            test data
	 * @return Label[] classified labels
	 */
	public Label[] classify(int[][] instances) {

		int nrows = instances.length;
		Label[] yPred = new Label[nrows]; // predicted data

		for (int i = 0; i < yPred.length; i++) {
			double neg = 0.0;
			double pos = 0.0;
			// Calculate the likelihood of being a negative and positive
			for (int j = 0; j < this.nFeatures - 1; j++) {
				neg += logPosProbs.get(j).get(instances[i][j])[0];
				pos += logPosProbs.get(j).get(instances[i][j])[1];
			}
			// Add-1 smoothing
			neg += Math.log((double) (this.negative + 1)
					/ (this.negative + this.positive + 2));
			pos += Math.log((double) (this.positive + 1)
					/ (this.negative + this.positive + 2));
			if (neg > pos) {
				yPred[i] = Label.Negative;
			} else {
				yPred[i] = Label.Positive;
			}
		}
		return yPred;
	}
}