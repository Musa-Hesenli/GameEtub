package com.example.quiz.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quiz.R;
import com.example.quiz.api.Package;
import com.example.quiz.api.Packages;
import com.example.quiz.recyclerview.QuizPackagesAdapter;
import com.example.quiz.retrofit.ApiClient;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Index#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Index extends Fragment {
    List<Package> packages;
    QuizPackagesAdapter adapter;
    RecyclerView recyclerView;
    private Context context;
    private final String category_id;
    private TextView resultText;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private LinearProgressIndicator linearProgressIndicator;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Index(int category_id) {
        // Required empty public constructor
        this.category_id = String.valueOf(category_id);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment index.
     */
    // TODO: Rename and change types and number of parameters
    public static Index newInstance(String param1, String param2, int id) {
        Index fragment = new Index(id);
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
        View view =  inflater.inflate(R.layout.fragment_index, container, false);
        context = view.getContext();
        resultText = view.findViewById(R.id.resultText);
        adapter = new QuizPackagesAdapter(context);
        recyclerView = view.findViewById(R.id.packagesRecyclerView);
        linearProgressIndicator = view.findViewById(R.id.progress_horizontal);
        fetchPackagesData();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }

    protected void fetchPackagesData() {
        if (!category_id.equals("-1")) {
            Call<Packages> call = ApiClient.getInstance().getApi().getRelatedPackages(category_id);
            call.enqueue(new Callback<Packages>() {
                @Override
                public void onResponse(Call<Packages> call, Response<Packages> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        if (response.body().getPackages() != null) {
                            Log.i("SELECTED_QUIZ", response.body().getPackages().toString());
                            packages = response.body().getPackages();
                            adapter.setPackages(packages);
                            recyclerView.setAdapter(adapter);
                            resultText.setVisibility(View.VISIBLE);
                            resultText.setText(response.body().getPackages().size() + " Results found.");
                        } else {
                            resultText.setVisibility(View.VISIBLE);
                            resultText.setText("No result found");
                        }
                        linearProgressIndicator.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<Packages> call, Throwable t) {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    linearProgressIndicator.setVisibility(View.GONE);
                }
            });
            return;
        };
        Call<Packages> call = ApiClient.getInstance().getApi().getPackages();
        call.enqueue(new Callback<Packages>() {
            @Override
            public void onResponse(Call<Packages> call, Response<Packages> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getPackages() != null) {
                        Log.i("SELECTED_QUIZ", response.body().getPackages().toString());
                        packages = response.body().getPackages();
                        adapter.setPackages(packages);
                        recyclerView.setAdapter(adapter);
                    }
                    linearProgressIndicator.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Packages> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                linearProgressIndicator.setVisibility(View.GONE);
            }
        });
    }
}