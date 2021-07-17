package com.example.quiz.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quiz.R;
import com.example.quiz.resultsRoomDatabase.ResultEntity;

class ResultViewHolder extends RecyclerView.ViewHolder {
    private final TextView quizName, rightAnswer, wrongAnswer, totalPoint;
    private ResultViewHolder(View itemView) {
        super(itemView);
        quizName = itemView.findViewById(R.id.quiz_result_name);
        rightAnswer = itemView.findViewById(R.id.rightAnswerCount);
        wrongAnswer = itemView.findViewById(R.id.wrongAnswerCount);
        totalPoint = itemView.findViewById(R.id.totalPointText);
    }
    public void bind(ResultEntity entity) {
        quizName.setText(entity.getQuiz_name());
        rightAnswer.setText(entity.getRight_answers());
        wrongAnswer.setText(entity.getWrong_answers());
        totalPoint.setText(entity.getTotal_point());
    }

    static ResultViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_result_item, parent, false);
        return new ResultViewHolder(view);
    }
}
public class ResultsAdapter extends ListAdapter<ResultEntity, ResultViewHolder> {
    private final Context context;
    public ResultsAdapter(@NonNull DiffUtil.ItemCallback<ResultEntity> diffCallback, Context context) {
        super(diffCallback);
        this.context = context;
    }
    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ResultViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        ResultEntity entity = getItem(position);
        holder.bind(entity);
    }

    public static class WordDiff extends DiffUtil.ItemCallback<ResultEntity> {
        @Override
        public boolean areItemsTheSame(@NonNull ResultEntity oldItem, @NonNull ResultEntity newItem) {
            return oldItem.getQuiz_name().toLowerCase().trim().equals(newItem.getQuiz_name().toLowerCase().trim());
        }
        @Override
        public boolean areContentsTheSame(@NonNull ResultEntity oldItem, @NonNull ResultEntity newItem) {
            return oldItem.getQuiz_name().toLowerCase().trim().equals(newItem.getQuiz_name().toLowerCase().trim());
        }
    }
}
