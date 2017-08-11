package com.example.wgu_companion.wgucompanion;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class TermCourseCursorAdapter extends CursorAdapter {

    public TermCourseCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_checkbox, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Get Term ID
        SharedPreferences passedUri = context.getSharedPreferences(TermsOverviewActivity.TERM_PREFS, 0);
        int termId = (int) passedUri.getLong("termUri", -1);

        //Set Items
        CheckBox courseChkBx = (CheckBox) view.findViewById(R.id.list_item_checkbox);
        TextView courseName = (TextView) view.findViewById(R.id.list_item_text);

        boolean check = false;
        int courseChecked = cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_TERM_ID));

        //Check if course is selected for term
        if(courseChecked == termId ){
            check = true;
        }

        //Check if course is complete
        if(cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_STATUS)).equals("Complete")){
            courseName.setPaintFlags(courseName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        //If complete but not part of term then not able to be checked
        if(!check && cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_STATUS)).equals("Complete")){
            courseChkBx.setClickable(false);
        }

        //Load Status
        String name = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_NAME));

        //Set Values
        courseChkBx.setChecked(check);
        courseName.setText(name);
    }
}
