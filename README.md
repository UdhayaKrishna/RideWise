# RideWise

RideWise is a low-level Java console application that simulates a ride-hailing system. It models the core workflow of a real ride platform: rider registration, driver registration, matching a rider to a driver, fare calculation, ride completion, and status tracking.

The project is intentionally simple, readable, and extensible. It focuses on clean domain modeling and object-oriented design rather than heavy frameworks or UI complexity.

## Project purpose

This application demonstrates how a ride-booking business can be structured using:

- domain entities such as Rider, Driver, Ride, and Location
- service-layer orchestration for business operations
- strategy-based behavior for driver matching and pricing
- explicit exception handling for invalid or impossible workflow states
- clean separation between the user interface and business rules

In other words, this is a study in designing a lightweight system that follows real-world software engineering principles without depending on external databases or web infrastructure.

## Core business flow

The application is built around a basic lifecycle:

1. A rider is registered with a current location.
2. A driver is registered with his/her location and availability state.
3. A rider requests a ride with a destination and vehicle type.
4. The system searches available drivers.
5. A matching strategy chooses the best driver.
6. The driver is assigned to the ride and marked unavailable.
7. The ride is completed, the fare is calculated, and the driver becomes available again.

## Design principles followed in this project

### 1. Separation of concerns

Each package has a distinct responsibility:

- model: domain data classes and business-specific concepts
- service: orchestration and business logic
- strategy: pluggable algorithms for matching and fare logic
- exception: domain-specific failures and invalid operations
- util: reusable support logic such as ID generation
- Main: console entry point and user interaction layer

This keeps the business logic independent from the UI and makes the code easier to maintain and test.

### 2. Single responsibility principle

Each class owns one meaningful responsibility:

- RiderService is responsible only for rider lifecycle operations.
- DriverService handles driver registration and availability state.
- RideService handles ride assignment, state changes, and completion.
- Driver tracks driver-specific state such as availability and completed rides.
- Ride represents a ride contract and lifecycle state.
- FareStrategy and RideMatchingStrategy handle only decision logic.

This prevents classes from becoming “god objects” and keeps responsibilities easy to reason about.

### 3. Dependency inversion and abstraction

The RideService does not hard-code the matching or pricing algorithm. Instead, it depends on interfaces:

- RideMatchingStrategy
- FareStrategy

This means the system is designed around abstractions rather than concrete implementations. The behavior can be changed without modifying the business service itself.

Example:

- current matching implementation: NearestDriverStrategy
- alternative matching implementation: LeastActiveDriverStrategy
- current fare implementation: DefaultFareStrategy
- alternative pricing implementation: PeakHourFareStrategy

This is a classic strategy pattern and is one of the most important design ideas in the codebase.

### 4. Open/closed principle

The system is open for extension but closed for modification.

If a new pricing model or matching algorithm is needed, a new class can be implemented without changing `RideService` internals. The service accepts new strategy objects through setter methods or constructor injection.

This is especially visible in:

- `RideService.setMatchingStrategy(...)`
- `RideService.setFareStrategy(...)`

### 5. Domain-driven modeling

The project models real business concepts explicitly, instead of using vague names like `Data` or `Controller`.

The main domain objects include:

- Rider: person requesting a ride
- Driver: person providing service
- Ride: active booking lifecycle
- Location: geo-coordinate position
- VehicleType: category such as BIKE, AUTO, or CAR
- FareReceipt: generated invoice after a completed ride

This makes the system easier to understand from a business perspective and mirrors how real service systems are structured.

### 6. Encapsulation and invariants

Fields such as ride IDs, driver names, locations, and statuses are protected behind getter/setter methods and are intentionally not exposed freely. The design enforces a clean boundary between data and behavior.

Examples:

- Driver tracks availability internally
- Ride tracks assignment status internally
- FareReceipt is immutable after creation
- IDs are generated centrally and safely

This protects domain correctness and reduces accidental misuse.

### 7. Explicit state management

Ride state is controlled through `RideStatus`, which prevents invalid transitions.

The `RideService.completeRide` method validates the current state before completion. If the ride is not in the correct state, it throws an `InvalidRideStateException`.

This prevents impossible actions such as:

- completing a ride that was never assigned
- marking a ride complete before it was accepted
- using a nonexistent ride ID

### 8. Fail-fast business validation

The project prefers explicit runtime errors over silent failure.

