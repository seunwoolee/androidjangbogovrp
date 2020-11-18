package com.example.jangbogovrp.model;

import com.google.gson.annotations.SerializedName;

public class RouteD {
    @SerializedName("route_number")
    public int routeNumber;
    @SerializedName("route_index")
    public int routeIndex;
    @SerializedName("lat")
    public double lat;
    @SerializedName("lon")
    public double lon;
    @SerializedName("name")
    public String name;
    @SerializedName("price")
    public int price;
    @SerializedName("address")
    public String address;
}
