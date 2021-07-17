package com.example.quiz.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.quiz.R;
import com.example.quiz.authSharedPreference.AuthPreferences;
import com.example.quiz.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

public class EditProfile extends AppCompatActivity {
    Button updateAccountButton;
    EditText usernameInput, passwordInput;
    AuthPreferences authPreferences;
    TextInputLayout usernameInputLayout, passwordInputLayout;
    LinearProgressIndicator loader;
    private ImageView profileImage;
    private String document_id;
    private String total_point;
    View divider;
    private TextView usernameText, emailText, totalPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        loader = findViewById(R.id.progress_horizontal);
        divider = findViewById(R.id.divider);
        divider.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        authPreferences = new AuthPreferences(this);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);

        usernameInput.setText(authPreferences.getString(Constants.USERNAME, ""));
        passwordInput.setText(authPreferences.getString("password", ""));

        usernameText = findViewById(R.id.usernameInfoText);
        emailText = findViewById(R.id.userinfo_last_text);
        totalPoint = findViewById(R.id.user_total_point);
        profileImage = findViewById(R.id.profileImage);

        Glide.with(this).asBitmap().load(authPreferences.getString("image_url", "")).into(profileImage);
        usernameText.setText(authPreferences.getString(Constants.USERNAME, ""));
        emailText.setText(authPreferences.getString(Constants.EMAIL, ""));

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users")
                .whereEqualTo("email", authPreferences.getString(Constants.EMAIL, null))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().size() > 0) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            totalPoint.setText(doc.getString("total_point") + " points");
                            document_id = doc.getId();
                            total_point = doc.getString("total_point");
                        }
                    }
                    loader.setVisibility(View.GONE);
                    divider.setVisibility(View.VISIBLE);
                });

        usernameInputLayout = findViewById(R.id.usernameLabel);
        passwordInputLayout = findViewById(R.id.passwordInputLabel);


        updateAccountButton = findViewById(R.id.updateAccount);
        updateAccountButton.setOnClickListener(v -> updateAccount());
    }

    private void updateAccount() {
        String username, password;
        username = usernameInput.getText().toString();
        password = passwordInput.getText().toString();
        if (username.length() < 6) {
            usernameInputLayout.setError("Username must be at least 6 characters");
            return;
        } else {
            usernameInputLayout.setError(null);
        }
        if (password.length() < 8) {
            passwordInputLayout.setError("Password must be at least 8 characters!");
            return;
        } else {
            passwordInputLayout.setError(null);
        }

        //todo: Update account information
        HashMap<String, String> user_info = new HashMap<>();
        user_info.put("username", username);
        user_info.put("email", authPreferences.getString("email", ""));
        user_info.put("image_url", authPreferences.getString("image_url", ""));
        user_info.put("password", password);
        user_info.put("total_point", total_point);
        if (total_point != null || document_id != null) {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("users")
                    .document(document_id)
                    .set(user_info)
                    .addOnSuccessListener(s -> {
                        Toast.makeText(getApplicationContext(), "Information updated successfully", Toast.LENGTH_SHORT).show();
                        usernameText.setText(username);
                        authPreferences.putString(Constants.USERNAME, username);
                    })
                    .addOnFailureListener(e -> Log.e(Constants.TAG, e.getMessage()));
            ;
        }


    }
}