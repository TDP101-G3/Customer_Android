package com.lu.customer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static androidx.navigation.Navigation.findNavController;

public class OpinionRespondFragment extends Fragment {
    private Activity activity;
    private static final String TAG = "OpinionRespondFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvOpinion;
    private List<Opinion> opinions;
    private int customer_id;
    private CommonTask opinionGetAllTask;
    private NavController navController;

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
        return inflater.inflate(R.layout.fragment_opinion_respond, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = findNavController(view);
        SharedPreferences pref = activity.getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        customer_id = pref.getInt("customer_id", 0);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvOpinion = view.findViewById(R.id.rvOpinion);
        rvOpinion.setLayoutManager(new LinearLayoutManager(activity));
        opinions = getOpinions();
        showOpinions(opinions);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                opinions = getOpinions();
                showOpinions(opinions);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private List<Opinion> getOpinions() {
        List<Opinion> opinions = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "OpinionServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "CustomerfindById");
            jsonObject.addProperty("customer_id", customer_id);
            String jsonOut = jsonObject.toString();
            opinionGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = opinionGetAllTask.execute().get();
                Type listType = new TypeToken<List<Opinion>>() {
                }.getType();
                opinions = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return opinions;
    }

    private void showOpinions(List<Opinion> opinions) {
        if (opinions == null || opinions.isEmpty()) {
            Common.showToast(activity, R.string.textNoOpinionsFound);
        }
        OpinionAdapter opinionAdapter = (OpinionAdapter) rvOpinion.getAdapter();
        if (opinionAdapter == null) {
            rvOpinion.setAdapter(new OpinionAdapter(activity, opinions));
        } else {
            opinionAdapter.setOpinions(opinions);
            opinionAdapter.notifyDataSetChanged();
        }
    }

    private class OpinionAdapter extends RecyclerView.Adapter<OpinionAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Opinion> opinions;

        OpinionAdapter(Context context, List<Opinion> opinions) {
            layoutInflater = LayoutInflater.from(context);
            this.opinions = opinions;
        }

        void setOpinions(List<Opinion> opinions) {
            this.opinions = opinions;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvOpinion;

            MyViewHolder(View itemView) {
                super(itemView);
                tvOpinion = itemView.findViewById(R.id.tvOpinion);
            }
        }

        @Override
        public int getItemCount() {
            return opinions == null ? 0 : opinions.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_opinion, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            final Opinion opinion = opinions.get(position);
            myViewHolder.tvOpinion.setText(opinion.getCustomer_opinion_question());
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("opinion", opinion);
                    navController.navigate(R.id.opinionDetailFragment,bundle);
                }
            });
        }

    }
}