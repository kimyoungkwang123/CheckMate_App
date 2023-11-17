package com.example.moble_project.test;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moble_project.R;
import com.example.moble_project.test.master.MasterActivity;
import com.example.moble_project.test.util.CustomItem;
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

public class ComentActivity extends AppCompatActivity implements View.OnClickListener{
    TextView com_titleDet;
    TextView com_contentDet;

    Button com_ListBtn;
    String name;
    String email;
    String phone;
    String grade;
    String state;
    String no;
    String title;
    String content;
    String idx;
    String url;
    String viewCnt;
    TextView countview;
    ArrayList<fileitem> fileList;
    Button fileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coment);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");

        no = getIntent().getStringExtra("no");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        grade = getIntent().getStringExtra("grade");
        state = getIntent().getStringExtra("state");
        idx = getIntent().getStringExtra("idx");
        url = getIntent().getStringExtra("file_url");
        viewCnt = getIntent().getStringExtra("viewCnt");

        Log.i("woo","보고 있는 공지 : "+idx);

        com_contentDet = findViewById(R.id.com_contentDet);
        com_titleDet = findViewById(R.id.com_titleDet);
        countview = findViewById(R.id.countview);


        com_contentDet.setText(content );
        com_titleDet.setText(title);
        countview.setText(viewCnt);

        fileId = findViewById(R.id.fileId);


        com_ListBtn = findViewById(R.id.com_ListBtn);

        GetIdxAsyncTask task = new GetIdxAsyncTask();
        task.execute(idx);

        com_ListBtn.setOnClickListener(this);

        fileList = new ArrayList<>();

        fileId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });



    }

    @Override
    public void onClick(View v) {
        int rs = v.getId();
        if(rs == R.id.com_ListBtn){
            backCommu();
        }
    }

    public void backCommu(){
        Log.i("woo","목록 버튼 클릭");
        Intent intent = new Intent(ComentActivity.this, MainSiteActivity.class);

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
                    }
                }

                // 첨부 파일 유무를 확인하고 버튼의 투명도를 설정
                if (fileList.isEmpty()) {
                    // 첨부 파일이 없으면 버튼을 투명하게 만듭니다.
                    fileId.setAlpha(0.0f);      // 완전 투명
                    fileId.setClickable(false); // 클릭 불가능하게 설정
                } else {
                    fileId.setAlpha(1.0f);      // 완전 불투명 (기본 상태)
                    fileId.setClickable(true);  // 클릭 가능하게 설정
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        Log.i("woo", "첨부 파일 버튼 클릭");
        Log.i("woo",fileList+"가져옴");

        // 팝업 메뉴에 파일 URL 목록을 추가합니다.
        for (int i = 0; i < fileList.size(); i++) {
            fileitem file = fileList.get(i);
            // getMenu() 메서드를 사용하여 메뉴에 접근하고, 파일의 URL을 항목으로 추가합니다.
            popup.getMenu().add(0, i, 0, file.getFilename()); // 'getFileUrl()'은 'fileitem'에서 파일의 URL을 반환하는 메서드여야 합니다.
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
    }

    private void handleFileUrl(String fileUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // 알림 영역에 다운로드 상태 표시

        // 파일의 제목과 설명을 설정합니다. 이 부분은 알림에 표시됩니다.
        request.setTitle("File Download");
        request.setDescription("Downloading file...");

        // 다운로드될 파일의 로컬 시스템 경로를 설정합니다.
        // "Environment.DIRECTORY_DOWNLOADS"는 시스템 다운로드 폴더를 나타냅니다.
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "downloaded_file");

        // 다운로드 매니저에 다운로드 작업을 추가합니다.
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }
}