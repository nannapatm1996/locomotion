package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.asus.robotframework.API.MotionControl;
import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.VisionConfig;
import com.asus.robotframework.API.results.DetectFaceResult;
import com.asus.robotframework.API.results.Location;
import com.asus.robotframework.API.results.RoomInfo;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainActivity extends RobotActivity {

    public MainActivity(RobotCallback robotCallback, RobotCallback.Listen robotListenCallback) {
        super(robotCallback, robotListenCallback);
    }

    // request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    // robotAPI flags
    private static boolean isRobotApiInitialed = false;

    // 1st roomInfo string
    private static String sRoom1;
    private static String sRoom2;
    private static String sRoom3;
    private static int current_map;

    // buttons
    private Button mBtnRoom1;
    private Button mBtnRoom2;
    private Button mBtnRoom3;
    private Button mBtnGoTo;
    private Button mBtnPermission;

    // textViews
    private TextView mTvRoom1;
    private TextView mTvRoom2;
    private TextView mTvRoom3;
    private TextView mTvPermissionState;

    private static Intent i;

    private static Context context;

    private static String facedetect_result = "no face detect";
    private static String thismap = "map1";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        i = new Intent(this, Submap0_activity.class);
        context = getApplicationContext();


        mTvPermissionState = (TextView) findViewById(R.id.TvPermission);
        mTvRoom1 = (TextView) findViewById(R.id.TvRoom1);
        mTvRoom2 = (TextView) findViewById(R.id.TvRoom2);
        mTvRoom3 = (TextView) findViewById(R.id.TvRoom3);


        mBtnPermission = (Button) findViewById(R.id.BtnPermission);
        mBtnPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });

        mBtnRoom1 = (Button) findViewById(R.id.BtnRoom1);
        mBtnRoom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<RoomInfo> arrayListRooms = robotAPI.contacts.room.getAllRoomInfo();
                    sRoom1 = arrayListRooms.get(0).keyword;

                    Log.d("ZenboGoToLocation", "arrayListRooms = " + arrayListRooms);
                    Log.d("ZenboGoToLocation", "arrayListRooms(0) = " + sRoom1);

                    mTvRoom1.setText(sRoom1);
                    mBtnGoTo.setEnabled(true);
                }
                catch (Exception e){
                    Log.d("ZenboGoToLocation", "get room info result exception = "+ e);
                }
            }
        });

        mBtnRoom2 = (Button) findViewById(R.id.BtnRoom2);
        mBtnRoom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<RoomInfo> arrayListRooms = robotAPI.contacts.room.getAllRoomInfo();
                sRoom2 = arrayListRooms.get(1).keyword;
                mTvRoom2.setText(sRoom2);
            }
        });

        mBtnRoom3 = (Button) findViewById(R.id.BtnRoom3);
        mBtnRoom3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<RoomInfo> arrayListRooms = robotAPI.contacts.room.getAllRoomInfo();
                sRoom3 = arrayListRooms.get(2).keyword;
                mTvRoom3.setText(sRoom3);
            }
        });

        mBtnGoTo = (Button) findViewById(R.id.btnGoTo);
        mBtnGoTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sRoom1.equals("")&& !sRoom2.equals("") && !sRoom3.equals("")){
                    if(isRobotApiInitialed){

                        //robotAPI.slam.getLocation();

                        robotAPI.robot.speak("Start!");
                        robotAPI.motion.goTo(sRoom1);


                        //Start positioning to other room

                        //by 2.5 mins the robot should reach its destination
                        new CountDownTimer(150000,1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {


                            }

                            @Override
                            public void onFinish() {

                                Intent i = new Intent(context, motioncontrol.class);
                                i.putExtra("thismap",thismap);
                                i.putExtra("sRoom2",sRoom2);
                                i.putExtra("sRoom3",sRoom3);
                                startActivity(i);

                            }
                        }.start();


                        // at that room robot should rotate and detect face in 2 mins
                       /* new CountDownTimer(270000,30000) {

                            @Override
                            public void onTick(long millisUntilFinished) {


                            }

                            @Override
                            public void onFinish() {


                                stopDetectFace();
                                robotAPI.robot.speak("Detect Face Stop");

                                Toast toast = Toast.makeText(context, facedetect_result, Toast.LENGTH_SHORT);
                                toast.show();

                                if (facedetect_result.equals("no face detect")){
                                    robotAPI.robot.speak("no face found");
                                }
                                else{
                                    robotAPI.robot.speak("face found");
                                }
                                /*robotAPI.robot.speak("Complete 1");
                                Intent i = new Intent(MainActivity.this,Submap0_activity.class);
                                i.putExtra("sRoom2",sRoom2);
                                i.putExtra("sRoom3",sRoom3);
                                startActivity(i);*/
                            //}
                        //}.start();

                      /*robotAPI.robot.speak("I am going to "+ sRoom1);
                      CurrentLocation = sRoom2;
                      robotAPI.motion.goTo(sRoom1);*/
                    }
                }
            }
        });

        //TODO: create delay





    }

    @Override
    protected void onResume() {
        super.onResume();

        // check permission READ_CONTACTS is granted or not
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted by user yet
            Log.d("ZenboGoToLocation", "READ_CONTACTS permission is not granted by user yet");
            mTvPermissionState.setText(getString(R.string.permission_not_granted));
            mBtnPermission.setEnabled(true);
            mBtnRoom1.setEnabled(false);
        }
        else{
            // permission is granted by user
            Log.d("ZenboGoToLocation", "READ_CONTACTS permission is granted");
            mTvPermissionState.setText(getString(R.string.permission_granted));
            mBtnPermission.setEnabled(false);
            mBtnRoom1.setEnabled(true);
        }

        // initial params
        mTvRoom1.setText(getString(R.string.first_room_info));
        mTvRoom2.setText(getString(R.string.first_room_info));
        mTvRoom3.setText(getString(R.string.first_room_info));
        mBtnGoTo.setEnabled(false);
        sRoom1="";
        sRoom2="";
        sRoom3="";

    }



    public static RobotCallback robotCallback = new RobotCallback() {
        @Override
        public void initComplete() {
            super.initComplete();

            Log.d("ZenboGoToLocation", "initComplete()");
            isRobotApiInitialed = true;
        }

        @Override
        public void onResult(int cmd, int serial, RobotErrorCode err_code, Bundle result) {
            super.onResult(cmd, serial, err_code, result);


        }

        @Override
        public void onDetectFaceResult(List<DetectFaceResult> resultList) {
            super.onDetectFaceResult(resultList);

            /*Log.d("RobotDevSample", "onDetectFaceResult: " + resultList.get(0));

            //use toast to show detected faces
            facedetect_result = "Face Detected";
            String toast_result = "Detect Face";
            Toast toast = Toast.makeText(context, toast_result, Toast.LENGTH_SHORT);
            toast.show();*/
        }

        @Override
        public void onStateChange(int cmd, int serial, RobotErrorCode err_code, RobotCmdState state) {
            super.onStateChange(cmd, serial, err_code, state);


        }
    };


    public static RobotCallback.Listen robotListenCallback = new RobotCallback.Listen() {
        @Override
        public void onFinishRegister() {

        }

        @Override
        public void onVoiceDetect(JSONObject jsonObject) {

        }

        @Override
        public void onSpeakComplete(String s, String s1) {

        }

        @Override
        public void onEventUserUtterance(JSONObject jsonObject) {

        }

        @Override
        public void onResult(JSONObject jsonObject) {

        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };


    public MainActivity() {
        super(robotCallback, robotListenCallback);
    }


    private void requestPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                this.checkSelfPermission(Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED) {
            // Android version is lesser than 6.0 or the permission is already granted.
            Log.d("ZenboGoToLocation", "permission is already granted");
            return;
        }

        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            //showMessageOKCancel("You need to allow access to Contacts",
            //        new DialogInterface.OnClickListener() {
            //            @Override
            //            public void onClick(DialogInterface dialog, int which) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
            //            }
            //        });
        }
    }

    /*
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    */

    private static void startDetectFace() {
        // start detect face
        VisionConfig.FaceDetectConfig config = new VisionConfig.FaceDetectConfig();
        config.enableDebugPreview = true;  // set to true if you need preview screen
        config.intervalInMS = 2000;
        config.enableDetectHead = true;
        robotAPI.vision.requestDetectFace(config);
    }

    private void stopDetectFace() {
        // stop detect face
        robotAPI.vision.cancelDetectFace();
    }

}

