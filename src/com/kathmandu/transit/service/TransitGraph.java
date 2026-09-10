package com.kathmandu.transit.service;

import com.kathmandu.transit.model.BusRoute;
import com.kathmandu.transit.model.RouteSegment;
import com.kathmandu.transit.model.Station;

import java.util.*;

/**
 * Directed multi-graph representing the Kathmandu Valley public transit network.
 * Stores stations, bus routes, and outgoing connections.
 */
public class TransitGraph {
    private final Map<String, Station> stationMap = new LinkedHashMap<>();
    private final Map<String, BusRoute> routeMap = new LinkedHashMap<>();
    private final Map<Station, List<RouteSegment>> adjacencyList = new HashMap<>();
    private final Map<Station, Set<BusRoute>> stationRoutesMap = new HashMap<>();

    public void addStation(Station station) {
        stationMap.put(station.getId(), station);
        adjacencyList.putIfAbsent(station, new ArrayList<>());
        stationRoutesMap.putIfAbsent(station, new HashSet<>());
    }

    public void addRoute(BusRoute route) {
        routeMap.put(route.getId(), route);
        List<Station> stops = route.getStations();

        for (Station s : stops) {
            addStation(s);
            stationRoutesMap.get(s).add(route);
        }

        // Add segments between consecutive stops in forward direction
        for (int i = 0; i < stops.size() - 1; i++) {
            Station from = stops.get(i);
            Station to = stops.get(i + 1);
            double dist = estimateSegmentDistance(from, to);
            int duration = estimateSegmentDuration(dist);
            RouteSegment segment = new RouteSegment(from, to, route, dist, duration);
            adjacencyList.get(from).add(segment);
        }

        // If route is bidirectional, add reverse segments
        if (route.isBidirectional()) {
            for (int i = stops.size() - 1; i > 0; i--) {
                Station from = stops.get(i);
                Station to = stops.get(i - 1);
                double dist = estimateSegmentDistance(from, to);
                int duration = estimateSegmentDuration(dist);
                RouteSegment segment = new RouteSegment(from, to, route, dist, duration);
                adjacencyList.get(from).add(segment);
            }
        }
    }

    /**
     * Estimates distance in km using coordinates (Haversine) with a winding road factor.
     */
    public static double estimateSegmentDistance(Station s1, Station s2) {
        double lat1 = s1.getLatitude();
        double lon1 = s1.getLongitude();
        double lat2 = s2.getLatitude();
        double lon2 = s2.getLongitude();

        // Haversine formula
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double straightDistance = 6371.0 * c; // Earth radius in km

        // Factor of 1.35 accounts for Kathmandu's real urban road curvature & topography
        double roadDist = straightDistance * 1.35;
        // Minimum segment distance buffer
        return Math.max(0.6, Math.round(roadDist * 10.0) / 10.0);
    }

    /**
     * Estimates travel duration in minutes based on distance and Kathmandu average traffic speed (~16-20 km/h).
     */
    public static int estimateSegmentDuration(double distanceKm) {
        // Average public bus speed in Kathmandu valley is ~18 km/h + 1.5 mins stop dwell time
        int minutes = (int) Math.round((distanceKm / 18.0) * 60.0 + 1.5);
        return Math.max(3, minutes);
    }

    public Station getStationById(String id) {
        return stationMap.get(id);
    }

    public Collection<Station> getAllStations() {
        return stationMap.values();
    }

    public Collection<BusRoute> getAllRoutes() {
        return routeMap.values();
    }

    public List<RouteSegment> getOutgoingSegments(Station station) {
        return adjacencyList.getOrDefault(station, Collections.emptyList());
    }

    public Set<BusRoute> getRoutesServing(Station station) {
        return stationRoutesMap.getOrDefault(station, Collections.emptySet());
    }
}
