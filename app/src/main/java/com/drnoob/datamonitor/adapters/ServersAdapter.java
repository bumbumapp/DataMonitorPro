package com.drnoob.datamonitor.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.drnoob.datamonitor.OnClickLisnter;
import com.drnoob.datamonitor.R;
import com.drnoob.datamonitor.ui.activities.ServersActivity;
import com.drnoob.datamonitor.ui.fragments.NetworkDiagnosticsFragment;
import com.drnoob.datamonitor.utils.ServersPreference;

import java.util.HashMap;
import java.util.List;

public class ServersAdapter extends RecyclerView.Adapter<ServersAdapter.ServerViewHolder> {

    Context context;
    HashMap<Integer, List<String>> mapValue;
    ServersPreference serversPreference;
    OnClickLisnter onClicklisnter;
    public ServersAdapter(Context context, OnClickLisnter onClicklisnter,HashMap<Integer,List<String>> mapValue){
        this.context=context;
        this.mapValue=mapValue;
        this.onClicklisnter=onClicklisnter;
    }

    @NonNull
    @Override
    public ServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.servers,parent,false);
        return new ServerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServerViewHolder holder, int position) {
        holder.server_location.setText(mapValue.get(position).get(2));
        holder.server_name.setText(mapValue.get(position).get(5));


    }

    @Override
    public int getItemCount() {
        return mapValue.size();
    }




    public class ServerViewHolder extends RecyclerView.ViewHolder  {
        TextView server_name,server_location;
        public ServerViewHolder(@NonNull View itemView) {
            super(itemView);
            server_name=itemView.findViewById(R.id.servername);
            server_location=itemView.findViewById(R.id.serve_location);
            serversPreference=new ServersPreference(context);
           itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    serversPreference.setInt(getAdapterPosition());
                    serversPreference.setBoolen(false);
                    onClicklisnter.OnClick();

                }
            });
        }


    }
}
