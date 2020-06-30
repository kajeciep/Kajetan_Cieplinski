package com.game.object;

import com.game.physics.Coord;
import com.game.sound.SoundEffect;
import com.game.state.GameState;

import javafx.scene.image.Image;

public class Grenade extends Projectile{

	public Grenade(Coord location) {
		super(12, location, false, 60, "grenade", 0);
		this.maxDam = 40;
		this.minDam = 15;
		this.maxVelocity = 15;
	}

}

