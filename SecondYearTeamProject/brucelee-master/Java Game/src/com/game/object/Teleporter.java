package com.game.object;

import com.game.physics.Coord;
import com.game.physics.Robot;
import com.game.sound.SoundEffect;
import com.game.state.GameState;

import javafx.scene.image.Image;

/**
 * The Projectile corresponding to the Teleport Weapon.
 */
public class Teleporter extends Projectile{

	/**
	 * The default constructor for this Projectile.
	 * @param location the location to spawn the Projectile at.
	 */
	public Teleporter(Coord location, int angle) {
		super(10, location, false, 0, "teleport_grenade", 0);
		this.maxDam = 10;
		this.minDam = 10;
		this.maxVelocity = 20;
		this.xPos = location.getX();
		this.yPos = location.getY();
	}

	@Override
	public boolean collided(GameState state) {
		Robot target = state.getCurrentChar();
		target.setXPos((int)xPos);
		target.setYPos((int) (yPos - target.getCollisionBox().getHeight()));
//		////System.out.println(target.getYPos());
		return true;
	}


	
	

}
