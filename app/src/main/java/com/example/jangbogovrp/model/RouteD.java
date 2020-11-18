package com.example.jangbogovrp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RouteD implements Parcelable {
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
    @SerializedName("order_id")
    public String orderId;

    protected RouteD(Parcel in) {
        routeNumber = in.readInt();
        routeIndex = in.readInt();
        lat = in.readDouble();
        lon = in.readDouble();
        name = in.readString();
        price = in.readInt();
        address = in.readString();
        orderId = in.readString();
    }

    public static final Creator<RouteD> CREATOR = new Creator<RouteD>() {
        @Override
        public RouteD createFromParcel(Parcel in) {
            return new RouteD(in);
        }

        @Override
        public RouteD[] newArray(int size) {
            return new RouteD[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(routeNumber);
        dest.writeInt(routeIndex);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeString(address);
        dest.writeString(orderId);
    }
}
