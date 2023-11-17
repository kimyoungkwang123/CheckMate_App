package com.example.moble_project.test.master;

import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS;
//import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS2;
import static com.example.moble_project.test.util.MyUtility.HOST_PORT;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.moble_project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class InoutDetailActivity extends AppCompatActivity {

    private Button btn_finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inout_detail);

        btn_finish = findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(view -> finish());

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        sendName(name);
    }

    void sendName(String name) {
        try {
            CustomTask task = new CustomTask();
            task.execute(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class CustomTask extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://"+HOST_ADDRESS+":"+HOST_PORT+"/api/outing/listdetails");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                sendMsg = "name=" + strings[0];
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                osw.write(sendMsg);
                Log.i("woo", "보내는 값"+sendMsg);
                osw.flush();

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
                    Log.i("woo", "통신 결과 "+conn.getResponseCode() + "에러");
                    receiveMsg = "error";
                    Log.i("woo", receiveMsg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                receiveMsg = "error";
            }
            return receiveMsg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.i("woo","outing 받은 값 : "+result);

            try {
                // 문자열로부터 JSON 배열을 만듭니다.
                JSONArray jsonArray = new JSONArray(result);

                // JSON 배열의 첫 번째 요소를 가져옵니다.
                // 만약 여러 개의 객체가 있는 경우 이 부분을 반복문으로 처리해야 할 수도 있습니다.
                JSONObject responseObject = jsonArray.getJSONObject(0);

                String name = responseObject.optString("name");
                String email = responseObject.optString("email");
                String fileURL = responseObject.optString("file_URL");
                String grade = responseObject.optString("grade");
                String phone = responseObject.optString("phone");

                String reason = responseObject.optString("reason");

                // TextViews를 업데이트 합니다.
                TextView nameView = findViewById(R.id.name);
                nameView.setText(name);

                TextView emailView = findViewById(R.id.email);
                emailView.setText(email);

                TextView gradeView = findViewById(R.id.grade);
                gradeView.setText(grade);

                TextView phoneView = findViewById(R.id.numberId); // UI의 id를 확인해야 합니다.
                phoneView.setText(phone);

                TextView reasonView = findViewById(R.id.reasonBox); // UI의 id를 확인해야 합니다.
                reasonView.setText(reason);

                // Glide를 사용하여 이미지 로드
                ImageView imageView = findViewById(R.id.imageView2);
                Glide.with(InoutDetailActivity.this)
                        .load(fileURL) // 서버에서 받아온 이미지 URL
                        .into(imageView); // 이미지를 표시할 ImageView


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}