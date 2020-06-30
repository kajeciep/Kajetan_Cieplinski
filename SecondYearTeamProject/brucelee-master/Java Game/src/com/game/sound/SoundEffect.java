package com.game.sound;

import java.io.File;
import java.net.URI;
import java.util.HashMap;

import javafx.scene.media.AudioClip;

import static javafx.scene.media.AudioClip.INDEFINITE;

/**
 * Used to store the data for specific sound effects as they are generated during a game.
 * @author Joshua Minton
 */
public class SoundEffect {
    protected AudioClip effect;
    protected String effectName;
    protected boolean stop;
    protected int priority;
    protected boolean loop;

    /**
     * Default constructor for a SoundEffect
     * @param name the name to be given to the sound effect. Should correspond in some way to a file in the resource folder.
     */
    public SoundEffect(String name){
    	effectName = name;
    }

    /**
     * Extended constructor, taking a name, priority string, and loop boolean.
     * @param name the name to be given to the SoundEffect. Should correspond in some way to a file in the resource folder.
     * @param priority the priority to be given to the SoundEffect. Should be "HIGH", "MEDIUM", or "LOW" (other values are equivalent to "LOW".)
     * @param loop whether or not this effect should loop.
     */
    public SoundEffect(String name, String priority, boolean loop){
        effectName = name;
        setPriority(priority);
        this.loop = loop;
    }

    /**
     * Sets the AudioClip for this SoundEffect.
     * @param audioLibrary a hashmap with AudioClips associated to Strings, from which the AudioClip is found, using the existing name for this SoundEffect.
     */
    public void setAudio( HashMap<String, AudioClip> audioLibrary){
        effect = audioLibrary.get(effectName + ".wav");
    }

    /**
     * Plays the AudioClip, if it exists, looping it if neccessary.
     */
    public void play(){
        if(effect != null) {
            if(loop){
                effect.setCycleCount(INDEFINITE);
            }
            effect.play();
        }
    }


    /**
     * Sets the priority field of the SoundEffect.
     * @param priority the priority to be given to the SoundEffect. Should be "HIGH", "MEDIUM", or "LOW" (other values are equivalent to "LOW".)
     */
    public void setPriority(String priority){
        if(priority.equals("HIGH")){
            this.priority = 1;
        }else if(priority.equals("MEDIUM")){
            this.priority = 2;
        }else{
            this.priority = 3;
        }
    }

    /**
     * Sets the volume field of the SoundEffect.
     * @param volume the desired volume value. 1 is 'normal' volume.
     */
    public void setVolume(double volume){
        if(effect != null) {
            effect.setVolume(volume);
        }
    }

    /**
     * Stops the SoundEffect's AudioClip playing.
     */
    public void stop(){
        stop = true;
        if(effect != null) {
            effect.stop();
        }
    }

	public AudioClip getEffect() {
		return effect;
	}

	public String getEffectName() {
		return effectName;
	}

	public int getPriority() {
		return priority;
	}

	public boolean isLoop() {
		return loop;
	}


}
