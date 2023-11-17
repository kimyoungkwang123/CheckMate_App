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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moble_project.R;
import com.example.moble_project.test.master.UpdateBoard;
import com.example.moble_project.test.util.CustomItem;
import com.example.moble_project.test.util.CustomItemAdapter;
import com.example.moble_project.test.util.CustomItemAdapter2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener{

    TextView titleDet;
    TextView contentDet;

    EditText smallboardEdit;
    Button backtoListBtn;
    Button updateBtn;
    Button smallboardcommit;
    String name;
    String email;
    String phone;
    String grade;
    String state;
    String no;
    String idx;
    String eno;

    String title;
    String content;
    String employee_reply_name;
    String reply_content;
    String reply_update_time;

    // 리스트뷰와 어댑터
    ListView smallBoardList;
    CustomItemAdapter2 adapter;

    ArrayList<CustomItem> dataList;
    String url;
    TextView countview;
    String viewCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");

        no = getIntent().getStringExtra("no");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        grade = getIntent().getStringExtra("grade");
        state = getIntent().getStringExtra("state");
        url = getIntent().getStringExtra("file_url");
        viewCnt = getIntent().getStringExtra("viewCnt");

        eno = getIntent().getStringExtra("eno");
        idx = getIntent().getStringExtra("idx");

        Log.i("woo","사용자 정보 ->  "+"사원 번호 : "+no+" 이름 : "+name+" 권한 : "+state);
        Log.i("woo","게시글 번호 : "+idx );

        contentDet = findViewById(R.id.contentDet);
        titleDet = findViewById(R.id.titleDet);
        countview = findViewById(R.id.countview);

        contentDet.setText(content );
        titleDet.setText(title);
        countview.setText(viewCnt);

        backtoListBtn = findViewById(R.id.backtoListBtn);
        updateBtn = findViewById(R.id.updateBtn);
        smallboardcommit = findViewById(R.id.smallboardcommit);

        smallboardEdit = findViewById(R.id.smallboardEdit);

        smallBoardList = findViewById(R.id.smallBoardList);

        backtoListBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        smallboardcommit.setOnClickListener(this);

        // CustomItem 객체 생성



        dataList = new ArrayList<>();
        adapter = new CustomItemAdapter2(this, android.R.layout.simple_list_item_1,dataList);
        smallBoardList.setAdapter(adapter);

        GetIdxAsyncTask task = new GetIdxAsyncTask();
        task.execute(idx);

        if(no == eno){
            updateBtn.setVisibility(View.VISIBLE);
        }else {
            updateBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int rs = v.getId();

        if(rs == R.id.backtoListBtn){
            backToList();
        } else if (rs == R.id.updateBtn) {
            update();
        } else if (rs == R.id.smallboardcommit) {
            smallBoard();
        }
    }
    public void smallBoard(){

        String content = smallboardEdit.getText().toString();

        SendIdxAsyncTask task = new SendIdxAsyncTask();
        task.execute(idx, content, no);



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


                // POST 데이터 구성
                String postParams = "idx=" + idx;
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postParams);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
                Log.i("woo", idx+" -> 보낸 idx "+url+" 요청 주소");
                // 서버 응답 처리 (필요한 경우)
                int responseCode = conn.getResponseCode();
                Log.i("woo",responseCode+" -> 응답 코드");
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
                    Log.i("woo",receiveMsg  );
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
//            Log.i("woo","데이터 받음"+result);

            if (result != null) {
                // 서버로부터 가져온 데이터를 처리하는 로직을 여기에 추가
                Log.i("woo",result);
                try {
                    JSONArray jsonArray = new JSONArray(result); // 'result'는 서버 응답에서 얻은 JSON 문자열입니다.
                    int jslength = jsonArray.length();
                    Log.i("woo","jsarray 길이 : "+jslength);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String employee_reply_name = jsonObject.getString("employee_reply_name");
//                        String employee_reply_name = jsonObject.isNull("employee_reply_name") ? "댓글이 없습니다." : jsonObject.getString("employee_reply_name");
//                        String reply_content = jsonObject.getString("reply_content");
                        String reply_content = jsonObject.isNull("reply_content") ? "댓글이 없습니다.": jsonObject.getString("reply_content");
//                        String reply_update_time = jsonObject.getString("reply_update_time");
                        String reply_update_time = jsonObject.isNull("reply_update_time") ? "00" : jsonObject.getString("reply_update_time");
                        String reply_no = jsonObject.getString("reply_no");

                        // 이제 이 데이터를 사용할 수 있습니다.
                        Log.i("woo", "reply_update_time: " + reply_update_time + " reply_content: " + reply_content +
                                " employee_reply_name : " +employee_reply_name+" reply_no :"+reply_no);
                        CustomItem item = new CustomItem();

                        item.setEmployee_reply_name(employee_reply_name);
                        item.setReply_content(reply_content);
                        item.setReply_update_time(reply_update_time);
                        item.setReply_no(reply_no);

                        dataList.add(item);
                    }

                    Log.i("woo","댓글 불러오기 완료");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }else {
                // 네트워크 오류 등으로 데이터를 가져오지 못한 경우
                Toast.makeText(BoardActivity.this, "데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SendIdxAsyncTask extends AsyncTask<String, Void, String> {
        String sendMsg;
        String receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://"+HOST_ADDRESS+":"+HOST_PORT+"/board/reply");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);
                Log.i("woo", strings[0]+" -> 보낸 idx "+url+" 요청 주소");

                // POST 데이터 구성
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "idx=" + strings[0] + "&content=" + strings[1]+"&eNo="+strings[2]; // 제목과 내용 추가

                osw.write(sendMsg);
                osw.flush();

                conn.connect();
                int responseCode = conn.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK) {
                    Log.i("woo", "데이터 가져 오는 중");
                    // 서버에서 데이터 읽기
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        Log.i("woo","inputLine"+inputLine);
                        response.append(inputLine);
                    }
                    in.close();
                    receiveMsg = response.toString();// 서버에서 받은 JSON 문자열 반환
                    Log.i("woo",receiveMsg  );
                } else {
                    Log.e("GetDataAsyncTask", "HTTP error code: " + conn.getResponseCode());
                }

                conn.disconnect();
                Log.i("woo","받아온 값 : "+sendMsg);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return receiveMsg;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("woo","받아서 처리 할 값 : "+s);

            if (s != null){
                Toast.makeText(BoardActivity.this, "댓글 성공.", Toast.LENGTH_SHORT).show();

                dataList.clear(); // 기존 댓글 데이터를 지우십시오.
                GetIdxAsyncTask task = new GetIdxAsyncTask();
                task.execute(idx);
            }

        }
    }

    public void update(){
        Intent intent = new Intent(BoardActivity.this, NormalUpdateBoard.class);

        intent.putExtra("no",no);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("state",state);
        intent.putExtra("grade",grade);
        intent.putExtra("file_url",url);

        intent.putExtra("idx",idx);
        intent.putExtra("eno",eno);

        startActivity(intent);
        finish();
    }

    public void backToList(){
        Log.i("woo","목록 버튼 클릭");
        Intent intent = new Intent(BoardActivity.this, MainSiteActivity.class);

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
}