Examples:

- `NoDriverAvailableException` when no driver can serve the request
- `EntityNotFoundException` when a rider, driver, or ride cannot be found
- `InvalidRideStateException` when the lifecycle is invalid

This approach makes system issues visible immediately and keeps problems understandable during debugging.

### 9. In-memory simplicity for console design

This project uses `HashMap`-based storage to keep the implementation lightweight and clear. It is not designed for production persistence, but it demonstrates a realistic architecture pattern:

- service layer reads and writes the in-memory repository
- domain objects represent the core data
- state is tracked in memory during the console session

This is a good teaching approach for understanding architecture before adding databases, APIs, or distributed services.

## Architecture overview

The project follows a layered layout that is easy to trace:

```text
Main.java
  |
  v
Service Layer
  - RiderService
  - DriverService
  - RideService
  |
  +--> Model Layer
  |      - Rider
  |      - Driver
  |      - Ride
  |      - Location
  |      - VehicleType
  |      - FareReceipt
  |
  +--> Strategy Layer
  |      - RideMatchingStrategy
  |      - FareStrategy
  |      - NearestDriverStrategy
  |      - LeastActiveDriverStrategy
  |      - DefaultFareStrategy
  |      - PeakHourFareStrategy
  |
  +--> Exception Layer
         - NoDriverAvailableException
         - EntityNotFoundException
         - InvalidRideStateException
```

## Important patterns used

### Strategy pattern

The system decides matching and fare calculations through interchangeable strategies.

- `RideService` uses a `RideMatchingStrategy` implementation to select drivers.
- `RideService` uses a `FareStrategy` implementation to calculate the fare.

This allows the business logic to change without touching the service core.

### Factory-like ID generation

The project avoids manual ID assignments and centralizes them through `IdGenerator`, which ensures unique IDs for riders, drivers, and rides.

This makes the code cleaner and less error-prone.

### State-driven workflow

The ride lifecycle is tracked with `RideStatus` and enforced within business methods. This is a common pattern in order systems, bookings, workflow systems, and service platforms.

## Data and behavioral model

### Driver behavior

A `Driver` has:

- unique ID
- name
- current location
- availability flag
- total rides completed

The driver becomes unavailable when assigned to a ride and returns to available once the ride is completed.

### Rider behavior

A `Rider` has:

- unique ID
- name
- location

The rider’s request contains a destination and vehicle preference.

### Ride behavior

A `Ride` has:

- unique ride ID
- assigned rider
- assigned driver
- destination
- computed distance
- preferred vehicle type
- status

This is the central transactional object of the system.

### Fare logic

Price is calculated using a base fare and a distance multiplier, adjusted by the vehicle type.

Current formula:

- base fare = 50
- cost per unit distance = 10
- vehicle factor modifies the total

This is simple but demonstrates that pricing is not embedded in the `Ride` class; it is delegated to a strategy, making future pricing rules easy to add.

## Console interaction design

The `Main` class acts as the presentation layer. It reads user inputs, dispatches actions, and prints status messages. This is intentionally kept lightweight so the business logic remains separate from terminal I/O.

The menu supports:

- add rider
- add driver
- view available drivers
- request ride
- complete ride
- view rides
- exit

## Why this project is a good software design example

This project is effective as a design exercise because it demonstrates a practical, readable system that uses real engineering ideas:

- object-oriented modeling with domain classes
- layered architecture
- reusable algorithms via strategy objects
- explicit state transitions
- clear exceptions for invalid operations
- decoupled service and UI responsibilities

It is intentionally simple enough to understand quickly, but rich enough to show how modern software systems are often designed.

## How to run the project

From the project root, run:

```bash
javac -d out $(find src -name '*.java')
java -cp out com.airtribe.ridewise.Main
```

## Future enhancement opportunities

Although the current version is intentionally minimal, the architecture is ready for expansion:

- add database persistence
- add REST APIs
- add driver rating and rider feedback
- add surge pricing conditions based on time and demand
- add multiple ride matching criteria such as ETA or driver rating
- add concurrency and thread safety for multi-user environments

## Summary

RideWise is a concise but well-structured ride-hailing system built to illustrate strong software design principles. It demonstrates how domain modeling, layered architecture, dependency inversion, and strategy-based extension can be combined to build a maintainable business application.

The project is not just a booking app; it is a practical example of how to design software around real business rules and responsibilities.
