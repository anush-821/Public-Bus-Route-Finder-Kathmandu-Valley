package com.kathmandu.transit.test;

import com.kathmandu.transit.data.KathmanduTransitDataLoader;
import com.kathmandu.transit.model.*;
import com.kathmandu.transit.service.FareCalculatorService;
import com.kathmandu.transit.service.RouteFinderService;
import com.kathmandu.transit.service.StationDirectoryService;
import com.kathmandu.transit.service.TransitGraph;

import java.util.List;
import java.util.Optional;

public class TransitAppTest {

    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Running Automated Tests for Kathmandu Valley Transit");
        System.out.println("==================================================");

        testDataLoader();
        testFareCalculator();
        testStationSearch();
        testDirectRouting();
        testMultiLegTransferRouting();

        System.out.println("\n--------------------------------------------------");
        System.out.printf("Test Summary: %d PASSED, %d FAILED\n", passedTests, failedTests);
        System.out.println("--------------------------------------------------");

        if (failedTests > 0) {
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println(" [PASS] " + testName);
            passedTests++;
        } else {
            System.err.println(" [FAIL] " + testName);
            failedTests++;
        }
    }

    private static void testDataLoader() {
        TransitGraph graph = KathmanduTransitDataLoader.loadData();
        assertTrue("Data Loader loads at least 40 stations", graph.getAllStations().size() >= 40);
        assertTrue("Data Loader loads at least 10 bus routes", graph.getAllRoutes().size() >= 10);
        assertTrue("Ratnapark station exists", graph.getStationById("RATNAPARK") != null);
        assertTrue("Suryabinayak station exists", graph.getStationById("SURYABINAYAK") != null);
    }

    private static void testFareCalculator() {
        FareCalculatorService calc = new FareCalculatorService();
        assertTrue("Fare for 3.5 km should be 20", calc.calculateFare(3.5) == 20);
        assertTrue("Fare for 7.5 km should be 27", calc.calculateFare(7.5) == 27);
        assertTrue("Fare for 13.0 km should be 32", calc.calculateFare(13.0) == 32);
        assertTrue("Fare for 18.5 km should be 35", calc.calculateFare(18.5) == 35);
        assertTrue("Fare for 25.0 km should be 40", calc.calculateFare(25.0) == 40);

        assertTrue("Student fare for Rs. 20 is Rs. 11", calc.calculateStudentFare(20) == 11);
        assertTrue("Student fare for Rs. 27 is Rs. 15", calc.calculateStudentFare(27) == 15);
        assertTrue("Student fare for Rs. 40 is Rs. 22", calc.calculateStudentFare(40) == 22);
    }

    private static void testStationSearch() {
        TransitGraph graph = KathmanduTransitDataLoader.loadData();
        StationDirectoryService directory = new StationDirectoryService(graph);

        List<Station> results = directory.searchStations("banesh");
        assertTrue("Search 'banesh' returns New Baneshwor", 
                results.stream().anyMatch(s -> s.getId().equals("NEW_BANESHWOR")));

        List<Station> devanagari = directory.searchStations("रत्न");
        assertTrue("Search Devanagari 'रत्न' returns Ratnapark", 
                devanagari.stream().anyMatch(s -> s.getId().equals("RATNAPARK")));

        List<Station> hubs = directory.getMajorHubs();
        assertTrue("Hubs include Kalanki and Ratnapark",
                hubs.stream().anyMatch(s -> s.getId().equals("KALANKI")) &&
                hubs.stream().anyMatch(s -> s.getId().equals("RATNAPARK")));
    }

    private static void testDirectRouting() {
        TransitGraph graph = KathmanduTransitDataLoader.loadData();
        FareCalculatorService fareCalc = new FareCalculatorService();
        RouteFinderService router = new RouteFinderService(graph, fareCalc);

        Station ratnapark = graph.getStationById("RATNAPARK");
        Station budhanilkantha = graph.getStationById("BUDHANILKANTHA");

        List<JourneyPlan> direct = router.findDirectRoutes(ratnapark, budhanilkantha);
        assertTrue("Direct route exists between Ratnapark and Budhanilkantha", !direct.isEmpty());
        assertTrue("Direct route is Sajha Yatayat Line 1", 
                direct.get(0).getLegs().get(0).getRoute().getId().equals("SAJHA_01"));
        assertTrue("Direct route has 0 transfers", direct.get(0).getTransferCount() == 0);
    }

    private static void testMultiLegTransferRouting() {
        TransitGraph graph = KathmanduTransitDataLoader.loadData();
        FareCalculatorService fareCalc = new FareCalculatorService();
        RouteFinderService router = new RouteFinderService(graph, fareCalc);

        Station koteshwor = graph.getStationById("KOTESHWOR");
        Station budhanilkantha = graph.getStationById("BUDHANILKANTHA");

        Optional<JourneyPlan> fastest = router.findRoute(koteshwor, budhanilkantha, RouteFinderService.Preference.FASTEST);
        assertTrue("Fastest route exists between Koteshwor and Budhanilkantha", fastest.isPresent());
        if (fastest.isPresent()) {
            JourneyPlan plan = fastest.get();
            assertTrue("Plan has at least 1 leg", !plan.getLegs().isEmpty());
            assertTrue("Total distance > 0", plan.getTotalDistanceKm() > 0);
            assertTrue("Total duration > 0", plan.getTotalDurationMinutes() > 0);
            assertTrue("Fare calculated", plan.getTotalFareNpr() > 0);
        }
    }
}
