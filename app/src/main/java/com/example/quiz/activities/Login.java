package com.example.quiz.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quiz.R;
import com.example.quiz.authSharedPreference.AuthPreferences;
import com.example.quiz.utilities.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;


public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText usernameInput;
    private EditText passwordInput;
    LinearProgressIndicator linearProgressIndicator;
    AuthPreferences authPreferences;


    private static final int RC_SIGN_IN = 202;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginSubmit = findViewById(R.id.loginSubmit);
        Button createAccountButton = findViewById(R.id.createAccountInLogin);
        TextView forgetPassword = findViewById(R.id.forget_password);

        Button signInWithGoogle = findViewById(R.id.signInWithGoogle);
        signInWithGoogle.setOnClickListener(this);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);

        loginSubmit.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);

        linearProgressIndicator = findViewById(R.id.progress_horizontal);

        authPreferences = new AuthPreferences(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginSubmit:
                Log.i(Constants.TAG, "Logged in");
                try {
                    validateAndLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.createAccountInLogin:
                Intent intent = new Intent(this, Register.class);
                startActivity(intent);
                break;
            case R.id.forget_password:
                Intent intentForgetPassword = new Intent(this, ForgetPassword.class);
                startActivity(intentForgetPassword);
                finish();
                break;
            case R.id.signInWithGoogle:
                signInWithGoogle();
                break;
        }
    }

    private void validateAndLogin() {
        String email = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            usernameInput.setError("Please enter a valid email address!");
            return;
        }
        if (password.length() < 8) {
            passwordInput.setError("Password must be at least 8 element!");
            return;
        }
        linearProgressIndicator.setVisibility(View.VISIBLE);
        login(email, password);
    }

    private void login(String email, String password) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 0) {
                            Toast.makeText(this, "Email or password is incorrect!", Toast.LENGTH_SHORT).show();
                        } else if (task.getResult().size() > 0) {
                            for (QueryDocumentSnapshot docx : task.getResult()) {
                                authPreferences.putString(Constants.USERNAME, docx.getString("username"));
                                authPreferences.putBoolean(Constants.IS_AUTHENTICATED, true);
                                authPreferences.putString(Constants.EMAIL, docx.getString("email"));
                                authPreferences.putString("password", docx.getString("password"));
                                authPreferences.putString("image_url", docx.getString("image_url"));
                                authPreferences.putString("total_point", docx.getString("total_point"));
                            }
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(exception -> Log.e(Constants.TAG, exception.getMessage()));
    }


    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Intent intent = new Intent(this, MainActivity.class);
            assert account != null;
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("users")
                    .whereEqualTo("email", account.getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() == 0) {
                                HashMap<String, String> user = new HashMap<>();
                                user.put("username", account.getDisplayName());
                                user.put("email", account.getEmail());
                                user.put("password", account.getEmail());
                                user.put("image_url", account.getPhotoUrl().toString());
                                user.put("total_point", "0");
                                firebaseFirestore.collection("users")
                                        .add(user)
                                        .addOnSuccessListener(s -> Log.i(Constants.TAG, "User created"));
                            } else {
                                Log.i(Constants.TAG, "User is already exists. Just login");
                            }
                            authPreferences.putString(Constants.USERNAME, account.getDisplayName());
                            authPreferences.putString(Constants.EMAIL, account.getEmail());
                            authPreferences.putBoolean(Constants.IS_AUTHENTICATED, true);
                            authPreferences.putString(Constants.SIGN_IN_METHOD, "google");
                            authPreferences.putString("password", account.getEmail());
                            authPreferences.putString("image_url", account.getPhotoUrl().toString());
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(e -> Log.e(Constants.TAG, e.getMessage()));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google", "signInResult:failed code=" + e.getStatusCode());
        }
    }
    
}