package com.game.object;

import com.game.physics.Coord;
import com.game.sound.SoundEffect;
import com.game.state.GameState;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;

public class GravGrenade extends Projectile {
    public GravGrenade(Coord location, int angle) {
        super(10, location, false, 120, "gravity_grenade", 0);
        this.maxDam = 10;
        this.minDam = 10;
        this.maxVelocity = 15;
    }

    @Override
    public boolean collided(GameState state) {
        Coord centre = this.getCentre();
        state.shiftRobots(centre.getX(), centre.getY(), (int)explosiveRadius, 300);
        state.getWorld().shiftTerrain(centre.getX(), centre.getY(), (int)explosiveRadius, 300);
        return true;
    }
}
