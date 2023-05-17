package jp.jaxa.iss.kibo.pathfind;

import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.pathfind.zone.Zone;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.utils.Line;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NotImplementedException;

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

    /**
     * Move to the PathFindNode using approximated the shortest path
     *
     * @param api         KiboRpcApi to call moveTo
     * @param from        current position of Astrobee
     * @param to          position to move to
     * @param orientation orientation parameter of api.moveTo
     */
    public static void pathFindMoveTo(KiboRpcApi api, PathFindNode from, PathFindNode to, Quaternion orientation) {
        pathFindMoveTo(api, from, to, orientation, false);
    }


    /**
     * Move to the PathFindNode using approximated the shortest path
     *
     * @param api                KiboRpcApi to call moveTo
     * @param from               current position of Astrobee
     * @param to                 position to move to
     * @param orientation        orientation parameter of api.moveTo
     * @param printRobotPosition whether to print position
     */
    public static void pathFindMoveTo(KiboRpcApi api, PathFindNode from, PathFindNode to, Quaternion orientation, boolean printRobotPosition) {
        for (Node node : getPathNodes(from, to)) {
            api.moveTo(node, orientation, printRobotPosition);
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
                    case GOAL:
                        return new Node[]{}; // TODO:
                    case POINT_1:
                        return new Node[]{
                                findNodeZ(Zone.keepOut1.xMin, Zone.keepIn1.yMin, start, end),
                                findNodeZ(Zone.keepOut1.xMax, Zone.keepIn1.yMin, start, end),
                        };
                    case POINT_2:
                        return new Node[]{
                                findNodeZ(Zone.keepIn2.xMin, Zone.keepIn1.yMax, start, end)
                        };
                    case POINT_3:
                        Node bottomNode = findNodeX(Zone.keepOut3.yMin, Zone.keepOut3.zMax, start, end);
                        return new Node[]{
                                findNodeZ(Zone.keepIn2.xMax, Zone.keepIn1.yMax, start, end),
                                bottomNode,
                                findNodeX(Zone.keepOut3.yMax, Zone.keepOut3.zMax, bottomNode, end)
                        };
                    case POINT_4:
                    case POINT_5:
                        Node edgeNode = findNodeZ(Zone.keepIn2.xMax, Zone.keepIn1.yMax, start, end);
                        return new Node[]{
                                edgeNode,
                                findNodeX(Zone.keepOut3.yMin, Zone.keepOut3.zMax, edgeNode, end)
                        };
                    case POINT_6: // unsure of keep out 2
                        return new Node[]{
                                findNodeZ(Zone.keepIn2.xMax, Zone.keepIn1.yMax, start, end)
                        };
                    case POINT_7:
                        throw new NotImplementedException("I haven't implemented QR"); // FIXME: Actually implement QR

                }
                break;
            case GOAL:
                switch (end.id) {
                    case START:
                        Node[] nodes = getPathNodes(end, start);
                        ArrayUtils.reverse(nodes);
                        return nodes;
                    case POINT_1:
                        return new Node[]{
                                findNodeX(Zone.keepOut4.yMin, Zone.keepOut4.zMin, start, end)
                        };
                    case POINT_2:
                        return new Node[]{
                                findNodeX(Zone.keepOut3.yMax, Zone.keepOut3.zMax, start, end),
                                findNodeX(Zone.keepOut3.yMin, Zone.keepOut3.zMax, start, end),
                                findNodeY(Zone.keepOut2.xMin, Zone.keepOut2.zMax, start, end)
                        };
                    case POINT_3:
                    case POINT_4:
                    case POINT_5: // Point 5 is unsure of keep out 4
                        return new Node[]{};
                    case POINT_6:
                        return new Node[]{
                                findNodeX(Zone.keepOut3.yMin, Zone.keepOut3.zMax, start, end)
                        };
                    case POINT_7:
                        throw new NotImplementedException("I haven't implemented QR"); // FIXME: Actually implement QR
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
                        return new Node[]{
                                findNodeX(Zone.keepOut1.yMax, Zone.keepOut1.zMax, start, end)
                        };
                    case POINT_3:
                        return new Node[]{
                                findNodeX(Zone.keepOut3.yMax, Zone.keepOut3.zMax, start, end)
                        };
                    case POINT_4:
                    case POINT_5:
                    case POINT_6:
                        return new Node[]{};
                    case POINT_7:
                        throw new NotImplementedException("I haven't implemented QR"); // FIXME: Actually implement QR
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
                                findNodeX(Zone.keepOut3.yMin, Zone.keepOut3.zMax, start, end),
                                findNodeX(Zone.keepOut3.yMax, Zone.keepOut3.zMax, start, end)
                        };
                    case POINT_4:
                        return new Node[]{
                                findNodeX(Zone.keepOut3.yMin, Zone.keepOut3.zMax, start, end)
                        };
                    case POINT_5:
                        return new Node[]{
                                findNodeY(Zone.keepOut2.xMin, Zone.keepOut2.zMax, start, end)
                        };
                    case POINT_6:
                        return new Node[]{
                                findNodeZ(Zone.keepOut2.xMin, Zone.keepOut2.yMin, start, end),
                                findNodeZ(Zone.keepOut2.xMax, Zone.keepOut2.yMin, start, end)
                        };
                    case POINT_7:
                        throw new NotImplementedException("I haven't implemented QR"); // FIXME: Actually implement QR
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
                        return new Node[]{}; // FIXME: HIT KEEP OUT 4
                    case POINT_5:
                        return new Node[]{
                                findNodeX(Zone.keepOut4.yMin, Zone.keepOut4.zMin, start, end),
                                findNodeX(Zone.keepOut4.yMin, Zone.keepOut4.zMax, start, end),
                        };
                    case POINT_6:
                        return new Node[]{
                                findNodeX(Zone.keepOut3.yMax, Zone.keepOut4.zMax, start, end),
                                findNodeX(Zone.keepOut3.yMin, Zone.keepOut4.zMax, start, end)
                        };
                    case POINT_7:
                        return new Node[]{};
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
                        return new Node[]{};
                    case POINT_7:
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
                        Node[] nodes = getPathNodes(end, start);
                        ArrayUtils.reverse(nodes);
                        return nodes;
                    case POINT_6:
                        return new Node[]{};
                    case POINT_7:
                        return new Node[]{};
                }
                break;
            case POINT_6:
                switch (end.id) {
                    case START:
                    case GOAL:
                    case POINT_1:
                    case POINT_2:
                    case POINT_3:
                    case POINT_4:
                    case POINT_5:
                        Node[] nodes = getPathNodes(end, start);
                        ArrayUtils.reverse(nodes);
                        return nodes;
                    case POINT_7:
                        return new Node[]{};
                }
                break;
            case POINT_7:
                Node[] nodes = getPathNodes(end, start);
                ArrayUtils.reverse(nodes);
                return nodes;
        }
        throw new IllegalStateException("Unexpected value: " + start.id + " and " + end.id);
    }
}