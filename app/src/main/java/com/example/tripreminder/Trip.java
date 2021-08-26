package com.example.tripreminder;

import java.util.ArrayList;
import java.util.Calendar;

public class Trip {
    private Calendar goDate;
    private Calendar returnDate;
    private String name;
    private String state;
    private String start;
    private String end;
    private boolean expand = false;
    private String key;
    private ArrayList<String> notes;
    private String way;
    private String repeat;

    public Trip() {

    }



    public Trip(Calendar goDate, Calendar returnDate, String name, String state, String start, String end, String key, ArrayList<String> notes, String way, String repeat) {
        this.goDate = goDate;
        this.returnDate = returnDate;
        this.name = name;
        this.state = state;
        this.start = start;
        this.end = end;
        this.key = key;
        this.notes = notes;
        this.way = way;
        this.repeat = repeat;
    }

    public Trip(Calendar goDate, String name, String state, String start, String end, String key, ArrayList<String> notes, String way, String repeat) {
        this.goDate = goDate;
        this.returnDate = returnDate;
        this.name = name;
        this.state = state;
        this.start = start;
        this.end = end;
        this.key = key;
        this.notes = notes;
        this.way = way;
        this.repeat = repeat;
    }

    public Calendar getGoDate() {
        return goDate;
    }

    public void setGoDate(Calendar goDate) {
        this.goDate = goDate;
    }

    public Calendar getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Calendar returnDate) {
        this.returnDate = returnDate;
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

    public ArrayList<String> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<String> notes) {
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
