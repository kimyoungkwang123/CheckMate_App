package com.example.moble_project.test;

import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS;
import static com.example.moble_project.test.util.MyUtility.HOST_PORT;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.moble_project.R;
//import com.example.moble_project.test.Camera.MainCamera;
import com.example.moble_project.test.Camera.MainCamera;
import com.example.moble_project.test.DTO.UserInfo;
import com.example.moble_project.test.master.MasterActivity;
import com.example.moble_project.test.util.MemoEditActivity;
import com.example.moble_project.test.util.MemoDbHelper;
import com.example.moble_project.test.util.LocationManagerHelper;

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
import java.util.List;

public class login extends AppCompatActivity implements View.OnClickListener {

    Button logOut_btn;
    Button commu_btn;
    Button btn_chul;
    Button btn_tae;
    Button btn_whe;
    Button btn_bok;
    Button masterBtn;

    String name;
    String email;
    String phone;
    String grade;
    String state;
    String no;
    String url;
    String responeClickBtn;
//    String reason = null;

    private ListView memoListView;
    private ArrayAdapter<String> memoListAdapter;
    private List<String> memoList; // 메모 제목을 저장하는 리스트
    private MemoDbHelper dbHelper; // 메모 데이터베이스 도우미 클래스
    UserInfo userinfo = new UserInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        no = getIntent().getStringExtra("no");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        grade = getIntent().getStringExtra("grade");
        state = getIntent().getStringExtra("state");
        url = getIntent().getStringExtra("file_url");

        Log.i("woo","사용자 정보 화면"+" 사용자 : "+name+" 사원 번호 : "+no+"..."+state);


        responeClickBtn = "0";

        userinfo.setNo(no);
        userinfo.setName(name);
        userinfo.setEmail(email);
        userinfo.setPhone(phone);
        userinfo.setGrade(grade);
        userinfo.setState(state);
        userinfo.setUrl(url);



        Log.i("woo","useringo : "+userinfo.toString());

        // UI 업데이트
        TextView nameTextView = findViewById(R.id.name);
        TextView emailTextView = findViewById(R.id.email);
        TextView gradeTextView = findViewById(R.id.grade);
        TextView phoneTextView = findViewById(R.id.numberId);

        callurl();

        nameTextView.setText(name);
        emailTextView.setText(email);
        phoneTextView.setText(phone);
        gradeTextView.setText(grade);

        logOut_btn = findViewById(R.id.logOutId);
        commu_btn = findViewById(R.id.commuId);
        masterBtn = findViewById(R.id.masterBtn);

        btn_chul = findViewById(R.id.btn_chul);//출근
        btn_tae = findViewById(R.id.btn_tae);//퇴근
        btn_whe = findViewById(R.id.btn_whe);//외출
        btn_bok = findViewById(R.id.btn_bok);//복귀

        commu_btn.setOnClickListener(this);
        logOut_btn.setOnClickListener(this);
        masterBtn.setOnClickListener(this);
        btn_chul.setOnClickListener(this);
        btn_tae.setOnClickListener(this);
        btn_whe.setOnClickListener(this);
        btn_bok.setOnClickListener(this);

        if(state.equals("2")){
            masterBtn.setVisibility(View.VISIBLE);
        } else {
            masterBtn.setVisibility(View.INVISIBLE);
        }

