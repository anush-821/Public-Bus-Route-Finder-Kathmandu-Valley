package com.kathmandu.transit.service;

import com.kathmandu.transit.model.Station;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides search, autocomplete, and lookup services for stations in Kathmandu Valley.
 */
public class StationDirectoryService {
    private final TransitGraph graph;

    public StationDirectoryService(TransitGraph graph) {
        this.graph = graph;
    }

    /**
     * Search stations matching a query string (English name, Nepali name, or area).
     */
    public List<Station> searchStations(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllStationsSorted();
        }

        String q = query.trim().toLowerCase();
        return graph.getAllStations().stream()
                .filter(s -> s.getName().toLowerCase().contains(q) ||
                             (s.getNepaliName() != null && s.getNepaliName().contains(query.trim())) ||
                             s.getArea().toLowerCase().contains(q) ||
                             s.getId().toLowerCase().contains(q))
                .sorted(Comparator.comparing(Station::getName))
                .collect(Collectors.toList());
    }

    /**
     * Returns all stations sorted alphabetically by English name.
     */
    public List<Station> getAllStationsSorted() {
        return graph.getAllStations().stream()
                .sorted(Comparator.comparing(Station::getName))
                .collect(Collectors.toList());
    }

    /**
     * Returns only major transit interchange hubs.
     */
    public List<Station> getMajorHubs() {
        return graph.getAllStations().stream()
                .filter(Station::isMajorHub)
                .sorted(Comparator.comparing(Station::getName))
                .collect(Collectors.toList());
    }

    /**
     * Finds station by exact ID or exact name (case-insensitive).
     */
    public Optional<Station> findByNameOrId(String identifier) {
        if (identifier == null) return Optional.empty();
        String target = identifier.trim().toLowerCase();

        for (Station s : graph.getAllStations()) {
            if (s.getId().equalsIgnoreCase(target) || 
                s.getName().equalsIgnoreCase(target) ||
                (s.getNepaliName() != null && s.getNepaliName().equalsIgnoreCase(target))) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }
}
