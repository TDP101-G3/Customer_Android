package com.lu.customer.signup_and_login;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.lu.customer.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpCreditcrad extends Fragment {
    private final static String TAG = "TAG_SignUp";
    private Activity activity;
    private Button btContinue;
    private EditText etCreditcardType, etCreditcardNumber, etCreditcardDate, etCreditcardSecureCode;


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
        return inflater.inflate(R.layout.fragment_sign_up_creditcard, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etCreditcardType = view.findViewById(R.id.etCreditcardType);
        etCreditcardNumber = view.findViewById(R.id.etCreditcardNumber);
        etCreditcardDate = view.findViewById(R.id.etCreditcardDate);
        etCreditcardSecureCode = view.findViewById(R.id.etCreditcardSecureCode);
        btContinue = view.findViewById(R.id.btContinue);

        Bundle sign1 = getArguments();
        if (sign1 != null) {
            final String customer_name = sign1.getString("name");
            final String customer_password = sign1.getString("password");
            final String customer_email = sign1.getString("email");
            final String customer_phone = sign1.getString("phoneNumber");
//            String text1 = "User name: " + driver_name + "; password: " + driver_password + "\n";
//            tvResult.append(text1);

            btContinue.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  Bundle sign2 = new Bundle();
                                                  String customer_name_2 = customer_name.trim();
                                                  String customer_password_2 = customer_password.trim();
                                                  String customer_email_2 = customer_email.trim();
                                                  String customer_phone_2 = customer_phone.trim();

                                                  String customer_creditcard_type_2 = etCreditcardType.getText().toString().trim();
                                                  String customer_creditcard_number_2 = etCreditcardNumber.getText().toString().trim();
                                                  String customer_creditcard_date_2 = etCreditcardDate.getText().toString().trim();
                                                  String customer_creditcard_secure_code_2 = etCreditcardSecureCode.getText().toString().trim();
                                                  if (customer_creditcard_type_2.length() <= 0) {
                                                      etCreditcardType.setError("類別不能為空");
                                                  }
                                                  if (customer_creditcard_number_2.length() <= 0) {
                                                      etCreditcardNumber.setError("卡號不能為空");
                                                  }
                                                  if (customer_creditcard_date_2.length() <= 0) {
                                                      etCreditcardDate.setError("到期日不能為空");
                                                  }
                                                  if (customer_creditcard_secure_code_2.length() <= 0) {
                                                      etCreditcardSecureCode.setError("安全碼不能為空");
                                                  }
                                                  if (customer_creditcard_type_2.isEmpty() || customer_creditcard_number_2.isEmpty() || customer_creditcard_date_2.isEmpty()|| customer_creditcard_secure_code_2.isEmpty()) {
                                                      return;
                                                  }


                                                  sign2.putString("name", customer_name_2);
                                                  sign2.putString("password", customer_password_2);
                                                  sign2.putString("email", customer_email_2);
                                                  sign2.putString("phoneNumber", customer_phone_2);
                                                  sign2.putString("creditCardType", customer_creditcard_type_2);
                                                  sign2.putString("creditCardNumber", customer_creditcard_number_2);
                                                  sign2.putString("creditCardDate", customer_creditcard_date_2);
                                                  sign2.putString("creditCardSecureCode", customer_creditcard_secure_code_2);

                                                  Navigation.findNavController(v).navigate(R.id.action_signUpCreditCard_to_signUp_2, sign2);
                                              }

                                          }

            );
        }
    }

}



