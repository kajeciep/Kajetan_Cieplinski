package com.game.object;

import java.util.Random;

import com.game.physics.Coord;
import com.game.physics.PhysicsObject;
import com.game.sound.SoundEffect;

import javafx.scene.image.Image;

/**
 * <p>
 * 		A SupplyDrop is a crate dropped in game. They provide ammo or a health boost to robots that pick them up.
 * 		They spawn in the sky and fall so are affected by physics.
 * </p>
 */
public class SupplyDrop extends PhysicsObject {

	protected SoundEffect fireSound;
	protected SoundEffect impactSound;
	protected SoundEffect collectSound;

	/**
	 * Default constructor for SupplyDrop.
	 * <p>
	 *     Create a SupplyDrop at the specified location.
	 * </p>
	 * @param location the location for the SupplyDrop to spawn.
	 */
	public SupplyDrop(Coord location) {
		super(40, 40, location, false, "supply");
		fireSound = new SoundEffect(name.concat("_fire"));
		impactSound = new SoundEffect(name.concat("_impact"));
		collectSound = new SoundEffect(name.concat("_collect"));
	}

	/**
	 * @return get the SoundEffect for the spawning of the SupplyDrop.
	 */
	public SoundEffect getFireSound() {
		return fireSound;
	}

	/**
	 * @return get the SoundEffect for the landing of the SupplyDrop.
	 */
	public SoundEffect getImpactSound() {
		return impactSound;
	}

	/**
	 * @return get the SoundEffect for when a Robot picks up the SupplyDrop.
	 */
	public SoundEffect getCollectSound() {
		return collectSound;
	}
}
