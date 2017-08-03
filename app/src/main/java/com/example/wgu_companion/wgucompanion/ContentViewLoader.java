package com.example.wgu_companion.wgucompanion;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class ContentViewLoader {
    public ContentViewLoader(){

    }

    //Program Data
    private String program = "";
    private int completed = 0;
    private int total = 0;
    public String loadProgramName(Context c){
        program = "";
        Uri uri = CompanionContentProvider.PROGRAM_URI;
        String filter = DBHelper.PROGRAM_ID + "=" + 1;
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.PROGRAM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        program = cursor.getString(cursor.getColumnIndex(DBHelper.PROGRAM_DEGREE_TYPE));
        program = program + " - " + cursor.getString(cursor.getColumnIndex(DBHelper.PROGRAM_NAME));
        Log.d("Load Data", "Program Name: " + program);

        return program;
    }

    public int loadCompletedCU(Context c){
        completed = 0;
        Uri uri = CompanionContentProvider.COURSE_URI;
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, null, null, null);
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
            if(cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_STATUS_ID)) == 3){
                completed = completed + cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_CU_COUNT));
            }
        }
        Log.d("Load Data", "Completed CUs: " + completed);

        return completed;
    }

    public int loadTotalCU(Context c){
        Uri uri = CompanionContentProvider.COURSE_URI;
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, null, null, null);
        cursor.moveToFirst();
        total = total + cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_CU_COUNT));
        Log.d("Load Data", "Total CUs: " + total);

        return total;
    }



    //Term Data

    //Course Data

    //Assessment Data

    //Note Data
}
