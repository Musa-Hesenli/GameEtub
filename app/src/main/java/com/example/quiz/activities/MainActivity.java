package com.example.quiz.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.quiz.R;
import com.example.quiz.authSharedPreference.AuthPreferences;
import com.example.quiz.fragments.CategoryFragment;
import com.example.quiz.fragments.Index;
import com.example.quiz.fragments.Profile;
import com.example.quiz.fragments.Rank;
import com.example.quiz.fragments.Settings;
import com.example.quiz.utilities.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    AuthPreferences authPreferences;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if user is signed in if not switch to login screen
        authPreferences = new AuthPreferences(this);
        if (!authPreferences.getBoolean(Constants.IS_AUTHENTICATED)) {
            intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        }
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        int category_id = getIntent().getIntExtra("category_id", -1);
        getSupportFragmentManager().beginTransaction().replace(R.id.appContentFragment, new Index(category_id)).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(this::selectBottomMenuItem);

    }


    @SuppressLint("NonConstantResourceId")
    public boolean selectBottomMenuItem(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.discover:
                replaceFragment(new Index(-1));
                break;
            case R.id.categories:
                replaceFragment(new CategoryFragment());
                break;
            case R.id.rank:
                replaceFragment(new Rank());
                break;
            case R.id.results:
                replaceFragment(new Profile());
                break;
            case R.id.settings:
                replaceFragment(new Settings());
                break;
        }
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.appContentFragment, fragment).commit();
    }
}