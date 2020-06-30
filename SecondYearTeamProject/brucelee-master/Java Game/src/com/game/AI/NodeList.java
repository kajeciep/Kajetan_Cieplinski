package com.game.AI;

import java.util.ArrayList;

import com.game.physics.Coord;
import com.game.state.GameState;
import com.game.state.GameWorld;

/**
 * A list of all the possible nodes that the AI can move to. It has methods in
 * it to search through them as well as create them
 * 
 * @author Isaac
 *
 */
public class NodeList {

	private ArrayList<Node> nodes;
	private GameState state;
	/**
	 * Records if the last move was in a wall/off the map
	 */
	private boolean invalid;

	/**
	 * Creates a list of nodes that contains the current position of the AI as
	 * it's starting node and that can be added to to explore the world. The
	 * first node should always be false to avoid the I standing in place and
	 * giving more of a challenge to the player
	 * 
	 * @param state
	 *            The current GameState so that the list can know where the
	 *            terrain is when creating nodes
	 * @param x
	 *            The x position of the robot
	 * @param y
	 *            The y position of the robot
	 */
	public NodeList(GameState state, int x, int y) {
		nodes = new ArrayList<Node>(500);
		this.state = state;
		nodes.add(new Node(new Coord(x, y), false));
	}

	/**
	 * Adds a node to the right of the last node in the list
	 * 
	 * @param height
	 *            The height the last node was from the ground
	 * @param candidate
	 */
	public void addRightNode(double height, boolean candidate) {
		Coord location = nodes.get(nodes.size() - 1).getLocation();
		int nextX = location.getX() + 50;
		if (nextX <= 0 || nextX >= GameWorld.worldWidth) {
			return;
		}
		Coord newPos = state.getNodeYPos(nextX, location.getY());
		newPos.setX(newPos.getX() + (int) (height / 2));
		Node n = new Node(newPos, candidate);
		// Dont walk of a cliff
		if (location.getY() - newPos.getY() < -200) {
			return;
		}
		nodes.add(n);
	}

	public void addLeftNode(double height, boolean candidate) {
		Coord location = nodes.get(nodes.size() - 1).getLocation();
		int nextX = location.getX() - 50;
		// Returns if the node would be out of bounds
		if (nextX <= 0 || nextX >= GameWorld.worldWidth) {
			return;
		}
		Coord newPos = state.getNodeYPos(nextX, location.getY());
		newPos.setX(newPos.getX() + (int) (height / 2));
		Node n = new Node(newPos, candidate);
		if (location.getY() - newPos.getY() < -200) {
			return;
		}
		nodes.add(n);
	}

	/**
	 * Finds the last node added to the list and returns it's position
	 * 
	 * @return The location of the last node added
	 */
	public Coord getCurrentPos() {
		return nodes.get(nodes.size() - 1).getLocation();
	}

	/**
	 * Checks if the last node added would have been outside the map and so
	 * wasn't added
	 * 
	 * @return Was the last node outside the map
	 */
	public boolean wasInvalid() {
		return invalid;
	}

	/**
	 * Returns a list of the nodes in order of priority. The AI prefers nodes
	 * closer to it's current position and so checks those first. This could
	 * easily be extended to factor in high ground
	 * 
	 * @return All of the nodes added, in order of priority
	 */
	public ArrayList<Node> getPriorityList() {
		ArrayList<Node> pList = new ArrayList<Node>();

		for (Node node : nodes) {
			if (node.isCanidate()) {
				pList.add(node);
			}
		}

		return pList;
	}

	/**
	 * Returns all the nodes on the way to the given node as a path to it,
	 * allowing the AI to move to it
	 * 
	 * @param node
	 *            The node to give a path to
	 * @return An ArrayList of all of the nodes generated on the way to the
	 *         node, including the node itself and the robots current position
	 */
	public ArrayList<Coord> getPathTo(Node node) {
		ArrayList<Coord> path = new ArrayList<Coord>();
		Node current;
		for (int i = 0; i < nodes.size(); i++) {
			current = nodes.get(i);
			path.add(current.getLocation());
			if (current.equals(node)) {
				break;
			}
		}
		return path;
	}

	/**
	 * The path to the last node generated. This is used to go as far as
	 * possible
	 * 
	 * @return An ArrayList containing all the nodes on the way to the last node
	 *         generated
	 */
	public ArrayList<Coord> getFailPath() {
		ArrayList<Coord> path = new ArrayList<Coord>();
		for (int i = 0; i < nodes.size(); i++) {
			path.add(nodes.get(i).getLocation());
		}
		return path;
	}

}
