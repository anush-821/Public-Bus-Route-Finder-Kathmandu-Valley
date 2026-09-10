package com.kathmandu.transit.model;

/**
 * Types of public transport vehicles operating in Kathmandu Valley.
 */
public enum VehicleType {
    BUS("Standard Bus (ठूलो बस)", "[BUS]"),
    MICROBUS("Microbus / HiAce (माइक्रोबस)", "[MICRO]"),
    SAFA_TEMPO("Safa Tempo Electric (सफा टेम्पो)", "[TEMPO]");

    private final String displayName;
    private final String tag;

    VehicleType(String displayName, String tag) {
        this.displayName = displayName;
        this.tag = tag;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return tag + " " + displayName;
    }
}
