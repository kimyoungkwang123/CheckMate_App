package com.example.moble_project.test.Camera;


import android.util.Base64;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class ApiHelper {
    private static final String BASE_URL = "http://192.168.0.39:5005/";
    private final ApiService apiService;

    public ApiHelper() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public void sendImageToServer(String encodedImage, String no,String clickBtn ,Callback<ResponseBody> callback) {
        MediaType mediaType = MediaType.parse("image/jpeg");
        byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        RequestBody requestBody = RequestBody.create(mediaType, imageBytes);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", "image.jpg", requestBody);
        RequestBody noBody = RequestBody.create(MediaType.parse("text/plain"), no);
        RequestBody clickBtnBody = RequestBody.create(MediaType.parse("text/plain"), clickBtn);

        Call<ResponseBody> call = apiService.uploadImage(part, noBody, clickBtnBody);
        call.enqueue(callback);
    }
}

