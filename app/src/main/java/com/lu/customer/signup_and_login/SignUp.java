package com.lu.customer.signup_and_login;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.lu.customer.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUp extends Fragment {
    private Activity activity;
    private Button btContinue;
    private EditText etRealName, etEmail, etPassword, etPhoneNumber;
    private final static String TAG = "SignUp";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity.setTitle(R.string.textSignUP);
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btContinue = view.findViewById(R.id.btContinue);
        etRealName = view.findViewById(R.id.etRealName);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);

        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle sign2 = new Bundle();

                String realName = etRealName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();

                if (realName.isEmpty()) {
                    etRealName.setError("真實姓名不能為空");
                }
                if (password.isEmpty()) {
                    etPassword.setError("密碼不能為空");
                }
                if (email.isEmpty()) {
                    etEmail.setError("Email不能為空");
                }
                if (phoneNumber.isEmpty()) {
                    etPhoneNumber.setError("手機號碼不能為空");
                }
                if (realName.isEmpty() || password.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
                    return;
                }

                sign2.putString("name", realName);
                sign2.putString("password", password);
                sign2.putString("email", email);
                sign2.putString("phoneNumber", phoneNumber);
//                Navigation.findNavController(v).navigate(R.id.action_signUp_to_signUpCreditCard, sign1);
                Navigation.findNavController(v).navigate(R.id.action_signUp_to_signUp_2, sign2);

            }
        });
    }
}
