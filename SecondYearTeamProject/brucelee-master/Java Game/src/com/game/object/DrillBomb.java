package com.game.object;

import com.game.physics.Coord;
import com.game.state.GameState;

public class DrillBomb extends Projectile{

	int drillNum;
	public int imgIndex;
	
	public DrillBomb(Coord location, int drillNum) {
		super(12,location,false,40,"drill_bomb", 0);
		this.maxDam = 6;
		this.minDam = 4;
		this.maxVelocity = 15;
		this.drillNum = drillNum;
		this.isRotational = true;
	}
	
	public DrillBomb(Coord location) {
		super(12,location,false,40,"drill_bomb", 0);
		this.maxDam = 6;
		this.minDam = 4;
		this.maxVelocity = 15;
		this.drillNum = 7;
	}
	

	@Override
	public boolean collided(GameState state) {
		if(drillNum < 0) {
			return true;
		}
		Projectile p = new DrillBomb(getPosition(), drillNum - 1);
		p.imgIndex = 200;
		p.setVelocity(getXVelocity());
		p.setYVelocity(getYVelocity());
		state.addProjectile(p);
		return true;
	}
}
