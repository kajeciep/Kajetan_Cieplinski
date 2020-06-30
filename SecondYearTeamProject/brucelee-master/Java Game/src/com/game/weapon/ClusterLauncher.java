package com.game.weapon;

import com.game.object.ClusterBomb;
import com.game.object.Projectile;
import com.game.physics.Coord;

import javafx.scene.image.Image;
//Fires a bomb that deals little damage but that launches multiple little bombs on impact
public class ClusterLauncher extends Weapon{
	
	public ClusterLauncher(int ammo) {
		super(1500,ammo,false, "cluster_launcher", 95);
		weapondId = 1;
	}

	@Override
	public Projectile createProjectile(Coord location, int angle) {
		return new ClusterBomb(location, angle);
	}
}
