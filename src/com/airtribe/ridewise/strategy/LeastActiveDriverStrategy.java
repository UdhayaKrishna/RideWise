package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;
import java.util.List;

public class LeastActiveDriverStrategy implements RideMatchingStrategy {
    @Override
    public Driver findDriver(Rider rider, List<Driver> drivers) {
        Driver leastActive = null;
        int minRides = Integer.MAX_VALUE;

        for (Driver driver : drivers) {
            if (driver.isAvailable()) {
                if (driver.getTotalRidesCompleted() < minRides) {
                    minRides = driver.getTotalRidesCompleted();
                    leastActive = driver;
                }
            }
        }
        return leastActive;
    }
}
