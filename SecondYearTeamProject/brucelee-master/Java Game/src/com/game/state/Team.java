package com.game.state;

import java.util.ArrayList;

import com.game.physics.Robot;

/**
 * This class stores the members of a team and tracks which one should be controlled next
 */
public class Team {
	private ArrayList<Robot> members;
	private int nextPlayer;
	
	/**
	 * Creates a team from the given robots
	 * @param members The robots to be a part of this team
	 */
	public Team(ArrayList<Robot> members) {
		this.members = members;
		this.nextPlayer = 0;
	}
	
	/**
	 * Gets the next living member of the team that should be controlled
	 * @return The Robot to be controlled
	 */
	public Robot getNextPlayer() {
		//Get the next player
		nextPlayer++;
		//If bigger than remaining players start at the beginning again
		if(nextPlayer > members.size() - 1) {
			nextPlayer = 0;
		}
		Robot r = members.get(nextPlayer);
		//Check to see if the player is dead
		while(r.getHealth() < 1) {
			members.remove(nextPlayer);
			r = members.get(nextPlayer);
		}
		return r;
	}

	/**
	 * Removes a robot from the team
	 * @param r The robot to be removed
	 */
	public void removeRobot(Robot r) {
		members.remove(r);
	}
	
	/**
	 * Checks if the given team has lost
	 * @return True if the team has no more members alive
	 */
	public boolean hasLost() {
		return members.size() < 1;
	}
	
	/**
	 * Gets all of the members of a team
	 * @return The teams members
	 */
	public ArrayList<Robot> getRobots() {
		return members;
	}
}
