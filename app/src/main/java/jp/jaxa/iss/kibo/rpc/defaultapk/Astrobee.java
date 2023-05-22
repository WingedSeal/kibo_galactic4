package jp.jaxa.iss.kibo.rpc.defaultapk;

import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.logger.Logger;
import jp.jaxa.iss.kibo.pathfind.*;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.utils.QRReader;
import jp.jaxa.iss.kibo.utils.QuaternionCalculator;
import jp.jaxa.iss.kibo.utils.CameraMode;

import java.util.List;

import static jp.jaxa.iss.kibo.utils.QuaternionCalculator.calculateRadianBetweenQuaternion;

public class Astrobee {
    public static final double ASTROBEE_ACCELERATION = 0.0087406;
    public static final double ASTROBEE_DECELERATION = 0.00734847;
    public static final Quaternion EMPTY_QUATERNION = new Quaternion(0, 0, 0, 1);
    public static final long TIME_THRESHOLD = 30000;
    private static final String GUESSED_QR_TEXT = "GO_TO_COLUMBUS";
    String scannedQrText = null;
    PathFindNode currentPathFindNode = PathFindNode.START;
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
    public boolean isQrScanned() {
        return scannedQrText != null;
    }


    /**
     * Shoot laser in the direction Astrobee is facing
     *
     * @throws IllegalStateException Attempted to shoot laser while not being on a point node(TargetPoint)
     */
    public void shootLaser() {
        if (!(currentPathFindNode instanceof TargetPoint)) {
            throw new IllegalStateException("Attempted to shoot laser while not being on a point node");
        }
        TargetPoint pointNode = (TargetPoint) currentPathFindNode;
        api.laserControl(true);
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
            moveTo(currentPathFindNode, QuaternionCalculator.calculateQuaternion(currentPathFindNode, PointOfInterest.QR_CODE));
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
            moveTo(currentPathFindNode, QuaternionCalculator.calculateQuaternion(PointOfInterest.QR_CODE, currentPathFindNode));
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
    public boolean attemptScanQR(int attempts) {
        Quaternion currentOrientation = api.getRobotKinematics().getOrientation();
        double navCamRadian = calculateRadianBetweenQuaternion(currentOrientation,
                QuaternionCalculator.calculateQuaternion(currentPathFindNode, PointOfInterest.QR_CODE));
        double dockCamRadian = calculateRadianBetweenQuaternion(currentOrientation,
                QuaternionCalculator.calculateQuaternion(PointOfInterest.QR_CODE, currentPathFindNode));
        if (dockCamRadian < navCamRadian)
            return attemptScanQRDock(true, attempts);
        return attemptScanQRNav(true, attempts);
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
}
