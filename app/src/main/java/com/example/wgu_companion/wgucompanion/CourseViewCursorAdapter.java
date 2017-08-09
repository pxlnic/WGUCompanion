package com.example.wgu_companion.wgucompanion;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CourseViewCursorAdapter extends CursorAdapter {
    private static final String TERM_PREFS = "Term_Prefs";

    public CourseViewCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_course, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        cursor.moveToFirst();

        TextView courseNameTv = (TextView) view.findViewById(R.id.course_item_name_text);
        TextView courseStartTv = (TextView) view.findViewById(R.id.course_item_dates_text);
        TextView courseStatusTv = (TextView) view.findViewById(R.id.course_item_status_text);

        String nameText = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_NAME));
        String dateText = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_START_DATE));
        String statusText = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_STATUS));
/*        Log.d("Load Data", "Course Name Loaded: " + nameText);

        Log.d("Load Data", "Course Start Date Loaded Loaded: " + dateText);
        String[] array = cursor.getColumnNames();
        for(int i = 0; i < array.length; i++) {
            Log.d("Load Data", "Status Column: " + array[i]);
            Log.d("Load Data", "Row Data: " + cursor.getString(i));
        }*/


        courseNameTv.setText(nameText);
        courseStartTv.setText(dateText);
        courseStatusTv.setText(statusText);
    }
}
