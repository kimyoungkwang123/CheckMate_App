package com.example.moble_project.test.Camera;

import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS;
import static com.example.moble_project.test.util.MyUtility.HOST_PORT;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.camera.core.Camera;

import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.moble_project.R;
import com.example.moble_project.test.DTO.UserInfo;
import com.example.moble_project.test.login;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainCamera extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private Camera camera;
    private CameraX camerax;
    private HandlerThread handlerThread; // 클래스 내에서 변수를 선언합니다.
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private Handler handler;
    private FirebaseHelper firebaseHelper;
    private Context context;
    private FaceDetectionView faceDetectionView;
    private CameraX.FaceDetectionListener faceDetectionListener;
    private Handler backgroundHandler;
    private Button captureButton;
    private String clickBtn, userNo, userName, userState, userEmail,userUrl, userPhone, userGrade, userReason;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("start","OnCreate Start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);

        UserInfo userInfo = new UserInfo();

        previewView = findViewById(R.id.previewView);
        handlerThread = new HandlerThread("CameraBackgroundThread");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());

        userNo = getIntent().getStringExtra("user_info_no");
        userName = getIntent().getStringExtra("user_info_name");
        userState = getIntent().getStringExtra("user_info_state");
        userEmail = getIntent().getStringExtra("user_info_email");
        userUrl = getIntent().getStringExtra("user_info_url");
        userPhone = getIntent().getStringExtra("user_info_phone");
        userGrade = getIntent().getStringExtra("user_info_grade");
        clickBtn = getIntent().getStringExtra("user_info_clickBtn");
        userReason = getIntent().getStringExtra("user_info_reason");


        // 카메라 권한을 확인하고 초기화
        if (allPermissionsGranted()) {
            // 카메라 권한을 확인하고 초기화 (백그라운드 스레드에서 호출)
            startCameraInBackground(previewView);
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        faceDetectionView = findViewById(R.id.faceDetectionView);


        // 캡처 버튼 클릭 이벤트 설정
        captureButton = findViewById(R.id.captureButton);
        captureButton.setEnabled(false); // 초기에는 버튼을 비활성화합니다.
        captureButton.setOnClickListener(new View.OnClickListener() {
            @androidx.camera.core.ExperimentalGetImage
            @Override
            public void onClick(View view) {
                ApiHelper apiHelper = new ApiHelper();
                camerax.captureImage(bytes -> {
                    String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);
                    // 서버로 base64Image 전송하는 로직, 예를 들면 ApiHelper 클래스를 사용하여 전송
                    String no = userNo;
                    String btnno = clickBtn;
//                    String reason = userReason;
                    apiHelper.sendImageToServer(base64Image, no, btnno ,new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {

                                try {
                                    String responseBodyString = response.body().string();

                                    Gson gson = new Gson();
                                    ServerRespone serverRespone = gson.fromJson(responseBodyString, ServerRespone.class);

                                    String msg = serverRespone.getMsg();
                                    String clickBtn = serverRespone.getClickBtn();
//                                    String reason = serverRespone.getReason();

                                    Log.i("woo","플라스크 서버 받은 값 : "+msg+" clickBtn : "+clickBtn);

                                    // 성공적으로 이미지가 서버에 전송되었을 때의 처리
//                                    Toast.makeText(MainCamera.this, "촬영 및 서버 전송 완료", Toast.LENGTH_SHORT).show();
                                    Log.d("woo ", "전송 완료");
                                    sendClickTimeAsyncTask task = new sendClickTimeAsyncTask();
                                    sendClickTimeAsyncTask2 task2 = new sendClickTimeAsyncTask2();
                                    if(!clickBtn.equals("3") && msg.equals("0")){
                                        task.execute(clickBtn);
                                        Log.i("woo","이전 화면으로 넘어갑니다.");
                                        goToBack();
                                    } else if (clickBtn.equals("3") && msg.equals("0")) {
                                        task2.execute(clickBtn);
                                        goToBack();
                                    }else if(msg.equals("1")){
                                        Toast.makeText(MainCamera.this, "다시 촬영 해주세요", Toast.LENGTH_SHORT).show();
                                    }


                                }catch (IOException e){
                                    e.printStackTrace();
                                }

                            } else {
                                // 실패한 경우의 처리
                                Toast.makeText(MainCamera.this, "Failed to upload image. Server error.", Toast.LENGTH_SHORT).show();
                                Log.e("woo", "이미지 업로드 실패 서버 에러: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // 네트워크 요청 실패 또는 기타 에러 발생 시 처리
                            Toast.makeText(MainCamera.this, "네트워크 오류.", Toast.LENGTH_SHORT).show();
                            Log.e("NetworkError", "네트워크 오류: ", t);
                        }
                    });
                });

            }
        });
    }

    public void startCameraInBackground(final PreviewView previewView) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("CameraX", "startCameraInBackground메서드 확인");

                    // camerax 객체를 여기서 초기화
                    camerax = new CameraX(MainCamera.this, faceDetectionView, faceDetectionListener, captureButton);
                    camerax.startCamera(previewView);
                } catch (Exception e) {
                    Log.i("CameraX", "startCameraInBackground에서 오류: " + e.getMessage());
                    // 추가적인 예외 처리를 여기에 추가할 수 있습니다.
                }
            }
        });
    }


    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                // 권한이 허용되면 카메라 초기화 (백그라운드 스레드에서 호출)
                startCameraInBackground(previewView);
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 카메라 Executor 종료
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }


    public void goToBack(){
        Intent intent = new Intent(MainCamera.this, login.class);

        intent.putExtra("no",userNo);
        intent.putExtra("name",userName);
        intent.putExtra("state",userState);
        intent.putExtra("email",userEmail);
        intent.putExtra("file_url",userUrl);
        intent.putExtra("phone",userPhone);
        intent.putExtra("grade",userGrade);

        startActivity(intent);
        finish();
    }
    public class sendClickTimeAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String SPRING = null;
            if (strings[0].equals("1")){
                SPRING = "/api/attendance/entry";
            } else if (strings[0].equals("2")) {
                SPRING = "/api/attendance/exit";
//            }else if (strings[0].equals("3")) {
//                SPRING = "/api/outing/outing"; //나중에 따로 구분
            }else if (strings[0].equals("4")) {
                SPRING = "/api/outing/return";
            }
            try {
                Log.i("woo", "서버 연결 시도");
                URL url = new URL("http://"+HOST_ADDRESS+":"+HOST_PORT+SPRING);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                // POST 데이터 구성
                String postParams = "ENo=" + userNo;
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postParams);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

                Log.i("woo", userNo+" -> 보낸 ENo "+url+" 요청 주소");

                int rsnum = conn.getResponseCode();
                Log.i("woo", rsnum + "..");

                // 응답 코드 확인
                if (rsnum == HttpURLConnection.HTTP_OK) {
                    Log.i("woo", "시간 저장 성공");
                } else {
                    Log.e("woo", "HTTP error code: " + conn.getResponseCode());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class sendClickTimeAsyncTask2 extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            try {
                Log.i("woo", "서버 연결 시도");
                URL url = new URL("http://"+HOST_ADDRESS+":"+HOST_PORT+"/api/outing/outing");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                // POST 데이터 구성
                String postParams = "ENo=" + userNo+"&REASON="+userReason;
                Log.i("woo",postParams);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postParams);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

                Log.i("woo", userNo+" -> 보낸 ENo "+url+" 요청 주소");

                int rsnum = conn.getResponseCode();
                Log.i("woo", rsnum + "..");

                // 응답 코드 확인
                if (rsnum == HttpURLConnection.HTTP_OK) {
                    Log.i("woo", "시간 저장 성공");
                } else {
                    Log.e("woo", "HTTP error code: " + conn.getResponseCode());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}