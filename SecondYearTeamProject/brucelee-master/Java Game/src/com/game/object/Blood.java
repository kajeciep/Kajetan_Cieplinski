package com.game.object;

import com.game.physics.Coord;
import com.game.physics.PhysicsObject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;


public class Blood extends PhysicsObject {
	
	private int imgIndex = 0;

	public Blood(Coord location, int bloodRadius) {
		super(bloodRadius, bloodRadius, location, false, "blood");
		
		EventHandler<ActionEvent> eventHandler = e -> {
			if (imgIndex == 0) {
				this.name = "blood1";
				imgIndex++;

			} else if (imgIndex == 1) {
				this.name = "blood2";
				imgIndex++;

			} else if (imgIndex == 2) {
				this.name = "blood3";
				imgIndex++;

			} else if (imgIndex == 3) {
				this.name = "blood4";
				imgIndex++;
				
			} else if (imgIndex == 4) {
				this.name = "blood5";
				imgIndex++;

			} else if (imgIndex == 5) {
				this.name = "blood6";
				imgIndex++;

			} else if (imgIndex == 6) {
				this.name = "blood7";
				imgIndex++;
			}
			else if (imgIndex == 7) {
				this.name = "blood8";
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
