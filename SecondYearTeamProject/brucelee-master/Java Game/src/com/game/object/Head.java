package com.game.object;

import com.game.physics.Coord;
import com.game.physics.PhysicsObject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;


public class Head extends PhysicsObject {
	
	private int imgIndex = 0;

	public Head(Coord location, int hRadius) {
		super(hRadius, hRadius, location, false, "head");
		
		EventHandler<ActionEvent> eventHandler = e -> {
			if(imgIndex >= 0 && imgIndex < 2)
				imgIndex++;
			
			else if (imgIndex == 2) {
				this.name = "head";
				imgIndex++;

			} else if (imgIndex == 3) {
				this.name = "head1";
				imgIndex++;

			} else if (imgIndex == 4) {
				this.name = "head2";
				imgIndex++;

			} else if (imgIndex == 5) {
				this.name = "head3";
				imgIndex++;
				
			} else if (imgIndex == 6) {
				this.name = "head4";
				imgIndex++;

			} else if (imgIndex == 7) {
				this.name = "head5";
				imgIndex++;

			} else if (imgIndex == 8) {
				this.name = "head6";
				imgIndex++;
			}
			else if (imgIndex == 9) {
				this.name = "head7";
				imgIndex++;
			}
		};
		
		Timeline animation = new Timeline(new KeyFrame(Duration.millis(100), eventHandler));

		animation.setCycleCount(Timeline.INDEFINITE);
		animation.play();
		
	}

}