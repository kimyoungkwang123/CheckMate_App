package com.example.moble_project.test.Camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class FaceDetectionView extends View {
    private boolean faceDetected = false;  // 얼굴 감지 여부를 확인하는 플래그
    private Paint paint;

    // 고정된 박스의 크기
    private static final int FIXED_WIDTH = 800;
    private static final int FIXED_HEIGHT = 1000;

    public FaceDetectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
    }

    public void setFaceRect(Rect rect) {
        faceDetected = rect != null;  // 얼굴 감지 여부를 설정
        invalidate(); // 뷰를 다시 그리도록 갱신합니다.
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (faceDetected) {
            int canvasWidth = getWidth();
            int canvasHeight = getHeight();

            // 고정된 박스 크기를 사용해 박스의 좌표를 결정
            int left = (canvasWidth - FIXED_WIDTH) / 2;
            int top = (canvasHeight - FIXED_HEIGHT) / 2;
            int right = left + FIXED_WIDTH;
            int bottom = top + FIXED_HEIGHT;

            // 감지된 얼굴 주위에 빨간색 박스 그리기
            canvas.drawRect(left, top, right, bottom, paint);
        } else {
            Log.d("FaceDetectionView", "얼굴 감지가 안됨");
        }
    }
}