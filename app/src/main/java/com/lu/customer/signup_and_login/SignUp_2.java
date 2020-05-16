package com.lu.customer.signup_and_login;

import android.app.Activity;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private ImageView ivIdFront, ivIdBack, ivCarDamage, ivCompulsory, ivCarThirdParty;
    private File file_ivIdFront, file_ivIdBack, file_ivCarDamage, file_ivCompulsory, file_ivCarThirdParty;
    private EditText etCarNumber, etCarModel, etCarColor;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private Uri contentUri;
    List<Bitmap> bitmaps;
    private int count = 0;
    private byte[] idFront, idBack, CarDamage, Compulsory, CarThirdParty;
    private TextView tvResult;

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
        tvResult = view.findViewById(R.id.tvResult);
        ivIdFront = view.findViewById(R.id.ivIdFront);
        ivIdBack = view.findViewById(R.id.ivIdBack);
        ivCarDamage = view.findViewById(R.id.ivCarDamage);
        ivCompulsory = view.findViewById(R.id.ivCompulsory);
        ivCarThirdParty = view.findViewById(R.id.ivCarThirdParty);
        etCarNumber = view.findViewById(R.id.etCarNumber);
        etCarModel = view.findViewById(R.id.etCarModel);
        etCarColor = view.findViewById(R.id.etCarColor);
        bitmaps = new ArrayList<>();


        Bundle sign2 = getArguments();
        if (sign2 != null) {
            final String customer_name = sign2.getString("name");
            final String customer_password = sign2.getString("password");
            final String customer_email = sign2.getString("email");
            final String customer_phone = sign2.getString("phoneNumber");
//            final String customer_creditcard_type = sign2.getString("creditCardType");
//            final String customer_creditcard_number = sign2.getString("creditCardNumber");
//            final String customer_creditcard_date = sign2.getString("creditCardDate");
//            final String customer_creditcard_secure_code = sign2.getString("creditCardSecureCode");
//            String text1 = "User name: " + customer_name + "; password: " + customer_password + "\n" ;
//            tvResult.append(text1);


            ivIdFront.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    file_ivIdFront = new File(dir, "ivIdFront.jpg");
                    contentUri = FileProvider.getUriForFile(
                            activity, activity.getPackageName() + ".provider", file_ivIdFront);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                        startActivityForResult(intent, REQ_TAKE_PICTURE);
                    } else {
                        Common.showToast(activity, "啟動相機失敗");
                    }
                }
            });
            ivIdBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    file_ivIdBack = new File(dir, "ivIdBack.jpg");
                    contentUri = FileProvider.getUriForFile(
                            activity, activity.getPackageName() + ".provider", file_ivIdBack);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                        startActivityForResult(intent, REQ_TAKE_PICTURE);
                    } else {
                        Common.showToast(activity, "啟動相機失敗");
                    }
                }
            });
            ivCarDamage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    file_ivCarDamage = new File(dir, "ivCarDamage.jpg");
                    contentUri = FileProvider.getUriForFile(
                            activity, activity.getPackageName() + ".provider", file_ivCarDamage);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                        startActivityForResult(intent, REQ_TAKE_PICTURE);
                    } else {
                        Common.showToast(activity, "啟動相機失敗");
                    }
                }
            });
            ivCompulsory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    file_ivCompulsory = new File(dir, "ivCompulsory.jpg");
                    contentUri = FileProvider.getUriForFile(
                            activity, activity.getPackageName() + ".provider", file_ivCompulsory);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                        startActivityForResult(intent, REQ_TAKE_PICTURE);
                    } else {
                        Common.showToast(activity, "啟動相機失敗");
                    }
                }
            });
            ivCarThirdParty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    file_ivCarThirdParty = new File(dir, "ivCarThirdParty.jpg");
                    contentUri = FileProvider.getUriForFile(
                            activity, activity.getPackageName() + ".provider", file_ivCarThirdParty);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                        startActivityForResult(intent, REQ_TAKE_PICTURE);
                    } else {
                        Common.showToast(activity, "啟動相機失敗");
                    }
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
                    if (bitmaps.size() != 5) {//如果沒拍照，就不能上傳
                        Common.showToast(activity, "驗證資料不完整就不能繼續註冊哦！");
                        return;
                    } else {
                        if (Common.networkConnected(activity)) {
                            String url = Common.URL + "CustomerServlet";//連伺服器
                            Customer customer = new Customer(customer_name, customer_email, customer_password, customer_phone, customer_number_plate, customer_car_model, customer_car_color);
//                            Creditcard creditcard = new Creditcard(customer_creditcard_type, customer_creditcard_number, customer_creditcard_date, customer_creditcard_secure_code);
                            idFront = Common.bitmapToPNG(bitmaps.get(0));
                            idBack = Common.bitmapToPNG(bitmaps.get(1));
                            CarDamage = Common.bitmapToPNG(bitmaps.get(2));
                            Compulsory = Common.bitmapToPNG(bitmaps.get(3));
                            CarThirdParty = Common.bitmapToPNG(bitmaps.get(4));
                            JsonObject jsonObject = new JsonObject();   //建一個物件
                            jsonObject.addProperty("action", "signUp");
                            jsonObject.addProperty("customer", new Gson().toJson(customer));
//                            jsonObject.addProperty("creditcard", new Gson().toJson(creditcard));

                            jsonObject.addProperty("imageBase64", Base64.encodeToString(idFront, Base64.DEFAULT));
                            jsonObject.addProperty("idBackBase64", Base64.encodeToString(idBack, Base64.DEFAULT));
                            jsonObject.addProperty("carDamageBase64", Base64.encodeToString(CarDamage, Base64.DEFAULT));
                            jsonObject.addProperty("compulsoryBase64", Base64.encodeToString(Compulsory, Base64.DEFAULT));
                            jsonObject.addProperty("carThirdPartyBase64", Base64.encodeToString(CarThirdParty, Base64.DEFAULT));

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
                    handleCropResult(intent);
                    break;
            }

        }
    }


    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri destinationUri = Uri.fromFile(file);
        UCrop.of(sourceImageUri, destinationUri)
//                .withAspectRatio(16, 9) // 設定裁減比例
//                .withMaxResultSize(500, 500) // 設定結果尺寸不可超過指定寬高
                .start(activity, this, REQ_CROP_PICTURE);
    }

    private void handleCropResult(Intent intent) {

        Uri resultUri = UCrop.getOutput(intent);
        if (resultUri == null) {
            return;
        }
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                bitmap = BitmapFactory.decodeStream(
                        activity.getContentResolver().openInputStream(resultUri));
                bitmaps.add(bitmap);
                count++;

            } else {
                ImageDecoder.Source source =
                        ImageDecoder.createSource(activity.getContentResolver(), resultUri);
                bitmap = ImageDecoder.decodeBitmap(source);
                bitmaps.add(bitmap);
                count++;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        if (bitmap != null) {
            switch (count) {
                case 1:
//                    Common.showToast(activity, "22222222222");
                    ivIdFront.setImageBitmap(bitmaps.get(0));
                    break;
                case 2:
                    ivIdBack.setImageBitmap(bitmaps.get(1));
                    break;
                case 3:
                    ivCarDamage.setImageBitmap(bitmaps.get(2));
                    break;
                case 4:
                    ivCompulsory.setImageBitmap(bitmaps.get(3));
                    break;
                case 5:
                    ivCarThirdParty.setImageBitmap(bitmaps.get(4));
                    break;
            }
        }
    }
}



