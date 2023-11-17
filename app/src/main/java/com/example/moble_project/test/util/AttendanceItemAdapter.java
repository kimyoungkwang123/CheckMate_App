package com.example.moble_project.test.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.moble_project.R;

import java.util.List;

public class AttendanceItemAdapter extends ArrayAdapter<AttendanceItem> {
    private final Context context;
    private final List<AttendanceItem> items;

    public AttendanceItemAdapter(Context context, int resource, List<AttendanceItem> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.custom_attendance, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView1 = convertView.findViewById(R.id.text); // 변경: 실제 ID로 교체해야 함
            viewHolder.textView2 = convertView.findViewById(R.id.text2); // 변경: 실제 ID로 교체해야 함
            viewHolder.textView3 = convertView.findViewById(R.id.text3); // 변경: 실제 ID로 교체해야 함
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AttendanceItem item = items.get(position);
        // 각 TextView 업데이트
        updateTextViews(viewHolder, item);

        return convertView;
    }

    private void updateTextViews(ViewHolder viewHolder, AttendanceItem item) {
        // 각 TextView의 텍스트 설정
        // ... [텍스트 설정 코드]
        String displayText1 = item.getName(); // 예시입니다. 실제 정보로 교체해야 합니다.
        String displayText2 = item.getEntryTime(); // 예시입니다. 실제 정보로 교체해야 합니다.
        String displayText3 = item.getExitTime() != null ? item.getExitTime() : context.getString(R.string.not_available); // 예시입니다. 실제 정보로 교체해야 합니다.

        viewHolder.textView1.setText(displayText1);
        viewHolder.textView2.setText(displayText2);
        viewHolder.textView3.setText(displayText3);

    }

    // ViewHolder 클래스 내부에 세 개의 TextView 변수 추가
    static class ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView textView3;
    }
}
