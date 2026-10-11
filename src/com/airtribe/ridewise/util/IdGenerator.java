package com.airtribe.ridewise.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final AtomicInteger riderCounter = new AtomicInteger(0);
    private static final AtomicInteger driverCounter = new AtomicInteger(0);
    private static final AtomicInteger rideCounter = new AtomicInteger(0);

    public static String generateRiderId() {
        return "RIDER-" + riderCounter.incrementAndGet();
    }

    public static String generateDriverId() {
        return "DRIVER-" + driverCounter.incrementAndGet();
    }

    public static String generateRideId() {
        return "RIDE-" + rideCounter.incrementAndGet();
    }
}