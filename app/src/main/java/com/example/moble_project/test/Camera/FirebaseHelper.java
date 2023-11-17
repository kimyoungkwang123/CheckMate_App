package com.example.moble_project.test.Camera;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;

import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


    public class FirebaseHelper {

    private FaceDetectionListener faceDetectionListener;
    private FirebaseVisionFaceDetector faceDetector;
    private ExecutorService firebaseExecutor;

    public interface FaceDetectionListener {
        void onFaceDetected(List<FirebaseVisionFace> faces);
        void onFaceDetectionFailure(Exception e);
    }

    public void setFaceDetectionListener(FaceDetectionListener listener) {
        this.faceDetectionListener = listener;
    }

    public FirebaseHelper(Context context) {
        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .setMinFaceSize(0.15f)
                .enableTracking()
                .build();

        faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        firebaseExecutor = Executors.newFixedThreadPool(3); // 필요에 따라 스레드 수 조절 가능
    }

        public void detectFaces_byte(byte[] imageBytes, int width, int height) {
            firebaseExecutor.execute(() -> {
                FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromByteArray(imageBytes,
                        new FirebaseVisionImageMetadata.Builder()
                                .setWidth(width)
                                .setHeight(height)
                                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                                .setRotation(FirebaseVisionImageMetadata.ROTATION_270)
                                .build());

                faceDetector.detectInImage(firebaseVisionImage)
                        .addOnSuccessListener(faces -> {
                            if (faceDetectionListener != null) {
                                faceDetectionListener.onFaceDetected(faces);
                                Log.d("FirebaseHelper", "얼굴감지성공 ");

                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirebaseHelper", "얼굴감지실패: " + e.getMessage());
                            if (faceDetectionListener != null) {
                                faceDetectionListener.onFaceDetectionFailure(e);
                            }
                        });
            });
        }

    public boolean checkIfRealFace(@NonNull FirebaseVisionFace face) {
        if (face == null) {
            throw new IllegalArgumentException("face cannot be null");
        }

        float smilingProbability = face.getSmilingProbability();
        float rightEyeOpenProbability = face.getRightEyeOpenProbability();
        float leftEyeOpenProbability = face.getLeftEyeOpenProbability();
        Log.d("CameraX", "오른쪽 눈깜박임"+ rightEyeOpenProbability + "왼쪽 눈깜박임" + leftEyeOpenProbability + "웃는 정도" + smilingProbability);
            // 값이 유효한지 확인
        if (smilingProbability >= 0.0 && smilingProbability <= 1.0 &&
                rightEyeOpenProbability >= 0.0 && rightEyeOpenProbability <= 1.0 &&
                leftEyeOpenProbability >= 0.0 && leftEyeOpenProbability <= 1.0) {
            return smilingProbability > 0.2 &&rightEyeOpenProbability>0.9&&leftEyeOpenProbability>0.9;
        } else {

            return false; // 유효하지 않은 값이므로 false 반환
        }
    }
}

