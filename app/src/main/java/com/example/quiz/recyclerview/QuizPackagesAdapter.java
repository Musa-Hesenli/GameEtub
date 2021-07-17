package com.example.quiz.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quiz.R;
import com.example.quiz.activities.ShowPackages;
import com.example.quiz.api.Package;

import java.util.ArrayList;
import java.util.List;

public class QuizPackagesAdapter extends RecyclerView.Adapter<QuizPackagesAdapter.ViewHolder> {
    private final Context context;
    List<Package> packages = new ArrayList<>();
    private static final String base_url = "https://musahesenli-quiz.herokuapp.com";
    public QuizPackagesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_package_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.playedBy.setText(String.valueOf(packages.get(position).getPlayed()));
        holder.creatorName.setText(packages.get(position).getCreator().getUsername());
        holder.categoryName.setText(packages.get(position).getCategory().getName());
        holder.quizTitle.setText(packages.get(position).getName());
        holder.quizDescription.setText(packages.get(position).getDescription());
        String image_url = packages.get(position).getImage();
        if(image_url != null) Glide.with(context).asBitmap().load(base_url + image_url).into(holder.backgroundImage);
        holder.container.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowPackages.class);
            intent.putExtra("package_id", String.valueOf(packages.get(position).getId()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView backgroundImage;
        private final TextView categoryName, creatorName, playedBy, quizTitle, quizDescription;
        private final ConstraintLayout container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            backgroundImage = itemView.findViewById(R.id.categoryBackgroundImage);
            categoryName = itemView.findViewById(R.id.categoryNameTag);
            creatorName = itemView.findViewById(R.id.quizCreator);
            playedBy = itemView.findViewById(R.id.playedByText);
            quizTitle = itemView.findViewById(R.id.quizTitle);
            quizDescription = itemView.findViewById(R.id.packageDescription);
            container = itemView.findViewById(R.id.packageContainer);
        }
    }


}
