package jp.jaxa.iss.kibo.rpc.thailand;

import android.graphics.Bitmap;
import android.support.constraint.solver.Goal;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.thailand.pathfind.*;
import jp.jaxa.iss.kibo.rpc.thailand.utils.CameraMode;
import jp.jaxa.iss.kibo.rpc.thailand.utils.QRReader;
import jp.jaxa.iss.kibo.rpc.thailand.utils.QuaternionCalculator;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.List;


public class Astrobee {
    public static double ASTROBEE_ACCELERATION = 0.00699;
    public static final Quaternion EMPTY_QUATERNION = new Quaternion(0, 0, 0, 1);
    private static final String GUESSED_QR_TEXT = "GO_TO_COLUMBUS";
    private static String scannedQrText = null;
    private PathFindNode previousPathFindNode = TargetPoint.START;
    private PathFindNode currentPathFindNode = TargetPoint.START;
    public final KiboRpcApi api;
    private final double[][] NAV_CAM_INTRINSICS, DOCK_CAM_INTRINSICS;

    public Astrobee(KiboRpcApi api) {
        this.api = api;
        this.NAV_CAM_INTRINSICS = api.getNavCamIntrinsics();
        this.DOCK_CAM_INTRINSICS = api.getDockCamIntrinsics();
    }

