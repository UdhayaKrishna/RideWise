package com.airtribe.ridewise.model;

public class Location {
    private final double latitude;
    private final double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public double distanceTo(Location other) {
        return Math.sqrt(Math.pow(this.latitude - other.latitude, 2) +
                Math.pow(this.longitude - other.longitude, 2));
    }
}