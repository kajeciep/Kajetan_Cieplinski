package com.game.AI;

import java.util.ArrayList;

import com.game.physics.Coord;
import com.game.physics.Robot;
import com.game.state.GameState;
import com.game.weapon.Weapon;

/**
 * This class should be used in the case of the AI having high health and makes
 * it play more aggressively and closer to the player. It is used for deciding
 * the exact movements of the AI as well as what enemy it should target
 * 
 * @author Isaac
 *
 */
public class HighHealthStrat implements Strategy {

	private Robot computer;
	private final int MAX_FIRE_RANGE = 1000;
	private AI ai;

	/**
	 * Creates a highHealthStrat for this AI controlled robot
	 * 
	 * @param computer
	 *            The robot this makes decisions for
	 */
	public HighHealthStrat(Robot computer) {
		this.computer = computer;
	}

	@Override
	public Plan generateMovement(Robot target, GameState state) {
		int energy = computer.getMaxEnergy();
		Coord centre = computer.getCentre();
		double distance = centre.getDistance(target.getCentre());
		// Should this be considered a node to shoot from
		// The AI should always move once to avoid making the game too easy for
		// the player
		boolean candidate = false;
		// ArrayList of all possible positions
		NodeList list = new NodeList(state, (int) computer.getXPos(), (int) computer.getYPos());
		int energyChange = 33;
		while (energy - energyChange > 0) {
			energy -= energyChange;
			// Should we move right or left
			candidate = Math.abs(distance) <= MAX_FIRE_RANGE;
			boolean isRight = centre.getX() < target.getCentre().getX();
			boolean isLeft = centre.getX() > target.getCentre().getX();
			if ((isRight && distance > 300) || (isLeft && distance < 300)) {
				// Move right if the target is right or very close to our left
				list.addRightNode(target.getCollisionBox().getHeight(), candidate);
			} else {
				// Move left if the target is left or very close to our right
				list.addLeftNode(target.getCollisionBox().getHeight(), candidate);
			}
			distance = list.getCurrentPos().getDistance(target.getCentre());
			if (list.wasInvalid()) {
				break;
			}
		}
		ArrayList<Node> candidates = list.getPriorityList();
		for (Node node : candidates) {
			// Try all priority list
			Plan plan = ai.chooseWeapon(computer.getAllWeapons().toArray(new Weapon[9]), node.getLocation(), target);
			if (plan.hasFireCommand()) {
				ArrayList<Coord> path = list.getPathTo(node);
				plan.setMovement(path);
				return plan;
			}
		}
		Plan plan;
		// If the teleporter has ammo, fire that
		if (computer.getAllWeapons().get(3).getAmmo() > 0) {
			if (computer.getXPos() < target.getXPos()) {
				plan = new Plan(350, 45);
			} else {
				plan = new Plan(350, 135);
			}
			plan.setWeaponSlot(3);
		} else {
			// AI walks closer to target
			plan = new Plan();
			plan.setMovement(list.getFailPath());
		}
		return plan;
	}

	@Override
	public Robot chooseTarget(Coord location, Robot[] enemyTeam) {
		Robot target = null;
		double currentHeuristic = Double.MAX_VALUE;
		for (Robot enemy : enemyTeam) {
			if (enemy == null || enemy.getHealth() < 1) {
				continue;
			}
			double nextHeuristic = this.getHeuristic(location, enemy);
			if (currentHeuristic > nextHeuristic) {
				currentHeuristic = nextHeuristic;
				target = enemy;
			}
		}
		return target;
	}

	/**
	 * A heuristic function that can be used to decide which robot to target.
	 * The value it returns is the euclidean distance to the target
	 * 
	 * @param location
	 *            The location of the AI controlled robot
	 * @param target
	 *            The robot to get a target heuristic for
	 * @return The heuristic value for this robot
	 */
	private double getHeuristic(Coord location, Robot target) {
		double distance = location.getDistance(target.getPosition()) / 100;
		double health = target.getHealth();
		return distance * health;
	}

	@Override
	public void setAI(AI ai) {
		this.ai = ai;
	}
}
