package maggi1;
import battlecode.common.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Carrier extends RobotPlayer {

    public static MapLocation hq;

    Carrier() throws GameActionException {
        hq = getHQLoc();
        moveDirection = rc.getLocation().directionTo(hq);
        tryTakeAnchor();
    }

    public static void tryTakeAnchor() throws GameActionException {
        if (rc.canTakeAnchor(hq, Anchor.STANDARD)) {
            rc.takeAnchor(hq, Anchor.STANDARD);
        }
    }

    void runCarrier() throws GameActionException {
        if (rc.getAnchor() != null) {
            // If I have an anchor singularly focus on getting it to the first island I see
            int[] islands = rc.senseNearbyIslands();
            Set<MapLocation> islandLocs = new HashSet<>();
            for (int id : islands) {
                if (rc.senseAnchor(id) == null) {
                    MapLocation[] thisIslandLocs = rc.senseNearbyIslandLocations(id);
                    islandLocs.addAll(Arrays.asList(thisIslandLocs));
                }
            }
            if (islandLocs.size() > 0) {
                MapLocation islandLocation = islandLocs.iterator().next();
                rc.setIndicatorString("Moving my anchor towards " + islandLocation);
                while (!rc.getLocation().equals(islandLocation)) {
                    moveTo(islandLocation);
                }
                if (rc.canPlaceAnchor()) {
                    rc.setIndicatorString("Huzzah, placed anchor!");
                    rc.placeAnchor();
                }
            }
        }

        // Try to gather from squares around us.
        if (rc.getWeight() < 20) {
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
                Direction dir = rc.getLocation().directionTo(well_one.getMapLocation());
                moveTo(well_one.getMapLocation());
            }

        } else if (rc.getAnchor() == null ){
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
            moveTo(hq);
        }



        // Occasionally try out the carriers attack
        if (rng.nextInt(20) == 1) {
            RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
            if (enemyRobots.length > 0) {
                if (rc.canAttack(enemyRobots[0].location)) {
                    rc.attack(enemyRobots[0].location);
                }
            }
        }

        // Also try to move randomly.
        moveRandomly();
    }
}
