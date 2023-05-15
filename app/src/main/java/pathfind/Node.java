package pathfind;

import gov.nasa.arc.astrobee.types.Point;

/**
 * A special Point, Astrobee can pass through while pathfinding
 */
public class Node extends Point {
    public Node(double x, double y, double z) {
        super(x, y, z);
    }
}
