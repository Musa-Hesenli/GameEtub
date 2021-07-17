package com.example.quiz.activities;

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
import android.widget.Toast;

import com.example.quiz.R;
import com.example.quiz.authSharedPreference.AuthPreferences;
import com.example.quiz.authSharedPreference.RegisterFirebase;
import com.example.quiz.utilities.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Random;


public class Register extends AppCompatActivity implements View.OnClickListener {
    EditText usernameInput, passwordInput, passwordConfirmInput, emailInput;
    String username, password, passwordConfirm, email;
    LinearProgressIndicator linearProgressIndicator;

    private static final int RC_SIGN_IN = 202;
    private Button loginWithGoogle;

    private AuthPreferences authPreferences;
    private static final String[] profileImage = {"https://www.shareicon.net/data/512x512/2015/09/18/103160_man_512x512.png", "https://cdn.icon-icons.com/icons2/2643/PNG/512/male_boy_person_people_avatar_icon_159358.png", "https://i0.wp.com/cdn0.iconfinder.com/data/icons/social-media-network-4/48/male_avatar-512.png", "https://i2.wp.com/cdn3.iconfinder.com/data/icons/business-round-flat-vol-1-1/36/user_account_profile_avatar_person_student_male-512.png", "https://www.shareicon.net/data/512x512/2016/09/15/829466_man_512x512.png"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        Button createAccountSubmit = findViewById(R.id.createAccountButton);
        Button loginInRegister = findViewById(R.id.loginButtonInRegister);
        createAccountSubmit.setOnClickListener(this);
        loginInRegister.setOnClickListener(this);

        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        passwordConfirmInput = findViewById(R.id.passwordConfirmInput);

        linearProgressIndicator = findViewById(R.id.progress_horizontal);

        authPreferences = new AuthPreferences(this);

        loginWithGoogle = findViewById(R.id.signInWithGoogle);
        loginWithGoogle.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButtonInRegister:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.createAccountButton:
                validateAndCreateAccount();
                break;
            case R.id.signInWithGoogle:
                signInWithGoogle();
                break;
        }
    }

    private void validateAndCreateAccount() {
        username = usernameInput.getText().toString();
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();
        passwordConfirm = passwordConfirmInput.getText().toString();
        if (username.length() < 6) {
            usernameInput.setError("Username must be at least 6 characters!");
            return;
        }
        if (email.length() == 0 || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email address");
            return;
        }
        if (password.length() < 8){
            passwordInput.setError("Please enter at least 8 characters");
            return;
        }
        if (passwordConfirm.length() < 8) {
            passwordConfirmInput.setError("Please enter at least 8 characters");
            return;
        }
        if (!passwordConfirm.equals(password)) {
            passwordConfirmInput.setError("Password and confirm must be the same!");
            return;
        }
        linearProgressIndicator.setVisibility(View.VISIBLE);
        createAccount();
    }

    private void createAccount() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().size() == 0) {
                        HashMap<String, String> user = new HashMap<>();
                        user.put("username", username);
                        user.put("email", email);
                        user.put("password", password);
                        user.put("image_url", profileImage[new Random().nextInt(profileImage.length) - 1]);
                        user.put("total_point", "0");
                        addUserToFireStore(user, firebaseFirestore);
                    } else if(task.getResult().size() > 0) {
                        Toast.makeText(this, "This email is already taken!", Toast.LENGTH_SHORT).show();
                    }
                    linearProgressIndicator.setVisibility(View.GONE);
                });
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
            Log.d(Constants.TAG, "Creating User");
            Intent intent = new Intent(this, MainActivity.class);
            assert account != null;
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("users")
                    .whereEqualTo("email", account.getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().size() == 0) {
                            HashMap<String, String> user = new HashMap<>();
                            user.put("username", account.getDisplayName());
                            user.put("email", account.getEmail());
                            user.put("password", account.getEmail());
                            user.put("image_url", account.getPhotoUrl().toString());
                            user.put("total_point", "0");
                            addUserToFireStore(user, firebaseFirestore);
                        } else {
                            if (task.isSuccessful()) {
                                authPreferences.putBoolean(Constants.IS_AUTHENTICATED, true);
                                authPreferences.putString(Constants.SIGN_IN_METHOD, "google");
                                authPreferences.putString(Constants.EMAIL, account.getEmail());
                                authPreferences.putString(Constants.USERNAME, account.getDisplayName());
                                authPreferences.putString("password", account.getEmail());
                                authPreferences.putString("total_point", "0");
                                authPreferences.putString("image_url", account.getPhotoUrl().toString());
                                startActivity(intent);
                                finish();
                            }
                        }

                    }).addOnFailureListener(e -> Log.e(Constants.TAG, e.getMessage()));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void addUserToFireStore(HashMap<String, String> items, FirebaseFirestore firebaseFirestore) {
        firebaseFirestore.collection("users")
                .add(items)
                .addOnSuccessListener(task -> {
                    Log.i(Constants.TAG, "User created");
                    authPreferences.putBoolean(Constants.IS_AUTHENTICATED, true);
                    authPreferences.putString(Constants.USERNAME, items.get(Constants.USERNAME));
                    authPreferences.putString(Constants.EMAIL, items.get(Constants.EMAIL));
                    authPreferences.putString(Constants.SIGN_IN_METHOD, "email");
                    authPreferences.putString("password", items.get("password"));
                    authPreferences.putString("image_url", items.get("image_url"));
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }).addOnFailureListener(t -> {
                    Log.e(Constants.TAG, t.getMessage());
        });
    }
}