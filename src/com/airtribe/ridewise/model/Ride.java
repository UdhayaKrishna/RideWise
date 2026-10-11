package com.airtribe.ridewise.model;

public class Ride {
    private final String id;
    private final Rider rider;
    private Driver driver;
    private final Location destination;
    private final double distance;
    private final VehicleType vehicleType;
    private RideStatus status;

    public Ride(String id, Rider rider, Location destination, VehicleType vehicleType) {
        this.id = id;
        this.rider = rider;
        this.destination = destination;
        this.vehicleType = vehicleType;
        this.distance = rider.getLocation().distanceTo(destination);
        this.status = RideStatus.REQUESTED;
    }

    public String getId() { return id; }
    public Rider getRider() { return rider; }
    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }
    public Location getDestination() { return destination; }
    public double getDistance() { return distance; }
    public VehicleType getVehicleType() { return vehicleType; }
    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }
}
