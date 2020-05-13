package com.lu.customer.order;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lu.customer.Common;
import com.lu.customer.CommonTask;
import com.lu.customer.Driver;
import com.lu.customer.Order;
import com.lu.customer.R;


public class orderDetailFragment extends Fragment {
    private static final String TAG = "orderDetailFragment";
    private Activity activity;
    private TextView tvOrderNumber, tvDetailStartAddress, tvDetailEndAddress, tvDetailOrderDate, tvDetailOrderMoney, tvDetailDriver, tvDetailComment, tvDetailDriverRate, tvHelp;
    private Order order;
    private CommonTask getDriverNameTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.textOrder);
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController;
        navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("order") == null) {
            Common.showToast(activity, R.string.textNoOrdersFound);
            navController.popBackStack();
            return;
        }
        order = (Order) bundle.getSerializable("order");

        tvOrderNumber = view.findViewById(R.id.tvOrderNumber);
        tvDetailStartAddress = view.findViewById(R.id.tvDetailStartAddress);
        tvDetailEndAddress = view.findViewById(R.id.tvDetailEndAddress);
        tvDetailOrderDate = view.findViewById(R.id.tvDetailOrderDate);
        tvDetailOrderMoney = view.findViewById(R.id.tvDetailOrderMoney);
        tvDetailDriver = view.findViewById(R.id.tvDetailDriver);
        tvDetailComment = view.findViewById(R.id.tvDetailComment);
        tvDetailDriverRate = view.findViewById(R.id.tvDetailDriverRate);
        tvHelp = view.findViewById(R.id.tvHelp);

        tvOrderNumber.setText(String.valueOf(order.getOrder_id()));
        tvDetailStartAddress.setText(order.getOrder_start());
        tvDetailEndAddress.setText(order.getOrder_end());
        tvDetailOrderDate.setText(order.getOrder_time());
        tvDetailOrderMoney.setText(String.valueOf(order.getOrder_money()));
        String drive_name = getDriverName(order.getDriver_id());
        String text = "您給" + drive_name + "評分了";
        tvDetailDriver.setText(drive_name);
        tvDetailComment.setText(text);
        tvDetailDriverRate.setText(String.valueOf(order.getDriver_score()));

    }
    private String getDriverName(int driver_id) {
        String drive_name = "";
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/DriverServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");
            jsonObject.addProperty("driver_id", driver_id);
            String jsonOut = jsonObject.toString();
            getDriverNameTask = new CommonTask(url, jsonOut);
            try{
                String jsonIn = getDriverNameTask.execute().get();
                Driver driver = null;
                driver = new Gson().fromJson(jsonIn, Driver.class);
                drive_name = driver.getDriver_name();
            }catch (Exception e){
                Log.e (TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return drive_name;
    }
}
