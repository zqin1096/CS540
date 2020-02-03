/**
 * Class for internal organization of a Neural Network.
 * There are 5 types of nodes. Check the type attribute of the node for details
 * 
 * Do not modify. 
 */

import java.util.*;

public class Node {
	// 0=input,1=biasToHidden,2=hidden,3=biasToOutput,4=Output
	private int type = 0;
	// Array List that will contain the parents (including the bias node) with
	// weights if applicable
	public ArrayList<NodeWeightPair> parents = null;

	private Double inputValue = 0.0;
	// Output value of a node: same as input value for an iput node, 1.0 for
	// bias nodes and calculate based on Sigmoid function for hidden and output
	// nodes
	private Double outputValue = 0.0;
	// sum of wi*xi
	private Double sum = 0.0;

	// Create a node with a specific type
	public Node(int type) {
		if (type > 4 || type < 0) {
			System.out.println("Incorrect value for node type");
			System.exit(1);
		} else {
			this.type = type;
		}
		if (type == 2 || type == 4) {
			parents = new ArrayList<NodeWeightPair>();
		}
	}

	// For an input node sets the input value which will be the value of a
	// particular attribute
	public void setInput(Double inputValue) {
		if (type == 0) {
			this.inputValue = inputValue;
		}
	}

	/**
	 * Calculate the output of a Sigmoid node. You can assume that outputs of
	 * the parent nodes have already been calculated You can get this value by
	 * using getOutput()
	 * 
	 * @param train:
	 *            the training set
	 */
	public void calculateOutput() {
		// Not an input or bias node
		if (type == 2 || type == 4) {
			sum = 0.0;
			for (int i = 0; i < this.parents.size(); i++) {
				sum += parents.get(i).weight * parents.get(i).node.getOutput();
			}
			outputValue = 1.0 / (1.0 + Math.exp(-sum));
		}
	}

	public double getSum() {
		return sum;
	}

	// Gets the output value
	public double getOutput() {
		if (type == 0) {
			return inputValue;
		} else if (type == 1 || type == 3) {
			return 1.00;
		} else {
			return outputValue;
		}
	}
}
