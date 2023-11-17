package com.example.moble_project.test.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomItemAdapter extends ArrayAdapter<CustomItem> {
    private Context context;
    private List<CustomItem> items;

    public CustomItemAdapter(Context context, int resource, List<CustomItem> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView textView = (TextView) rowView.findViewById(android.R.id.text1);
        CustomItem item = items.get(position);
        textView.setText(item.getTitle()); // 'getTitle()'은 CustomItem 클래스의 메서드로, 해당 데이터의 제목을 반환합니다.
        return rowView;
    }
}
