package jp.jaxa.iss.kibo.rpc.defaultapk;

import android.graphics.Point;

import jp.jaxa.iss.kibo.logger.Logger;
import jp.jaxa.iss.kibo.pathfind.OptimalPath;
import jp.jaxa.iss.kibo.pathfind.PathFindNode;
import jp.jaxa.iss.kibo.pathfind.Target;
import jp.jaxa.iss.kibo.pathfind.TargetPoint;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;
import java.util.ArrayList;
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
        boolean considerGoal = false;
        boolean toGoal = false;
        boolean qrScanned = false;
        // PathFindNode QRNode = TargetPoint.getTargetPoint(7);
        try {
            astrobee.startMission();
            do {
                List<Integer> activeTargetsList = api.getActiveTargets();
                PathFindNode[] activeTargets = new PathFindNode[activeTargetsList.size()];
                if (api.getTimeRemaining().get(1) < 120000) {
                    considerGoal=true;
                }
                
                for (int i=0; i<activeTargetsList.size(); i++) {
                    activeTargets[i] = TargetPoint.getTargetPoint(activeTargetsList.get(i));
                }
                PathFindNode[] pathNodes = new OptimalPath(api.getTimeRemaining().get(1), astrobee.currentPathFindNode, activeTargets, considerGoal).getPath();
                if (pathNodes == null) break;
                else if (pathNodes.length != activeTargets.length) toGoal = true;
                for (PathFindNode nextNode: pathNodes) {
                    astrobee.moveTo(nextNode);
                    TargetPoint nextTargetPoint = (TargetPoint)nextNode;
                    if (nextTargetPoint.getPointNumber() <= 6) {
                        astrobee.shootLaser();
                    }
                    if (nextTargetPoint.getPointNumber() == 5) {
                        if (astrobee.attemptScanQRDock(true)) qrScanned = true;
                    }
                    else if (nextTargetPoint.getPointNumber() == 7) {
                        if (astrobee.attemptScanQRNav(true)) qrScanned = true;
                    }
                }
                if (toGoal) break;
               
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

