package com.game.state;

import com.game.UI.JavaFXTestingRule;
import javafx.scene.image.PixelReader;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.fail;

public class GameWorldTest {

    @Rule
    public JavaFXTestingRule javafxRule = new JavaFXTestingRule();

    @Test
    public void generationTest() {
        GameWorld world = new GameWorld(false, 1, true);
        assert(world.mapImage != null);
        assert(world.mapImage.getWidth() == world.getWorldWidth());
        assert(world.mapImage.getHeight() == world.getWorldHeight());
        for(int x = 0; x < world.map.length; x++){
            for(int y = 0; y < world.map[0].length; y++){
                assert(world.map[x][y] || ! world.map[x][y]); //that is, every pixel in the map is set to true, or false.
            }
        }
    }

    @Test
    public void seedTest() {
        GameWorld worldOne = new GameWorld(false, 235132451, true);
        GameWorld worldTwo = new GameWorld(false, 235132451, true);
        worldOne.generateTerrain();
        worldTwo.generateTerrain();
        assert(worldOne.mapImage.getWidth() == worldTwo.mapImage.getWidth());
        for(int x = 0; x < worldOne.mapImage.getWidth(); x++){
            for(int y = 0; y < worldOne.mapImage.getHeight(); y++){
                assert(worldOne.map[x][y] == worldTwo.map[x][y]);
            }
        }
    }

    @Test
    public void destructionTest() {
        GameWorld world = new GameWorld(false, 1, true);
        GameWorld worldClone = new GameWorld(false, 1, true);
        int xcentre = 1500;
        int ycentre = 900;
        int radius = 100;
        world.destructTerrain(xcentre, ycentre, radius);
        for(int x = 0; x < world.map.length; x++){
            for(int y = 0; y < world.map[0].length; y++){
                Double distance = Math.sqrt(Math.abs(Math.pow((x - xcentre), 2) + Math.pow((y - ycentre), 2)));
                if(distance <= radius){
                    assert(world.map[x][y] == true);
                }else{
                    assert(world.map[x][y] == worldClone.map[x][y]);
                }
            }
        }
    }

    @Test
    public void destructionEdgeTest() {
        GameWorld world = new GameWorld(false, 1, true);
        GameWorld worldClone = new GameWorld(false, 1, true);
        int xcentre = 1918;
        int ycentre = 1079;
        int radius = 100;
        try{
            world.destructTerrain(xcentre, ycentre, radius);
            world.saveImageToFile();
        }catch(ArrayIndexOutOfBoundsException e){
            fail("destructTerrain should cope with destructions partly off-screen.");
        }
        for(int x = 0; x < world.map.length; x++){
            for(int y = 0; y < world.map[0].length; y++){
                Double distance = Math.sqrt(Math.abs(Math.pow((x - xcentre), 2) + Math.pow((y - ycentre), 2)));
                if(distance <= radius){
                    assert(world.map[x][y] == true);
                }else{
                    assert(world.map[x][y] == worldClone.map[x][y]);
                }
            }
        }
    }

    @Test
    public void destructionEdgeTest2() {
        GameWorld world = new GameWorld(false, 1, true);
        GameWorld worldClone = new GameWorld(false, 1, true);
        int xcentre = 1921;
        int ycentre = 1081;
        int radius = 100;
        try{
            world.destructTerrain(xcentre, ycentre, radius);
        }catch(ArrayIndexOutOfBoundsException e){
            fail("destructTerrain should cope with destructions centered off-screen.");
        }
        for(int x = 0; x < world.map.length; x++){
            for(int y = 0; y < world.map[0].length; y++){
                Double distance = Math.sqrt(Math.abs(Math.pow((x - xcentre), 2) + Math.pow((y - ycentre), 2)));
                if(distance <= radius){
                    assert(world.map[x][y] == true);
                }else{
                    assert(world.map[x][y] == worldClone.map[x][y]);
                }
            }
        }
    }

    @Test
    public void shiftTest() {
        GameWorld world = new GameWorld(false, 1, true);
        GameWorld worldClone = new GameWorld(false, 1, true);
        int xcentre = 1500;
        int ycentre = 900;
        int radius = 100;
        int shiftDistance = 300;
        world.shiftTerrain(xcentre, ycentre, radius, shiftDistance);
        for(int x = 0; x < world.map.length; x++){
            for(int y = 0; y < world.map[0].length; y++){
                Double distance = Math.sqrt(Math.abs(Math.pow((x - xcentre), 2) + Math.pow((y - ycentre), 2)));
                if(distance <= radius){
                    assert(world.map[x][y] == true);
                    assert(world.map[x][y - shiftDistance] == false);
                }else if(y < (ycentre - shiftDistance - radius) || y > (ycentre - shiftDistance + radius)){
                    assert(world.map[x][y] == worldClone.map[x][y]);
                }
            }
        }

    }

    @Test
    public void shiftEdgeTest() {
        GameWorld world = new GameWorld(false, 1, true);
        GameWorld worldClone = new GameWorld(false, 1, true);
        int xcentre = 1918;
        int ycentre = 1079;
        int radius = 100;
        int shiftDistance = 300;
        try{
            world.shiftTerrain(xcentre, ycentre, radius, shiftDistance);
        }catch(ArrayIndexOutOfBoundsException e){
            fail("shiftTerrain should cope with shifts partly off-screen.");
        }
        for(int x = 0; x < world.map.length; x++){
            for(int y = 0; y < world.map[0].length; y++){
                Double distance = Math.sqrt(Math.abs(Math.pow((x - xcentre), 2) + Math.pow((y - ycentre), 2)));
                if(distance <= radius){
                    assert(world.map[x][y] == true);
                }else{
                    assert(world.map[x][y] == worldClone.map[x][y]);
                }
            }
        }
    }
}
