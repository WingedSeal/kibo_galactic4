package jp.jaxa.iss.kibo.pathfind;

public class TargetPoint extends PathFindNode {
    private final int pointNumber;
    private final int score;


    public TargetPoint(double x, double y, double z, POI_Id id, int pointNumber, int score) {
        super(x, y, z, id);
        this.pointNumber = pointNumber;
        this.score = score;
    }

    public int getScore() {return score;};


    private static final TargetPoint[] TARGET_POINTS = {
            new TargetPoint(11.2681, -9.73-0.05d, 5.3625-0.28d, POI_Id.POINT_1, 1, 30),
            new TargetPoint(10.513384, -9.085172, 3.76203+0.90d, POI_Id.POINT_2, 2, 20),
            new TargetPoint(10.6031, -7.71007, 3.76093+0.80d, POI_Id.POINT_3, 3, 40),
            new TargetPoint(10.402, -6.8406, 5.0825, POI_Id.POINT_4, 4, 20),
            new TargetPoint(11.102, -8.0304, 5.9076-0.828d, POI_Id.POINT_5, 5, 30),
            new TargetPoint(12.023-0.85d, -8.989, 4.8305, POI_Id.POINT_6, 6, 30),
            new TargetPoint(11.369, -8.5518, 4.48, POI_Id.POINT_7, 7 , 20),
    };

    public static TargetPoint getTargetPoint(int pointNumber) {
        return TARGET_POINTS[pointNumber - 1];
    }

    public int getPointNumber() {
        return pointNumber;
    }
}
