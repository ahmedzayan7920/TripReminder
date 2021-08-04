package com.example.tripreminder;

public class Trip {
    private String date;
    private String time;
    private String name;
    private String state;
    private String start;
    private String destination;
    private static boolean expand;

    public Trip(String date, String time, String name, String state, String start, String destination) {
        this.date = date;
        this.time = time;
        this.name = name;
        this.state = state;
        this.start = start;
        this.destination = destination;
        this.expand = false;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }
}