        // 메모 목록 초기화 및 어댑터 설정
        dbHelper = new MemoDbHelper(this);
        memoList = new ArrayList<>();
        memoListView = findViewById(R.id.memoListView);
        memoListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, memoList);
        memoListView.setAdapter(memoListAdapter);


        memoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 선택한 메모의 내용을 불러와 메모 편집 화면으로 전환
                String selectedMemoTitle = memoList.get(position);

                // 여기서 메모 ID를 가져와야 합니다.
                long memoId = dbHelper.getMemoId(selectedMemoTitle);

                // 선택한 메모의 ID를 MemoEditActivity로 전달하여 해당 메모를 불러옵니다.
                openMemoEditScreen(memoId);
            }
        });

    }

    public void callurl() {
        String imageUrl = url;
        ImageView imageView = findViewById(R.id.imageView2); // 이미지를 표시할 ImageView

        Glide.with(login.this)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // 이미지 로드 실패 시 처리
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
    }

    // 메모 편집 화면으로 이동하는 메서드
    private void openMemoEditScreen(long memoId) {
        Intent editMemoIntent = new Intent(login.this, MemoEditActivity.class);
        editMemoIntent.putExtra("memo_id", memoId); // 메모 ID를 MemoEditActivity로 전달
        startActivity(editMemoIntent);
    }

    @Override
    public void onClick(View v) {
        // 버튼 ID에 따라 적절한 메서드 호출
        if (v.getId() == R.id.commuId) {
            commu(); // 커뮤니티 관련 작업 처리
        } else if (v.getId() == R.id.logOutId) {
            logOut(); // 로그아웃 처리
        } else if (v.getId() == R.id.masterBtn) {
            master(); // 관리자 버튼 관련 작업 처리
        } else if (v.getId() == R.id.btn_chul) {
            userinfo.setClickBtn("1");
            location();
            Log.i("woo","출석 : "+userinfo.getClickBtn());

        } else if (v.getId() == R.id.btn_tae) {
            userinfo.setClickBtn("2");
            location();
            Log.i("woo","퇴근 : "+userinfo.getClickBtn());

        } else if (v.getId() == R.id.btn_whe) {
            userinfo.setClickBtn("3");
            showExitReasonDialog();
            Log.i("woo","외출 : "+userinfo.getClickBtn());
        } else if (v.getId() == R.id.btn_bok) {
            userinfo.setClickBtn("4");
            location();
            Log.i("woo","복귀 : "+userinfo.getClickBtn());

        }
    }

    public void location() {
        // LocationManagerHelper 인스턴스 생성
        Log.i("woo","위치 정보 요청");
        LocationManagerHelper locationManagerHelper = new LocationManagerHelper(this, this);

        // 목표 위치 설정 (예: 위도 37.424, 경도 -122.084의 위치). 실제 값을 사용해야 합니다.
        Location targetLocation = new Location("target");
        targetLocation.setLatitude(36.8108786);
        targetLocation.setLongitude(127.1483884);

        // 위치 확인 및 목표 위치와의 거리에 따라 액션 수행
        locationManagerHelper.checkLocationAndGoToActivity(MainCamera.class, targetLocation, userinfo);
    }

    public void master() {
        Log.i("woo","관리자 버튼 클릭");
        Intent intent = new Intent(login.this, MasterActivity.class);

        intent.putExtra("no", no);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("state", state);
        intent.putExtra("grade", grade);
        intent.putExtra("file_url", url);

        startActivity(intent);
    }

    public void commu() {
        Log.i("woo", "커뮤 버튼 클릭");
        Intent intent = new Intent(login.this, MainSiteActivity.class);

        intent.putExtra("no", no);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("state", state);
        intent.putExtra("grade", grade);
        intent.putExtra("file_url", url);

        startActivity(intent);
    }

    public void logOut() {
        Log.i("woo", "로그아웃 버튼 클릭");
        Intent intent = new Intent(login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면이 다시 포커스를 얻을 때 메모 목록을 업데이트합니다.
        updateMemoList();
    }

    private void updateMemoList() {
        memoList.clear();
        memoList.add("새 메모 만들기"); // 새 메모 만들기 항목 추가

        // 데이터베이스에서 메모 제목을 가져와서 목록에 추가합니다.
        List<String> memoTitles = dbHelper.getAllMemoTitles();
        memoList.addAll(memoTitles);

        // 어댑터에 변경을 알립니다.
        memoListAdapter.notifyDataSetChanged();
    }

    // 외출 사유를 입력받는 대화상자를 띄우는 메소드
    private void showExitReasonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("외출 사유 입력");

        // EditText 뷰를 설정하여 사용자 입력을 받을 수 있게 합니다.
        final EditText input = new EditText(this);
        input.setTextColor(Color.BLACK);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);

        // 확인 버튼을 설정합니다.
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String exitReason = input.getText().toString();
                Log.i("woo", "외출 사유: " + exitReason);
                userinfo.setReason(exitReason);

                location();
            }
        });
        // 취소 버튼을 설정합니다.
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show(); // 대화상자를 화면에 표시합니다.
    }


}
