package com.lu.customer.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.lu.customer.Common;
import com.lu.customer.CommonTask;
import com.lu.customer.Customer;
import com.lu.customer.R;

import java.lang.reflect.Type;
import java.util.List;


public class CarInsuranceFragment extends Fragment {
    private static final String TAG = "TAG_CarAssuranceFragment";
    private FragmentActivity activity;
    private RecyclerView rvInsurance;
    private TextView tvCarNumber, tvCarModel, tvCarColor;
    private CommonTask insuranceGetAllTask, carFindByIdTask;
    private List<Insurance> insurances;
    private Customer customer;
    private ConstraintLayout layoutExpire;
    int customer_id = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.textCarAssurance);
        return inflater.inflate(R.layout.fragment_car_insurance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvCarNumber = view.findViewById(R.id.tvCarNumber);
        tvCarModel = view.findViewById(R.id.tvCarModel);
        tvCarColor = view.findViewById(R.id.tvCarColor);

        customer = findById(customer_id);
        setCar(customer);
        insurances = getInsurances(customer_id);
        System.out.println("insurances: " + insurances);
        rvInsurance = view.findViewById(R.id.rvInsurance);
        rvInsurance.setLayoutManager(new LinearLayoutManager(activity));

        showInsurance(insurances);

        Button btCarEdit = view.findViewById(R.id.btCarEdit);
        btCarEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("customer", customer);
                Navigation.findNavController(view).navigate(R.id.action_carInsuranceFragment_to_carEditFragment, bundle);
            }
        });
    }

    @SuppressLint("LongLogTag")   //??????
    private Customer findById(int customer_id) {
        Customer customer = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/CustomerServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");
            jsonObject.addProperty("customer_id", customer_id);
            String jsonOut = jsonObject.toString();
            carFindByIdTask = new CommonTask(url, jsonOut);
            try{
                String jsonIn = carFindByIdTask.execute().get();
                JsonObject jsonResult = new Gson().fromJson(jsonIn, JsonObject.class);

                int customerId = jsonResult.get("customer_id").getAsInt();
                String customerName = jsonResult.get("customer_name").getAsString();
                String customerPhone = jsonResult.get("customer_phone").getAsString();
                String customerEmail = jsonResult.get("customer_email").getAsString();
                String carNumberPlate = jsonResult.get("customer_number_plate").getAsString();
                String carModel = jsonResult.get("customer_car_model").getAsString();
                String carColor = jsonResult.get("customer_car_color").getAsString();
                customer = new Customer(customerId, customerName, customerPhone, customerEmail, carNumberPlate, carModel, carColor);

            }catch (Exception e){
                Log.e (TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        System.out.println("findById() return customer = " + customer);
        return customer;
    }
    private void setCar(Customer customer){
        tvCarNumber.setText(String.valueOf(customer.getCustomer_number_plate()));
        tvCarModel.setText(String.valueOf(customer.getCustomer_car_model()));
        tvCarColor.setText(String.valueOf(customer.getCustomer_car_color()));
    }

    @SuppressLint("LongLogTag")   //??????
    private List<Insurance> getInsurances(int customer_id) {
        List<Insurance> insurances = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/CustomerServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getInsurances");
            jsonObject.addProperty("customer_id", customer_id);
            String jsonOut = jsonObject.toString();
            System.out.println("insurances String jsonOut: " + jsonOut);
            insuranceGetAllTask = new CommonTask(url, jsonOut);
            try{
                String jsonIn = insuranceGetAllTask.execute().get();
                System.out.println("getInsurance input insurances: " + jsonIn);
                Type listType = new TypeToken<List<Insurance>>() {
                }.getType(); // ???????????
                insurances = new Gson().fromJson(jsonIn, listType);
            }catch (Exception e){
                Log.e (TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return insurances;
    }

    private void showInsurance(List<Insurance> insurances) {
        // books 在app開啟後會先呼叫方法books=getBook();
        if (insurances == null || insurances.isEmpty()){
            Common.showToast(activity, R.string.textNoAssurancesFound);
        }
        // 準備一個recyclerView的類別 並且抓他的內容 準備連server
        InsuranceAdapter insuranceAdapter = (InsuranceAdapter) rvInsurance.getAdapter();
        if (insuranceAdapter == null) {
            // 如果沒有內容，則new出來執行rv顯示的指令
            rvInsurance.setAdapter(new InsuranceAdapter(activity, insurances));
        } else {
            // 如果已有內容，則延續壽命this.books = books;
            insuranceAdapter.setInsurances(insurances);
            // 叫adapter去重新刷新畫面 getView()
            insuranceAdapter.notifyDataSetChanged();
        }
    }

    private class InsuranceAdapter extends RecyclerView.Adapter<InsuranceAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Insurance> insurances;
        private int imageSize;

        public InsuranceAdapter(Context context, List<Insurance> insurances) {
            layoutInflater = LayoutInflater.from(context);
            this.insurances = insurances;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }
        // 呼叫時機   1.showBooks()
        void setInsurances(List<Insurance> insurances){
            this.insurances = insurances;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivChecked, ivSituation, ivInsuranceEdit;
            TextView tvInsuranceName, tvInsuranceDate, tvInsuranceResult;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ivChecked = itemView.findViewById(R.id.ivChecked);
                ivSituation = itemView.findViewById(R.id.ivSituation);
                ivInsuranceEdit = itemView.findViewById(R.id.ivInsuranceEdit);
                tvInsuranceName = itemView.findViewById(R.id.tvInsuranceTitle);
                tvInsuranceDate = itemView.findViewById(R.id.tvInsuranceDate);
                tvInsuranceResult = itemView.findViewById(R.id.tvInsuranceResult);
            }
        }

        @Override
        public int getItemCount() {
            return insurances.size();
        }

        @NonNull
        @Override
        public InsuranceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_insurance, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull InsuranceAdapter.MyViewHolder myViewHolder, int position) {
            final Insurance insurance = insurances.get(position);
            // 保險名稱
            String insuranceName = String.valueOf(insurance.getInsuranceName());
            if(insuranceName.equals("carDamage")){
                myViewHolder.tvInsuranceName.setText(R.string.textCarDamage);
            } else if (insuranceName.equals("compulsory")){
                myViewHolder.tvInsuranceName.setText(R.string.textCompulsory);
            } else if (insuranceName.equals("third")) {
                myViewHolder.tvInsuranceName.setText(R.string.textCarThirdParty);
            }
            // 到期日
            myViewHolder.tvInsuranceDate.setText(insurance.getExpireDate());
            // 認證狀態
            String situation = insurance.getSituation();
            if (situation.isEmpty() || situation.equals("unfinished")){
                myViewHolder.tvInsuranceResult.setText(R.string.textAssuranceIsEmpty);
                myViewHolder.ivChecked.setImageResource(R.drawable.notchecked);
                myViewHolder.ivSituation.setImageResource(R.drawable.situation0);
            } else if(situation.equals("success")) {
                myViewHolder.tvInsuranceResult.setText(R.string.textAssuranceIsSuccessful);
                myViewHolder.ivChecked.setImageResource(R.drawable.checked);
                myViewHolder.ivSituation.setImageResource(R.drawable.situation1);
            } else if(situation.equals("processing")){
                myViewHolder.tvInsuranceResult.setText(R.string.textAssuranceIsProcessing);
                myViewHolder.ivChecked.setImageResource(R.drawable.notchecked);
                myViewHolder.ivSituation.setImageResource(R.drawable.situation0);
            } else if(situation.equals("expired")){
                myViewHolder.tvInsuranceResult.setText(R.string.textAssuranceExpired);
                myViewHolder.ivChecked.setImageResource(R.drawable.notchecked);
                myViewHolder.ivSituation.setImageResource(R.drawable.situation0);
            } else if(situation.equals("failed")){
                myViewHolder.tvInsuranceResult.setText(R.string.textAssuranceFail);
                myViewHolder.ivChecked.setImageResource(R.drawable.notchecked);
                myViewHolder.ivSituation.setImageResource(R.drawable.situation0);
            } else {
                myViewHolder.tvInsuranceResult.setText(R.string.textAssuranceFail);
                myViewHolder.ivChecked.setImageResource(R.drawable.notchecked);
                myViewHolder.ivSituation.setImageResource(R.drawable.situation0);
            }
            // 編輯保險資料按鈕
            myViewHolder.ivInsuranceEdit.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("insurance", insurance);
                    bundle.putSerializable("customer_id", customer_id);
                    Navigation.findNavController(view).navigate(R.id.action_carInsuranceFragment_to_insuranceEditFragment, bundle);
                }
            });
        }
    }



}


