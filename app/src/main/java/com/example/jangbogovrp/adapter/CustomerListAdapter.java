package com.example.jangbogovrp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jangbogovrp.R;
import com.example.jangbogovrp.model.RouteD;

import java.util.ArrayList;
import java.util.List;

public class CustomerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CustomerListAdapter";
    private Context mContext;
    private List<RouteD> mRouteDs = new ArrayList<RouteD>();
    private OnOrderBtnClickListener mOnOrderBtnClickListener;

    public void setOnOrderBtnClickListener(final OnOrderBtnClickListener onOrderBtnClickListener) {
        mOnOrderBtnClickListener = onOrderBtnClickListener;
    }

    public CustomerListAdapter(Context context, List<RouteD> routeDs) {
        mRouteDs = routeDs;
        mContext = context;
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView seq;
        public TextView name;
        public TextView price;
        public TextView address;
        public Button detail;
        public View tmap;
        public CardView cardView;

        public OriginalViewHolder(View v) {
            super(v);
            seq = (TextView) v.findViewById(R.id.seq);
            name = (TextView) v.findViewById(R.id.name);
            price = (TextView) v.findViewById(R.id.price);
            address = (TextView) v.findViewById(R.id.address);
            detail = (Button) v.findViewById(R.id.detail);
            tmap = (ImageView) v.findViewById(R.id.tmap);
            cardView = (CardView) v.findViewById(R.id.card);

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customers, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint({"ResourceAsColor", "DefaultLocale"})
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            RouteD routeD = mRouteDs.get(position);
            view.seq.setText(String.format("%s", String.valueOf(routeD.routeIndex)));
            view.name.setText(routeD.name);
            view.price.setText(String.format("%s원", String.format("%,d", routeD.price)));
            view.address.setText(routeD.address);

            int color = R.color.yellow_100;
            if (position % 4 == 1) {
                color = R.color.green_100;
            } else if (position % 4 == 2) {
                color = R.color.blue_100;
            } else if (position % 4 == 3) {
                color = R.color.red_100;
            }
            view.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, color));

            view.detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnOrderBtnClickListener != null) {
                        mOnOrderBtnClickListener.onBtnClick(routeD.orderId);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mRouteDs.size();
    }

    public interface OnOrderBtnClickListener {
        void onBtnClick(String orderId);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, RouteD obj, int pos);
    }

}