package com.lu.customer.payment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import com.lu.customer.R;

import org.json.JSONException;
import org.json.JSONObject;

import tech.cherri.tpdirect.api.TPDCardInfo;
import tech.cherri.tpdirect.api.TPDConsumer;
import tech.cherri.tpdirect.api.TPDGooglePay;
import tech.cherri.tpdirect.api.TPDMerchant;
import tech.cherri.tpdirect.api.TPDServerType;
import tech.cherri.tpdirect.api.TPDSetup;
import tech.cherri.tpdirect.callback.TPDGooglePayListener;
import tech.cherri.tpdirect.callback.TPDTokenFailureCallback;
import tech.cherri.tpdirect.callback.TPDTokenSuccessCallback;

import static com.lu.customer.Common.CARD_TYPES;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "TAG_PaymentActivity";
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 101;
    private TPDGooglePay tpdGooglePay;
    private RelativeLayout btBuy;
    private TextView tvResult;
    private TextView tvPaymentInfo;
    private PaymentData paymentData;
    private Button btConfirmPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleViews();

        Log.d(TAG, "SDK version is " + TPDSetup.getVersion());

        // 使用TPDSetup設定環境。每個設定值出處參看strings.xml
        TPDSetup.initInstance(getApplicationContext(),
                Integer.parseInt(getString(R.string.TapPay_AppID)),
                getString(R.string.TapPay_AppKey),
                TPDServerType.Sandbox); //Sandbox測試區

        prepareGooglePay();
    }

    public void prepareGooglePay(){
     TPDMerchant tpdMerchant = new TPDMerchant();
     tpdMerchant.setMerchantName(getString(R.string.TapPay_MerchantName)); //設定商店名稱、
     tpdMerchant.setSupportedNetworks(CARD_TYPES); //設定信用卡種類

     //設定客戶填寫項目
     TPDConsumer  tpdConsumer = new TPDConsumer();
     tpdConsumer.setPhoneNumberRequired(false); //不需要電話號碼
     tpdConsumer.setShippingAddressRequired(false); //不需要運送地址
     tpdConsumer.setEmailRequired(false); //不需要Email

     tpdGooglePay = new TPDGooglePay(this , tpdMerchant , tpdConsumer);
     // 檢查user裝置是否支援Google pay
     tpdGooglePay.isGooglePayAvailable(new TPDGooglePayListener() {
         @Override
         public void onReadyToPayChecked(boolean isReadyToPay, String msg) {
            Log.d(TAG,"Pay with Google availability: " + isReadyToPay);
            if(isReadyToPay){
                btBuy.setEnabled(true);
            }else{
                tvResult.setText(R.string.textCannotUseGPay);
            }
         }
     });
 }

    private void handleViews() {
        btBuy = findViewById(R.id.btBuy);
        btBuy.setEnabled(false);
        btBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳出user資訊讓user確認，確認後會呼叫onActivityResult()
                tpdGooglePay.requestPayment(TransactionInfo.newBuilder()
                .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                .setTotalPrice("100") //消費總金額
                .setCurrencyCode("TWD") //幣別
                .build(), LOAD_PAYMENT_DATA_REQUEST_CODE);
            }
        });

        tvPaymentInfo = findViewById(R.id.tvPaymentInfo);

        btConfirmPay = findViewById(R.id.btConfirmPay);
        btConfirmPay.setEnabled(false);
        btConfirmPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPrimeTapPay(paymentData);
            }
        });

        tvResult = findViewById(R.id.tvResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE){
            switch (resultCode){
                case Activity.RESULT_OK:
                    btConfirmPay.setEnabled(true);
                    paymentData = PaymentData.getFromIntent(data); //取得付款資訊
                    if(paymentData != null){
                        showPaymentInfo(paymentData);
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    btConfirmPay.setEnabled(false);
                    tvResult.setText(R.string.textCanceled);
                    break;
                case AutoResolveHelper.RESULT_ERROR:
                    btConfirmPay.setEnabled(false);
                    Status status = AutoResolveHelper.getStatusFromIntent(data);
                    if(status != null){
                        String text = "status code" + status.getStatusCode() +
                                " , message: " + status.getStatusMessage();
                        Log.d(TAG , text);
                        tvResult.setText(text);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void showPaymentInfo(PaymentData paymentData) {
        try {
            JSONObject paymentDataJO = new JSONObject(paymentData.toJson());
            String cardDescription = paymentDataJO.getJSONObject("paymentMethodData").getString
                    ("description");
                    tvPaymentInfo.setText(cardDescription);
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    private void getPrimeTapPay(PaymentData paymentData) {
        showProgressDialog();
        // 呼叫getPrime()只將支付資料提交給TapPay以取得prime
        tpdGooglePay.getPrime(
                paymentData,
                new TPDTokenSuccessCallback() {
                    @Override
                    public void onSuccess(String prime, TPDCardInfo tpdCardInfo) {
                        hideProgressDialog();

                        String text = "Your prime is " + prime
                                + "\n\nUse below cURL to proceed the payment : \n"
                                + ApiUtil.generatePayByPrimeCURLForSandBox(prime,
                                getString(R.string.TapPay_PartnerKey),
                                getString(R.string.TapPay_MerchantID));
                        Log.d(TAG, text);
                        tvResult.setText(text);
                    }
                },
                new TPDTokenFailureCallback() {
                    @Override
                    public void onFailure(int status, String reportMsg) {
                        hideProgressDialog();
                        String text = "TapPay getPrime failed. status: " + status + ", message: " + reportMsg;
                        Log.d(TAG, text);
                        tvResult.setText(text);
                    }
                });
    }

    public ProgressDialog mProgressDialog;


    private void showProgressDialog() {
        if(mProgressDialog == null ){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Loading...");
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }
}