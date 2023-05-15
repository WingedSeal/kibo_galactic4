package pathfind.zone;

public class Zone {

    public ZoneType zoneType;
    double xMin;
    double yMin;
    double zMin;
    double xMax;
    double yMax;
    double zMax;

    public Zone(ZoneType zoneType, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        this.zoneType = zoneType;
        this.xMin = xMin;
        this.yMin = yMin;
        this.zMin = zMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.zMax = zMax;
    }

    public static final Zone keepOut1 = new Zone(ZoneType.KEEP_OUT, 10.783, -9.8899, 4.8385, 11.071, -9.6929, 5.0665);
    public static final Zone keepOut2 = new Zone(ZoneType.KEEP_OUT, 10.8652, -9.0734, 4.3861, 10.9628, -8.7314, 4.6401);
    public static final Zone keepOut3 = new Zone(ZoneType.KEEP_OUT, 10.185, -8.3826, 4.1475, 11.665, -8.2826, 4.6725);
    public static final Zone keepOut4 = new Zone(ZoneType.KEEP_OUT, 10.7955, -8.0635, 5.1055, 11.3525, -7.7305, 5.1305);
    public static final Zone keepOut5 = new Zone(ZoneType.KEEP_OUT, 10.563, -7.1449, 4.6544, 10.709, -6.8099, 4.8164);
    public static final Zone keepIn1 = new Zone(ZoneType.KEEP_IN, 10.3, -10.2, 4.32, 11.55, -6.0, 5.57);
    public static final Zone keepIn2 = new Zone(ZoneType.KEEP_IN, 9.5, -10.5, 4.02, 10.5, -9.6, 4.8);
}
