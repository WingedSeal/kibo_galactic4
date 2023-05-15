package pathfind;

public class PointOfInterest extends Node {
    public PointOfInterest(double x, double y, double z, POI_Id id) {
        super(x, y, z);
        this.id = id;
    }
    public static final PointOfInterest QR_CODE = new PointOfInterest(11.381944, -8.566172, 3.76203, POI_Id.QR_CODE);

    public final POI_Id id;
}
