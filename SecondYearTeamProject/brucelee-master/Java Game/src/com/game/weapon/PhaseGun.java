package com.game.weapon;

import com.game.object.Projectile;
import com.game.physics.Coord;
import com.game.object.PhaseLaser;

//Fires in a straight line but deals little damage. Can be used to finish of hard to reach enemies
//as well as to destroy supply drops before enemy reaches them
public class PhaseGun extends Weapon {
	
    public PhaseGun(int ammo) {
        super(5000, ammo, true, "phase_gun", 70);
        weapondId = 5;
    }
    
	@Override
	public Projectile createProjectile(Coord location, int angle) {
		return new PhaseLaser(location, angle);
	}
    
}
