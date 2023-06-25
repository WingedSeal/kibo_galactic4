package jp.jaxa.iss.kibo.rpc.defaultapk;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.logger.Logger;
import jp.jaxa.iss.kibo.pathfind.*;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.utils.CameraMode;
import jp.jaxa.iss.kibo.utils.QRReader;
import jp.jaxa.iss.kibo.utils.QuaternionCalculator;

import java.util.List;


public class Astrobee {
    public static double ASTROBEE_ACCELERATION = 0.00699;
    public static final Quaternion EMPTY_QUATERNION = new Quaternion(0, 0, 0, 1);
//    public static final long TIME_THRESHOLD = 30000;
    private static final String GUESSED_QR_TEXT = "GO_TO_COLUMBUS";
    private static String scannedQrText = null;
    private PathFindNode previousPathFindNode = TargetPoint.START;
    private PathFindNode currentPathFindNode = TargetPoint.START;
    public final KiboRpcApi api;

    public Astrobee(KiboRpcApi api) {
        this.api = api;
    }

    public void startMission() {
        api.startMission();
    }

    public void __forceEndMission() {
        api.notifyGoingToGoal();
        api.reportMissionCompletion(Logger.logMessage);
    }

    public void endMission() {
        api.notifyGoingToGoal();
        moveTo(PathFindNode.GOAL);
        if (scannedQrText == null)
            api.reportMissionCompletion(GUESSED_QR_TEXT);
        else
            api.reportMissionCompletion(scannedQrText);
    }

    public void moveTo(PathFindNode node) {
        if (!(node instanceof TargetPoint)) {
            moveTo(node, EMPTY_QUATERNION);
            return;
        }
        TargetPoint pointNode = (TargetPoint) node;
        Quaternion orientation;
        if (pointNode.getPointNumber() <= 6) {
            orientation = QuaternionCalculator.calculateQuaternion(node, Target.getTarget(pointNode.getPointNumber()));
        } else {
            //orientation = QuaternionCalculator.calculateQuaternion(node, PointOfInterest.QR_CODE);
            orientation = new Quaternion(0f, 0.707f, 0f, 0.707f);
        }
        moveTo(node, orientation);

    }

    public void moveToPoint(int pointNumber) {
        moveTo(TargetPoint.getTargetPoint(pointNumber));
    }

    public void moveTo(PathFindNode node, Quaternion orientation) {
        previousPathFindNode = currentPathFindNode;
        currentPathFindNode = node;
        PathFind.pathFindMoveTo(this, previousPathFindNode, node, orientation);
    }


    /**
     * @return whether the QR code was NOT already scanned
     */
    public boolean isQrNotScanned() {
        return scannedQrText == null;
    }


    /**
     * Shoot laser in the direction Astrobee is facing
     *
     * @throws IllegalStateException Attempted to shoot laser while not being on a point node(TargetPoint)
     * @throws NullPointerException Attempted to turn laser on while not in the target point area
     */
    public void shootLaser() {
        if (!(currentPathFindNode instanceof TargetPoint)) {
            throw new IllegalStateException("Attempted to shoot laser while not being on a point node");
        }
        TargetPoint pointNode = (TargetPoint) currentPathFindNode;
        Result result = api.laserControl(true);
        if (result == null) {
            throw new NullPointerException("astrobee not on the target point");
        }
        api.takeTargetSnapshot(pointNode.getPointNumber());
        List<Integer> activateTargets = api.getActiveTargets();
        if (activateTargets.contains(pointNode.getPointNumber()) && activateTargets.size() == 1) { //change this to throw only when lastest activate target list count = 1
            throw new IllegalStateException("fail to deactivate target");
        }
    }

    /**
     * Attempt to scan QR using nav camera
     *
     * @param isRotate whether to automatically rotate Astrobee
     * @param attempts amount of attempts to try to scan
     * @return whether the scan was successful
     */
    public boolean attemptScanQRNav(boolean isRotate, int attempts) {
        if (isRotate)
            moveTo(currentPathFindNode, QuaternionCalculator.calculateNavCamQuaternion(currentPathFindNode, PointOfInterest.QR_CODE));
//            moveTo(currentPathFindNode, new Quaternion(0,0.707f,0,0.707f));
        for (int i = 0; i < attempts; ++i) {
            if (scannedQrText != null) break;
            scannedQrText = QRReader.readQR(api);
        }
        return scannedQrText != null;
    }


