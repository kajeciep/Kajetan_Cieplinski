package com.game.AI;

import com.game.physics.Coord;

/**
 * Each node stores a possible position that the AI could move to
 * 
 * @author Isaac
 *
 */
public class Node {
	// Is this node a possible candidate for moving to
	private boolean candidate;
	private Coord location;

	/**
	 * Creates a node marking a given position
	 * 
	 * @param location
	 *            The position of this node in the world
	 * @param candidate
	 *            Is this node a node that is in consideration to fire from or
	 *            is it to far away
	 */
	public Node(Coord location, boolean candidate) {
		this.location = location;
		this.candidate = candidate;
	}

	/**
	 * Get the location that this node is at
	 * 
	 * @return The location of the node in the world
	 */
	public Coord getLocation() {
		return location;
	}

	/**
	 * Is the node close enough to the target that a projectile could be shot
	 * from it
	 * 
	 * @return Is this a candidate to shoot from
	 */
	public boolean isCanidate() {
		return candidate;
	}
}
