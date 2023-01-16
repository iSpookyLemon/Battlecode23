package maggi2_2;
import battlecode.common.*;

public class Launcher extends RobotPlayer {

    public static MapLocation hq;
    public static MapLocation enemyhq;
    //public static boolean camp = rng.nextBoolean();

    Launcher() throws GameActionException {
        hq = getHQLoc();
        moveDirection = rc.getLocation().directionTo(hq);
    }

    void runLauncher() throws GameActionException {
        // Try to attack someone
        int radius = rc.getType().visionRadiusSquared;
        int attackRadius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length > 0) {
            for (RobotInfo enemy : enemies) {
                if (enemy.getLocation().isWithinDistanceSquared(rc.getLocation(), attackRadius)) {
                    rc.setIndicatorString(Integer.toString(rc.getActionCooldownTurns()));
                    if (rc.canAttack(enemy.getLocation())) {
                        rc.setIndicatorString("Attacking");
                        rc.attack(enemy.getLocation());
                    }
                    if (enemy.getType() == RobotType.LAUNCHER) {
                        Direction dir = rc.getLocation().directionTo(enemy.getLocation());
                        moveTo(dir.opposite());
                    }
                }
                if (enemy.getType() == RobotType.HEADQUARTERS) {
                    enemyhq = enemy.getLocation();
                }
                if (enemy.getType() == RobotType.CARRIER) {
                    moveTo(enemy.getLocation());
                }
            }
        }

        // Also try to move randomly.
        if (enemyhq == null) {
            moveRandomly();
        } else {
            if (!rc.getLocation().equals(enemyhq)) {
                pathfind(enemyhq, RobotType.LAUNCHER.visionRadiusSquared);
            }
        }
    }
}
