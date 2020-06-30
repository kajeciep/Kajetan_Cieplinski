package com.game.graphics;

import com.game.UI.HUDComponent;
import com.game.object.Blood;
import com.game.object.Explosion;
import com.game.object.Head;
import com.game.object.Projectile;
import com.game.physics.PhysicsObject;
import com.game.physics.Robot;
import com.game.sound.SoundEffect;
import com.game.state.GameState;
import com.game.state.GameWorld;
import com.game.weapon.Weapon;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import javafx.scene.media.AudioClip;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;

/**
 * A class to output the contents of a GameState to the user, both visually and with audio.
 * The UI creates an instance of this class, which it uses to update the GraphicsContext with the latest GameState.
 * @author Joshua Minton
 */
public class Renderer {

    private ArrayList<SoundEffect> playedSoundEffects = new ArrayList<SoundEffect>();
    private Image weaponImg, projectileImg;
    private Image rotWeaponImg;
    private Color lavaColour;
    private Color baseLavaColour = Color.ORANGE;
    private Color highLavaColour = Color.ORANGERED;
    private double redDiff, greenDiff, blueDiff, opacityDiff;
    private double lavaCycleLengthSeconds = 2;
    private double colourCount, opacityCount, max, opacity, tide, val;
    private Integer rollingY;
    private GraphicsContext gc;

    private double universalScale = 0.125;
    private double masterVolume;

    private int lastSlot = 444;
    private int lastAngle = 444;
    private boolean facingLeft = false;
    private Weapon weapon;

    protected HashMap<String, Image> imageHash = new HashMap<>();
    protected HashMap<String, AudioClip> soundHash = new HashMap<>();

    private SoundEffect music;


    /**
     * Default constructor for all Renderers.
     * Inititates the values for the lava colour transitions and opacity changes.
     * @param masterVolume The volume at which all sound effects should be played at.
     */
    public Renderer(double masterVolume) {
        lavaColour = baseLavaColour;
        redDiff = (highLavaColour.getRed() - baseLavaColour.getRed()) / (lavaCycleLengthSeconds * 60);
        greenDiff = (highLavaColour.getGreen() - baseLavaColour.getGreen()) / (lavaCycleLengthSeconds * 60);
        blueDiff = (highLavaColour.getBlue() - baseLavaColour.getBlue()) / (lavaCycleLengthSeconds * 60);
        opacityDiff = 0.80 / (lavaCycleLengthSeconds * 60); //first value + opacity = highest opacity of a lava overlay
        colourCount = 0;
        opacityCount = 0;
        opacity = 0.10; //lowest opacity of a lava overlay
        max = lavaCycleLengthSeconds * 60;
        val = 0.05;
        this.masterVolume = masterVolume;
    }

    /**
     * Resets variables of this class so a new game can be played without data from the last game carrying forwards.
     */
    public void reset() {
        playedSoundEffects.clear();
        rollingY =  null;
    }

