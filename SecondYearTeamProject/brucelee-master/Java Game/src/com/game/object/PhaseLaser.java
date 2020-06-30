package com.game.object;

import com.game.physics.Coord;
import com.game.sound.SoundEffect;
import com.game.state.GameState;

import javafx.scene.image.Image;

/**
 * The Projectile corresponding to the PhaseGun Weapon.
 */
public class PhaseLaser extends Projectile {

	/**
	 * The default constructor for this Projectile.
	 * @param location the location to spawn the Projectile at.
	 * @param angle the angle at which to launch the Projectile.
	 */
	public PhaseLaser(Coord location, int angle) {
		super(10, location, true,0, "phase_laser_air",angle);
		this.maxDam = 40;
		this.minDam = 40;
		this.maxVelocity = 20;
		this.setXPos(location.getX());
		this.setYPos(location.getY());
		this.fireSound = new SoundEffect("phase_laser_fire");
		this.impactSound = new SoundEffect("phase_laser_impact");
	}

	/**
	 * This method is for moving the simulating the projectile path.
	 * @param time The time the simulation should be stepped forward to simulate velocities.
	 */
	@Override
	public void moveObject(double time) {
		this.name = "phase_laser_air";
		xPos += xVelocity * time;
		yPos -= yVelocity * time; // This is because the y co-ordinate increases
									// as you go down the screen
		// Decrease velocity according to gravity
		if (!isBullet && isFalling) {
			yVelocity -= GameState.gravity * time;
			if(yVelocity < maxVelocity) {
				yVelocity = maxVelocity;
			}
		}

	}

	/**
	 * @param state The GameState of the world.
	 * @return whether or not the Projectile collided with terrain. Always false for PhaseLaser.
	 */
	@Override
	public boolean collided(GameState state) {
		this.name = "phase_laser_ground";
		return false;
	}

}
