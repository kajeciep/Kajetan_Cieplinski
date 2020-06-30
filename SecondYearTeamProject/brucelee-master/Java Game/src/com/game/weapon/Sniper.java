package com.game.weapon;

import com.game.object.Projectile;
import com.game.object.SniperBullet;
import com.game.physics.Coord;

//Long range weapon used to deal damage when you have line of sight. Easier to aim but deals less damage
public class Sniper extends  Weapon {
    public Sniper(int ammo) {
        super(5000, ammo, true, "sniper", 65);
        weapondId = 7;
    }

	@Override
	public Projectile createProjectile(Coord location, int angle) {
		return new SniperBullet(location, angle);
	}
}
