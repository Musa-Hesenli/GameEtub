package com.example.quiz.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.example.quiz.R;
import com.example.quiz.activities.AboutPage;
import com.example.quiz.activities.EditProfile;
import com.example.quiz.activities.Login;
import com.example.quiz.activities.MainActivity;
import com.example.quiz.authSharedPreference.AuthPreferences;
import com.example.quiz.utilities.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private AuthPreferences authPreferences;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Settings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings.
     */
    // TODO: Rename and change types and number of parameters
    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Button signOutButton = view.findViewById(R.id.logOutButton);
        authPreferences = new AuthPreferences(view.getContext());
        signOutButton.setOnClickListener(v -> logOut(v.getContext()));

        // Create on click listeners for relative layouts
        RelativeLayout editProfile = view.findViewById(R.id.editProfileSettings);
        RelativeLayout subscribeLatestNews = view.findViewById(R.id.settingsSubscribeLatestNews);
        SwitchCompat latestNewsSwitch = view.findViewById(R.id.latestNewsSwitch);
        if (authPreferences.getBoolean(Constants.SUBSCRIBE_LATEST_NEWS)) {
            latestNewsSwitch.setChecked(true);
        }
        subscribeLatestNews.setOnClickListener(v -> {
          if (latestNewsSwitch.isChecked()) {
              latestNewsSwitch.setChecked(false);
              authPreferences.putBoolean(Constants.SUBSCRIBE_LATEST_NEWS, false);
          } else {
              latestNewsSwitch.setChecked(true);
              authPreferences.putBoolean(Constants.SUBSCRIBE_LATEST_NEWS, true);
          }
        });
        latestNewsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            authPreferences.putBoolean(Constants.SUBSCRIBE_LATEST_NEWS, isChecked);
        });

        RelativeLayout allowNotifications = view.findViewById(R.id.allowNotifications);
        SwitchCompat switchCompat = view.findViewById(R.id.switchAllowNotifications);
        switchCompat.setChecked(authPreferences.getBoolean(Constants.ALLOW_NOTIFICATIONS));
        allowNotifications.setOnClickListener(v -> {
            if (switchCompat.isChecked()) {
                switchCompat.setChecked(false);
                authPreferences.putBoolean(Constants.ALLOW_NOTIFICATIONS, false);
            } else {
                switchCompat.setChecked(true);
                authPreferences.putBoolean(Constants.ALLOW_NOTIFICATIONS, true);
            }
        });

        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            authPreferences.putBoolean(Constants.ALLOW_NOTIFICATIONS, isChecked);
        });


        editProfile.setOnClickListener(this::relativeClicks);

        RelativeLayout aboutItem = view.findViewById(R.id.settingsAboutItem);
        aboutItem.setOnClickListener(v-> startActivity(new Intent(view.getContext(), AboutPage.class)));
        
        return view;
    }

    private void logOut(Context context) {
        if (authPreferences.getString(Constants.SIGN_IN_METHOD, "").equals("google")) {
            GoogleSignInOptions gso = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
            mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   completeSignOut(context);
               }
            });
        } else {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            completeSignOut(context);
        }
    }

    private void completeSignOut(Context context) {
        authPreferences.clearAuthPreferences();
        Intent intent = new Intent(context, Login.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void relativeClicks(View v) {
        switch (v.getId()) {
            case R.id.editProfileSettings:
                startActivity(new Intent(v.getContext(), EditProfile.class));
                break;
        }
    }
}