package jp.jaxa.iss.kibo.pathfind;

import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.pathfind.zone.Zone;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.utils.Line;
import org.apache.commons.lang.ArrayUtils;
import jp.jaxa.iss.kibo.rpc.defaultapk.Astrobee;
import jp.jaxa.iss.kibo.utils.QuaternionCalculator;

import static jp.jaxa.iss.kibo.utils.Line.distanceBetweenPoints;

public class PathFind {

    private static Node findNodeX(double y, double z, Node start, Node end) {
        return new Node(Line.findOptimizedPosition(start, end, null, y, z), y, z);
    }

    private static Node findNodeY(double x, double z, Node start, Node end) {
        return new Node(x, Line.findOptimizedPosition(start, end, x, null, z), z);
    }

    private static Node findNodeZ(double x, double y, Node start, Node end) {
        return new Node(x, y, Line.findOptimizedPosition(start, end, x, y, null));
    }

    private static NodeWithOrientation findNodeWithOrientationX(double y, double z, Node start, Node end, Node pointedNode) {
        return NodeWithOrientation.fromNode(findNodeX(y, z, start, end), pointedNode);
    }

    private static NodeWithOrientation findNodeWithOrientationY(double x, double z, Node start, Node end, Node pointedNode) {
        return NodeWithOrientation.fromNode(findNodeY(x, z, start, end), pointedNode);
    }

    private static NodeWithOrientation findNodeWithOrientationZ(double x, double y, Node start, Node end, Node pointedNode) {
        return NodeWithOrientation.fromNode(findNodeZ(x, y, start, end), pointedNode);
    }

    /**
     * Move to the PathFindNode using approximated the shortest path
     *
     * @param astrobee    Astrobee object
     * @param from        current position of Astrobee
     * @param to          position to move to
     * @param orientation orientation parameter of api.moveTo
     */
    public static void pathFindMoveTo(Astrobee astrobee, PathFindNode from, PathFindNode to, Quaternion orientation) {
        pathFindMoveTo(astrobee, from, to, orientation, false);
    }


    /**
     * Move to the PathFindNode using approximated the shortest path
     *
     * @param astrobee           Astrobee object
     * @param from               current position of Astrobee
     * @param to                 position to move to
     * @param orientation        orientation parameter of api.moveTo
     * @param printRobotPosition whether to print position
     */
    public static void pathFindMoveTo(Astrobee astrobee, PathFindNode from, PathFindNode to, Quaternion orientation, boolean printRobotPosition) {
        KiboRpcApi api = astrobee.api;
        for (Node node : getPathNodes(astrobee,from, to)) {
            if (node instanceof NodeWithOrientation) {
                NodeWithOrientation nodeWithOrientation = (NodeWithOrientation)node;
                if (nodeWithOrientation.getPointedNode().equals(PointOfInterest.QR_CODE) && !astrobee.isQrScanned()) {
                    Quaternion rotate;
                    switch (nodeWithOrientation.getCameraMode()) {
                        case DOCK:
                            rotate = QuaternionCalculator.calculateDockCamQuaternion(nodeWithOrientation,Target.QR_CODE);
                            break;
                        case NAV:
                            rotate = QuaternionCalculator.calculateNavCamQuaternion(nodeWithOrientation,Target.QR_CODE);
                            break;
                        default:
                            rotate = QuaternionCalculator.calculateNavCamQuaternion(nodeWithOrientation,Target.QR_CODE);
                            break;
                    }
                    moveTo(astrobee, nodeWithOrientation, rotate, printRobotPosition);
                    astrobee.attemptScanQR(5,nodeWithOrientation.getCameraMode());
                }
                else moveTo(astrobee, nodeWithOrientation, orientation, printRobotPosition);

            }
            else moveTo(astrobee, node, orientation, printRobotPosition);
        }
        moveTo(astrobee, to, orientation, printRobotPosition);
    }

    /**
     * move Astrobee to node and quaternion
     *
     * @param astrobee             Astrobee
     * @param node                 Point
     * @param quaternion           Quaternion
     * @param printRoBotPosition   printRobotPosition
     */
    public static void moveTo(Astrobee astrobee, Node node ,Quaternion quaternion,boolean printRoBotPosition){
        Result result = astrobee.api.moveTo(node,quaternion,printRoBotPosition);
        int loopCounter = 0;
        while(!result.hasSucceeded() && loopCounter < 4){
            result = astrobee.api.moveTo(node, quaternion,printRoBotPosition);
            ++loopCounter;
        }
        //can be implement to return boolean, use for checking if astrobee move successfully.
    }

    public static double estimateTotalDistance(Astrobee astrobee,PathFindNode from, PathFindNode to) {
        Node[] nodes = getPathNodes(astrobee,from, to);
        if (nodes.length == 0) {
            return distanceBetweenPoints(from, to);
        }

        double totalDistance = 0;
        totalDistance += distanceBetweenPoints(from, nodes[0]);
        for (int i = 1; i < nodes.length; ++i) {
            totalDistance += distanceBetweenPoints(nodes[i - 1], nodes[i]);
        }
        totalDistance += distanceBetweenPoints(nodes[nodes.length - 1], to);
        return totalDistance;
    }

