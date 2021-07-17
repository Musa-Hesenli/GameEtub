package com.example.quiz.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quiz.R;
import com.example.quiz.recyclerview.RankAdapter;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Rank#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Rank extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private RankAdapter adapter;
    private LinearProgressIndicator linearProgressIndicator;
    private List<com.example.quiz.apiRank.Rank> rankList = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Rank() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Rank.
     */
    // TODO: Rename and change types and number of parameters
    public static Rank newInstance(String param1, String param2) {
        Rank fragment = new Rank();
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
        View view = inflater.inflate(R.layout.fragment_rank, container, false);
        recyclerView = view.findViewById(R.id.rankRecyclerView);
        adapter = new RankAdapter(view.getContext());
        linearProgressIndicator = view.findViewById(R.id.progress_horizontal);
        fetchRankData();
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }

    protected void fetchRankData() {
        linearProgressIndicator.setVisibility(View.VISIBLE);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        rankList.clear();
        firebaseFirestore.collection("users").orderBy("total_point", Query.Direction.DESCENDING).get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            com.example.quiz.apiRank.Rank rank = new com.example.quiz.apiRank.Rank();
                            rank.setId(documentSnapshot.getId());
                            rank.setImage_url(documentSnapshot.getString("image_url"));
                            rank.setTotal_point(documentSnapshot.getString("total_point"));
                            rank.setUser(documentSnapshot.getString("username"));
                            rank.setEmail(documentSnapshot.getString("email"));
                            rankList.add(rank);
                        }
                        adapter.setRankList(rankList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                    linearProgressIndicator.setVisibility(View.GONE);
                });
        }

}