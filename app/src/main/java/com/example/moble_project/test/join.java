package com.example.moble_project.test;
import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS;
import static com.example.moble_project.test.util.MyUtility.HOST_PORT;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.example.moble_project.R;
import com.example.moble_project.test.util.S3Util;

public class join extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 2;
    private Uri imageUri;
    private String imageUrl;
    private static final String CHECK_DUPLICATE_URL = "http://"+HOST_ADDRESS+":"+HOST_PORT+"/api/board/android/checkDuplicate";
    private static final String JOIN_URL = "http://"+HOST_ADDRESS+":"+HOST_PORT+"/api/board/android/join";
    private ImageView imageView;
    Button passcheckBtn;
    EditText et_pass;
    EditText et_passcheck;
    Button emailCheckBtn;
    ImageView passwordhint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Button btn_check = findViewById(R.id.btn_check);
        Button btn_signup = findViewById(R.id.btn_signup);
        Button btn_pic = findViewById(R.id.btn_pic);

        btn_check.setOnClickListener(this);
        btn_pic.setOnClickListener(this);
        btn_signup.setOnClickListener(this);

        EditText et_name = findViewById(R.id.et_name);
        EditText et_birth = findViewById(R.id.et_birth);
        EditText et_jumin = findViewById(R.id.et_jumin);
        EditText et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);
        EditText et_email = findViewById(R.id.et_email);
        EditText et_phone = findViewById(R.id.et_phone);

        et_passcheck = findViewById(R.id.et_passcheck);
        passcheckBtn = findViewById(R.id.passcheckBtn);

        imageView = findViewById(R.id.imageView);
        passcheckBtn.setOnClickListener(this);

        emailCheckBtn = findViewById(R.id.emailCheckBtn);
        emailCheckBtn.setOnClickListener(this);
        Log.i("woo","회원 가입 화면");
        passwordhint = findViewById(R.id.passwordhint);
        passwordhint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(join.this)
                        .setTitle("비밀번호 규칙")
                        .setMessage("영어 대소문자, 숫자, 특수 문자(!,@,#,$,<,>,. 1개 이상)포함 8자리 이상")
                        .setPositiveButton("확인", null)
                        .show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int rs = view.getId();
        if (rs == R.id.btn_check) {
            checkDuplicate();
        } else if (rs == R.id.btn_pic) {
            openGallery();
        } else if (rs == R.id.btn_signup) {
            if (imageUri != null) {
                // 이미지 업로드 완료 후 회원가입 시도
                signup();
            } else {
                Toast.makeText(this, "이미지를 선택하세요.", Toast.LENGTH_SHORT).show();
            }
        } else if (rs == R.id.passcheckBtn) {
            passcheck();
        } else if (rs == R.id.emailCheckBtn) {
            String email = ((EditText) findViewById(R.id.et_email)).getText().toString();

            emailCheck(email);
        }
    }
    public void emailCheck(String email){
        if(isValidEmail(email)){
            Toast.makeText(this, "사용 가능한 이메일 입니다.", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "사용 불가능한 이메일 입니다.", Toast.LENGTH_SHORT).show();
        }
    }
    public void passcheck(){
        String pass1 = et_pass.getText().toString();
        String pass2 = et_passcheck.getText().toString();
        if(isValidPassword(pass1)){
            if(pass1.equals(pass2)){
                Toast.makeText(getApplicationContext(), "비밀번호가 일치 합니다..", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "비밀번호가 일치 하지 않습니다..", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(), "사용할 수 없는 비밀번호 입니다.", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isValidUsername(String id) {
        return id.matches("[a-z0-9]+");
    }
    private void checkDuplicate() {
        String id = ((EditText) findViewById(R.id.et_id)).getText().toString();
        if (isValidUsername(id)) {
            // 아이디가 유효한 경우
            checkDuplicate(id);
        } else {
            // 아이디가 유효하지 않은 경우
            Toast.makeText(this, "유효한 아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkDuplicate(final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHECK_DUPLICATE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("Success")) {
                            Toast.makeText(getApplicationContext(), "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            } else {
                Toast.makeText(this, "이미지를 선택하지 못했습니다.", Toast.LENGTH_SHORT).show();
                // 이미지를 선택하지 못한 경우에 대한 처리 추가
            }
        } else {
            // 이미지를 선택하지 않은 경우에 대한 처리 추가
            Toast.makeText(this, "이미지를 선택해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // 폰 번호가 "010"으로 시작하고 총 10자리여야 합니다.
        return phoneNumber.matches("010\\d{8}");
    }

//    private boolean isValidJuminNumber(String birth, String jumin){
//        // 생년월일은 6자리로 구성되어야 합니다.
//        if (birth.length() != 6) {
//            return false;
//        }
//        // 생년 부분은 1900년부터 2099년 사이의 값을 가져야 합니다.
//        int year = Integer.parseInt(birth.substring(0, 2));
//        int month = Integer.parseInt(birth.substring(2, 4));
//        int day = Integer.parseInt(birth.substring(4, 6));
//
//        int currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100; // 현재 연도의 뒤 2자리를 가져옵니다.
//
//        if (year < 0 || (year > currentYear && year < 1900) || year > 2099) {
//            return false;
//        }
//
//        if (month < 1 || month > 12) {
//            return false;
//        }
//        // 각 월의 일수를 체크하여 일자의 유효성을 검사합니다.
//        int[] daysInMonth = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
//        if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
//            daysInMonth[2] = 29; // 윤년인 경우 2월은 29일까지 있습니다.
//        }
//        if (day < 1 || day > daysInMonth[month]) {
//            return false;
//        }
//        // 주민등록번호는 7자리로 구성되어야 합니다.
//        if (jumin.length() != 7) {
//            return false;
//        }
//        // 주민등록번호의 첫 번째 숫자는 1, 2, 3, 4 중 하나여야 합니다.
//        char firstChar = jumin.charAt(0);
//        if (firstChar != '1' && firstChar != '2' && firstChar != '3' && firstChar != '4') {
//            return false;
//        }
//        // 생년월일 부분과 주민등록번호의 나머지 부분은 숫자로만 이루어져야 합니다.
//        if (!birth.matches("\\d{6}") && !jumin.substring(1).matches("\\d{6}")) {
//            return false;
//        }
//
//        // 주민등록번호 검증 알고리즘을 적용하여 유효성 검사
////        int[] factors = {2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5};
////        int sum = 0;
////        for (int i = 0; i < 12; i++) {
////            sum += (jumin.charAt(i) - '0') * factors[i];
////        }
////        int lastDigit = (11 - (sum % 11)) % 10;
////
////        // 주민등록번호의 마지막 자리 숫자와 계산된 숫자가 일치해야 합니다.
////        if (lastDigit != (jumin.charAt(12) - '0')) {
////            return false;
////        }
//
//        return true;
//    }

    private void signup() {
        // 이미지 업로드 및 imageUrl 가져오기
        String phone = ((EditText) findViewById(R.id.et_phone)).getText().toString();
        String birth = ((EditText) findViewById(R.id.et_birth)).getText().toString();
        String jumin = ((EditText) findViewById(R.id.et_jumin)).getText().toString();
//        if (isValidPhoneNumber(콜) && isValidJuminNumber(birth,jumin)) {
//            // 유효한 폰 번호인 경우
//            uploadFileToS3(imageUri);
//        } else if (isValidPhoneNumber(콜) == false){
//            // 유효하지 않은 폰 번호인 경우
//            Toast.makeText(this, "유효한 폰 번호를 입력하세요.", Toast.LENGTH_SHORT).show();
//        } else if (isValidJuminNumber(birth,jumin)== false) {
//            Toast.makeText(this, "유효하지 않은 주민 번호 입니다.", Toast.LENGTH_SHORT).show();
//        }
        if(isValidPhoneNumber(phone)){
            uploadFileToS3(imageUri);
        }

    }

    private void onImageUploadComplete(String imageUrl) {
        if (imageUrl != null) {
            // 이미지 업로드가 완료된 후 회원가입 정보 전송
            String name = ((EditText) findViewById(R.id.et_name)).getText().toString();
            String birth = ((EditText) findViewById(R.id.et_birth)).getText().toString();
            String jumin = ((EditText) findViewById(R.id.et_jumin)).getText().toString();
            String id = ((EditText) findViewById(R.id.et_id)).getText().toString();
            String pw = ((EditText) findViewById(R.id.et_pass)).getText().toString();
            String phone = ((EditText) findViewById(R.id.et_phone)).getText().toString();
            String email = ((EditText) findViewById(R.id.et_email)).getText().toString();
            this.imageUrl = imageUrl;

            // 회원가입 정보를 JSON 형식으로 생성
            JSONObject requestData = new JSONObject();
            try {
                requestData.put("name", name);
                requestData.put("birth", birth);
                requestData.put("jm", jumin);
                requestData.put("id", id);
                requestData.put("password", pw);
                requestData.put("phone", phone);
                requestData.put("email", email);
                requestData.put("fileurl", this.imageUrl); // 이미지 URL 추가

                // Convert the JSON object to a string
                String jsonString = requestData.toString();
                Log.i("woo", jsonString);

                // Send the registration data to the server
                CustomTask task = new CustomTask();
                task.execute(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("woo", "회원가입 정보 생성 실패");
            }
        } else {
            Toast.makeText(this, "이미지 업로드가 완료되지 않았습니다.", Toast.LENGTH_SHORT).show();
            // 이미지 업로드가 완료되지 않은 경우에 대한 처리 추가
        }
    }

    private void uploadFileToS3(Uri fileUri) {
        try {
            // 파일의 원본 이름을 가져옵니다.
            String originalFileName = getFileName(fileUri);

            // ContentResolver를 사용하여 파일의 InputStream을 가져옵니다.
            InputStream inputStream = getContentResolver().openInputStream(fileUri);

            // 원본 파일 이름을 전달하여 File 객체를 생성합니다.
            File file = S3Util.getInstance().createFileFromInputStream(inputStream, getApplicationContext(), originalFileName);

            Log.i("woo", "File created, starting upload...");

            // 파일 객체를 사용하여 S3에 업로드합니다.
            S3Util.getInstance().uploadWithTransferUtility(this,
                    "image-bucket-for-checkmate", file, new TransferListener() {
                        @Override
                        public void onStateChanged(int id, TransferState state) {
                            Log.i("woo", "TransferState: " + state);
                            if (TransferState.COMPLETED == state) {
                                // 업로드가 성공적으로 완료된 후 URL을 가져옵니다.
                                String url = S3Util.getInstance().getFileUrl("image-bucket-for-checkmate", originalFileName);
                                Log.i("woo", "File URL: " + url);
                                Toast.makeText(join.this, "Upload Completed!", Toast.LENGTH_LONG).show();
                                onImageUploadComplete(url);
                            } else if (TransferState.FAILED == state) {
                                Toast.makeText(join.this, "Upload Failed!", Toast.LENGTH_LONG).show();
                                Log.i("woo","업로드 실패");
                            }
                        }

                        @Override
                        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                            float percentDone = ((float) bytesCurrent / (float) bytesTotal) * 100;
                            Log.i("woo", "Progress: " + percentDone + "%");
                        }

                        @Override
                        public void onError(int id, Exception ex) {
                            Log.e("woo", "Error during upload", ex);
                            Toast.makeText(join.this, "Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            Log.e("woo", "Error: ", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) { // Check if column index is valid
                        result = cursor.getString(columnIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment(); // Use getLastPathSegment as a fallback
        }

        // 파일 이름에서 슬래시를 제거하고 마지막 세그먼트만 가져옵니다.
        if (result != null) {
            if (result.contains("/")) {
                result = result.substring(result.lastIndexOf("/") + 1);
            }

            // 파일 이름 끝에 슬래시가 있으면 제거합니다.
            if (result.endsWith("/")) {
                result = result.substring(0, result.length() - 1);
            }
        }

        return result;
    }

    private class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String jsonString = strings[0];
                URL url = new URL(JOIN_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                Log.i("woo", "JSON Data: " + jsonString);


                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonString);
                writer.flush();
                writer.close();
                os.close();
                Log.i("woo","보낸 값 : "+jsonString);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuilder buffer = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                } else {
                    Log.i("통신결과", conn.getResponseCode() + "에러");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Log.w("woo", result+" -> 받은 값");
                if ("Success".equals(result)) {
                    Toast.makeText(join.this, "회원가입 신청완료", Toast.LENGTH_SHORT).show();
                    Log.i("woo","가입 완료");
                    Intent intent = new Intent(join.this, MainActivity.class);
                    startActivity(intent);
                } else if ("Failure".equals(result)) {
                    Toast.makeText(join.this, "빈칸이 존재합니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(join.this, "허용되지 않은 접근입니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                // Handle the case where result is null
                Log.e("받은값", "Result is null");
                Toast.makeText(join.this, "Error: Result is null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isValidPassword(String password) {
        // 최소 길이 조건 검사
        if (password.length() < 8) {
            return false;
        }

        // 대문자, 소문자, 숫자, 특수 문자 조건 검사
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if ("!@#$%^&*()_+[]{}|;':,.<>?".contains(String.valueOf(c))) {
                hasSpecialChar = true;
            }
        }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}