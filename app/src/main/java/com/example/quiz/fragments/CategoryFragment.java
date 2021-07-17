package com.example.quiz.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.quiz.R;
import com.example.quiz.activities.MainActivity;
import com.example.quiz.api.Package;
import com.example.quiz.api.Packages;
import com.example.quiz.apiCategories.Categories;
import com.example.quiz.apiCategories.Category;
import com.example.quiz.recyclerview.CategoriesAdapter;
import com.example.quiz.retrofit.ApiClient;
import com.example.quiz.utilities.Constants;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment implements CategoriesAdapter.OnCategoryListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private List<Category> categories;
    private CategoriesAdapter adapter;
    private LinearProgressIndicator linearProgressIndicator;
    private View divider;
    private Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Categories.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        context = view.getContext();
        recyclerView = view.findViewById(R.id.categoriesRecyclerView);
        divider = view.findViewById(R.id.divider);
        adapter = new CategoriesAdapter(view.getContext(), this);
        linearProgressIndicator = view.findViewById(R.id.progress_horizontal);
        fetchCategories();
        return view;
    }

    private void fetchCategories() {
        Call<Categories> call  = ApiClient.getInstance().getApi().getAllCategories();
        call.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                if (response.isSuccessful() && response.body().getCategories() != null) {
                    Log.i(Constants.TAG, response.body().getCategories().toString());
                    categories = response.body().getCategories();
                    adapter.setCategoryList(categories);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                    adapter.notifyDataSetChanged();
                } else {
                    Log.i(Constants.TAG, response.body().toString());
                }
                linearProgressIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {
                Log.e(Constants.TAG, t.getLocalizedMessage());
                linearProgressIndicator.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCategoryClick(int position) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("category_id", categories.get(position).getId());
        startActivity(intent);
    }
}