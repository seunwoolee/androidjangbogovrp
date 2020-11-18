package com.example.jangbogovrp.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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
    private boolean isDrawn = false;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
       @Override
        public void onMapReady(GoogleMap googleMap) {
            if (!isDrawn) {
                LatLng latLng = null;
                for (RouteD routed : mRouteDS) {
                    latLng = new LatLng(routed.lat, routed.lon);
                    googleMap.addMarker(new MarkerOptions().position(latLng).title(routed.name));
                }

                if (latLng != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                    isDrawn = true;
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        mRouteDS = getArguments().getParcelableArrayList("routeDs");
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