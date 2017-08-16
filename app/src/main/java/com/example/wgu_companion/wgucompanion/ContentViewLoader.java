package com.example.wgu_companion.wgucompanion;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContentViewLoader {
    public ContentViewLoader() {

    }

    public String loadProgramName(Context c) {
        String program;
        Uri uri = CompanionContentProvider.PROGRAM_URI;
        String filter = DBHelper.PROGRAM_ID + "=" + 1;
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.PROGRAM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        program = cursor.getString(cursor.getColumnIndex(DBHelper.PROGRAM_DEGREE_TYPE));
        program = program + " - " + cursor.getString(cursor.getColumnIndex(DBHelper.PROGRAM_NAME));


        return program;
    }

    public int loadProgramID(Context c) {
        int programId;
        Uri uri = CompanionContentProvider.PROGRAM_URI;
        String filter = DBHelper.PROGRAM_ID + "=" + 1;
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.PROGRAM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        programId = cursor.getInt(cursor.getColumnIndex(DBHelper.PROGRAM_ID));


        return programId;
    }

    public int loadCompletedCU(Context c) {
        int completed = 0;
        Uri uri = CompanionContentProvider.COURSE_URI;
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, null, null, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            if (cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_STATUS)).equals("Complete")) {
                completed = completed + cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_CU_COUNT));
            }
            cursor.moveToNext();
        }


        return completed;
    }

    public int loadTotalCU(Context c) {
        int total = 0;
        Uri uri = CompanionContentProvider.COURSE_URI;
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, null, null, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            total = total + cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_CU_COUNT));
            cursor.moveToNext();
        }


        return total;
    }

    public String loadTermName(Context c, Uri id) {
        String term;
        Uri uri = CompanionContentProvider.TERM_URI;
        String filter = DBHelper.TERM_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.TERM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        term = cursor.getString(cursor.getColumnIndex(DBHelper.TERM_NAME));


        return term;
    }

    public String loadTermStart(Context c, Uri id) {
        String termStart;
        Uri uri = CompanionContentProvider.TERM_URI;
        String filter = DBHelper.TERM_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.TERM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        termStart = cursor.getString(cursor.getColumnIndex(DBHelper.TERM_START_DATE));


        return termStart;
    }

    public String loadTermEnd(Context c, Uri id) {
        String termEnd;
        Uri uri = CompanionContentProvider.TERM_URI;
        String filter = DBHelper.TERM_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.TERM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        termEnd = cursor.getString(cursor.getColumnIndex(DBHelper.TERM_END_DATE));


        return termEnd;
    }

    public String loadTermStatus(Context c, Uri id) {
        String termStatus;
        Uri uri = CompanionContentProvider.TERM_URI;
        String filter = DBHelper.TERM_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.TERM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        termStatus = cursor.getString(cursor.getColumnIndex(DBHelper.TERM_STATUS));


        return termStatus;
    }

    public int loadTermCompletedCU(Context c, Uri id) {
        int termCompleted = 0;
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_TERM_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            if (cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_STATUS)).equals("Complete")) {
                termCompleted = termCompleted + cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_CU_COUNT));
            }
        }


        return termCompleted;
    }

    public int loadTermTotalCU(Context c, Uri id) {
        int termTotal = 0;
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_TERM_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            termTotal = termTotal + cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_CU_COUNT));
            cursor.moveToNext();
        }


        return termTotal;
    }

    public boolean loadTermStartReminder(Context c, Uri id) {
        int termStartCheck;
        Uri uri = CompanionContentProvider.TERM_URI;
        String filter = DBHelper.TERM_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.TERM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        termStartCheck = cursor.getInt(cursor.getColumnIndex(DBHelper.TERM_START_REMINDER));

        boolean isChecked = false;
        if (termStartCheck == 1) {
            isChecked = true;
        }


        return isChecked;
    }

    public boolean loadTermEndReminder(Context c, Uri id) {
        int termEndCheck;
        Uri uri = CompanionContentProvider.TERM_URI;
        String filter = DBHelper.TERM_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.TERM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        termEndCheck = cursor.getInt(cursor.getColumnIndex(DBHelper.TERM_END_REMINDER));

        boolean isChecked = false;
        if (termEndCheck == 1) {
            isChecked = true;
        }


        return isChecked;
    }

    public List<String> loadCourseIds(Cursor cursor) {
        List<String> termCourseIds = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            termCourseIds.add(cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_ID)));
            cursor.moveToNext();
        }


        return termCourseIds;
    }

    public List<String> loadCourseTermIds(Cursor cursor) {
        List<String> courseTermIds = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            courseTermIds.add(cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_TERM_ID)));
            cursor.moveToNext();
        }


        return courseTermIds;
    }

    public String loadCourseName(Context c, Uri id) {
        String courseName;
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        courseName = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_NAME));


        return courseName;
    }

    public String loadCourseStart(Context c, Uri id) {
        String courseStart;
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        courseStart = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_START_DATE));


        return courseStart;
    }

    public boolean loadCourseStartReminder(Context c, Uri id) {
        int courseStartCheck;
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        courseStartCheck = cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_START_REMINDER));

        boolean isChecked = false;
        if (courseStartCheck == 1) {
            isChecked = true;
        }


        return isChecked;
    }

    public String loadCourseEnd(Context c, Uri id) {
        String courseEnd;
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        courseEnd = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_END_DATE));


        return courseEnd;
    }

    public boolean loadCourseEndReminder(Context c, Uri id) {
        int courseStartCheck;
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        courseStartCheck = cursor.getInt(cursor.getColumnIndex(DBHelper.COURSE_END_REMINDER));

        boolean isChecked = false;
        if (courseStartCheck == 1) {
            isChecked = true;
        }


        return isChecked;
    }

    public String loadCourseStatus(Context c, Uri id) {
        String courseStatus;
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        courseStatus = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_STATUS));

        //courseStatus = loadStatus(c, courseStatusId);


        return courseStatus;
    }

    public String loadCourseDescription(Context c, Uri id) {
        String courseDescription;
        Uri uri = CompanionContentProvider.COURSE_URI;
        String filter = DBHelper.COURSE_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.COURSE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        courseDescription = cursor.getString(cursor.getColumnIndex(DBHelper.COURSE_DESCRIPTION));


        return courseDescription;
    }

