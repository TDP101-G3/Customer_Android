package com.lu.customer.signup_and_login;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lu.customer.Common;
import com.lu.customer.R;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Logout extends Fragment {
    private Activity activity;
    private static int customer_id = 0;
    private static String customer_name = null;
    private SharedPreferences preferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public void onStart() {
        super.onStart();
        preferences = activity.getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
        customer_id = preferences.getInt("customer_id", customer_id);
        customer_name = preferences.getString("customer_name", customer_name);
        if (customer_id != 0){
            preferences.edit()   //開啟編輯狀態，編輯的東西暫存到記憶體裡
                    .putInt("customer_id", 0)
                    .putString("customer_name", null)
                    .apply();   //真的把記憶體的東西存到檔案裡
            Navigation.findNavController(getView()).navigate(R.id.action_logout_to_login);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }
}
