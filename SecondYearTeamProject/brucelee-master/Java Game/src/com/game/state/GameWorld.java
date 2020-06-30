package com.game.state;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

/**
 * A class representing the 'world' or 'map' for a single Solar Legends match.
 * boolean[][] map is a pixel-level representation of the terrain of the world.
 * This class also stores the image used to visualise the GameWorld when the game is rendered.
 * @author Joshua Minton
 */
public class GameWorld {
    public static final int worldHeight = 1080;
    public static final int worldWidth = 1920;
    public static boolean[][] map = new boolean[worldWidth][worldHeight];
    private int[] heightCurve;

    private Color topColourSky;
    private Color bottomColourSky;
    private Color topColourGround;
    private Color bottomColourGround;
    private Color caveColour; //= bottomColourGround.darker();
    private Color starWhite;

    private Image stars;

    private HashMap<Integer, Color> groundColours = new HashMap<>();
    private HashMap<Integer, Color> skyColours = new HashMap<>();
    private Double redDiffGround, redDiffSky, greenDiffGround, greenDiffSky, blueDiffGround, blueDiffSky;
    private WorldType type;

    Random randomiser = new Random();
    private long seed;
    public WritableImage mapImage = null;
    private boolean onClient;

    /**
     * Default constructor for all GameWorlds.
     * Calls methods to generate random terrain, selects a random WorldType, and calculates the colourings for the terrain depending on the WorldType.
     * @param isFlat Whether the world is to be flat; all on the same level. For testing purposes.
     * @param seed The seed to be fed into the randomiser that is used to generate random terrain. This allows us to generate the same random terrain on two different machines.
     * @param onClient Whether or not the method is being called by a Client (rather than a Server)
     */
    public GameWorld(boolean isFlat, long seed, boolean onClient) {
        this.onClient = onClient;
        if(isFlat){
            generateTerrainFlat();
        }else{
            this.seed = seed;
            generateTerrain();
        }

        if (onClient) {
            //find a random world type and set colours accordingly.
            type = randomWorld();
            if (type == WorldType.EARTH) {
                topColourSky = Color.DARKBLUE;
                bottomColourSky = Color.LIGHTBLUE;
                topColourGround = Color.GREEN;
                bottomColourGround = Color.TAN.darker();
            } else if (type == WorldType.MARS) {
                topColourSky = Color.BLACK;
                bottomColourSky = Color.GREY;
                topColourGround = Color.ORANGE;
                bottomColourGround = Color.ORANGE.darker().darker();
            } else if (type == WorldType.MOON) {
                topColourSky = Color.BLACK;
                bottomColourSky = Color.BLACK;
                topColourGround = Color.WHITE;
                bottomColourGround = Color.LIGHTGRAY.darker().darker();
            } else if (type == WorldType.NEPTUNE) {
                topColourSky = Color.DARKSLATEBLUE.darker();
                bottomColourSky = Color.SLATEBLUE;
                topColourGround = Color.LIGHTSTEELBLUE;
                bottomColourGround = Color.STEELBLUE.darker().darker();
            } else if (type == WorldType.TITAN) {
                topColourSky = Color.ORANGE;
                bottomColourSky = Color.LIGHTGOLDENRODYELLOW;
                topColourGround = Color.TAN;
                bottomColourGround = Color.SANDYBROWN;
            }else if (type == WorldType.PLUTO) {
                topColourSky = Color.PURPLE;
                bottomColourSky = Color.MEDIUMPURPLE;
                topColourGround = Color.LIGHTSEAGREEN;
                bottomColourGround = Color.DARKSEAGREEN.darker();
            }else if (type == WorldType.VENUS) {
                topColourSky = Color.DARKRED;
                bottomColourSky = Color.YELLOW;
                topColourGround = Color.DARKBLUE;
                bottomColourGround = Color.DARKBLUE.darker();
            }
            caveColour = bottomColourGround.darker();

            //calculating the amount we must change the rgb values of the top colour to reach the bottom colour after 1080 iterations - essentially making a gradient.
            redDiffGround = (topColourGround.getRed() - bottomColourGround.getRed()) / worldHeight;
            greenDiffGround = (topColourGround.getGreen() - bottomColourGround.getGreen()) / worldHeight;
            blueDiffGround = (topColourGround.getBlue() - bottomColourGround.getBlue()) / worldHeight;

            redDiffSky = (topColourSky.getRed() - bottomColourSky.getRed()) / worldHeight;
            greenDiffSky = (topColourSky.getGreen() - bottomColourSky.getGreen()) / worldHeight;
            blueDiffSky = (topColourSky.getBlue() - bottomColourSky.getBlue()) / worldHeight;

            updateWorldImage();
        }
    }

