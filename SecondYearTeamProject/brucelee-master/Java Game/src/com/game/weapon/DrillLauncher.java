package com.game.weapon;

import com.game.object.DrillBomb;
import com.game.object.Projectile;
import com.game.physics.Coord;

//Deals little damage to the player but can be used to drill through the terrian and drop enemies in lava
//Or to create paths through mountains
public class DrillLauncher extends Weapon{
	
	public DrillLauncher(int ammo) {
		super(1500, ammo,false, "drill_launcher", 70);
		weapondId = 2;
	}

	@Override
	public Projectile createProjectile(Coord location, int angle) {
		return new DrillBomb(location);
	}
}
