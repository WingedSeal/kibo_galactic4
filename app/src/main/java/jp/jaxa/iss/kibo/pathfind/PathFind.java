package jp.jaxa.iss.kibo.pathfind;

import jp.jaxa.iss.kibo.utils.Line;
import org.apache.commons.lang.ArrayUtils;
import jp.jaxa.iss.kibo.pathfind.zone.Zone;

public class PathFind {

    private static Node findNodeX(double y, double z, PathFindNode start, PathFindNode end) {
        return new Node(Line.findOptimizedPosition(start, end, null, y, z), y, z);
    }

    private static Node findNodeY(double x, double z, PathFindNode start, PathFindNode end) {
        return new Node(x, Line.findOptimizedPosition(start, end, x, null, z), z);
    }

    private static Node findNodeZ(double x, double y, PathFindNode start, PathFindNode end) {
        return new Node(x, y, Line.findOptimizedPosition(start, end, x, y, null));
    }

    /**
     * Find Nodes(ending Node not included) in between 2 PathFindNode Astrobee needs to move through to reach the end PathFindNode
     *
     * @param start Current position of Astrobee
     * @param end   PathFindNode Astrobee want to go to
     * @return Nodes needed to travel
     */
    public static Node[] getPathNodes(PathFindNode start, PathFindNode end) {
        if (start.id == end.id) {
            throw new IllegalArgumentException("Start POI is the same as end POI, cannot path find");
        }
        switch (start.id) {
            case START:
                switch (end.id) {
                    case GOAL:
                        return new Node[]{};
                    case POINT_1:
                        return new Node[]{
                                findNodeZ(Zone.keepOut1.xMin, Zone.keepIn1.yMin, start, end)
                        };
                    case POINT_2:
                        return new Node[]{
                                findNodeZ(Zone.keepIn1.xMax, Zone.keepIn1.yMax, start, end)
                        };
                    case POINT_3:
                        return new Node[]{};
                    case POINT_4:
                        return new Node[]{};
                    case POINT_5:
                        return new Node[]{};
                    case POINT_6:
                        return new Node[]{};
                    case POINT_7:
                        return new Node[]{};
                }
                break;
            case GOAL:
                switch (end.id) {
                    case START:
                        Node[] nodes = getPathNodes(end, start);
                        ArrayUtils.reverse(nodes);
                        return nodes;
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
                    case POINT_7:
                        return new Node[]{};
                }
                break;
            case POINT_1:
                switch (end.id) {
                    case START:
                    case GOAL:
                        Node[] nodes = getPathNodes(end, start);
                        ArrayUtils.reverse(nodes);
                        return nodes;
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
                    case POINT_7:
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
                        return new Node[]{};
                    case POINT_4:
                        return new Node[]{};
                    case POINT_5:
                        return new Node[]{};
                    case POINT_6:
                        return new Node[]{};
                    case POINT_7:
                        return new Node[]{};
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
                        return new Node[]{};
                    case POINT_5:
                        return new Node[]{};
                    case POINT_6:
                        return new Node[]{};
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