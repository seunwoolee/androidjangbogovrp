package com.example.jangbogovrp.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jangbogovrp.R;
import com.example.jangbogovrp.http.HttpService;
import com.example.jangbogovrp.http.RetrofitClient;
import com.example.jangbogovrp.model.RouteD;
import com.example.jangbogovrp.model.User;
import com.example.jangbogovrp.utils.Tools;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsFragment extends Fragment {
    private final String TAG = "MapsFragment";
    private List<RouteD> mRouteDS = new ArrayList<RouteD>();

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Context context = getContext();
            Realm realm = Tools.initRealm(context);

            User user = realm.where(User.class).findFirst();
            HttpService httpService = RetrofitClient.getHttpService(user.key);
            Call<List<RouteD>> call = httpService.getRouteDs();
            Callback<List<RouteD>> callback = new Callback<List<RouteD>>() {
                @Override
                public void onResponse(Call<List<RouteD>> call, Response<List<RouteD>> response) {
                    Log.d(TAG, "성공");
                    if (response.isSuccessful()) {
                        mRouteDS = response.body();
                        LatLng latLng = null;
                        for (RouteD routed : mRouteDS) {
                            latLng = new LatLng(routed.lat, routed.lon);
                            googleMap.addMarker(new MarkerOptions().position(latLng).title(routed.name));
                        }

                        if (latLng != null) {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<RouteD>> call, Throwable t) {

                }
            };
            call.enqueue(callback);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}