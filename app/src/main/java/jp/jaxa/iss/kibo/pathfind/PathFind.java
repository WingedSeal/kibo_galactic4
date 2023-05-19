package jp.jaxa.iss.kibo.pathfind;

import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.pathfind.zone.Zone;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.utils.Line;
import org.apache.commons.lang.ArrayUtils;
import jp.jaxa.iss.kibo.rpc.defaultapk.Astrobee;

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

    private static OrientedNode findOrientedNodeX(double y, double z, Node start, Node end, Node pointedNode) {
        return OrientedNode.fromNode(findNodeX(y, z, start, end), pointedNode);
    }

    private static OrientedNode findOrientedNodeY(double x, double z, Node start, Node end, Node pointedNode) {
        return OrientedNode.fromNode(findNodeY(x, z, start, end), pointedNode);
    }

    private static OrientedNode findOrientedNodeZ(double x, double y, Node start, Node end, Node pointedNode) {
        return OrientedNode.fromNode(findNodeZ(x, y, start, end), pointedNode);
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
        for (Node node : getPathNodes(from, to)) {
            if (node instanceof OrientedNode) {
                OrientedNode orientedNode = (OrientedNode)node;
                if (orientedNode.getPointedNode().equals(PointOfInterest.QR_CODE) && !astrobee.isQrScanned()) {
                    api.moveTo(orientedNode, orientedNode.getOrientation(), printRobotPosition);
                    switch (orientedNode.getCameraMode()) {
                        case DOCK:
                            astrobee.attemptScanQRDock(false, 5);
                            break;
                        case NAV:
                            astrobee.attemptScanQRNav(false, 5);
                            break;
                        default:
                            astrobee.attemptScanQRNav(false, 5);
                            break;
                    }
                }
                else api.moveTo(orientedNode, orientation, printRobotPosition);

            }
            else api.moveTo(node, orientation, printRobotPosition);
        }
        api.moveTo(to, orientation, printRobotPosition);
    }

    public static double estimateTotalDistance(PathFindNode from, PathFindNode to) {
        Node[] nodes = getPathNodes(from, to);
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

    public static double[] estimatePathDistances(PathFindNode from, PathFindNode to) {
        Node[] nodes = getPathNodes(from, to);
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
    private static Node[] getPathNodes(PathFindNode start, PathFindNode end) {
        if (start.id == end.id) {
            return new Node[]{};
        }
        switch (start.id) {
            case START:
                switch (end.id) {
                    case POINT_1:
                        return new Node[]{
                                findNodeZ((Zone.keepOut1.xMin + Zone.keepOut1.xMax)/2, Zone.keepIn1.yMin, start, end),
                        };
                    case POINT_2:
                        return new Node[]{};
                    case POINT_3:
                        Node bottomNode = findNodeX(Zone.keepOut3.yMin, Zone.keepOut3.zMax, start, end);
                        return new Node[]{
                                findNodeZ(Zone.keepIn2.xMax, Zone.keepIn1.yMax, start, bottomNode),
                                bottomNode,
                                findNodeX(Zone.keepOut3.yMax, Zone.keepOut3.zMax, bottomNode, end)
                        };
                    case POINT_4:
                        Node edgeNode = findNodeZ(Zone.keepIn2.xMax, Zone.keepIn1.yMax, start, end);
                        return new Node[]{
                                edgeNode,
                                findNodeX(Zone.keepOut3.yMin, Zone.keepOut3.zMax, edgeNode, end)
                        };
                    case POINT_5:
                        return new Node[]{};
                    case POINT_6: // remove node (KOZ not violated)
                        return new Node[]{};
                }
                break;
            case GOAL:
                switch (end.id) {
                    case START:
                        break;
                    case POINT_1: // must add one anchor point
                        return new Node[]{
                                findOrientedNodeX(Zone.keepOut3.yMax, Zone.keepOut3.zMax, start, end, PointOfInterest.QR_CODE)
                        };
                    case POINT_2:
                    return new Node[]{
                                findNodeY(Zone.keepOut4.xMin, Zone.keepOut3.zMax, start, end)
                        };
                    case POINT_3:
                    case POINT_4:
                    case POINT_5: // Point 5 is unsure of keep out 4
                        return new Node[]{};
                    case POINT_6:
                        return new Node[]{
                                findNodeX(Zone.keepOut3.yMin, Zone.keepOut3.zMax, start, end)
                        };
                }
                break;
            case POINT_1:
                switch (end.id) {
                    case START:
                    case GOAL:
                        Node[] nodes = getPathNodes(end, start);
                        ArrayUtils.reverse(nodes);
                        return nodes;
                    case POINT_2: // unsure of keep out 2
                        return new Node[]{};
                    case POINT_3:
                        return new Node[]{
                                findOrientedNodeX(Zone.keepOut3.yMax, Zone.keepOut3.zMax, start, end, PointOfInterest.QR_CODE)
                        };
                    case POINT_4:
                    case POINT_5:
                    case POINT_6:
                        return new Node[]{};
                }
                break;
            case POINT_2:
                switch (end.id) {
                    case START:
                    case GOAL:
                    case POINT_1:
                        Node[] nodes = getPathNodes(end, start);
                        ArrayUtils.reverse(nodes);
                        return nodes;
                    case POINT_3:
                        return new Node[]{
                                findOrientedNodeX((Zone.keepOut3.yMin + Zone.keepOut3.yMax)/2, Zone.keepOut3.zMax, start, end, PointOfInterest.QR_CODE)
                        };
                    case POINT_4:
                        return new Node[]{
                                findNodeX(Zone.keepOut5.yMin, Zone.keepOut4.zMax, start, end)
                        };
                    case POINT_5:
                        return new Node[]{};
                    case POINT_6:
                        return new Node[]{
                                findNodeZ((Zone.keepOut2.xMin + Zone.keepOut2.xMax)/2, Zone.keepOut2.yMin, start, end)
                        };
                }
                break;
            case POINT_3:
                switch (end.id) {
                    case START:
                    case GOAL:
                    case POINT_1:
                    case POINT_2:
                        Node[] nodes = getPathNodes(end, start);
                        ArrayUtils.reverse(nodes);
                        return nodes;
                    case POINT_4:
                        return new Node[]{
                                findNodeX(Zone.keepOut4.yMin, Zone.keepOut4.zMax, start, end)
                        };
                    case POINT_5:
                        return new Node[]{
                                findNodeX(Zone.keepOut4.yMin, (Zone.keepOut4.zMin + Zone.keepOut4.zMax) / 2, start, end)
                        };
                    case POINT_6:
                        return new Node[]{
                                findOrientedNodeX((Zone.keepOut3.yMax + Zone.keepOut3.yMin)/2, Zone.keepOut3.zMax, start, end, PointOfInterest.QR_CODE)
                        };
                }
                break;
            case POINT_4:
                switch (end.id) {
                    case START:
                    case GOAL:
                    case POINT_1:
                    case POINT_2:
                    case POINT_3:
                        Node[] nodes = getPathNodes(end, start);
                        ArrayUtils.reverse(nodes);
                        return nodes;
                    case POINT_5:
                        return new Node[]{};
                    case POINT_6:
                        return new Node[]{
                                findOrientedNodeX(Zone.keepOut3.yMin, Zone.keepOut4.zMax, start, end, PointOfInterest.QR_CODE)
                        };
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
                        Node[] nodes = getPathNodes(end, start);
                        ArrayUtils.reverse(nodes);
                        return nodes;
                    case POINT_6:
                        return new Node[]{};
                }
                break;
            case POINT_6:
                Node[] nodes = getPathNodes(end, start);
                ArrayUtils.reverse(nodes);
                return nodes;
        }
        throw new IllegalStateException("Unexpected value: " + start.id + " and " + end.id);
    }
}