package com.game.object;

import com.game.physics.Coord;
import com.game.sound.SoundEffect;
import com.game.state.GameState;

import javafx.scene.image.Image;

public class ClusterGrenade extends Projectile{
	
	public ClusterGrenade(Coord location, int angle) {
		super(5, location, false, 20, "cluster_grenade", 0);
		this.maxDam = 10;
		this.minDam = 5;
		this.maxVelocity = 15;
	}
	
}
