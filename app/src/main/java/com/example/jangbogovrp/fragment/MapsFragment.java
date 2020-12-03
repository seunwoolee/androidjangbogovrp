package com.example.jangbogovrp.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.jangbogovrp.R;
import com.example.jangbogovrp.activity.MainActivity;
import com.example.jangbogovrp.http.HttpService;
import com.example.jangbogovrp.http.RetrofitClient;
import com.example.jangbogovrp.model.RouteD;
import com.example.jangbogovrp.model.User;
import com.example.jangbogovrp.utils.Tools;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsFragment extends Fragment {
    private final String TAG = "MapsFragment";
    private List<RouteD> mRouteDS = new ArrayList<RouteD>();
    private boolean isDrawn = false;
    private boolean isAm = true;
    private IsAmButtonClicked isAmButtonClicked;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (!isDrawn) {
                for (RouteD routed : mRouteDS) {
                    LatLng latLng = new LatLng(routed.lat, routed.lon);
                    googleMap.addMarker(new MarkerOptions().position(latLng).title(routed.name));
                }

                Context context = getContext();
                assert context != null;
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                isDrawn = true;

                FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(context);
                Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                locationTask.addOnSuccessListener(location -> {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getAltitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
                });


            }
        }
    };

    public void setIsAmButtonClicked(IsAmButtonClicked isAmButtonClicked) {
        this.isAmButtonClicked = isAmButtonClicked;
    }

    public interface IsAmButtonClicked {
        void buttonClicked();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        mRouteDS = getArguments().getParcelableArrayList("routeDs");
        if (mRouteDS.size() == 0) {
            Toast.makeText(getContext(), "배송 데이터가 없습니다.", Toast.LENGTH_LONG).show();
        }
        isAm = getArguments().getBoolean("isAm");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup root_view = (ViewGroup) inflater.inflate(R.layout.fragment_maps, container, false);
        Button button = root_view.findViewById(R.id.isAm);

        if (isAm) {
            button.setText("오전");
        } else {
            button.setText("오후");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAmButtonClicked.buttonClicked();
            }
        });
        return root_view;
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