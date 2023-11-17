package com.example.moble_project.test.Camera;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CameraX {

    private Context context;
    private ExecutorService cameraExecutor;
    private FirebaseHelper firebaseHelper;
    private FaceDetectionView faceDetectionView;
    private FaceDetectionListener listener;
    private ImageCapture imageCapture;
    private boolean isCapturing = false;
    private ProcessCameraProvider cameraProvider;
    private Button captureButton;




    public interface FaceDetectionListener {
        void onFaceDetected(List<FirebaseVisionFace> faces);

        void onFaceDetectionFailure(Exception e);

    }


    public CameraX(Context context, FaceDetectionView faceDetectionView, FaceDetectionListener listener, Button captureButton) {
        this.context = context;
        this.faceDetectionView = faceDetectionView;
        this.listener = listener;
        this.captureButton = captureButton;
        this.cameraExecutor = Executors.newFixedThreadPool(3);

        firebaseHelper = new FirebaseHelper(context);
        firebaseHelper.setFaceDetectionListener(new FirebaseHelper.FaceDetectionListener() {

            private boolean wasRealFace = false; // 초기 상태는 false로 설정
            private boolean wasFaceDetectionFailure = false; // 초기 상태는 false로 설정

            @Override
            public void onFaceDetected(List<FirebaseVisionFace> faces) {
                if (!faces.isEmpty()) {
                    FirebaseVisionFace face = faces.get(0);
                    Rect bounds = face.getBoundingBox();

                    boolean isRealFace = firebaseHelper.checkIfRealFace(face);

                    if (isRealFace) {
                        // 버튼을 활성화
                        captureButton.setEnabled(true);

                        // 3초 후에 버튼을 비활성화
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                captureButton.setEnabled(false);
                            }
                        }, 3000); // 3초를 밀리초로 변환한 값
                    }


                    // 얼굴 상태가 "사진"에서 "실물"로 변경될 때만 토스트 메시지를 출력합니다.
                    if (!wasRealFace && isRealFace) {
                        if (context != null) {
//                            Toast.makeText(context, "사진입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (wasRealFace && !isRealFace) {
                        if (context != null) {
                            Toast.makeText(context, "실물입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    // 얼굴이 감지되면 박스를 그리도록 설정
                    if (faceDetectionView != null) {
                        faceDetectionView.setFaceRect(bounds);
                    }

                    if (context != null) {
                        Log.d("CameraX_실물", "실물 감지 성공\n" +
                                "얼굴 좌표 - " + bounds.left + ", " + bounds.top + ", " + bounds.right + ", " + bounds.bottom);
                    }

                    if (listener != null) {
                        listener.onFaceDetected(faces);
                        Log.d("CameraX", "얼굴 감지 실패");
                    }

                    // 상태를 업데이트합니다.
                    wasRealFace = isRealFace;
                    wasFaceDetectionFailure = false;
                } else {
                    // 얼굴 감지가 실패하면 박스를 그리지 않도록 설정
                    if (faceDetectionView != null) {
                        faceDetectionView.setFaceRect(null);
                    }

                    if (!wasFaceDetectionFailure) {
                        if (context != null) {
                            Toast.makeText(context, "얼굴을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        wasFaceDetectionFailure = true;
                    }

                    if (listener != null) {
                        listener.onFaceDetectionFailure(new Exception("얼굴을 찾을 수 없습니다."));
                    }

                    // 얼굴이 인식되지 않았으므로 상태를 업데이트합니다.
                    wasRealFace = false;
                }
            }

            @Override
            public void onFaceDetectionFailure(Exception e) {

                // 박스를 그리지 않도록 설정
                if (faceDetectionView != null) {
                    faceDetectionView.setFaceRect(null);
                }
                // 사용자에게 실패 메시지를 표시
                if (context != null) {
                    Toast.makeText(context, "얼굴 감지에 실패했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                // 외부에서 전달받은 리스너의 메서드 호출
                if (listener != null) {
                    listener.onFaceDetectionFailure(e);
                }
            }
        });
    }

    public void startCamera(PreviewView previewView) {
        cameraExecutor = Executors.newSingleThreadExecutor();

        // 카메라 초기화
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get(); // 클래스의 멤버 변수에 할당

                // 미리보기 설정
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // 이미지 분석 설정지
                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();
                imageAnalysis.setAnalyzer(cameraExecutor, image -> {
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.capacity()];
                    buffer.get(bytes);
                    firebaseHelper.detectFaces_byte(bytes, image.getWidth(), image.getHeight());
                    image.close();
                });

                // 이미지 캡처 설정
                ImageCapture imageCapture = new ImageCapture.Builder()
                        .setFlashMode(ImageCapture.FLASH_MODE_OFF) // 플래시 모드 설정
                        .build();


                // 카메라 바인딩
                CameraSelector cameraSelector = new CameraSelector.Builder()
//                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, preview, imageAnalysis);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));
    }
    private void initializeImageCapture() {
        imageCapture = new ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                .build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        // 이미지 캡처 객체를 카메라에 바인딩
        Camera camera = cameraProvider.bindToLifecycle(
                (LifecycleOwner) context, cameraSelector, imageCapture);
    }
    public void captureImage(Consumer<byte[]> resultCallback) {
        if (imageCapture == null) {
            initializeImageCapture(); // 객체가 null이면 초기화
        }

        try {
            imageCapture.takePicture(cameraExecutor, new ImageCapture.OnImageCapturedCallback() {
                @Override
                public void onCaptureSuccess(@NonNull ImageProxy image) {
                    try {
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        Log.d("캡쳐 성공", "이미지 캡쳐 성공");

                        resultCallback.accept(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("캡쳐 오류", "이미지 캡쳐중 오류: " + e.getMessage());
                    } finally {
                        image.close();
                    }
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    exception.printStackTrace();
                    Log.e("캡쳐 오류", "이미지 캡쳐 오류: " + exception.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("캡쳐 오류", "captureImage메서드에서 오류: " + e.getMessage());
        }
    }

}

