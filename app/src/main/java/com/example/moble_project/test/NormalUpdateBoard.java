package com.example.moble_project.test;

import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS;
import static com.example.moble_project.test.util.MyUtility.HOST_PORT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moble_project.R;
import com.example.moble_project.test.MainSiteActivity;
import com.example.moble_project.test.WriteBoardActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NormalUpdateBoard extends AppCompatActivity implements View.OnClickListener{
    String idx;
    String eno;
    Button commitBtn;
    Button backBtn;
    EditText titleBox;
    EditText contentbox;
    String no, name, email, phone, grade, state, url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_update_board);

        idx = getIntent().getStringExtra("idx");
        eno = getIntent().getStringExtra("eno");

        no = getIntent().getStringExtra("no");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        grade = getIntent().getStringExtra("grade");
        state = getIntent().getStringExtra("state");
        url = getIntent().getStringExtra("file_url");

        Log.i("woo", "받은 값 : "+idx);

        commitBtn = findViewById(R.id.commitBtn);
        backBtn = findViewById(R.id.backBtn);

        titleBox = findViewById(R.id.titleBox);
        contentbox = findViewById(R.id.contentbox);

        commitBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int rs = v.getId();

        if(rs == R.id.backBtn){
            back();
        } else if (rs == R.id.commitBtn) {
            commit();
        }
    }

    public void back(){
        Log.i("woo","뒤로 가기 버튼");
        Intent intent = new Intent(NormalUpdateBoard.this, MainSiteActivity.class);

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

    public void commit(){

        Log.i("woo","저장 클릭");
        String title = titleBox.getText().toString();
        String content = contentbox.getText().toString();

        CustomTask task = new CustomTask();
        task.execute(idx, title, content,eno);

        Intent intent = new Intent(NormalUpdateBoard.this, MainSiteActivity.class);

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
        String receiveMsg = null;
        String sendMsg;

        @Override
        protected String doInBackground(String... strings) {
            // 서버로부터 데이터를 가져오는 네트워크 작업 수행
            try {
                Log.i("woo","normal update 서버 연결 시도");
                String str;
                URL url = new URL("http://"+HOST_ADDRESS+":"+HOST_PORT+"/board/noticeUpdateAPP"); // 서버 URL 수정 필요
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                Log.i("woo",url+"접속 시도");
                // 인코딩 및 요청 설정
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // 데이터 전송 준비
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "idx="+strings[0]+"&title=" + strings[1] + "&content=" + strings[2]; // 제목과 내용 추가

                osw.write(sendMsg);
                osw.flush();

                Log.i("woo","보낸 값(update) "+sendMsg);

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

                    receiveMsg = buffer.toString();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return receiveMsg;
        }

    }


}