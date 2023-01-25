package maggi3_2;
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
    public static MapLocation buildLoc;

    Headquarters() throws GameActionException {
        
    }

    void runHeadquarters() throws GameActionException {

        buildLoc = me.add(buildDirection);
        usedAd = 0;
        usedMana = 0;

        if (anchorCooldown > 0) {
            anchorCooldown--;
        }
        if (anchorCooldown == 0) {
            if (logTotal(adLog) > 40 && logTotal(manaLog) > 40) {
                builtAnchor = false;
            }
        }
        // Pick a direction to build in.
        
        /*if (turnCount == 1) {
            if (rc.canBuildAnchor(Anchor.STANDARD)) {
                // If we can build an anchor do it!
                rc.buildAnchor(Anchor.STANDARD);
                rc.setIndicatorString("Building anchor! " + rc.getNumAnchors(null));
                usedAd += 100;
                usedMana += 100;
            }
        } else */if (rc.getNumAnchors(Anchor.STANDARD) > 0) {
            smartBuild(RobotType.CARRIER);
        } else if (!builtAnchor) {
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
            while (rc.getActionCooldownTurns() < 10) {
                if (rc.getResourceAmount(ResourceType.MANA) >= 45) {
                    smartBuild(RobotType.LAUNCHER);
                    continue;
                } else if (rc.getResourceAmount(ResourceType.ADAMANTIUM) >= 50) {
                    smartBuild(RobotType.CARRIER);
                    continue;
                }
                break;
            }
        }

        updateLogs();
        rc.setIndicatorString(buildDirection.toString());
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

    public static void smartBuild(RobotType type) throws GameActionException {
        for (int i = 0; i < 8; i++) {
            if (rc.canBuildRobot(type, buildLoc)) {
                rc.buildRobot(type, buildLoc);
                buildDirection = buildDirection.rotateRight();
                if (type == RobotType.CARRIER) {
                    usedAd += 50;
                } else if (type == RobotType.LAUNCHER) {
                    usedMana += 60;
                }
                break;
            }
            buildDirection = buildDirection.rotateRight();
            buildLoc = me.add(buildDirection);
        }
    }
}
