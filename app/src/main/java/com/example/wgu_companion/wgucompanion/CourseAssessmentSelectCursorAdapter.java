package com.example.wgu_companion.wgucompanion;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by NicR on 8/11/2017.
 */

public class CourseAssessmentSelectCursorAdapter extends CursorAdapter {

    public CourseAssessmentSelectCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_checkbox, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Get Course ID
        SharedPreferences passedUri = context.getSharedPreferences(CoursesOverviewActivity.COURSE_PREFS, 0);
        int courseId = (int) passedUri.getLong("courseUri", 0);
        boolean check = false;

        //Get Cursor Assessment Course IDs that match this course
        SharedPreferences pref = context.getSharedPreferences(CoursesOverviewActivity.COURSE_PREFS, 0);
        Long id = pref.getLong("courseUri", 0);
        String dialogCourseAssessmentFilter = DBHelper.ASSESSMENT_COURSE_ID + "=" + id;

        Cursor assessmentCursor = context.getContentResolver().query(CompanionContentProvider.ASSESSMENT_URI, DBHelper.ASSESSMENT_COLUMNS,
                dialogCourseAssessmentFilter, null, null);
        assessmentCursor.moveToFirst();

        //Get Assessment Type
        String assessmentTypeText = cursor.getString(cursor.getColumnIndex(DBHelper.ASSESSMENT_TYPE_NAME));

        for(int i=0; i<assessmentCursor.getCount(); i++){
            int aCourseId = assessmentCursor.getInt(assessmentCursor.getColumnIndex(DBHelper.ASSESSMENT_COURSE_ID));
            String aTypeName = assessmentCursor.getString(assessmentCursor.getColumnIndex(DBHelper.ASSESSMENT_TYPE));
            if(courseId == aCourseId && assessmentTypeText.equals(aTypeName)){
                check = true;
            }
            assessmentCursor.moveToNext();
        }

        //Set Items
        CheckBox assessmentChkBx = (CheckBox) view.findViewById(R.id.list_item_checkbox);
        TextView assessmentType = (TextView) view.findViewById(R.id.list_item_text);

        //Load Status
        String name = cursor.getString(cursor.getColumnIndex(DBHelper.ASSESSMENT_TYPE_NAME));

        //Set Values
        assessmentChkBx.setChecked(check);
        assessmentType.setText(name);
    }
}
