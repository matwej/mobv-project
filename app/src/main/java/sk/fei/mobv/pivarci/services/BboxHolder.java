package sk.fei.mobv.pivarci.services;

public class BboxHolder {

    private static double ONE_LAT_IN_M = 110500;
    private double ONE_LON_IN_M = 75000;

    private Double minLat = 0.0;
    private Double maxLat = 0.0;
    private Double minLon = 0.0;
    private Double maxLon = 0.0;

    public BboxHolder() {
    }

    public void calculate(Double lat, Double lon, int dist) {
        ONE_LON_IN_M = 111000 * Math.cos(deg2rad(lat));
        Double latDiff = dist / ONE_LAT_IN_M;
        minLat = lat - latDiff;
        maxLat = lat + latDiff;
        Double lonDiff = dist / ONE_LON_IN_M;
        minLon = lon - lonDiff;
        maxLon = lon + lonDiff;
    }

    public Double getMinLat() {
        return minLat;
    }

    public Double getMaxLat() {
        return maxLat;
    }

    public Double getMinLon() {
        return minLon;
    }

    public Double getMaxLon() {
        return maxLon;
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    public static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
