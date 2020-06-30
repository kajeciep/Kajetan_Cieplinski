package com.game.state;

import com.game.AI.AI;
import com.game.AI.Plan;
import com.game.UI.HUDComponent;
import com.game.UI.InGameHUD;
import com.game.object.*;
import com.game.physics.Coord;
import com.game.physics.PhysicsObject;
import javafx.scene.input.KeyCode;
import com.game.physics.Robot;
import com.game.sound.SoundEffect;
import com.game.weapon.Weapon;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;

/**
 * The current state for the running game.
 * <p>
 * This is where all the rules and logical aspects of the game are
 * checked/managed. This stores the world, and also handles physics for the
 * game.
 * </p>
 */
public class GameState {
    public static final int noTeams = 2;
    public static final int teamSize = 3;
    public static final int MAX_VELOCITY = 15;
    public static double gravity = 9.81;

    private ArrayList<Robot> robots;
    private ArrayList<Projectile> projectiles;
    private ArrayList<SoundEffect> soundEffects;
    private GameWorld world;
    private ArrayList<Team> teams;
    private ArrayList<SupplyDrop> drops;
    private ArrayList<Explosion> explosions;
    private ArrayList<Blood> blood;
    private ArrayList<Head> heads;
    private ArrayList<HUDComponent> hudComponents;
    private Loadout[] loadouts;

    public int whosTurn;
    private Robot currentCharacter;
    public boolean endedTurn;
    public boolean dropCreated;
    public int gameResult;

    private InGameHUD igh;

    public int turnNumber = 0;

    private final int SUDDEN_DEATH_TURN = 20;
    private final int RISE_PER_TURN = 50;
    public final int STARTING_HEIGHT = 100;
    protected int lavaHeight;

    private boolean onClient;

    private boolean singlePlayer;
    private AI computer;
    private Plan plan;
    private Coord oldPos;
    private final int MAX_STUCK = 10;
    private int stuckFor;
    private int difficulty = 0;

    /**
     * The constructor for GameState. Requires some flags and the robot
     * loadouts.
     *
     * @param singlePlayer boolean for if the game is in single player mode.
     * @param onClient     boolean for if the game is in multiplayer mode.
     * @param loadouts     the Loadout for each Robot in the game.
     */
    public GameState(boolean singlePlayer, boolean onClient, Loadout[] loadouts) {
        gravity = 9.81;
        this.loadouts = loadouts;
        gameResult = -1;
        this.singlePlayer = singlePlayer;
        this.onClient = onClient;
        if (singlePlayer) {
            world = new GameWorld(false, System.currentTimeMillis(), onClient);
            lavaHeight = world.getWorldHeight() - STARTING_HEIGHT;
        }
        robots = new ArrayList<Robot>();
        soundEffects = new ArrayList<SoundEffect>();
        projectiles = new ArrayList<Projectile>();
        drops = new ArrayList<SupplyDrop>();
        explosions = new ArrayList<Explosion>();
        blood = new ArrayList<Blood>();
        heads = new ArrayList<Head>();
        if (onClient)
            hudComponents = new ArrayList<HUDComponent>();
    }

    /**
     * Initialises the GameState.
     * <p>
     * Initialises the team turn count - starting with team 1. Creates the team
     * - with the expected Loadout for each Robot. Each Robot spawns in a random
     * location that is not too close to another Robot. Initialises the AI for
     * singleplayer, who will control the second team.
     * </p>
     */
    public void init() {
        if (singlePlayer) {
            whosTurn = 1;
        }
        teams = new ArrayList<>();
        ArrayList<Integer> existingSpawns = new ArrayList();
        // Generates the teams
        for (int t = 0; t < noTeams; t++) {
            ArrayList<Robot> members = new ArrayList<>();
            Random ran = new Random();
            for (int c = 0; c < teamSize; c++) {
                // Randomise spawn positions
                int position = ran.nextInt(world.getWorldWidth() - 50);

                // checking the new spawn isn't too close to an existing one
                boolean goodSpawn = false;
                while (!goodSpawn) {
                    goodSpawn = true;
                    position = ran.nextInt(world.getWorldWidth() - 50);
                    for (Integer spawn : existingSpawns) {
                        if (Math.abs(spawn - position) < (100)) {
                            goodSpawn = false;
                        }
                    }
                }
                existingSpawns.add(position);
                Robot r;
                if (t == 0) {
                    r = new Robot(50, 23, new Coord(position, 0), loadouts[c], (t * 10 + c));
                } else if (!singlePlayer) {
                    r = new Robot(50, 23, new Coord(position, 0), loadouts[c], (t * 10 + c));
                } else {
                    r = new Robot(50, 23, new Coord(position, 0), new Loadout(difficulty), (t * 10 + c));
                }
                int y = 0;
                while (!isOnGround(r)) {
                    y += 5;
                    r.setYPos(y);
                }
                r.setYPos(y - 5);
                members.add(r);
                robots.add(r);
            }
            Team team = new Team(members);
            teams.add(team);
        }
        if (onClient) {
            igh = new InGameHUD(robots);
            hudComponents = igh.getHud();
        }

        if (singlePlayer) {

            // Code for AI to play. Needs to be changed for multiplayer

            endedTurn = true;
            currentCharacter = teams.get(whosTurn).getNextPlayer();
            this.endTurn();
            endedTurn = false;
            // singlePlayer = true;
            Robot[] enemy = new Robot[3];
            teams.get(0).getRobots().toArray(enemy);
            Robot[] myTeam = new Robot[3];
            teams.get(1).getRobots().toArray(myTeam);
            computer = new AI(enemy, myTeam, 0, this);
        } else {
            currentCharacter = robots.get(0);
        }
    }

