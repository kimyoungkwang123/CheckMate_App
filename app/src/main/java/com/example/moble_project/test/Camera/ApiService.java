package com.example.moble_project.test.Camera;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("receive_image") // 엔드포인트 설정
    Call<ResponseBody> uploadImage(
            @Part MultipartBody.Part body,
            @Part("no") RequestBody no,
            @Part("clickBtn") RequestBody clickBtn
    );
}