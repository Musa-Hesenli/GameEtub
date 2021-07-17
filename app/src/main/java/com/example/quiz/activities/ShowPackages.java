package com.example.quiz.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.quiz.R;
import com.example.quiz.apiSelectedPackage.Packages;
import com.example.quiz.apiSelectedPackage.Question;
import com.example.quiz.fragments.QuizInterface;
import com.example.quiz.retrofit.ApiClient;
import com.example.quiz.utilities.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class ShowPackages extends AppCompatActivity {
    protected static String selectedPackageID;
    private Context context;
    RelativeLayout loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_packages);
        context = this;
        loader = findViewById(R.id.loaderAnimation);
        Constants.is_quiz_finished = false;
        Intent intent = getIntent();
        selectedPackageID = intent.getStringExtra("package_id");

        try {
            getSelectedPackage();
        } catch (Exception e) {
            Log.i("SELECTED_QUIZ", e.getMessage());
        }
    }

    protected void getSelectedPackage() {
        Call<Packages> packagesCall = ApiClient.getInstance().getApi().getSelectedPackage(selectedPackageID);
        packagesCall.enqueue(new Callback<Packages>() {
            @Override
            public void onResponse(Call<Packages> call, Response<Packages> response) {
                if (response.isSuccessful() && response.body().getQuestions() != null) {
                    Packages questions = response.body();
                    startFragmentTransaction(questions);
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                    finish();
                }
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Packages> call, Throwable t) {
                Log.i("SELECTED_QUIZ", t.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
                loader.setVisibility(View.GONE);
            }
        });
    }

    protected void startFragmentTransaction(Packages questionList) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new QuizInterface(questionList, 0, questionList.getQuestions().size())).commit();
    }

    @Override
    public void onBackPressed() {
        if (!Constants.is_quiz_finished) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Warning!");
            builder.setMessage("If you leave quiz you will lose all the progress which you have made. Do you want to continue?");
            builder.setPositiveButton("Yes", (dialog, which) -> finish());
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            finish();
        }
    }
}