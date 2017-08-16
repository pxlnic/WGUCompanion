package com.example.wgu_companion.wgucompanion;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by NicR on 8/14/2017.
 */

public class AssessmentViewCursorAdapter extends CursorAdapter {
    public AssessmentViewCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_assessment, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Course ID
        int id = cursor.getInt(cursor.getColumnIndex(DBHelper.ASSESSMENT_COURSE_ID));
        String filter = DBHelper.COURSE_ID + "=" + id;
        Cursor courseNameCursor = context.getContentResolver().query(CompanionContentProvider.COURSE_URI, DBHelper.COURSE_COLUMNS, filter,
                null, null);
        courseNameCursor.moveToFirst();
        Log.d("Load Data", "Assessment Course Count: " + courseNameCursor.getCount());

        TextView courseNameTv = (TextView) view.findViewById(R.id.assessment_item_name_text);
        TextView assessmentTypeTv = (TextView) view.findViewById(R.id.assessment_item_type_text);
        TextView assessmentStatusTv = (TextView) view.findViewById(R.id.assessment_item_status_text);

        String nameText = courseNameCursor.getString(courseNameCursor.getColumnIndex(DBHelper.COURSE_NAME));
        Log.d("Load Data", "Course Name: " + nameText);
        String typeText = cursor.getString(cursor.getColumnIndex(DBHelper.ASSESSMENT_TYPE));
        String statusText = cursor.getString(cursor.getColumnIndex(DBHelper.ASSESSMENT_STATUS));

        courseNameTv.setText(nameText);
        assessmentTypeTv.setText(typeText);
        assessmentStatusTv.setText(statusText);
    }
}
