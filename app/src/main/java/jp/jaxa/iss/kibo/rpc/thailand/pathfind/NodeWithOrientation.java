package jp.jaxa.iss.kibo.rpc.thailand.pathfind;

import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.thailand.utils.CameraMode;
import jp.jaxa.iss.kibo.rpc.thailand.utils.QuaternionCalculator;

public class NodeWithOrientation extends Node {

    private Quaternion orientation;
    private final CameraMode cameraMode;
    private final Node pointedNode;

    public static NodeWithOrientation node5 = new NodeWithOrientation(11.369, -8.5518, 4.95d);

    public NodeWithOrientation(double x, double y, double z) {
        super(x, y, z);
        this.pointedNode = PointOfInterest.QR_CODE;
        this.cameraMode = CameraMode.NAV;
        this.orientation = QuaternionCalculator.UPWARD;

    }


    public NodeWithOrientation(double x, double y, double z, Node pointedNode) {
        this(x, y, z, pointedNode, CameraMode.NAV);
    }

    public NodeWithOrientation(double x, double y, double z, Node pointedNode, CameraMode mode) {
        super(x, y, z);
        cameraMode = mode;
        this.pointedNode = pointedNode;
        switch (mode) {
            case NAV:
                orientation = QuaternionCalculator.UPWARD;
                break;
            case DOCK:
                orientation = QuaternionCalculator.DOWNWARD;
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
