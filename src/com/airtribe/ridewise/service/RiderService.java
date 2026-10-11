package com.airtribe.ridewise.service;

import com.airtribe.ridewise.exception.EntityNotFoundException;
import com.airtribe.ridewise.model.Location;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.HashMap;
import java.util.Map;

public class RiderService {
    private final Map<String, Rider> riderStorage = new HashMap<>();

    public String registerRider(String name, Location location) {
        String uniqueId = IdGenerator.generateRiderId();
        riderStorage.put(uniqueId, new Rider(uniqueId, name, location));
        return uniqueId;
    }

    public Rider getRiderById(String id) {
        if (!riderStorage.containsKey(id)) {
            throw new EntityNotFoundException("Rider registry mismatch: No rider found with ID '" + id + "'.");
        }
        return riderStorage.get(id);
    }
}
