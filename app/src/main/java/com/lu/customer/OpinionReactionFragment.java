package com.lu.customer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static android.content.Context.MODE_PRIVATE;
public class OpinionReactionFragment extends Fragment {

    private Activity activity;
    private EditText etQuestion;
    private Button btReport;
    private int customer_id;
    private String customer_opinion_question;
    private static final String TAG = "OpinionReactionFragment";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity.setTitle(R.string.textReaction);
        return inflater.inflate(R.layout.fragment_opinion_reaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etQuestion = view.findViewById(R.id.etQuestion);
        btReport = view.findViewById(R.id.btReport);
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        customer_id = pref.getInt("customer_id", 0);
        btReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = etQuestion.getText().toString().trim();
                if (question.length() <= 0) {
                    Common.showToast(getActivity(), R.string.textCannotbenull);
                    Log.e(TAG, "輸入為空");
                    return;
                }
                customer_opinion_question = question;
                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "OpinionServlet";
                    Opinion opinion = new Opinion(customer_id, customer_opinion_question);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "CustomeropinionInsert");
                    jsonObject.addProperty("opinion", new Gson().toJson(opinion));
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(getActivity(), R.string.textInsertFail);
                    } else {
                        Common.showToast(getActivity(), R.string.textInsertSuccess);
                    }
                } else {
                    Common.showToast(getActivity(), R.string.textNoNetwork);
                }
                etQuestion.setText("");
            }
        });
    }
}