//Save New/Edited Course Data

    public String loadAssessmentCourseName(Context c, Uri id) {
        String assessmentCourseName;
        Uri uri = CompanionContentProvider.ASSESSMENT_URI;
        String filter = DBHelper.ASSESSMENT_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.ASSESSMENT_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        int assessmentCourseId = cursor.getInt(cursor.getColumnIndex(DBHelper.ASSESSMENT_COURSE_ID));

        Uri courseUri = Uri.parse(CompanionContentProvider.COURSE_URI + "/" + assessmentCourseId);
        assessmentCourseName = loadCourseName(c, courseUri);


        return assessmentCourseName;
    }

    public String loadAssessmentType(Context c, Uri id) {
        String assessmentType;
        Uri uri = CompanionContentProvider.ASSESSMENT_URI;
        String filter = DBHelper.ASSESSMENT_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.ASSESSMENT_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        assessmentType = cursor.getString(cursor.getColumnIndex(DBHelper.ASSESSMENT_TYPE));

        //assessmentType = loadType(c, assessmentTypeId);


        return assessmentType;
    }

    public String loadAssessmentDueDate(Context c, Uri id) {
        String assessmentDueDate;
        Uri uri = CompanionContentProvider.ASSESSMENT_URI;
        String filter = DBHelper.ASSESSMENT_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.ASSESSMENT_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        assessmentDueDate = cursor.getString(cursor.getColumnIndex(DBHelper.ASSESSMENT_DUE_DATE));


        return assessmentDueDate;
    }

    public boolean loadAssessmentGoalReminder(Context c, Uri id) {
        int assessmentGoalChk;
        Uri uri = CompanionContentProvider.ASSESSMENT_URI;
        String filter = DBHelper.ASSESSMENT_ID + "=" + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.ASSESSMENT_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        assessmentGoalChk = cursor.getInt(cursor.getColumnIndex(DBHelper.ASSESSMENT_DUE_DATE_REMINDER));

        boolean isChecked = false;
        if (assessmentGoalChk == 1) {
            isChecked = true;
        }


        return isChecked;
    }

    public String loadAssessmentStatus(Context c, Uri id) {
        String assessmentStatus;
        Uri uri = CompanionContentProvider.ASSESSMENT_URI;
        String filter = DBHelper.ASSESSMENT_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.ASSESSMENT_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        assessmentStatus = cursor.getString(cursor.getColumnIndex(DBHelper.ASSESSMENT_STATUS));


        //assessmentStatus = loadStatus(c, statusId);

        return assessmentStatus;
    }

    public List<String> loadAssessmentIds(Cursor cursor) {
        List<String> courseAssessmentIds = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            courseAssessmentIds.add(cursor.getString(cursor.getColumnIndex(DBHelper.ASSESSMENT_ID)));
            cursor.moveToNext();
        }

        return courseAssessmentIds;
    }

    public List<String> loadAssessmentCourseIds(Cursor cursor) {
        List<String> courseAssessmentIds = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            courseAssessmentIds.add(cursor.getString(cursor.getColumnIndex(DBHelper.ASSESSMENT_COURSE_ID)));
            cursor.moveToNext();
        }

        return courseAssessmentIds;
    }

    public List<String> loadMentorIds(Cursor cursor) {
        List<String> courseMentorIds = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            courseMentorIds.add(cursor.getString(cursor.getColumnIndex(DBHelper.MENTOR_ID)));
            cursor.moveToNext();
        }


        return courseMentorIds;
    }

