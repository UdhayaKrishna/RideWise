package com.airtribe.ridewise.model;

public enum VehicleType {
    BIKE(1.0),
    AUTO(1.5),
    CAR(2.0);

    private final double baseFactor;

    VehicleType(double baseFactor) {
        this.baseFactor = baseFactor;
    }

    public double getBaseFactor() { return baseFactor; }
}
