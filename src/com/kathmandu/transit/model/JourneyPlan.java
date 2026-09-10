package com.kathmandu.transit.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the complete recommended journey from origin to destination,
 * potentially comprising multiple legs and transfers.
 */
public class JourneyPlan {
    private final Station origin;
    private final Station destination;
    private final List<JourneyLeg> legs;
    private final String preference;
    private final double totalDistanceKm;
    private final int totalDurationMinutes;
    private final int totalFareNpr;
    private final int totalStudentFareNpr;

    public JourneyPlan(Station origin, Station destination, List<JourneyLeg> legs, String preference) {
        this.origin = origin;
        this.destination = destination;
        this.legs = new ArrayList<>(legs);
        this.preference = preference;

        double dist = 0.0;
        int time = 0;
        int fare = 0;
        int studentFare = 0;

        for (int i = 0; i < legs.size(); i++) {
            JourneyLeg leg = legs.get(i);
            dist += leg.getDistanceKm();
            time += leg.getDurationMinutes();
            // Add 7 minutes estimated transfer buffer for switching buses in Kathmandu
            if (i > 0) {
                time += 7;
            }
            fare += leg.getFareNpr();
            studentFare += leg.getStudentFareNpr();
        }

        this.totalDistanceKm = dist;
        this.totalDurationMinutes = time;
        this.totalFareNpr = fare;
        this.totalStudentFareNpr = studentFare;
    }

    public Station getOrigin() {
        return origin;
    }

    public Station getDestination() {
        return destination;
    }

    public List<JourneyLeg> getLegs() {
        return Collections.unmodifiableList(legs);
    }

    public String getPreference() {
        return preference;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public int getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public int getTotalFareNpr() {
        return totalFareNpr;
    }

    public int getTotalStudentFareNpr() {
        return totalStudentFareNpr;
    }

    public int getTransferCount() {
        return Math.max(0, legs.size() - 1);
    }

    public boolean isDirect() {
        return legs.size() == 1;
    }

    /**
     * Generates a step-by-step textual guide for the commuter.
     */
    public List<String> getStepByStepInstructions() {
        List<String> steps = new ArrayList<>();
        if (legs.isEmpty()) {
            steps.add("Already at destination: " + destination.getDisplayLabel());
            return steps;
        }

        for (int i = 0; i < legs.size(); i++) {
            JourneyLeg leg = legs.get(i);
            String vehicleTag = leg.getRoute().getVehicleType().getTag();
            int stepNum = i + 1;

            if (i == 0) {
                steps.add(String.format("Step %d: Board %s %s (%s) at %s.",
                        stepNum, vehicleTag, leg.getRoute().getName(), 
                        leg.getRoute().getOperator(), leg.getBoardingStation().getDisplayLabel()));
            } else {
                steps.add(String.format("Step %d: Transfer! Board %s %s (%s) at %s.",
                        stepNum, vehicleTag, leg.getRoute().getName(),
                        leg.getRoute().getOperator(), leg.getBoardingStation().getDisplayLabel()));
            }

            if (!leg.getIntermediateStops().isEmpty()) {
                StringBuilder stopsList = new StringBuilder("        Pass through: ");
                for (int s = 0; s < leg.getIntermediateStops().size(); s++) {
                    stopsList.append(leg.getIntermediateStops().get(s).getName());
                    if (s < leg.getIntermediateStops().size() - 1) {
                        stopsList.append(" -> ");
                    }
                }
                steps.add(stopsList.toString());
            }

            steps.add(String.format("        Ride for %d min (~%.1f km). Alight at %s. (Leg Fare: Rs. %d)",
                    leg.getDurationMinutes(), leg.getDistanceKm(), 
                    leg.getAlightingStation().getDisplayLabel(), leg.getFareNpr()));
        }

        steps.add("Arrival: You have reached " + destination.getDisplayLabel() + "!");
        return steps;
    }
}
