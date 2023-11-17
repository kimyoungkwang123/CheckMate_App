package com.example.moble_project.test.util;

import static com.example.moble_project.test.util.MyUtility.HOST_ADDRESS;
import static com.example.moble_project.test.util.MyUtility.HOST_PORT;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moble_project.R;
import com.example.moble_project.test.MainSiteActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CustomItemAdapter2 extends ArrayAdapter<CustomItem> {
    private Context context;
    private List<CustomItem> items;


    public CustomItemAdapter2(Context context, int resource, List<CustomItem> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // 여기서는 custom layout을 사용해야 합니다.
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_with_delete, parent, false);
        }
        SharedPreferences sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE);
        String name = sharedPref.getString("name", null);

        // 데이터 가져오기
        CustomItem currentItem = getItem(position);
        Log.i("woo","댓글 작성한 사원 : "+currentItem.getEmployee_reply_name());

        // 뷰 참조 가져오기
        TextView textView = convertView.findViewById(R.id.item_text); // 수정된 ID를 사용합니다.
//        TextView textView = convertView.findViewById(R.id.item_text2); // 수정된 ID를 사용합니다.
        Button deleteButton = convertView.findViewById(R.id.delete_button);

        if ( name.equals(currentItem.getEmployee_reply_name())){
            deleteButton.setVisibility(View.VISIBLE);
        }else {
            deleteButton.setVisibility(View.INVISIBLE);
        }


        // 뷰에 데이터 설정
//        if(currentItem.getEmployee_reply_name() == null) {
//            textView.setText("댓글이 없습니다.");
//        }else {
//            String fullText = "  "+currentItem.getReply_content() +"\n"
//                    +" 작성 일자 : "+currentItem.getReply_update_time();
//            textView.setText(fullText);
//        }
        String fullText = "  "+currentItem.getReply_content() +"\n"
                +" 작성 일자 : "+currentItem.getReply_update_time();
        textView.setText(fullText);

        // 삭제 버튼에 클릭 리스너 설정
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 선택한 아이템의 ID 또는 고유 식별자 가져오기. 이 예제에서는 'idx'라고 가정합니다.
                CustomItem itemToRemove = items.get(position);
                String idx = String.valueOf(itemToRemove.getReply_no()); // getId() 메서드는 CustomItem 클래스에 있는 실제 메서드에 따라 다릅니다.

                // AsyncTask를 실행하여 서버에 삭제 요청을 보냅니다.
                new SendIdxAsyncTask().execute(currentItem.getReply_no());

                // 여기서 아이템 삭제
                items.remove(position);
                notifyDataSetChanged(); // 리스트뷰에 변경 사항을 알립니다.
            }
        });

        return convertView;
    }

    private class SendIdxAsyncTask extends AsyncTask<String, Void, String> {
        String sendMsg;
        String receiveMsg;
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("http://"+HOST_ADDRESS+":"+HOST_PORT+"/board/replydeleteAPP");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // 인코딩
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // 데이터 전송 준비
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "no=" + strings[0];

                osw.write(sendMsg);
                osw.flush();
                Log.i("woo","앱에서 보내는 값2 : "+sendMsg);
                int rsnum = conn.getResponseCode();
                Log.i("woo",rsnum+"..");
                if (rsnum == HttpURLConnection.HTTP_OK) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }

            return receiveMsg;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            Log.i("woo","데이터 받음"+result);

            if (result != null) {
                // 서버로부터 가져온 데이터를 처리하는 로직을 여기에 추가
                Toast.makeText(CustomItemAdapter2.this.getContext(), "삭제 완료.", Toast.LENGTH_SHORT).show();

            } else {
                // 네트워크 오류 등으로 데이터를 가져오지 못한 경우
                Toast.makeText(CustomItemAdapter2.this.getContext(), "데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}