package com.drnoob.datamonitor.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.drnoob.datamonitor.OnClickLisnter;
import com.drnoob.datamonitor.R;
import com.drnoob.datamonitor.Speed.GetSpeedTestHostsHandler;
import com.drnoob.datamonitor.adapters.ServersAdapter;
import com.drnoob.datamonitor.utils.ServersPreference;

import java.util.HashMap;
import java.util.List;

public class ServersActivity extends AppCompatActivity {
    GetSpeedTestHostsHandler getSpeedTestHostsHandler = null;
    RecyclerView serversRecycler;
    ServersAdapter adapter;
    HashMap<Integer, List<String>> mapValue;
    Button closeSrver;
    Handler handler=new Handler();
    ProgressBar progressBar;
    ImageView back;
    TextView internet;
    ServersPreference serversPreference;
    OnClickLisnter onClicklisnter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);
        serversRecycler=findViewById(R.id.recyclerView);
        getSpeedTestHostsHandler=new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();
        back=findViewById(R.id.back);
        closeSrver=findViewById(R.id.closest_server);
        progressBar=findViewById(R.id.progress_bar);
        internet=findViewById(R.id.internet);
        serversPreference=new ServersPreference(this);
        onClicklisnter=new OnClickLisnter() {
            @Override
            public void OnClick() {
                startActivity(new Intent(getBaseContext(),MainActivity.class));
            }
        };
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                progressBar.setVisibility(View.GONE);
                if (activeNetwork!=null){
                    internet.setVisibility(View.GONE);
                    mapValue= getSpeedTestHostsHandler.getMapValue();
                    Log.d("TAG","Errss"+mapValue.size());
                    adapter= new ServersAdapter(ServersActivity.this,onClicklisnter,mapValue);
                    serversRecycler.setAdapter(adapter);
                    serversRecycler.setLayoutManager(new LinearLayoutManager(ServersActivity.this));
                }
                else{
                    internet.setVisibility(View.VISIBLE);
                }
            }
        },1500);



      closeSrver.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              serversPreference.setBoolen(true);
              Intent intent=new Intent(ServersActivity.this,MainActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(intent);
          }
      });




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();
    }


}