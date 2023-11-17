package com.example.moble_project.test.master;

import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS;
import static com.example.moble_project.test.util.MyUtility.HOST_PORT;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moble_project.R;
import com.example.moble_project.test.util.S3Util;
import com.example.moble_project.test.util.fileitem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailBoardCheck extends AppCompatActivity implements View.OnClickListener {

    // UI Components
    private String intentFileURL;
    private String intentFileNAME;
    private String fileName;
    private TextView titleDet, contentDet;
    private Button backtoListBtn, delBtn, updateBtn, chumBtn;

    // Data Fields
    private String title, content, eNo, noticeyn, idx;
    private String no, name, email, phone, grade, state, url;
    private ArrayList<fileitem> fileList = new ArrayList<>();
    private List<Pair<String, String>> filename_url = new ArrayList<>();

    private String file_URL, file_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_board_check);
        titleDet = findViewById(R.id.titleDet);
        contentDet = findViewById(R.id.contentDet);
        backtoListBtn = findViewById(R.id.backtoListBtn);
        delBtn = findViewById(R.id.delBtn);
        updateBtn = findViewById(R.id.updateBtn);
        chumBtn = findViewById(R.id.chum);

        titleDet.setText(title);
        contentDet.setText(content);

        //프로필 인텐트
        no = getIntent().getStringExtra("no");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        grade = getIntent().getStringExtra("grade");
        state = getIntent().getStringExtra("state");
        url = getIntent().getStringExtra("file_url");  // 프로필 사진url

        //선택된 게시글 인탠트
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        eNo = getIntent().getStringExtra("eNo");
        noticeyn = getIntent().getStringExtra("noticeyn");
        idx = getIntent().getStringExtra("idx");
        Log.i("DetailBoardCheckfromboch","eNo: "+eNo);

        //첨부파일 이름, 다운링크 인텐트
        if (title != null) {
            titleDet.setText(title);
        }

        if (content != null) {
            contentDet.setText(content);
        }
        new GetIdxAsyncTask().execute(idx);

        setOnClickListeners();

    }

    private void setOnClickListeners() {
        backtoListBtn.setOnClickListener(this);
        delBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        chumBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.backtoListBtn) {
            back();
        } else if (id == R.id.delBtn) {
            del();
        } else if (id == R.id.updateBtn) {
            update();
        } else if (id == R.id.chum) {
            showPopupMenu(v);
        }
    }

    private void showPopupMenu(View view) {
        if (fileList != null && !fileList.isEmpty()) {
            PopupMenu popup = new PopupMenu(this, view);
            Log.i("woo", "첨부 파일 버튼 클릭");
            Log.i("woo",fileList+"가져옴");

            // 파일 목록이 있는 경우 팝업 메뉴를 표시합니다.
            for (int i = 0; i < fileList.size(); i++) {
                fileitem file = fileList.get(i);
                popup.getMenu().add(0, i, 0, file.getFilename());
            }

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // 선택된 파일의 URL을 가져옵니다.
                    fileitem selectedFile = fileList.get(item.getItemId());
                    // 파일을 처리하는 로직 (예: 웹 브라우저에서 URL 열기, 다운로드 등)
                    handleFileUrl(selectedFile.getFileurl());
                    return true;
                }
            });

            popup.show(); // 팝업 메뉴 표시
        } else {
            // 파일 목록이 없는 경우 사용자에게 알림을 표시합니다.
            Toast.makeText(this, "첨부 파일이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private class GetIdxAsyncTask extends AsyncTask<String, Void, String> {
        String receiveMsg;
        @Override
        protected String doInBackground(String... params) {
            String idx = params[0];
            try {
                URL url = new URL("http://"+HOST_ADDRESS+":"+HOST_PORT+"/board/list/"+idx);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);
                Log.i("woo", idx+" -> 보낸 idx "+url+" 요청 주소");

                // POST 데이터 구성
                String postParams = "idx=" + idx;
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postParams);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

                // 서버 응답 처리 (필요한 경우)
                int responseCode = conn.getResponseCode();
                Log.i("woo","통신 결과 -> "+responseCode);
                if(responseCode == HttpURLConnection.HTTP_OK) {
                    Log.i("woo", "데이터 가져 오는 중");
                    // 서버에서 데이터 읽기
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
//                        Log.i("woo","inputLine"+inputLine);
                        response.append(inputLine);
                    }
                    in.close();
                    receiveMsg = response.toString();// 서버에서 받은 JSON 문자열 반환
//                    Log.i("woo",receiveMsg  );
                } else {
                    Log.e("GetDataAsyncTask", "HTTP error code: " + conn.getResponseCode());
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("woo", "데이터 받음" + result);
            try {
                JSONArray jsonArray = new JSONArray(result); // 'result'는 서버 응답에서 얻은 JSON 문자열입니다.
                int jslength = jsonArray.length();

                Log.i("woo", "jsarray 길이 : " + jslength);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // 여기에서 필요한 데이터를 추출하고 처리합니다.

                    String fileadd = jsonObject.getString("file_URL");
                    String filename = jsonObject.getString("file_NAME");

                    if (!jsonObject.isNull("file_URL") && !jsonObject.isNull("file_NAME")) {
                        fileitem item = new fileitem(fileadd, filename);
                        fileList.add(item);
                        filename_url.add(new Pair<>(filename, fileadd));
                    }
                }

                if (fileList.isEmpty()) {
                    chumBtn.setVisibility(View.INVISIBLE);
                } else {
                    chumBtn.setVisibility(View.VISIBLE);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void handleFileUrl(String fileUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("File Download");
        request.setDescription("Downloading file...");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "downloaded_file");

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }

    private void del() {
        // Remove the file from S3
        String s3FilePath = url.replace("https://image-bucket-for-checkmate.s3.ap-northeast-2.amazonaws.com/", "");
        S3Util s3Util = S3Util.getInstance();
        s3Util.deleteFileFromS3("image-bucket-for-checkmate", "uploads/" + s3FilePath);

        // Delete post from DB
        new SendIdxAsyncTaskfordel().execute(idx);
        transitionTo(BoardCheckActivity.class);
    }

    private void update() {
        ArrayList<String> fileUrls = new ArrayList<>();
        ArrayList<String> fileNames = new ArrayList<>();

        for (Pair<String, String> pair : filename_url) {
            fileNames.add(pair.first);
            fileUrls.add(pair.second);
        }

        Intent intent = new Intent(DetailBoardCheck.this, UpdateBoard.class);
        populateIntent(intent);

        intent.putExtra("idx", idx);
        intent.putExtra("eNo", eNo);
        intent.putExtra("title", title);
        intent.putExtra("content", content);

        intent.putStringArrayListExtra("file_URLs", fileUrls);
        intent.putStringArrayListExtra("file_NAMEs", fileNames);

        Log.d("DetailBoardCheck", "eNo: " + eNo);

        startActivity(intent);
        finish();
    }

    private void back() {
        transitionTo(BoardCheckActivity.class);
    }

    private void transitionTo(Class<?> targetClass) {
        Intent intent = new Intent(DetailBoardCheck.this, targetClass);
        populateIntent(intent);
        startActivity(intent);
        finish();
    }

    private void populateIntent(Intent intent) {
        intent.putExtra("no", no);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("state", state);
        intent.putExtra("grade", grade);
        intent.putExtra("file_url", url);
    }

    private class SendIdxAsyncTaskfordel extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String response = ""; // 이것은 응답을 저장하기 위한 변수입니다. 실제로는 여러분이 어떻게 데이터를 가져오는지에 따라 조정해야 할 수 있습니다.
            try {
                URL url = new URL("http://" + HOST_ADDRESS + ":" + HOST_PORT + "/board/boardDeleteAPP");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                setHttpConnectionProperties(conn, params[0]);
                conn.connect();

                int responseCode = conn.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK) {
                    // 여기에서 응답 데이터를 가져와 'response' 변수에 할당합니다.
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response; // doInBackground에서의 반환 값이 onPostExecute의 매개변수로 전달됩니다.
        }

        private void setHttpConnectionProperties(HttpURLConnection conn, String idx) throws Exception {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(idx);
            writer.flush();
            writer.close();
            os.close();
        }
    }


}