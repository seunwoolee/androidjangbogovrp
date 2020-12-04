package com.example.jangbogovrp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jangbogovrp.R;
import com.example.jangbogovrp.model.OrderDetail;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "OrderDetailAdapter";
    private Context mContext;
    private List<OrderDetail> mOrderDetails = new ArrayList<OrderDetail>();

    public OrderDetailAdapter(Context context, List<OrderDetail> orderDetails) {
        mOrderDetails = orderDetails;
        mContext = context;
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {
//        public ImageView image;
        public TextView productName;
        public TextView price;
        public TextView count;

        public OriginalViewHolder(View v) {
            super(v);
//            image = (ImageView) v.findViewById(R.id.image);
            productName = (TextView) v.findViewById(R.id.productName);
            price = (TextView) v.findViewById(R.id.price);
            count = (TextView) v.findViewById(R.id.count);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_details, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint({"ResourceAsColor", "DefaultLocale"})
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            OrderDetail orderDetail = mOrderDetails.get(position);
            view.productName.setText(String.format("%s원", orderDetail.productName));
            view.price.setText(String.format("%s원", String.format("%,d", orderDetail.price)));
            view.count.setText(String.format("%sea", String.format("%,d", orderDetail.count)));
        }
    }

    @Override
    public int getItemCount() {
        return mOrderDetails.size();
    }


}