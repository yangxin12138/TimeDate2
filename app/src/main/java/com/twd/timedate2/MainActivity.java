package com.twd.timedate2;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.twd.timedate2.dialog.DatePickerDialog;
import com.twd.timedate2.dialog.TimePickerDialog;
import com.twd.timedate2.dialog.TimeZoneDialog;
import com.twd.timedate2.interfaces.DateSelectedInterface;
import com.twd.timedate2.interfaces.OnTimeZoneSelectedListener;
import com.twd.timedate2.interfaces.TimeSelectedInterface;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity  implements View.OnClickListener ,
        TimeSelectedInterface, DateSelectedInterface
        , OnTimeZoneSelectedListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private DateTimeUtils utils;
    private Context context = this;
    private LinearLayout LL_Time;
    private LinearLayout LL_Date;
    private LinearLayout LL_TimeSwitch;
    private LinearLayout LL_is24HoursSwitch;
    private LinearLayout LL_timeZone;
    private TextView time_summary;
    private TextView date_summary;
    private TextView timeZone_summary;
    private TextView time_title;
    private TextView date_title;
    private TextView timeZone_title;
    private SwitchCompat switch_time;
    private SwitchCompat switch_24Hours;
    private Handler timerHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTheme(R.style.Theme_KapokWhite);
        setContentView(R.layout.activity_main);
        utils = new DateTimeUtils(this);
        initView();
        updateTimeRunnable.run();
    }
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            getSystemTime();
            //每隔一秒更新一次时间
            timerHandler.postDelayed(this,1000);
        }
    };

    private void initView(){
        //LL
        LL_Time = findViewById(R.id.LL_Time);
        LL_Date = findViewById(R.id.LL_Date);
        LL_TimeSwitch = findViewById(R.id.LL_TimeSwitch);
        LL_is24HoursSwitch = findViewById(R.id.LL_is24Hours);
        LL_timeZone = findViewById(R.id.LL_TImeZone);
        //summary
        time_summary = findViewById(R.id.time_summary);date_summary = findViewById(R.id.date_summary);timeZone_summary = findViewById(R.id.timeZone_summary);
        //title
        time_title = findViewById(R.id.time_title);date_title = findViewById(R.id.date_title);timeZone_title = findViewById(R.id.timeZone_summary);
        //switch
        switch_time = findViewById(R.id.switch_auto);switch_24Hours = findViewById(R.id.switch_24Hours);
        switch_time.setChecked(utils.isNetworkTimeEnabled());
        switch_24Hours.setChecked(utils.is24HoursEnabled());
        //clicklistener
        LL_Time.setOnClickListener(this);
        LL_TimeSwitch.setOnClickListener(this);
        LL_is24HoursSwitch.setOnClickListener(this);
        LL_Date.setOnClickListener(this);
        LL_timeZone.setOnClickListener(this);
        LL_TimeSwitch.requestFocus();

        if (switch_time.isChecked()){
            time_title.setTextColor(getResources().getColor(R.color.auto_time_checked)); time_summary.setTextColor(getResources().getColor(R.color.auto_time_checked));
            date_title.setTextColor(getResources().getColor(R.color.auto_time_checked));  date_summary.setTextColor(getResources().getColor(R.color.auto_time_checked));
            LL_Time.setFocusable(false); LL_Date.setFocusable(false);
        }else {
            time_title.setTextColor(getResources().getColor(R.color.black)); time_summary.setTextColor(getResources().getColor(R.color.index_summary));
            date_title.setTextColor(getResources().getColor(R.color.black)); date_summary.setTextColor(getResources().getColor(R.color.index_summary));
            LL_Time.setFocusable(true); LL_Date.setFocusable(true);
        }

        switch_time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 更新系统设置
                setUseNetworkTime(isChecked);
            }
        });

        switch_24Hours.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //更改系统设置
                setUse24HoursTime(isChecked);
            }
        });
    }
    private void getSystemTime(){
        //获取当前时间和日期
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        //设置日期的格式
        TimeZone timeZone = calendar.getTimeZone();
        String timeZoneId = timeZone.getID();
        DateFormat dateFormat;
        if ("Asia/Shanghai".equals(timeZoneId)){
            dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        }else {
            dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        }
        String formatterDate = dateFormat.format(currentDate);

        String dayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault()).format(currentDate);
        String finalFormatterDate = dayOfWeek+"\n"+formatterDate;

        String timeFormatString = DateTimeUtils.getTimeFormat(this);
        //设置时间的格式
        DateFormat timeFormat = new SimpleDateFormat(timeFormatString);
        String formatterTime = timeFormat.format(currentDate);

        // 获取当前时区的完整名称
        String timeZoneDisplayName = utils.getTimeZoneList().get(timeZoneId);

        // 计算时区偏移量，并格式化为"+HH:mm"的形式
        String timeZoneInfo = timeZoneDisplayName +"\n"+ " GMT " +
                String.format("%s%02d:%02d",
                        timeZone.getRawOffset() >= 0 ? "+" : "-",
                        Math.abs(timeZone.getRawOffset()) / 3600000,
                        Math.abs(timeZone.getRawOffset() % 3600000) / 60000);
        //在TextView上更新日期和时间
        time_summary.setText(formatterTime);
        date_summary.setText(formatterDate);
        timeZone_summary.setText(timeZoneInfo);
    }

    private void setUse24HoursTime(boolean enabled){
        //更新系统设置
        if (Settings.System.putInt(getContentResolver(),Settings.System.TIME_12_24,enabled ? 24 : 12)){
            Log.i(TAG, "setUse24HoursTime: is24Hours + " + (enabled ? "24" : "12"));
        }else {
            Log.i(TAG, "setUse24HoursTime: Failed to set 24Hours");
        }
    }

    private void  setUseNetworkTime(boolean enabled){
        //更新系统设置
        if (Settings.Global.putInt(getContentResolver(),Settings.Global.AUTO_TIME, enabled? 1:0)){
            Log.i(TAG, "setUseNetworkTime: Network time " + (enabled ? "enabled":"disabled"));
        }else {
            Log.i(TAG, "setUseNetworkTime: Failed to update setting");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.LL_Time){
            showTimeDialog();
        }else if (v.getId() == R.id.LL_TimeSwitch){
            switch_time.setChecked(!switch_time.isChecked());
            if (switch_time.isChecked()){
                time_title.setTextColor(getResources().getColor(R.color.auto_time_checked)); time_summary.setTextColor(getResources().getColor(R.color.auto_time_checked));
                date_title.setTextColor(getResources().getColor(R.color.auto_time_checked)); date_summary.setTextColor(getResources().getColor(R.color.auto_time_checked));
                LL_Time.setFocusable(false); LL_Date.setFocusable(false);
            }else {
                time_title.setTextColor(getResources().getColor(R.color.black)); time_summary.setTextColor(getResources().getColor(R.color.index_summary));
                date_title.setTextColor(getResources().getColor(R.color.black)); date_summary.setTextColor(getResources().getColor(R.color.index_summary));
                LL_Time.setFocusable(true); LL_Date.setFocusable(true);
            }
        } else if (v.getId() == R.id.LL_is24Hours) {
            switch_24Hours.setChecked(!switch_24Hours.isChecked());
        } else if (v.getId() == R.id.LL_Date) {
            showDateDialog();
        } else if (v.getId() == R.id.LL_TImeZone) {
            showTimeZoneDialog();
        }
    }

    private void showTimeDialog(){
        TimePickerDialog dialog = new TimePickerDialog(this,this);
        dialog.show();
    }

    private void showDateDialog(){
        DatePickerDialog dialog = new DatePickerDialog(this,this);
        dialog.show();
    }

    private void showTimeZoneDialog(){
        TimeZoneDialog dialog = new TimeZoneDialog(this,this);
        dialog.show();
    }
    @Override
    public void onDateSelected(String date) {
        try {
            // 使用"/"分割日期字符串
            String[] parts = date.split("/");
            if (parts.length == 3) {
                // 分别解析年、月、日
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                Calendar calendar =  Calendar.getInstance();
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month-1);
                calendar.set(Calendar.DAY_OF_MONTH,day);

                long when = calendar.getTimeInMillis();
                if (when / 1000 < Integer.MAX_VALUE){
                    ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(when);
                }
                date_summary.setText(date);

            } else {
                // 如果日期格式不正确，抛出异常或处理错误
                throw new IllegalArgumentException("Date format should be yyyy/MM/dd");
            }
        }catch (NumberFormatException e) {
            // 如果解析整数失败，打印错误信息
            System.out.println("Error parsing date components to integers: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // 如果日期格式不正确，打印错误信息
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void onTimeZoneSelected(String timeZoneId) {
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = calendar.getTimeZone();
        // 获取当前时区的完整名称
        String timeZoneDisplayName = utils.getTimeZoneList().get(timeZoneId);

        // 计算时区偏移量，并格式化为"+HH:mm"的形式
        String timeZoneInfo = timeZoneDisplayName +"\n"+ " GMT " +
                String.format("%s%02d:%02d",
                        timeZone.getRawOffset() >= 0 ? "+" : "-",
                        Math.abs(timeZone.getRawOffset()) / 3600000,
                        Math.abs(timeZone.getRawOffset() % 3600000) / 60000);
        timeZone_summary.setText(timeZoneInfo);
    }

    @Override
    public void onTimeSelected(String time) {
        Log.i(TAG, "onTimeSelected: the time is "+ time);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault()); // 24小时制时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // 日期格式
        Date userTime = null;
        try {
            userTime = timeFormat.parse(time);
        }catch (ParseException e){
            timeFormat.applyPattern("hh:mm a");
            try {
                userTime = timeFormat.parse(time);
            }catch (ParseException exception){
                e.printStackTrace();
                Log.e(TAG, "Failed to parse time", exception);
                return; // 如果时间格式不正确，返回并结束方法
            }
        }
        try {
            //获取当前日期
            String currentDateStr = dateFormat.format(new Date());
            Date currentDate = dateFormat.parse(currentDateStr);

            // 创建一个新的Date对象，使用当前日期和用户选择的时间
            Date newDateWithUserTime = new Date(currentDate.getTime());
            newDateWithUserTime.setHours(userTime.getHours());
            newDateWithUserTime.setMinutes(userTime.getMinutes());

            // 将新的时间设置为系统时间
            long timeInMillis = newDateWithUserTime.getTime();
            SystemClock.setCurrentTimeMillis(timeInMillis);
            Log.i(TAG, "New time in milliseconds: " + timeInMillis);
            // 设置UI上的时间显示
            time_summary.setText(time);
        }catch (ParseException e){
            e.printStackTrace();
        }
    }
}