    public static double[] estimatePathDistances(Astrobee astrobee,PathFindNode from, PathFindNode to) {
        Node[] nodes = getPathNodes(astrobee,from, to);
        if (nodes.length == 0) {
            return new double[]{distanceBetweenPoints(from, to)};
        }

        double[] pathDistances = new double[nodes.length + 1];
        pathDistances[0] = distanceBetweenPoints(from, nodes[0]);
        for (int i = 1; i < nodes.length; ++i) {
            pathDistances[i] = distanceBetweenPoints(nodes[i - 1], nodes[i]);
        }
        pathDistances[nodes.length] = distanceBetweenPoints(nodes[nodes.length - 1], to);
        return pathDistances;
    }

    /**
     * Find Nodes(ending Node not included) in between 2 PathFindNode Astrobee needs to move through to reach the end PathFindNode
     *
     * @param start current position of Astrobee
     * @param end   PathFindNode Astrobee want to go to
     * @return nodes needed to travel
     */
    private static Node[] getPathNodes(Astrobee astrobee,PathFindNode start, PathFindNode end) {
        if (start.id == end.id) {
            return new Node[]{};
        }
        switch (start.id) {
            case START:
                switch (end.id) {
                    case POINT_1:
                        return new Node[]{findNodeY(Zone.keepOut1.xMin,Zone.keepOut1.zMax,start,end),};
                    case POINT_2:
                        return new Node[]{};
                    case POINT_3:
                        return new Node[]{NodeWithOrientation.node5};
                    case POINT_4:
                        return new Node[]{TargetPoint.getTargetPoint(2)};
                    case POINT_5:
                        return new Node[]{};
                    case POINT_6:
                        return new Node[]{};
                }
                break;
            case GOAL:
                switch (end.id) {
                    case START:
                        break;
                    case POINT_1:
                        return new Node[]{};
                    case POINT_2:
                        return new Node[]{};
                    case POINT_3:
                        return new Node[]{};
                    case POINT_4:
                        return new Node[]{};
                    case POINT_5:
                        return new Node[]{};
                    case POINT_6:
                        return new Node[]{};
                }
                break;
            case POINT_1:
                switch (end.id) {
                    case START:
                    case GOAL:
                        Node[] nodes = getPathNodes(astrobee,end, start);
                        ArrayUtils.reverse(nodes);
                        return nodes;
                    case POINT_2:
                        return new Node[]{};
                    case POINT_3:
                        if(!astrobee.isQrScanned())
                            return new Node[]{NodeWithOrientation.node5};

                        else
                            return new Node[]{findNodeWithOrientationX((Zone.keepOut3.yMin + Zone.keepOut3.yMax)/2, Zone.keepOut3.zMax, start, end, PointOfInterest.QR_CODE)};
                    case POINT_4:
                        return new Node[]{};
                    case POINT_5:
                        return new Node[]{};
                    case POINT_6:
                        return new Node[]{};
                }
                break;
            case POINT_2:
                switch (end.id) {
                    case START:
                    case GOAL:
                        return new Node[]{};
                    case POINT_1:
                        return new Node[]{};
                    case POINT_3:
                        if(!astrobee.isQrScanned())
                            return new Node[]{NodeWithOrientation.node5};
                        else
                            return new Node[]{findNodeWithOrientationX((Zone.keepOut3.yMin + Zone.keepOut3.yMax)/2, Zone.keepOut3.zMax, start, end, PointOfInterest.QR_CODE)};
                    case POINT_4:
                        return new Node[]{};
                    case POINT_5:
                        return new Node[]{};
                    case POINT_6:
                        return new Node[]{};
                }
                break;
            case POINT_3:
                switch (end.id) {
                    case START:
                    case GOAL:
                        return new Node[]{};
                    case POINT_1:
                        if(!astrobee.isQrScanned())
                            return new Node[]{NodeWithOrientation.node5};

                        else
                            return new Node[]{
                                    findNodeWithOrientationX((Zone.keepOut3.yMin + Zone.keepOut3.yMax)/2, Zone.keepOut3.zMax, start, end, PointOfInterest.QR_CODE)};
                    case POINT_2:
                        Node[] nodes = getPathNodes(astrobee, end, start);
                        ArrayUtils.reverse(nodes);
                        return nodes;
                    case POINT_4:
                        return new Node[]{};
                    case POINT_5:
                        return new Node[]{};
                    case POINT_6:
                        if(!astrobee.isQrScanned())
                            return new Node[]{NodeWithOrientation.node5};
                        else
                            return new Node[]{findNodeWithOrientationX((Zone.keepOut3.yMax + Zone.keepOut3.yMin)/2, Zone.keepOut3.zMax, start, end, PointOfInterest.QR_CODE)};
                }
                break;
            case POINT_4:
                switch (end.id) {
                    case START:
                    case GOAL:
                        return new Node[]{};
                    case POINT_1:
                        return new Node[]{};
                    case POINT_2:
                        return new Node[]{};
                    case POINT_3:
                        Node[] nodes = getPathNodes(astrobee,end, start);
                        ArrayUtils.reverse(nodes);
                        return nodes;
                    case POINT_5:
                        return new Node[]{};
                    case POINT_6:
                        return new Node[]{};
                }
                break;
            case POINT_5:
                switch (end.id) {
                    case START:
                    case GOAL:
                    case POINT_1:
                    case POINT_2:
                    case POINT_3:
                    case POINT_4:
                    case POINT_6:
                        return new Node[]{};
                }
                break;
            case POINT_6:
                Node[] nodes = getPathNodes(astrobee,end, start);
                ArrayUtils.reverse(nodes);
                return nodes;
        }
        throw new IllegalStateException("Unexpected value: " + start.id + " and " + end.id);
    }
}