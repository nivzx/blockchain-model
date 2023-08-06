package org.example;

public class Transaction {
    private String location;
    private double signalLevel;

    // Constructor
    public Transaction(String location, double signalLevel) {
        this.location = location;
        this.signalLevel = signalLevel;
    }

    // Getters
    public String getLocation() {
        return location;
    }

    public double getSignalLevel() {
        return signalLevel;
    }

    // Setters
    public void setLocation(String location) {
        this.location = location;
    }

    public void setSignalLevel(double signalLevel) {
        this.signalLevel = signalLevel;
    }
}
