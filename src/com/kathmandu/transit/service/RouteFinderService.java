package com.kathmandu.transit.service;

import com.kathmandu.transit.model.*;

import java.util.*;

/**
 * Multi-criteria route finding service implementing Dijkstra algorithm with
 * transit line continuity and transfer penalties.
 */
public class RouteFinderService {

    public enum Preference {
        FASTEST("Fastest Journey"),
        SHORTEST("Shortest Distance"),
        FEWEST_TRANSFERS("Minimum Bus Transfers"),
        DIRECT_ONLY("Direct Routes Only");

        private final String label;

        Preference(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final TransitGraph graph;
    private final FareCalculatorService fareCalculator;

    public RouteFinderService(TransitGraph graph, FareCalculatorService fareCalculator) {
        this.graph = graph;
        this.fareCalculator = fareCalculator;
    }

    public TransitGraph getGraph() {
        return graph;
    }

    public FareCalculatorService getFareCalculator() {
        return fareCalculator;
    }

    /**
     * Finds all direct routes connecting origin and destination without transfers.
     */
    public List<JourneyPlan> findDirectRoutes(Station origin, Station destination) {
        List<JourneyPlan> directPlans = new ArrayList<>();
        if (origin.equals(destination)) {
            return directPlans;
        }

        for (BusRoute route : graph.getAllRoutes()) {
            List<Station> stops = route.getStations();
            int fromIdx = stops.indexOf(origin);
            int toIdx = stops.indexOf(destination);

            // Forward direction
            if (fromIdx >= 0 && toIdx >= 0 && fromIdx < toIdx) {
                directPlans.add(buildDirectPlan(route, stops.subList(fromIdx, toIdx + 1), origin, destination));
            }
            // Reverse direction if bidirectional
            else if (route.isBidirectional() && fromIdx >= 0 && toIdx >= 0 && fromIdx > toIdx) {
                List<Station> reversedStops = new ArrayList<>(stops.subList(toIdx, fromIdx + 1));
                Collections.reverse(reversedStops);
                directPlans.add(buildDirectPlan(route, reversedStops, origin, destination));
            }
        }

        // Sort direct plans by duration
        directPlans.sort(Comparator.comparingInt(JourneyPlan::getTotalDurationMinutes));
        return directPlans;
    }

    private JourneyPlan buildDirectPlan(BusRoute route, List<Station> stopSublist, Station origin, Station destination) {
        double dist = 0.0;
        int duration = 0;
        List<Station> intermediates = new ArrayList<>();

        for (int i = 0; i < stopSublist.size() - 1; i++) {
            Station s1 = stopSublist.get(i);
            Station s2 = stopSublist.get(i + 1);
            double d = TransitGraph.estimateSegmentDistance(s1, s2);
            dist += d;
            duration += TransitGraph.estimateSegmentDuration(d);
            if (i > 0) {
                intermediates.add(s1);
            }
        }

        int fare = fareCalculator.calculateFare(dist);
        int studentFare = fareCalculator.calculateStudentFare(fare);

        JourneyLeg leg = new JourneyLeg(route, origin, destination, intermediates, dist, duration, fare, studentFare);
        return new JourneyPlan(origin, destination, Collections.singletonList(leg), "DIRECT");
    }

    /**
     * Finds the optimal journey from origin to destination according to the chosen preference.
     */
    public Optional<JourneyPlan> findRoute(Station origin, Station destination, Preference preference) {
        if (origin.equals(destination)) {
            return Optional.of(new JourneyPlan(origin, destination, Collections.emptyList(), preference.name()));
        }

        if (preference == Preference.DIRECT_ONLY) {
            List<JourneyPlan> direct = findDirectRoutes(origin, destination);
            return direct.isEmpty() ? Optional.empty() : Optional.of(direct.get(0));
        }

        // State node for Dijkstra: (station, busRoute)
        PriorityQueue<DijkstraNode> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.cost));
        Map<StateKey, Double> bestCosts = new HashMap<>();
        Map<StateKey, EdgeArrival> predecessors = new HashMap<>();

        // Start from origin with null incoming route
        StateKey startKey = new StateKey(origin, null);
        bestCosts.put(startKey, 0.0);
        pq.add(new DijkstraNode(origin, null, 0.0, 0.0, 0, 0));

        StateKey destKeyReached = null;
        double bestDestCost = Double.MAX_VALUE;

