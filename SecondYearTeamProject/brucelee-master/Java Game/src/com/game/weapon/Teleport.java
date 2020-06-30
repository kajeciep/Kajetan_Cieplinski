package com.game.weapon;

import com.game.object.Projectile;
import com.game.object.Teleporter;
import com.game.physics.Coord;

import javafx.scene.image.Image;

//Teleports the player to where the projectile lands. Used for positioning and not damage
public class Teleport extends Weapon{
	public Teleport(int ammo) {
		super(1500, ammo, false, "teleporter", 85);
		weapondId = 9;
	}

	@Override
	public Projectile createProjectile(Coord location, int angle) {
		return new Teleporter(location, angle);
	}
}
