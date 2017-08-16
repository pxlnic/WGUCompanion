package com.example.wgu_companion.wgucompanion;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CourseMentorCursorAdapter extends CursorAdapter {
    public CourseMentorCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView mentorTv = (TextView) view.findViewById(android.R.id.text1);

        //Get Course Mentor from Course/Mentor relationship list
        String mentorFilter = DBHelper.MENTOR_ID + "=" + cursor.getInt(cursor.getColumnIndex(DBHelper.MENTOR_COURSE_M_ID));

        Cursor mentorCursor = context.getContentResolver().query(CompanionContentProvider.MENTOR_URI, DBHelper.MENTOR_COLUMNS,
                mentorFilter, null, null);

        mentorCursor.moveToFirst();

        mentorCursor.moveToFirst();
        String mentorText = mentorCursor.getString(mentorCursor.getColumnIndex(DBHelper.MENTOR_NAME));

        mentorTv.setText(mentorText);
    }
}
