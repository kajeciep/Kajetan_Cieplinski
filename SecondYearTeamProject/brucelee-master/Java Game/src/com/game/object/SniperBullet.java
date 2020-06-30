package com.game.object;

import com.game.physics.Coord;
import com.game.sound.SoundEffect;
import com.game.state.GameState;

import javafx.scene.image.Image;

/**
 * The Projectile corresponding to the Sniper Weapon
 */
public class SniperBullet extends Projectile {

	/**
	 * The default constructor for this Projectile.
	 * @param location the location to spawn the Projectile at.
	 * @param angle the angle at which to launch the Projectile.
	 */
	public SniperBullet(Coord location, int angle) {
		super(10, location, true,0, "bullet", angle);
		this.maxDam = 30;
		this.minDam = 30;
		this.maxVelocity = 20;
		this.setXPos(location.getX());
		this.setYPos(location.getY());
	}

}
