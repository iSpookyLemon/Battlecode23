package maggi2_1;
import battlecode.common.*;

public class Headquarters extends RobotPlayer {

    public static Direction buildDirection = Direction.NORTH;
    public static boolean builtAnchor = true;
    public static int[] adLog = new int[10];
    public static int[] manaLog = new int[10];
    public static int anchorCooldown = 0;
    public static int prevAd = 200;
    public static int prevMana = 200;
    public static int usedAd = 0;
    public static int usedMana = 0;

    Headquarters() throws GameActionException {
        
    }

    void runHeadquarters() throws GameActionException {
        usedAd = 0;
        usedMana = 0;

        if (anchorCooldown > 0) {
            anchorCooldown--;
        }
        // Pick a direction to build in.

        if (turnCount == 1) {
            if (rc.canBuildAnchor(Anchor.STANDARD)) {
                // If we can build an anchor do it!
                rc.buildAnchor(Anchor.STANDARD);
                rc.setIndicatorString("Building anchor! " + rc.getNumAnchors(null));
                usedAd += 100;
                usedMana += 100;
            }
        }
        
        if (anchorCooldown == 0) {
            if (logTotal(adLog) > 40 && logTotal(manaLog) > 40) {
                builtAnchor = false;
            }
        }

        if (!builtAnchor) {
            if (rc.canBuildAnchor(Anchor.STANDARD)) {
                // If we can build an anchor do it!
                rc.buildAnchor(Anchor.STANDARD);
                rc.setIndicatorString("Building anchor! " + rc.getNumAnchors(null));
                builtAnchor = true;
                anchorCooldown = 20;
                usedAd += 100;
                usedMana += 100;
            }
        } else {

            MapLocation buildLoc = rc.getLocation().add(buildDirection);
            if (turnCount % 2 == 0) {
                // Let's try to build a carrier.
                //rc.setIndicatorString("Trying to build a carrier");
                if (rc.canBuildRobot(RobotType.CARRIER, buildLoc)) {
                    rc.buildRobot(RobotType.CARRIER, buildLoc);
                    buildDirection.rotateRight();
                    usedAd += 50;
                }
            } else {
                // Let's try to build a launcher.
                //rc.setIndicatorString("Trying to build a launcher");
                if (rc.canBuildRobot(RobotType.LAUNCHER, buildLoc)) {
                    rc.buildRobot(RobotType.LAUNCHER, buildLoc);
                    buildDirection.rotateRight();
                    usedMana += 60;
                }
            }
        }
        updateLogs();
    }

    public static void updateLogs() {
        for (int i = 1; i < 10; i++) {
            adLog[i - 1] = adLog[i];
            manaLog[i - 1] = manaLog[i];
        }
        int gainedAd = rc.getResourceAmount(ResourceType.ADAMANTIUM) - prevAd + usedAd;
        prevAd = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        adLog[9] = gainedAd;
        int gainedMana = rc.getResourceAmount(ResourceType.MANA) - prevMana + usedMana;
        prevMana = rc.getResourceAmount(ResourceType.MANA);
        manaLog[9] = gainedMana;
        rc.setIndicatorString("Gained Ad: " + gainedAd + "; Gained Mana: " + gainedMana);
    }

    public static int logTotal(int[] log) {
        int total = 0;
        for (int i = 0; i < 9; i++) {
            total += log[i];
        }
        return total;
    }
}
