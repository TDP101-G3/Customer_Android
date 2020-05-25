package com.lu.customer.payment;

import android.util.Log;

import com.lu.customer.Common;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiUtil {
    private  static  final  String TAG = "TAG_ApiUtil";

    // 開啟MyTask (AsyncTask) 將交易資訊送至TapPay測試區
    public static String generatePayByPrimeCURLForSandBox(String prime , String partnerKey , String merchantId){
        JSONObject paymentJO = new JSONObject();
        try {
            paymentJO.put("partner_key" , partnerKey);
            paymentJO.put("prime" , prime);
            paymentJO.put("merchant_id" , merchantId);
            paymentJO.put("amount" , 1000);
            paymentJO.put("currency" , "TWD");
            paymentJO.put("order_number", "SN0001");
            paymentJO.put("details" , "代駕費用");
            JSONObject cardHolderJO = new JSONObject();
            cardHolderJO.put("phone_number" , "+886987654321");
            cardHolderJO.put("name" , "John");
            cardHolderJO.put("email" , "john3300047@yahoo.com.tw" );

            paymentJO.put("cardholder" , cardHolderJO);
            Log.d(TAG , "paymentJO: " + paymentJO.toString());
        } catch(JSONException e) {
            e.printStackTrace();
        }
        // TapPay測試區網址
        String url = Common.TAPPAY_DOMAIN_SANDBOX + Common.TAPPAY_PAY_BY_PRIME_URL;
        MyTask myTask = new MyTask(url , paymentJO.toString() , partnerKey);
        String result = "";
        try{
            result = myTask.execute().get();
        }catch (Exception e){
        Log.e(TAG , e.toString());
        }
        return  result;
    }

}

