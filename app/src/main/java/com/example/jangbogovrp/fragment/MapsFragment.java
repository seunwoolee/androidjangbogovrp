package com.example.jangbogovrp.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jangbogovrp.R;
import com.example.jangbogovrp.activity.MainActivity;
import com.example.jangbogovrp.adapter.CustomerListAdapter;
import com.example.jangbogovrp.adapter.OrderDetailAdapter;
import com.example.jangbogovrp.http.HttpService;
import com.example.jangbogovrp.http.RetrofitClient;
import com.example.jangbogovrp.model.OrderDetail;
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
import com.skt.Tmap.TMapTapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.jangbogovrp.fragment.MainFragment.onOrderBtnClickListener;
import static com.example.jangbogovrp.fragment.MainFragment.onTmapBtnClickListener;

public class MapsFragment extends Fragment {
    private final String TAG = "MapsFragment";
    private TMapTapi mTmap;
    private List<RouteD> mRouteDS = new ArrayList<RouteD>();
    private boolean isDrawn = false;
    private boolean isAm = true;
    private Context mContext;
    private IsAmButtonClicked isAmButtonClicked;
    private final HttpService mHttpService;

    public MapsFragment(HttpService httpService) {
        mHttpService = httpService;
    }

    @SuppressLint("DefaultLocale")
    private final GoogleMap.OnMarkerClickListener onMarkerClickListener = marker -> {
        RouteD routeD = null;
        for (int i = 0; i < mRouteDS.size(); i++) {
            if (mRouteDS.get(i).name.equals(marker.getTitle())) {
                routeD = mRouteDS.get(i);
                break;
            }
        }

        View view = getLayoutInflater().inflate(R.layout.item_customers, null, false);
        ((TextView) view.findViewById(R.id.seq)).setText("");
        ((TextView) view.findViewById(R.id.name)).setText(routeD.name);
        ((TextView) view.findViewById(R.id.price)).setText(String.format("%s원", String.format("%,d", routeD.price)));
        ((TextView) view.findViewById(R.id.address)).setText(routeD.address);


        Button detail = (Button) view.findViewById(R.id.detail);
        ImageView tmap = (ImageView) view.findViewById(R.id.tmap);
        CardView cardView = (CardView) view.findViewById(R.id.card);
        cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.quantum_white_100));
        RouteD finalRouteD = routeD;
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onOrderBtnClickListener != null) {
                    onOrderBtnClickListener.onBtnClick(mHttpService, finalRouteD.orderId, mContext);
                }
            }
        });
        tmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTmapBtnClickListener != null) {
                    onTmapBtnClickListener.onItemClick(finalRouteD, mTmap, mContext);
                }
            }
        });

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();
        return true;
    };

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
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
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.setOnMarkerClickListener(onMarkerClickListener);
                isDrawn = true;

                FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(context);
                Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                locationTask.addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getAltitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
                    }
                });
            }
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

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
        if (mRouteDS == null) {
            mRouteDS = new ArrayList<RouteD>();
        }
        isAm = getArguments().getBoolean("isAm");
        initTmap();
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

    private void initTmap() {
        mTmap = new TMapTapi(mContext);
        mTmap.setSKTMapAuthentication("0de9ecde-b87c-404c-b7f8-be4ed7b85d4f");
    }

}