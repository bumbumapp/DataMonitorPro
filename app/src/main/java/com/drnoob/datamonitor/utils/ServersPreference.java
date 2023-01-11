package com.drnoob.datamonitor.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ServersPreference {
   private SharedPreferences sharedPreferences;
   public ServersPreference(Context context){
       sharedPreferences=context.getSharedPreferences("name",Context.MODE_PRIVATE);
   }

   public void setInt(Integer index){
       SharedPreferences.Editor editor=sharedPreferences.edit();
       editor.putInt("server_index",index);
       editor.apply();
       }
   public int getIntValue(){
       return sharedPreferences.getInt("server_index",0);
   }
    public void setBoolen(Boolean isBest){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("serverBest",isBest);
        editor.apply();
    }
    public Boolean getBestServer(){
        return sharedPreferences.getBoolean("serverBest",true);
    }

}
