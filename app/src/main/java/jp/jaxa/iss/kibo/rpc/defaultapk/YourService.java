package jp.jaxa.iss.kibo.rpc.defaultapk;

import jp.jaxa.iss.kibo.logger.Logger;
import jp.jaxa.iss.kibo.pathfind.OptimalPath;
import jp.jaxa.iss.kibo.pathfind.TargetPoint;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import java.util.Arrays;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    private static final long MINIMUM_MILLISECONDS_TO_END_MISSION = 30000;

    protected void runPlan1_backup() {
        Astrobee astrobee = new Astrobee(api);
        try {
            astrobee.startMission();

            astrobee.moveToPoint(5);
            astrobee.shootLaser();
            astrobee.attemptScanQRDock(false, 5);

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
        boolean shouldConsiderGoal;
        boolean isGoingToGoal = false;

        // PathFindNode QRNode = TargetPoint.getTargetPoint(7);
        try {
            astrobee.startMission();
            do {
                shouldConsiderGoal = true;
                TargetPoint[] activePoints = astrobee.getActivePoints();

                TargetPoint[] pathNodes = new OptimalPath(astrobee,
                        api.getTimeRemaining().get(1), astrobee.getCurrentPathFindNode(), activePoints, shouldConsiderGoal).getPath();
                if (pathNodes == null) break;
                else if (pathNodes.length != activePoints.length) isGoingToGoal = true;
                for (TargetPoint nextTargetPoint : pathNodes) {
                    try{
                        astrobee.moveTo(nextTargetPoint);
                    }catch (Exception e){
                        Logger.__log(e.getMessage());
                        isGoingToGoal = astrobee.failMoveTo();
                        break;
                    }
                    try{
                        if (nextTargetPoint.getPointNumber() <= 6) {
                            astrobee.shootLaser();
                        }
                    }catch (Exception e){
                        isGoingToGoal = astrobee.failDeactivatedTarget();
                        break;

                    }

                    if (nextTargetPoint.getPointNumber() == 5 && !astrobee.isQrScanned()) {
                        astrobee.attemptScanQRDock(false, 5);
                    } else if (nextTargetPoint.getPointNumber() == 7) {
                        astrobee.attemptScanQRNav(true, 3);
                    }
                }
                if (isGoingToGoal) break;
                if(!astrobee.isQrScanned() && api.getTimeRemaining().get(1) < 120000){
                    astrobee.moveTo(TargetPoint.getTargetPoint(5));
                    astrobee.attemptScanQRDock(false, 5);
                    if(!astrobee.isQrScanned()){
                        astrobee.moveTo(TargetPoint.getTargetPoint(7));
                        astrobee.attemptScanQRNav(true, 5);
                        astrobee.moveTo(TargetPoint.getTargetPoint(5));
                    }
                }

            } while (api.getTimeRemaining().get(1) > MINIMUM_MILLISECONDS_TO_END_MISSION);
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

