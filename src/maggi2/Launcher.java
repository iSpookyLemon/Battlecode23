package maggi2;
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
            MapLocation toAttack = enemies[0].location;
            //MapLocation toAttack = rc.getLocation().add(Direction.EAST);

            if (rc.canAttack(toAttack)) {
                rc.setIndicatorString("Attacking");        
                rc.attack(toAttack);
            }
        }

        // Also try to move randomly.
        moveRandomly();
    }
}
