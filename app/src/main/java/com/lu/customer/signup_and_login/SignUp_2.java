package com.lu.customer.signup_and_login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lu.customer.Common;
import com.lu.customer.CommonTask;
import com.lu.customer.Customer;
import com.lu.customer.R;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUp_2 extends Fragment {
    private final static String TAG = "TAG_Sign2";
    private Activity activity;
    private Button btSignUp;
    private ImageView ivIdFront, ivIdBack, ivCarDamage, ivCompulsory, ivCarThirdParty, ivUserPhoto;
    private File file_ivIdFront, file_ivIdBack, file_ivCarDamage, file_ivCompulsory, file_ivCarThirdParty, file_ivUserPhoto;
    private EditText etCarNumber, etCarModel, etCarColor;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private static final int PER_EXTERNAL_STORAGE = 201;
    private Uri contentUri,croppedImageUri;
    private int count = 0;
    private byte[] idFront, idBack, CarDamage, Compulsory, CarThirdParty, userPhoto;
    private int action = 0;
//    private TextView tvResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.textSignUP);
        return inflater.inflate(R.layout.fragment_sign_up_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btSignUp = view.findViewById(R.id.btSignUP);
//        tvResult = view.findViewById(R.id.tvResult);
        ivIdFront = view.findViewById(R.id.ivIdFront);
        ivIdBack = view.findViewById(R.id.ivIdBack);
        ivCarDamage = view.findViewById(R.id.ivCarDamage);
        ivCompulsory = view.findViewById(R.id.ivCompulsory);
        ivCarThirdParty = view.findViewById(R.id.ivCarThirdParty);
        etCarNumber = view.findViewById(R.id.etCarNumber);
        etCarModel = view.findViewById(R.id.etCarModel);
        etCarColor = view.findViewById(R.id.etCarColor);
        ivUserPhoto = view.findViewById(R.id.ivUserPhoto);


        Bundle sign2 = getArguments();
        if (sign2 != null) {
            final String customer_name = sign2.getString("name");
            final String customer_password = sign2.getString("password");
            final String customer_email = sign2.getString("email");
            final String customer_phone = sign2.getString("phoneNumber");
//            String text1 = "User name: " + customer_name + "; password: " + customer_password + "\n" ;
//            tvResult.append(text1);


            ivIdFront.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action = 1;
                    PopupMenu popupMenu = null;
                    // 判斷版本
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        popupMenu = new PopupMenu(activity, v, Gravity.END);
                    }
                    popupMenu.inflate(R.menu.photo_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.takePicture:
                                    askExternalStoragePermission();
                                    takePicture("IdFront.jpg",file_ivIdFront);
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
            ivIdBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action = 2;
                    PopupMenu popupMenu = null;
                    // 判斷版本
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        popupMenu = new PopupMenu(activity, v, Gravity.END);
                    }
                    popupMenu.inflate(R.menu.photo_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.takePicture:
                                    askExternalStoragePermission();
                                    takePicture("IdBack.jpg",file_ivIdBack);
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
            ivCarDamage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action = 3;
                    PopupMenu popupMenu = null;
                    // 判斷版本
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        popupMenu = new PopupMenu(activity, v, Gravity.END);
                    }
                    popupMenu.inflate(R.menu.photo_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.takePicture:
                                    askExternalStoragePermission();
                                    takePicture("CarDamage.jpg",file_ivCarDamage);
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
            ivCompulsory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action = 4;
                    PopupMenu popupMenu = null;
                    // 判斷版本
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        popupMenu = new PopupMenu(activity, v, Gravity.END);
                    }
                    popupMenu.inflate(R.menu.photo_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.takePicture:
                                    askExternalStoragePermission();
                                    takePicture("Compulsory.jpg",file_ivCompulsory);
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
            ivCarThirdParty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action = 5;
                    PopupMenu popupMenu = null;
                    // 判斷版本
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        popupMenu = new PopupMenu(activity, v, Gravity.END);
                    }
                    popupMenu.inflate(R.menu.photo_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.takePicture:
                                    askExternalStoragePermission();
                                    takePicture("CarThirdParty.jpg",file_ivCarThirdParty);
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
            ivUserPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action = 6;
                    PopupMenu popupMenu = null;
                    // 判斷版本
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        popupMenu = new PopupMenu(activity, v, Gravity.END);
                    }
                    popupMenu.inflate(R.menu.photo_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.takePicture:
                                    askExternalStoragePermission();
                                    takePicture("UserPhoto.jpg",file_ivUserPhoto);
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


            btSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object carNumber = etCarNumber.getText();
                    Object carModel = etCarModel.getText();
                    Object carColor = etCarColor.getText();
                    String customer_number_plate = carNumber.toString().trim();
                    String customer_car_model = carModel.toString().trim();
                    String customer_car_color = carColor.toString().trim();
                    if (etCarNumber.length() <= 0) {
                        etCarNumber.setError("類別不能為空");
                    }
                    if (etCarModel.length() <= 0) {
                        etCarModel.setError("卡號不能為空");
                    }
                    if (etCarColor.length() <= 0) {
                        etCarColor.setError("到期日不能為空");
                    }
                    if (customer_number_plate.isEmpty() || customer_car_model.isEmpty() || customer_car_color.isEmpty()) {
                        return;
                    }
                    if (idFront == null || idBack == null || CarDamage == null || Compulsory == null || CarThirdParty == null || userPhoto == null) {//如果沒拍照，就不能上傳
                        Common.showToast(activity, "驗證資料不完整就不能繼續註冊哦！");
                        return;
                    } else {
                        if (Common.networkConnected(activity)) {
                            String url = Common.URL + "CustomerServlet";//連伺服器
                            Customer customer = new Customer(customer_name, customer_email, customer_password, customer_phone, customer_number_plate, customer_car_model, customer_car_color);

                            JsonObject jsonObject = new JsonObject();   //建一個物件
                            jsonObject.addProperty("action", "signUp");
                            jsonObject.addProperty("customer", new Gson().toJson(customer));

                            jsonObject.addProperty("imageBase64", Base64.encodeToString(idFront, Base64.DEFAULT));
                            jsonObject.addProperty("idBackBase64", Base64.encodeToString(idBack, Base64.DEFAULT));
                            jsonObject.addProperty("carDamageBase64", Base64.encodeToString(CarDamage, Base64.DEFAULT));
                            jsonObject.addProperty("compulsoryBase64", Base64.encodeToString(Compulsory, Base64.DEFAULT));
                            jsonObject.addProperty("carThirdPartyBase64", Base64.encodeToString(CarThirdParty, Base64.DEFAULT));
                            jsonObject.addProperty("userPhotoBase64", Base64.encodeToString(userPhoto, Base64.DEFAULT));

                            int count = 0;
                            try {
                                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                count = Integer.parseInt(result);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.showToast(activity, "註冊失敗");
                            } else {
                                Common.showToast(activity, "註冊成功，請重新登入");
                            }
                        } else {
                            Common.showToast(activity, "沒有連線");
                        }
//                        Navigation.findNavController(v).navigate(R.id.action_signUp_2_to_checkPhoneNumber);
                        Navigation.findNavController(v).navigate(R.id.action_signUp_2_to_login);
                    }

                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_PICK_PICTURE:
                    crop(intent.getData());
                    break;
                case REQ_CROP_PICTURE:
                    Log.d(TAG, "REQ_CROP_PICTURE: " + croppedImageUri.toString());
                    try {
                        // 顯示縮圖
                        Bitmap picture = BitmapFactory.decodeStream(
                                activity.getContentResolver().openInputStream(croppedImageUri));
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        picture.compress(Bitmap.CompressFormat.JPEG, 100, out);

                        if ( action == 1 ) {
                            ivIdFront.setImageBitmap(picture);
                            idFront = out.toByteArray();
                        } else if ( action == 2 ){
                            ivIdBack.setImageBitmap(picture);
                            idBack = out.toByteArray();
                        } else if ( action == 3 ){
                            ivCarDamage.setImageBitmap(picture);
                            CarDamage = out.toByteArray();
                        } else if ( action == 4 ){
                            ivCompulsory.setImageBitmap(picture);
                            Compulsory = out.toByteArray();
                        } else if ( action == 5 ){
                            ivCarThirdParty.setImageBitmap(picture);
                            CarThirdParty = out.toByteArray();
                        } else if ( action == 6 ){
                            ivUserPhoto.setImageBitmap(picture);
                            userPhoto = out.toByteArray();
                        } else {
                            Common.showToast(activity, R.string.textNoCameraApp);
                            return;
                        }
                        // 轉成可回傳資料型態
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
        Uri destinationUri = Uri.fromFile(file);
        croppedImageUri = Uri.fromFile(file);
        UCrop.of(sourceImageUri, destinationUri)
//                .withAspectRatio(16, 9) // 設定裁減比例
//                .withMaxResultSize(500, 500) // 設定結果尺寸不可超過指定寬高
                .start(activity, this, REQ_CROP_PICTURE);
    }

    private void askExternalStoragePermission() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        int result = ContextCompat.checkSelfPermission(activity, permissions[0]);
        if (result == PackageManager.PERMISSION_DENIED) {
            requestPermissions(permissions, PER_EXTERNAL_STORAGE);
        }
    }

    private void takePicture(String filename, File myFile){
        // 這行就是利用intent去開啟Android的照相機介面，再然後拍完照，即呼叫onActivityResult
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 取得外部儲存資源
        File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir != null && !dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, getString(R.string.textDirNotCreated));
                return;
            }
        }
        myFile = new File(dir, filename); // 要存檔的路徑
        contentUri = FileProvider.getUriForFile(
                activity, activity.getPackageName() + ".provider", myFile);
        // 新增一張照片，在開啟Android的照相機介面時，把這張照片指定為輸出檔案位置。
        // 將uri存入，MediaStore.EXTRA_OUTPUT是指定存储Uri的键，通過傳送一個EXTRA_OUTPUT的Extra給Intent，指定儲存圖像的路徑。
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // 呼叫 onActivityResult()
            startActivityForResult(intent, REQ_TAKE_PICTURE); // 拍照
        } else {
            Common.showToast(activity, R.string.textNoCameraApp);
        }
    }

    private void pickPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_PICK_PICTURE);
    }




}



