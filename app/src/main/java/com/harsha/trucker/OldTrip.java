package com.harsha.trucker;

/**
 * Created by LENOVO on 08-01-2018.
 */

public class OldTrip {

    private String name, realName, source, destination, cost, date, rideDuration;



    public OldTrip(String name, String realName, String source, String destination, String cost, String date, String rideDuration) {
        this.name = name;
        this.realName = realName;
        this.source = source;
        this.destination = destination;
        this.cost = cost;
        this.date = date;
        this.rideDuration = rideDuration;
    }

    public String getName() {
        return name;
    }

    public String getRealName() {
        return realName;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getCost() {
        return cost;
    }

    public String getDate() {
        return date;
    }

    public String getRideDuration() {
        return rideDuration;
    }
}




