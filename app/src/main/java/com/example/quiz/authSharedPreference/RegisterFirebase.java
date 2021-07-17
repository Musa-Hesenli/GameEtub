package com.example.quiz.authSharedPreference;

import android.util.Log;


import com.example.quiz.utilities.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFirebase {
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    public void createAccountInFirestore(String username, String email, String image_url) {
        Map<String, String> user_info = new HashMap<>();
        user_info.put("username", username);
        user_info.put("email", email);
        user_info.put("image_url", image_url);
        user_info.put("total_point", "0.0");
        firebaseFirestore.collection("users")
                .add(user_info)
                .addOnFailureListener(e-> {
                    Log.e(Constants.TAG, e.getMessage());
                });
    }

    public void checkAccount(String username, String email, String photoUrl) {
        firebaseFirestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().size() == 0) {
                        Log.d(Constants.TAG, String.valueOf(task.getResult().size() + " Create new account"));
                        createAccountInFirestore(username, email, photoUrl);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(Constants.TAG, e.getMessage());
                });
    }


}
