package jp.jaxa.iss.kibo.rpc.thailand.pathfind;

public class TargetPoint extends PathFindNode {
    private final int pointNumber;
    private final int score;


    public TargetPoint(double x, double y, double z, POI_Id id, int pointNumber, int score) {
        super(x, y, z, id);
        this.pointNumber = pointNumber;
        this.score = score;
    }

    public int getScore() {
        return score;
    }
    

    private static final TargetPoint[] TARGET_POINTS = {
            new TargetPoint(11.2681d-0.02d, -9.8d, 5.1125d+0.01d, POI_Id.POINT_1, 1, 30), // change point 1
            new TargetPoint(10.612d, -9.0709d,4.715  , POI_Id.POINT_2, 2, 20),
            new TargetPoint(10.71d, -7.7d,4.715, POI_Id.POINT_3, 3, 40),
            new TargetPoint(10.5489, -6.8615,5.065 , POI_Id.POINT_4, 4, 20),
            new TargetPoint(10.94,-8.524172,4.86059/*11.102d, -8.0304d, 5.9076d-0.810d*/, POI_Id.POINT_5, 5, 30),
            new TargetPoint(12.023d - 0.85d, -8.989d, 4.8305, POI_Id.POINT_6, 6, 30),
            new TargetPoint(11.369, -8.5518, 4.95d, POI_Id.POINT_7, 7, 20),
    };

    private static final TargetPoint[] REAL_TARGET_POINTS = {
                new TargetPoint(11.2746d, -9.92284d, 5.2988d, POI_Id.REALPOINT_1, 1, 30),
                new TargetPoint(10.612d, -9.0709d, 4.48d, POI_Id.REALPOINT_2, 2, 20),
                new TargetPoint(10.71d, -7.7d, 4.48d, POI_Id.REALPOINT_3, 3, 40),
                new TargetPoint(10.51d, -6.7185d, 5.1804, POI_Id.REALPOINT_4, 4, 20),
    };


    public static TargetPoint getRealTargetPoint(int pointNumber) {
        return REAL_TARGET_POINTS[pointNumber - 1];
    }

    public static TargetPoint getTargetPoint(int pointNumber) {
        return TARGET_POINTS[pointNumber - 1];
    }

    public int getPointNumber() {
        return pointNumber;
    }
}
