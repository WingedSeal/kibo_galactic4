package pathfind;

public class PointOfInterest extends Node {
    public PointOfInterest(double x, double y, double z, POI_Id id) {
        super(x, y, z);
        this.id = id;
    }

    public final POI_Id id;

    public static final PointOfInterest START = new PointOfInterest(9.815, -9.806, 4.293, POI_Id.START);
    public static final PointOfInterest GOAL = new PointOfInterest(11.2746, -6.7607, 4.9654, POI_Id.GOAL);
    public static final PointOfInterest QR_CODE = new PointOfInterest(11.381944, -8.566172, 3.76203, POI_Id.QR_CODE);
}
