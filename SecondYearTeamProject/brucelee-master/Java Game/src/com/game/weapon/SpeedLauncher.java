package com.game.weapon;

import com.game.object.Projectile;
import com.game.object.SpeedBomb;
import com.game.physics.Coord;

//The faster is hits the ground, the bigger te explosion it deals
public class SpeedLauncher extends Weapon {
	
	public SpeedLauncher(int ammo) {
		super(1500,ammo,false,"speed_launcher", 70);
		weapondId = 8;
	}

	@Override
	public Projectile createProjectile(Coord location, int angle) {
		return new SpeedBomb(location, angle);
	}

}
