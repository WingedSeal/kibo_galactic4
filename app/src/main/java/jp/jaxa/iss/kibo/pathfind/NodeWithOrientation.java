package jp.jaxa.iss.kibo.pathfind;

import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.utils.CameraMode;
import jp.jaxa.iss.kibo.utils.QuaternionCalculator;

public class NodeWithOrientation extends Node {

    private Quaternion orientation;
    private final CameraMode cameraMode;
    private final Node pointedNode;

    public NodeWithOrientation(double x, double y, double z, Node pointedNode) {
        this(x, y, z, pointedNode, CameraMode.NAV);
    }

    public NodeWithOrientation(double x, double y, double z, Node pointedNode, CameraMode mode) {
        super(x, y, z);
        cameraMode = mode;
        this.pointedNode = pointedNode;
        switch (mode) {
            case NAV:
                orientation = QuaternionCalculator.calculateNavCamQuaternion(this, pointedNode);
                break;
            case DOCK:
                orientation = QuaternionCalculator.calculateDockCamQuaternion(this, pointedNode);
                break;
        }
    }

    public static NodeWithOrientation fromNode(Node node, Node point) {
        return new NodeWithOrientation(node.getX(), node.getY(), node.getZ(), point);
    }

    public static NodeWithOrientation fromNode(Node node, Node point, CameraMode mode) {
        return new NodeWithOrientation(node.getX(), node.getY(), node.getZ(), point, mode);
    }

    public Quaternion getOrientation() {
        return orientation;
    }

    public CameraMode getCameraMode() {
        return cameraMode;
    }

    public Node getPointedNode() {
        return pointedNode;
    }
}
