package com.example.tripreminder;

import java.util.Calendar;
import java.util.Date;

public class Trip {
    private Date date;
    private String name;
    private String state;
    private String start;
    private String end;
    private static boolean expand;
    private String key;
    private String notes;
    private String way;
    private String repeat;

    public Trip() {
    }



    public Trip(Date date, String name, String state, String start, String end, String key, String notes, String way, String repeat) {
        this.date = date;
        this.name = name;
        this.state = state;
        this.start = start;
        this.end = end;
        this.key = key;
        this.notes = notes;
        this.way = way;
        this.repeat = repeat;
        this.expand = false;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
}
