package jp.jaxa.iss.kibo.rpc.thailand.pathfind;

import gov.nasa.arc.astrobee.types.Point;

import java.util.Objects;

/**
 * A special Point, Astrobee can pass through while pathfinding
 */
public class Node extends Point {
    public Node(double x, double y, double z) {
        super(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node node = (Node) obj;
            return (this.getX() == node.getX() &&
                    this.getY() == node.getY() &&
                    this.getZ() == node.getZ());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getZ());
    }
}
