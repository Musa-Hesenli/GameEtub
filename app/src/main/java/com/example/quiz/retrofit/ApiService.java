package com.example.quiz.retrofit;

import com.example.quiz.api.Creator;
import com.example.quiz.api.Package;
import com.example.quiz.api.Packages;
import com.example.quiz.apiCategories.Categories;
import com.example.quiz.apiCategories.Category;
import com.example.quiz.apiRank.Ranks;
import com.example.quiz.fragments.CategoryFragment;
import com.example.quiz.postrequests.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("packages")
    Call<Packages> getPackages();

    @GET("packages/{id}")
    Call<com.example.quiz.apiSelectedPackage.Packages> getSelectedPackage(
            @Path("id") String packageID
    );

    @GET("quiz-categories")
    Call<Categories> getAllCategories();

    @GET("ranks")
    Call<Ranks> getRanks();

    @POST("users")
    Call<Creator> createNewUser(
            @Body User user,
            Callback<Creator> clb
            );

    @GET("packages")
    Call<Packages> getRelatedPackages(
            @Query("category") String category_id
    );
}
