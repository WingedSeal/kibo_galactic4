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
            new TargetPoint(11.2746, -9.92284, 5.2988, POI_Id.POINT_1, 1, 30),
            new TargetPoint(10.612, -9.0709, 4.48, POI_Id.POINT_2, 2, 20),
            new TargetPoint(10.71, -7.7, 4.48, POI_Id.POINT_3, 3, 40),
            new TargetPoint(10.51, -6.7185, 5.1804, POI_Id.POINT_4, 4, 20),
            new TargetPoint(11.114, -7.9756, 5.3393, POI_Id.POINT_5, 5, 30),
            new TargetPoint(11.355, -8.9929, 4.7818, POI_Id.POINT_6, 6, 30),
            new TargetPoint(11.369, -8.5518, 4.48, POI_Id.POINT_7, 7 , 20),
    };

    public static TargetPoint getTargetPoint(int pointNumber) {
        return TARGET_POINTS[pointNumber - 1];
    }

    public int getPointNumber() {
        return pointNumber;
    }
}
