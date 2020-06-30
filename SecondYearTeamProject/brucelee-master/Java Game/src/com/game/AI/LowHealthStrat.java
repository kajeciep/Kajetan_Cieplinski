package com.game.AI;

import com.game.physics.Coord;
import com.game.physics.Robot;
import com.game.state.GameState;

import java.util.ArrayList;

/**
 * This class was intended to be used by the AI when it got to lower health but
 * was never fully implemented
 * 
 */
public class LowHealthStrat implements Strategy {

	@Override
	public Plan generateMovement(Robot r, GameState state) {
		// TODO Auto-generated method stub
		return null;
	}

	private double getHeuristic(Coord location, Robot target) {
		location.getDistance(target.getPosition());
		return 0;
	}

	@Override
	public Robot chooseTarget(Coord location, Robot[] enemyTeam) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAI(AI ai) {

	}
}
