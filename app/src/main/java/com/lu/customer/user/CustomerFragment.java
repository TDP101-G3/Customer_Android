package com.lu.customer.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lu.customer.Common;
import com.lu.customer.CommonTask;
import com.lu.customer.Customer;
import com.lu.customer.ImageTask;
import com.lu.customer.R;

import java.text.DecimalFormat;

import static android.content.Context.MODE_PRIVATE;


public class CustomerFragment extends Fragment {
    private String TAG = "TAG_CustomerFragment";
    private Activity activity;
    private TextView tvUserName, tvPhone, tvEmail, tvCarPlate;
    private ImageView ivCustomerPhoto;
    private CommonTask carFindByIdTask;
    private ImageTask customerImageTask;
    private Customer customer;
    private int imageSize;
    private int customer_id;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        customer_id = pref.getInt("customer_id", 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.personalData);
        return inflater.inflate(R.layout.fragment_customer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUserName = view.findViewById(R.id.tvUserName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvCarPlate = view.findViewById(R.id.tvCarPlate);
        ivCustomerPhoto = view.findViewById(R.id.ivCustomerPhoto);
        customer = findById(customer_id);
        setCustomer(customer);
        setCustomerPhoto(customer_id);

        ivCustomerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_customerFragment_to_userPhotoFragment);
            }
        });

        Button btUser = view.findViewById(R.id.btUser);
        btUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("customer", customer);
                Navigation.findNavController(view).navigate(R.id.action_customerFragment_to_customerEditFragment, bundle);
            }
        });

        Button btCarAssurance = view.findViewById(R.id.btCarInsurance);
        btCarAssurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_customerFragment_to_carInsuranceFragment);
            }
        });
    }

    private void setCustomerPhoto(int customer_id) {
        String url = Common.URL + "/CustomerServlet";
        imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        customerImageTask = new ImageTask(url, customer_id, imageSize, ivCustomerPhoto);
        customerImageTask.execute();
    }

    @SuppressLint("LongLogTag")
    private Customer findById(int customer_id) {
        Customer customer = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/CustomerServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");
            jsonObject.addProperty("customer_id", customer_id);
            String jsonOut = jsonObject.toString();
            carFindByIdTask = new CommonTask(url, jsonOut);
            try{
                String jsonIn = carFindByIdTask.execute().get();
                customer = new Gson().fromJson(jsonIn, Customer.class);
            }catch (Exception e){
                Log.e (TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        System.out.println("findById() return customer = " + customer);
        return customer;
    }
    private void setCustomer(Customer customer){
        tvUserName.setText(String.valueOf(customer.getCustomer_name()));
        tvPhone.setText(String.valueOf(customer.getCustomer_phone()));
        tvEmail.setText(String.valueOf(customer.getCustomer_email()));
        tvCarPlate.setText(String.valueOf(customer.getCustomer_number_plate()));
    }
}