    /**
     * Initialises the GameState with any extra attributes for the multiplayer
     * only.
     * <p>
     * Checks if the game host has random gravity on. Sets up the team of the
     * opposing player.
     * </p>
     *
     * @param randomGravity boolean saying whether the game has random gravity.
     */
    public void initOnline(boolean randomGravity) {
        setRandomGravity(randomGravity);
        teams = new ArrayList<>();
        for (int t = 0; t < noTeams; t++) {
            ArrayList<Robot> members = new ArrayList<>();
            for (int c = 0; c < teamSize; c++) {
                members.add(robots.get(t * teamSize + c));
            }
            Team team = new Team(members);
            teams.add(team);
        }
        currentCharacter = robots.get(0);
        igh = new InGameHUD(robots);
        hudComponents = igh.getHud();
    }

    /**
     * Generate the GameWorld for the game.
     * <p>
     * Generate the GameWorld used in the current game. Takes a seed so that
     * random generation can be the same over multiplayer (each client generates
     * the same GameWorld).
     * </p>
     *
     * @param seed a seed for random generation.
     */
    public void createGameWorld(long seed) {

        world = new GameWorld(false, seed, onClient);
        lavaHeight = world.getWorldHeight() - STARTING_HEIGHT;
    }

    /**
     * <p>
     * This method advances time forward in the physics simulation. It checks to
     * see if any objects have collided, and handles the collision if they have.
     * </p>
     *
     * @param timePassed The amount of time that the physics simulation should move
     *                   forward by.
     */
    public void step(double timePassed) {
        int index = 0;
        String name;
        int length = projectiles.size();

        if (currentCharacter.getHealth() < 1) {
            endedTurn = true;
            endTurn();
        }

        checkLavaDeaths();

        if (currentCharacter.isDead()) {
            endedTurn = true;
            endTurn();
        }

        if ((!onClient || singlePlayer) && !isOnGround(currentCharacter) && !currentCharacter.getFalling()) {
            for (Robot r : robots) {
                r.setYPos(r.getYPos() + 4);
                r.setFalling(true);
            }
        }
        while (index < length) {
            if (length > 0) {
                projectiles.get(index).incrementCounter();
            }

            projectiles.get(index).moveObject(timePassed);
            projectileCollisions(projectiles.get(index));

            index++;
            length = projectiles.size();
        }

        for (SupplyDrop drop : drops) {
            drop.moveObject(timePassed);
            if (isColliding(drop, drop.getXPos(), drop.getYPos())) {
                soundEffects.add(drop.getImpactSound());
                drop.stopY();
                drop.setFalling(false);
            }
        }
        for (Head aHead : heads) {
            aHead.moveObject(timePassed);
            if (isColliding(aHead, aHead.getXPos(), aHead.getYPos())) {
                aHead.stopY();
                aHead.setFalling(false);
            }
        }
        try {
            if ((!onClient || singlePlayer))
                for (Robot robot : robots) {
                    checkCollisions(robot, timePassed);
                }
        } catch (ConcurrentModificationException c) {
        }
    }

    /**
     * Update the HUD components of the game.
     * <p>
     * Retrieves all the HUD components and updates them to reflect the current
     * character.
     * </p>
     */
    public void updateGameHUD() {
        igh.updateHUD(currentCharacter);
        hudComponents = igh.getHud();
    }

    /**
     * Update the health bars for each Robot.
     * <p>
     * Retrieve all health bars displayed on screen and update them with the new
     * HP for each Robot.
     * </p>
     */
    public void updateRobotsHealthBars() {
        igh.updateHealthBars();
        hudComponents = igh.getHud();
    }

    /**
     * Get the HUD components.
     *
     * @return all HUD components currently displayed on screen in an ArrayList.
     */
    public ArrayList<HUDComponent> getHUDParts() {
        return hudComponents;
    }

