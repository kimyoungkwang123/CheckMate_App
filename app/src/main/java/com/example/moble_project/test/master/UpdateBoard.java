package com.example.moble_project.test.master;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.example.moble_project.R;
import com.example.moble_project.test.MainSiteActivity;
import com.example.moble_project.test.util.S3Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class UpdateBoard extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private String eNo;

    private static final int FILE_SELECT_CODE = 0;
    private ArrayList<String> file_NAMEs;//intent
    private ArrayList<String> file_URLs;//intent
    private ArrayList<String> uploadedFiles = new ArrayList<>();
    private List<Pair<String, String>> uploadedFilesWithUrls = new ArrayList<>();
    EditText titleBox, contentbox;
    Button commitBtn, backBtn, btn_fileadd, btn_filelist;
    String no, name, email, phone, grade, state, url;//intent로 로그인 정보 넘겨온 값
    String title, content, file_URL, file_NAME, idx, noticeyn_i;
    String notice_yn;
    RadioGroup radiogroup;
    RadioButton comentcheckbox, boardcheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_board);

        no = getIntent().getStringExtra("no");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        grade = getIntent().getStringExtra("grade");
        state = getIntent().getStringExtra("state");
        url = getIntent().getStringExtra("file_url");

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        eNo = getIntent().getStringExtra("eNo");
        noticeyn_i = getIntent().getStringExtra("noticeyn");
        idx = getIntent().getStringExtra("idx");

        file_URLs = getIntent().getStringArrayListExtra("file_URLs");
        file_NAMEs = getIntent().getStringArrayListExtra("file_NAMEs");
        //인탠트로 받아온 첨부파일 이름과 주소

        Log.d("UpdateBoard", "file_URLs: " + file_URLs);
        Log.d("UpdateBoard", "file_NAMEs: " + file_NAMEs);

        Log.i("woo", "글 수정 페이지 no : " + no + ", state : " + state + ", grade : " + grade);
        Log.i("woo", "title : " + title + ", content : " + content + ", eNo : " + eNo + ", noticeyn_i :" + noticeyn_i + "idx :" + idx);

        backBtn = findViewById(R.id.backBtn);
        titleBox = findViewById(R.id.titleBox);
        contentbox = findViewById(R.id.contentbox);
        commitBtn = findViewById(R.id.commitBtn);

        radiogroup = findViewById(R.id.radiogroup);
        comentcheckbox = findViewById(R.id.comentcheckbox);
        boardcheckbox = findViewById(R.id.boardcheckbox);

        btn_fileadd = findViewById(R.id.btn_fileadd);
        btn_filelist = findViewById(R.id.btn_filelist);

        titleBox.setText(title);
        contentbox.setText(content);

        for (int i = 0; i < file_NAMEs.size(); i++) {
            String fileName = file_NAMEs.get(i);
            String fileURL = file_URLs.get(i);
            uploadedFilesWithUrls.add(new Pair<>(fileName, fileURL));
            uploadedFiles.add(fileName);
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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        btn_fileadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                explore();
            }
        });
        btn_filelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DEBUG_TAG", "btn_filelist is clicked");
                showFileListPopup(view);
            }
        });
    }

    public void update() {
        new UpdateBoardTask().execute();
    }
    public void fileforDB(){
        new fileforDBTask().execute();
    }

    private class UpdateBoardTask extends AsyncTask<Void, Void, Integer> {
        private String idx;

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + HOST_ADDRESS + ":" + HOST_PORT + "/board/noticeUpdateAPP");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json"); // JSON 형식으로 데이터를 보내도록 변경
                conn.setDoOutput(true);

                idx = getIntent().getStringExtra("idx");
                title = titleBox.getText().toString();
                content = contentbox.getText().toString();
                eNo = getIntent().getStringExtra("eNo");

                // Create a JSONObject to hold the data
                JSONObject jsonData = new JSONObject();
                jsonData.put("idx", idx);
                jsonData.put("title", title);
                jsonData.put("content", content);
                jsonData.put("eNo", eNo);

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
                return responseCode;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(Integer responseCode) {
            if (responseCode != null && responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(UpdateBoard.this, "Edit Successful", Toast.LENGTH_SHORT).show();
                fileforDB();
            } else {
                Toast.makeText(UpdateBoard.this, "Edit Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class fileforDBTask extends AsyncTask<Void, Void, Integer> {


        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                URL url = new URL("http://" + HOST_ADDRESS + ":" + HOST_PORT + "/board/fileupload_app");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json"); // JSON 형식으로 데이터를 보내도록 변경
                conn.setDoOutput(true);


                // Create a JSONObject to hold the data
                JSONObject jsonData = new JSONObject();
                jsonData.put("idx", idx);
                // Create a JSONArray to hold file information
                JSONArray filesArray = new JSONArray();
                for (Pair<String, String> fileInfo : uploadedFilesWithUrls) {
                    JSONObject fileObject = new JSONObject();
                    fileObject.put("file_NAME", fileInfo.first);
                    fileObject.put("file_URL", fileInfo.second);
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
                return responseCode;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(Integer responseCode) {
            if (responseCode != null && responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(UpdateBoard.this, "file_Edit Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateBoard.this, BoardCheckActivity.class);

                intent.putExtra("no", no);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("phone", phone);
                intent.putExtra("state", state);
                intent.putExtra("grade", grade);
                intent.putExtra("file_url", url);

                startActivity(intent);
            } else {
                Toast.makeText(UpdateBoard.this, "file_Edit Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showFileListPopup(View v) {
        PopupMenu filePopupMenu = new PopupMenu(this, v);
        // 파일이 없는 경우 토스트 메시지를 표시합니다.
        if (uploadedFiles.isEmpty()) {
            Toast.makeText(this, "파일이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        filePopupMenu.getMenu().clear();

        // 파일 이름과 URL을 사용하여 메뉴 항목을 생성합니다.
        for (int i = 0; i < uploadedFiles.size(); i++) {
            String fileName = uploadedFiles.get(i);
            Log.d("FileListLog", "showFileListPopup: 팝업에 파일 추가됨 - " + fileName);

            // 파일 이름을 메뉴에 추가합니다.
            filePopupMenu.getMenu().add(Menu.NONE, i, Menu.NONE, fileName);
        }

        // 메뉴 아이템 클릭 이벤트를 처리합니다.
        filePopupMenu.setOnMenuItemClickListener(item -> {
            String selectedFile = item.getTitle().toString();
            Log.i("삭제할 파일 이름", selectedFile);
            String selectedFileURL = null;
            String selectedFileNAME = null;
            for (Pair<String, String> fileInfo : uploadedFilesWithUrls) {
                if (fileInfo.first.equals(selectedFile)) {
                    selectedFileURL = fileInfo.second;
                    selectedFileNAME = fileInfo.first;
                    break;
                }
            }

            if (selectedFileURL == null) {
                Toast.makeText(UpdateBoard.this, "파일을 찾을 수 없습니다: " + selectedFile, Toast.LENGTH_SHORT).show();
                return true;
            }

            final String finalSelectedFileURL = selectedFileURL;
            final String finalSelectedFileNAME = selectedFileNAME;

            // 파일을 비동기적으로 삭제하는 AsyncTask를 실행합니다.
            new AsyncTask<String, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(String... params) {
                    try {
                        String s3FilePath = params[0];
                        Log.i("params[0]", params[0]);
                        S3Util s3Util = S3Util.getInstance();
                        s3Util.deleteFileFromS3("image-bucket-for-checkmate", "uploads/" + s3FilePath);

                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    if (success) {
                        Toast.makeText(UpdateBoard.this, "파일이 삭제되었습니다: " + finalSelectedFileNAME, Toast.LENGTH_SHORT).show();
                        uploadedFiles.remove(selectedFile);
                        uploadedFilesWithUrls.removeIf(pair -> pair.first.equals(finalSelectedFileNAME)); // Java 8 이상 필요
                        Log.d("FileListLog", "onPostExecute: 파일 삭제 후 리스트에서 제거됨 - " + finalSelectedFileNAME);
                    } else {
                        Toast.makeText(UpdateBoard.this, "파일 삭제에 실패했습니다: " + finalSelectedFileNAME, Toast.LENGTH_SHORT).show();
                    }

                    // 파일 목록을 업데이트하고 팝업을 다시 표시합니다.
                    showFileListPopup(v);
                }
            }.execute(selectedFileNAME);

            return true;
        });
        filePopupMenu.show();
    }

    public void onFileUploaded(String fileName) {
        uploadedFiles.add(fileName);
        Log.d("woo", "onFileUploaded: 리스트에 추가됨 - " + fileName);
    }

    // 파일이 업로드되고 나서 호출되는 메서드, 파일명과 URL을 리스트에 추가합니다.
    public void onFileUploaded_post(String fileName, String fileUrl) {
        Pair<String, String> fileInfo = new Pair<>(fileName, fileUrl);
        uploadedFilesWithUrls.add(fileInfo);
        Log.d("woo", "onFileUploaded_post: 리스트에 추가됨 - " + fileName + ", URL: " + fileUrl);
    }

    private void explore() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");  // 모든 유형의 파일을 표시하도록 설정합니다.
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "파일을 선택하세요"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // 파일 선택기가 없을 때 예외 처리합니다.
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
                Log.d("File Name", fileName); // 파일명을 로그로 출력합니다.
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
            Log.e("woo", "Failed to get file name", e);
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
                            Toast.makeText(UpdateBoard.this, "Upload Complete!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(UpdateBoard.this, "Upload Error!", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(UpdateBoard.this, BoardCheckActivity.class);

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
}