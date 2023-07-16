package jp.jaxa.iss.kibo.rpc.galactic4.pathfind;

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
            new TargetPoint(11.2681d, -9.73d - 0.10d, 5.3625d - 0.28d, POI_Id.POINT_1, 1, 30),
            new TargetPoint(10.513384d, -9.085172d, 3.76203d + 0.96d, POI_Id.POINT_2, 2, 20),
            new TargetPoint(10.6031d, -7.71007d, 3.76093d + 0.96d, POI_Id.POINT_3, 3, 40),
            new TargetPoint(10.402d, -6.8406d, 5.0825, POI_Id.POINT_4, 4, 20),
            new TargetPoint(11.114, -7.9756, 5.9076d - 0.810d/*11.102d, -8.0304d, 5.9076d-0.810d*/, POI_Id.POINT_5, 5, 30),
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
