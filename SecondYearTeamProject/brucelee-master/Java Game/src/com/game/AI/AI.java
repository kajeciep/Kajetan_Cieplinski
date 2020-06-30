package com.game.AI;

import java.util.ArrayList;
import java.util.Random;

import com.game.physics.Coord;
import com.game.physics.PhysicsObject;
import com.game.state.GameState;
import com.game.weapon.Weapon;
import com.game.physics.Robot;

/**
 * This class is used in single player mode to calculate what movements the
 * computer controlled enemies should make and what angle and velocity they
 * should fire their projectiles at. It also decides what weapon they should use
 * 
 * @author Isaac
 *
 */
public class AI {

	private static final int MAX_FIRE_RANGE = 500;
	private Robot[] enemyTeam;
	private Robot[] myTeam;
	private int difficulty;
	private GameState state;

	/**
	 * Creates the AI when singleplayer mode begins
	 * 
	 * @param enemy
	 *            The enemy teams robots
	 * @param myTeam
	 *            The robots on the same team as the AI
	 * @param difficulty
	 *            The difficulty that the AI is on
	 * @param state
	 *            The state of the world
	 */
	public AI(Robot[] enemy, Robot[] myTeam, int difficulty, GameState state) {
		this.enemyTeam = enemy;
		this.myTeam = myTeam;
		this.difficulty = difficulty;
		this.state = state;
	}

	/**
	 * Ask the AI to generate a plan given the current state of the world
	 * 
	 * @param c
	 *            The active robot that a plan should be created for
	 * @return The plan that the given robot should execute
	 */
	public Plan generatePlan(Robot c) {
		Strategy s = c.getStrategy();
		Robot target = s.chooseTarget(c.getPosition(), enemyTeam);
		s.setAI(this);
		Plan plan = s.generateMovement(target, state);
		return plan;
	}

	/**
	 * Decides which weapon the AI should use based on the range of the weapon
	 * and which ones will hit. The AI chooses weapons at random to avoid using
	 * the same weapons repeatedly
	 * 
	 * @param weapons
	 *            The list of all weapons the AI is able to fire (That have
	 *            ammo)
	 * @param location
	 *            The position that the AI is currently in
	 * @param target
	 *            The robot the AI should target
	 * @return A plan that may contain a fire command telling the AI what angle
	 *         and velocity to fire at to hit the enemy and what weapon it
	 *         shoudl fire
	 */
	public Plan chooseWeapon(Weapon[] weapons, Coord location, Robot target) {
		ArrayList<Weapon> orderedWeapons = new ArrayList<Weapon>();
		// Sort the weapons based on which deals the most damage and removes
		// any that are out of range
		for (Weapon w : weapons) {
			Coord centre = target.getCentre();
			if (w.getRange() > location.getDistance(new Coord((int) target.getXPos(), (int) target.getYPos()))) {
				orderedWeapons.add(w);
			}
		}
		// Fire the weapons at random and return the first one that lands a hit
		// As all projectiles have the same path through the air and max launch
		// velocity, we only need to
		// Check one bullet weapon and one projectile weapon
		boolean bCheck = false;
		boolean pCheck = false;
		Random r = new Random();
		// for(Weapon w : orderedWeapons) {
		while (true) {
			int nextSlot = r.nextInt(orderedWeapons.size());
			Weapon w = orderedWeapons.get(nextSlot);
			// Don't fire if the weapon is the teleport or gravity gun, or has
			// no ammo
			if (nextSlot == 3 || nextSlot == 5 || w.getAmmo() < 1) {
				continue;
			}
			Plan p = null;
			Coord tPos = target.getCentre();
			if (!w.firesBullet() && !bCheck) {
				p = target(location, tPos);
				bCheck = true;
			} else if (!pCheck) {
				p = targetBullet(location, tPos);
				pCheck = true;
			}
			if (p != null && p.hasFireCommand()) {
				// Find the index of the weapon
				int i = 0;
				for (Weapon weapon : weapons) {
					if (weapon == w) {
						p.setWeaponSlot(i);
						return p;
					}
					i++;
				}
			}
			// Break if a projectile and bullet have both been checked
			if (pCheck && bCheck) {
				break;
			}
		}
		System.out.println("UNABLE TO FIND AN ANGLE TO LAUNCH AT");
		return new Plan();
	}

