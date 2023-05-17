package jp.jaxa.iss.kibo.rpc.defaultapk;

import jp.jaxa.iss.kibo.logger.Logger;
import jp.jaxa.iss.kibo.pathfind.PathFind;
import jp.jaxa.iss.kibo.pathfind.PathFindNode;
import jp.jaxa.iss.kibo.pathfind.Target;
import jp.jaxa.iss.kibo.pathfind.TargetPoint;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import java.util.Arrays;
import java.util.List;


/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    @Override
    protected void runPlan1_backup() {
        Astrobee astrobee = new Astrobee(api);
        try {
            astrobee.startMission();
            astrobee.moveTo(TargetPoint.getTargetPoint(4));
            astrobee.shootLaser();
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
        PathFindNode QRNode = TargetPoint.getTargetPoint(7);
        try {
            astrobee.startMission();
            do {
                PathFindNode nextNode = null;
                double minDistance = 99;
                double currDistance;
                PathFindNode currNode;

                for (int target: api.getActiveTargets()) {
                    currNode = TargetPoint.getTargetPoint(target);
                    currDistance = PathFind.getPathDistance(astrobee.currentPathFindNode, currNode);
                    if (minDistance > currDistance && astrobee.isNodeInTime(currNode)) {
                        minDistance = currDistance;
                        nextNode = currNode;
                    }
                }
                
                if (astrobee.scannedQrText == null) {
                    currDistance = PathFind.getPathDistance(astrobee.currentPathFindNode, QRNode);
                    if (minDistance > currDistance && astrobee.isNodeInTime(QRNode)) {
                        minDistance = currDistance;
                        nextNode = QRNode;
                    }
                }

                if (nextNode != null) {
                    if (nextNode instanceof TargetPoint) {
                        astrobee.moveTo(nextNode);
                        TargetPoint nextTargetPoint = (TargetPoint)nextNode;
                        Logger.__log("Moving to point " + nextTargetPoint.getPointNumber());
                        if (nextTargetPoint.getPointNumber() <= 6) {
                            astrobee.shootLaser();
                        }
                        if (nextTargetPoint.getPointNumber() == 5) {
                            Logger.__log("Attempting QR Dock scan...");
                            astrobee.attemptScanQRDock();
                        }
                        else if (nextTargetPoint.getPointNumber() == 7) {
                            Logger.__log("Attempting QR Nav scan...");
                            astrobee.attemptScanQRNav();
                        }
                    }
                }
                else {
                    Logger.__log("EXITING...");
                }
            } while (api.getTimeRemaining()[0] > 30000);

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

