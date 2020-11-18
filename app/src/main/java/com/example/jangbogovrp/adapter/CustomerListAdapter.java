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

import androidx.recyclerview.widget.RecyclerView;

import com.example.jangbogovrp.R;
import com.example.jangbogovrp.model.RouteD;

import java.util.ArrayList;
import java.util.List;

public class CustomerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CustomerListAdapter";
    private Context mContext;
    private List<RouteD> mRouteDs;
    private OnItemClickListener mOnItemClickListener;
    private OnCheckboxClickListener mOnCheckboxClickListener;

    public void setOnCheckBoxClickListener(final OnCheckboxClickListener onCheckBoxClickListener) {
        this.mOnCheckboxClickListener = onCheckBoxClickListener;
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public CustomerListAdapter(Context context, List<RouteD> routeDs) {
        mRouteDs = routeDs;
        mContext = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView price;
        public TextView address;
        public Button detail;
        public View tmap;

        public OriginalViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            price = (TextView) v.findViewById(R.id.price);
            address = (TextView) v.findViewById(R.id.address);
            detail = (Button) v.findViewById(R.id.detail);
            tmap = (ImageView) v.findViewById(R.id.tmap);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customers, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            RouteD routeD = mRouteDs.get(position);
            view.name.setText(routeD.name);
//            view.price.setText(routeD.price);
            view.address.setText(routeD.address);


//            view.checkBox.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(mOnCheckboxClickListener != null){
//                        mOnCheckboxClickListener.onItemClick(position);
//                    }
//                }
//            });
//
//            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (mOnItemClickListener != null) {
//                        mOnItemClickListener.onItemClick(view, items.get(position), position);
//                    }
//                }
//            });

        }
    }

    @Override
    public int getItemCount() {
        return mRouteDs.size();
    }

    public interface OnCheckboxClickListener {
        void onItemClick(int pos);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, RouteD obj, int pos);
    }

}