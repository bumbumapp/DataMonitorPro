/*
 * Copyright (C) 2021 Dr.NooB
 *
 * This file is a part of Data Monitor <https://github.com/itsdrnoob/DataMonitor>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.drnoob.datamonitor.ui.fragments;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.drnoob.datamonitor.R;
import com.drnoob.datamonitor.Speed.DevilDownloadTest;
import com.drnoob.datamonitor.Speed.GetSpeedTestHostsHandler;
import com.drnoob.datamonitor.Speed.HttpUploadTest;
import com.drnoob.datamonitor.Speed.PingTest;
import com.drnoob.datamonitor.ui.activities.MainActivity;
import com.drnoob.datamonitor.ui.activities.ServersActivity;
import com.drnoob.datamonitor.utils.ServersPreference;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


@Keep
public class NetworkDiagnosticsFragment extends Fragment {
    private static final String TAG = NetworkDiagnosticsFragment.class.getSimpleName();
    static float position = 0;
    static float lastPosition = 0;
    public static TextView currentConnectionType;
    private LottieAnimationView rippleView;
    private Activity activity=new Activity();
    private ConstraintLayout diagnosticsView;
    private ProgressBar diagnosticsRunning;
    private RelativeLayout relativeLayout;
    GetSpeedTestHostsHandler getSpeedTestHostsHandler = null;
    HashSet<String> tempBlackList;
    private TextView hostname, locationname, type_of_network;
    private TextView fping, fdownload, fupload, mhead, mdatametartype;
    private TextView pingTextView, downloadTextView, uploadTextView;
    private TextView startButton,ratingme;
    private ImageView imageView;
    private Bitmap bitmap;
    private DecimalFormat dec;
    private DecimalFormat decmbs;
    ServersPreference serversPreference;
    LinearLayout diagnostic;
    private ImageView barImageView;
    HashMap<Integer, List<String>> mapValue ;
    int findServerIndex = 0;

    Handler handlerd=new Handler();


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();
    }
    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_network_diagnostics, container, false);
    }

    @SuppressLint("ShowToast")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        serversPreference=new ServersPreference(requireContext());
        startButton = view.findViewById(R.id.startButton);
        barImageView = (ImageView) view.findViewById(R.id.barImageView);
        diagnostic=view.findViewById(R.id.diagnostics);
        /**speed view image*/
        imageView = view.findViewById(R.id.imageView);
        /**speed view image*/
        rippleView = view.findViewById(R.id.ripple_view);
//        diagnosticsInfo = view.findViewById(R.id.info);
//        currentTest = view.findViewById(R.id.current_test);
        mdatametartype = view.findViewById(R.id.datametartype);
        relativeLayout=view.findViewById(R.id.relativelayout);
        diagnosticsView = view.findViewById(R.id.test_view);
        diagnosticsRunning = view.findViewById(R.id.diagnostics_running);

        dec = new DecimalFormat("#.##");
        decmbs = new DecimalFormat("#.###");

        tempBlackList = new HashSet<>();
        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();


        /**host , location , ping , textview*/
        hostname = view.findViewById(R.id.host);
        locationname = view.findViewById(R.id.location);
        pingTextView = view.findViewById(R.id.pingTextView);
        /**host , location , ping , datametar type textview*/

        /**network type view , main head, datametar type   textview*/
        type_of_network = view.findViewById(R.id.type);
        mhead = view.findViewById(R.id.heder);
        /**network type view , main head ,datametar type   textview*/

        /**upload , download textView/**/
        downloadTextView =view.findViewById(R.id.downloadTextView);
        uploadTextView = view.findViewById(R.id.uploadTextView);
        /**upload , download /**/


        /**set custom font*/

        /**set custom font id's*/
//        fhost = findViewById(R.id.fhost); /**Host name textview*/
        fping = view.findViewById(R.id.fping); /**ping name textview*/
//        flocation = findViewById(R.id.flocation); /**location name textview*/
        fdownload = view.findViewById(R.id.textView2); /**download name textview*/
        fupload = view.findViewById(R.id.textView3); /**upload name textview*/
        /**set custom font id's*/





        /**get network type*/
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                type_of_network.setText("WIFI");
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                type_of_network.setText("MOBILE");
            } else {
                type_of_network.setText("OFF");
            }
        }
        /**get network type*/






        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startButton.setEnabled(false);
                diagnosticsRunning.setVisibility(View.VISIBLE);
                startButton.setText(R.string.connecting);
                rippleView.setVisibility(View.INVISIBLE);
                diagnostic.setVisibility(View.VISIBLE);