    /**
     * <p>
     * This creates an explosion at the given position and checks to see if any
     * objects fall within it's radius. If they do, it damages them and launches
     * them based on how close they are to the centre of the explosion.
     * </p>
     *
     * @param p The projectile that is exploding.
     */
    public void explosion(Projectile p) {
        Coord centre = p.getCentre();
        double xPos = centre.getX();
        double yPos = centre.getY();
        // ////System.out.println(xPos + "," + yPos + "," + p.getXPos() + "," +
        // p.getYPos());
        double radius = p.getExplosiveRadius();
        // ////System.out.println("radius is " + radius);
        // moved these out of the loop so destructTerrain can use them,
        // shouldn't be an issue hopefully?

        for (Robot obj : robots) {
            double objX = obj.getXPos();
            double objY = obj.getYPos();

            // Check all 4 corners of the object
            double minX = obj.getXPos();
            double maxX = minX + obj.getWidth();
            double minY = obj.getYPos();
            double maxY = minY + obj.getHeight();

            double dist1 = Math.sqrt(Math.pow((xPos - minX), 2) + Math.pow((yPos - minY), 2));
            double dist2 = Math.sqrt(Math.pow((xPos - minX), 2) + Math.pow((yPos - maxY), 2));
            double dist3 = Math.sqrt(Math.pow((xPos - maxX), 2) + Math.pow((yPos - minY), 2));
            double dist4 = Math.sqrt(Math.pow((xPos - maxX), 2) + Math.pow((yPos - maxY), 2));
            // Finds the smallest distance
            double distance = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
            if (distance < radius) {
                double damFraction = 1 - (distance / radius);
                obj.takeDamage((int) (damFraction * p.getDamage()));
                if (onClient)
                    updateGameHUD();
                double angle = Math.tan((xPos - obj.getXPos()) / (yPos - obj.getYPos()));
                double dy = Math.abs(objY - yPos);
                double dx = Math.abs(objX - xPos);
                if (xPos > objX && yPos > objY) {
                    angle = 90 + Math.toDegrees(Math.atan(dx / dy));
                } else if (xPos > objX && yPos < objY) {
                    angle = 180 + Math.toDegrees(Math.atan(dy / dx));
                } else if (xPos < objX && yPos < objY) {
                    angle = 270 + Math.toDegrees(Math.atan(dx / dy));
                } else {
                    angle = Math.toDegrees(Math.atan(dy / dx));
                }
                // ////System.out.println("damage fraction is " + damFraction);
                // ////System.out.println("ANGLE IS " + angle);
                obj.launch(angle, 200 * damFraction);
            }
        }

        int x = centre.getX() - 50;
        int y = centre.getY();
        Coord location = new Coord(x, y);
        Explosion explosion = new Explosion(centre, (int) radius);
        explosions.add(explosion);

        if (singlePlayer || !onClient) {
            world.destructTerrain((int) xPos, (int) yPos, (int) radius);
        }
        // do some damage

        soundEffects.add(p.getImpactSound());
    }

    /**
     * A method for shifting Robots a fixed distance upward.
     * <p>
     * This method is used to shift any Robot within a radius from a point
     * upwards a given distance. This is only used when a GravGrenade explosion
     * occurs.
     * </p>
     *
     * @param xPos     the X of the centre of the explosion.
     * @param yPos     the Y of the centre of the explosion.
     * @param radius   the radius of the explosion.
     * @param distance the distance for which each Robot is shifted upward.
     */
    public void shiftRobots(int xPos, int yPos, int radius, int distance) {
        Coord c = new Coord(xPos, yPos);
        for (Robot r : robots) {
            if (c.getDistance(r.getCentre()) < radius + 20) {
                r.setYPos(r.getYPos() - distance);
            }
        }
    }

    /**
     * Checks to see if the given Robot is colliding with the GameWorld or a
     * SupplyDrop and moves the Robot.
     * <p>
     * Check to see if the given Robot is currently colliding with the GameWorld
     * or a SupplyDrop. If they are, they are moved in the correct direction to
     * end the collision and the appropriate collision event takes place.
     * </p>
     *
     * @param obj  The Robot that is being checked.
     * @param time the time passed which determines how far the Robot will be
     *             moved.
     */
    public void checkCollisions(Robot obj, double time) {
        Rectangle collisionBox = obj.getCollisionBox();
        Coord location = new Coord((int) obj.getXPos(), (int) obj.getYPos());
        int maxX = (int) (location.getX() + collisionBox.getWidth());
        int maxY = (int) (location.getY() + collisionBox.getHeight());
        // Move the robots
        obj.stepX(world, time);
        obj.stepY(world, time);
        obj.friction();
        // Move back onto the map
        if (location.getX() < 0) {
            obj.setXPos(0);
        } else if (maxX > world.worldWidth) {
            obj.setXPos(world.worldWidth - 1 - collisionBox.getWidth());
        }
        // Checks to see if colliding with supply box
        int index = 0;
        int length = drops.size();
        while (index < length) {
            SupplyDrop drop = drops.get(index);
            if (collisionBox.intersects(drop.getCollisionBox().getBoundsInParent())) {
                drops.remove(drop);
                obj.pickupSupplies();
                length = drops.size();
                soundEffects.add(drop.getCollectSound());
            }
            index++;
        }

        if (onClient)
            updateGameHUD();
        if (obj.isDead()) {

            for (Team team : teams) {
                team.removeRobot(obj);
            }
            if (obj.getRobotId() == currentCharacter.getRobotId()) {
                endedTurn = true;
                endTurn();
            }
        }
        // robots.remove(obj);
        // }
        // Moves back to ground if moving down slope
        for (int i = 0; i < 10; i++) {
            if (!obj.getFalling() && isOnGround(obj)) {
                return;
            }
            obj.setYPos(obj.getYPos() + 1);
        }
        obj.setFalling(true);
        obj.setYPos(obj.getYPos() - 10);
    }

