package com.example.wgu_companion.wgucompanion;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CourseViewCursorAdapter extends CursorAdapter {
    public CourseViewCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_course, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView courseNameTv = (TextView) view.findViewById(R.id.course_item_name_text);
        TextView courseStartTv = (TextView) view.findViewById(R.id.course_item_dates_text);
        TextView courseStatusTv = (TextView) view.findViewById(R.id.course_item_status_text);

        String nameText = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_NAME));
        String dateText = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_START_DATE));
        String statusText = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_STATUS));

        courseNameTv.setText(nameText);
        courseStartTv.setText(dateText);
        courseStatusTv.setText(statusText);
    }
}