//                if (!serversPreference.getBestServer()){
//
//                            mapValue = getSpeedTestHostsHandler.getMapValue();
//                            if (mapValue!=null){
//                                if(mapValue.size()>=serversPreference.getIntValue()){
//                                    handlerd.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            hostname.setText(mapValue.get(serversPreference.getIntValue()).get(5));
//                                            locationname.setText(mapValue.get(serversPreference.getIntValue()).get(3));
//                                            diagnosticsView.setVisibility(View.GONE);
//                                            relativeLayout.setVisibility(View.VISIBLE);
//                                            mainrun();
//                                        }
//                                    },3000);
//
//
//                                }
//
//                            }
//
//
//
//                        }



               if (activeNetwork!=null){
                   HashMap<Integer, String> mapKey = getSpeedTestHostsHandler.getMapKey();
                   mapValue=getSpeedTestHostsHandler.getMapValue();
                   double selfLat = getSpeedTestHostsHandler.getSelfLat();
                   double selfLon = getSpeedTestHostsHandler.getSelfLon();
                   double tmp = 19349458;
                   double dist = 0.0;

                   for (int index : mapKey.keySet()) {


                       Location source = new Location("Source");
                       source.setLatitude(selfLat);
                       source.setLongitude(selfLon);

                       List<String> ls = mapValue.get(index);
                       Location dest = new Location("Dest");
                       dest.setLatitude(Double.parseDouble(ls.get(0)));
                       dest.setLongitude(Double.parseDouble(ls.get(1)));

                       double distance = source.distanceTo(dest);
                       if (tmp > distance) {
                           tmp = distance;
                           dist = distance;
                           findServerIndex = index;
                       }
                   }

                   handlerd.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           diagnosticsView.setVisibility(View.GONE);
                           relativeLayout.setVisibility(View.VISIBLE);
                           mdatametartype.setVisibility(View.VISIBLE);
                           hostname.setText(mapValue.get(findServerIndex).get(5));
                           locationname.setText(mapValue.get(findServerIndex).get(3));
                           mainrun();
                       }
                   },3000);
               }
               }







        });



    }
    public void mainrun(){
        startButton.setEnabled(false);

        if (getSpeedTestHostsHandler == null) {
            getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
            getSpeedTestHostsHandler.start();
        }

        new Thread(new Runnable() {
            RotateAnimation rotate;


            @Override
            public void run() {
                if (activity != null) {


                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mhead.setText("Selecting best server");
                        }
                    });

                    //Get egcodes.speedtest hosts
                    int timeCount = 600; //1min
                    while (!getSpeedTestHostsHandler.isFinished()) {
                        timeCount--;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                        if (timeCount <= 0) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "No Connection...", Toast.LENGTH_LONG).show();
                                    startButton.setEnabled(true);
                                    startButton.setText("Restart Test");
                                    try {
                                    } catch (Exception O) {
                                    }
                                }
                            });
                            getSpeedTestHostsHandler = null;
                            return;
                        }
                    }

                    //Find closest server
                    HashMap<Integer, String> mapKey = getSpeedTestHostsHandler.getMapKey();
                    Log.d("TAG", "Errsst" + tempBlackList.size());
                    double dist = 0.0;
                    String testAddr = mapKey.get(0).replace("http://", "https://");
                    final List<String> info = mapValue.get(serversPreference.getIntValue());

                    final double distance = dist;

                    if (info == null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mhead.setText("There was a problem");
                            }
                        });
                        return;
                    }


                    //Reset value, graphics
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pingTextView.setText("0 ms");
                            downloadTextView.setText("0 Mpbs");
                            uploadTextView.setText("0 Mpbs");

                        }
                    });
                    final List<Double> pingRateList = new ArrayList<>();
                    final List<Double> downloadRateList = new ArrayList<>();
                    final List<Double> uploadRateList = new ArrayList<>();
                    Boolean pingTestStarted = false;
                    Boolean pingTestFinished = false;
                    Boolean downloadTestStarted = false;
                    Boolean downloadTestFinished = false;
                    Boolean uploadTestStarted = false;
                    Boolean uploadTestFinished = false;

                    //Init Test
                    final PingTest pingTest = new PingTest(info.get(6).replace(":8080", ""), 3);
                    final DevilDownloadTest downloadTest = new DevilDownloadTest(testAddr.replace(testAddr.split("/")[testAddr.split("/").length - 1], ""));
                    final HttpUploadTest uploadTest = new HttpUploadTest(testAddr);


                    //Tests
                    while (true) {
                        if (!pingTestStarted) {
                            pingTest.start();
                            pingTestStarted = true;
                        }
                        if (pingTestFinished && !downloadTestStarted) {
                            downloadTest.start();
                            downloadTestStarted = true;
                        }
                        if (downloadTestFinished && !uploadTestStarted) {
                            uploadTest.start();
                            uploadTestStarted = true;
                        }


                        //Ping Test
                        if (pingTestFinished) {
                            //Failure
                            if (pingTest.getAvgRtt() == 0) {
                                System.out.println("Ping error...");
                            } else {
                                //Success
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pingTextView.setText(dec.format(pingTest.getAvgRtt()) + " ms");
                                        try {
                                            mhead.setText(getResources().getString(R.string.testing_latency));
                                        } catch (Exception O) {
                                        }
                                    }
                                });
                            }
                        } else {
                            pingRateList.add(pingTest.getInstantRtt());

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pingTextView.setText(dec.format(pingTest.getInstantRtt()) + " ms");
                                    try {
                                        mhead.setText(getResources().getString(R.string.testing_latency));
                                    } catch (Exception O) {
                                    }
                                }
                            });

                            //Update chart
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Creating an  XYSeries for Income
                                    XYSeries pingSeries = new XYSeries("");
                                    pingSeries.setTitle("");

                                    int count = 0;
                                    List<Double> tmpLs = new ArrayList<>(pingRateList);
                                    for (Double val : tmpLs) {
                                        pingSeries.add(count++, val);
                                    }

                                    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                    dataset.addSeries(pingSeries);


                                }
                            });
                        }


                        //Download Test
                        if (pingTestFinished) {
                            if (downloadTestFinished) {
                                //Failure
                                if (downloadTest.getFinalDownloadRate() == 0) {
                                    System.out.println("Download error...");
                                } else {
                                    //Success
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            downloadTextView.setText(decmbs.format(downloadTest.getFinalDownloadRate()) + " Mbps");


                                            try {
                                                mhead.setText(getResources().getString(R.string.testing_download));
                                            } catch (Exception O) {
                                            }
                                        }
                                    });
                                }
                            } else {
                                //Calc position
                                double downloadRate = downloadTest.getInstantDownloadRate();
                                downloadRateList.add(downloadRate);

                                position = (float) getPositionByRate(downloadRate);


                                activity.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        rotate = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                        rotate.setInterpolator(new LinearInterpolator());
                                        rotate.setDuration(100);
                                        barImageView.startAnimation(rotate);
                                        imageView.setImageResource(getMeterDrawableId(downloadTest.getInstantDownloadRate()));

                                        downloadTextView.setText(decmbs.format(downloadTest.getInstantDownloadRate()) + " Mbps");

                                        try {
                                            mhead.setText(getResources().getString(R.string.testing_download));
                                        } catch (Exception O) {
                                        }

                                    }

                                });
                                lastPosition = position;

                                /**Update chart*/
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Creating an  XYSeries for Income
                                        XYSeries downloadSeries = new XYSeries("");
                                        downloadSeries.setTitle("");
                                        List<Double> tmpLs = new ArrayList<>(downloadRateList);
                                        int count = 0;
                                        for (Double val : tmpLs) {
                                            downloadSeries.add(count++, val);
                                        }
                                        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                        dataset.addSeries(downloadSeries);
                                    }
                                });

                            }
                        }


                        //Upload Test
                        if (downloadTestFinished) {
                            if (uploadTestFinished) {
                                //Failure
                                if (uploadTest.getFinalUploadRate() == 0) {
                                    System.out.println("Upload error...");
                                } else {
                                    //Success
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            uploadTextView.setText(decmbs.format(uploadTest.getFinalUploadRate()) + "Mbps");

                                            try {
                                                mhead.setText(getResources().getString(R.string.testing_upload));
                                            } catch (Exception O) {
                                            }
                                        }
                                    });
                                }
                            } else {
                                //Calc position
                                double uploadRate = uploadTest.getInstantUploadRate();
                                uploadRateList.add(uploadRate);

                                position = (float) getPositionByRate(uploadRate);


                                activity.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        rotate = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                        rotate.setInterpolator(new LinearInterpolator());
                                        rotate.setDuration(100);
                                        barImageView.startAnimation(rotate);
                                        imageView.setImageResource(getMeterDrawableId(uploadTest.getInstantUploadRate()));
                                        uploadTextView.setText(decmbs.format(uploadTest.getInstantUploadRate()) + " Mbps");


                                        try {
                                            mhead.setText(getResources().getString(R.string.testing_upload));
                                        } catch (Exception O) {
                                        }

                                    }

                                });
                                lastPosition = position;

                                //Update chart
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Creating an  XYSeries for Income
                                        XYSeries uploadSeries = new XYSeries("");
                                        uploadSeries.setTitle("");

                                        int count = 0;
                                        List<Double> tmpLs = new ArrayList<>(uploadRateList);
                                        for (Double val : tmpLs) {
                                            if (count == 0) {
                                                val = 0.0;
                                            }
                                            uploadSeries.add(count++, val);
                                        }

                                        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                        dataset.addSeries(uploadSeries);
                                    }
                                });

                            }
                        }

                        //Test bitti
                        if (pingTestFinished && downloadTestFinished && uploadTest.isFinished()) {
                            break;
                        }

                        if (pingTest.isFinished()) {
                            pingTestFinished = true;
                        }
                        if (downloadTest.isFinished()) {
                            downloadTestFinished = true;
                        }
                        if (uploadTest.isFinished()) {
                            uploadTestFinished = true;
                        }

                        if (pingTestStarted && !pingTestFinished) {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                            }
                        } else {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                            }
                        }
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startButton.setEnabled(true);
                            diagnosticsView.setVisibility(View.VISIBLE);
                            relativeLayout.setVisibility(View.GONE);
                            mdatametartype.setVisibility(View.INVISIBLE);
                            startButton.setText("Restart Test");
                            mhead.setText("");
                            diagnosticsRunning.setVisibility(View.INVISIBLE);
                            rippleView.setVisibility(View.VISIBLE);
