package com.example.jangbogovrp.http;


import com.example.jangbogovrp.model.OrderDetail;
import com.example.jangbogovrp.model.RouteD;
import com.example.jangbogovrp.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HttpService {
//    @Headers({
//            "Authorization: Token b553b4d63c2a1b679f4140f430da57198aec162b"
//    })
    @GET("/delivery/android_routeD/")
    Call<List<RouteD>> getRouteDs(@Query("isAm") boolean isAm);

    @GET("/customer/get_orders/")
    Call<List<OrderDetail>> getDetailOrders(@Query("order_ids[]") ArrayList<String> orderIds);

    @FormUrlEncoded
    @POST("/rest-auth/login/")
    Call<User> login(@Field("username") String username, @Field("password") String password);

}


