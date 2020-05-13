package com.lu.customer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class CallDialogActivity extends AppCompatActivity {
    private static final String TAG = "CallDialogActivity";
    private EditText etStart,etEnd;
    private CommonTask callTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_activity);
        handleViews();
        setResult(RESULT_CANCELED);
    }

    private void handleViews() {
        etStart = findViewById(R.id.etStart);
        etEnd = findViewById(R.id.etEnd);
        Button btOk = findViewById(R.id.btOk);
        Button btCancel = findViewById(R.id.btCancel);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String start = etStart.getText().toString().trim();
                String end = etEnd.getText().toString().trim();
                if (start.length() <= 0 || end.length() <= 0) {
                    showToast(R.string.textCannotbenull);
                    return;
                }
                SharedPreferences pref = getSharedPreferences(Common.PREF_FILE,
                        MODE_PRIVATE);
                pref.edit()
                        .putString("start", start)
                        .putString("end", end)
                        .apply();
                setResult(RESULT_OK);
                finish();
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTitle("");
    }

    private void showToast(int messageResId) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }


}
