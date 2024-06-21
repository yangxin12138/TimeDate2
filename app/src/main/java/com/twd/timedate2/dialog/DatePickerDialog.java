package com.twd.timedate2.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import com.twd.timedate2.DateTimeUtils;
import com.twd.timedate2.R;
import com.twd.timedate2.interfaces.DateSelectedInterface;

import java.util.Calendar;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 15:04 2024/6/21
 */
public class DatePickerDialog extends Dialog implements View.OnClickListener, View.OnFocusChangeListener{
    String selectDate;
    private DatePicker datePicker;
    private Context mContext;
    private DateTimeUtils utils;
    private final static String TAG = "DatePickerDialog";
    private DateSelectedInterface dateSelectedInterface;
    String months[] = {"1", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "11", "12"};

    public DatePickerDialog(@NonNull Context context, DateSelectedInterface dateSelectedInterface) {
        super(context);
        mContext = context;
        this.dateSelectedInterface = dateSelectedInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_picker);

        datePicker = findViewById(R.id.datePicker);
        utils = new DateTimeUtils(mContext);
        // 设置最早可选日期为2010年1月1日
        Calendar calendarMin = Calendar.getInstance();
        calendarMin.set(2010,Calendar.JANUARY,1);
        long minDate = calendarMin.getTimeInMillis();
        datePicker.setMinDate(minDate);

        //设置最晚可选日期为2070年12月31日
        Calendar calendarMax = Calendar.getInstance();
        calendarMax.set(2070,Calendar.DECEMBER,31);
        long maxDate = calendarMax.getTimeInMillis();
        datePicker.setMaxDate(maxDate);

        // 获取DatePicker的年、月、日子控件视图
        NumberPicker yearPicker = ((NumberPicker) ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(0));
        NumberPicker monthPicker = ((NumberPicker) ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(1));
        NumberPicker dayPicker = ((NumberPicker) ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(2));
        ((NumberPicker) ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(0)).setDisplayedValues(months);

        datePicker.setCalendarViewShown(false);//隐藏日历视图
        datePicker.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        //获取当前的时间
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // 注意，Calendar.MONTH从0开始，即0代表1月
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        selectDate = String.format("%04d/%02d/%02d", year, month + 1, day);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    ((NumberPicker) ((ViewGroup) ((ViewGroup) datePicker.getChildAt(0)).getChildAt(0)).getChildAt(0)).setDisplayedValues(months);
                    int displayMonth = monthOfYear + 1;
                    selectDate = String.format("%04d/%02d/%02d",year,displayMonth ,dayOfMonth);
                    Log.i(TAG, "onDateChanged: 日期是 = "+selectDate);
                }
            });
        }
        datePicker.post(new Runnable() {
            @Override
            public void run() {datePicker.requestFocus();}});
        yearPicker.setOnClickListener(this); monthPicker.setOnClickListener(this); dayPicker.setOnClickListener(this);
        yearPicker.setOnFocusChangeListener(this); monthPicker.setOnFocusChangeListener(this); dayPicker.setOnFocusChangeListener(this);
    }
    @Override
    public void onClick(View v) {
        if (dateSelectedInterface != null){
            Log.i(TAG, "onClick: selectDate is Empty:"+selectDate.isEmpty());
            dateSelectedInterface.onDateSelected(selectDate);
        }
        dismiss();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            Log.i(TAG, "onFocusChange: 日期聚焦获取焦点");
            v.setBackgroundResource(R.drawable.dialog_item_background);
        }else {
            v.setBackgroundResource(0);
        }
    }
}
