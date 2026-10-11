package com.airtribe.ridewise.model;

public class Driver {
    private final String id;
    private final String name;
    private Location currentLocation;
    private boolean available;
    private int totalRidesCompleted;

    public Driver(String id, String name, Location currentLocation) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
        this.available = true;
        this.totalRidesCompleted = 0;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Location getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(Location currentLocation) { this.currentLocation = currentLocation; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public int getTotalRidesCompleted() { return totalRidesCompleted; }
    public void incrementRides() { this.totalRidesCompleted++; }
}