    /**
     * A method to choose one of the enums in the WorldType class at random.
     * @return The randomly selected WorldType enum.
     */
    public WorldType randomWorld(){
        return WorldType.values()[randomiser.nextInt(WorldType.values().length)];
    }

    /**
     * A method to populate the boolean[][] map with a representation of randomly generated terrain.
     * <p>
     *     We generate 5 random values with which we use the Math.sin function.
     *     This essentially creates a number of different sine waves we can 'layer' over eachother to create a 'heightmap', from which the map[][] can be filled.
     * The result is somewhat realistic-looking terrain.
     * </p>
     *
     * http://www.riemers.net/eng/Tutorials/XNA/Csharp/Series2D/Random_terrain.php
     */
    public void generateTerrain() {
        //the various numbers in this method are designed to create sensible looking terrain in a 1920x1080 array.

        heightCurve = new int[worldWidth];

        randomiser.setSeed(seed); //setting a seed to generate terrain from saves us sending a 1920x1080 boolean array across a network.

        //code re-use starts here

        double rand1 = randomiser.nextDouble() + 1;
        double rand2 = randomiser.nextDouble() + 2;
        double rand3 = randomiser.nextDouble() + 3;
        double rand4 = randomiser.nextDouble() + 4;
        double rand5 = randomiser.nextDouble() + 5;

        float offset = (float) worldHeight - (worldHeight / (float) 2.5);
        float peakheight = (float) (worldHeight * 0.12); //bigger numbers = less of that attribute
        float flatness = (float) (worldWidth * 0.12);

        for (int x = 0; x < worldWidth; x++) {
            double height = peakheight / rand1 * Math.sin((float) x / flatness * rand1 + rand1);
            height += peakheight / rand2 * Math.sin((float) x / flatness * rand2 + rand2);
            height += peakheight / rand3 * Math.sin((float) x / flatness * rand3 + rand3);
            height += peakheight / rand4 * Math.sin((float) x / flatness * rand4 + rand4);
            height += peakheight / rand5 * Math.sin((float) x / flatness * rand5 + rand5);
            height += 5 * Math.cos((float) x / (15));
            height += offset;
            heightCurve[x] = (int) height;
        }

        //code re-use ends here
        //this code was adapted from:
        //http://www.riemers.net/eng/Tutorials/XNA/Csharp/Series2D/Random_terrain.php

        //fill the array using the heightCurve.
        for(int x = 0; x < (worldWidth); x++){
            for(int y = 0; y < (worldHeight); y++){
                if(heightCurve[x] < y){
                    map[x][y] = false;
                }else{
                    map[x][y] = true;
                }
            }
        }
    }


    /**
     * A method to fill the boolean[][] map with terrain all on a single level. Mainly for test purposes.
     */
    public void generateTerrainFlat() {
        heightCurve = new int[worldWidth];

        for (int x = 0; x < worldWidth; x++) {
            heightCurve[x] = 500;
        }

        for(int x = 0; x < (worldWidth); x++){
            for(int y = 0; y < (worldHeight); y++){
                if(heightCurve[x] < y){
                    map[x][y] = false;
                }else{
                    map[x][y] = true;
                }
            }
        }
    }


