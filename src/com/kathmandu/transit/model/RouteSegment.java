package com.kathmandu.transit.model;

import java.util.Objects;

/**
 * Directed link between two consecutive stops on a specific bus route.
 */
public class RouteSegment {
    private final Station from;
    private final Station to;
    private final BusRoute route;
    private final double distanceKm;
    private final int durationMinutes;

    public RouteSegment(Station from, Station to, BusRoute route, double distanceKm, int durationMinutes) {
        this.from = from;
        this.to = to;
        this.route = route;
        this.distanceKm = distanceKm;
        this.durationMinutes = durationMinutes;
    }

    public Station getFrom() {
        return from;
    }

    public Station getTo() {
        return to;
    }

    public BusRoute getRoute() {
        return route;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteSegment that = (RouteSegment) o;
        return Objects.equals(from, that.from) &&
               Objects.equals(to, that.to) &&
               Objects.equals(route, that.route);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, route);
    }

    @Override
    public String toString() {
        return from.getName() + " -> " + to.getName() + " via " + route.getName() + 
               " (" + String.format("%.1f", distanceKm) + " km, " + durationMinutes + " min)";
    }
}
