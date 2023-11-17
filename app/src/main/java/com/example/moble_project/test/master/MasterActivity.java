package com.example.moble_project.test.master;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import com.example.moble_project.R;
import com.example.moble_project.test.MainActivity;
import com.example.moble_project.test.MainSiteActivity;
import com.example.moble_project.test.login;

public class MasterActivity extends AppCompatActivity implements View.OnClickListener{
    String no;
    String name;
    String email;
    String phone;
    String grade;
    String state;
    String url;
    //마이페이지 버튼, 로그아웃 버튼 커뮤 버튼
    Button menuBtn;

    Button boardcheck;
    Button inoutId;
    Button checkId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        no = getIntent().getStringExtra("no");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        grade = getIntent().getStringExtra("grade");
        state = getIntent().getStringExtra("state");
        url = getIntent().getStringExtra("file_url");

        Log.i("woo","관리자 페이지"+" 이름 : "+name+" 사원 번호 : "+no);

        menuBtn = findViewById(R.id.menuBtn);

        checkId = findViewById(R.id.checkId);
        inoutId = findViewById(R.id.inoutId);
        boardcheck = findViewById(R.id.boardcheck);

        checkId.setOnClickListener(this);
        inoutId.setOnClickListener(this);
        boardcheck.setOnClickListener(this);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int rs = v.getId();

        if(rs == R.id.checkId){
            Log.i("woo","출근 대장 버튼 클릭");
            //
            Intent intent = new Intent(MasterActivity.this, AttendanceActivity.class);

            intent.putExtra("no",no);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);
            intent.putExtra("state",state);
            intent.putExtra("grade",grade);
            intent.putExtra("file_url",url);

            startActivity(intent);
            finish();
        }else if (rs == R.id.inoutId){
            Log.i("woo","외출 일지 버튼 클릭");
            //
            Intent intent = new Intent(MasterActivity.this, InOutActivity.class);

            intent.putExtra("no",no);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);
            intent.putExtra("state",state);
            intent.putExtra("grade",grade);
            intent.putExtra("file_url",url);

            startActivity(intent);
            finish();
        }else if(rs == R.id.boardcheck){
            Log.i("woo","게시판 관리 버튼 클릭");
            //
            Intent intent = new Intent(MasterActivity.this, BoardCheckActivity.class);

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

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.inflate(R.menu.menulist); // 메뉴 리소스. 메뉴 아이템을 포함한 XML 파일이 필요합니다.
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.mypageitem) {
                    // "마이페이지" 아이템 클릭 시 다른 액티비티로 이동
                    Intent myPageIntent = new Intent(MasterActivity.this, login.class); // 'MyPageActivity.class' 실제 마이페이지 액티비티 클래스명으로 변경

                    myPageIntent.putExtra("no", no);
                    myPageIntent.putExtra("name", name);
                    myPageIntent.putExtra("email", email);
                    myPageIntent.putExtra("phone", phone);
                    myPageIntent.putExtra("state", state);
                    myPageIntent.putExtra("grade", grade);
                    myPageIntent.putExtra("file_url",url);

                    startActivity(myPageIntent);
//                    finish(); // 현재 액티비티를 종료
                    return true;
                } else if (itemId == R.id.commuitem) {
                    // "커뮤니티" 아이템 클릭 시 다른 액티비티로 이동
                    Intent communityIntent = new Intent(MasterActivity.this, MainSiteActivity.class); // 'CommunityActivity.class' 실제 커뮤니티 액티비티 클래스명으로 변경

                    communityIntent.putExtra("no", no);
                    communityIntent.putExtra("name", name);
                    communityIntent.putExtra("email", email);
                    communityIntent.putExtra("phone", phone);
                    communityIntent.putExtra("state", state);
                    communityIntent.putExtra("grade", grade);
                    communityIntent.putExtra("file_url",url);

                    startActivity(communityIntent);
//                    finish(); // 현재 액티비티를 종료
                    return true;
                } else if (itemId == R.id.logoutitem) {
                    // "로그아웃" 아이템 클릭 시 로그아웃 처리를 수행한 후 로그인 화면으로 이동
                    // 로그아웃 처리 코드 (예: 세션 종료, 사용자 정보 삭제 등)
                    Intent loginIntent = new Intent(MasterActivity.this, MainActivity.class); // 'LoginActivity.class' 실제 로그인 액티비티 클래스명으로 변경
                    startActivity(loginIntent);
                    finish(); // 현재 액티비티를 종료
                    return true;
                } else {
                    return false;
                }
            }
        });

        popup.show(); // 이 부분은 이 위치에 있어야 합니다.
    }
}