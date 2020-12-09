package com.example.jangbogovrp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jangbogovrp.R;
import com.example.jangbogovrp.adapter.CustomerListAdapter;
import com.example.jangbogovrp.adapter.OrderDetailAdapter;
import com.example.jangbogovrp.http.HttpService;
import com.example.jangbogovrp.model.OrderDetail;
import com.example.jangbogovrp.model.RouteD;
import com.skt.Tmap.TMapTapi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainFragment extends Fragment {
    private final String TAG = "MainFragment";
    private TMapTapi mTmap;
    private Context mContext;
    private List<RouteD> mRouteDS = new ArrayList<>();
    private final HttpService mHttpService;

    public static final CustomerListAdapter.OnOrderBtnClickListener onOrderBtnClickListener = (httpService, orderId, context) -> {
        ArrayList<String> orderIds = new ArrayList<>();
        orderIds.add(orderId);

        Call<List<OrderDetail>> call = httpService.getDetailOrders(orderIds);
        call.enqueue(new Callback<List<OrderDetail>>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderDetail>> call, @NonNull Response<List<OrderDetail>> response) {
                if (response.isSuccessful()) {
                    showOrderDetailDialog((ArrayList<OrderDetail>) response.body(), context);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderDetail>> call, @NonNull Throwable t) {

            }
        });
    };

    public static final CustomerListAdapter.OnTmapBtnClickListener onTmapBtnClickListener = (obj, tmap, context) -> {
        if (!tmap.isTmapApplicationInstalled()) {
            Toast.makeText(context, "Tmap을 설치해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        tmap.invokeRoute(obj.address, (float) obj.lon, (float) obj.lat);

    };

    public static void showOrderDetailDialog(ArrayList<OrderDetail> orderDetails, Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_order_detail);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        OrderDetailAdapter adapter = new OrderDetailAdapter(context, orderDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();
    }

    public MainFragment(HttpService httpService) {
        mHttpService = httpService;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        mRouteDS = getArguments().getParcelableArrayList("routeDs");
        if (mRouteDS == null) {
            mRouteDS = new ArrayList<>();
        }
        initTmap();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root_view = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        DateFormat mmddFormat = new SimpleDateFormat("MM월dd일", Locale.KOREAN);
        DateFormat weekdayFormat = new SimpleDateFormat("EEE요일", Locale.KOREAN);
        Date date = new Date();

        TextView mmdd = root_view.findViewById(R.id.mmdd);
        TextView weekday = root_view.findViewById(R.id.weekday);

        mmdd.setText(mmddFormat.format(date));
        weekday.setText(weekdayFormat.format(date));

        RecyclerView mRecyclerView = (RecyclerView) root_view.findViewById(R.id.recyclerView);
        CustomerListAdapter adapter = new CustomerListAdapter(mContext, mRouteDS, mTmap, mHttpService);
        adapter.setOnOrderBtnClickListener(onOrderBtnClickListener);
        adapter.setOnTmapBtnClickListener(onTmapBtnClickListener);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        return root_view;
    }

    private void initTmap() {
        mTmap = new TMapTapi(mContext);
        mTmap.setSKTMapAuthentication("0de9ecde-b87c-404c-b7f8-be4ed7b85d4f");
    }
}