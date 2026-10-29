package com.example.utis;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

@IgnoreExtraProperties
public class RouteSchedule {
    @PropertyName("Route")
    private String route;
    
    @PropertyName("Source")
    private String source;
    
    @PropertyName("Source Time")
    private String sourceTime;
    
    @PropertyName("Destination")
    private String destination;
    
    @PropertyName("Destination Time")
    private String destinationTime;

    // Default constructor required for calls to DataSnapshot.getValue(RouteSchedule.class)
    public RouteSchedule() {
    }

    public RouteSchedule(String route, String source, String sourceTime, String destination, String destinationTime) {
        this.route = route;
        this.source = source;
        this.sourceTime = sourceTime;
        this.destination = destination;
        this.destinationTime = destinationTime;
    }

    @PropertyName("Route")
    public String getRoute() {
        return route;
    }

    @PropertyName("Route")
    public void setRoute(String route) {
        this.route = route;
    }

    @PropertyName("Source")
    public String getSource() {
        return source;
    }

    @PropertyName("Source")
    public void setSource(String source) {
        this.source = source;
    }

    @PropertyName("Source Time")
    public String getSourceTime() {
        return sourceTime;
    }

    @PropertyName("Source Time")
    public void setSourceTime(String sourceTime) {
        this.sourceTime = sourceTime;
    }

    @PropertyName("Destination")
    public String getDestination() {
        return destination;
    }

    @PropertyName("Destination")
    public void setDestination(String destination) {
        this.destination = destination;
    }

    @PropertyName("Destination Time")
    public String getDestinationTime() {
        return destinationTime;
    }

    @PropertyName("Destination Time")
    public void setDestinationTime(String destinationTime) {
        this.destinationTime = destinationTime;
    }
}
