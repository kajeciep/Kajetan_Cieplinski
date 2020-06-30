package com.game.weapon;

import com.game.object.GravGrenade;
import com.game.object.Projectile;
import com.game.physics.Coord;

//Multiplayer only item. Shifts the terrain up in the area it hits
public class GravityGun extends Weapon {
    public GravityGun(int ammo) {
        super(5000, ammo, false, "gravity_gun", 80);
        weapondId = 3;
    }

    @Override
    public Projectile createProjectile(Coord location, int angle) {
        return new GravGrenade(location, angle);
    }
}
