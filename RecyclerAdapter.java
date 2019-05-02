package com.minghaoqin.q.eaoow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.BeanHolder> {

    private List<PriorityTable> pt;
    private Context context;
    private LayoutInflater layoutInflater;

    public RecyclerAdapter(List<PriorityTable> pt, Context context) {
        layoutInflater = LayoutInflater.from(context);
        this.pt = pt;
        this.context = context;
    }


    @Override
    public BeanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.rest_list_item, parent, false);
        return new BeanHolder(view);
    }

    @Override
    public void onBindViewHolder(BeanHolder holder, int position) {
        Log.e("bind", "onBindViewHolder: " + pt.get(position));
        holder.textViewRestName.setText(String.valueOf(pt.get(position).getRestName()));
        holder.textViewPriority.setText("Restaurant Score: " + String.valueOf(pt.get(position).getPriority()));
    }

    @Override
    public int getItemCount() {
        return pt.size();
    }

    public class BeanHolder extends RecyclerView.ViewHolder {

        TextView textViewRestName;
        TextView textViewPriority;


        public BeanHolder(View itemView) {
            super(itemView);
            textViewPriority = itemView.findViewById(R.id.priority);
            textViewRestName = itemView.findViewById(R.id.restname);
        }
    }
}