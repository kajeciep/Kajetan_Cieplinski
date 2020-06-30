package com.game.object;

import com.game.physics.Coord;
import com.game.state.GameState;

/**
 * The Projectile corresponding to the SpeedLauncher Weapon.
 */
public class SpeedBomb extends Projectile {

	/**
	 * The default constructor for this Projectile.
	 * @param location the location to spawn the Projectile at.
	 */
	public SpeedBomb(Coord location, int angle) {
		super(25,location,false,30,"speed_bomb", 0);
		this.maxDam = 25;
		this.minDam = 15;
		this.maxVelocity = 15;
	}
	
	@Override
	public boolean collided(GameState state) {
		explosiveRadius += Math.abs( yVelocity / 5);
		System.out.println("ERADIUS WAS " + explosiveRadius);
		maxDam += Math.abs(yVelocity / 35);
		maxDam += Math.abs(xVelocity / 35);
		return true;
	}
}
