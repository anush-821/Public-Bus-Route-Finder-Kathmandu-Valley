package com.kathmandu.transit.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a public bus route / line operating in the Kathmandu Valley.
 */
public class BusRoute {
    private final String id;
    private final String code;
    private final String name;
    private final String operator;
    private final VehicleType vehicleType;
    private final List<Station> stations;
    private final int frequencyMinutes;
    private final String operatingHours;
    private final String colorHex;
    private final boolean bidirectional;

    public BusRoute(String id, String code, String name, String operator,
                    VehicleType vehicleType, List<Station> stations,
                    int frequencyMinutes, String operatingHours,
                    String colorHex, boolean bidirectional) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.operator = operator;
        this.vehicleType = vehicleType;
        this.stations = new ArrayList<>(stations);
        this.frequencyMinutes = frequencyMinutes;
        this.operatingHours = operatingHours;
        this.colorHex = colorHex;
        this.bidirectional = bidirectional;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getOperator() {
        return operator;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public List<Station> getStations() {
        return Collections.unmodifiableList(stations);
    }

    public int getFrequencyMinutes() {
        return frequencyMinutes;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public String getColorHex() {
        return colorHex;
    }

    public boolean isBidirectional() {
        return bidirectional;
    }

    /**
     * Checks if this route serves a given station.
     */
    public boolean serves(Station station) {
        return stations.contains(station);
    }

    /**
     * Finds index of a station along this route.
     */
    public int indexOf(Station station) {
        return stations.indexOf(station);
    }

    /**
     * Returns true if both stations are served in order.
     */
    public boolean connectsInOrder(Station from, Station to) {
        int i1 = stations.indexOf(from);
        int i2 = stations.indexOf(to);
        return i1 >= 0 && i2 >= 0 && i1 < i2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusRoute busRoute = (BusRoute) o;
        return Objects.equals(id, busRoute.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[" + code + "] " + name + " (" + operator + ")";
    }
}
