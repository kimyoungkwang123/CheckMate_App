package com.example.moble_project.test.master;

import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS;
import static com.example.moble_project.test.util.MyUtility.HOST_PORT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.moble_project.R;
import com.example.moble_project.test.BoardActivity;
import com.example.moble_project.test.MainSiteActivity;
import com.example.moble_project.test.util.AttendanceItem;
import com.example.moble_project.test.util.AttendanceItemAdapter;
import com.example.moble_project.test.util.CustomItem;
import com.example.moble_project.test.util.CustomItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;

public class BoardCheckActivity extends AppCompatActivity implements View.OnClickListener{
    private ArrayList<CustomItem> commentDataList;
    private ArrayList<CustomItem> normalDataList;
    String no;
    String name;
    String email;
    String phone;
    String grade;
    String state;
    String url;
    EditText searchBox;

    RadioButton commentcheck;
    RadioButton boardcheck;
    RadioButton allboard;
    Button searchBtn;
    Button backBtn;
    ListView boardcheckList;
    CustomItemAdapter adapter;
    ArrayList<CustomItem> boardDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_check);

        no = getIntent().getStringExtra("no");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        grade = getIntent().getStringExtra("grade");
        state = getIntent().getStringExtra("state");
        url = getIntent().getStringExtra("file_url");

        Log.i("woo",no+", "+name+", "+state );

        boardcheckList = findViewById(R.id.boardcheckList);

        commentcheck = findViewById(R.id.commentcheck);
        boardcheck = findViewById(R.id.boardcheck);
        allboard = findViewById(R.id.allboard);

        searchBtn = findViewById(R.id.searchBtn);
        backBtn = findViewById(R.id.backBtn);

        searchBox = findViewById(R.id.searchBox);

        boardDataList = new ArrayList<>();
        adapter = new CustomItemAdapter(this, android.R.layout.simple_list_item_1,boardDataList);
        boardcheckList.setAdapter(adapter);

        commentDataList = new ArrayList<>();
        normalDataList = new ArrayList<>();

        new GetDataAsyncTask().execute();

        boardcheckList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭한 아이템의 내용을 가져옴
                CustomItem selectedItem = (CustomItem) parent.getItemAtPosition(position);
                String selectedItemString = selectedItem.toString(); // 또는 CustomItem 클래스에 정의된 다른 문자열 반환 메서드
                // 다음 화면으로 내용 전달
                Intent intent = new Intent(BoardCheckActivity.this, DetailBoardCheck.class);
                intent.putExtra("title", selectedItem.getTitle());
                intent.putExtra("content", selectedItem.getContent());
                intent.putExtra("eNo",selectedItem.getEno());
                intent.putExtra("noticeyn",selectedItem.getNoticeyn());
                intent.putExtra("idx",selectedItem.getIdx());

                intent.putExtra("file_URL",selectedItem.getFile_URL());
                intent.putExtra("file_NAME",selectedItem.getFile_NAME());


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

        commentcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateListView(commentDataList);
                }
            }
        });

        boardcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    updateListView(normalDataList);
                }
            }
        });

        allboard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    adapter.clear();
                    adapter.addAll(boardDataList);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        backBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int rs = v.getId();

        if(rs == R.id.backBtn){
            Intent intent = new Intent(BoardCheckActivity.this, MasterActivity.class);

            intent.putExtra("no",no);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);
            intent.putExtra("state",state);
            intent.putExtra("grade",grade);
            intent.putExtra("file_url",url);

            startActivity(intent);
            finish();

        } else if (rs == R.id.searchBtn) {
            search();
        }
    }



    private void updateListView(ArrayList<CustomItem> newList) {
        adapter = new CustomItemAdapter(this, android.R.layout.simple_list_item_1, newList);
        boardcheckList.setAdapter(adapter);
    }

    public void search(){

        String searchword = searchBox.getText().toString().toLowerCase(); // 검색어 가져오기

        // 검색 결과를 저장할 새 리스트 생성
        ArrayList<CustomItem> filteredList = new ArrayList<>();

        // 원본 데이터 리스트에서 검색어가 포함된 아이템을 필터링하여 새 리스트에 추가
        for (CustomItem item : boardDataList) {
            if (item.getTitle().toLowerCase().contains(searchword) || item.getContent().toLowerCase().contains(searchword)) {
                filteredList.add(item);
            }
        }

        // 어댑터의 데이터를 필터링된 결과로 교체하고 UI 갱신
        adapter.clear();
        adapter.addAll(filteredList);
        adapter.notifyDataSetChanged();

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
//                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
//                    BufferedReader reader = new BufferedReader(tmp);
//                    // 데이터 저장
//                    StringBuffer buffer = new StringBuffer();
//
//                    String str;
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

                        // file_NAME과 file_URL 정보를 추출
                        String file_NAME = jsonObject.getString("file_NAME");
                        String file_URL = jsonObject.getString("file_URL");


                        // 이제 이 데이터를 사용할 수 있습니다.
                        Log.i("woo", "번호: " + idx + " 제목: " + title +
                                " 내용: " + content + " 사원 번호: " + eno+" notice_yn : "+noticeyn);

                        CustomItem item = new CustomItem();

                        item.setTitle(title);
                        item.setContent(content);
                        item.setEno(eno);
                        item.setNoticeyn(noticeyn);
                        item.setFile_NAME(file_NAME);  // 설정
                        item.setFile_URL(file_URL);    // 설정
                        item.setIdx(idx);

                        boardDataList.add(item);

                        if ("y".equalsIgnoreCase(item.getNoticeyn())) {
                            commentDataList.add(item);
                        } else {
                            normalDataList.add(item);
                        }
                    }
                    Log.i("woo","게시글 불러오기 완료");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();

            } else {
                // 네트워크 오류 등으로 데이터를 가져오지 못한 경우
                Toast.makeText(BoardCheckActivity.this, "데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        }

    }

}