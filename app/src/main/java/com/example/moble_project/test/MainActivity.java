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
import android.widget.TextView;
import android.widget.Toast;

import com.example.moble_project.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button join_btn;
    Button login_btn;
    EditText id_edit;
    EditText pw_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("woo","로그인 화면");

        join_btn = findViewById(R.id.b);
        login_btn = findViewById(R.id.log);

        join_btn.setOnClickListener(this);
        login_btn.setOnClickListener(this);

        id_edit = findViewById(R.id.id);
        pw_edit = findViewById(R.id.pw);
    }

    @Override
    public void onClick(View v) {
        int rs = v.getId();
        if (rs == R.id.b) {
            join();
        } else if (rs == R.id.log) {
            login();
        }
    }
    void login() {
        Log.w("login", "로그인하는중");
        try {
            String id = id_edit.getText().toString();
            String pw = pw_edit.getText().toString();
            Log.w("앱에서 보낸 값", id + ", " + pw);

            // POST 메소드 호출
            CustomTask task = new CustomTask();
            task.execute(id, pw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void join(){
        Intent intent = new Intent(MainActivity.this, join.class);
        startActivity(intent);
    }
    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://"+HOST_ADDRESS+":"+HOST_PORT+"/api/loginApp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // 인코딩
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // 데이터 전송 준비
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "id=" + strings[0] + "&pw=" + strings[1];

                osw.write(sendMsg);
                osw.flush();
                Log.i("woo","앱에서 보내는 값2 : "+sendMsg);

                int er = conn.getResponseCode();
                Log.i("woo",er+"응답 코드");
                // 응답 코드 확인
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
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
                } else if (er == 500){ // 통신 오류 처리
                    Log.i("woo", conn.getResponseCode() + "에러");
//                    Toast.makeText(MainActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                    receiveMsg = "error";
                }
            } catch (MalformedURLException e) { // 잘못된 URL 형식
                e.printStackTrace();
                receiveMsg = "error";
            } catch (IOException e) { // 예외
                e.printStackTrace();
                receiveMsg = "error";
            }
            return receiveMsg;
        }
        @Override
        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
            Log.w("받은값", result);

            if(result != null){

                // 서버에서 받은 결과를 처리하고 필요한 액티비티로 이동하는 로직을 추가하세요.
                if(result.equals("error")){
                    Toast.makeText(MainActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("woo",result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        String no = jsonObject.getString("no");
                        String name = jsonObject.getString("name");
                        String email = jsonObject.getString("email");
                        String phone = jsonObject.getString("phone");
                        String grade = jsonObject.getString("grade");
                        String state = jsonObject.getString("state");
//                    byte[] file_content = Base64.decode(jsonObject.getString("base64Image"), Base64.DEFAULT);
                        String url = jsonObject.getString("file_URL");
                        Log.i("woo",state);

                        if("1".equals(state)){
                            Log.i("woo", "일반 사용자");
                            Intent intent = new Intent(MainActivity.this, login.class);
                            intent.putExtra("no", no);
                            intent.putExtra("name", name);
                            intent.putExtra("email", email);
                            intent.putExtra("phone", phone);
                            intent.putExtra("grade", grade);
                            intent.putExtra("state", state);
                            intent.putExtra("file_url",url);
//                        intent.putExtra("base64Image",file_content);

                            startActivity(intent);
                            finish(); // 현재 액티비티 종료 (선택사항)
                        } else if ("2".equals(state)) {// 1 일반 사용자 2 관리자 0 허가 전 사용자
//
                            Log.i("woo", "관리자");
                            Intent intent = new Intent(MainActivity.this, login.class);

                            intent.putExtra("no", no);
                            intent.putExtra("name", name);
                            intent.putExtra("email", email);
                            intent.putExtra("phone", phone);
                            intent.putExtra("grade", grade);
                            intent.putExtra("state", state);
                            intent.putExtra("file_url",url);

                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(MainActivity.this,"허가 받지않은 사용자 입니다.",Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                        // JSON 파싱 오류 처리
                        Toast.makeText(MainActivity.this, "유저 정보 파싱 실패", Toast.LENGTH_SHORT).show();
                        Log.i("woo","파싱 실패");
                    }
                }
            }else{
                Log.i("woo","알 수 없음...");
            }
        }

    }
}