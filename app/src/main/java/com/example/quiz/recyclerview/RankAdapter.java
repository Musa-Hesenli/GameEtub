package com.example.quiz.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quiz.R;
import com.example.quiz.apiRank.Rank;
import com.example.quiz.authSharedPreference.AuthPreferences;
import com.example.quiz.utilities.Constants;

import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {
    private final Context context;
    private List<Rank> rankList;
    private AuthPreferences authPreferences;
    public RankAdapter(Context context) {
        this.context = context;
        authPreferences = new AuthPreferences(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rank_user_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RankAdapter.ViewHolder holder, int position) {
        holder.totalPoint.setText(String.valueOf(rankList.get(position).getTotal_point()));
        holder.username.setText(rankList.get(position).getUser());
        holder.rankQueue.setText(String.valueOf(position + 1));
        String image = rankList.get(position).getImage_url();
        if (image == null) holder.profileImage.setImageResource(R.mipmap.av1);
        else Glide.with(context).asBitmap().load(rankList.get(position).getImage_url()).into(holder.profileImage);
        if (authPreferences.getString(Constants.EMAIL, "").equals(rankList.get(position).getEmail())) {
            holder.container.setBackgroundColor(Color.rgb(160, 216, 169));
        }
    }

    @Override
    public int getItemCount() {
        return rankList.size();
    }

    public void setRankList(List<Rank> rankList) {
        this.rankList = rankList;
        notifyDataSetChanged();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView username;
        private final TextView totalPoint;
        private final TextView rankQueue;
        private final ImageView profileImage;
        private final ConstraintLayout container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameRank);
            totalPoint = itemView.findViewById(R.id.totalPointText);
            rankQueue = itemView.findViewById(R.id.rankText);
            profileImage = itemView.findViewById(R.id.profileImage);
            container = itemView.findViewById(R.id.rankContainer);
        }
    }
}
