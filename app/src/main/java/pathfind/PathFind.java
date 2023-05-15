package pathfind;

public class PathFind {
    
}

class StaticPath {
    public static void getPathNodes(PointOfInterest start, PointOfInterest end) {
        switch (start.id) {
            case POINT_1:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + start.id);
        }
    }
}