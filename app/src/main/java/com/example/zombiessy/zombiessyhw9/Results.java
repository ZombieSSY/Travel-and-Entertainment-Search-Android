package com.example.zombiessy.zombiessyhw9;

public class Results {
    private String icon;
    private String name;
    private String address;
    private String place_id;
    private double lat;
    private double lng;

    public Results(String icon, String name, String address, String place_id, double lat, double lng) {
        this.icon = icon;
        this.name = name;
        this.address = address;
        this.place_id = place_id;
        this.lat = lat;
        this.lng = lng;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
