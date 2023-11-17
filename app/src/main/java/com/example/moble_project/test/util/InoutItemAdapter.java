package com.example.moble_project.test.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.moble_project.R;

import java.util.List;

public class InoutItemAdapter extends ArrayAdapter<InoutItem> {
    private final Context context;
    private final List<InoutItem> items;

    public InoutItemAdapter(Context context, int resource, List<InoutItem> items) {
        super(context,resource,items);

        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.custom_inout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView1 = convertView.findViewById(R.id.text); // 변경: 실제 ID로 교체해야 함
            viewHolder.textView2 = convertView.findViewById(R.id.text2); // 변경: 실제 ID로 교체해야 함
            viewHolder.textView3 = convertView.findViewById(R.id.text3); // 변경: 실제 ID로 교체해야 함
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        InoutItem item = items.get(position);
        // 각 TextView 업데이트
        updateTextViews(viewHolder, item);

        return convertView;
    }

    private void updateTextViews(ViewHolder viewHolder, InoutItem item) {
        // 각 TextView의 텍스트 설정
        // ... [텍스트 설정 코드]
        String displayText1 = item.getName(); // 예시입니다. 실제 정보로 교체해야 합니다.
        String displayText2 = item.getOutTime(); // 예시입니다. 실제 정보로 교체해야 합니다.
        String displayText3 = item.getReturnTime() != null ? item.getReturnTime() : context.getString(R.string.not_available); // 예시입니다. 실제 정보로 교체해야 합니다.

        viewHolder.textView1.setText(displayText1);
        viewHolder.textView2.setText(displayText2);
        viewHolder.textView3.setText(displayText3);

//        // 색상 변경 로직, 필요에 따라 개별적으로 설정할 수 있음
//        if (item.getEntryTime() != null && !item.getEntryTime().isEmpty()) {
//            viewHolder.textView3.setTextColor(Color.RED); // 예시
//            // 다른 텍스트 뷰에 대한 조건들...
//        } else if (item.getExitTime() != null && !item.getExitTime().isEmpty()) {
//            viewHolder.textView2.setTextColor(Color.BLUE); // 예시
//            // 다른 텍스트 뷰에 대한 조건들...
//        } else {
//            viewHolder.textView1.setTextColor(Color.BLACK); // 예시
//            // 다른 텍스트 뷰에 대한 조건들...
//        }
    }

    // ViewHolder 클래스 내부에 세 개의 TextView 변수 추가
    static class ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView textView3;
    }
}
