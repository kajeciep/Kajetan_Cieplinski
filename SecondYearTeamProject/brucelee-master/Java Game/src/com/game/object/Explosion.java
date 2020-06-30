package com.game.object;

import com.game.physics.Coord;
import com.game.state.GameState;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class Explosion extends Projectile {
	
	private int imgIndex = 0;

	public Explosion(Coord location, int expRadius) {
		super(expRadius, location, false, expRadius, "explosion", 0);
		EventHandler<ActionEvent> eventHandler = e -> {
			if (imgIndex == 0) {
				this.name = "explosion0";
				imgIndex++;

			} else if (imgIndex == 1) {
				this.name = "explosion1";
				imgIndex++;

			} else if (imgIndex == 2) {
				this.name = "explosion2";
				imgIndex++;

			} else if (imgIndex == 3) {
				this.name = "explosion3";
				imgIndex++;
				
			} else if (imgIndex == 4) {
				this.name = "explosion4";
				imgIndex++;

			} else if (imgIndex == 5) {
				this.name = "explosion5";
				imgIndex++;

			} else if (imgIndex == 6) {
				this.name = "explosion6";
				imgIndex++;
			}
			else if (imgIndex == 7) {
				this.name = "explosion7";
				imgIndex++;
			}
			else if (imgIndex == 8) {
				this.name = null;
			}
		};
		
		Timeline animation = new Timeline(new KeyFrame(Duration.millis(100), eventHandler));

		animation.setCycleCount(Timeline.INDEFINITE);
		animation.play();
		
	}

}
