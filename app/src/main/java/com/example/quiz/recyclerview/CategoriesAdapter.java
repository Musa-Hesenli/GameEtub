package com.example.quiz.recyclerview;

import android.content.Context;
import android.util.Log;
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
import com.example.quiz.api.Packages;
import com.example.quiz.apiCategories.Category;
import com.example.quiz.retrofit.ApiClient;
import com.example.quiz.utilities.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder>{
    List<Category> categoryList;
    private final Context context;
    private final OnCategoryListener onCategoryListener;

    public CategoriesAdapter(Context context, OnCategoryListener onCategoryListener) {
        this.context = context;
        this.onCategoryListener = onCategoryListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view, onCategoryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.ViewHolder holder, int position) {
        holder.categoryName.setText(categoryList.get(position).getName());
        Glide.with(context).asBitmap().load(Constants.BASE_URL + categoryList.get(position).getImage()).into(holder.categoryImage);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView categoryName;
        private final ImageView categoryImage;
        private final ConstraintLayout categoryCardContainer;
        OnCategoryListener onCategoryListener;
        public ViewHolder(@NonNull View itemView, OnCategoryListener onCategoryListener) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryNameTag);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryCardContainer = itemView.findViewById(R.id.categoryCardContainer);
            this.onCategoryListener = onCategoryListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCategoryListener.onCategoryClick(getAdapterPosition());
        }
    }

    public interface OnCategoryListener {
        void onCategoryClick(int position);
    }
}
