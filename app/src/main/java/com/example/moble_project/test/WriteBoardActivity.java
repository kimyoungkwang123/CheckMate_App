package com.example.moble_project.test;

import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS;
import static com.example.moble_project.test.util.MyUtility.HOST_PORT;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.example.moble_project.R;
import com.example.moble_project.test.util.S3Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class WriteBoardActivity extends AppCompatActivity implements View.OnClickListener{


    private ProgressDialog progressDialog;
    private String postIdx;

    private static final int FILE_SELECT_CODE = 0;
    private ArrayList<String> uploadedFiles = new ArrayList<>();
    private ArrayList<Pair<String, String>> uploadedFilesWithUrls = new ArrayList<>();

    EditText titleBox, contentbox;
    Button commitBtn, backBtn, btn_fileadd, btn_filelist;
    String no, name, email, phone, grade, state, url;//intent로 로그인 정보 넘겨온 값
    String title, content, file_URL, file_NAME, eno, idx, noticeyn_i;
    String notice_yn;

    RadioGroup radiogroup;
    RadioButton comentcheckbox, boardcheckbox;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main03_user_writeboard);

        no = getIntent().getStringExtra("no");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        grade = getIntent().getStringExtra("grade");
        state = getIntent().getStringExtra("state");
        url = getIntent().getStringExtra("file_url");

        Log.i("woo","글 작성 페이지 no : " + no+", state : " + state + ", grade : " + grade );

        backBtn = findViewById(R.id.backBtn);
        titleBox = findViewById(R.id.titleBox);
        contentbox = findViewById(R.id.contentbox);
        commitBtn = findViewById(R.id.commitBtn);

        radiogroup = findViewById(R.id.radiogroup);
        comentcheckbox = findViewById(R.id.comentcheckbox);
        boardcheckbox = findViewById(R.id.boardcheckbox);

        btn_fileadd = findViewById(R.id.btn_fileadd);
        btn_filelist = findViewById(R.id.btn_filelist);


        if("2".equals(state)){
            radiogroup.setVisibility(View.VISIBLE);
            btn_fileadd.setVisibility(View.VISIBLE);
            btn_filelist.setVisibility(View.VISIBLE);

        } else {
            radiogroup.setVisibility(View.INVISIBLE);
            btn_fileadd.setVisibility(View.INVISIBLE);
            btn_filelist.setVisibility(View.INVISIBLE);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Uploading file...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        comentcheckbox.setChecked(true);
        notice_yn = "y"; // 기본값을 "y"로 설정합니다.

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.comentcheckbox) {
                    notice_yn = "y";
                } else if (checkedId == R.id.boardcheckbox) {
                    notice_yn = "n";
                }

            }
        });

        backBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);
        btn_fileadd.setOnClickListener(v -> explore());
        // btn_filelist 버튼 클릭 이벤트
        btn_filelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileListPopup(view);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int rs = v.getId();
        if (rs == R.id.backBtn) {
            back();
        } else if (rs == R.id.commitBtn) {
            commit();
        } else if (rs == R.id.btn_fileadd) {
            explore();
        }
    }

    public void update(String idx) {
        new UpdateBoardTask(idx).execute();
    }
    // 비동기 작업을 위한 AsyncTask 서브 클래스
    private class UpdateBoardTask extends AsyncTask<Void, Void, Integer> {
        private String idx;

        public UpdateBoardTask(String idx) {
            this.idx = idx;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            // update 작업을 여기에서 수행합니다.
            try {
                URL url = new URL("http://" + HOST_ADDRESS + ":" + HOST_PORT + "/board/inputboardFILE");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonData = new JSONObject();
                jsonData.put("idx", idx);

                // Create a JSONArray to hold file information
                JSONArray filesArray = new JSONArray();
                for (Pair<String, String> fileInfo : uploadedFilesWithUrls) {
                    JSONObject fileObject = new JSONObject();
                    fileObject.put("name", fileInfo.first);
                    fileObject.put("path", fileInfo.second);
                    filesArray.put(fileObject);
                }
                jsonData.put("files", filesArray);

                // Convert the JSON object to a string
                String jsonRequestData = jsonData.toString();

                Log.d("woo", "Request Data: " + jsonRequestData);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonRequestData);
                writer.flush();
                writer.close();
                os.close();


                int responseCode = conn.getResponseCode();
                Log.i("woo","응답 코드 : "+responseCode);
                return responseCode; // HTTP 응답 코드를 반환합니다.


            } catch (Exception e) {
                e.printStackTrace();
                return null; // 오류가 발생하면 null을 반환합니다.
            }
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            if (responseCode != null && responseCode == HttpURLConnection.HTTP_OK) {
                // 서버 응답이 성공이면 실행될 코드
                Toast.makeText(WriteBoardActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
            } else {
                // 실패했을 때 실행될 코드
                Toast.makeText(WriteBoardActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showFileListPopup(View v) {
        PopupMenu filePopupMenu = new PopupMenu(this, v);
        S3Util s3Util = S3Util.getInstance();

        for (String fileName : uploadedFiles) {
            filePopupMenu.getMenu().add(fileName);
        }

        filePopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String selectedFile = item.getTitle().toString();

                // Remove the file from S3 in a background thread
                new AsyncTask<String, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(String... params) {
                        String selectedFile = params[0];
                        try {
                            // Remove the file from S3
                            String s3FilePath = selectedFile; // 파일 이름 그대로 S3 경로로 사용 (수정할 필요가 없을 경우)
                            S3Util s3Util = S3Util.getInstance();
                            s3Util.deleteFileFromS3("image-bucket-for-checkmate", "uploads/" + s3FilePath);
                            return true; // Deletion successful
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false; // Deletion failed
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean success) {
                        if (success) {
                            // 파일 삭제 성공
                            Toast.makeText(WriteBoardActivity.this, "File deleted: " + selectedFile, Toast.LENGTH_SHORT).show();
                            // 리스트에서 파일명 제거
                            uploadedFiles.remove(selectedFile);
                        } else {
                            // 파일 삭제 실패
                            Toast.makeText(WriteBoardActivity.this, "File deletion failed: " + selectedFile, Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute(selectedFile);

                return true;
            }
        });
        filePopupMenu.show();
    }

    public void onFileUploaded(String fileName) {
        uploadedFiles.add(fileName);
    }

    public void onFileUploaded_post(String fileName, String fileUrl) {
        Pair<String, String> fileInfo = new Pair<>(fileName, fileUrl);
        uploadedFilesWithUrls.add(fileInfo);
    }

    private void explore() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");  // 모든 유형의 파일을 보여주도록 설정
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "파일을 선택하세요"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // 파일 선택기가 없는 경우
            Toast.makeText(this, "파일 선택기를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                String fileName = getFileName(uri);
                Log.d("File Name", fileName); // 파일명 로그 출력
                file_NAME = getFileName(uri);
                uploadFile(uri);
            } else {
                Toast.makeText(this, "파일을 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String displayName = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columnIndex != -1) { // 여기에서 유효성을 확인합니다.
                    displayName = cursor.getString(columnIndex);
                }
            }
        } catch (Exception e) {
            Log.e("File Selection", "Failed to get file name", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return displayName;
    }

    private void uploadFile(Uri mImageUri) {
        if (mImageUri != null) {
            try {
                // Uri에서 InputStream 얻기
                InputStream inputStream = getContentResolver().openInputStream(mImageUri);

                // 파일의 원본 이름을 가져옵니다.
                String originalFileName = getFileName(mImageUri);

                // S3Util을 사용하여 InputStream으로부터 파일 생성
                // 원본 파일 이름을 전달하여 임시 파일 대신 해당 이름을 사용합니다.
                S3Util s3Util = S3Util.getInstance();
                File fileToUpload = s3Util.createFileFromInputStream(inputStream, this, originalFileName);

                // 업로드 시작 시 ProgressDialog 표시
                progressDialog.show();

                // 생성된 파일을 S3에 업로드
                // 원본 파일 이름을 객체 키로 사용하여 업로드합니다.
                s3Util.uploadWithTransferUtility(this, "image-bucket-for-checkmate", "uploads", fileToUpload, new TransferListener() {

                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        // 상태가 변경될 때마다 호출됩니다.
                        if (state == TransferState.COMPLETED) {
                            // 업로드 완료
                            Toast.makeText(WriteBoardActivity.this, "Upload Complete!", Toast.LENGTH_SHORT).show();
                            String fileUrl = s3Util.getFileUrl("image-bucket-for-checkmate", "uploads/" + originalFileName);
                            // 업로드된 파일 이름과 URL을 리스트에 추가합니다.
                            onFileUploaded(originalFileName);
                            onFileUploaded_post(originalFileName, fileUrl);
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        // 업로드 진행 상태를 업데이트합니다.
                        int percentage = (int) (bytesCurrent * 100 / bytesTotal); // (수정된 부분: 분모 0 문제 방지)
                        progressDialog.setProgress(percentage);
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        // 오류가 발생했을 때 호출됩니다.
                        Toast.makeText(WriteBoardActivity.this, "Upload Error!", Toast.LENGTH_SHORT).show();
                        Log.e("S3Upload", "Error during upload", ex);
                        progressDialog.dismiss();
                    }

                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "File not found!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to open file!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No file to upload.", Toast.LENGTH_SHORT).show();
        }
    }

    public void back() {
        Log.i("woo", "뒤로가기 버튼 클릭");
        Intent intent = new Intent(WriteBoardActivity.this, MainSiteActivity.class);

        intent.putExtra("no", no);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("state", state);
        intent.putExtra("grade", grade);
        intent.putExtra("file_url", url);

        startActivity(intent);
        finish();
    }

    public void commit(){
        if(notice_yn == null && "2".equals(state)){
            notice_yn = "y";
        }else if(notice_yn == null && !"2".equals(state)){
            notice_yn = "n";
        }
        Log.i("woo","저장 클릭");
        String title = titleBox.getText().toString();
        String content = contentbox.getText().toString();

        CustomTask task = new CustomTask();
        task.execute(title, content, notice_yn, no);

        Intent receivedIntent = getIntent();
        if (receivedIntent != null && receivedIntent.hasExtra("idx")) {
            idx = receivedIntent.getStringExtra("idx");
        } else {
            // idx 값을 가져오지 못한 경우에 대한 예외 처리
            Log.e("woo", "Failed to get idx from Intent");
            // 필요한 처리를 추가하세요.
        }

        Intent intent = new Intent(WriteBoardActivity.this, MainSiteActivity.class);

        intent.putExtra("no",no);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("state",state);
        intent.putExtra("grade",grade);
        intent.putExtra("file_url",url);


        startActivity(intent);

        finish();
    }

    private class CustomTask extends AsyncTask<String, Void, String> {
        String idx = null;
        String sendMsg;

        @Override
        protected String doInBackground(String... strings) {
            // 서버로부터 데이터를 가져오는 네트워크 작업 수행
            try {
                Log.i("woo","서버 연결 시도");
                String str;
                URL url = new URL("http://" + HOST_ADDRESS + ":" + HOST_PORT + "/board/inputboard"); // 서버 URL 수정 필요
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // 인코딩 및 요청 설정
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // 데이터 전송 준비
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "title=" + strings[0] + "&content=" + strings[1]+ "&notice_YN="
                        +strings[2]+"&eNo="+strings[3]; // 제목과 내용 추가

                osw.write(sendMsg);
                osw.flush();

                Log.i("woo",sendMsg);

                // 응답 코드 확인
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 서버에서 데이터 읽기
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);

                    // 데이터 저장
                    StringBuffer buffer = new StringBuffer();

                    // 한 줄씩 데이터 읽어오기
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }

                    idx = buffer.toString();
                    Log.i("woo ", "receiveMsg : "+idx);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return idx;


        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // IDX 값이 유효하고, 파일이 업로드되었는지 확인합니다.
            if (result != null) {
                postIdx = result; // IDX 값을 멤버 변수에 저장합니다.

                if (!uploadedFilesWithUrls.isEmpty()) {
                    // 파일이 업로드되었으므로 update 메소드를 호출합니다.
                    update(postIdx);
                } else {
                    // 파일이 업로드되지 않았으나, 게시글 저장은 성공했습니다.
                    Toast.makeText(WriteBoardActivity.this, "저장 성공." + postIdx + " (첨부파일 없음)", Toast.LENGTH_SHORT).show();
                }
            } else {
                // IDX 값이 반환되지 않았으므로, 저장이 실패한 것으로 간주합니다.
                Toast.makeText(WriteBoardActivity.this, "저장 실패.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}