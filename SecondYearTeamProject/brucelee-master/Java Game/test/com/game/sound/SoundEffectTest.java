package com.game.sound;

import com.game.sound.SoundEffect;
import java.io.File;
import java.util.HashMap;

import javafx.scene.media.AudioClip;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SoundEffectTest {
	
	@Test
	public void creationTest() throws Exception {
		SoundEffect soundEffect = new SoundEffect("testSound", "HIGH", true);
		assert(soundEffect.getEffectName().equals("testSound"));
		assert(soundEffect.getPriority() == 1);
		assert(soundEffect.isLoop());
	}

	@Test
	public void audioSetTest() throws Exception {
		File soundFile = new File("Java Game/src/res/sounds/menu.wav");
		AudioClip soundAudioClip = new AudioClip(soundFile.toURI().toString());
		SoundEffect soundEffect = new SoundEffect("testSound", "HIGH", true);
		HashMap<String, AudioClip> soundHash = new HashMap<>();
		soundHash.put("testSound.wav", soundAudioClip);
		soundEffect.setAudio(soundHash);
		assert(soundEffect.getEffect().equals(soundAudioClip));
	}

	@Test
	public void audioPlayNullTest() {
		SoundEffect soundEffect = new SoundEffect("abc");
		try{
			soundEffect.play();
		}catch(NullPointerException e){
			fail("SoundEffect should cope with trying to play an uninitialised AudioClip.");
		}
	}

	@Test
	public void audioStopTest() {
		SoundEffect soundEffect = new SoundEffect("abc");
		try{
			soundEffect.stop();
		}catch(NullPointerException e){
			fail("SoundEffect should cope with trying to stop an uninitialised AudioClip.");
		}
	}

	@Test
	public void setPriorityTest() {
		SoundEffect soundEffect = new SoundEffect("abc");
		soundEffect.setPriority("HIGH");
		assertEquals(1, soundEffect.getPriority());
		soundEffect.setPriority("MEDIUM");
		assertEquals(2, soundEffect.getPriority());
		soundEffect.setPriority("LOW");
		assertEquals(3, soundEffect.getPriority());
		soundEffect.setPriority("COVFEFE");
		assertEquals(3, soundEffect.getPriority());
	}
}
