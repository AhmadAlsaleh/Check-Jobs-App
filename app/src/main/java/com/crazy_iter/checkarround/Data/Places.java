package com.crazy_iter.checkarround.Data;

/**
 * Created by CrazyITer on 3/24/2018.
 */

public class Places {
    private String id, title;
    private Double lat, lon;

    public Places(String id, String title, Double lat, Double lon) {
        this.id = id;
        this.title = title;
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
