package com.example.moble_project.test;

import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS;
import static com.example.moble_project.test.util.MyUtility.HOST_PORT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.moble_project.R;
import com.example.moble_project.test.util.CustomItem;
import com.example.moble_project.test.util.CustomItemAdapter;

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
import java.util.ArrayList;

public class MainSiteActivity extends AppCompatActivity implements View.OnClickListener{

    String name;
    String email;
    String phone;
    String grade;
    String state;
    String no;
    String url;
    Button logoutButton;
    Button myPageButton;
    Button write;

    ListView boardlist;
    ListView comentlist;
    ArrayList<CustomItem> dataList = new ArrayList<>();
    CustomItemAdapter adapter;
    CustomItemAdapter comentAdapter;

    ArrayList<CustomItem> boardDataList;
    ArrayList<CustomItem> comentDataList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main02_user_site);

        //이전의 Activity에서 받아온 정보들
        no = getIntent().getStringExtra("no");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        grade = getIntent().getStringExtra("grade");
        state = getIntent().getStringExtra("state");
        url = getIntent().getStringExtra("file_url");

        SharedPreferences sharedPref = getSharedPreferences("AppData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("name", name);
        editor.apply();

        Log.i("woo",no+", "+name+", "+state );
        //리스트뷰 ID값
        comentlist = findViewById(R.id.comentlist);
        boardlist = findViewById(R.id.boardlist);

        //버튼 ID값
        logoutButton = findViewById(R.id.logoutButton);
        myPageButton = findViewById(R.id.myPageButton);
        write = findViewById(R.id.write);
        //버튼 클릭 리스너
        logoutButton.setOnClickListener(this);
        myPageButton.setOnClickListener(this);
        write.setOnClickListener(this);

        //일반 게시글 리스트뷰에 저장을 위함
        boardDataList = new ArrayList<>();
        adapter = new CustomItemAdapter(this, android.R.layout.simple_list_item_1,boardDataList);
        boardlist.setAdapter(adapter);
        //공지글 리스트뷰에 저장을 위함
        comentDataList = new ArrayList<>();
        comentAdapter = new CustomItemAdapter(this, android.R.layout.simple_list_item_1, comentDataList);
        comentlist.setAdapter(comentAdapter);

        new GetDataAsyncTask().execute();

        boardlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭한 아이템의 내용을 가져옴
                CustomItem selectedItem = (CustomItem) parent.getItemAtPosition(position);
                String selectedItemString = selectedItem.toString(); // 또는 CustomItem 클래스에 정의된 다른 문자열 반환 메서드

                // 다음 화면으로 내용 전달
                Intent intent = new Intent(MainSiteActivity.this, BoardActivity.class);
                intent.putExtra("title", selectedItem.getTitle());
                intent.putExtra("content", selectedItem.getContent());
                intent.putExtra("idx",selectedItem.getIdx());
                intent.putExtra("eno",selectedItem.getEno());
                intent.putExtra("viewCnt",selectedItem.getView_cnt());

                intent.putExtra("no",no);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("phone", phone);
                intent.putExtra("state",state);
                intent.putExtra("grade",grade);
                intent.putExtra("file_url",url);

                Log.i("woo",selectedItemString);

                startActivity(intent);
            }
        });

        // comentlist 리스트뷰에 클릭 리스너 설정
        comentlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭한 아이템의 내용을 가져옴
                CustomItem selectedItem = (CustomItem) parent.getItemAtPosition(position);
                String selectedItemString = selectedItem.toString(); // 또는 CustomItem 클래스에 정의된 다른 문자열 반환 메서드

                // 다음 화면으로 내용 전달
                Intent intent = new Intent(MainSiteActivity.this, ComentActivity.class);
                intent.putExtra("title", selectedItem.getTitle());
                intent.putExtra("content", selectedItem.getContent());
                intent.putExtra("idx",selectedItem.getIdx());
                intent.putExtra("viewCnt",selectedItem.getView_cnt());

                intent.putExtra("no",no);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("phone", phone);
                intent.putExtra("state",state);
                intent.putExtra("grade",grade);
                intent.putExtra("file_url",url);

                Log.i("woo",selectedItemString);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int rs = v.getId();
        if(rs == R.id.logoutButton){
            logOut();
        } else if (rs == R.id.myPageButton) {
            myPage();
        }else if(rs == R.id.write){
            writePage();
        }
    }
    public void writePage(){
        Log.i("woo", "글 작성 페이지");
        Intent intent = new Intent(MainSiteActivity.this, WriteBoardActivity.class);

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
    public void logOut(){
        Log.i("woo","로그아웃 버튼 클릭");
        Intent intent = new Intent(MainSiteActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void myPage(){
        Log.i("woo","마이페이지 버튼 클릭");
        Intent intent = new Intent(MainSiteActivity.this,login.class);

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

    private class GetDataAsyncTask extends AsyncTask<Void, Void, String> {
        String receiveMsg;

        @Override
        protected String doInBackground(Void... voids) {
            // 서버로부터 데이터를 가져오는 네트워크 작업 수행
            try {
                Log.i("woo","서버 연결 시도");
                URL url = new URL("http://"+HOST_ADDRESS+":"+HOST_PORT+"/board/appBoardList");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // 인코딩 및 요청 헤더 설정
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("GET");

                int rsnum = conn.getResponseCode();
                Log.i("woo",rsnum+"..");

                // 응답 코드 확인
                if (rsnum == HttpURLConnection.HTTP_OK) {
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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return receiveMsg;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Log.i("woo","데이터 받음"+result);

            if (result != null) {
                // 서버로부터 가져온 데이터를 처리하는 로직을 여기에 추가
//                Log.i("woo",result);
                try {
                    JSONArray jsonArray = new JSONArray(result); // 'result'는 서버 응답에서 얻은 JSON 문자열입니다.
                    int jslength = jsonArray.length();


                    Log.i("woo","jsarray 길이 : "+jslength);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        // 여기에서 필요한 데이터를 추출하고 처리합니다.
                        String idx = jsonObject.getString("idx");
                        String title = jsonObject.getString("title");
                        String content = jsonObject.getString("content");
                        String eno = jsonObject.getString("eNo");
                        String noticeyn = jsonObject.getString("notice_YN");
                        String viewCnt = jsonObject.getString("view_cnt");

                        // 이제 이 데이터를 사용할 수 있습니다.
                        Log.i("woo", "번호: " + idx + " 제목: " + title +
                                " 내용: " + content + " 사원 번호: " + eno+" notice_YN : "+noticeyn
                                +" view_cnt : "+viewCnt);

                        CustomItem item = new CustomItem(title, content);

                        item.setTitle(title);
                        item.setContent(content);
                        item.setNoticeyn(noticeyn);
                        item.setIdx(idx);
                        item.setEno(eno);
                        item.setView_cnt(viewCnt);

                        if("y".equals(noticeyn) || "Y".equals(noticeyn)){
                            comentDataList.add(item);
                        } else if ("n".equals(noticeyn) || "N".equals(noticeyn)) {
                            boardDataList.add(item);
                        }

                    }
                    Log.i("woo","게시글 불러오기 완료");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
                comentAdapter.notifyDataSetChanged(); // 만약 comentAdapter가 final이 아니라면 클래스 레벨 변수로 변경해야 합니다.

            } else {
                // 네트워크 오류 등으로 데이터를 가져오지 못한 경우
                Toast.makeText(MainSiteActivity.this, "데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}