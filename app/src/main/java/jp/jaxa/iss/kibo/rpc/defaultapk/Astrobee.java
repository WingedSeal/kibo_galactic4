package jp.jaxa.iss.kibo.rpc.defaultapk;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.logger.Logger;
import jp.jaxa.iss.kibo.pathfind.*;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.utils.QRReader;
import jp.jaxa.iss.kibo.utils.QuaternionCalculator;
import jp.jaxa.iss.kibo.utils.CameraMode;

import java.util.List;



public class Astrobee {
    public static double ASTROBEE_ACCELERATION = 0.00699;
    public static final Quaternion EMPTY_QUATERNION = new Quaternion(0, 0, 0, 1);
    public static final long TIME_THRESHOLD = 30000;
    private final double[][] NAV_CAM_INTRINSICS;
    private final double[][] DOCK_CAM_INTRINSICS;
    private static final String GUESSED_QR_TEXT = "GO_TO_COLUMBUS";
    private static String scannedQrText = null;
    PathFindNode currentPathFindNode = PathFindNode.START;
    public final KiboRpcApi api;

    public Astrobee(KiboRpcApi api) {
        this.api = api;
        this.NAV_CAM_INTRINSICS = api.getNavCamIntrinsics();
        this.DOCK_CAM_INTRINSICS = api.getDockCamIntrinsics();
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
            orientation = QuaternionCalculator.calculateQuaternion(node, PointOfInterest.QR_CODE);
        }
        moveTo(node, orientation);

    }

    public void moveToPoint(int pointNumber) {
        moveTo(TargetPoint.getTargetPoint(pointNumber));
    }

    public void moveTo(PathFindNode node, Quaternion orientation) {
        PathFind.pathFindMoveTo(this, currentPathFindNode, node, orientation);
        currentPathFindNode = node;
    }



    /**
     * @return whether the QR code was already scanned
     */
    public static boolean isQrScanned() {
        return scannedQrText != null;
    }


    /**
     * Shoot laser in the direction Astrobee is facing
     *
     * @throw IllegalStateException Attempted to shoot laser while not being on a point node(TargetPoint)
     */
    public void shootLaser() {
        if (!(currentPathFindNode instanceof TargetPoint)) {
            throw new IllegalStateException("Attempted to shoot laser while not being on a point node");
        }
        TargetPoint pointNode = (TargetPoint) currentPathFindNode;
        Result result = api.laserControl(true);
        if(result == null){
            moveToPoint(5);
            return;
        }
        int loopCount = 0;
        while(!result.hasSucceeded() && loopCount <4){
            result = api.laserControl(true);
            ++loopCount;
        }
        api.takeTargetSnapshot(pointNode.getPointNumber());


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
            moveTo(currentPathFindNode, QuaternionCalculator.calculateDockCamQuaternion(currentPathFindNode,PointOfInterest.QR_CODE));
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
        switch (mode){
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

    /**
     * Get Nav camera intrinsics
     *
     * @return [0] Nav camera matrix [1] distortion coefficient
     */
    public double[][] getNavCamIntrinsics(){
        return this.NAV_CAM_INTRINSICS;
    }

    /**
     * Get Dock camera intrinsics
     *
     * @return [0] Dock camera matrix [1] distortion coefficient
     */
    public double[][] getDockCamIntrinsics(){
        return this.DOCK_CAM_INTRINSICS;
    }
}
