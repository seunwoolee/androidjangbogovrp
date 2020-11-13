package com.example.jangbogovrp.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.jangbogovrp.R;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

public class MainFragment extends Fragment {
    private TMapTapi mTmap;
    private View root_view;
    private Context mContext;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
//        root_view = inflater.inflate(R.layout.fragment_main, container, false);
//        LinearLayout linearLayoutTmap = (LinearLayout) root_view.findViewById(R.id.linearLayoutTmap);
//        TMapView tMapView = new TMapView(mContext);
//        tMapView.setSKTMapApiKey("0de9ecde-b87c-404c-b7f8-be4ed7b85d4f");
//        linearLayoutTmap.addView(tMapView);
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    private void initTmap() {
        mTmap = new TMapTapi(mContext);
        mTmap.setSKTMapAuthentication("0de9ecde-b87c-404c-b7f8-be4ed7b85d4f");
    }
}