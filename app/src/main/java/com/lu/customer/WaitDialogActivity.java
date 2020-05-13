package com.lu.customer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

public class WaitDialogActivity extends AppCompatActivity {
    private static final String TAG = "WaitDialogActivity";
    private LocalBroadcastManager broadcastManager;
    private int customer_id = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_activity);
        broadcastManager = LocalBroadcastManager.getInstance(this);
        registerChatReceiver();
        CommonTwo.connectServer(this, CommonTwo.loadUserName(this));
    }

    /**
     * 註冊廣播接收器攔截聊天資訊
     * 因為是在Fragment註冊，所以Fragment頁面未開時不會攔截廣播
     */
    private void registerChatReceiver() {
        IntentFilter chatFilter = new IntentFilter("chat");
        broadcastManager.registerReceiver(chatReceiver, chatFilter);
    }

    private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            final ChatMessage chatMessage = new Gson().fromJson(message, ChatMessage.class);
            String m = chatMessage.getMessage();
            // 接收到聊天訊息，若發送者與目前聊天對象相同，就換頁
            if (m.equals("yes")) {
                handleViews(m);
            }
            else if(m.equals("no")){
                handleViews(m);
            }
            Log.d(TAG, message);
        }
    };

    private void handleViews(String m) {
        if(m.equals("yes")){
            setResult(RESULT_OK);
            finish();
        }
        else if(m.equals("no")){
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTitle("");
    }

    private void showToast(int messageResId) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Fragment頁面切換時解除註冊，但不需要關閉WebSocket，
        // 否則回到前頁好友列表，會因為斷線而無法顯示好友
        broadcastManager.unregisterReceiver(chatReceiver);
    }
}
