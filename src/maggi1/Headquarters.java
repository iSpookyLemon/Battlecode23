package maggi1;
import battlecode.common.*;

public class Headquarters extends RobotPlayer {

    public static Direction buildDirection = Direction.NORTH;


    Headquarters() throws GameActionException {
        
    }

    void runHeadquarters() throws GameActionException {
        // Pick a direction to build in.

        if (rc.canBuildAnchor(Anchor.STANDARD)) {
            // If we can build an anchor do it!
            rc.buildAnchor(Anchor.STANDARD);
            rc.setIndicatorString("Building anchor! " + rc.getNumAnchors(null));
        }

        MapLocation buildLoc = rc.getLocation().add(buildDirection);
        if (rng.nextBoolean()) {
            // Let's try to build a carrier.
            rc.setIndicatorString("Trying to build a carrier");
            if (rc.canBuildRobot(RobotType.CARRIER, buildLoc)) {
                rc.buildRobot(RobotType.CARRIER, buildLoc);
                buildDirection.rotateRight();
            }
        } else {
            // Let's try to build a launcher.
            rc.setIndicatorString("Trying to build a launcher");
            if (rc.canBuildRobot(RobotType.LAUNCHER, buildLoc)) {
                rc.buildRobot(RobotType.LAUNCHER, buildLoc);
                buildDirection.rotateRight();
            }
        }
    }
}
