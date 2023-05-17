package jp.jaxa.iss.kibo.rpc.defaultapk;

import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.logger.Logger;
import jp.jaxa.iss.kibo.pathfind.*;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.utils.QRReader;
import jp.jaxa.iss.kibo.utils.QuaternionCalculator;

public class Astrobee {
    public static final double ASTROBEE_ACCELERATION = 0.001979193446334;
    public static final Quaternion EMPTY_QUATERNION = new Quaternion(0, 0, 0, 1);
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

    public void moveTo(PathFindNode node, Quaternion orientation) {
        PathFind.pathFindMoveTo(api, currentPathFindNode, node, orientation);
        currentPathFindNode = node;
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

    public boolean attemptScanQRNav(boolean isRotate) {
        if (isRotate)
            moveTo(currentPathFindNode, QuaternionCalculator.calculateQuaternion(currentPathFindNode, PointOfInterest.QR_CODE));
        scannedQrText = QRReader.readQR(api);
        return scannedQrText != null;
    }

    public boolean attemptScanQRDock(boolean isRotate) {
        if (isRotate)
            moveTo(currentPathFindNode, QuaternionCalculator.calculateQuaternion(PointOfInterest.QR_CODE, currentPathFindNode));
        scannedQrText = QRReader.readQR(api, QRReader.CameraMode.DOCK);
        return scannedQrText != null;
    }

}
