package com.university.univation;

import android.content.Context;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;
import android.widget.*;

import java.util.*;

import android.view.View.*;
import android.app.AlertDialog;
import android.content.*;
import android.view.inputmethod.*;
import android.os.*;
import java.net.*;
import android.app.*;
import android.net.*;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.*;
import java.text.*;

/**
 * 
 */
public class buildingAdapter extends RecyclerView.Adapter<buildingAdapter.ViewHolder> {

   public Context context;
	ProgressDialog progress;
    List<building> building1;
	private int lastPosition = -1;

	BuildingListener listener;

    public buildingAdapter(List<building> building1, Context context,BuildingListener listener){

        super();
        this.building1 = building1;
        this.context = context;
        this.listener=listener;
    }

	@Override
	public int getItemViewType(int position)
	{

		building building =  building1.get(position);

		return 5;



		// TODO: Implement this method
		//return super.getItemViewType(position);
	}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.building_item, parent, false);
		ViewHolder viewHolder = new ViewHolder(v);
		return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {

        final building building=  building1.get(position);

		Viewholder.name.setText(building.getName());
		Viewholder.desc.setText(building.getDesc());

        Viewholder.locate.setImageDrawable(VectorDrawableCompat.create(context.getResources(),R.drawable.ic_place_black_24dp,context.getTheme()));

        Picasso.with(context)
                .load(building.getImage())
                .placeholder(R.mipmap.ic_launcher)
                .into(Viewholder.image);

        Viewholder.locate.setOnClickListener(v->listener.locateClickListener(building.getLocation()));

    }

    @Override
    public int getItemCount() {

        return building1.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

		TextView name;
		TextView desc;
		ImageView image,locate;

        public ViewHolder(View itemView) {

            super(itemView);

			name=itemView.findViewById(R.id.name);
			desc= itemView.findViewById(R.id.desc);
			image= itemView.findViewById(R.id.image);
			locate=itemView.findViewById(R.id.place);

        }
    }

    public interface BuildingListener{
        void locateClickListener(LatLng loc);
    }
	
}
