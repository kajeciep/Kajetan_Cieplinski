package com.game.UI;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;

import com.game.physics.Coord;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class HUDComponentTest {

	Coord position = new Coord(50, 100);
	String imageName = "HUDHighBar";
	boolean healthBar = true;

	@Rule
	public JavaFXTestingRule javafxRule = new JavaFXTestingRule();

	@Test
	public void testHUDComponentConstructor() {
		HUDComponent hc = new HUDComponent(imageName, position, healthBar);
		assertSame(hc.getHudImageName(), imageName);
		assertEquals(hc.getXPos(), 50.0, 0.0);
		assertEquals(hc.getYPos(), 100.0, 0.0);
	}

	@Test
	public void testGetBarHUDImage() {
		HUDComponent hc = new HUDComponent(imageName, position, healthBar);
		Image img = new Image("res/HUDHighBar.png");
		Image restructimg = hc.getHudImage(img);
		ImageView iv = new ImageView(img);
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		params.setViewport(new Rectangle2D(0, 0, (img.getWidth()), (0.25 * img.getHeight())));
		img = iv.snapshot(params, null);
		assertEquals(img.getHeight(), restructimg.getHeight(), 0.0);
		assertEquals(img.getWidth(), restructimg.getWidth(), 0.0);
	}

}
