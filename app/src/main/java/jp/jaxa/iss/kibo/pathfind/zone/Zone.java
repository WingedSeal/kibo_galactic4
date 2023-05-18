package jp.jaxa.iss.kibo.pathfind.zone;

public class Zone {


    /**
     * Minimum distance on each axis that Astrobee should keep between zone's walls
     */
    private static final double THRESHOLD = 0.16;

    public ZoneType zoneType;
    public double xMin;
    public double yMin;
    public double zMin;
    public double xMax;
    public double yMax;
    public double zMax;

    public Zone(ZoneType zoneType, double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        this.zoneType = zoneType;
        this.xMin = xMin;
        this.yMin = yMin;
        this.zMin = zMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.zMax = zMax;
    }

    public static final Zone keepOut1 = new Zone(ZoneType.KEEP_OUT,
            10.783 - THRESHOLD, -9.8899 - THRESHOLD, 4.8385 - THRESHOLD,
            11.071 + THRESHOLD, -9.6929 + THRESHOLD, 5.0665 + THRESHOLD
    );
    public static final Zone keepOut2 = new Zone(ZoneType.KEEP_OUT,
            10.8652 - THRESHOLD, -9.0734 - THRESHOLD, 4.3861 - THRESHOLD,
            10.9628 + THRESHOLD, -8.7314 + THRESHOLD, 4.6401 + THRESHOLD
    );
    public static final Zone keepOut3 = new Zone(ZoneType.KEEP_OUT,
            10.185 - THRESHOLD, -8.3826 - THRESHOLD, 4.1475 - THRESHOLD,
            11.665 + THRESHOLD, -8.2826 + THRESHOLD, 4.6725 + THRESHOLD
    );
    public static final Zone keepOut4 = new Zone(ZoneType.KEEP_OUT,
            10.7955 - THRESHOLD, -8.0635 - THRESHOLD, 5.1055 - THRESHOLD,
            11.3525 + THRESHOLD, -7.7305 + THRESHOLD, 5.1305 + THRESHOLD
    );
    public static final Zone keepOut5 = new Zone(ZoneType.KEEP_OUT,
            10.563 - THRESHOLD, -7.1449 - THRESHOLD, 4.6544 - THRESHOLD,
            10.709 + THRESHOLD, -6.8099 + THRESHOLD, 4.8164 + THRESHOLD
    );
    public static final Zone keepIn1 = new Zone(ZoneType.KEEP_IN,
            10.3 + THRESHOLD, -10.2 + THRESHOLD, 4.32 + THRESHOLD,
            11.55 - THRESHOLD, -6.0 - THRESHOLD, 5.57 - THRESHOLD
    );
    public static final Zone keepIn2 = new Zone(ZoneType.KEEP_IN,
            9.5 + THRESHOLD, -10.5 + THRESHOLD, 4.02 + THRESHOLD,
            10.5 - THRESHOLD, -9.6 - THRESHOLD, 4.8 - THRESHOLD
    );
}