        while (!pq.isEmpty()) {
            DijkstraNode current = pq.poll();
            StateKey currentKey = new StateKey(current.station, current.route);

            if (current.cost > bestCosts.getOrDefault(currentKey, Double.MAX_VALUE)) {
                continue;
            }

            if (current.station.equals(destination)) {
                if (current.cost < bestDestCost) {
                    bestDestCost = current.cost;
                    destKeyReached = currentKey;
                }
                break;
            }

            for (RouteSegment segment : graph.getOutgoingSegments(current.station)) {
                Station nextStation = segment.getTo();
                BusRoute nextRoute = segment.getRoute();

                boolean isTransfer = (current.route != null && !current.route.equals(nextRoute));

                double stepCost;
                double stepDist = segment.getDistanceKm();
                int stepDuration = segment.getDurationMinutes();
                int transferAdd = isTransfer ? 1 : 0;

                switch (preference) {
                    case SHORTEST:
                        // Slight penalty for transfer so distance being equal, avoid transfers
                        stepCost = stepDist + (isTransfer ? 0.3 : 0.0);
                        break;
                    case FEWEST_TRANSFERS:
                        // Strong penalty for changing bus
                        stepCost = (transferAdd * 1000.0) + (stepDuration);
                        break;
                    case FASTEST:
                    default:
                        // 7 minutes wait penalty for bus interchange
                        stepCost = stepDuration + (isTransfer ? 7.0 : 0.0);
                        break;
                }

                double newCost = current.cost + stepCost;
                StateKey nextKey = new StateKey(nextStation, nextRoute);

                if (newCost < bestCosts.getOrDefault(nextKey, Double.MAX_VALUE)) {
                    bestCosts.put(nextKey, newCost);
                    predecessors.put(nextKey, new EdgeArrival(currentKey, segment));
                    pq.add(new DijkstraNode(nextStation, nextRoute, newCost, 
                            current.accumulatedDistance + stepDist,
                            current.accumulatedDuration + stepDuration + (isTransfer ? 7 : 0),
                            current.transfers + transferAdd));
                }
            }
        }

        if (destKeyReached == null) {
            return Optional.empty();
        }

        // Reconstruct segments
        List<RouteSegment> segmentPath = new ArrayList<>();
        StateKey trace = destKeyReached;
        while (predecessors.containsKey(trace)) {
            EdgeArrival arrival = predecessors.get(trace);
            segmentPath.add(arrival.segment);
            trace = arrival.fromState;
        }
        Collections.reverse(segmentPath);

        // Compress consecutive segments of the same bus route into JourneyLegs
        List<JourneyLeg> legs = compressSegmentsIntoLegs(segmentPath);
        return Optional.of(new JourneyPlan(origin, destination, legs, preference.name()));
    }

    private List<JourneyLeg> compressSegmentsIntoLegs(List<RouteSegment> segments) {
        List<JourneyLeg> legs = new ArrayList<>();
        if (segments.isEmpty()) return legs;

        int i = 0;
        while (i < segments.size()) {
            BusRoute currentRoute = segments.get(i).getRoute();
            Station boardingStation = segments.get(i).getFrom();
            List<Station> intermediates = new ArrayList<>();
            double legDist = 0.0;
            int legDuration = 0;

            int j = i;
            while (j < segments.size() && segments.get(j).getRoute().equals(currentRoute)) {
                RouteSegment seg = segments.get(j);
                legDist += seg.getDistanceKm();
                legDuration += seg.getDurationMinutes();
                if (j > i) {
                    intermediates.add(seg.getFrom());
                }
                j++;
            }

            Station alightingStation = segments.get(j - 1).getTo();
            int fare = fareCalculator.calculateFare(legDist);
            int studentFare = fareCalculator.calculateStudentFare(fare);

            legs.add(new JourneyLeg(currentRoute, boardingStation, alightingStation,
                    intermediates, legDist, legDuration, fare, studentFare));

            i = j;
        }

        return legs;
    }

    // Helper data structures for Dijkstra search
    private static class StateKey {
        final Station station;
        final BusRoute route;

        StateKey(Station station, BusRoute route) {
            this.station = station;
            this.route = route;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StateKey stateKey = (StateKey) o;
            return Objects.equals(station, stateKey.station) &&
                   Objects.equals(route, stateKey.route);
        }

        @Override
        public int hashCode() {
            return Objects.hash(station, route);
        }
    }

    private static class DijkstraNode {
        final Station station;
        final BusRoute route;
        final double cost;
        final double accumulatedDistance;
        final int accumulatedDuration;
        final int transfers;

        DijkstraNode(Station station, BusRoute route, double cost, 
                     double accumulatedDistance, int accumulatedDuration, int transfers) {
            this.station = station;
            this.route = route;
            this.cost = cost;
            this.accumulatedDistance = accumulatedDistance;
            this.accumulatedDuration = accumulatedDuration;
            this.transfers = transfers;
        }
    }

    private static class EdgeArrival {
        final StateKey fromState;
        final RouteSegment segment;

        EdgeArrival(StateKey fromState, RouteSegment segment) {
            this.fromState = fromState;
            this.segment = segment;
        }
    }
}
