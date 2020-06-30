package com.game.object;

import com.game.physics.Coord;
import com.game.sound.SoundEffect;
import com.game.state.GameState;

import javafx.scene.image.Image;

public class ClusterBomb extends Projectile{
	
	public ClusterBomb(Coord location, int angle) {
		super(12, location, false, 0, "cluster_bomb", 0);
		this.maxDam = 10 ;
		this.minDam = 10;
		this.maxVelocity = 15;
	}

	@Override
	public boolean collided(GameState state) {
		int launchAngle = 140;
		for(int i = 0; i < 10; i++) {
			Projectile p = new ClusterGrenade(new Coord((int)xPos,(int)yPos), 0);
			p.launch(launchAngle, 110);
			state.addProjectile(p);
			launchAngle -= 10;
		}
		return true;
	}

}
