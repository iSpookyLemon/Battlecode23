package maggi2_1;
import battlecode.common.*;

public class Launcher extends RobotPlayer {

    public static MapLocation hq;

    Launcher() throws GameActionException {
        hq = getHQLoc();
        moveDirection = rc.getLocation().directionTo(hq);
    }

    void runLauncher() throws GameActionException {
        // Try to attack someone
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);
        if (enemies.length > 0) {
            for (RobotInfo enemy : enemies) {
                if (enemy.getType() != RobotType.HEADQUARTERS) {
                    if (rc.canAttack(enemy.getLocation())) {
                        rc.setIndicatorString("Attacking");
                        rc.attack(enemy.getLocation());
                    }
                    if (enemy.getType() == RobotType.CARRIER) {
                        moveTo(enemy.getLocation());
                    }
                    if (enemy.getType() == RobotType.LAUNCHER) {
                        Direction dir = rc.getLocation().directionTo(enemy.getLocation());
                        moveTo(dir.opposite());
                    }
                }
            }
        }

        // Also try to move randomly.
        moveRandomly();
    }
}
