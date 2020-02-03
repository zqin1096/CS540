///////////////////////////////////////////////////////////////////////////////
// Main Class File:  FindPath.java
// File:             BreadthFirstSearcher.java
// Semester:         (540) Spring 2017
//
//Author:           Zhaoyin Qin
//Email:            zqin23@wisc.edu
//CS Login:         zhaoyin
//Lecturer's Name:  Chuck Dyer
////////////////////////////80 columns wide //////////////////////////////////

import java.util.LinkedList;

/**
 * Breadth-First Search (BFS)
 * 
 * You should fill the search() method of this class.
 */
public class BreadthFirstSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze
	 *            initial maze.
	 */
	public BreadthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main breadth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		// explored list is a Boolean array that indicates if a state associated
	    // with a given position in the maze has already been explored.
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
		// used to check if a state with a specific position is on the frontier
		boolean[][] nodeOnFrontier = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
		// Queue implementing the Frontier list
		LinkedList<State> queue = new LinkedList<State>();
		// set gValue of start state to zero
		int gValue = 0;
		// create and initialize start state
		State startState = new State(maze.getPlayerSquare(), null, gValue, maxDepthSearched);
		// add startState to the queue
		queue.add(startState);
		// indicate startState is currently on the frontier
		nodeOnFrontier[startState.getX()][startState.getY()] = true;
		while (!queue.isEmpty()) {
			// update the maximum size of frontier
			maxSizeOfFrontier = Math.max(maxSizeOfFrontier, queue.size());
			// remove the first element of queue
			State state = queue.pop();
			// increment number of nodes expanded
			noOfNodesExpanded++;
			// check if state is goal state
			if (state.isGoal(maze)) {
				// update cost
				cost = state.getGValue();
				// update maxDepthSearched 
				maxDepthSearched = state.getDepth();
				// set the path from start state to goal state
				updateMaze(state);
				return true;
			}
			// indicate the position has been visited
			explored[state.getX()][state.getY()] = true;
			// indicate this node is removed from frontier
			nodeOnFrontier[state.getX()][state.getY()] = false;
			for (State successor : state.getSuccessors(explored, maze)) {
				// check if the node is on the frontier
				if(!nodeOnFrontier[successor.getX()][successor.getY()]) {
					queue.add(successor);
					// indicate this node is currently on the frontier
					nodeOnFrontier[successor.getX()][successor.getY()] = true;
				}
			}
		}
		return false;
	}

	/**
	 * Set the solution path.
	 * 
	 * @param goalState the goal state of a maze
	 */
	private void updateMaze(State goalState) {
		State curr = goalState.getParent();
		while (curr.getX() != maze.getPlayerSquare().X || curr.getY() != maze.getPlayerSquare().Y) {
			// set the solution path to '.'
			maze.setOneSquare(curr.getSquare(), '.');
			// move to the next node
			curr = curr.getParent();
		}
	}
}