//                            startButton.setBackground(getResources().getDrawable(R.drawable.button_run_diagnostics_background, null));

                        }
                    });


                }
            }
        }).start();
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private int getMeterDrawableId(double speed) {
        if (speed >= 0 && speed < 5) {
            return R.drawable.ic_meter_0;
        }
        else if (speed >= 5 && speed < 10) {
            return R.drawable.ic_meter_5;
        }
        else if (speed >= 10 && speed < 15) {
            return R.drawable.ic_meter_10;
        }
        else if (speed >= 15 && speed < 20) {
            return R.drawable.ic_meter_15;
        }
        else if (speed >= 20 && speed < 25) {
            return R.drawable.ic_meter_20;
        }
        else if (speed >= 25 && speed < 30) {
            return R.drawable.ic_meter_25;
        }
        else if (speed >= 30 && speed < 50) {
            return R.drawable.ic_meter_30;
        }
        else if (speed >= 50 && speed < 75) {
            return R.drawable.ic_meter_50;
        }
        else if (speed >= 75 && speed < 100) {
            return R.drawable.ic_meter_75;
        }
        else if (speed > 100) {
            return R.drawable.ic_meter_100;
        }
        return R.drawable.ic_meter;
    }
    public double getPositionByRate(double rate) {
        if (rate >= 0 && rate <= 30) {
            return rate * 6;
        }
        else if (rate >= 30 && rate <= 50) {
            return (float) ((rate - 30) * 1.5) + 180;
        }
        else if (rate > 50 && rate <= 100) {
            return (float) ((rate - 50) * 1.2) + 210;
        }
        else if (rate > 100) {
            return 270;
        }
        return 0;
    }

}