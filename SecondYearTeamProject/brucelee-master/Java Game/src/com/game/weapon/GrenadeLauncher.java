package com.game.weapon;

import com.game.object.Grenade;
import com.game.object.Projectile;
import com.game.physics.Coord;

import javafx.scene.image.Image;

//Has a bigger radius than the Rocket launcher but deals less damage
public class GrenadeLauncher extends Weapon{
	
	public GrenadeLauncher(int ammo) {
		super(1500, ammo, false, "grenade_launcher", 80);
		weapondId = 4;
	}

	@Override
	public Projectile createProjectile(Coord location, int angle) {
		return new Grenade(location);
	}
}
