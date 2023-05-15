package pathfind;

public class TargetPoint extends PathFindNode {
    public TargetPoint(double x, double y, double z, POI_Id id) {
        super(x, y, z, id);
    }

    private static final TargetPoint[] TARGET_POINTS = {
            new TargetPoint(11.2746, -9.92284, 5.2988, POI_Id.POINT_1),
            new TargetPoint(10.612, -9.0709, 4.48, POI_Id.POINT_2),
            new TargetPoint(10.71, -7.7, 4.48, POI_Id.POINT_3),
            new TargetPoint(10.51, -6.7185, 5.1804, POI_Id.POINT_4),
            new TargetPoint(11.114, -7.9756, 5.3393, POI_Id.POINT_5),
            new TargetPoint(11.355, -8.9929, 4.7818, POI_Id.POINT_6),
            new TargetPoint(11.369, -8.5518, 4.48, POI_Id.POINT_7),
    };

    public static TargetPoint getTargetPoint(int pointNumber) {
        return TARGET_POINTS[pointNumber];
    }
}