    /**
     * Checks to see if the given Projectile is colliding with a Robot or with
     * the GameWorld.
     * <p>
     * Checks to see if the given Projectile has collided with a Robot or the
     * GameWorld. If a collision has occurred, then the appropriate collision
     * event takes place.
     * </p>
     *
     * @param p the Projectile which will be checked.
     */
    public void projectileCollisions(Projectile p) {
        Rectangle pBox = p.getCollisionBox();

        int index = 0;
        int length = robots.size();
        // Check all robots
        while (index < length) {
            Robot r = robots.get(index);
            // Ignore the robot that fired the projectile
            if (r == p.getCreator()) {
                index++;
                continue;
            }
            Rectangle rBox = r.getCollisionBox();
            if (pBox.intersects(rBox.getX(), rBox.getY(), rBox.getWidth(), rBox.getHeight())) {
                // Deal damage if it's a bullet
                if (p.isBullet()) {
                    r.takeDamage(p.getDamage());
                } else {
                    r.takeDamage(p.getDamage() * (int) 0.25);
                }
                if (onClient)
                    updateGameHUD();
                p.collided(this);
                this.explosion(p);
                projectiles.remove(p);
                if (r.isDead()) {
                    for (Team team : teams) {
                        team.removeRobot(r);
                    }
                    if (r.getRobotId() == currentCharacter.getRobotId()) {
                        endedTurn = true;
                        endTurn();
                    }
                }
            }
            length = robots.size();
            index++;
        }

        index = 0;
        length = drops.size();
        while (index < length) {
            SupplyDrop s = drops.get(index);
            Rectangle sBox = s.getCollisionBox();
            if (pBox.intersects(sBox.getX(), sBox.getY(), sBox.getWidth(), sBox.getHeight())) {
                // Deal damage
                this.explosion(p);
                p.collided(this);
                projectiles.remove(p);
                drops.remove(s);
            }
            length = drops.size();
            index++;
        }

        Coord location = new Coord((int) p.getXPos(), (int) p.getYPos());
        int maxX = (int) (location.getX() + pBox.getWidth());
        int maxY = (int) (location.getY() + pBox.getHeight());
        for (int i = location.getX(); i < maxX; i++) {
            for (int j = location.getY(); j < maxY; j++) {
                if (i < 0 || i > world.worldWidth - 1 || j < 0 || j > world.worldHeight - 1) {
                    projectiles.remove(p);
                    return;
                } else if (!world.map[i][j] && p.collided(this)) {
                    // ////System.out.println("Collided with the world");
                    this.explosion(p);
                    projectiles.remove(p);
                    return;
                }
            }
        }
    }

