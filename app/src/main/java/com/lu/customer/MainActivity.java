package com.lu.customer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TextView tvNameinfo,tvScoreinfo;
    private int customer_id = 1;
    private CircleImageView ivUser;
    private Customer customer = null;
    private Order order = null;
    private Activity activity;
    private Runnable runnable;
    private Handler handler = new Handler();
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);
        setUpActionBar();
        initDrawer();
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navigationView, navController);
        View header = navigationView.getHeaderView(0);
        tvNameinfo = header.findViewById(R.id.tvNameinfo);
        tvScoreinfo = header.findViewById(R.id.tvScoreinfo);
        ivUser = header.findViewById(R.id.ivDriver);
        runnable = new Runnable() {
            public void run() {
                SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                customer_id = pref.getInt("customer_id", 0);
                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "CustomerServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "findById");
                    jsonObject.addProperty("customer_id", customer_id);
                    try {
                        String jsonIn = new CommonTask(url, jsonObject.toString()).execute().get();
                        customer = new Gson().fromJson(jsonIn, Customer.class);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }
                String name = "";
                if (customer != null) {
                    name = customer.getCustomer_name();
                }
                tvNameinfo.setText(name);

                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "OrderServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "getCustomer_score");
                    jsonObject.addProperty("customer_id", customer_id);
                    try {
                        String jsonIn = new CommonTask(url, jsonObject.toString()).execute().get();
                        order = new Gson().fromJson(jsonIn, Order.class);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }
                String score = "";
                Double s = 5.0;
                if (order != null) {
                    s = order.getCustomer_score();
                }
                DecimalFormat mDecimalFormat = new DecimalFormat("#.#");
                score = mDecimalFormat.format(s);
                tvScoreinfo.setText(score);
                showPhoto();
                if(name.equals("")){
                    handler.postDelayed(this,1000);
                }
                else{
                    handler.removeCallbacks(runnable);
                }
            }
        };
        handler.postDelayed(runnable,1000);
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 設定ActionBar標題列的左上角可以加上按鈕
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // 初始化抽屜選單功能
    private void initDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        // 建立ActionBarDrawerToggle監聽器，監聽抽屜開關的狀態
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.textOpen, R.string.textClose);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        // 將左上角按鈕動畫與抽屜選單開關同步化
        actionBarDrawerToggle.syncState();
        navigationView = findViewById(R.id.navigationView);
    }

    // 點擊標題列上的按鈕會呼叫此方法
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 如果抽屜開啟，將之關閉；如果關閉，將之開啟
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPhoto(){
        int imageSize = getResources().getDisplayMetrics().widthPixels / 3;
        Bitmap bitmap = null;
        try {
            String url = Common.URL + "CustomerServlet";
            bitmap = new ImageTask(url, customer_id, imageSize).execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            ivUser.setImageBitmap(bitmap);
        } else {
            ivUser.setImageResource(R.drawable.no_image);
        }
    }
}


