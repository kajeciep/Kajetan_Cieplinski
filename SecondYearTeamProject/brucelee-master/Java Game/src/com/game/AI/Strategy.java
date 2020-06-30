package com.game.AI;

import java.util.ArrayList;

import com.game.physics.Coord;
import com.game.physics.Robot;
import com.game.state.GameState;

/**
 * This interface stores the methods that all strategies should have. It's set
 * up to follow the strategy pattern
 * 
 * @author Isaac
 *
 */
public interface Strategy {
	/**
	 * Generate movement for the given robot over the terrain as it currently is
	 * to a position that it can fire and hit a robot on the enemy team. If not
	 * then it will attempt to use it's teleporter or to simply move as far as
	 * possible
	 * 
	 * @param r
	 *            The robot that a path is needed for
	 * @param state
	 *            The current state of the world allowing then robot to move
	 *            over the terrain
	 * @return A plan containing movement instructions and fire instructions for
	 *         the robot
	 */
	public Plan generateMovement(Robot r, GameState state);

	/**
	 * Choose which robot on the enemy team should be targeted
	 * 
	 * @param location
	 *            The current position in the world of the robot
	 * @param enemyTeam
	 *            All the enemies on the opposite team that are currently still
	 *            alive
	 * @return The robot on the enemy team that has been decided as the best
	 *         target
	 */
	public Robot chooseTarget(Coord location, Robot[] enemyTeam);

	/**
	 * Set the AI that should be used to do the targeting for this strategy
	 * 
	 * @param ai
	 *            The AI to be used when calculating power and velocity for the
	 *            AI
	 */
	public void setAI(AI ai);

}
