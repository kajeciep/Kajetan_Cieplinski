package com.game.weapon;

import com.game.object.Projectile;
import com.game.object.Rocket;
import com.game.physics.Coord;

import javafx.scene.image.Image;

//Most common weapon. Deals a lot of damage on a direct hit, but has a small explosion radius
public class RocketLauncher extends Weapon {
    public RocketLauncher(int ammo) {
    	super(1500,ammo, false, "rocket_launcher", 50);
    	weapondId = 6;
    }

	@Override
	public Projectile createProjectile(Coord location, int angle) {
		return new Rocket(location, angle);
	}
}
