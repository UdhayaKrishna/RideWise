package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Ride;

public class DefaultFareStrategy implements FareStrategy {
    private static final double BASE_FARE = 50.0;
    private static final double RATE_PER_UNIT_DISTANCE = 10.0;

    @Override
    public double calculateFare(Ride ride) {
        return (BASE_FARE + (ride.getDistance() * RATE_PER_UNIT_DISTANCE)) * ride.getVehicleType().getBaseFactor();
    }
}