    /**
     * Recurses through the resource directory, adding any .wav or .png files to soundHash and imageHash as AudioClips and Images respectively.
     * Essentially importing all the audio and visual resources for the game.
     * File names form the key for each file.
     * @param directory The file (a folder) in which all the games resources are stored.
     */
    public void importFiles(File directory) {
        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                importFiles(f);
            }
            if (f.isFile()) {
                String ext;
                try {
                    ext = f.getName().substring(f.getName().lastIndexOf(".") + 1);
                } catch (Exception e) {
                    ext = "";
                }
                if (ext.equals("wav")) {
                    String fileToFind = f.getAbsolutePath();
                    f = new File(fileToFind);
                    URI uri = f.toURI();
                    AudioClip soundEffect = new AudioClip(uri.toString());
                    soundHash.put(f.getName(), soundEffect);
                }
                if (ext.equals("png")) {
                    Image image = null;
                    try {
                        image = new Image(f.toURI().toURL().toExternalForm());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    imageHash.put(f.getName(), image);
                }
            }
        }
    }

    /**
     * Used to set and play the background music.
     * @param music the SoundEffect to be played.
     */
    public void playMusic(SoundEffect music) {
        if (this.music != null) {
            this.music.stop();
        }
        this.music = music;
        this.music.setAudio(soundHash);
        this.music.setVolume(masterVolume);
        this.music.play();
    }

    /**
     * Sets the masterVolume value of the Renderer.
     * @param volume the desired masterVolume.
     */
    public void setMusicVolume(double volume) {
        masterVolume = volume;
    }

    /**
     * This method stops any background music that is playing.
     */
    public void stopMusic() {
        if (this.music != null) {
            music.stop();
        }
    }

    /**
     * Takes a GameState and a GraphicsContext and draws the contents of the GameState to the GraphicsContext.
     * Also plays any SoundEffects from the GameState that have not yet been played.
     * @param gc the GraphicsContext to be drawn to.
     * @param state the GameState used to determine what should be drawn.
     */
    public void draw(GraphicsContext gc, GameState state) {
        this.gc = gc;

        //We check all sound effects and play the ones we haven't played yet.
        //This is to ensure that the playing of a sound effect doesn't rely on a single GameState being received successfully.
        for (SoundEffect s : state.getSoundEffects()) {
            if (!playedSoundEffects.contains(s)) {
                s.setAudio(soundHash);
                s.setVolume(masterVolume);
                s.play();
                playedSoundEffects.add(s);
            }
        }


        //draw the background
        GameWorld world = state.getWorld();
        gc.drawImage(world.mapImage, 0, 0);

        //draw the projectiles
        String name;
        for (Projectile p : state.getProjectiles()) {
            name = p.getName();
            projectileImg = imageHash.get(name + "_ingame.png");
            if (name.equals("rocket") || name.equals("drill_bomb")) { //for these two weapons, we change the rotation angle mid-flight.
                projectileRotationDraw(p);
            } else {
                if (p.isAngled()) { //'bullet' type projectiles are launched at a particular angle and stay in that angle.
                    projectileImg = rotateProjectile(p.getFireAngle(), projectileImg);
                }
                draw(projectileImg, p.getXPos(), p.getYPos());
            }
        }

        //draw the robots
        double robotCenterX, robotCenterY, healthPercentage;
        Image robotImage;
        for (Robot r : state.getRobots()) {
            if (r == null || r.isDead())
                continue;
            r.setImage();
            robotImage = imageHash.get(r.getName() + ".png");
            gc.drawImage(robotImage, r.getXPos(), r.getYPos(), (robotImage.getWidth() * universalScale), (robotImage.getHeight() * universalScale));
            robotCenterX = r.getXPos() + (robotImage.getWidth() * universalScale) / 2;
            robotCenterY = r.getYPos() + (robotImage.getHeight() * universalScale) / 2;

            if (state.getCurrentChar() == r) { //drawing the weapon at its current angle
                if (r.getSlot() != lastSlot) { //only need to call rotate if we've changed weapon...
                    lastSlot = r.getSlot();
                    weapon = r.getWeapon();
                    weaponImg = imageHash.get(weapon.getName() + "_rotate.png");
                    rotWeaponImg = rotateWeapon(r.getAngle(), weaponImg);
                }

                if (r.getAngle() != lastAngle || r.isFacingLeft() != facingLeft) { //...or changed angle.
                    lastAngle = r.getAngle();
                    facingLeft = r.isFacingLeft();
                    rotWeaponImg = rotateWeapon(r.getAngle(), imageHash.get(weapon.getName() + "_rotate.png"));
                }

                if (rotWeaponImg != null) { //draw the weapon at the appropriate position
                    if (facingLeft) {
                        gc.drawImage(rotWeaponImg, 0,
                                0,
                                (rotWeaponImg.getWidth()),
                                (rotWeaponImg.getHeight()),
                                robotCenterX + ((rotWeaponImg.getWidth() * universalScale) / 2), robotCenterY - (rotWeaponImg.getHeight() * universalScale / 2),
                                -(rotWeaponImg.getWidth() * universalScale), rotWeaponImg.getHeight() * universalScale);
                    } else {
                        gc.drawImage(rotWeaponImg, robotCenterX - (rotWeaponImg.getWidth() * universalScale / 2),
                                robotCenterY - (rotWeaponImg.getHeight() * universalScale / 2),
                                (rotWeaponImg.getWidth() * universalScale),
                                (rotWeaponImg.getHeight() * universalScale));
                    }
                }
            }
            HUDComponent hc = new HUDComponent(r);
            healthPercentage = (double) r.getHealth() / (double) r.getMaxHealth();
            hc.setPercentage(healthPercentage);
            if (healthPercentage >= 0.67) {
                hc.setHudImage("HUDHighBar");
            } else if (healthPercentage >= 0.33) {
                hc.setHudImage("HUDMediumBar");
            } else {
                hc.setHudImage("HUDLowBar");
            }
            hc.setXPos(r.getXPos());
            hc.setYPos(r.getYPos() - 20);
            name = hc.getHudImageName();
            Image hudImage = imageHash.get(name + ".png");
            hudImage = hc.getHudImage(hudImage);
            gc.drawImage(hudImage, hc.getXPos(), hc.getYPos(), hudImage.getWidth(), hudImage.getHeight());
        }

        //Draw any supply drops
        for (PhysicsObject p : state.getDrops()) {
            name = p.getName();
            Image dropImage = imageHash.get(name + ".png");
//            draw(dropImage, p.getXPos(), p.getYPos());
            gc.drawImage(dropImage, p.getXPos(), p.getYPos(), dropImage.getWidth() * universalScale, dropImage.getHeight() * universalScale);
        }

        //Draw any explosions
        for (Explosion exp : state.getExplosions()) {
            name = exp.getName();
            Image explosionImage = imageHash.get(name + ".png");
            if (explosionImage != null) {
                gc.drawImage(explosionImage, exp.getXPos() - exp.getExplosiveRadius(), exp.getYPos() - exp.getExplosiveRadius(), exp.getExplosiveRadius() * 2, exp.getExplosiveRadius() * 2);
            }
        }

        // Draw any blood spills
        for (Blood blood : state.getBlood()) {
            name = blood.getName();
            Image bloodImage = imageHash.get(name + ".png");
            gc.drawImage(bloodImage, blood.getXPos() - blood.getWidth() / 2,
                    blood.getYPos() - blood.getHeight() / 2, blood.getWidth(),
                    blood.getHeight());
        }

        // Draw the head
        for (Head head : state.getHeads()) {
            name = head.getName();
            Image headImage = imageHash.get(name + ".png");
            gc.drawImage(headImage, head.getXPos(), head.getYPos() + 15, headImage.getWidth() * universalScale, headImage.getHeight() * universalScale);
        }

        //Draw the lava
        if (rollingY == null) {
            rollingY = world.getWorldHeight() - state.STARTING_HEIGHT;
        }
        int actualY = state.getSDArea();
        if (actualY != -1) {
            incrementLavaColour();
            incrementLavaOpacity();

            //rolling Y increments by 1 each step, catching up with the actual lava level which increases suddenly,
            //so as to create a smooth increase in lava level every time it changed.
            if (rollingY > actualY) {
                rollingY--;
                val = 0.0;
            }

            //tide is a small modifier on the lava level, oscillating between 5 and -5, so the lava rises up and down subtly.
            tide = (Math.sin(val) * 5) + rollingY;
            gc.setGlobalAlpha(1);
            gc.drawImage(imageHash.get("lava_1" + ".png"), 0, tide);
            //the second lava image is drawn on top at an opacity ranging from 25% to 50%
            gc.setGlobalAlpha(opacity);
            gc.drawImage(imageHash.get("lava_2" + ".png"), 0, tide);
            gc.setFill(lavaColour);
            //the shifting lava colour is drawn on top at 3/4th opacity
            gc.setGlobalAlpha(0.75);
            gc.fillRect(0, tide, world.getWorldWidth(), 1080 - (tide));
            gc.setStroke(lavaColour);
            gc.setLineWidth(10);
            gc.strokeLine(0, tide, world.getWorldWidth(), tide);
            val = val + 0.025;
            //reset opacity before we draw everything else
            gc.setGlobalAlpha(1);
        }

        //draw all HUD components
        for (HUDComponent hc : state.getHUDParts()) {
            name = hc.getHudImageName();
            Image hudImage = imageHash.get(name + ".png");
            hudImage = hc.getHudImage(hudImage);
            gc.drawImage(hudImage, hc.getXPos(), hc.getYPos(), hudImage.getWidth(), hudImage.getHeight());
        }

    }

    /**
     * Used to draw an image at a specific point, taking into account scaling.
     * @param image the Image to be drawn.
     * @param x the x coordinate that image should be drawn at.
     * @param y the y coordinate that image should be drawn at.
     */
    private void draw(Image image, double x, double y) {
        gc.drawImage(image, x + (image.getWidth() * universalScale) / 2, y + (image.getHeight() * universalScale) / 2, image.getWidth() * universalScale, image.getHeight() * universalScale);
    }

    /**
     * Rotates an image by a given angle and returns it. Used specifically for rotating robot arms holding weapons.
     * Uses an ImageView to perform the rotation. The Image is converted to an ImageView, rotated, and a snapshot of the ImageView is returned.
     * @param angle The angle the Image is to be rotated by.
     * @param armAndWeapon The Image to be rotated.
     * @return The rotated Image.
     */
    private Image rotateWeapon(int angle, Image armAndWeapon) {
        //rotate the given image by the given angle, by converting it to an ImageView, rotating it,
        //and getting a snapshot of the ImageView as an Image we can draw to the graphics context.
        ImageView iv = new ImageView(armAndWeapon);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        if (facingLeft) {
            params.setTransform(new Rotate(Math.abs(angle) - 180, armAndWeapon.getWidth() / 2, armAndWeapon.getWidth() / 2));
        } else {
            params.setTransform(new Rotate(-angle, armAndWeapon.getWidth() / 2, armAndWeapon.getWidth() / 2));
        }
        params.setViewport(new Rectangle2D(0, 0, 1000, 1000));
        return iv.snapshot(params, null);
    }

    /**
     * Rotates an image by a given angle and returns it. Used specifically for rotating projectiles.
     * Uses an ImageView to perform the rotation. The Image is converted to an ImageView, rotated, and a snapshot of the ImageView is returned.
     * @param angle The angle the Image is to be rotated by.
     * @param projectile The Image to be rotated.
     * @return The rotated Image.
     */
    private Image rotateProjectile(int angle, Image projectile) {
        //rotate the given image by the given angle, by converting it to an ImageView, rotating it,
        //and getting a snapshot of the ImageView as an Image we can draw to the graphics context.
        ImageView iv = new ImageView(projectile);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        params.setTransform(new Rotate(-angle, projectile.getWidth() / 2, projectile.getWidth() / 2));
        params.setViewport(new Rectangle2D(0, 0, projectile.getWidth(), projectile.getWidth()));
        return iv.snapshot(params, null);
    }


    /**
     * Called to shift the current lavaColour a little away from one shade and towards another shade.
     * The size of each shift was calculated and stored in redDiff, greenDiff and blueDiff based on the difference between each RGB value and the length of the cycle over which it should change.
     */
    private void incrementLavaColour() {
        colourCount++;
        if (colourCount >= max) {
            blueDiff *= -1;
            greenDiff *= -1;
            redDiff *= -1;
            colourCount = 0;
        }
        lavaColour = new Color(lavaColour.getRed() + redDiff, lavaColour.getGreen() + greenDiff, lavaColour.getBlue() + blueDiff, 1.0);
    }

    /**
     * Called to shift the current lavaColour a little away from one shade and towards another shade.
     * The size of each shift was calculated and stored in opacityDiff based on the desired opacity variance and the length of the cycle over which it should change.
     */
    private void incrementLavaOpacity() {

        opacityCount++;
        if (opacityCount >= (60 * lavaCycleLengthSeconds)) {
            opacityDiff *= -1;
            opacityCount = 0;
        }
        opacity += opacityDiff;
    }

    /**
     * A method used to draw Projectiles that change their angle mid-flight, depending on the value of the counter stored within them.
     * @param p The Projectile to be drawn.
     */
    private void projectileRotationDraw(Projectile p) {
        String name = p.getName();
        Image projectileImage;
        if (!facingLeft) {
            if (p.getCounter() <= 30) {
                projectileImage = rotateProjectile(0, imageHash.get(name.concat("_right_ingame.png")));
                gc.drawImage(projectileImage, p.getXPos(), p.getYPos(),
                        projectileImage.getWidth() * universalScale,
                        projectileImage.getHeight() * universalScale);
            } else if (p.getCounter() > 30 && p.getCounter() <= 60) {
                projectileImage = rotateProjectile(-20, imageHash.get(name.concat("_right_ingame.png")));
                gc.drawImage(projectileImage, p.getXPos(), p.getYPos(),
                        projectileImage.getWidth() * universalScale,
                        projectileImage.getHeight() * universalScale);
            } else if (p.getCounter() > 60 && p.getCounter() <= 90) {
                projectileImage = rotateProjectile(-40, imageHash.get(name.concat("_right_ingame.png")));
                gc.drawImage(projectileImage, p.getXPos(), p.getYPos(),
                        projectileImage.getWidth() * universalScale,
                        projectileImage.getHeight() * universalScale);
            } else if (p.getCounter() > 90 && p.getCounter() <= 120) {
                projectileImage = rotateProjectile(-60, imageHash.get(name.concat("_right_ingame.png")));
                gc.drawImage(projectileImage, p.getXPos(), p.getYPos(),
                        projectileImage.getWidth() * universalScale,
                        projectileImage.getHeight() * universalScale);
            } else if (p.getCounter() > 120 && p.getCounter() <= 150) {
                projectileImage = rotateProjectile(-80, imageHash.get(name.concat("_right_ingame.png")));
                gc.drawImage(projectileImage, p.getXPos(), p.getYPos(),
                        projectileImage.getWidth() * universalScale,
                        projectileImage.getHeight() * universalScale);
            } else if (p.getCounter() > 150) {
                projectileImage = rotateProjectile(-95, imageHash.get(name.concat("_right_ingame.png")));
                gc.drawImage(projectileImage, p.getXPos(), p.getYPos(),
                        projectileImage.getWidth() * universalScale,
                        projectileImage.getHeight() * universalScale);
            }
        } else {
            if (p.getCounter() <= 30) {
                projectileImage = rotateProjectile(0, imageHash.get(name.concat("_left_ingame.png")));
                gc.drawImage(projectileImage, p.getXPos(), p.getYPos(),
                        projectileImage.getWidth() * universalScale,
                        projectileImage.getHeight() * universalScale);
            } else if (p.getCounter() > 30 && p.getCounter() <= 60) {
                projectileImage = rotateProjectile(20, imageHash.get(name.concat("_left_ingame.png")));
                gc.drawImage(projectileImage, p.getXPos(), p.getYPos(),
                        projectileImage.getWidth() * universalScale,
                        projectileImage.getHeight() * universalScale);
            } else if (p.getCounter() > 60 && p.getCounter() <= 90) {
                projectileImage = rotateProjectile(40, imageHash.get(name.concat("_left_ingame.png")));
                gc.drawImage(projectileImage, p.getXPos(), p.getYPos(),
                        projectileImage.getWidth() * universalScale,
                        projectileImage.getHeight() * universalScale);
            } else if (p.getCounter() > 90 && p.getCounter() <= 120) {
                projectileImage = rotateProjectile(60, imageHash.get(name.concat("_left_ingame.png")));
                gc.drawImage(projectileImage, p.getXPos(), p.getYPos(),
                        projectileImage.getWidth() * universalScale,
                        projectileImage.getHeight() * universalScale);
            } else if (p.getCounter() > 120 && p.getCounter() <= 150) {
                projectileImage = rotateProjectile(80, imageHash.get(name.concat("_left_ingame.png")));
                gc.drawImage(projectileImage, p.getXPos(), p.getYPos(),
                        projectileImage.getWidth() * universalScale,
                        projectileImage.getHeight() * universalScale);
            } else if (p.getCounter() > 150) {
                projectileImage = rotateProjectile(95, imageHash.get(name.concat("_left_ingame.png")));
                gc.drawImage(projectileImage, p.getXPos(), p.getYPos(),
                        projectileImage.getWidth() * universalScale,
                        projectileImage.getHeight() * universalScale);
            }
        }
    }
}