package jp.jaxa.iss.kibo.rpc.defaultapk;

import jp.jaxa.iss.kibo.logger.Logger;
import jp.jaxa.iss.kibo.pathfind.OptimalPath;
import jp.jaxa.iss.kibo.pathfind.PathFindNode;
import jp.jaxa.iss.kibo.pathfind.TargetPoint;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import java.util.Arrays;
import java.util.List;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    //    @Override
    protected void runPlan1_backup() {
        Astrobee astrobee = new Astrobee(api);
        try {
            astrobee.startMission();

            astrobee.moveToPoint(5);
            astrobee.shootLaser();
            astrobee.attemptScanQRDock(false);

            astrobee.endMission();
        } catch (Exception e) {
            Logger.__log("CRITICAL ERROR");
            Logger.__log(e.getMessage());
            Logger.__log(Arrays.toString(e.getStackTrace()));
            astrobee.__forceEndMission();
        }
    }

    @Override
    protected void runPlan1() {
        Astrobee astrobee = new Astrobee(api);
        boolean shouldConsiderGoal = false;
        boolean isGoingToGoal = false;
        // PathFindNode QRNode = TargetPoint.getTargetPoint(7);
        try {
            astrobee.startMission();
            do {
                List<Integer> activeTargetsList = api.getActiveTargets();
                PathFindNode[] activeTargets = new PathFindNode[activeTargetsList.size()];
                if (api.getTimeRemaining().get(1) < 120000) {
                    shouldConsiderGoal = true;
                }

                for (int i = 0; i < activeTargetsList.size(); i++) {
                    activeTargets[i] = TargetPoint.getTargetPoint(activeTargetsList.get(i));
                }
                PathFindNode[] pathNodes = new OptimalPath(api.getTimeRemaining().get(1), astrobee.currentPathFindNode, activeTargets, shouldConsiderGoal).getPath();
                if (pathNodes == null) break;
                else if (pathNodes.length != activeTargets.length) isGoingToGoal = true;
                for (PathFindNode nextNode : pathNodes) {
                    astrobee.moveTo(nextNode);
                    TargetPoint nextTargetPoint = (TargetPoint) nextNode;
                    if (nextTargetPoint.getPointNumber() <= 6) {
                        astrobee.shootLaser();
                    }
                    if (nextTargetPoint.getPointNumber() == 5) {
                        astrobee.attemptScanQRDock(false);
                    } else if (nextTargetPoint.getPointNumber() == 7) {
                        astrobee.attemptScanQRNav(true);
                    }
                }
                if (isGoingToGoal) break;

            } while (api.getTimeRemaining().get(1) > 20000);

            astrobee.endMission();

        } catch (Exception e) {
            Logger.__log("CRITICAL ERROR");
            Logger.__log(e.getMessage());
            Logger.__log(Arrays.toString(e.getStackTrace()));
            astrobee.__forceEndMission();
        }
    }

    @Override
    protected void runPlan3() {
        // write your plan 3 here
    }

}

