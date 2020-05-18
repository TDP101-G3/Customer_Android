package com.lu.customer.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lu.customer.Common;
import com.lu.customer.CommonTask;
import com.lu.customer.Customer;
import com.lu.customer.R;


public class CustomerEditFragment extends Fragment {
    private static final String TAG = "TAG_CustomerEditFragment";
    private FragmentActivity activity;
    private EditText etName, etPhone, etEmail;
    private Customer customer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);  //???
        activity.setTitle(R.string.textCustomerEdit);
        return inflater.inflate(R.layout.fragment_customer_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etName = view.findViewById(R.id.etName);
        etPhone = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);

        final NavController navController;
        navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("customer") == null) {
            Common.showToast(activity, R.string.textNoCustomerFound);
            navController.popBackStack();
            return;
        }
        customer = (Customer) bundle.getSerializable("customer");
        showCustomer();

        Button btDone = view.findViewById(R.id.btDone);
        btDone.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                if (name.length() <= 0) {
                    Common.showToast(activity, R.string.textNameIsInvalid);
                    return;
                }
                String phone = etPhone.getText().toString();
                if (phone.length() <= 0) {
                    Common.showToast(activity, R.string.textPhoneIsInvalid);
                    return;
                }
                String email = etEmail.getText().toString();
                if (email.length() <= 0) {
                    Common.showToast(activity, R.string.textEmailIsInvalid);
                    return;
                }
                customer = customer.updateCustomer(name, phone, email);

                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "/CustomerServlet";
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.addProperty("action", "updateCustomer");
                    jsonObject.addProperty("customer", new Gson().toJson(customer));
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
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

    private void showCustomer() {
        etName.setText(customer.getCustomer_name());
        etPhone.setText(customer.getCustomer_phone());
        etEmail.setText(customer.getCustomer_email());
    }
}
