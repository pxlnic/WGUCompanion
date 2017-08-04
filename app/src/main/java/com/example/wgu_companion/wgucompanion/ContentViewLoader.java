package com.example.wgu_companion.wgucompanion;

import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.Uri;
import android.util.Log;

public class ContentViewLoader {
    public ContentViewLoader(){

    }

    //Program Data
    private String program = "";
    private int programId = 0;
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

    public int loadProgramID(Context c){
        programId = 0;
        Uri uri = CompanionContentProvider.PROGRAM_URI;
        String filter = DBHelper.PROGRAM_ID + "=" + 1;
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.PROGRAM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        programId = cursor.getInt(cursor.getColumnIndex(DBHelper.PROGRAM_ID));
        Log.d("Load Data", "Program ID: " + programId);

        return programId;
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
        total = 0;
        Uri uri = CompanionContentProvider.COURSE_URI;
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, null, null, null);
        cursor.moveToFirst();
        total = total + cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_CU_COUNT));
        Log.d("Load Data", "Total CUs: " + total);

        return total;
    }

    //Term Overview Data


    //Term Detail Data
    String term = "";
    String termStart = "";
    String termEnd = "";
    int termCompleted = 0;
    int termTotal = 0;

    public String loadTermName(Context c, Uri id){
        term = "";
        Uri uri = CompanionContentProvider.TERM_URI;
        String filter = DBHelper.TERM_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.TERM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        term = cursor.getString(cursor.getColumnIndex(DBHelper.TERM_NAME));
        Log.d("Load Data", "Term Name: " + term);

        return term;
    }

    public String loadTermStart(Context c, Uri id){
        termStart = "";
        Uri uri = CompanionContentProvider.TERM_URI;
        String filter = DBHelper.TERM_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.TERM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        termStart = cursor.getString(cursor.getColumnIndex(DBHelper.TERM_START_DATE));
        Log.d("Load Data", "Term Name: " + termStart);

        return termStart;
    }

    public String loadTermEnd(Context c, Uri id){
        termEnd = "";
        Uri uri = CompanionContentProvider.TERM_URI;
        String filter = DBHelper.TERM_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.TERM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        termEnd = cursor.getString(cursor.getColumnIndex(DBHelper.TERM_END_DATE));
        Log.d("Load Data", "Term Start Date: " + termEnd);

        return termEnd;
    }

    public int loadTermCompletedCU(Context c, Uri id){
        termCompleted = 0;
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_TERM_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
            if(cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_STATUS_ID)) == 3){
                termCompleted = termCompleted + cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_CU_COUNT));
            }
        }
        Log.d("Load Data", "Completed Term CUs: " + termCompleted);

        return termCompleted;
    }

    public int loadTermTotalCU(Context c, Uri id){
        termTotal = 0;
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_TERM_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        termTotal = termTotal + cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_CU_COUNT));
        Log.d("Load Data", "Total Term CUs: " + termTotal);

        return termTotal;
    }

    //Course Data

    //Assessment Data

    //Note Data

    //Date Conversions
    public String convertDate(String date) throws ParseException {
        SimpleDateFormat fromDate = new SimpleDateFormat("yyyy-mm-dd");
        fromDate.setLenient(false);
        SimpleDateFormat toDate = new SimpleDateFormat("MM - dd - yyyy");
        toDate.setLenient(false);

        Date temp = fromDate.parse(date);
        String formattedDate = toDate.format(temp);

        return formattedDate;
    }
}
