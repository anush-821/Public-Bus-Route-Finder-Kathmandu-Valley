package com.kathmandu.transit.ui;

import com.kathmandu.transit.data.KathmanduTransitDataLoader;
import com.kathmandu.transit.model.*;
import com.kathmandu.transit.service.FareCalculatorService;
import com.kathmandu.transit.service.RouteFinderService;
import com.kathmandu.transit.service.StationDirectoryService;
import com.kathmandu.transit.service.TransitGraph;

import java.util.*;

/**
 * Interactive Console / Terminal User Interface for Kathmandu Valley Transit Route Finder.
 */
public class ConsoleApp {

    private final TransitGraph graph;
    private final FareCalculatorService fareCalculator;
    private final RouteFinderService routeFinder;
    private final StationDirectoryService directory;
    private final Scanner scanner;

    public ConsoleApp() {
        this.graph = KathmanduTransitDataLoader.loadData();
        this.fareCalculator = new FareCalculatorService();
        this.routeFinder = new RouteFinderService(graph, fareCalculator);
        this.directory = new StationDirectoryService(graph);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            System.out.print("Enter your choice (1-6): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleRouteSearch();
                    break;
                case "2":
                    handleDirectRoutesSearch();
                    break;
                case "3":
                    handleStationDirectory();
                    break;
                case "4":
                    handleBusRoutesViewer();
                    break;
                case "5":
                    handleFareRules();
                    break;
                case "6":
                    System.out.println("\nThank you for using Kathmandu Valley Public Bus Route Finder! Subha Yatra (Safe travels)!\n");
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid choice. Please enter a number from 1 to 6.\n");
            }
        }
    }

    private void printBanner() {
        System.out.println("================================================================================");
        System.out.println("                   KATHMANDU VALLEY PUBLIC BUS ROUTE FINDER                     ");
        System.out.println("            Smart Transit Navigation for Kathmandu, Lalitpur & Bhaktapur        ");
        System.out.println("================================================================================");
        System.out.printf("  Loaded: %d Bus Stops | %d Public Transit Lines | Real DoTM Fare Slabs\n",
                graph.getAllStations().size(), graph.getAllRoutes().size());
        System.out.println("================================================================================\n");
    }

    private void printMainMenu() {
        System.out.println("MAIN MENU:");
        System.out.println("  [1] Find Best Route (Fastest / Shortest / Minimum Transfers)");
        System.out.println("  [2] Find Direct Bus Lines (No Transfers)");
        System.out.println("  [3] Search & Browse Bus Stops (Directory & Major Hubs)");
        System.out.println("  [4] View All Bus Routes & Schedules");
        System.out.println("  [5] View Kathmandu Fare Slabs & Concession Rules");
        System.out.println("  [6] Exit Application");
        System.out.println("--------------------------------------------------------------------------------");
    }

    private void handleRouteSearch() {
        System.out.println("\n--- FIND BEST BUS ROUTE ---");

        Station origin = promptStation("Enter Origin Bus Stop (e.g. Kalanki, Ratnapark, Lagankhel): ");
        if (origin == null) return;

        Station destination = promptStation("Enter Destination Bus Stop (e.g. Suryabinayak, Bauddha, Thankot): ");
        if (destination == null) return;

        if (origin.equals(destination)) {
            System.out.println("\n[!] Origin and destination are the same! You are already at " + origin.getName() + "\n");
            return;
        }

        System.out.println("\nChoose Routing Preference:");
        System.out.println("  1. Fastest Route (Quickest arrival with transfer waiting accounted)");
        System.out.println("  2. Shortest Distance (Minimum km)");
        System.out.println("  3. Fewest Transfers (Prioritize staying on the same bus)");
        System.out.print("Select preference (1-3, default=1): ");
        String prefInput = scanner.nextLine().trim();

        RouteFinderService.Preference preference = RouteFinderService.Preference.FASTEST;
        if ("2".equals(prefInput)) {
            preference = RouteFinderService.Preference.SHORTEST;
        } else if ("3".equals(prefInput)) {
            preference = RouteFinderService.Preference.FEWEST_TRANSFERS;
        }

        Optional<JourneyPlan> planOpt = routeFinder.findRoute(origin, destination, preference);
        if (!planOpt.isPresent() || planOpt.get().getLegs().isEmpty()) {
            System.out.println("\n[!] No transit route found connecting " + origin.getName() + " and " + destination.getName());
            System.out.println("    Try searching for an intermediate hub such as Ratnapark or Kalanki.\n");
            return;
        }

        JourneyPlan plan = planOpt.get();
        displayJourneyPlan(plan);
    }

    private void handleDirectRoutesSearch() {
        System.out.println("\n--- DIRECT BUS ROUTES (NO TRANSFERS) ---");

        Station origin = promptStation("Enter Origin Bus Stop: ");
        if (origin == null) return;

        Station destination = promptStation("Enter Destination Bus Stop: ");
        if (destination == null) return;

        List<JourneyPlan> directRoutes = routeFinder.findDirectRoutes(origin, destination);
        if (directRoutes.isEmpty()) {
            System.out.println("\n[!] No direct single-bus route found between " + origin.getName() + " and " + destination.getName() + ".");
            System.out.println("    Use option [1] (Find Best Route) to view transfer routes via central hubs.\n");
            return;
        }

        System.out.printf("\nFound %d direct route(s) from %s to %s:\n\n",
                directRoutes.size(), origin.getName(), destination.getName());

        for (int i = 0; i < directRoutes.size(); i++) {
            JourneyPlan plan = directRoutes.get(i);
            JourneyLeg leg = plan.getLegs().get(0);
            BusRoute route = leg.getRoute();

            System.out.printf("  Option %d: %s %s (%s)\n", i + 1,
                    route.getVehicleType().getTag(), route.getName(), route.getOperator());
            System.out.printf("    Duration: ~%d mins | Distance: %.1f km | Regular Fare: Rs. %d | Student: Rs. %d\n",
                    plan.getTotalDurationMinutes(), plan.getTotalDistanceKm(),
                    plan.getTotalFareNpr(), plan.getTotalStudentFareNpr());
            System.out.printf("    Frequency: Every %d mins | Operating Hours: %s\n",
                    route.getFrequencyMinutes(), route.getOperatingHours());

            if (!leg.getIntermediateStops().isEmpty()) {
                System.out.print("    Stops: " + origin.getName());
                for (Station s : leg.getIntermediateStops()) {
                    System.out.print(" -> " + s.getName());
                }
                System.out.println(" -> " + destination.getName());
            }
            System.out.println();
        }
    }

    private void displayJourneyPlan(JourneyPlan plan) {
        System.out.println("\n================================================================================");
        System.out.println("                         RECOMMENDED JOURNEY PLAN                               ");
        System.out.println("================================================================================");
        System.out.printf(" Origin:        %s\n", plan.getOrigin().getName());
        System.out.printf(" Destination:   %s\n", plan.getDestination().getName());
        System.out.printf(" Total Time:    ~%d minutes\n", plan.getTotalDurationMinutes());
        System.out.printf(" Total Distance: %.1f km\n", plan.getTotalDistanceKm());
        System.out.printf(" Transfers:     %d (%s)\n", plan.getTransferCount(),
                plan.isDirect() ? "Direct Route" : plan.getTransferCount() + " vehicle change(s)");
        System.out.printf(" Standard Fare: Rs. %d NPR\n", plan.getTotalFareNpr());
        System.out.printf(" Student Fare:  Rs. %d NPR (with 45%% concession)\n", plan.getTotalStudentFareNpr());
        System.out.println("--------------------------------------------------------------------------------");

        System.out.println("STEP-BY-STEP NAVIGATION GUIDE:");
        List<String> instructions = plan.getStepByStepInstructions();
        for (String step : instructions) {
            System.out.println("  " + step);
        }

        System.out.println("\nVISUAL TRANSIT TIMELINE:");
        printAsciiTimeline(plan);
        System.out.println("================================================================================\n");
    }

    private void printAsciiTimeline(JourneyPlan plan) {
        for (int i = 0; i < plan.getLegs().size(); i++) {
            JourneyLeg leg = plan.getLegs().get(i);
            BusRoute r = leg.getRoute();

            if (i > 0) {
                System.out.println("       |");
                System.out.println("      [TRANSIT INTERCHANGE: Change bus here (~7 min wait)]");
                System.out.println("       |");
            }

            System.out.printf("  (O)  [%s] (%s)\n", leg.getBoardingStation().getName(), leg.getBoardingStation().getArea());
            System.out.printf("   |   | Line: %s %s\n", r.getVehicleType().getTag(), r.getName());
            System.out.printf("   |   | Operator: %s (Frequency: ~%d min)\n", r.getOperator(), r.getFrequencyMinutes());
            System.out.printf("   |   | Ride: ~%d mins (%.1f km) | Fare: Rs. %d\n",
                    leg.getDurationMinutes(), leg.getDistanceKm(), leg.getFareNpr());

            if (!leg.getIntermediateStops().isEmpty()) {
                System.out.printf("   |   | Passing %d stops: %s\n",
                        leg.getIntermediateStops().size(),
                        formatStopNames(leg.getIntermediateStops()));
            }

            if (i == plan.getLegs().size() - 1) {
                System.out.printf("  (X)  [%s] (%s) --> ARRIVAL\n",
                        leg.getAlightingStation().getName(), leg.getAlightingStation().getArea());
            }
        }
    }

    private String formatStopNames(List<Station> stops) {
        StringBuilder sb = new StringBuilder();
        int maxShow = Math.min(3, stops.size());
        for (int i = 0; i < maxShow; i++) {
            sb.append(stops.get(i).getName());
            if (i < maxShow - 1) sb.append(", ");
        }
        if (stops.size() > 3) {
            sb.append(" (+").append(stops.size() - 3).append(" more)");
        }
        return sb.toString();
    }

    private Station promptStation(String promptText) {
        while (true) {
            System.out.print(promptText);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Search cancelled.");
                return null;
            }

            Optional<Station> exact = directory.findByNameOrId(input);
            if (exact.isPresent()) {
                return exact.get();
            }

            List<Station> matches = directory.searchStations(input);
            if (matches.isEmpty()) {
                System.out.println("  [!] No station matched '" + input + "'. Try again or type 'hub' for major stops.");
                continue;
            }

            if (matches.size() == 1) {
                return matches.get(0);
            }

            System.out.println("  Multiple stations matched your input. Please choose one:");
            int displayCount = Math.min(8, matches.size());
            for (int i = 0; i < displayCount; i++) {
                Station s = matches.get(i);
                System.out.printf("    [%d] %s - %s (%s)\n", i + 1, s.getName(), s.getArea(), s.getDescription());
            }

            System.out.print("  Select (1-" + displayCount + ", or 0 to retype): ");
            String sel = scanner.nextLine().trim();
            try {
                int idx = Integer.parseInt(sel);
                if (idx >= 1 && idx <= displayCount) {
                    return matches.get(idx - 1);
                }
            } catch (NumberFormatException ignored) {}

            System.out.println("  Retrying station input...");
        }
    }

    private void handleStationDirectory() {
        System.out.println("\n--- BUS STATIONS DIRECTORY ---");
        System.out.println("  1. List Major Transit Hubs");
        System.out.println("  2. Search Station by Name / Area");
        System.out.println("  3. List All Stations Alphabetically");
        System.out.print("Select option (1-3): ");
        String sub = scanner.nextLine().trim();

        if ("1".equals(sub)) {
            List<Station> hubs = directory.getMajorHubs();
            System.out.println("\nMAJOR TRANSIT INTERCHANGE HUBS IN KATHMANDU VALLEY:");
            System.out.printf("%-20s %-25s %s\n", "Station Name", "Area", "Description");
            System.out.println("----------------------------------------------------------------------------------------------------");
            for (Station h : hubs) {
                System.out.printf("%-20s %-25s %s\n",
                        h.getName(), h.getArea(), h.getDescription());
            }
            System.out.println();
        } else if ("2".equals(sub)) {
            System.out.print("Enter search keyword: ");
            String kw = scanner.nextLine().trim();
            List<Station> results = directory.searchStations(kw);
            System.out.printf("\nFound %d matching station(s):\n", results.size());
            for (Station s : results) {
                System.out.printf("  * %-25s | Area: %-22s | Hub: %s\n",
                        s.getName(), s.getArea(), s.isMajorHub() ? "YES" : "No");
            }
            System.out.println();
        } else {
            List<Station> all = directory.getAllStationsSorted();
            System.out.printf("\nALL %d STATIONS IN KATHMANDU VALLEY:\n", all.size());
            for (int i = 0; i < all.size(); i++) {
                Station s = all.get(i);
                System.out.printf("  [%2d] %-30s (%s)\n", i + 1, s.getName(), s.getArea());
            }
            System.out.println();
        }
    }

    private void handleBusRoutesViewer() {
        System.out.println("\n--- PUBLIC BUS ROUTES IN KATHMANDU VALLEY ---");
        Collection<BusRoute> routes = graph.getAllRoutes();
        int idx = 1;
        for (BusRoute r : routes) {
            System.out.printf("\n[%d] %s (%s)\n", idx++, r.getName(), r.getCode());
            System.out.printf("    Operator: %s | Type: %s\n", r.getOperator(), r.getVehicleType().getDisplayName());
            System.out.printf("    Operating Hours: %s | Frequency: Every %d minutes\n",
                    r.getOperatingHours(), r.getFrequencyMinutes());
            System.out.printf("    Total Stops: %d\n", r.getStations().size());

            System.out.print("    Route Path: ");
            List<Station> stops = r.getStations();
            for (int s = 0; s < stops.size(); s++) {
                System.out.print(stops.get(s).getName());
                if (s < stops.size() - 1) System.out.print(" -> ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void handleFareRules() {
        System.out.println("\n================================================================================");
        System.out.println("        OFFICIAL KATHMANDU VALLEY FARE SLABS (DoTM Mandated)                   ");
        System.out.println("================================================================================");
        System.out.println(" Distance Slab                 Regular Fare (NPR)   Student / Senior Fare (45% Concession)");
        System.out.println(" -------------------------------------------------------------------------------");
        System.out.printf(" 0 to 5 km (Base fare)         Rs. %-17d Rs. %d\n",
                FareCalculatorService.FARE_UP_TO_5_KM,
                fareCalculator.calculateStudentFare(FareCalculatorService.FARE_UP_TO_5_KM));
        System.out.printf(" 5 to 10 km                    Rs. %-17d Rs. %d\n",
                FareCalculatorService.FARE_UP_TO_10_KM,
                fareCalculator.calculateStudentFare(FareCalculatorService.FARE_UP_TO_10_KM));
        System.out.printf(" 10 to 15 km                   Rs. %-17d Rs. %d\n",
                FareCalculatorService.FARE_UP_TO_15_KM,
                fareCalculator.calculateStudentFare(FareCalculatorService.FARE_UP_TO_15_KM));
        System.out.printf(" 15 to 20 km                   Rs. %-17d Rs. %d\n",
                FareCalculatorService.FARE_UP_TO_20_KM,
                fareCalculator.calculateStudentFare(FareCalculatorService.FARE_UP_TO_20_KM));
        System.out.printf(" Above 20 km                   Rs. %-17d Rs. %d\n",
                FareCalculatorService.FARE_ABOVE_20_KM,
                fareCalculator.calculateStudentFare(FareCalculatorService.FARE_ABOVE_20_KM));
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" Notes:");
        System.out.println("   * Concession requires valid Student ID card or Senior Citizen card.");
        System.out.println("   * In multi-leg journeys with transfers, each separate vehicle leg is ticketed.");
        System.out.println("================================================================================\n");
    }
}