package maggi3;

import battlecode.common.*;

import java.util.*;

/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public strictfp class RobotPlayer {

    public static class Pair {
        public MapLocation loc;
        public int distance;

        public Pair(MapLocation loc, int distance) {
            this.loc = loc;
            this.distance = distance;
        }
    }

    /**
     * We will use this variable to count the number of turns this robot has been alive.
     * You can use static variables like this to save any information you want. Keep in mind that even though
     * these variables are static, in Battlecode they aren't actually shared between your robots.
     */
    static int turnCount = 0;
    static RobotController rc;
    static MapLocation me;
    static Direction moveDirection;
    /**
     * A random number generator.
     * We will use this RNG to make some random moves. The Random class is provided by the java.util.Random
     * import at the top of this file. Here, we *seed* the RNG with a constant number (6147); this makes sure
     * we get the same sequence of numbers every time this code is run. This is very useful for debugging!
     */
    static final Random rng = new Random();

    /** Array containing all the possible movement directions. */
    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    static HashMap<Direction, Integer> directionToNum = new HashMap<Direction, Integer>();
    

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * It is like the main function for your robot. If this method returns, the robot dies!
     *
     * @param rc  The RobotController object. You use it to perform actions from this robot, and to get
     *            information on its current status. Essentially your portal to interacting with the world.
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        directionToNum.put(Direction.NORTH, 0);
        directionToNum.put(Direction.NORTHEAST, 1);
        directionToNum.put(Direction.EAST, 2);
        directionToNum.put(Direction.SOUTHEAST, 3);
        directionToNum.put(Direction.SOUTH, 4);
        directionToNum.put(Direction.SOUTHWEST, 5);
        directionToNum.put(Direction.WEST, 6);
        directionToNum.put(Direction.NORTHWEST, 7);
        // Hello world! Standard output is very useful for debugging.
        // Everything you say here will be directly viewable in your terminal when you run a match!
        //System.out.println("I'm a " + rc.getType() + " and I just got created! I have health " + rc.getHealth());

        // You can also use indicators to save debug notes in replays.
        //rc.setIndicatorString("Hello world!");

        RobotPlayer.rc = rc;
        me = rc.getLocation();

        Headquarters headquarters = null;
        Carrier carrier = null;
        Launcher launcher = null;

        switch (rc.getType()) {
            case HEADQUARTERS:      headquarters = new Headquarters();  break;
            case CARRIER:      carrier = new Carrier();   break;
            case LAUNCHER:      launcher = new Launcher(); break;
            case BOOSTER: // Examplefuncsplayer doesn't use any of these robot types below.
            case DESTABILIZER: // You might want to give them a try!
            case AMPLIFIER:       break;
        }

        while (true) {
            me = rc.getLocation();
            // This code runs during the entire lifespan of the robot, which is why it is in an infinite
            // loop. If we ever leave this loop and return from run(), the robot dies! At the end of the
            // loop, we call Clock.yield(), signifying that we've done everything we want to do.
            turnCount += 1;  // We have now been alive for one more turn!

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode.
            try {
                // The same run() function is called for every robot on your team, even if they are
                // different types. Here, we separate the control depending on the RobotType, so we can
                // use different strategies on different robots. If you wish, you are free to rewrite
                // this into a different control structure!
                switch (rc.getType()) {
                    case HEADQUARTERS:     headquarters.runHeadquarters();  break;
                    case CARRIER:      carrier.runCarrier();   break;
                    case LAUNCHER: launcher.runLauncher(); break;
                    case BOOSTER: // Examplefuncsplayer doesn't use any of these robot types below.
                    case DESTABILIZER: // You might want to give them a try!
                    case AMPLIFIER:       break;
                }

            } catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } finally {
                // Signify we've done everything we want to do, thereby ending our turn.
                // This will make our code wait until the next turn, and then perform this loop again.
                Clock.yield();
            }
            // End of loop: go back to the top. Clock.yield() has ended, so it's time for another turn!
        }

        // Your code should never reach here (unless it's intentional)! Self-destruction imminent...
    }

    public static int[] senseIslands() {
        return rc.senseNearbyIslands();
    }

    public static void moveTo(MapLocation end) throws GameActionException {
        Direction dir = me.directionTo(end);
        moveTo(dir);
    }

    public static void pathfind(MapLocation end, int visionRadius) throws GameActionException {
        Direction dir = me.directionTo(end);
        int dirNum = directionToNum.get(dir);

        if (!rc.canSenseLocation(end)) {
            /*
            double slope = end.y - rc.getLocation().y / (end.x - rc.getLocation().x);
            int newx = (int) Math.sqrt(visionRadius / (1 + slope * slope));
            int newy = (int) (slope * newx) + rc.getLocation().y;
            if (end.x > rc.getLocation().x) {
                newx = newx + rc.getLocation().x;
            } else {
                newx = -newx + rc.getLocation().x;
            }
            if (end.y > rc.getLocation().y) {
                newy = newy + rc.getLocation().y;
            } else {
                newy = -newy + rc.getLocation().y;
            }
            end = new MapLocation(newx, newy);
            */
            MapLocation next = me.add(dir);
            while (rc.canSenseLocation(next)) {
                end = next;
                next = next.add(dir);
            }
        }
        //rc.setIndicatorDot(end, 255, 0, 0);


        PriorityQueue<Pair> queue = new PriorityQueue<Pair>(new Comparator<Pair>() {
            public int compare(Pair pair1, Pair pair2) {
                return Integer.compare(pair1.distance, pair2.distance);
            }
        });
        HashMap<MapLocation, MapLocation> previous = new HashMap<MapLocation, MapLocation>();
        queue.add(new Pair(me, me.distanceSquaredTo(end)));
        previous.put(me, me);
        
        while (!queue.isEmpty()) {
            Pair current = queue.poll();
            if (current.loc.equals(end)) {
                break;
            }
            for (int i = 0; i < 5; i++) {
                MapLocation newLoc = current.loc.add(directions[(((dirNum - 2 + i) % 8) + 8) % 8]);
                if (!previous.containsKey(newLoc) && rc.canSenseLocation(newLoc)) {
                    previous.put(newLoc, current.loc);
                    MapInfo info = rc.senseMapInfo(newLoc);
                    if (info.isPassable()) {
                        queue.add(new Pair(newLoc, newLoc.distanceSquaredTo(end)));
                        //rc.setIndicatorDot(newLoc, 0, 0, 255);
                    }
                }
            }
        }
        
        MapLocation current = end;
        if (previous.get(current) != null) {
            while (previous.get(current) != me) {
                //rc.setIndicatorLine(current, previous.get(current), 0, 255, 0);
                current = previous.get(current);
            }
            moveTo(current);
            //rc.setIndicatorDot(end, 255, 0, 0);
        } else {
            moveTo(dir);
        }
        /*
        if (rc.canMove(rc.getLocation().directionTo(current))) {
            rc.move(rc.getLocation().directionTo(current));
        }
        */
    }

    public static void moveTo(Direction dir) throws GameActionException {
        if (rc.isMovementReady()) {
            Direction left = dir;
            Direction right = dir;
            for (int i = 0; i < 3; i++) {
                if (rc.canMove(left)) {
                    rc.move(left);
                    me = rc.getLocation();
                }
                if (rc.canMove(right)) {
                    rc.move(right);
                    me = rc.getLocation();
                }
                left = left.rotateLeft();
                right = right.rotateRight();
            }
            if (rc.isMovementReady()) {
                if (rc.canMove(right)) {
                    rc.move(right);
                    me = rc.getLocation();
                }
            }
        }
    }

    public static Direction atWall() throws GameActionException {
        for (int i = 0; i < 8; i += 2) {
            if (!rc.onTheMap(me.add(directions[i]))) {
                return directions[i];
            }
        }
        return null;
    }

    public static void moveRandomly() throws GameActionException {
        Direction wallDirection = atWall();
        if (wallDirection != null) {
            moveDirection = wallDirection.opposite();
        }
        if (turnCount % 10 == 0) {
            int num = rng.nextInt(2);
            if (num == 0) {
                moveDirection = moveDirection.rotateLeft();
            } else {
                moveDirection = moveDirection.rotateRight();
            }
        }
        moveTo(moveDirection);
    }

    public static MapLocation getHQLoc() throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots(8, rc.getTeam());
        int minDistance = Integer.MAX_VALUE;
        MapLocation loc = null;
        for (RobotInfo robot : robots) {
            if (robot.getType() == RobotType.HEADQUARTERS && me.distanceSquaredTo(robot.getLocation()) < minDistance) {
                loc = robot.getLocation();
            }
        }
        return loc;
    }
}
