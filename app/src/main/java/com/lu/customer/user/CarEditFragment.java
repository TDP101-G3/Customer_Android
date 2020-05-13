package com.lu.customer.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lu.customer.Common;
import com.lu.customer.CommonTask;
import com.lu.customer.Customer;
import com.lu.customer.R;


public class CarEditFragment extends Fragment {
    private static final String TAG = "TAG_CarEditFragment";
    FragmentActivity activity;
    private TextInputEditText etPlate, etBrand, etModel, etColor;
    private Customer customer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.textCarEdit);
        super.onCreateView(inflater, container, savedInstanceState);  //???
        return inflater.inflate(R.layout.fragment_car_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etPlate = view.findViewById(R.id.etPlate);
        etBrand = view.findViewById(R.id.etBrand);
        etModel = view.findViewById(R.id.etModel);
        etColor = view.findViewById(R.id.etColor);

        final NavController navController;
        navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("customer") == null) {
            Common.showToast(activity, R.string.textNoCustomerFound);
            navController.popBackStack();
            return;
        }
        customer = (Customer) bundle.getSerializable("customer");
        showCar();

        Button btDone = view.findViewById(R.id.btDone);
        btDone.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                String plate = etPlate.getText().toString().trim();
                if (plate.length() <= 0) {
                    Common.showToast(activity, R.string.textPlateIsInvalid);
                    return;
                }
                String brand = etBrand.getText().toString().trim();
                if (brand.length() <= 0) {
                    Common.showToast(activity, R.string.textBrandIsInvalid);
                    return;
                }
                String model = etModel.getText().toString().trim();
                if (model.length() <= 0) {
                    Common.showToast(activity, R.string.textModelIsInvalid);
                    return;
                }
                String color = etColor.getText().toString().trim();
                if (color.length() <= 0) {
                    Common.showToast(activity, R.string.textModelIsInvalid);
                    return;
                }
                String brandModel = "";
                brandModel += brand + " " + model;
                customer = customer.updateCar(plate, brandModel, color);

                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "/CustomerServlet";
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.addProperty("action", "updateCar");
                    jsonObject.addProperty("customer", new Gson().toJson(customer));

                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        System.out.println("Update result= " + result);
                        count = Integer.valueOf(result);
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
                /* 回前一個Fragment */
                navController.popBackStack();
            }
        });
    }

    private void showCar() {
        etPlate.setText(customer.getCustomer_number_plate());
        String brandModel = customer.getCustomer_car_model();
        String[] sArr = brandModel.split(" ");
        etBrand.setText(sArr[0]);
        etModel.setText(sArr[1]);
        etColor.setText(customer.getCustomer_car_color());
    }



}
