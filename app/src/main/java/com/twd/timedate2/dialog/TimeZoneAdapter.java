package com.twd.timedate2.dialog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twd.timedate2.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 15:13 2024/6/21
 */
public class TimeZoneAdapter  extends BaseAdapter {

    private Context context;
    private Map<String, String> timeZoneIds;
    private List<String> keyset;
    private int focusedItem = 0;
    String timeZoneId;

    public void setFocusedItem(int position){
        focusedItem = position;
        notifyDataSetChanged();
    }

    public TimeZoneAdapter(Context context, Map<String, String> timeZoneIds) {
        this.context = context;
        this.timeZoneIds = timeZoneIds;
        keyset = new ArrayList<>(timeZoneIds.keySet());
    }

    @Override
    public int getCount() {
        return timeZoneIds.size();
    }

    @Override
    public String getItem(int position) {
        timeZoneId = keyset.get(position);
        return timeZoneId;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.timezone_item,parent,false);
        }

        timeZoneId = keyset.get(position);
        Log.i("yangxin", "getView: 现在的时区是timeZoneId = "+ timeZoneId);
        String displayName =timeZoneIds.get(timeZoneId);
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

        TextView textViewTimeZoneName = convertView.findViewById(R.id.textViewTimeZoneName);
        TextView textViewTimeZoneOffset = convertView.findViewById(R.id.textViewTimeZoneOffset);
        LinearLayout root_LL = convertView.findViewById(R.id.time_zone_root);
        textViewTimeZoneName.setText(displayName);
        Log.i("yangxin", "getView: " + timeZoneId +"的名字是："+textViewTimeZoneName.getText().toString());
        textViewTimeZoneOffset.setText(formatOffset(timeZone.getRawOffset()));
        if (position == focusedItem){
            root_LL.setBackgroundResource(R.drawable.dialog_item_background);
        }else {
            root_LL.setBackgroundResource(0);

        }
        return convertView;
    }

    private String formatOffset(int offsetMillis) {
        int offsetHours = offsetMillis / 3600000; // 毫秒转换为小时
        int offsetMinutes = (offsetMillis % 3600000) / 60000; // 余下的毫秒转换为分钟
        // 使用String.format()方法格式化为 "GMT+HH:mm" 形式
        return String.format("GMT%+03d:%02d", offsetHours, offsetMinutes);
    }
}
