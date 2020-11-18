package com.example.jangbogovrp.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.jangbogovrp.R;
import com.example.jangbogovrp.adapter.CustomerListAdapter;
import com.example.jangbogovrp.http.HttpService;
import com.example.jangbogovrp.http.RetrofitClient;
import com.example.jangbogovrp.model.RouteD;
import com.example.jangbogovrp.model.User;
import com.example.jangbogovrp.utils.Tools;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment {
    private final String TAG = "MainFragment";
    private TMapTapi mTmap;
    private Context mContext;
    private CustomerListAdapter mAdapter;
    private List<RouteD> mRouteDS = new ArrayList<RouteD>();
    private RecyclerView mRecyclerView;
    Realm mRealm;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Tools.initRealm(mContext);
        initTmap();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root_view = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) root_view.findViewById(R.id.recyclerView);
        if(mRouteDS.size() == 0) {
            User user = mRealm.where(User.class).findFirst();
            HttpService httpService = RetrofitClient.getHttpService(user.key);
            Call<List<RouteD>> call = httpService.getRouteDs();
            Callback<List<RouteD>> callback = new Callback<List<RouteD>>() {
                @Override
                public void onResponse(Call<List<RouteD>> call, Response<List<RouteD>> response) {
                    Log.d(TAG, "성공");
                    if (response.isSuccessful()) {
                        mRouteDS = response.body();
                        mAdapter = new CustomerListAdapter(mContext, mRouteDS);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mRecyclerView.setHasFixedSize(true);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }

                @Override
                public void onFailure(Call<List<RouteD>> call, Throwable t) {

                }
            };
            call.enqueue(callback);
        } else {
            mAdapter = new CustomerListAdapter(mContext, mRouteDS);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(mAdapter);
        }

        return root_view;
    }

    private void initTmap() {
        mTmap = new TMapTapi(mContext);
        mTmap.setSKTMapAuthentication("0de9ecde-b87c-404c-b7f8-be4ed7b85d4f");
//        linearLayoutTmap.addView(tMapView);

    }
}