    /**
     * A method to save the Image representation of the world to an image file.
     * Used for development/testing.
     */
    public void saveImageToFile(){
        WritableImage image = mapImage;
        String path = "map.png";
        File ImageFile = new File(path);
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(renderedImage, "png", ImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * A method to create a circle of 'true' values (representing empty space) around a particular point in the boolean[][] map.
     * Used to change the terrain as explosions happen.
     * The mapImage is also updated to reflect the changes.
     * @param xcentre The x coordinate of the centre of the circle of destruction.
     * @param ycentre The y coordinate of the centre of the circle of destruction.
     * @param radius The radius of the circle of destruction.
     */
    public void destructTerrain(int xcentre, int ycentre, int radius){
        PixelReader preader = null;
        PixelReader starreader = null;
        PixelWriter pwriter = null;
        if (onClient) {
            starreader = stars.getPixelReader();
            preader = mapImage.getPixelReader();
            pwriter = mapImage.getPixelWriter();
        }
        for(int y = -radius; y <= radius; y++){
            for(int x = -radius; x <= radius; x++){
                if(x*x+y*y <= radius*radius){
                    try {map[xcentre + x][ycentre + y] = true; //set that element in the array to true, that is, empty space.
                        //if we're not changing a pixel that is already sky...
                        if((onClient) && preader.getColor((xcentre + x), (ycentre + y)).hashCode() != (skyColours.get(ycentre + y)).hashCode()){
                            //if there should be a star pixel in that position, draw the right coloured star pixel for that y coordinate.
                            if(starreader.getColor(xcentre + x, ycentre + y).hashCode() != 0){
                                pwriter.setColor(xcentre + x, ycentre + y, starWhite.interpolate(skyColours.get(ycentre + y), ((float) (ycentre + y) / (float) (worldHeight/1.5))));
                            }else{
                                //otherwise draw the correct sky colour.
                                pwriter.setColor((xcentre + x), (ycentre + y), skyColours.get(ycentre + y));
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {}
                }
            }
        }
    }

    /**
     * A method to move a circular area of terrain around a certain point a certain distance upwards.
     * @param xcentre The x coordinate of the centre of the area to be shifted.
     * @param ycentre The y coordinate of the centre of the area to be shifted.
     * @param radius The radius of the circle to be shifted.
     * @param distance How far upwards the circle is to be shifted.
     */
    public void shiftTerrain(int xcentre, int ycentre, int radius, int distance){
        PixelReader preader = null;
        PixelReader starreader = null;
        PixelWriter pwriter = null;
        if (onClient) {
            starreader = stars.getPixelReader();
            preader = mapImage.getPixelReader();
            pwriter = mapImage.getPixelWriter();
        }
        for(int y = -radius; y <= radius; y++){
            for(int x = -radius; x <= radius; x++){
                if(x*x+y*y <= radius*radius){
                    try {
                        if(map[xcentre + x][ycentre + y] == false){
                            map[xcentre + x][ycentre + y - distance] = false; //if a pixel is ground, make the pixel the specified distance above it ground also.
                            if (onClient) //if we're on the client, we also change the destination pixel to be the same colour as the source pixel.
                                pwriter.setColor((xcentre + x), (ycentre + y - distance), preader.getColor(xcentre + x, ycentre + y));
                        }
                        map[xcentre + x][ycentre + y] = true; //make the source pixel true, that is, empty space.
                        if(onClient && preader.getColor((xcentre + x), (ycentre + y)).hashCode() == (groundColours.get(ycentre + y)).hashCode()) {
                            //if there should be a star pixel in that position, draw the right coloured star pixel for that y coordinate.
                            if (starreader.getColor(xcentre + x, ycentre + y).hashCode() != 0) {
                                pwriter.setColor(xcentre + x, ycentre + y, starWhite.interpolate(skyColours.get(ycentre + y), ((float) (ycentre + y) / (float) (worldHeight / 1.5))));
                            } else {
                                //otherwise draw the correct sky colour.
                                pwriter.setColor((xcentre + x), (ycentre + y), skyColours.get(ycentre + y));
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {}
                }
            }
        }
    }


    /**
     * Updates the Image mapImage to reflect the current state of the world.
     */
    public void updateWorldImage(){
        stars = new Image("res/images/world/stars.png");
        starWhite = new Color(1, 1, 1, 1);
        WritableImage image = new WritableImage(map.length, map[0].length);
        PixelWriter pwriter = image.getPixelWriter();
        PixelReader preader = stars.getPixelReader();
        for (int y = 0; y < map[0].length; y++) {
            groundColours.put(y, new Color((topColourGround.getRed() - redDiffGround * y), (topColourGround.getGreen() - greenDiffGround * y), (topColourGround.getBlue() - blueDiffGround * y), 1.0));
            skyColours.put(y, new Color((topColourSky.getRed() - redDiffSky * y), (topColourSky.getGreen() - greenDiffSky * y), (topColourSky.getBlue() - blueDiffSky * y), 1.0));
            for (int x = 0; x < map.length; x++) {
                if (!map[x][y]) {
                    pwriter.setColor(x, y, groundColours.get(y));
                } else {
                    if(heightCurve[x] < (y - 7)){
                        pwriter.setColor(x, y, caveColour);
                    } else {
                        if(preader.getColor(x, y).hashCode() != 0){
                            pwriter.setColor(x, y, starWhite.interpolate(skyColours.get(y), ((float) y/ (float) (worldHeight/1.5))));
                        }else{
                            pwriter.setColor(x, y, skyColours.get(y));
                        }

                    }
                }
            }
        }
        mapImage = image;
    }


    /**
     * Returns the worldHeight value.
     * @return the worldHeight.
     */
    public int getWorldHeight() {
        return worldHeight;
    }

    /**
     * Returns the worldWidth value.
     * @return the worldWidth.
     */
    public int getWorldWidth() {
        return worldWidth;
    }

    /**
     * Returns the seed that was used by the Random when generating terrain.
     * @return the seed.
     */
    public long getSeed() {
        return seed;
    }

}