    /**
     * Check if the given Projectile is colliding with the GameWorld or is
     * attempting to leave the GameWorld.
     * <p>
     * Check to see if the given Projectile is currently colliding with the
     * GameWorld. If it is then returns true. Will also return true if the
     * Projectile is attempting to leave the edges of the GameWorld.
     * </p>
     *
     * @param p the object to check.
     * @return true if the object is colliding with the level.
     */
    public boolean isColliding(PhysicsObject p, double simX, double simY) {
        Rectangle pBox = p.getCollisionBox();

        int maxX = (int) (simX + pBox.getWidth());
        int maxY = (int) (simY + pBox.getHeight());
        for (int i = (int) simX; i < maxX; i++) {
            for (int j = (int) simY; j < maxY; j++) {
                if (i < 0 || i > 1919 || j < 0 || j > 1079) {
                    // ////System.out.println("OUT OF BOUNDS");
                    return true;
                }
                if (!world.map[i][j]) {
                    // ////System.out.println("TERRAIN HERE");
                    // ////System.out.println(i + " " + j);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Adds a Projectile to the world.
     * <p>
     * Pass a projectile to the GameState. This is usually called when a Weapon
     * is fired, but also when a SupplyDrop spawns.
     * </p>
     *
     * @param projectile The Projectile object that should be added.
     */
    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
        this.addSoundEffect(projectile.getFireSound());
    }

    // /**
    // * Calculate how much fall damage a character should take
    // *
    // * @param
    // * The character who should take damage
    // * @return The damage taken
    // */
    // public double calcFallDamage(Robot robot) {
    // double damage = Math.abs(robot.getYVelocity()) / 15;
    // if(damage > 0)
    // if (damage > 13) {
    // return damage;
    // }
    // return 0;
    // }

    /**
     * Get the current character.
     * <p>
     * Request the current Robot which is being controlled. This Robot could
     * either be player controlled or AI controlled.
     * </p>
     *
     * @return the Robot which is currently taking its turn.
     */
    public Robot getCurrentChar() {
        return currentCharacter;
    }

    /**
     * Fire a Weapon from the current player.
     * <p>
     * Fires a Weapon from the current Robot. Returns a Projectile that
     * corresponds to that Weapon. Checks that the it is the given players turn,
     * the Weapon has ammo, the turn has not ended and that the Robot firing is
     * not falling.
     * </p>
     *
     * @param player that is firing.
     * @return the Projectile that is created by the Weapon fired.
     */
    public Projectile fireWeapon(int player) {
        Weapon weapon = currentCharacter.getWeapon();
        if (player != whosTurn || endedTurn || weapon.getAmmo() < 1 || currentCharacter.getFalling()) {
            return null;
        }
        endedTurn = true;
        weapon.fired();
        double xPos = (currentCharacter.getXPos() + (currentCharacter.getWidth() / 4));
        double yPos = (currentCharacter.getYPos() + (currentCharacter.getHeight() / 4));
        // if(!currentCharacter.isFacingLeft()){
        // xPos = (currentCharacter.getXPos() + (currentCharacter.width / 2));
        // }
        int angle = currentCharacter.getAngle();
        int power = currentCharacter.getPower();
        Projectile p = weapon.createProjectile(new Coord((int) xPos, (int) yPos), angle);
        p.setCreator(currentCharacter);
        p.launch(angle, power * p.getMax() / 200);
        // ////System.out.println(power );
        this.addProjectile(p);

        return p;
    }

    /**
     * The same as fireWeapon but used on multiplayer.
     * <p>
     * Fires a Weapon from the current character. Only called when the server
     * tells it to so there is no conflict between current Robot.
     * </p>
     *
     * @return the Projectile that is created by te Weapon fired.
     */
    public Projectile onlineFireWeapon() {
        Weapon weapon = currentCharacter.getWeapon();
        weapon.fired();
        double xPos = (currentCharacter.getXPos() + (currentCharacter.getWidth() / 4));
        double yPos = (currentCharacter.getYPos() + (currentCharacter.getHeight() / 4));

        int angle = currentCharacter.getAngle();
        int power = currentCharacter.getPower();

        Projectile p = weapon.createProjectile(new Coord((int) xPos, (int) yPos), angle);
        p.setCreator(currentCharacter);
        p.launch(angle, power * p.getMax() / 200);
        // ////System.out.println(power );
        this.addProjectile(p);
        return p;
    }

    public void onlineCheckDrops() {
        Rectangle collisionBox = currentCharacter.getCollisionBox();
        int index = 0;
        int length = drops.size();
        while (index < length) {
            SupplyDrop drop = drops.get(index);
            if (collisionBox.intersects(drop.getCollisionBox().getBoundsInParent())) {
                drops.remove(drop);
                length = drops.size();
                soundEffects.add(drop.getCollectSound());
            }
            index++;
        }
    }

    /**
     * Ends the current turn.
     * <p>
     * Ends the current turn. This may spawn a SupplyDrop, it will increment the
     * turn count (if lava needs to rise). And sets the currentCharacter to the
     * next Robot on the next Team. It also checks to see if any Robot is dead.
     * </p>
     *
     * @param dropCreated    whether or not a drop is to be spawned this turn.
     * @param supplyDropXPos the xPosition of the SupplyDrop that will be created.
     * @param turnNumber
     * @param whosTurn
     */
    public void endTurn(boolean dropCreated, int supplyDropXPos, int turnNumber, int whosTurn) {
        if (dropCreated) {
            SupplyDrop supplyDrop = new SupplyDrop(new Coord(supplyDropXPos, 0));
            drops.add(supplyDrop);
            soundEffects.add(supplyDrop.getFireSound());
        }

        this.whosTurn = whosTurn;

        // update the lava height
        this.turnNumber = turnNumber;
        if (turnNumber > SUDDEN_DEATH_TURN) {
            lavaHeight = lavaHeight - RISE_PER_TURN;
        }
        // check for deaths due to lava
        checkLavaDeaths();

        // Sets the next character to move
        currentCharacter.endTurn();

        checkDeaths();
        currentCharacter = teams.get(whosTurn).getNextPlayer();
        currentCharacter.startTurn();

    }

    /**
     * Checks if the turn has ended.
     * <p>
     * Checks to see if the turn has ended. This can only happen when all
     * movement has stopped.
     * </p>
     *
     * @return true when the turn has ended successfully.
     */
    public boolean endTurn() {
        // Checks for movement
        if (endedTurn && !isMovement()) {
            endedTurn = false;
            whosTurn++;
            if (whosTurn >= noTeams) {
                whosTurn = 0;
            }

            Random r = new Random();
            if (r.nextInt(5) == 1) {
                Coord location = new Coord(r.nextInt(world.worldWidth - 50), 0);
                SupplyDrop drop = new SupplyDrop(location);
                drops.add(drop);
                dropCreated = true;
                soundEffects.add(drop.getFireSound());
            } else {
                dropCreated = false;
            }
            // Gives the next player control

            // Kills all robots in the range
            checkLavaDeaths();
            currentCharacter.endTurn();

            checkDeaths();

            // update the lava height
            turnNumber++;
            if (turnNumber > SUDDEN_DEATH_TURN) {
                lavaHeight = lavaHeight - RISE_PER_TURN;
            }

            if (teams.get(0).hasLost()) {
                if (teams.get(1).hasLost()) {
                    gameResult = 2;
                    return true;
                } else {
                    gameResult = 1;
                    return true;
                }
            } else {
                if (teams.get(1).hasLost()) {
                    gameResult = 0;
                    return true;
                }
            }

            // Sets the next character to move

            currentCharacter = teams.get(whosTurn).getNextPlayer();
            currentCharacter.startTurn();

            // Checks to see if the AI should play
            if (singlePlayer) {
                plan = null;

                stuckFor = 0;
                oldPos = currentCharacter.getPosition();
                if (singlePlayer && whosTurn == 1) {
                    // Creates a plan if it's the AI's turn to play
                    plan = computer.generatePlan(currentCharacter);
                    // executePlan(computer.generatePlan(currentCharacter));
                }

            }
            return true;
        }
        return false;

    }

    /**
     * Checks for any movement in the GameState.
     * <p>
     * Checks each Projectile and Robot to see if any of them are moving.
     * Returns true if they are.
     * </p>
     *
     * @return
     */
    public boolean isMovement() {
        for (Projectile p : projectiles) {
            if (Math.abs(p.getXVelocity()) > 0 || Math.abs(p.getYVelocity()) > 0) {
                return true;
            }
        }
        for (Robot r : robots) {
            if (Math.abs(r.getYVelocity()) > 0 || Math.abs(r.getXVelocity()) > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean projectileExists(Projectile p) {
        return projectiles.contains(p);
    }

    public boolean robotExists(Robot robot) {
        return teams.get(0).getRobots().contains(robot) || teams.get(1).getRobots().contains(robot);
    }

    /**
     * Make the current Robot jump.
     * <p>
     * Responds to a Spacebar input, if the Robot ID given is the current Robot,
     * then it performs a jump. Robot must have enough energy to jump, and will
     * lose energy when it does.
     * </p>
     *
     * @param player that is expected to jump.
     */
    public void jump(int player) {
        // ////System.out.println("TRYING TO JUMP");
        if (!endedTurn && currentCharacter.hasEnergy() && !currentCharacter.getFalling() && player == whosTurn) {
            // ////System.out.println("JUMPED");
            currentCharacter.launch(90, 150);
            currentCharacter.decreaseEnergyJump();
        }
    }

    /**
     * Make the current Robot move left.
     * <p>
     * Responds to an A input. If the Robot ID given is the current Robot, then
     * it performs a movement to the left. Robot must have enough energy and
     * will lose energy when it does.
     * </p>
     *
     * @param player that is expected to move left.
     */
    public void moveLeft(int player) {
        if (!endedTurn && !currentCharacter.getFalling() && player == whosTurn) {
            if (currentCharacter.hasEnergy()) {
                currentCharacter.moveLeft();
                currentCharacter.decreaseEnergy();
                if (onClient)
                    updateGameHUD();
            }
            currentCharacter.swapAngleLeft();
        }
    }

    /**
     * Make the current Robot move right.
     * <p>
     * Responds to a D input. If the Robot ID given is the current Robot, then
     * it performs a movement to the right. Robot must have enough energy and
     * will lose energy when it does.
     * </p>
     *
     * @param player that is expected to move right.
     */
    public void moveRight(int player) {
        if (!endedTurn && !currentCharacter.getFalling() && player == whosTurn) {
            if (currentCharacter.hasEnergy()) {
                currentCharacter.moveRight();
                currentCharacter.decreaseEnergy();
                if (onClient)
                    updateGameHUD();
            }
            currentCharacter.swapAngleRight();
        }
    }

    /**
     * Make the current Robot increase its aim angle.
     * <p>
     * Responds to an Up input. If the Robot ID given is the current Robot, then
     * its aim angle will increase.
     * </p>
     *
     * @param player that is expected to increase its aim angle.
     */
    public void increaseAngle(int player) {
        if (!endedTurn && !currentCharacter.getFalling() && player == whosTurn) {
            currentCharacter.changeAngle(1);
            // ////System.out.println(currentCharacter.getAngle());
        }
    }

    /**
     * Make the current Robot decrease its aim angle.
     * <p>
     * Responds to an Down input. If the Robot ID given is the current Robot,
     * then its aim angle will decrease.
     * </p>
     *
     * @param player that is expected to decrease its aim angle.
     */
    public void decreaseAngle(int player) {
        if (!endedTurn && !currentCharacter.getFalling() && player == whosTurn) {
            currentCharacter.changeAngle(-1);
            // ////System.out.println(currentCharacter.getAngle());
        }
    }

    /**
     * Make the current Robot increase its shot power.
     * <p>
     * Responds to an Right input. If the Robot ID given is the current Robot,
     * then its shot power will increase.
     * </p>
     *
     * @param player that is expected to increase its shot power.
     */
    public void increasePower(int player) {
        if (!endedTurn && !currentCharacter.getFalling() && player == whosTurn) {
            currentCharacter.changePower(1);
            if (onClient)
                updateGameHUD();
            // ////System.out.println(currentCharacter.getPower());
        }
    }

    /**
     * Make the current Robot decrease its shot power.
     * <p>
     * Responds to an Left input. If the Robot ID given is the current Robot,
     * then its shot power will decrease.
     * </p>
     *
     * @param player that is expected to decrease its shot power.
     */
    public void decreasePower(int player) {
        if (!endedTurn && !currentCharacter.getFalling() && player == whosTurn) {
            currentCharacter.changePower(-1);
            if (onClient)
                updateGameHUD();
        }
    }

    /**
     * Set the current Weapon slot for the current Robot.
     * <p>
     * Sets the currently equipped Weapon to the desired Weapon. Checks if the
     * Robot ID is that of the current Robot.
     * </p>
     *
     * @param slot   the slot id of the Weapon to be equipped.
     * @param player the player who is expected to change Weapon.
     */
    public void setCurrentSlot(int slot, int player) {
        if (!endedTurn && player == whosTurn) {
            currentCharacter.setPlayerSlot(slot);
            if (onClient)
                updateGameHUD();
        }
    }

    /**
     * Get all of the robots currently in the world
     *
     * @return The current robots in the world
     */
    public ArrayList<Robot> getRobots() {
        return robots;
    }

    /**
     * Get all of the projectiles that currently exist in the world
     *
     * @return The current projectiles in the world
     */
    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    /**
     * Get all of the supply drops that currently exist in the world
     *
     * @return The current supply drops
     */
    public ArrayList<SupplyDrop> getDrops() {
        return drops;
    }

    /**
     * Get the current terrain of the world
     *
     * @return The games terrain
     */
    public GameWorld getWorld() {
        return world;
    }

    /**
     * Get all of the explosions currently happening in the world
     *
     * @return All of the current explosion
     */
    public ArrayList<Explosion> getExplosions() {
        return explosions;
    }

    /**
     * Get the blood of all the dead robots currently in the world
     *
     * @return The bloood of the dead robots
     */
    public ArrayList<Blood> getBlood() {
        return blood;
    }

    /**
     * Get all of the heads of dead robots currently in the world
     *
     * @return The robot heads
     */
    public ArrayList<Head> getHeads() {
        return heads;
    }

    /**
     * Stops the current character from moving if they aren't falling currently
     */
    public void stop() {
        if (!currentCharacter.getFalling()) {
            currentCharacter.stop();
        }
    }

    /**
     * Get the height from the bottom of the screen that the top of the lava
     * currently is
     *
     * @return The height of the lava currently
     */
    public int getSDArea() {
        return lavaHeight;
    }

    /**
     * Check to see if the AI has been stuck for more than 10 frames and end
     * it's turn if it has. If not then it checks the AI still has energy to
     * move and ends it's turn if it doesn't. If it does then it returns the
     * code for the key press to move the AI in the direction it should go
     *
     * @return The code for the key press to move the AI
     */
    public KeyCode getNextInput() {
        // Check for if stuck
        Coord currentPos = currentCharacter.getPosition();
        if (oldPos.getX() == currentPos.getX() && oldPos.getY() == currentPos.getY()) {
            stuckFor++;
            if (stuckFor > MAX_STUCK) {
                // Stop following plan
                if (currentCharacter.hasEnergy()) {
                    if (plan.getNextCode(currentPos) == KeyCode.A) {
                        return KeyCode.Q;
                    } else {
                        return KeyCode.E;
                    }
                }
                endedTurn = true;
            }
        }
        oldPos = currentPos;
        return plan.getNextCode(currentCharacter.getPosition());
    }

    /**
     * Check if the current plan generated by the AI has any commands as well as
     * if it's the AI's turn to execute them. It always returns false if it's
     * multiplayer
     *
     * @return True if the AI has commands and it's the AI's turn
     */
    public boolean hasCommands() {
        if (!singlePlayer || whosTurn == 0 || endedTurn) {
            return false;
        } else if (plan.hasMoreCommands()) {
            return true;
        }
        executePlan(plan);
        return false;
    }

    /**
     * Checks if the given plan has a fire command and executes it if it does.
     * It then ends the AI's turn
     *
     * @param plan The plan to be executed
     */
    public void executePlan(Plan plan) {
        // Execute fire commands if they exist
        if (plan.hasFireCommand()) {
            // Fire the weapon
            if (plan.getAngle() > 90 && plan.getAngle() < 270) {
                currentCharacter.setFacingLeft(true);
                currentCharacter.moveLeft();
                currentCharacter.swapAngleLeft();
            } else {
                currentCharacter.setFacingLeft(false);
                currentCharacter.moveRight();
                currentCharacter.swapAngleRight();

            }

            currentCharacter.setSlot(plan.getWeaponSlot());
            Weapon weapon = currentCharacter.getWeapon();
            weapon.fired();
            // Create the projectile
            double xPos = currentCharacter.getXPos();
            double yPos = currentCharacter.getYPos();
            int angle = plan.getAngle();
            int power = plan.getVelocity();
            Projectile p = weapon.createProjectile(new Coord((int) xPos, (int) yPos), angle);
            p.setCreator(currentCharacter);
            // Fire the projectile
            p.launch(angle, power);
            this.addProjectile(p);
        }
        // End the turn
        endedTurn = true;
        endTurn();
    }

    /**
     * Checks if the given robot is currently on the terrain
     *
     * @param obj The robot to check
     * @return True if the robot is on the terrain
     */
    public boolean isOnGround(Robot obj) {
        Rectangle collisionBox = obj.getCollisionBox();
        Coord location = new Coord((int) obj.getXPos(), (int) obj.getYPos());
        int maxX = (int) (location.getX() + collisionBox.getWidth());
        int maxY = (int) (location.getY() + collisionBox.getHeight());
        for (int i = location.getX() + 3; i < maxX - 3; i++) {
            if (!world.map[i][maxY + 2]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the array list of sound effects that need to be played
     *
     * @return The sound effects to be played
     */
    public ArrayList<SoundEffect> getSoundEffects() {
        return soundEffects;
    }

    /**
     * Ad a sound effect to the list of sound effects waiting to be played
     *
     * @param soundEffect
     */
    public void addSoundEffect(SoundEffect soundEffect) {
        this.soundEffects.add(soundEffect);
    }

    /**
     * Set the robots currently in the game world
     *
     * @param robots The robots in the world
     */
    public void setRobots(ArrayList<Robot> robots) {
        this.robots = robots;
    }

    /**
     * Set the robot that's the current character being controlled
     *
     * @param currentCharacter The current robot being controlled
     */
    public void setCurrentCharacter(Robot currentCharacter) {
        this.currentCharacter = currentCharacter;
    }

    /**
     * Get the map of the world represented as a seed
     *
     * @return The seed of the world
     */
    public long getSeed() {
        return world.getSeed();
    }

    /**
     * Sets the current difficulty of the AI
     *
     * @param difficulty The difficulty to set the AI to
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Sets the random gravity if it's enabled. It generates a random value for
     * gravity from a predefined range with it being equally likely to be a low
     * or high gravity world
     *
     * @param randomGravity True if random gravity is enabled
     */
    public void setRandomGravity(boolean randomGravity) {
        if (randomGravity) {
            Random r = new Random();
            if (!singlePlayer) {
                r.setSeed(world.getSeed());
            }

            if (r.nextBoolean()) {
                int randomNo = r.nextInt(11) + 5;
                gravity *= randomNo;
            } else {
                int randomNo = r.nextInt(6) + 25;
                gravity *= randomNo;
            }
        } else {
            gravity *= 20;
        }
    }

    /**
     * Sets which players turn it currently is
     *
     * @param turn The value to set the current turn to
     */
    public void setTurn(int turn) {
        whosTurn = turn;
    }

    /**
     * This is used by the AI to move a given position until it's on the surface
     * of the terrain. It finds the closest point to the surface of the terrain
     * at the given point meaning that it can handle things like caves and holes
     * from explosions
     *
     * @param x The x position to find the surface for
     * @param y The current y position that it should start from
     * @return The co-ordinates of the closest surface terrain to this point
     */
    // Moves the y coord for an ai node to the ground
    public Coord getNodeYPos(int x, int y) {
        // If in the ground
        if (!world.map[x][y]) {
            // ////System.out.println("Node is in the ground");
            // Move up until the pixel is air
            while (y > 0 && !world.map[x][y]) {
                // ////System.out.println("CUrrent y " + y);
                y--;
            }
            return new Coord(x, y);
        } else {
            // ////System.out.println("Node is in the air");
            // Move down until the next pixel is ground
            while (y < world.worldHeight && world.map[x][y + 1]) {
                // ////System.out.println("Current y " + y);
                y++;
            }
            return new Coord(x, y);
        }

    }

    /**
     * Checks to see if the lava has killed any robots
     */
    public void checkLavaDeaths() {
        int index = 0;
        int length = robots.size();
        while (index < length) {
            length = robots.size();
            Robot rob = robots.get(index);
            if (rob.getYPos() + (rob.getHeight() * 0.25) >= lavaHeight) {
                rob.takeDamage(1000);
                if (onClient) {
                    updateGameHUD();
                }
                if (rob.isDead()) {
                    for (Team team : teams) {
                        team.removeRobot(rob);
                    }
                }
            }
            index++;
        }
    }

    /**
     * Checks all the robots in the world to see if they have died and performs
     * any required actions if that's the case
     */
    private void checkDeaths() {
        ArrayList<Robot> deadRobots = new ArrayList<>();
        for (Robot r : robots) {
            if (r.isDead())
                deadRobots.add(r);
        }
        for (Robot r : deadRobots) {
            blood.add(new Blood(r.getCentre(), 80));
            heads.add(new Head(r.getCentre(), 50));
            robots.remove(r);
        }
    }

    public void clearExplosions() {
        explosions.clear();
    }
}
