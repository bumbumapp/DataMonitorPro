package com.drnoob.datamonitor;

import android.os.CountDownTimer;

import com.drnoob.datamonitor.core.Values;


public  class  Timers {

    public static CountDownTimer timer(){
        return new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                 Values.TIMER_FINISHED=true;
            }
        };
    }

}