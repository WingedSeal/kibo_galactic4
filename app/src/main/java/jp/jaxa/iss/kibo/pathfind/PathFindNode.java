package jp.jaxa.iss.kibo.pathfind;


/**
 * A special Point of interest that support pathfinding
 */
public class PathFindNode extends PointOfInterest {
    public PathFindNode(double x, double y, double z, POI_Id id) {
        super(x, y, z, id);

    }

    public static final PathFindNode START = new PathFindNode(9.815, -9.806, 4.293, POI_Id.START);
    public static final PathFindNode GOAL = new PathFindNode(11.2746, -6.7607, 4.9654, POI_Id.GOAL);
}