    public void startMission() {
        api.startMission();
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

    public void moveToRealPoint(int pointNumber) {
        moveTo(TargetPoint.getRealTargetPoint(pointNumber));
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
     * check whether the time left from moving to RealPoint then coming back and going to Goal
     * is less than the current time remaining
     *
     * used in getPathTimeToGoal(TargetPoint node)
     *
     * @param pointNode currentPathFindNode
     * @param timeLeft  TimeRemaining - time duration moving from currentPathFindNode to Goal
     *
     *
     */
    private void checkTime(TargetPoint pointNode, double timeLeft){
        switch(pointNode.id){
            case POINT_1:
                if(timeLeft > 23712+4000){
                    throw new NullPointerException("astrobee not on the target point");
                }
                break;
            case POINT_2:
                if(timeLeft > 21856+4000){
                    throw new NullPointerException("astrobee not on the target point");
                }
                break;
            case POINT_3:
                if(timeLeft > 21952+4000){
                    throw new NullPointerException("astrobee not on the target point");
                }
                break;
            case POINT_4:
                if(timeLeft > 22832+4000){
                    throw new NullPointerException("astrobee not on the target point");
                }
                break;
        }
        endMission();
    }

    /**
     * check whether you should go to the other node or not in case there is no much time left
     * for shooting both nodes
     *
     * used in checkTime(TargetPoint pointNode, TargetPoint[] pathNodes, double optimalTime)
     *
     * @param pointNode currentPathFindNode
     * @param pathNodes currentPath from getting optimal path
     * @param optimalTime time duration moving from currentPathFindNode to Goal according to optimal path
     * @param timeRemaining the remaining time of the mission
     * @return whether you will go the other node or not
     */


    private boolean shouldGoToOtherNode(TargetPoint pointNode, TargetPoint[] pathNodes, double optimalTime, long timeRemaining){
        if(pathNodes[1].getScore() >= pointNode.getScore()){
            if(api.getTimeRemaining().get(1) > optimalTime){
                return true;
            }
        }
        else{
            double timeLeft = timeRemaining - getPathTimeToGoal(pointNode);
            switch(pointNode.id){
                case POINT_1:
                    if(timeLeft > 23712+4000){
                        break;
                    }
                    return true;
                case POINT_2:
                    if(timeLeft > 21856+4000){
                        break;
                    }
                    return true;
                case POINT_3:
                    if(timeLeft > 21952+4000) {
                        break;
                    }
                    return true;
                case POINT_4:
                    if(timeLeft > 22832+4000) {
                        break;
                    }
                    return true;
            }
        }
        return false;
    }


    /**
     * check whether the time left from moving to RealPoint then coming back and going to Goal
     * is less than the current time remaining
     *
     * used in  getPathTimeToGoal(TargetPoint node)
     *
     * @param pointNode currentPathFindNode
     * @param pathNodes currentPath from getting optimal path
     * @param optimalTime time duration moving from currentPathFindNode to Goal according to optimal path
     * @param timeRemaining the remaining time of the mission
     *
     */
    private void checkTime(TargetPoint pointNode, TargetPoint[] pathNodes, double optimalTime, long timeRemaining){
        double timeLeft = timeRemaining - getPathTimeToGoal(pointNode);
        switch(pointNode.id){
            case POINT_1:
                if(timeLeft > 23712+4000){
                    throw new NullPointerException("astrobee not on the target point");
                }
                break;
            case POINT_2:
                if(timeLeft > 21856+4000){
                    throw new NullPointerException("astrobee not on the target point");
                }
                break;
            case POINT_3:
                if(timeLeft > 21952+4000){
                    throw new NullPointerException("astrobee not on the target point");
                }
                break;
            case POINT_4:
                if(timeLeft > 22832+4000){
                    throw new NullPointerException("astrobee not on the target point");
                }
                break;
        }
        if(shouldGoToOtherNode(pointNode, pathNodes, optimalTime, timeRemaining)){
                moveTo(pathNodes[1]);
                shootLaser();
            }
        else{
            shootTargetFromRealPoint();
        }
        endMission();

    }

    /**
     * calculate the time spent on walking from that node to Goal
     *
     * @param node TargetPoint
     * @return estimated total time in milliseconds
     */
    private double getPathTimeToGoal(TargetPoint node){
        int totalTimeSec = 0;
        for (double distance : PathFind.estimatePathDistances(this, node, PathFindNode.GOAL)) {
            if (distance > 2.8d) Astrobee.ASTROBEE_ACCELERATION = 0.00804d;
            else if (distance > 2.39d) Astrobee.ASTROBEE_ACCELERATION = 0.00798d;
            else if (distance > 1.0d) Astrobee.ASTROBEE_ACCELERATION = 0.00761d;
            else Astrobee.ASTROBEE_ACCELERATION = 0.00736d;
            totalTimeSec += 2 * (Math.sqrt(distance / Astrobee.ASTROBEE_ACCELERATION));
        }
        return totalTimeSec*1000;
    }

    /**
     * Shoot laser in the direction Astrobee is facing
     *
     * @throws IllegalStateException Attempted to shoot laser while not being on a point node(TargetPoint)
     * @throws NullPointerException  Attempted to turn laser on while not in the target point area
     */
    public void shootLaser() {
        if (!(currentPathFindNode instanceof TargetPoint)) {
            throw new IllegalStateException("Attempted to shoot laser while not being on a point node");
        }
        TargetPoint pointNode = (TargetPoint) currentPathFindNode;
        Result result = api.laserControl(true);
        if (result == null) {
            if(pointNode.equals(TargetPoint.getRealTargetPoint(pointNode.getPointNumber()))){
                throw new NullPointerException("astrobee not on the target point");
            }
            TargetPoint[] activePoints = getActivePoints();
            long timeRemaining = api.getTimeRemaining().get(1);
            switch(activePoints.length){
                case 1:
                    double timeLeft = timeRemaining - getPathTimeToGoal(pointNode);
                    checkTime(pointNode,timeLeft);
                    break;
                case 2:
                    OptimalPath optimalPath = new OptimalPath(this, timeRemaining, currentPathFindNode, activePoints, true);
                    double optimalTime = optimalPath.getMinTime();
                    TargetPoint[] pathNodes = optimalPath.getPath();
                    checkTime(pointNode, pathNodes, optimalTime, timeRemaining);
                    break;
            }
        }
        api.takeTargetSnapshot(pointNode.getPointNumber());
        List<Integer> activateTargets = api.getActiveTargets();
        if (activateTargets.contains(pointNode.getPointNumber()) && activateTargets.size() == 1) { //change this to throw only when last activate target list count = 1
            double timeLeft = api.getTimeRemaining().get(1) - getPathTimeToGoal(pointNode);
            switch(pointNode.id){
                case POINT_1:
                    if(timeLeft > 23712+4000){
                        throw new IllegalStateException("fail to deactivate target");
                    }
                    break;
                case POINT_2:
                    if(timeLeft > 21856+4000){
                        throw new IllegalStateException("fail to deactivate target");
                    }
                    break;
                case POINT_3:
                    if(timeLeft > 21952+4000){
                        throw new IllegalStateException("fail to deactivate target");
                    }
                    break;
                case POINT_4:
                    if(timeLeft > 22832+4000){
                        throw new IllegalStateException("fail to deactivate target");
                    }
                    break;
            }
            endMission();
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
        api.flashlightControlFront(0.05f);
        for (int i = 0; i < attempts; ++i) {
            if (scannedQrText != null) break;
            scannedQrText = QRReader.readQR(this);
        }
        api.flashlightControlFront(0.0f);
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
        api.flashlightControlFront(0.05f);
        for (int i = 0; i < attempts; ++i) {
            if (scannedQrText != null) break;
            scannedQrText = QRReader.readQR(this, CameraMode.DOCK);
        }
        api.flashlightControlFront(0.0f);
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

    /**
     * Get Nav camera intrinsics
     *
     * @return [0] Nav camera matrix [1] distortion coefficient
     */
    public double[][] getNavCamIntrinsics() {
        return this.NAV_CAM_INTRINSICS;
    }

    /**
     * Get Dock camera intrinsics
     *
     * @return [0] Dock camera matrix [1] distortion coefficient
     */
    public double[][] getDockCamIntrinsics() {
        return this.DOCK_CAM_INTRINSICS;
    }

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
        for (int i = 0; i < 10; ++i) {
            try {
                if (previousPathFindNode.equals(PathFindNode.GOAL) || currentPathFindNode.equals(PathFindNode.GOAL)) {
                    return true;
                } else if (currentPathFindNode.equals(TargetPoint.getTargetPoint(5)) || previousPathFindNode.equals(TargetPoint.getTargetPoint(5))) {
                    moveTo(TargetPoint.GOAL);
                } else {
                    moveTo(TargetPoint.getTargetPoint(5));

                }
                return false;

            } catch (Exception ignored) {
            }
        }
        return true;

    }

    /**
     * Go to the real point to attempt to shoot the target
     * <p>
     * Called when astrobee fails to shoot/deactivate target
     *
     * @return whether to go the goal
     */
    public boolean shootTargetFromRealPoint() {
        for (int i = 0; i < 10; i++) {
            try {
                TargetPoint pointNode = (TargetPoint) currentPathFindNode;
                if (currentPathFindNode.equals(TargetPoint.getRealTargetPoint(pointNode.getPointNumber()))) {
                    moveToPoint(pointNode.getPointNumber());
                    shootLaser();
                } else {
                    moveToRealPoint(pointNode.getPointNumber());
                    shootLaser();
                    moveToPoint(pointNode.getPointNumber());
                }
                return false;
            } catch (Exception ignored) {
            }
        }
        return true;

    }

    /**
     * Undistort mat image
     *
     * @param distortedImg mat image
     * @param mode         camera's mode
     * @return undistorted image
     */
    public Bitmap undistortMatImage(Mat distortedImg, CameraMode mode) {
        double[][] camIntrinsics;
        switch (mode) {
            case NAV:
                camIntrinsics = NAV_CAM_INTRINSICS;
                break;
            case DOCK:
                camIntrinsics = DOCK_CAM_INTRINSICS;
                break;
            default:
                throw new IllegalArgumentException("mode should be NAV or DOCK");
        }
        Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
        Mat dstMatrix = new Mat(1, 5, CvType.CV_32FC1);
        cameraMatrix.put(0, 0, camIntrinsics[0]);
        dstMatrix.put(0, 0, camIntrinsics[1]);
        Mat undistortedImg = new Mat(distortedImg.rows(), distortedImg.cols(), CvType.CV_8UC4);
        Bitmap returnImg;
        try {
            Imgproc.undistort(distortedImg, undistortedImg, cameraMatrix, dstMatrix);
            returnImg = Bitmap.createBitmap(distortedImg.cols(), distortedImg.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(undistortedImg, returnImg);
            return returnImg;
        } catch (Exception ignored) {
        }
        return null;
    }
}
