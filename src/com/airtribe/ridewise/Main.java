package com.airtribe.ridewise;

import com.airtribe.ridewise.exception.NoDriverAvailableException;
import com.airtribe.ridewise.model.*;
import com.airtribe.ridewise.service.DriverService;
import com.airtribe.ridewise.service.RiderService;
import com.airtribe.ridewise.service.RideService;
import com.airtribe.ridewise.strategy.DefaultFareStrategy;
import com.airtribe.ridewise.strategy.NearestDriverStrategy;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final RiderService riderService = new RiderService();
    private static final DriverService driverService = new DriverService();

    private static final RideService rideService = new RideService(
            driverService,
            new NearestDriverStrategy(),
            new DefaultFareStrategy()
    );

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Welcome to the Low-Level Ride Hailing Console System ===");

        while (true) {
            printMenu();
            System.out.print("Select an option: ");
            String input = scanner.nextLine().trim();

            try {
                switch (input) {
                    case "1":
                        handleAddRider(scanner);
                        break;
                    case "2":
                        handleAddDriver(scanner);
                        break;
                    case "3":
                        handleViewAvailableDrivers();
                        break;
                    case "4":
                        handleRequestRide(scanner);
                        break;
                    case "5":
                        handleCompleteRide(scanner);
                        break;
                    case "6":
                        handleViewRides();
                        break;
                    case "7":
                        System.out.println("Exiting application. Goodbye!");
                        scanner.close();
                        System.exit(0);
                    default:
                        System.out.println("[Error] Invalid entry. Please enter a valid number (1-7).");
                }
            } catch (Exception e) {
                System.out.println("[Runtime Error] " + e.getMessage());
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("1. Add Rider");
        System.out.println("2. Add Driver");
        System.out.println("3. View Available Drivers");
        System.out.println("4. Request Ride");
        System.out.println("5. Complete Ride");
        System.out.println("6. View Rides");
        System.out.println("7. Exit");
    }

    private static void handleAddRider(Scanner scanner) {
        System.out.print("Enter Rider Name: ");
        String name = scanner.nextLine().trim();

        double lat = parseDoubleInput(scanner, "Enter Location Latitude: ");
        double lon = parseDoubleInput(scanner, "Enter Location Longitude: ");

        String rideerId = riderService.registerRider(name, new Location(lat, lon));
        System.out.println("[Success] Rider with id: " + rideerId + ", registered successfully!");
    }

    private static void handleAddDriver(Scanner scanner) {
        System.out.print("Enter Driver Name: ");
        String name = scanner.nextLine().trim();

        double lat = parseDoubleInput(scanner, "Enter Current Latitude: ");
        double lon = parseDoubleInput(scanner, "Enter Current Longitude: ");

        String driverId = driverService.registerDriver(name, new Location(lat, lon));
        System.out.println("[Success] Driver with id: " + driverId + ", registered successfully!");
    }

    private static void handleViewAvailableDrivers() {
        List<Driver> available = driverService.getAvailableDrivers();
        if (available.isEmpty()) {
            System.out.println("No drivers are currently available.");
            return;
        }
        System.out.println("--- Available Drivers ---");
        for (Driver d : available) {
            System.out.printf("ID: %s | Name: %s | Location: (%.2f, %.2f) | Lifetime Rides: %d\n",
                    d.getId(), d.getName(), d.getCurrentLocation().getLatitude(),
                    d.getCurrentLocation().getLongitude(), d.getTotalRidesCompleted());
        }
    }

    private static void handleRequestRide(Scanner scanner) {
        System.out.print("Enter Rider ID: ");
        String riderId = scanner.nextLine().trim();
        Rider rider = riderService.getRiderById(riderId);

        double destLat = parseDoubleInput(scanner, "Enter Destination Latitude: ");
        double destLon = parseDoubleInput(scanner, "Enter Destination Longitude: ");

        System.out.print("Enter Vehicle Type (BIKE, AUTO, CAR): ");
        String typeInput = scanner.nextLine().trim().toUpperCase();
        VehicleType type;
        try {
            type = VehicleType.valueOf(typeInput);
        } catch (IllegalArgumentException e) {
            System.out.println("[Error] Invalid vehicle type setup selected.");
            return;
        }

        try {
            Ride ride = rideService.requestRide(rider, new Location(destLat, destLon), type);
            System.out.printf("[Success] Ride Matched! Ride ID: %s assigned to Driver: %s (%s)\n",
                    ride.getId(), ride.getDriver().getName(), ride.getDriver().getId());

        } catch (NoDriverAvailableException e) {
            // Targeted presentation feedback for this specific business constraint breach
            System.out.println("[Booking Alert] " + e.getMessage());
            System.out.println("-> Suggestion: Please wait a few moments or try changing your vehicle type choice.");
        }
    }

    private static void handleCompleteRide(Scanner scanner) {
        System.out.print("Enter Ride ID to complete: ");
        String rideId = scanner.nextLine().trim();

        FareReceipt receipt = rideService.completeRide(rideId);
        System.out.println("[Success] Ride closed successfully!");
        System.out.println(receipt);
    }

    private static void handleViewRides() {
        List<Ride> rides = rideService.getAllRides();
        if (rides.isEmpty()) {
            System.out.println("No transactional rides found in ledger system.");
            return;
        }
        System.out.println("--- System Rides ---");
        for (Ride r : rides) {
            String driverInfo = (r.getDriver() != null) ? r.getDriver().getName() : "UNASSIGNED";
            System.out.printf("Ride ID: %s | Rider: %s | Driver: %s | Status: %s | Distance: %.2f units\n",
                    r.getId(), r.getRider().getName(), driverInfo, r.getStatus(), r.getDistance());
        }
    }

    private static double parseDoubleInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[Invalid Entry] Please enter a valid decimal number value.");
            }
        }
    }
}
