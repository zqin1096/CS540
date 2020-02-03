
///////////////////////////////////////////////////////////////////////////////
// Main Class File:  FindPath.java
// File:             AStarSearcher.java
// Semester:         (540) Spring 2017
//
//Author:           Zhaoyin Qin
//Email:            zqin23@wisc.edu
//CS Login:         zhaoyin
//Lecturer's Name:  Chuck Dyer
////////////////////////////80 columns wide //////////////////////////////////

import java.util.PriorityQueue;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze
	 *            initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		// explored list is a Boolean array that indicates if a state associated
		// with a given position in the maze has already been explored.
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
		// used to check if a state with a specific position is on the frontier
		boolean[][] isOnFrontier = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
		PriorityQueue<StateFValuePair> frontier = new PriorityQueue<StateFValuePair>();
		// set gValue of start state to zero
		int gValue = 0;
		// create and initialize start state
		StateFValuePair startState = new StateFValuePair(
				new State(maze.getPlayerSquare(), null, gValue, maxDepthSearched),
				gValue + heuristic(maze.getPlayerSquare().X, maze.getPlayerSquare().Y));
		// add startState to the frontier
		frontier.add(startState);
		while (!frontier.isEmpty()) {
			// update the maximum size of frontier
			maxSizeOfFrontier = Math.max(maxSizeOfFrontier, frontier.size());
			// remove the element with highest priority from the frontier
			StateFValuePair stateFValue = frontier.poll();
			// increment the number of nodes expanded
			noOfNodesExpanded++;
			// check if stateFValue is goal state
			if (stateFValue.getState().isGoal(maze)) {
				cost = stateFValue.getState().getGValue();
				maxDepthSearched = stateFValue.getState().getDepth();
				updateMaze(stateFValue.getState());
				return true;
			}
			// indicate the position has been visited
			explored[stateFValue.getState().getX()][stateFValue.getState().getY()] = true;
			isOnFrontier[stateFValue.getState().getX()][stateFValue.getState().getY()] = false;
			for (State successor : stateFValue.getState().getSuccessors(explored, maze)) {
				// check if the node is on the frontier
				if (!isOnFrontier[successor.getX()][successor.getY()]) {
					frontier.add(new StateFValuePair(successor,
							successor.getGValue() + heuristic(successor.getX(),
									successor.getY())));
					// indicate this node is currently on the frontier
					isOnFrontier[successor.getX()][successor.getY()] = true;
				}
			}
		}
		return false;
	}

	/**
	 * Calculate the heuristic value of a state
	 * 
	 * @return the distance between the current position and the goal position
	 */
	private double heuristic(double x, double y) {
		return Math.hypot(maze.getGoalSquare().X - x,
				maze.getGoalSquare().Y - y);
	}

	/**
	 * Set the solution path.
	 * 
	 * @param goalState the goal state of a maze
	 */
	private void updateMaze(State goalState) {
		State curr = goalState.getParent();
		while (curr.getX() != maze.getPlayerSquare().X
				|| curr.getY() != maze.getPlayerSquare().Y) {
			maze.setOneSquare(curr.getSquare(), '.');
			curr = curr.getParent();
		}
	}
}
