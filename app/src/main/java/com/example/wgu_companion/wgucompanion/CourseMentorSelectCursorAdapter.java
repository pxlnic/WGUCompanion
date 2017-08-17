package com.example.wgu_companion.wgucompanion;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by NicR on 8/11/2017.
 */

public class CourseMentorSelectCursorAdapter extends CursorAdapter {
    public CourseMentorSelectCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_checkbox, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Get Course and Mentor IDs
        SharedPreferences passedUri = context.getSharedPreferences(CoursesOverviewActivity.COURSE_PREFS, 0);
        int courseId = (int) passedUri.getLong("courseUri", 0);
        int mentorId = cursor.getInt(cursor.getColumnIndex(DBHelper.MENTOR_ID));

        //Establish node connections
        CheckBox mentorChk = (CheckBox) view.findViewById(R.id.list_item_checkbox);
        TextView mentorTv = (TextView) view.findViewById(R.id.list_item_text);

        //Filter Course/Mentor Relationship
        String cmIDFilter = DBHelper.MENTOR_COURSE_M_ID + "=" + mentorId + " AND " +
                            DBHelper.MENTOR_COURSE_C_ID + "=" + courseId;

        Cursor cmCursor = context.getContentResolver().query(CompanionContentProvider.COURSE_MENTOR_URI, DBHelper.COURSE_MENTOR_COLUMNS,
                cmIDFilter, null, null);

        boolean check = false;
        if(cmCursor != null){
            int count = cmCursor.getCount();
            if(count == 0) {
                check = false;
            }
            else{
                check = true;
            }
        }

        //Set Mentor Name Text
        String mentorText = cursor.getString(cursor.getColumnIndex(DBHelper.MENTOR_NAME));

        //Bind values
        if(ContentViewLoader.getCourseAction().equals("insert")){
            mentorChk.setChecked(false);
        }
        else {
            mentorChk.setChecked(check);
        }
        mentorTv.setText(mentorText);
    }
}
