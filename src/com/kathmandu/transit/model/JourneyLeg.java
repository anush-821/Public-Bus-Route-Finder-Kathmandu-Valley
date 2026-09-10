package com.kathmandu.transit.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A continuous portion of a journey traveled on a single bus route without changing vehicles.
 */
public class JourneyLeg {
    private final BusRoute route;
    private final Station boardingStation;
    private final Station alightingStation;
    private final List<Station> intermediateStops;
    private final double distanceKm;
    private final int durationMinutes;
    private final int fareNpr;
    private final int studentFareNpr;

    public JourneyLeg(BusRoute route, Station boardingStation, Station alightingStation,
                      List<Station> intermediateStops, double distanceKm, int durationMinutes,
                      int fareNpr, int studentFareNpr) {
        this.route = route;
        this.boardingStation = boardingStation;
        this.alightingStation = alightingStation;
        this.intermediateStops = new ArrayList<>(intermediateStops);
        this.distanceKm = distanceKm;
        this.durationMinutes = durationMinutes;
        this.fareNpr = fareNpr;
        this.studentFareNpr = studentFareNpr;
    }

    public BusRoute getRoute() {
        return route;
    }

    public Station getBoardingStation() {
        return boardingStation;
    }

    public Station getAlightingStation() {
        return alightingStation;
    }

    public List<Station> getIntermediateStops() {
        return Collections.unmodifiableList(intermediateStops);
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getFareNpr() {
        return fareNpr;
    }

    public int getStudentFareNpr() {
        return studentFareNpr;
    }

    public int getStopCount() {
        return intermediateStops.size() + 1;
    }

    @Override
    public String toString() {
        return "Board " + route.getOperator() + " (" + route.getName() + ") at " + 
               boardingStation.getName() + " -> Alight at " + alightingStation.getName() + 
               " [" + getStopCount() + " stops, " + String.format("%.1f", distanceKm) + 
               " km, " + durationMinutes + " min, Rs. " + fareNpr + "]";
    }
}
