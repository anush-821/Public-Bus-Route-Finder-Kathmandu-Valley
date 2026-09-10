package com.kathmandu.transit.model;

import java.util.Objects;

/**
 * Represents a transit bus stop or major transit station in the Kathmandu Valley.
 */
public class Station {
    private final String id;
    private final String name;
    private final String nepaliName;
    private final String area;
    private final double latitude;
    private final double longitude;
    private final boolean majorHub;
    private final String description;

    public Station(String id, String name, String nepaliName, String area, 
                   double latitude, double longitude, boolean majorHub, String description) {
        this.id = id;
        this.name = name;
        this.nepaliName = nepaliName;
        this.area = area;
        this.latitude = latitude;
        this.longitude = longitude;
        this.majorHub = majorHub;
        this.description = description;
    }

    public Station(String id, String name, String nepaliName, String area, 
                   double latitude, double longitude, boolean majorHub) {
        this(id, name, nepaliName, area, latitude, longitude, majorHub, "");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNepaliName() {
        return nepaliName;
    }

    public String getArea() {
        return area;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isMajorHub() {
        return majorHub;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Formatted display label including both English and Nepali names.
     */
    public String getDisplayLabel() {
        if (nepaliName != null && !nepaliName.isEmpty()) {
            return name + " (" + nepaliName + ")";
        }
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(id, station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getDisplayLabel();
    }
}
