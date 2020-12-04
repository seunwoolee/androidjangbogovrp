package com.example.jangbogovrp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

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
    private List<RouteD> mRouteDS = new ArrayList<RouteD>();
    private List<OrderDetail> mOrderDetails = new ArrayList<OrderDetail>();
    private HttpService mHttpService;

    private CustomerListAdapter.OnOrderBtnClickListener onOrderBtnClickListener = new CustomerListAdapter.OnOrderBtnClickListener() {
        @Override
        public void onBtnClick(String orderId) {
            ArrayList<String> orderIds = new ArrayList<String>();
            orderIds.add(orderId);

            Call<List<OrderDetail>> call = mHttpService.getDetailOrders(orderIds);
            call.enqueue(new Callback<List<OrderDetail>>() {
                @Override
                public void onResponse(Call<List<OrderDetail>> call, Response<List<OrderDetail>> response) {
                    if (response.isSuccessful()) {
                        mOrderDetails = (ArrayList<OrderDetail>) response.body();
                        showOrderDetailDialog((ArrayList<OrderDetail>) mOrderDetails);
                    }
                }

                @Override
                public void onFailure(Call<List<OrderDetail>> call, Throwable t) {

                }
            });
        }
    };

    private CustomerListAdapter.OnTmapBtnClickListener onTmapBtnClickListener = new CustomerListAdapter.OnTmapBtnClickListener() {
        @Override
        public void onItemClick(RouteD obj) {
            if(!mTmap.isTmapApplicationInstalled()) {
                Toast.makeText(mContext, "Tmap을 설치해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            mTmap.invokeRoute(obj.address, (float) obj.lon, (float) obj.lat);
        }
    };

    private void showOrderDetailDialog(ArrayList<OrderDetail> orderDetails) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_order_detail);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        OrderDetailAdapter adapter = new OrderDetailAdapter(mContext, orderDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        if (mRouteDS.size() == 0) {
            Toast.makeText(getContext(), "배송 데이터가 없습니다.", Toast.LENGTH_LONG).show();
        }
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

        DateFormat mmddFormat = new SimpleDateFormat("MM월dd일", Locale.KOREAN);
        DateFormat weekdayFormat = new SimpleDateFormat("EEE요일", Locale.KOREAN);
        Date date = new Date();

        TextView mmdd = root_view.findViewById(R.id.mmdd);
        TextView weekday = root_view.findViewById(R.id.weekday);

        mmdd.setText(mmddFormat.format(date));
        weekday.setText(weekdayFormat.format(date));

        RecyclerView mRecyclerView = (RecyclerView) root_view.findViewById(R.id.recyclerView);
        CustomerListAdapter mAdapter = new CustomerListAdapter(mContext, mRouteDS);
        mAdapter.setOnOrderBtnClickListener(onOrderBtnClickListener);
        mAdapter.setOnTmapBtnClickListener(onTmapBtnClickListener);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        return root_view;
    }

    private void initTmap() {
        mTmap = new TMapTapi(mContext);
        mTmap.setSKTMapAuthentication("0de9ecde-b87c-404c-b7f8-be4ed7b85d4f");
    }


}