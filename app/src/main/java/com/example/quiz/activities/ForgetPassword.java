package com.example.quiz.activities;

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
import com.example.quiz.mail.JavaMailAPI;
import com.example.quiz.utilities.Constants;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Random;

public class ForgetPassword extends AppCompatActivity implements View.OnClickListener{
    private LinearProgressIndicator progressBar;
    private View divider;
    private EditText emailInput, newPasswordInput;
    private String document_id;
    TextInputLayout emailInputLayout, codeInputLayout, newPasswordLayout;
    private int number;
    private Button checkCode, resetPassword;
    private AuthPreferences authPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        progressBar = findViewById(R.id.progress_horizontal);
        divider = findViewById(R.id.divider);
        emailInput = findViewById(R.id.passwordResetEmail);
        emailInputLayout = findViewById(R.id.passwordResetInputLayout);
        codeInputLayout = findViewById(R.id.passwordLayout);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        newPasswordInput = findViewById(R.id.newPassowrdInput);
        resetPassword = findViewById(R.id.resetPassword);
        authPreferences = new AuthPreferences(this);

        Button sendCodeButton = findViewById(R.id.sendCode);
        checkCode = findViewById(R.id.updatePassword);
        sendCodeButton.setOnClickListener(this);
        checkCode.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendCode) {
            String email = emailInput.getText().toString();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.length() == 0) {
                emailInputLayout.setError("Please enter a valid email address!");
                return;
            } else {
                emailInputLayout.setError(null);
                document_id = null;
                progressBar.setVisibility(View.VISIBLE);
                divider.setVisibility(View.GONE);
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseFirestore.collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(task -> {
                           if (task.isSuccessful() && task.getResult().size() > 0) {
                               for (QueryDocumentSnapshot doc : task.getResult()) {
                                   document_id = doc.getId();
                               }
                               Random rnd = new Random();
                               number = rnd.nextInt(999999);

                               @SuppressLint("DefaultLocale") String generatedString =  String.format("%06d", number);;
                               JavaMailAPI mailAPI = new JavaMailAPI(this, email, "Password Reset Request","Please enter this numbers to reset your password - " + generatedString, progressBar, divider);
                               mailAPI.execute();
                           } else if (task.isSuccessful() && task.getResult().size() == 0) {
                               progressBar.setVisibility(View.GONE);
                               divider.setVisibility(View.GONE);
                               Toast.makeText(this, "We couldn't find this email in our records", Toast.LENGTH_SHORT).show();
                           }
                        }).addOnFailureListener(err -> {
                            Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
                            divider.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                });
            }

        } else if (v.getId() == R.id.updatePassword) {
            EditText codeInput = findViewById(R.id.passwordCodeInput);
            if (codeInput.getText().toString().equals(String.valueOf(number))) {
                codeInputLayout.setVisibility(View.GONE);
                checkCode.setVisibility(View.GONE);
                newPasswordLayout.setVisibility(View.VISIBLE);
                resetPassword.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Please check email and enter code again. That is wrong code.", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.resetPassword) {
            String newPassword = newPasswordInput.getText().toString();
            if (newPassword.length() < 8) {
                newPasswordLayout.setError("Please enter at least 8 characters!");
                return;
            } else {
                newPasswordLayout.setError(null);
            }
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("users")
                    .document(document_id)
                    .update("password", newPassword)
                    .addOnSuccessListener(task -> {
                        switchActivity();
                    }).addOnFailureListener(err -> {
                        progressBar.setVisibility(View.GONE);
                        divider.setVisibility(View.GONE);
                        Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void switchActivity() {
        progressBar.setVisibility(View.VISIBLE);
        divider.setVisibility(View.GONE);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users")
                .document(document_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        authPreferences.putString(Constants.USERNAME, task.getResult().getString("username"));
                        authPreferences.putString(Constants.EMAIL, task.getResult().getString("email"));
                        authPreferences.putBoolean(Constants.IS_AUTHENTICATED,true);
                        authPreferences.putString("image_url", task.getResult().getString("image_url"));
                        authPreferences.putString("password", task.getResult().getString("password"));
                        authPreferences.putString("total_point", task.getResult().getString("total_point"));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    divider.setVisibility(View.GONE);
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    divider.setVisibility(View.VISIBLE);
                    Log.e(Constants.TAG, e.getMessage());
        });
    }
}