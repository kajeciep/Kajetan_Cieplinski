package com.game.object;

import com.game.physics.Coord;

/**
 * The Projectile corresponding to the RocketLauncher Weapon.
 */
public class Rocket extends Projectile {

	/**
	 * The default constructor for this Projectile.
	 * @param location the location to spawn the Projectile at.
	 */
	public Rocket(Coord location, int angle) {
		// All values are placeholder values for the actual game values
		super(12, location, false, 70, "rocket", 0);
		this.maxDam = 20;
		this.minDam = 15;
		this.maxVelocity = 10;
		this.imgIndex = 0;
		this.isRotational = true;
	}
}
