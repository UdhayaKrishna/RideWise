package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Ride;

public class PeakHourFareStrategy implements FareStrategy {
    private static final double BASE_FARE = 50.0;
    private static final double RATE_PER_UNIT_DISTANCE = 10.0;
    private static final double PEAK_SURGE_MULTIPLIER = 1.5;

    @Override
    public double calculateFare(Ride ride) {
        double standardFare = (BASE_FARE + (ride.getDistance() * RATE_PER_UNIT_DISTANCE)) * ride.getVehicleType().getBaseFactor();
        return standardFare * PEAK_SURGE_MULTIPLIER;
    }
}
