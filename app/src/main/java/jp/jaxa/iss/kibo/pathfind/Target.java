package jp.jaxa.iss.kibo.pathfind;

public class Target extends PointOfInterest {
    public Target(double x, double y, double z, POI_Id id, double radius) {
        super(x, y, z, id);
        this.radius = radius;
    }

    private final double radius;

    public double getRadius() {
        return radius;
    }

    private static final Target[] TARGETS = {
            new Target(11.2625, -10.58, 5.3625, POI_Id.TARGET_1, 0.04),
            new Target(10.513384, -9.085172, 3.76203, POI_Id.TARGET_2, 0.05),
            new Target(10.6031, -7.71007, 3.76093, POI_Id.TARGET_3, 0.03),
            new Target(9.866984, -6.673972, 5.09531, POI_Id.TARGET_4, 0.05),
            new Target(11.102, -8.0304, 5.9076, POI_Id.TARGET_5, 0.04),
            new Target(12.023, -8.989, 4.8305, POI_Id.TARGET_6, 0.04)
    };

    public static Target getTarget(int targetNumber) {
        return TARGETS[targetNumber];
    }
}