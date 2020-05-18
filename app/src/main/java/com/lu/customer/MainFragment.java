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
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.lu.customer.CommonTwo.chatWebSocketClient;


public class MainFragment extends Fragment {
    private static final String TAG = "TAG_MainFragment";
    private static final int PER_ACCESS_LOCATION = 0;
    private static final int REQ_CALL = 2;
    private static final int REQ_WAIT = 3;
    private EditText etStart,etEnd;
    private Button btCall;
    private Activity activity;
    private MapView mapView;
    private GoogleMap map;
    private View view;
    private int driver_id = 0;
    private int customer_id;
    private LocalBroadcastManager broadcastManager;
    private String order_start,order_end;
    private String user,driver;
    private double order_money,driver_income;
    private Handler handler = new Handler();
    private Runnable runnable;
    private int money,distance;
    private double startLatitude,startLongitude,endLatitude,endLongitude;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        customer_id = pref.getInt("customer_id", 0);
        user = "customer"+customer_id;
        CommonTwo.saveUserName(activity,user);
        broadcastManager = LocalBroadcastManager.getInstance(activity);
        CommonTwo.connectServer(activity, CommonTwo.loadUserName(activity));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerChatReceiver();

        mapView = view.findViewById(R.id.mapView);
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

        btCall = view.findViewById(R.id.btCall);
        btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });

    }

    private void call() {
        Intent loginIntent = new Intent(activity, CallDialogActivity.class);
        startActivityForResult(loginIntent, REQ_CALL);
    }

    private void waitDriver(){
        Intent loginIntent = new Intent(activity, WaitDialogActivity.class);
        startActivityForResult(loginIntent, REQ_WAIT);
    }

    @Override
    public void onStart() {
        super.onStart();
        askAccessLocationPermission();
    }

    private void askAccessLocationPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        int result = ActivityCompat.checkSelfPermission(activity, permissions[0]);
        if (result == PackageManager.PERMISSION_DENIED) {
            requestPermissions(permissions, PER_ACCESS_LOCATION);
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        view = getView();
        if(resultCode == RESULT_OK) {
            if (requestCode == REQ_CALL) {
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                order_start = pref.getString("start", "");
                order_end = pref.getString("end", "");
                String startLocationName = order_start;
                String endLocationName = order_end;

                // geocode
                Address startAddress = geocode(startLocationName);
                if (startAddress == null) {
                    Toast.makeText(activity, R.string.textLocationNotFound, Toast.LENGTH_SHORT).show();
                    return;
                }
                startLatitude = startAddress.getLatitude();
                startLongitude = startAddress.getLongitude();

                Address endAddress = geocode(endLocationName);
                if (endAddress == null) {
                    Toast.makeText(activity, R.string.textLocationNotFound, Toast.LENGTH_SHORT).show();
                    return;
                }
                endLatitude = endAddress.getLatitude();
                endLongitude = endAddress.getLongitude();
                pref.edit()
                        .putFloat("startLatitude", (float)startLatitude)
                        .putFloat("startLongitude", (float)startLongitude)
                        .putFloat("endLatitude", (float)endLatitude)
                        .putFloat("endLongitude", (float)endLongitude)
                        .apply();
                float[] results = new float[1];
                Location.distanceBetween(startLatitude,
                        startLongitude, endLatitude,
                        endLongitude, results);
                float d = results[0];
                distance = (int) d*2/1000+1;
                calculateMoney();
                if(distance < 3){
                    money = 300;
                }
                if(distance > 10){
                    money = money + (distance-10)*50;
                }
                if (distance > 30){
                    money = money + (20*50) + (distance-30)/2*50;
                }
                String text ="出發地：\n"+startLocationName+"\n目的地：\n"+endLocationName+"\n金額："+money;
                order_money = money;
                DecimalFormat mDecimalFormat = new DecimalFormat("#.#");
                driver_income = Double.parseDouble(mDecimalFormat.format((double) money*2/3));
                new AlertDialog.Builder(activity)
                        /* 設定標題 */
                        .setTitle(R.string.textMessage)
                        /* 設定圖示 */
                        //.setIcon(R.drawable.alert)
                        /* 設定訊息文字 */
                        .setMessage(text)
                        /* 設定positive與negative按鈕上面的文字與點擊事件監聽器 */
                        .setPositiveButton(R.string.textYes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /* 結束此Activity頁面 */

                                if (Common.networkConnected(activity)) {
                                    String url = Common.URL + "CustomerServlet";
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("action", "matchDriver");
                                    jsonObject.addProperty("startLatitude", startLatitude);
                                    jsonObject.addProperty("startLongitude", startLongitude);
                                    try {
                                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                        driver_id = Integer.parseInt(result);
                                    } catch (Exception e) {
                                        Log.e(TAG, e.toString());
                                    }
                                } else {
                                    Common.showToast(activity, R.string.textNoNetwork);
                                }
                                if(driver_id != 0) {
                                    driver = "driver" + driver_id;
                                    CommonTwo.saveDriverName(activity, driver);
                                    String sender = CommonTwo.loadUserName(activity);
                                    String friend = CommonTwo.loadDriverName(activity);
                                    String message = "call";
                                    ChatMessage chatMessage = new ChatMessage("chat", sender, friend, message);
                                    String chatMessageJson = new Gson().toJson(chatMessage);
                                    chatWebSocketClient.send(chatMessageJson);
                                    Log.d(TAG, "output: " + chatMessageJson);

                                    waitDriver();
                                }
                                else{
                                    Common.showToast(activity, String.valueOf(driver_id));
                                    dialog.cancel();
                                }
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
            else if(requestCode == REQ_WAIT){
                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "OrderServlet";
                    Order order = new Order(customer_id, driver_id, order_start, order_end, order_money, driver_income);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "orderInsert");
                    jsonObject.addProperty("order", new Gson().toJson(order));
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(getActivity(), R.string.textInsertFail);
                    } else {
                        Common.showToast(getActivity(), R.string.textInsertSuccess);
                    }
                } else {
                    Common.showToast(getActivity(), R.string.textNoNetwork);
                }
                Driver driver = new Driver(driver_id);
                Bundle bundle = new Bundle();
                bundle.putSerializable("driver", driver);
                Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_callFragment,bundle);
            }
        }
        else if(resultCode == RESULT_CANCELED){
            if (requestCode == REQ_CALL){
                Common.showToast(activity,"取消");
            } else if(requestCode == REQ_WAIT){
                Common.showToast(activity, "配對失敗");
            }
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
            // 接收到聊天訊息，若發送者與目前聊天對象相同，就換頁
            Log.d(TAG, message);
        }
    };

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

    private Address reverseGeocode(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(activity);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        if (addressList == null || addressList.isEmpty()) {
            return null;
        } else {
            return addressList.get(0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Fragment頁面切換時解除註冊，但不需要關閉WebSocket，
        // 否則回到前頁好友列表，會因為斷線而無法顯示好友
        broadcastManager.unregisterReceiver(chatReceiver);
    }

    public void calculateMoney(){
        Calendar calendar = Calendar.getInstance();
        //小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //分钟
        int minute = calendar.get(Calendar.MINUTE);
        //开始时间
        int sth = 07;//小时
        int stm = 00;//分
        //结束时间
        int eth = 23;//小时
        int etm = 59;//分

        if (hour >= sth && hour <= eth) {
            money = 450;
        } else {
            money = 550;
        }
    }
}
