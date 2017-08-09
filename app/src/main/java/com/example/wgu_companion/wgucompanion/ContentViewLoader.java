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
            if(cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_STATUS)).equals("Complete")){
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

//Term Detail Data
    String term = "";
    String termStart = "";
    String termEnd = "";
    String termStatus = "";
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
        Log.d("Load Data", "Term Start Date: " + termStart);

        return termStart;
    }

    public String loadTermEnd(Context c, Uri id){
        termEnd = "";
        Uri uri = CompanionContentProvider.TERM_URI;
        String filter = DBHelper.TERM_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.TERM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        termEnd = cursor.getString(cursor.getColumnIndex(DBHelper.TERM_END_DATE));
        Log.d("Load Data", "Term End Date: " + termEnd);

        return termEnd;
    }

    public String loadTermStatus(Context c, Uri id){
        termStatus = "";
        Uri uri = CompanionContentProvider.TERM_URI;
        String filter = DBHelper.TERM_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.TERM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        termStatus = cursor.getString(cursor.getColumnIndex(DBHelper.TERM_STATUS));
        Log.d("Load Data", "Term Status: " + termStatus);

        return termStatus;
    }

    public int loadTermCompletedCU(Context c, Uri id){
        termCompleted = 0;
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_TERM_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
            if(cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_STATUS)).equals("Complete")){
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

//Save New/Edited Term Data
    private int termStartCheck = -1;
    private int termEndCheck = -1;
    private int[] termCourseIds;

    public boolean loadTermStartReminder(Context c, Uri id){
        termStartCheck = -1;
        Uri uri = CompanionContentProvider.TERM_URI;
        String filter = DBHelper.TERM_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.TERM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        termStartCheck = cursor.getInt(cursor.getColumnIndex(DBHelper.TERM_START_REMINDER));

        boolean isChecked = false;
        if(termStartCheck == 1){
            isChecked = true;
        }
        Log.d("Load Date", "Term Start Reminder: " + isChecked);

        return isChecked;
    }

    public boolean loadTermEndReminder(Context c, Uri id){
        termEndCheck = -1;
        Uri uri = CompanionContentProvider.TERM_URI;
        String filter = DBHelper.TERM_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.TERM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        termEndCheck = cursor.getInt(cursor.getColumnIndex(DBHelper.TERM_END_REMINDER));

        boolean isChecked = false;
        if(termEndCheck == 1){
            isChecked = true;
        }
        Log.d("Load Date", "Term Start Reminder: " + isChecked);

        return isChecked;
    }

    public int[] loadCourseIds(Context c, TermCourseListAdapter a){
        termCourseIds = new int[a.getCount()];
        for(int i = 0; i < termCourseIds.length; i++){
            //termCourseIds[i] = a;
        }

        return termCourseIds;
    }

//Course Detail Data
    private String courseName = "";
    private String courseStart = "";
    private String courseEnd = "";
    private String courseStatus = "";
    private String courseDescription = "";

    public String loadCourseName(Context c, Uri id){
        courseName = "";
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        courseName = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_NAME));

        return courseName;
    }

    public String loadCourseStart(Context c, Uri id){
        courseStart= "";
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        courseStart = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_START_DATE));

        return courseStart;
    }

    public String loadCourseEnd(Context c, Uri id){
        courseEnd= "";
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        courseEnd = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_END_DATE));

        return courseEnd;
    }

    public String loadCourseStatus(Context c, Uri id){
        courseStatus= "";
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        courseStatus = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_STATUS));

        //courseStatus = loadStatus(c, courseStatusId);

        return courseStatus;
    }

    public String loadCourseDescription(Context c, Uri id){
        courseDescription= "";
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        courseDescription = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_DESCRIPTION));

        return courseDescription;
    }

//Save New/Edited Course Data

