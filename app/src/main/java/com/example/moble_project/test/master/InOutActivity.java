package com.example.moble_project.test.master;

import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS;
//import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS2;
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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.moble_project.R;
import com.example.moble_project.test.util.AttendanceItem;
import com.example.moble_project.test.util.AttendanceItemAdapter;
import com.example.moble_project.test.util.InoutItem;
import com.example.moble_project.test.util.InoutItemAdapter;

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

public class InOutActivity extends AppCompatActivity {
    private ArrayList<InoutItem> inoutDataList;
    private InoutItemAdapter adapter;
    private Button backBtn;
    private Button searchBtn;
    private RadioButton btnToday;
    private RadioButton btnYesterday;
    private RadioButton btnall;
    private TextView searchbox;
    private ListView inoutlist;
    String no, name, email, phone, grade, state, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_out);

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
        inoutlist = findViewById(R.id.inoutlist);

        inoutDataList = new ArrayList<>();
        adapter = new InoutItemAdapter(this, android.R.layout.simple_list_item_1, inoutDataList);
        inoutlist.setAdapter(adapter);
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

        // Set a click listener for today's button



        inoutlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InoutItem selectedItem = inoutDataList.get(i);
                String name = selectedItem.getName();
                Log.i("woo", name);
                Intent detailIntent = new Intent(InOutActivity.this, InoutDetailActivity.class);
                detailIntent.putExtra("name", name);
                startActivity(detailIntent);
            }
        });

        btnToday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    displayTodayInout();
                }
            }
        });

        btnYesterday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    displayYesterdayInout();
                }
            }
        });

        btnall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    adapter.clear();
                    adapter.addAll(inoutDataList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void search(String searchText) {
        ArrayList<InoutItem> filteredList = new ArrayList<>();
        for (InoutItem item : inoutDataList) {
            if (item.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter = new InoutItemAdapter(this, android.R.layout.simple_list_item_1, filteredList);
        inoutlist.setAdapter(adapter);
    }

    private void back() {
        Intent intent = new Intent(InOutActivity.this, MasterActivity.class);

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

    private void displayTodayInout() {
        LocalDate currentDate = LocalDate.now();
        Log.d("woo", "Inout Current Date: " + currentDate.toString());

        ArrayList<InoutItem> todayInout = new ArrayList<>();
        for (InoutItem item : inoutDataList) {
            try {
                LocalDate entryDate = LocalDate.parse(item.getReturnDate());
                Log.d("woo", "Inout Entry Date: " + entryDate.toString());

                if (entryDate.equals(currentDate)) {
                    todayInout.add(item);
                }
            } catch (DateTimeParseException e) {
                // 예외 발생 시 로그를 남기고, 해당 항목은 리스트에 추가하지 않음
                Log.e("woo", "Inout Error parsing return date: " + item.getReturnDate(), e);
            }
        }
        updateListView(todayInout);
    }

    private void displayYesterdayInout() {
        LocalDate currentDate = LocalDate.now();
        LocalDate yesterdayDate = currentDate.minusDays(1);
        ArrayList<InoutItem> yesterdayInout = new ArrayList<>();
        for (InoutItem item : inoutDataList) {
            try {
                LocalDate entryDate = LocalDate.parse(item.getReturnDate());
                if (entryDate.equals(yesterdayDate)) {
                    yesterdayInout.add(item);
                }
            } catch (DateTimeParseException e) {
                // 예외 발생 시 로그를 남기고, 해당 항목은 리스트에 추가하지 않음
                Log.e("woo", "Inout Error parsing return date: " + item.getReturnDate(), e);
            }
        }
        updateListView(yesterdayInout);
    }

    private void updateListView(ArrayList<InoutItem> newList) {
        adapter = new InoutItemAdapter(this, android.R.layout.simple_list_item_1, newList);
        inoutlist.setAdapter(adapter);
    }

    private class GetDataAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String receiveMsg = "";
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://" + HOST_ADDRESS + ":" + HOST_PORT + "/api/outing/App/list");
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
                Log.e("NetworkError", "Socket Timeout", e);
            } catch (UnknownHostException e) {
                Log.e("NetworkError", "Unknown Host", e);
            } catch (IOException e) {
                Log.e("NetworkError", "IO Exception", e);
            } catch (Exception e) {
                Log.e("GeneralError", "Unknown error", e);
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
                    String returnDateTime = jsonObject.isNull("return_") ? "복귀 전" : jsonObject.getString("return_");
                    String outDateTime = jsonObject.isNull("outing") ? "미확인" : jsonObject.getString("outing");
                    String returnDate = returnDateTime.equals("복귀 전") ? "복귀 전" : returnDateTime.split("T")[0];
                    String returnTime = returnDateTime.equals("복귀 전") ? "복귀 전" : returnDateTime.split("T")[1];
                    String outDate = outDateTime.equals("미확인") ? "미확인" : outDateTime.split("T")[0];
                    String outTime = outDateTime.equals("미확인") ? "미확인" : outDateTime.split("T")[1];

                    InoutItem item = new InoutItem();
                    item.setOutDate(outDate);
                    item.setOutTime(outTime);
                    item.setReturnTime(returnTime);
                    item.setReturnDate(returnDate);
                    item.setName(name);

                    inoutDataList.add(item);
                }

                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Log.e("JSONError", "JSON parsing error", e);
            }
        }
    }
}