package com.lu.customer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class OpinionDetailFragment extends Fragment {
    private final static String TAG = "OpinionDetailFragment";
    private FragmentActivity activity;
    private Opinion opinion;
    private TextView tvQuestion,tvAnswer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opinion_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvQuestion = view.findViewById(R.id.tvQuestion);
        tvAnswer = view.findViewById(R.id.tvAnswer);

        final NavController navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("opinion") == null) {
            Common.showToast(activity, R.string.textNoOpinionsFound);
            navController.popBackStack();
            return;
        }
        opinion = (Opinion) bundle.getSerializable("opinion");
        String question = "";
        String answer = "";
        if (opinion!= null) {
            question = "問題：\n" + opinion.getCustomer_opinion_question();
            answer = "回答：\n" + opinion.getCustomer_opinion_answer();
            if(opinion.getCustomer_opinion_answer()==null){
                answer = "回答：\n請等待客服人員回覆";
            }
        }
        tvQuestion.setText(question);
        tvAnswer.setText(answer);
    }
}