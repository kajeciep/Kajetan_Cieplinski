package com.game.object;

import com.game.physics.Coord;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ImageProjectile extends Pane {

	public ImageView prView;

	private int offsetX = 0;
	private int offsetY = 0;
	private int radius;
	Coord pos = new Coord(0,0);
	
	public Projectile projectile;
	
	public Point2D projectileVelocity = new Point2D(0,0);
	
	public ImageProjectile(Projectile projectile, Image prImg) {
		this.projectile = projectile;
		prView = new ImageView(prImg);
		radius = (int) projectile.getRadius();
		prView.setViewport(new Rectangle2D(offsetX, offsetY, (radius * 2), (radius * 2)));
		getChildren().addAll(this.prView);
	}
}