//Assessment Detail Data
    private String assessmentCourseName = "";
    private String assessmentType = "";
    private String assessmentDueDate = "";
    private String assessmentStatus = "";

    public String loadAssessmentCourseName(Context c, Uri id){
        assessmentCourseName = "";
        Uri uri = CompanionContentProvider.ASSESSMENT_URI;
        String filter = DBHelper.ASSESSMENT_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.ASSESSMENT_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        int assessmentCourseId = cursor.getInt(cursor.getColumnIndex(DBHelper.ASSESSMENT_COURSE_ID));

        Uri courseUri = Uri.parse(CompanionContentProvider.COURSE_URI + "/" + assessmentCourseId);
        assessmentCourseName = loadCourseName(c, courseUri);

        return assessmentCourseName;
    }

    public String loadAssessmentType(Context c, Uri id){
        assessmentType = "";
        Uri uri = CompanionContentProvider.ASSESSMENT_URI;
        String filter = DBHelper.ASSESSMENT_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.ASSESSMENT_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        assessmentType = cursor.getString(cursor.getColumnIndex(DBHelper.ASSESSMENT_TYPE));

        //assessmentType = loadType(c, assessmentTypeId);

        return assessmentType;
    }

    public String loadAssessmentDueDate(Context c, Uri id){
        assessmentDueDate = "";
        Uri uri = CompanionContentProvider.ASSESSMENT_URI;
        String filter = DBHelper.ASSESSMENT_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.ASSESSMENT_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        assessmentDueDate = cursor.getString(cursor.getColumnIndex(DBHelper.ASSESSMENT_DUE_DATE));

        return assessmentDueDate;
    }

    public String loadAssessmentStatus(Context c, Uri id){
        assessmentStatus = "";
        Uri uri = CompanionContentProvider.ASSESSMENT_URI;
        String filter = DBHelper.ASSESSMENT_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.ASSESSMENT_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        assessmentStatus = cursor.getString(cursor.getColumnIndex(DBHelper.ASSESSMENT_STATUS));

        //assessmentStatus = loadStatus(c, statusId);

        return assessmentStatus;
    }

//Save New/Edited Assessment Data

//Note Data
    private String noteTitle = "";
    private String noteText = "";

    public String loadNoteTitle(Context c, Uri id){
        noteTitle = "";
        Uri uri = CompanionContentProvider.NOTE_URI;
        String filter = DBHelper.NOTE_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.NOTE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        noteTitle = cursor.getString(cursor.getColumnIndex(DBHelper.NOTE_TITLE));

        return noteTitle;
    }

    public String loadNoteText(Context c, Uri id){
        noteText = "";
        Uri uri = CompanionContentProvider.NOTE_URI;
        String filter = DBHelper.NOTE_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.NOTE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        noteText = cursor.getString(cursor.getColumnIndex(DBHelper.NOTE_TEXT));

        return noteText;
    }

//Other Getter Methods
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

    public String loadStatus(Context statusContext, int statusId){
        String statusName = "";
        Uri statusUri = CompanionContentProvider.STATUS_URI;
        String statusFilter = DBHelper.STATUS_ID + " = " + statusId;
        Cursor statusCursor = statusContext.getContentResolver().query(statusUri, DBHelper.STATUS_COLUMNS, statusFilter, null, null);
        statusCursor.moveToFirst();
        statusName = statusCursor.getString(statusCursor.getColumnIndex(DBHelper.STATUS_NAME));

        return statusName;
    }

    public String loadType(Context typeContext, int typeId){
        String typeName = "";
        Uri typeUri = CompanionContentProvider.ASSESSMENT_TYPE_URI;
        String typeFilter = DBHelper.ASSESSMENT_TYPE_ID + " = " + typeId;
        Cursor typeCursor = typeContext.getContentResolver().query(typeUri, DBHelper.ASSESSMENT_TYPE_COLUMNS, typeFilter, null, null);
        typeCursor.moveToFirst();
        typeName = typeCursor.getString(typeCursor.getColumnIndex(DBHelper.ASSESSMENT_TYPE_NAME));

        return typeName;
    }
}
