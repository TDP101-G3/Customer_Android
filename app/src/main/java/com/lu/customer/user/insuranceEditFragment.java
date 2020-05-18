package com.lu.customer.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lu.customer.Common;
import com.lu.customer.CommonTask;
import com.lu.customer.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;


public class insuranceEditFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private String TAG = "TAG_InsuranceEditFragment";
    private FragmentActivity activity;
    private TextView tvInsuranceName, tvInsuranceDate;
    private Insurance insurance;
    private ImageView ivInsurance;
    private byte[] image;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_IMAGE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private static final int PER_EXTERNAL_STORAGE = 201;
    private Uri contentUri, croppedImageUri;
    private ConstraintLayout layoutExpire;
    private static int mYear, mMonth, mDay;
    private int customer_id ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.textAssuranceEdit);
        return inflater.inflate(R.layout.fragment_insurance_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvInsuranceName = view.findViewById(R.id.tvInsuranceName);
        tvInsuranceDate = view.findViewById(R.id.tvInsuranceDate);
        layoutExpire = view.findViewById(R.id.layoutExpire);
        ivInsurance = view.findViewById(R.id.ivInsurance);
        final NavController navController;
        navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("insurance") == null) {
            Common.showToast(activity, R.string.textNoInsuranceFound);
            navController.popBackStack();
            return;
        }
        insurance = (Insurance) bundle.getSerializable("insurance");
        customer_id = (int) bundle.getSerializable("customer_id");
        String insuranceName = String.valueOf(insurance.getInsuranceName());
        if(insuranceName.equals("carDamage")){
            tvInsuranceName.setText(R.string.textCarDamage);
        } else if (insuranceName.equals("compulsory")){
            tvInsuranceName.setText(R.string.textCompulsory);
        } else if (insuranceName.equals("third")) {
            tvInsuranceName.setText(R.string.textCarThirdParty);
        }

        // 選擇時間
        layoutExpire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(
                        activity,
                        insuranceEditFragment.this,insuranceEditFragment.mYear, insuranceEditFragment.mMonth, insuranceEditFragment.mDay)
                        .show();
                showExpireDate();
            }
        });

        // 完成上傳
        Button btCommit = view.findViewById(R.id.btCommit);
        btCommit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String date = tvInsuranceDate.getText().toString();
                if (date.length() <= 0) {
                    Common.showToast(activity, R.string.textDateIsInvalid);
                    return;
                }
                insurance = insurance.updateInsurance(date);

                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "/CustomerServlet";
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.addProperty("action", "updateInsurance");
                    jsonObject.addProperty("customer_id", customer_id);
                    jsonObject.addProperty("insurance", new Gson().toJson(insurance));
                    // 有圖才上傳
                    if (image == null) {
                        Common.showToast(activity, R.string.textImageIsInvalid);
                        return;
                    } else {jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));}
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

        Button btInsuranceAdd = view.findViewById(R.id.btInsuranceAdd);
        btInsuranceAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                PopupMenu popupMenu = null;
                // 判斷版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    popupMenu = new PopupMenu(activity, view, Gravity.END);
                }
                popupMenu.inflate(R.menu.photo_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.takePicture:
                                askExternalStoragePermission();
                                takePicture();
                                break;
                            case R.id.pickPicture:
                                pickPicture();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();

            }
        });

    }

    /* 覆寫OnDateSetListener.onDateSet()以處理日期挑選完成事件。
           日期挑選完成會呼叫此方法，並傳入選取的年月日 */

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        insuranceEditFragment.mYear = year;
        insuranceEditFragment.mMonth = month;
        insuranceEditFragment.mDay = day;
        updateDisplay();
    }


    private void showExpireDate() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
    }

    private void updateDisplay() {
        tvInsuranceDate.setText(new StringBuilder().append(mYear).append("-")
                .append(pad(mMonth + 1)).append("-").append(pad(mDay))
                );
    }

    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        } else {
            return "0" + number;
        }
    }

    private void showToast(Context context, int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }

    // 詢問使用者 取用外部儲存體的公開檔案
    private void askExternalStoragePermission() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        int result = ContextCompat.checkSelfPermission(activity, permissions[0]);
        if (result == PackageManager.PERMISSION_DENIED) {
            requestPermissions(permissions, PER_EXTERNAL_STORAGE);
        }
    }

    private void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir != null && !dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, getString(R.string.textDirNotCreated));
                return;
            }
        }
        dir = new File(dir, "picture.jpg"); // 要存檔的路徑
        contentUri = FileProvider.getUriForFile( // 在manifest23行還有xml都要做事情
                activity, activity.getPackageName() + ".provider", dir);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(intent, REQ_TAKE_PICTURE); // 拍照
        } else {
            showToast(activity, R.string.textNoCameraApp);
        }
    }

    private void pickPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { // 使用者決定要不要照片
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_PICK_IMAGE:
                    Uri uri = data.getData();
                    crop(uri);
                    break;
                case REQ_CROP_PICTURE:
                    Log.d(TAG, "REQ_CROP_PICTURE: " + croppedImageUri.toString());
                    try {
                        Bitmap picture = BitmapFactory.decodeStream(
                                activity.getContentResolver().openInputStream(croppedImageUri));
                        ivInsurance.setImageBitmap(picture);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        picture.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        image = out.toByteArray();
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, e.toString());
                    }
                    break;
            }
        }
    }

    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        croppedImageUri = Uri.fromFile(file);
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // the recipient of this Intent can read soruceImageUri's data
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // set image source Uri and type
            cropIntent.setDataAndType(sourceImageUri, "image/*");
            // send crop message
            cropIntent.putExtra("crop", "true");
            // aspect ratio of the cropped area, 0 means user define
            cropIntent.putExtra("aspectX", 0); // this sets the max width
            cropIntent.putExtra("aspectY", 0); // this sets the max height
            // output with and height, 0 keeps original size
            cropIntent.putExtra("outputX", 0);
            cropIntent.putExtra("outputY", 0);
            // whether keep original aspect ratio
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri);
            // whether return data by the intent
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQ_CROP_PICTURE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException e) {
            showToast(activity, R.string.textNoImageCropAppFound);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PER_EXTERNAL_STORAGE) {
            // 如果user不同意將資料儲存至外部儲存體的公開檔案，就將儲存按鈕設為disable
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(activity, R.string.textShouldGrant, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
