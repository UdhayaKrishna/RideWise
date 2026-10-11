package com.airtribe.ridewise.service;

import com.airtribe.ridewise.exception.EntityNotFoundException;
import com.airtribe.ridewise.exception.InvalidRideStateException;
import com.airtribe.ridewise.exception.NoDriverAvailableException;
import com.airtribe.ridewise.model.*;
import com.airtribe.ridewise.strategy.FareStrategy;
import com.airtribe.ridewise.strategy.RideMatchingStrategy;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RideService {
    private final Map<String, Ride> rideStorage = new HashMap<>();
    private final Map<String, FareReceipt> receiptStorage = new HashMap<>();

    private final DriverService driverService;
    private RideMatchingStrategy matchingStrategy;
    private FareStrategy fareStrategy;

    public RideService(DriverService driverService, RideMatchingStrategy matchingStrategy, FareStrategy fareStrategy) {
        this.driverService = driverService;
        this.matchingStrategy = matchingStrategy;
        this.fareStrategy = fareStrategy;
    }

    // Dynamic switching capabilities ensuring OCP & Strategy Compliance
    public void setMatchingStrategy(RideMatchingStrategy matchingStrategy) {
        this.matchingStrategy = matchingStrategy;
    }

    public void setFareStrategy(FareStrategy fareStrategy) {
        this.fareStrategy = fareStrategy;
    }

    public Ride requestRide(Rider rider, Location destination, VehicleType vehicleType) {
        String rideId = IdGenerator.generateRideId();
        Ride ride = new Ride(rideId, rider, destination, vehicleType);

        List<Driver> availableDrivers = driverService.getAvailableDrivers();
        Driver matchedDriver = matchingStrategy.findDriver(rider, availableDrivers);

        if (matchedDriver == null) {
            ride.setStatus(RideStatus.CANCELLED);
            rideStorage.put(rideId, ride);
            throw new NoDriverAvailableException("Matching failure: No available drivers found within the system radius.");
        }

        matchedDriver.setAvailable(false);
        ride.setDriver(matchedDriver);
        ride.setStatus(RideStatus.ASSIGNED);

        rideStorage.put(rideId, ride);
        return ride;
    }

    public FareReceipt completeRide(String rideId) {
        Ride ride = rideStorage.get(rideId);
        if (ride == null) {
            throw new EntityNotFoundException("Ride lifecycle error: Transaction ID '" + rideId + "' does not exist.");
        }
        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new InvalidRideStateException("Action denied: Cannot complete ride '" + rideId + "' because its current state is " + ride.getStatus());
        }

        ride.setStatus(RideStatus.COMPLETED);

        double amount = fareStrategy.calculateFare(ride);
        FareReceipt receipt = new FareReceipt(rideId, amount);
        receiptStorage.put(rideId, receipt);

        Driver driver = ride.getDriver();
        driver.setCurrentLocation(ride.getDestination());
        driver.incrementRides();
        driver.setAvailable(true);

        return receipt;
    }

    public List<Ride> getAllRides() {
        return new ArrayList<>(rideStorage.values());
    }
}
