package maggi2_2;
import battlecode.common.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Carrier extends RobotPlayer {

    public static MapLocation hq;
    public static int resourceThreshold = 20;
    public static int mode = 0;
    // mode 0 = collecting resources
    // mode 1 = back to hq
    // mode 2 = anchor

    Carrier() throws GameActionException {
        hq = getHQLoc();
        moveDirection = rc.getLocation().directionTo(hq);
        tryTakeAnchor();
        WellInfo[] wells = rc.senseNearbyWells();
        if (wells.length > 0) {
            resourceThreshold = 40;
        }
    }

    public static void tryTakeAnchor() throws GameActionException {
        if (rc.canTakeAnchor(hq, Anchor.STANDARD)) {
            rc.takeAnchor(hq, Anchor.STANDARD);
            mode = 2;
        }
    }

    void runCarrier() throws GameActionException {
        if (mode == 0) {
            MapLocation me = rc.getLocation();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    MapLocation wellLocation = new MapLocation(me.x + dx, me.y + dy);
                    if (rc.canCollectResource(wellLocation, -1)) {
                        if (rng.nextBoolean()) {
                            rc.collectResource(wellLocation, -1);
                            rc.setIndicatorString("Collecting, now have, AD:" + 
                                rc.getResourceAmount(ResourceType.ADAMANTIUM) + 
                                " MN: " + rc.getResourceAmount(ResourceType.MANA) + 
                                " EX: " + rc.getResourceAmount(ResourceType.ELIXIR));
                        }
                    }
                }
            }

            WellInfo[] wells = rc.senseNearbyWells();
            if (wells.length > 0) {
                WellInfo well_one = wells[0];
                MapLocation well_loc = well_one.getMapLocation();
                //moveTo(well_one.getMapLocation());
                if (!rc.getLocation().equals(well_loc)) {
                    pathfind(well_loc, RobotType.CARRIER.visionRadiusSquared);
                }
            }

            if (rc.getWeight() >= resourceThreshold) {
                mode = 1;
            }
        } else if (mode == 1) {
            RobotInfo[] robots = rc.senseNearbyRobots(1, rc.getTeam());
            for (RobotInfo robot : robots) {
                if (robot.getType() == RobotType.HEADQUARTERS) {
                    int adAmount = rc.getResourceAmount(ResourceType.ADAMANTIUM);
                    int manaAmount = rc.getResourceAmount(ResourceType.MANA);
                    if (rc.canTransferResource(robot.getLocation(), ResourceType.ADAMANTIUM, adAmount)) {
                        rc.transferResource(robot.getLocation(), ResourceType.ADAMANTIUM, adAmount);
                    }
                    if (rc.canTransferResource(robot.getLocation(), ResourceType.MANA, manaAmount)) {
                        rc.transferResource(robot.getLocation(), ResourceType.MANA, manaAmount);
                    }
                }
            }
            //moveTo(hq);
            pathfind(hq, RobotType.CARRIER.visionRadiusSquared);

            if (rc.getWeight() == 0) {
                mode = 0;
            }
        } else if (mode == 2) {
            // If I have an anchor singularly focus on getting it to the first island I see
            int[] islands = rc.senseNearbyIslands();
            for (int id : islands) {
                if (rc.senseAnchor(id) == null) {
                    MapLocation[] thisIslandLocs = rc.senseNearbyIslandLocations(id);
                    MapLocation islandLocation = thisIslandLocs[0];
                    //rc.setIndicatorString("Moving my anchor towards " + islandLocation);
                    if (!rc.getLocation().equals(islandLocation)) {
                        pathfind(islandLocation, RobotType.CARRIER.visionRadiusSquared);
                    } else if (rc.canPlaceAnchor()) {
                        rc.setIndicatorString("Huzzah, placed anchor!");
                        rc.placeAnchor();
                        mode = 0;
                    }
                }
            }
        }



        // Occasionally try out the carriers attack
        /*
        if (rng.nextInt(20) == 1) {
            RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
            if (enemyRobots.length > 0) {
                if (rc.canAttack(enemyRobots[0].location)) {
                    rc.attack(enemyRobots[0].location);
                }
            }
        }
        */

        // Also try to move randomly.
        moveRandomly();
    }
}
