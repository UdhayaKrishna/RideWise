package com.airtribe.ridewise.service;

import com.airtribe.ridewise.exception.EntityNotFoundException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Location;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverService {
    private final Map<String, Driver> driverStorage = new HashMap<>();

    public String registerDriver(String name, Location location) {
        String uniqueId = IdGenerator.generateDriverId();
        driverStorage.put(uniqueId, new Driver(uniqueId, name, location));
        return uniqueId;
    }

    public void updateAvailability(String id, boolean available) {
        Driver driver = getDriverById(id);
        driver.setAvailable(available);
    }

    public List<Driver> getAvailableDrivers() {
        List<Driver> availableDrivers = new ArrayList<>();
        for (Driver d : driverStorage.values()) {
            if (d.isAvailable()) {
                availableDrivers.add(d);
            }
        }
        return availableDrivers;
    }

    public Driver getDriverById(String id) {
        if (!driverStorage.containsKey(id)) {
            throw new EntityNotFoundException("Driver registry mismatch: No driver found with ID '" + id + "'.");
        }
        return driverStorage.get(id);
    }

    public List<Driver> getAllDrivers() {
        return new ArrayList<>(driverStorage.values());
    }
}
