
/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 * 
 */

import java.util.*;

public class NNImpl {
	public ArrayList<Node> inputNodes = null;// list of the input layer nodes.
	public ArrayList<Node> hiddenNodes = null;// list of the hidden layer nodes
	public ArrayList<Node> outputNodes = null;// list of the output layer nodes

	public ArrayList<Instance> trainingSet = null;// the training set

	Double learningRate = 1.0; // variable to store the learning rate
	int maxEpoch = 1; // variable to store the maximum number of epochs

	/**
	 * This constructor creates the nodes necessary for the neural network Also
	 * connects the nodes of different layers After calling the constructor the
	 * last node of both inputNodes and hiddenNodes will be bias nodes.
	 */

	public NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount,
			Double learningRate, int maxEpoch, Double[][] hiddenWeights,
			Double[][] outputWeights) {
		this.trainingSet = trainingSet;
		this.learningRate = learningRate;
		this.maxEpoch = maxEpoch;

		// input layer nodes
		inputNodes = new ArrayList<Node>();
		int inputNodeCount = trainingSet.get(0).attributes.size();
		int outputNodeCount = trainingSet.get(0).classValues.size();
		for (int i = 0; i < inputNodeCount; i++) {
			Node node = new Node(0);
			inputNodes.add(node);
		}

		// bias node from input layer to hidden
		Node biasToHidden = new Node(1);
		inputNodes.add(biasToHidden);

		// hidden layer nodes
		hiddenNodes = new ArrayList<Node>();
		for (int i = 0; i < hiddenNodeCount; i++) {
			Node node = new Node(2);
			// Connecting hidden layer nodes with input layer nodes
			for (int j = 0; j < inputNodes.size(); j++) {
				NodeWeightPair nwp = new NodeWeightPair(inputNodes.get(j),
						hiddenWeights[i][j]);
				node.parents.add(nwp);
			}
			hiddenNodes.add(node);
		}
		// bias node from hidden layer to output
		Node biasToOutput = new Node(3);
		hiddenNodes.add(biasToOutput);

		// Output node layer
		outputNodes = new ArrayList<Node>();
		for (int i = 0; i < outputNodeCount; i++) {
			Node node = new Node(4);
			// Connecting output layer nodes with hidden layer nodes
			for (int j = 0; j < hiddenNodes.size(); j++) {
				NodeWeightPair nwp = new NodeWeightPair(hiddenNodes.get(j),
						outputWeights[i][j]);
				node.parents.add(nwp);
			}
			outputNodes.add(node);
		}
	}

	/**
	 * Get the output from the neural network for a single instance Return the
	 * idx with highest output values. For example if the outputs of the
	 * outputNodes are [0.1, 0.5, 0.2, 0.1, 0.1], it should return 1. If outputs
	 * of the outputNodes are [0.1, 0.5, 0.1, 0.5, 0.2], it should return 3. The
	 * parameter is a single instance.
	 */

	public int calculateOutputForInstance(Instance inst) {
		for (int i = 0; i < inst.attributes.size(); i++) {
			inputNodes.get(i).setInput(inst.attributes.get(i));
		}
		for (int i = 0; i < hiddenNodes.size(); i++) {
			hiddenNodes.get(i).calculateOutput();
		}
		for (int i = 0; i < outputNodes.size(); i++) {
			outputNodes.get(i).calculateOutput();
		}
		int index = 0;
		double max = outputNodes.get(0).getOutput();
		for (int i = 1; i < outputNodes.size(); i++) {
			if (outputNodes.get(i).getOutput() >= max) {
				index = i;
				max = outputNodes.get(i).getOutput();
			}
		}
		return index;
	}

	/**
	 * Train the neural networks with the given parameters
	 * 
	 * The parameters are stored as attributes of this class
	 */

	public void train() {
		for (int i = 0; i < this.maxEpoch; i++) {
			for (int j = 0; j < trainingSet.size(); j++) {
				// Propagate the inputs forward to compute the outputs
				calculateOutputForInstance(trainingSet.get(j));
				// Propagate deltas backwards from output layer to input layer
				// and update every weight in network
				backPropagate(trainingSet.get(j));
			}
		}
	}

	/**
	 * Calculate the derivative
	 * 
	 * @param val Output value at a hidden unit or output unit
	 * 
	 * @return Derivative at a unit
	 * 
	 */
	private double sigmoidDerivative(double val) {
		return (val * (1.0 - val));
	}

	/**
	 * Propagate the жд values back to the previous layer and update the weights
	 * between the two layers.
	 * 
	 * @param inst One of the training example
	 */
	private void backPropagate(Instance inst) {
		double[] erro = new double[outputNodes.size()];
		// Calculate the error at the output nodes
		for (int i = 0; i < outputNodes.size(); i++) {
			erro[i] = (inst.classValues.get(i) - outputNodes.get(i).getOutput())
					* sigmoidDerivative(outputNodes.get(i).getOutput());
		}

		// Propagate errors back to the hidden layer
		double[] errh = new double[hiddenNodes.size() - 1];
		for (int i = 0; i < errh.length; i++) {
			for (int j = 0; j < outputNodes.size(); j++) {
				errh[i] += erro[j] * outputNodes.get(j).parents.get(i).weight;
			}
			errh[i] *= sigmoidDerivative(hiddenNodes.get(i).getOutput());
		}
		// Update the weights for the output layer
		for (int i = 0; i < outputNodes.size(); i++) {
			for (int j = 0; j < hiddenNodes.size(); j++) {
				outputNodes.get(i).parents.get(j).weight += learningRate
						* erro[i] * hiddenNodes.get(j).getOutput();
			}
		}

		// Update the weights for the hidden layer
		for (int i = 0; i < errh.length; i++) {
			for (int j = 0; j < inputNodes.size(); j++) {
				hiddenNodes.get(i).parents.get(j).weight += learningRate
						* errh[i] * inputNodes.get(j).getOutput();
			}
		}
	}
}
