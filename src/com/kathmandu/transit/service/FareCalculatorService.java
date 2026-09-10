package com.kathmandu.transit.service;

/**
 * Calculates public transport fares in Kathmandu Valley based on 
 * Nepal Department of Transport Management (DoTM) regulations.
 */
public class FareCalculatorService {

    // Official Kathmandu Valley fare slabs (in NPR)
    public static final int FARE_UP_TO_5_KM = 20;
    public static final int FARE_UP_TO_10_KM = 27;
    public static final int FARE_UP_TO_15_KM = 32;
    public static final int FARE_UP_TO_20_KM = 35;
    public static final int FARE_ABOVE_20_KM = 40;

    // Student discount percentage mandated by law
    public static final double STUDENT_DISCOUNT_RATE = 0.45;

    /**
     * Computes the regular single-leg fare based on distance in kilometers.
     *
     * @param distanceKm Distance travelled in km.
     * @return Fare in Nepalese Rupees (NPR).
     */
    public int calculateFare(double distanceKm) {
        if (distanceKm <= 0.0) {
            return 0;
        }
        if (distanceKm <= 5.0) {
            return FARE_UP_TO_5_KM;
        } else if (distanceKm <= 10.0) {
            return FARE_UP_TO_10_KM;
        } else if (distanceKm <= 15.0) {
            return FARE_UP_TO_15_KM;
        } else if (distanceKm <= 20.0) {
            return FARE_UP_TO_20_KM;
        } else {
            return FARE_ABOVE_20_KM;
        }
    }

    /**
     * Computes concession / student fare (45% discount).
     *
     * @param regularFare Regular fare in NPR.
     * @return Discounted student fare in NPR.
     */
    public int calculateStudentFare(int regularFare) {
        if (regularFare <= 0) return 0;
        int discounted = (int) Math.round(regularFare * (1.0 - STUDENT_DISCOUNT_RATE));
        // In practice in Kathmandu Valley, minimum student fare is Rs. 11 or 12
        return Math.max(11, discounted);
    }

    /**
     * Returns the description of the fare slab for a given distance.
     */
    public String getFareSlabDescription(double distanceKm) {
        if (distanceKm <= 5.0) {
            return "0 - 5 km slab: Rs. 20 (Base Fare)";
        } else if (distanceKm <= 10.0) {
            return "5 - 10 km slab: Rs. 27";
        } else if (distanceKm <= 15.0) {
            return "10 - 15 km slab: Rs. 32";
        } else if (distanceKm <= 20.0) {
            return "15 - 20 km slab: Rs. 35";
        } else {
            return "Above 20 km slab: Rs. 40";
        }
    }
}
