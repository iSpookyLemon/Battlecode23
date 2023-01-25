package maggi3_2;
import battlecode.common.*;

import java.util.*;

public class Launcher extends RobotPlayer {

    public static MapLocation hq;
    public static MapLocation enemyhq;
    //public static boolean camp = rng.nextBoolean();

    Launcher() throws GameActionException {
        hq = getHQLoc();
        moveDirection = me.directionTo(hq).opposite();
    }

    void runLauncher() throws GameActionException {
        // Try to attack someone
        int radius = rc.getType().visionRadiusSquared;
        int attackRadius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        Arrays.sort(enemies, new SortEnemies());
        if (enemies.length > 0) {
            for (RobotInfo enemy : enemies) {
                if (enemy.getType() == RobotType.HEADQUARTERS) {
                    enemyhq = enemy.getLocation();
                }
                if (enemy.getType() == RobotType.CARRIER) {
                    moveTo(enemy.getLocation());
                    if (rc.canAttack(enemy.getLocation())) {
                        rc.setIndicatorString("Attacking");
                        rc.attack(enemy.getLocation());
                    }
                }
                if (enemy.getType() == RobotType.LAUNCHER) {
                    Direction dir = me.directionTo(enemy.getLocation());
                    if (enemy.getLocation().isWithinDistanceSquared(me, attackRadius)) {
                        if (rc.canAttack(enemy.getLocation())) {
                            rc.setIndicatorString("Attacking");
                            rc.attack(enemy.getLocation());
                        }
                        moveTo(dir.opposite());
                    } else {
                        moveTo(dir);
                        if (rc.canAttack(enemy.getLocation())) {
                            rc.setIndicatorString("Attacking");
                            rc.attack(enemy.getLocation());
                        }
                    }
                }
            }
        }

        if (rc.getActionCooldownTurns() < 10) {
            MapInfo[] infos = rc.senseNearbyMapInfos(rc.getType().actionRadiusSquared);
            for (MapInfo info : infos) {
                if (info.hasCloud() && rc.canAttack(info.getMapLocation())) {
                    rc.attack(info.getMapLocation());
                    break;
                }
            }
        }

        // Also try to move randomly.
        if (enemyhq == null) {
            moveRandomly();
        } else {
            if (!me.equals(enemyhq)) {
                pathfind(enemyhq, false);
            }
        }
    }

    class SortEnemies implements Comparator<RobotInfo> {
 
        // Method
        // Sorting in ascending order of roll number
        public int compare(RobotInfo a, RobotInfo b)
        {
            if (a.getType() == RobotType.LAUNCHER && b.getType() != RobotType.LAUNCHER) {
                return -1;
            } else if (a.getType() != RobotType.LAUNCHER && b.getType() == RobotType.LAUNCHER) {
                return 1;
            } else {
                return Integer.compare(a.getLocation().distanceSquaredTo(me), b.getLocation().distanceSquaredTo(me));
            }
        }
    }
}