	/**
	 * Target a given location by trying all possible angles and simulating
	 * where a projectile will land. It also adds an error to where the AI
	 * thinks the enemy is based on the current difficulty to ensure the AI
	 * doesn';t hit every single shot that's available to it
	 * 
	 * @param startLocation
	 *            The location that the projectile should start at
	 * @param target
	 *            The position that the projectile should hit
	 * @return A plan that may contain a fire command to hit the target location
	 */
	private Plan target(Coord startLocation, Coord target) {
		double angle = 90;
		double offset = 0;

		// Creates the error for the AI using a gaussian
		Random r = new Random();
		double error = r.nextGaussian() * diffMult() / 10.4;// 20.8;
		// Sets variables
		double tx = startLocation.getX();
		double ty = startLocation.getY();
		double xDiff = Math.abs(target.getX() - tx);
		double yDiff = Math.abs(target.getY() - ty);
		double px = target.getX() + xDiff * error;
		double py = target.getY() + yDiff * error;

		// Creates a physics object to simulate the path
		PhysicsObject finder = new PhysicsObject(5, 5, startLocation, false, "finder");

		// Tries all possible launches to see which works
		while (true) {
			for (double i = 0; i <= 400; i += 16) {
				finder.resetValues();
				// Calculate the new launch angle
				double launchAngle = (angle - offset);
				// Set the parameters for finder
				finder.launch(launchAngle, i);
				// If it gets within acceptable error, return these parameters
				// as the plan
				if (finder.simulate(px, py, state, myTeam)) {
					return new Plan((int) i, (int) launchAngle);
				}
				/*
				 * Calculate the angle with a negative offset instead. This
				 * means the AI searches angles close to 45 degrees first and
				 * expands out from there
				 */
				finder.resetValues();
				launchAngle = (angle + offset);
				finder.launch(launchAngle, i);
				if (finder.simulate(px, py, state, myTeam)) {
					return new Plan((int) i, (int) launchAngle);
				}
			}
			offset -= 1;
			if (offset < -90) {
				return new Plan();
			}
		}
	}

	/**
	 * Finds the angle to fire a bullet at to hit the chosen target and then
	 * checks to see if it hits or if collides with the terrain. It also adds
	 * error to the enemy position
	 * 
	 * @param startLocation
	 *            The location the bullet is firing from
	 * @param target
	 *            The location of the target that the bullet should be fired at
	 * @return An empty plan if the bullet doesn't hit and a plan with the
	 *         correct angle if it does
	 */
	private Plan targetBullet(Coord startLocation, Coord target) {
		// Creates the error for the AI using a gaussian
		Random r = new Random();
		double error = r.nextGaussian() * diffMult() / 20.8;
		// Sets variables
		double tx = startLocation.getX();
		double ty = startLocation.getY();
		double xDiff = Math.abs(target.getX() - tx);
		double yDiff = Math.abs(target.getY() - ty);
		double px = target.getX() + xDiff * error * diffMult();
		double py = target.getY() + yDiff * error * diffMult();

		// Calculate angle from horizon
		double dx = Math.abs(px - tx);
		double dy = Math.abs(py - ty);
		double angle;
		if (tx > px && ty > py) {
			angle = 90 + Math.toDegrees(Math.atan(dx / dy));
		} else if (tx > px && ty < py) {
			angle = 180 + Math.toDegrees(Math.atan(dy / dx));
		} else if (tx < px && ty < py) {
			angle = 270 + Math.toDegrees(Math.atan(dx / dy));
		} else {
			angle = Math.toDegrees(Math.atan(dy / dx));
		}
		// Creates a physics object to simulate the path
		PhysicsObject finder = new PhysicsObject(5, 5, startLocation, true, "finder");
		// Tries to see if this collides with the player
		finder.launch(angle, 200);
		if (finder.simulate(px, py, state, myTeam)) {
			return new Plan(200, (int) angle);
		}
		return new Plan();
	}

	/**
	 * Used to give a different error based on the current difficulty of the AI
	 * 
	 * @return The value that the error should be scaled by on this difficulty
	 */
	public double diffMult() {
		if (difficulty == 2) {
			return 0.6;
		} else if (difficulty == 1) {
			return 1;
		} else {
			return 1.4;
		}
	}
}