    /**
     * Attempt to scan QR using dock camera
     *
     * @param isRotate whether to automatically rotate Astrobee
     * @param attempts amount of attempts to try to scan
     * @return whether the scan was successful
     */
    public boolean attemptScanQRDock(boolean isRotate, int attempts) {
        if (isRotate)
            moveTo(currentPathFindNode, QuaternionCalculator.calculateDockCamQuaternion(currentPathFindNode, PointOfInterest.QR_CODE));
        for (int i = 0; i < attempts; ++i) {
            if (scannedQrText != null) break;
            scannedQrText = QRReader.readQR(api, CameraMode.DOCK);
        }
        return scannedQrText != null;
    }


    /**
     * Attempt to scan QR while automatically rotating Astrobee using the closest camera.
     *
     * @param attempts amount of attempts to try to scan
     * @return whether the scan was successful
     */
    public boolean attemptScanQR(int attempts, CameraMode mode) {
        switch (mode) {
            case NAV:
                attemptScanQRNav(false, attempts);
                break;
            case DOCK:
                attemptScanQRDock(false, attempts);
                break;
        }
        return scannedQrText != null;
    }


    /**
     * Get current points of active targets from api
     *
     * @return active points
     */
    public TargetPoint[] getActivePoints() {
        List<Integer> activeTargetsNumbers = api.getActiveTargets();
        TargetPoint[] activePoints = new TargetPoint[activeTargetsNumbers.size()];
        for (int i = 0; i < activeTargetsNumbers.size(); i++) {
            activePoints[i] = TargetPoint.getTargetPoint(activeTargetsNumbers.get(i));
        }
        return activePoints;
    }

    /*/**
     * Get Nav camera intrinsics
     *
     * @return [0] Nav camera matrix [1] distortion coefficient
     *
    public double[][] getNavCamIntrinsics(){
        return this.NAV_CAM_INTRINSICS;
    }

    /**
     * Get Dock camera intrinsics
     *
     * @return [0] Dock camera matrix [1] distortion coefficient
     *
    public double[][] getDockCamIntrinsics(){
        return this.DOCK_CAM_INTRINSICS;
    }*/

    /**
     * Get node that astrobee currently at
     *
     * @return currentPathFindNode
     */
    public PathFindNode getCurrentPathFindNode() {
        return currentPathFindNode;
    }

    /**
     * Get node where astrobee was before
     *
     * @return previousPathFindNode
     */

    public PathFindNode getPreviousPathFindNode() {
        return previousPathFindNode;
    }

    /**
     * Error handling when Astrobee.shootLaser() and PathFind.moveTo is not working properly
     *
     * @return true if everything fail to run, false if it can go on
     */
    public boolean failMoveTo() {
        try {
            if (previousPathFindNode.equals(PathFindNode.GOAL) || currentPathFindNode.equals(PathFindNode.GOAL)) {
                Logger.__log("end");
                return true;
            } else if (currentPathFindNode.equals(TargetPoint.getTargetPoint(5)) || previousPathFindNode.equals(TargetPoint.getTargetPoint(5))) {
                Logger.__log("to goal");
                moveTo(TargetPoint.GOAL);
            } else {
                Logger.__log("to 5");
                moveTo(TargetPoint.getTargetPoint(5));

            }
            return false;

        } catch (Exception e) {
            return failMoveTo();
        }
    }

    public boolean failDeactivatedTarget() {
        try {
            if (currentPathFindNode.equals(TargetPoint.getTargetPoint(5))) {
                Logger.__log("to goal");
                moveTo(TargetPoint.GOAL);
            } else {
                Logger.__log("to 5");
                moveTo(TargetPoint.getTargetPoint(5));

            }
            return false;
        } catch (Exception e) {
            return failMoveTo();
        }
    }
}
