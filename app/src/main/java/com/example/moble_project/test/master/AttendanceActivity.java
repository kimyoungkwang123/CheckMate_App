package com.example.moble_project.test.master;

//import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS_2;
import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS;
import static com.example.moble_project.test.util.MyUtility.HOST_PORT;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moble_project.R;
import com.example.moble_project.test.login;
import com.example.moble_project.test.util.AttendanceItem;
import com.example.moble_project.test.util.AttendanceItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class AttendanceActivity extends AppCompatActivity {
    private ArrayList<AttendanceItem> attendanceDataList;
    private AttendanceItemAdapter adapter;
    private Button backBtn;
    private Button searchBtn;
    private RadioButton btnToday;
    private RadioButton btnYesterday;
    private RadioButton btnall;
    private TextView searchbox;
    private ListView attendancelist;
    String no, name, email, phone, grade, state, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        no = getIntent().getStringExtra("no");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");
        grade = getIntent().getStringExtra("grade");
        state = getIntent().getStringExtra("state");
        url = getIntent().getStringExtra("file_url");

        Log.i("woo", no + ", " + name + ", " + state);

        backBtn = findViewById(R.id.backBtn);
        searchBtn = findViewById(R.id.searchBtn);

        btnToday = findViewById(R.id.btn_today);
        btnYesterday = findViewById(R.id.btn_yester);
        btnall = findViewById(R.id.btn_all);

        searchbox = findViewById(R.id.searchBox);
        attendancelist = findViewById(R.id.attendancelist);

        attendanceDataList = new ArrayList<>();
        adapter = new AttendanceItemAdapter(this, android.R.layout.simple_list_item_1, attendanceDataList);
        attendancelist.setAdapter(adapter);
        new GetDataAsyncTask().execute();


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(searchbox.getText().toString());
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        attendancelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AttendanceItem selectedItem = attendanceDataList.get(i);
                String name = selectedItem.getName();
                Log.i("woo", name);
                Intent detailIntent = new Intent(AttendanceActivity.this, DetailActivity.class);
                detailIntent.putExtra("name", name);
                startActivity(detailIntent);
            }
        });

        btnToday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    displayTodayAttendance();
                }
            }
        });

        btnYesterday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    displayYesterdayAttendance();
                }
            }
        });

        btnall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    adapter.clear();
                    adapter.addAll(attendanceDataList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void search(String searchText) {
        ArrayList<AttendanceItem> filteredList = new ArrayList<>();
        for (AttendanceItem item : attendanceDataList) {
            if (item.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter = new AttendanceItemAdapter(this, android.R.layout.simple_list_item_1, filteredList);
        attendancelist.setAdapter(adapter);
    }

    private void back() {
        Intent intent = new Intent(AttendanceActivity.this, MasterActivity.class);

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

    private void displayTodayAttendance() {
        LocalDate currentDate = LocalDate.now();
        Log.d("woo", "todayCurrent Date: " + currentDate.toString());

        ArrayList<AttendanceItem> todayAttendance = new ArrayList<>();

        for (AttendanceItem item : attendanceDataList) {
            try {
                LocalDate entryDate = LocalDate.parse(item.getEntryDate());
                Log.d("woo", "todayEntry Date: " + entryDate.toString());

                if (entryDate.equals(currentDate)) {
                    todayAttendance.add(item);
                }
            } catch (DateTimeParseException e) {
                Log.e("woo", "EntryDate parsing error for value: " + item.getEntryDate());
                // 올바르지 않은 날짜 형식인 경우 처리 로직
            }
        }

        updateListView(todayAttendance);
    }

    private void displayYesterdayAttendance() {
        LocalDate currentDate = LocalDate.now();
        LocalDate yesterdayDate = currentDate.minusDays(1);
        ArrayList<AttendanceItem> yesterdayAttendance = new ArrayList<>();
        for (AttendanceItem item : attendanceDataList) {
            try {
                LocalDate entryDate = LocalDate.parse(item.getEntryDate());
                if (entryDate.equals(yesterdayDate)) {
                    yesterdayAttendance.add(item);
                }
            } catch (DateTimeParseException e) {
                // 예외 발생시 로그를 남기고, 해당 항목은 리스트에 추가하지 않음
                Log.e("woo", "Attendance Error parsing entry date: " + item.getEntryDate(), e);
            }
        }
        updateListView(yesterdayAttendance);
    }


    private void updateListView(ArrayList<AttendanceItem> newList) {
        adapter = new AttendanceItemAdapter(this, android.R.layout.simple_list_item_1, newList);
        attendancelist.setAdapter(adapter);
    }

    private class GetDataAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String receiveMsg = "";
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://" + HOST_ADDRESS + ":" + HOST_PORT + "/api/attendance/attendance/list");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                receiveMsg = buffer.toString();

                Log.i("woo", "받은 값 : "+receiveMsg);
            } catch (SocketTimeoutException e) {
                Log.e("woo", "Socket Timeout", e);
            } catch (UnknownHostException e) {
                Log.e("woo", "Unknown Host", e);
            } catch (IOException e) {
                Log.e("woo", "IO Exception", e);
            } catch (Exception e) {
                Log.e("woo", "Unknown error", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return receiveMsg;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray jsonArray = new JSONArray(result);
                Log.i("woo",result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String name = jsonObject.isNull("name") ? "N/A" : jsonObject.getString("name");
                    String entryDateTime = jsonObject.isNull("entry") ? "출근 전" : jsonObject.getString("entry");
                    String exitDateTime = jsonObject.isNull("exit_") ? "퇴근 전" : jsonObject.getString("exit_");
                    String entryDate = entryDateTime.equals("출근 전") ? "출근 전" : entryDateTime.split("T")[0];
                    String entryTime = entryDateTime.equals("출근 전") ? "출근 전" : entryDateTime.split("T")[1];
                    String exitDate = exitDateTime.equals("퇴근 전") ? "퇴근 전" : exitDateTime.split("T")[0];
                    String exitTime = exitDateTime.equals("퇴근 전") ? "퇴근 전" : exitDateTime.split("T")[1];

                    AttendanceItem item = new AttendanceItem();
                    item.setExitDate(exitDate);
                    item.setExitTime(exitTime);
                    item.setEntryDate(entryDate);
                    item.setEntryTime(entryTime);
                    item.setName(name);

                    attendanceDataList.add(item);
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("JSONError", "JSON parsing error", e);
            }
        }
    }
}