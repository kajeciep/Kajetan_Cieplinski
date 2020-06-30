package com.game.AI;

import java.util.ArrayList;

import com.game.physics.Coord;

import javafx.scene.input.KeyCode;

/**
 * A class that stores the variables used for the AI's turn. It can be used by
 * an AI robot to move and fire at a member of the opposing team. It gives
 * instructions to the AI about how to reach the given point
 * 
 * @author Isaac
 *
 */
public class Plan {
	int power;
	int angle;
	ArrayList<Coord> moveSpots = new ArrayList<Coord>();
	int moveIterator = 0;
	int weaponSlot;

	/**
	 * Sets the values to launch a projectile at this angle and power
	 * 
	 * @param power
	 *            The power to be launched at
	 * @param angle
	 *            The angle to be launched at
	 */
	public Plan(int power, int angle) {
		this.power = power;
		this.angle = angle;
	}

	/**
	 * Creates a plan with no fire command meaning that it won't attempt to fire
	 * a projectile
	 */
	public Plan() {
		power = 0;
		angle = 0;
	}

	/**
	 * Returns true if the AI has generated a command to fire
	 * 
	 * @return if the AI wants to fire
	 */
	public boolean hasFireCommand() {
		return power != 0;
	}

	/**
	 * 
	 * @return The velocity that the projectile should be launched at
	 */
	public int getVelocity() {
		return power;
	}

	/**
	 * 
	 * @return The angle that should be fired at
	 */
	public int getAngle() {
		return angle;
	}

	/**
	 * Get the weapon to be fired
	 * 
	 * @return The weapon index
	 */
	public int getWeaponSlot() {
		return weaponSlot;
	}

	/**
	 * Sets the weapon to be fired
	 * 
	 * @param weaponSlot
	 *            The index to fire
	 */
	public void setWeaponSlot(int weaponSlot) {
		this.weaponSlot = weaponSlot;
	}

	/**
	 * Sets the movement that the AI should take during it's turn
	 * 
	 * @param mov
	 *            The list of all the nodes that the AI should travel
	 */
	public void setMovement(ArrayList<Coord> mov) {
		moveSpots = mov;
	}

	/**
	 * Checks if the plan has more movement commands left to be executed
	 * 
	 * @return true if there are more commands left for the AI to move
	 */
	public boolean hasMoreCommands() {
		return moveIterator < moveSpots.size();
	}

	/**
	 * Get the next code for whcih way the AI should move to reach it's position
	 * 
	 * @param The
	 *            current location of the AI
	 * @return The keycode for which button press should be input to move the AI
	 */
	public KeyCode getNextCode(Coord location) {
		Coord nextMove = moveSpots.get(moveIterator);
		KeyCode val;
		if (location.getX() > nextMove.getX()) {
			val = KeyCode.A;
		} else {
			val = KeyCode.D;
		}
		if (Math.abs(location.getX() - nextMove.getX()) < 4) {
			moveIterator++;
		}
		return val;
	}

}
