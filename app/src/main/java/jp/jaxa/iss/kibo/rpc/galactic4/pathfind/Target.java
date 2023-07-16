package jp.jaxa.iss.kibo.rpc.galactic4.pathfind;

public class Target extends PointOfInterest {
    public Target(double x, double y, double z, POI_Id id, double radius, int targetNumber, int score) {
        super(x, y, z, id);
        this.radius = radius;
        this.targetNumber = targetNumber;
        this.score = score;
    }

    private final double radius;
    private final int targetNumber;
    private final int score;

    public double getRadius() {
        return radius;
    }

    public int getTargetNumber() {
        return targetNumber;
    }

    public int getScore() {
        return score;
    }

    private static final Target[] TARGETS = {
            new Target(11.2625, -10.58, 5.3625, POI_Id.TARGET_1, 0.04, 1, 30),
            new Target(10.513384, -9.085172, 3.76203, POI_Id.TARGET_2, 0.05, 2, 20),
            new Target(10.6031, -7.71007, 3.76093, POI_Id.TARGET_3, 0.03, 3, 40),
            new Target(9.866984, -6.673972, 5.09531, POI_Id.TARGET_4, 0.05, 4, 20),
            new Target(11.102, -8.0304, 5.9076, POI_Id.TARGET_5, 0.04, 5, 30),
            new Target(12.023, -8.989, 4.8305, POI_Id.TARGET_6, 0.04, 6, 30)
    };

    public static Target getTarget(int targetNumber) {
        return TARGETS[targetNumber - 1];
    }

}
