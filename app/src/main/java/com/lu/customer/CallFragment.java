package com.lu.customer;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.lu.customer.CommonTwo.chatWebSocketClient;


public class CallFragment extends Fragment {
    private Activity activity;
    private View view;
    private MapView mapView;
    private GoogleMap map;
    private LatLng latLng;
    private TextView tvName,tvPhone,tvTime,tvScore;
    private Button btCancel;
    private Driver driver = null;
    private Driver driver2 = null;
    private Order order = null;
    private int customer_id = 1;
    private static final String TAG = "TAG_CallFragment";
    private LocalBroadcastManager broadcastManager;
    private Handler handler = new Handler();
    private Runnable runnable;
    private static final int REQ_STAR = 2;
    private int driver_id;
    private int order_id = 0;
    private String order_start,order_end;
    private double startLatitude,startLongitude,endLatitude,endLongitude;
    private CircleImageView ivDriver;
    private double driverLatitude,driverLongitude;
    private int status = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        broadcastManager = LocalBroadcastManager.getInstance(activity);
        CommonTwo.connectServer(activity, CommonTwo.loadUserName(activity));
        runnable = new Runnable(){
            public void run(){
                map.clear();
                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "DriverServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "getLocation");
                    jsonObject.addProperty("driver_id", driver_id);
                    try {
                        String jsonIn = new CommonTask(url, jsonObject.toString()).execute().get();
                        driver2 = new Gson().fromJson(jsonIn, Driver.class);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }

                if (driver2 != null){
                    driverLatitude = driver2.getDriver_latitude();
                    driverLongitude = driver2.getDriver_longitude();
                    latLng = new LatLng(driverLatitude,driverLongitude);
                }
                addMarker(latLng);
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                startLatitude =  pref.getFloat("startLatitude",0);
                startLongitude =  pref.getFloat("startLongitude",0);
                endLatitude =  pref.getFloat("endLatitude",0);
                endLongitude =  pref.getFloat("endLongitude",0);
                if(status == 0){
                    float[] results = new float[1];
                    Location.distanceBetween(driverLatitude,
                        driverLongitude, startLatitude,
                        startLongitude, results);
                    float d = results[0];
                    Log.e(TAG, String.valueOf(d));
                    int distance = (int) d*2/1000+1;
                    Log.e(TAG, String.valueOf(distance));
                    int t = distance*60/40;
                    Log.e(TAG, String.valueOf(t));
                    String time = "預估駕駛到達時間："+t+"分鐘";
                    tvTime.setText(time);
                }
                else if(status == 2){
                    float[] results = new float[1];
                    Location.distanceBetween(driverLatitude,
                            driverLongitude, endLatitude,
                            endLongitude, results);
                    float d = results[0];
                    int distance = (int) d*2/1000+1;
                    int t = distance*60/40;
                    String time = "預估到達目的地時間："+t+"分鐘";
                    tvTime.setText(time);
                }
                //CommonTwo.showToast(activity,"runnable start");
                handler.postDelayed(this,1000); //1秒 單位：ms
                //postDelayed(this,6000)方法安排一個Runnable物件到主執行緒佇列中
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity.setTitle(R.string.textCall);
        return inflater.inflate(R.layout.fragment_call, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler.postDelayed(runnable,1000);
        registerChatReceiver();
        mapView = view.findViewById(R.id.mapView2);
        // 在Fragment生命週期方法內呼叫對應的MapView方法
        mapView.onCreate(savedInstanceState);
        mapView.onStart();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                showMyLocation();
            }
        });
        tvName = view.findViewById(R.id.tvNameinfo);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvTime = view.findViewById(R.id.tvTime);
        tvScore = view.findViewById(R.id.tvScoreinfo);
        btCancel = view.findViewById(R.id.btCancel);
        ivDriver = view.findViewById(R.id.ivDriver);
        final NavController navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("driver") == null) {
            Common.showToast(activity, R.string.textNoDriverFound);
            navController.popBackStack();
            return;
        }
        Driver driver1 = (Driver) bundle.getSerializable("driver");
        driver_id = driver1.getDriver_id();
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "DriverServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getInformation");
            jsonObject.addProperty("driver_id", driver_id);
            try {
                String jsonIn = new CommonTask(url, jsonObject.toString()).execute().get();
                driver = new Gson().fromJson(jsonIn, Driver.class);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        showPhoto();
        if (driver != null){
            String name = "駕駛姓名："+driver.getDriver_name();
            String phone = "駕駛電話："+driver.getDriver_phone();
            tvName.setText(name);
            tvPhone.setText(phone);
        }

        if (Common.networkConnected(activity)) {
            String url = Common.URL + "OrderServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getDriver_score");
            jsonObject.addProperty("driver_id", driver_id);
            try {
                String jsonIn = new CommonTask(url, jsonObject.toString()).execute().get();
                order = new Gson().fromJson(jsonIn, Order.class);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        Double s = 5.0;
        if(order != null){
            s = order.getDriver_score();
        }
        DecimalFormat mDecimalFormat = new DecimalFormat("#.#");
        String sc = mDecimalFormat.format(s);
        String score = "駕駛評價："+sc+"/5";
        tvScore.setText(score);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity)
                        /* 設定標題 */
                        //.setTitle(R.string.textTitle)
                        /* 設定圖示 */
                        //.setIcon(R.drawable.alert)
                        /* 設定訊息文字 */
                        .setMessage(R.string.textCancelConfirm)
                        /* 設定positive與negative按鈕上面的文字與點擊事件監聽器 */
                        .setPositiveButton(R.string.textYes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /* 結束此Activity頁面 */
                                String sender = CommonTwo.loadUserName(activity);
                                String friend = CommonTwo.loadDriverName(activity);
                                String message = "cancel";
                                ChatMessage chatMessage = new ChatMessage("chat", sender, friend, message);
                                String chatMessageJson = new Gson().toJson(chatMessage);
                                chatWebSocketClient.send(chatMessageJson);
                                Log.d(TAG, "output: " + chatMessageJson);
                                handler.removeCallbacks(runnable);
                                activity.onBackPressed();
                            }
                        })
                        .setNegativeButton(R.string.textNo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /* 關閉對話視窗 */
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        showMyLocation();
    }

    private void showMyLocation() {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
    }

    /**
     * 註冊廣播接收器攔截聊天資訊
     * 因為是在Fragment註冊，所以Fragment頁面未開時不會攔截廣播
     */
    private void registerChatReceiver() {
        IntentFilter chatFilter = new IntentFilter("chat");
        broadcastManager.registerReceiver(chatReceiver, chatFilter);
    }

    // 接收到聊天訊息會在TextView呈現
    private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            ChatMessage chatMessage = new Gson().fromJson(message, ChatMessage.class);
            String m = chatMessage.getMessage();
            if(m.equals("cancel")){
                new AlertDialog.Builder(activity)
                        /* 設定標題 */
                        //.setTitle(R.string.textTitle)
                        /* 設定圖示 */
                        //.setIcon(R.drawable.alert)
                        /* 設定訊息文字 */
                        .setMessage(R.string.textCancelCheck)
                        /* 設定positive與negative按鈕上面的文字與點擊事件監聽器 */
                        .setPositiveButton(R.string.textYes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /* 結束此Activity頁面 */
                                handler.removeCallbacks(runnable);
                                activity.onBackPressed();
                            }
                        })
                        .setNegativeButton(R.string.textNo, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /* 關閉對話視窗 */
                                handler.removeCallbacks(runnable);
                                activity.onBackPressed();
                            }
                        })
                        .show();
            }
            else if (m.equals("start")){
                status = 2;
            }
            else if (m.equals("finish")){
                handler.removeCallbacks(runnable);
                star();
            }
            // 接收到聊天訊息，若發送者與目前聊天對象相同，就換頁
            Log.d(TAG, message);
        }
    };

    private void addMarker(LatLng latLng) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.car);
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(icon));
        moveMap(latLng);
    }

    private void moveMap(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(15)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
    }

    private void star() {
        Intent loginIntent = new Intent(activity, StarDialogActivity.class);
        startActivityForResult(loginIntent, REQ_STAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        view = getView();
        if(resultCode == RESULT_OK) {
            if (requestCode == REQ_STAR) {
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                float n = pref.getFloat("n", 0);

                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "OrderServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "getOrderId");
                    jsonObject.addProperty("driver_id", String.valueOf(driver_id));
                    try {
                        String jsonIn = new CommonTask(url, jsonObject.toString()).execute().get();
                        order_id = Integer.parseInt(jsonIn);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }

                Order order = new Order(order_id,n);

                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "OrderServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "driver_scoreUpdate");
                    jsonObject.addProperty("order", new Gson().toJson(order));
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, R.string.textUpdateFail);
                    } else {
                        Common.showToast(activity, R.string.textUpdateSuccess);
                    }
                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }
                CommonTwo.showToast(activity,String.valueOf(n));
                activity.onBackPressed();
            }
        }
        else if(resultCode == RESULT_CANCELED){
            if (requestCode == REQ_STAR) {
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                float m = pref.getFloat("m", 0);

                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "OrderServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "getOrderId");
                    jsonObject.addProperty("driver_id", String.valueOf(driver_id));
                    try {
                        String jsonIn = new CommonTask(url, jsonObject.toString()).execute().get();
                        order_id = Integer.parseInt(jsonIn);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }

                Order order = new Order(order_id,m);

                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "OrderServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "driver_scoreUpdate");
                    jsonObject.addProperty("order", new Gson().toJson(order));
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.parseInt(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, R.string.textUpdateFail);
                    } else {
                        Common.showToast(activity, R.string.textUpdateSuccess);
                    }
                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }
                CommonTwo.showToast(activity,String.valueOf(m));
                activity.onBackPressed();
            }
        }
    }

    private Address geocode(String locationName) {
        Geocoder geocoder = new Geocoder(activity);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocationName(locationName, 1);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        if (addressList == null || addressList.isEmpty()) {
            return null;
        } else {
            return addressList.get(0);
        }
    }

    private void showPhoto(){
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;
        try {
            String url = Common.URL + "DriverServlet";
            bitmap = new DriverImageTask(url, driver_id, imageSize).execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            ivDriver.setImageBitmap(bitmap);
        } else {
            ivDriver.setImageResource(R.drawable.no_image);
        }
    }
}