//Save New/Edited Assessment Data

    //Note Data
    private String noteTitle = "";
    private String noteText = "";

    public String loadNoteTitle(Context c, Uri id) {
        noteTitle = "";
        Uri uri = CompanionContentProvider.NOTE_URI;
        String filter = DBHelper.NOTE_ID + " = " + id.getLastPathSegment();
        Cursor cursor = c.getContentResolver().query(uri, DBHelper.NOTE_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        noteTitle = cursor.getString(cursor.getColumnIndex(DBHelper.NOTE_TITLE));


        return noteTitle;
    }

    public String loadNoteText(Context c, Uri id) {
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
    public String convertDate(int day, int month, int year) {
        String formattedDate;
        String dayText = "" + day;
        String monthText = "" + month;

        if (day < 10) {
            dayText = "" + 0 + day;
        }
        if (month < 10) {
            monthText = "" + 0 + month;
        }

        formattedDate = year + "/" + monthText + "/" + dayText;

        return formattedDate;
    }

    public String loadStatus(Context statusContext, int statusId) {
        String statusName = "";
        Uri statusUri = CompanionContentProvider.STATUS_URI;
        String statusFilter = DBHelper.STATUS_ID + " = " + statusId;
        Cursor statusCursor = statusContext.getContentResolver().query(statusUri, DBHelper.STATUS_COLUMNS, statusFilter, null, null);
        statusCursor.moveToFirst();
        statusName = statusCursor.getString(statusCursor.getColumnIndex(DBHelper.STATUS_NAME));

        return statusName;
    }

    public String loadType(Context typeContext, int typeId) {
        String typeName = "";
        Uri typeUri = CompanionContentProvider.ASSESSMENT_TYPE_URI;
        String typeFilter = DBHelper.ASSESSMENT_TYPE_ID + " = " + typeId;
        Cursor typeCursor = typeContext.getContentResolver().query(typeUri, DBHelper.ASSESSMENT_TYPE_COLUMNS, typeFilter, null, null);
        typeCursor.moveToFirst();
        typeName = typeCursor.getString(typeCursor.getColumnIndex(DBHelper.ASSESSMENT_TYPE_NAME));

        return typeName;
    }

    //Create list of reminders
    public void setReminders(Context context) throws ParseException {
        List<CompanionReminders> reminderList = new ArrayList<>();
        Date now = new Date();
        Log.d("Load Data", "Current Date: " + now);

        //Get Term Reminders
        String termFilter = DBHelper.TERM_START_REMINDER + "= 1 OR" + DBHelper.TERM_END_REMINDER + "= 1";
        Cursor termCursor = context.getContentResolver().query(CompanionContentProvider.TERM_URI, DBHelper.TERM_COLUMNS,
                termFilter, null, null);
        termCursor.moveToFirst();

        //Check if any reminders currently
        if (termCursor.getCount() > 0) {
            for (int i = 0; i < termCursor.getCount(); i++) {
                int startRemind = termCursor.getInt(termCursor.getColumnIndex(DBHelper.TERM_START_REMINDER));
                int endRemind = termCursor.getInt(termCursor.getColumnIndex(DBHelper.TERM_END_REMINDER));

                //Create Term Reminder Entity
                CompanionReminders c = new CompanionReminders();

                //Set Reminders
                int termId = termCursor.getInt(termCursor.getColumnIndex(DBHelper.TERM_ID));
                c.setTermReminderId(termId);
                Date startDate;
                Date endDate;
                boolean isStart = false;
                boolean isEnd = false;

                //Check if Start needs reminder
                String startDateText = termCursor.getString(termCursor.getColumnIndex(DBHelper.TERM_START_DATE));
                Date verifyStart = new SimpleDateFormat("yyyy/MM/dd").parse(startDateText);
                if (startRemind == 1 && verifyStart.after(now)) {
                    startDate = new SimpleDateFormat("yyyy/MM/dd").parse(startDateText);
                    c.setTermReminderStart(startDate);
                    isStart = true;
                }

                //Check if End needs reminder
                String endDateText = termCursor.getString(termCursor.getColumnIndex(DBHelper.TERM_END_DATE));
                Date verifyEnd = new SimpleDateFormat("yyyy/MM/dd").parse(endDateText);
                if (endRemind == 1 && verifyEnd.after(now)) {
                    endDate = new SimpleDateFormat("yyyy/MM/dd").parse(endDateText);
                    c.setTermReminderEnd(endDate);
                    isEnd = true;
                }

                //Set Reminder Info
                if (isStart || isEnd) {
                    reminderList.add(c);
                }

                termCursor.moveToNext();
            }
        }

        //Set Course Reminders


        //Set Assessment Reminders


        //Add Notifications for each reminder

    }
}
