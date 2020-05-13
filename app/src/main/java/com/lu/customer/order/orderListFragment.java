package com.lu.customer.order;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.lu.customer.Common;
import com.lu.customer.CommonTask;
import com.lu.customer.Order;
import com.lu.customer.R;

import java.lang.reflect.Type;
import java.util.List;

public class orderListFragment extends Fragment {
    private String TAG = "TAG_OrderListFragment";
    private Activity activity;
    private RecyclerView rvOrderHistory;
    private CommonTask orderGetAllTask;
    private List<Order> orders;
    int customer_id = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.textOrder);
        return inflater.inflate(R.layout.fragment_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orders = getOrders(customer_id);
        System.out.println("orders: " + orders);
        rvOrderHistory = view.findViewById(R.id.rvOrderHistory);
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(activity));
        showOrders(orders);


    }

    private List<Order> getOrders(int customer_id) {
        List<Order> orders = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "/CustomerServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getOrders");
            jsonObject.addProperty("customer_id", customer_id);
            String jsonOut = jsonObject.toString();
            System.out.println("orders String jsonOut: " + jsonOut);
            orderGetAllTask = new CommonTask(url, jsonOut);
            try{
                String jsonIn = orderGetAllTask.execute().get();
                System.out.println("getOrder input orders: " + jsonIn);
                Type listType = new TypeToken<List<Order>>() {
                }.getType(); // ???????????
                orders = new Gson().fromJson(jsonIn, listType);
            }catch (Exception e){
                Log.e (TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return orders;
    }

    private void showOrders(List<Order> orders) {
        if (orders == null || orders.isEmpty()){
            Common.showToast(activity, R.string.textNoOrdersFound);
        }
        // 準備一個recyclerView的類別 並且抓他的內容 準備連server
        orderListFragment.OrderHistoryAdapter orderAdapter = (orderListFragment.OrderHistoryAdapter) rvOrderHistory.getAdapter();
        if (orderAdapter == null) {
            // 如果沒有內容，則new出來執行rv顯示的指令
            rvOrderHistory.setAdapter(new orderListFragment.OrderHistoryAdapter(activity, orders));
        } else {
            // 如果已有內容，則延續壽命this.books = books;
            orderAdapter.setOrders(orders);
            // 叫adapter去重新刷新畫面 getView()
            orderAdapter.notifyDataSetChanged();
        }
    }

    private class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Order> orders;

        public OrderHistoryAdapter(Context context, List<Order> orders) {
            layoutInflater = LayoutInflater.from(context);
            this.orders = orders;
        }

        void setOrders(List<Order> orders){
            this.orders = orders;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvOrderDate, tvOrderMoney, tvOderRate, tvStartAddress, tvEndAddress;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
                tvOrderMoney = itemView.findViewById(R.id.tvOrderMoney);
                tvOderRate = itemView.findViewById(R.id.tvOderRate);
                tvStartAddress = itemView.findViewById(R.id.tvStartAddress);
                tvEndAddress = itemView.findViewById(R.id.tvEndAddress);
            }
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        @NonNull
        @Override
        public orderListFragment.OrderHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_order, parent, false);
            return new orderListFragment.OrderHistoryAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull orderListFragment.OrderHistoryAdapter.MyViewHolder myViewHolder, int position) {
            final Order order = orders.get(position);
            myViewHolder.tvOrderDate.setText(order.getOrder_time());
            myViewHolder.tvOrderMoney.setText(String.valueOf(order.getOrder_money()));
            myViewHolder.tvOderRate.setText(String.valueOf(order.getDriver_score()));
            myViewHolder.tvStartAddress.setText(order.getOrder_start());
            myViewHolder.tvEndAddress.setText(order.getOrder_end());
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("order", order);
                    Navigation.findNavController(view).navigate(R.id.action_orderListFragment_to_orderDetailFragment, bundle);
                }
            });
        }
    }
}
