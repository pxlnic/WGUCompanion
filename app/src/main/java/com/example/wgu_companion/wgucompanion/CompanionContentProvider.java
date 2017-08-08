package com.example.wgu_companion.wgucompanion;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class CompanionContentProvider extends ContentProvider {
    //Authority Constant
    private static final String AUTHORITY = "come.example.wgu_companion.companioncontentprovider";

    //Path Constants
    private static final String PROGRAM_PATH = "program";
    private static final String TERM_PATH = "term";
    private static final String COURSE_PATH = "course";
    private static final String ASSESSMENT_PATH = "assessment";
    private static final String ASSESSMENT_TYPE_PATH = "assessmentType";
    private static final String MENTOR_PATH = "mentor";
    private static final String COURSE_MENTOR_PATH = "courseMentor";
    private static final String NOTE_PATH = "note";
    private static final String STATUS_PATH = "status";
    private static final String STATUS_TYPE_PATH = "statusType";

    //Content URI Constants
    public static final Uri PROGRAM_URI = Uri.parse("content://" + AUTHORITY + "/" + PROGRAM_PATH);
    public static final Uri TERM_URI = Uri.parse("content://" + AUTHORITY + "/" + TERM_PATH);
    public static final Uri COURSE_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSE_PATH);
    public static final Uri ASSESSMENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ASSESSMENT_PATH);
    public static final Uri ASSESSMENT_TYPE_URI = Uri.parse("content://" + AUTHORITY + "/" + ASSESSMENT_TYPE_PATH);
    public static final Uri MENTOR_URI = Uri.parse("content://" + AUTHORITY + "/" + MENTOR_PATH);
    public static final Uri COURSE_MENTOR_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSE_MENTOR_PATH);
    public static final Uri NOTE_URI = Uri.parse("content://" + AUTHORITY + "/" + NOTE_PATH);
    public static final Uri STATUS_URI = Uri.parse("content://" + AUTHORITY + "/" + STATUS_PATH);

    //Add URI Calls
    private static final int PROGRAM = 1;
    private static final int PROGRAM_ID = 2;
    private static final int TERM = 3;
    private static final int TERM_ID = 4;
    private static final int COURSE = 5;
    private static final int COURSE_ID = 6;
    private static final int ASSESSMENT = 7;
    private static final int ASSESSMENT_ID = 8;
    private static final int ASSESSMENT_TYPE = 9;
    private static final int ASSESSMENT_TYPE_ID = 10;
    private static final int MENTOR = 11;
    private static final int MENTOR_ID = 12;
    private static final int COURSE_MENTOR = 13;
    private static final int COURSE_MENTOR_ID = 14;
    private static final int NOTE = 15;
    private static final int NOTE_ID = 16;
    private static final int STATUS = 17;
    private static final int STATUS_ID = 18;

    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String TERM_ITEM_TYPE = "Term";
    public static final String COURSE_ITEM_TYPE = "Course";
    public static final String ASSESSMENT_ITEM_TYPE = "Assessment";
    public static final String MENTOR_ITEM_TYPE = "Mentor";
    public static final String NOTE_ITEM_TYPE = "Note";

    static{
        matcher.addURI(AUTHORITY, PROGRAM_PATH, PROGRAM);
        matcher.addURI(AUTHORITY, PROGRAM_PATH + "/#", PROGRAM_ID);
        matcher.addURI(AUTHORITY, TERM_PATH, TERM);
        matcher.addURI(AUTHORITY, TERM_PATH + "/#", TERM_ID);
        matcher.addURI(AUTHORITY, COURSE_PATH, COURSE);
        matcher.addURI(AUTHORITY, COURSE_PATH + "/#", COURSE_ID);
        matcher.addURI(AUTHORITY, ASSESSMENT_PATH, ASSESSMENT);
        matcher.addURI(AUTHORITY, ASSESSMENT_PATH + "/#", ASSESSMENT_ID);
        matcher.addURI(AUTHORITY, ASSESSMENT_TYPE_PATH, ASSESSMENT_TYPE);
        matcher.addURI(AUTHORITY, ASSESSMENT_TYPE_PATH + "/#", ASSESSMENT_TYPE_ID);
        matcher.addURI(AUTHORITY, MENTOR_PATH, MENTOR);
        matcher.addURI(AUTHORITY, MENTOR_PATH + "/#", MENTOR_ID);
        matcher.addURI(AUTHORITY, COURSE_MENTOR_PATH, COURSE_MENTOR);
        matcher.addURI(AUTHORITY, COURSE_MENTOR_PATH + "/#", COURSE_MENTOR_ID);
        matcher.addURI(AUTHORITY, NOTE_PATH, NOTE);
        matcher.addURI(AUTHORITY, NOTE_PATH + "/#", NOTE_ID);
        matcher.addURI(AUTHORITY, STATUS_PATH, STATUS);
        matcher.addURI(AUTHORITY, STATUS_PATH + "/#", STATUS_ID);
    }

    private SQLiteDatabase companionDb;

    @Override
    public boolean onCreate() {
        //Get/Create SQL DB
        DBHelper helper = new DBHelper(getContext());
        companionDb = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;

        switch(matcher.match(uri)){
            case PROGRAM:
                cursor = companionDb.query(DBHelper.TABLE_PROGRAM, DBHelper.PROGRAM_COLUMNS, selection, null, null, null, DBHelper.PROGRAM_ID + " DESC");
                break;
            case PROGRAM_ID:
                selection = DBHelper.PROGRAM_ID + "=" + uri.getLastPathSegment();
                cursor = companionDb.query(DBHelper.TABLE_PROGRAM, DBHelper.PROGRAM_COLUMNS, selection, null, null, null, DBHelper.PROGRAM_ID + " DESC");
                break;
            case TERM:
                cursor = companionDb.query(DBHelper.TABLE_TERM, DBHelper.TERM_COLUMNS, selection, null, null, null, DBHelper.TERM_ID + " DESC");
                break;
            case TERM_ID:
                selection = DBHelper.TERM_ID + "=" + uri.getLastPathSegment();
                cursor = companionDb.query(DBHelper.TABLE_TERM, DBHelper.TERM_COLUMNS, selection, null, null, null, DBHelper.TERM_ID + " DESC");
                break;
            case COURSE:
                cursor = companionDb.query(DBHelper.TABLE_COURSE, DBHelper.COURSE_COLUMNS, selection, null, null, null, DBHelper.COURSE_ID + " DESC");
                break;
            case COURSE_ID:
                selection = DBHelper.COURSE_ID + "=" + uri.getLastPathSegment();
                cursor = companionDb.query(DBHelper.TABLE_COURSE, DBHelper.COURSE_COLUMNS, selection, null, null, null, DBHelper.COURSE_ID + " DESC");
                break;
            case ASSESSMENT:
                cursor = companionDb.query(DBHelper.TABLE_ASSESSMENT, DBHelper.ASSESSMENT_COLUMNS, selection, null, null, null, DBHelper.ASSESSMENT_ID + " DESC");
                break;
            case ASSESSMENT_ID:
                selection = DBHelper.ASSESSMENT_ID + "=" + uri.getLastPathSegment();
                cursor = companionDb.query(DBHelper.TABLE_ASSESSMENT, DBHelper.ASSESSMENT_COLUMNS, selection, null, null, null, DBHelper.ASSESSMENT_ID + " DESC");                break;
            case ASSESSMENT_TYPE:
                cursor = companionDb.query(DBHelper.TABLE_ASSESSMENT_TYPE, DBHelper.ASSESSMENT_TYPE_COLUMNS, selection, null, null, null, DBHelper.ASSESSMENT_TYPE_ID + " DESC");
                break;
            case ASSESSMENT_TYPE_ID:
                selection = DBHelper.ASSESSMENT_TYPE_ID + "=" + uri.getLastPathSegment();
                cursor = companionDb.query(DBHelper.TABLE_ASSESSMENT_TYPE, DBHelper.ASSESSMENT_TYPE_COLUMNS, selection, null, null, null, DBHelper.ASSESSMENT_TYPE_ID + " DESC");
                break;
            case MENTOR:
                cursor = companionDb.query(DBHelper.TABLE_MENTOR, DBHelper.MENTOR_COLUMNS, selection, null, null, null, DBHelper.MENTOR_ID + " DESC");
                break;
            case MENTOR_ID:
                selection = DBHelper.MENTOR_ID + "=" + uri.getLastPathSegment();
                cursor = companionDb.query(DBHelper.TABLE_MENTOR, DBHelper.MENTOR_COLUMNS, selection, null, null, null, DBHelper.MENTOR_ID + " DESC");
                break;
            case COURSE_MENTOR:
                cursor = companionDb.query(DBHelper.TABLE_COURSE_MENTOR, DBHelper.COURSE_MENTOR_COLUMNS, selection, null, null, null, DBHelper.MENTOR_COURSE_ID + " DESC");
                break;
            case COURSE_MENTOR_ID:
                selection = DBHelper.MENTOR_COURSE_ID + "=" + uri.getLastPathSegment();
                cursor = companionDb.query(DBHelper.TABLE_COURSE_MENTOR, DBHelper.COURSE_MENTOR_COLUMNS, selection, null, null, null, DBHelper.MENTOR_COURSE_ID + " DESC");
                break;
            case NOTE:
                cursor = companionDb.query(DBHelper.TABLE_NOTE, DBHelper.NOTE_COLUMNS, selection, null, null, null, DBHelper.NOTE_ID + " DESC");
                break;
            case NOTE_ID:
                selection = DBHelper.NOTE_ID + "=" + uri.getLastPathSegment();
                cursor = companionDb.query(DBHelper.TABLE_NOTE, DBHelper.NOTE_COLUMNS, selection, null, null, null, DBHelper.NOTE_ID + " DESC");
                break;
            case STATUS:
                cursor = companionDb.query(DBHelper.TABLE_STATUS, DBHelper.STATUS_COLUMNS, selection, null, null, null, DBHelper.STATUS_ID + " DESC");
                break;
            case STATUS_ID:
                selection = DBHelper.STATUS_ID + "=" + uri.getLastPathSegment();
                cursor = companionDb.query(DBHelper.TABLE_STATUS, DBHelper.STATUS_COLUMNS, selection, null, null, null, DBHelper.STATUS_ID + " DESC");
                break;
            default:
                Toast.makeText(getContext(), "Query Failed!", Toast.LENGTH_SHORT).show();
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        String uriString = "";
        long id;
        switch (matcher.match(uri)){
            case PROGRAM:
                id = companionDb.insert(DBHelper.TABLE_PROGRAM, null, values);
                uriString = PROGRAM_PATH + "/" + id;
                break;
            case TERM:
                id = companionDb.insert(DBHelper.TABLE_TERM, null, values);
                uriString = TERM_PATH + "/" + id;
                break;
            case COURSE:
                id = companionDb.insert(DBHelper.TABLE_COURSE, null, values);
                uriString = COURSE_PATH + "/" + id;
                break;
            case ASSESSMENT:
                id = companionDb.insert(DBHelper.TABLE_ASSESSMENT, null, values);
                uriString = ASSESSMENT_PATH + "/" + id;
                break;
            case ASSESSMENT_TYPE:
                id = companionDb.insert(DBHelper.TABLE_ASSESSMENT_TYPE, null, values);
                uriString = ASSESSMENT_TYPE_PATH + "/" + id;
                break;
            case MENTOR:
                id = companionDb.insert(DBHelper.TABLE_MENTOR, null, values);
                uriString = MENTOR_PATH + "/" + id;
                break;
            case COURSE_MENTOR:
                id = companionDb.insert(DBHelper.TABLE_COURSE_MENTOR, null, values);
                uriString = COURSE_MENTOR_PATH + "/" + id;
                break;
            case NOTE:
                id = companionDb.insert(DBHelper.TABLE_NOTE, null, values);
                uriString = NOTE_PATH + "/" + id;
                break;
            case STATUS:
                id = companionDb.insert(DBHelper.TABLE_STATUS, null, values);
                uriString = STATUS_PATH + "/" + id;
                break;
            default:
                Toast.makeText(getContext(), "Insert Failed", Toast.LENGTH_SHORT).show();
        }
        return Uri.parse(uriString);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int deleted = 0;
        switch(matcher.match(uri)){
            case PROGRAM:
                deleted = companionDb.delete(DBHelper.TABLE_PROGRAM, selection, selectionArgs);
                break;
            case TERM:
                deleted = companionDb.delete(DBHelper.TABLE_TERM, selection, selectionArgs);
                break;
            case COURSE:
                deleted = companionDb.delete(DBHelper.TABLE_COURSE, selection, selectionArgs);
                break;
            case ASSESSMENT:
                deleted = companionDb.delete(DBHelper.TABLE_ASSESSMENT, selection, selectionArgs);
                break;
            case ASSESSMENT_TYPE:
                deleted = companionDb.delete(DBHelper.TABLE_ASSESSMENT_TYPE, selection, selectionArgs);
                break;
            case MENTOR:
                deleted = companionDb.delete(DBHelper.TABLE_MENTOR, selection, selectionArgs);
                break;
            case COURSE_MENTOR:
                deleted = companionDb.delete(DBHelper.TABLE_COURSE_MENTOR, selection, selectionArgs);
                break;
            case NOTE:
                deleted = companionDb.delete(DBHelper.TABLE_NOTE, selection, selectionArgs);
                break;
            case STATUS:
                deleted = companionDb.delete(DBHelper.TABLE_STATUS, selection, selectionArgs);
                break;
            default:
                Toast.makeText(getContext(), "Delete Failed!", Toast.LENGTH_SHORT).show();
                break;
        }
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int updated = 0;
        switch(matcher.match(uri)){
            case PROGRAM:
                updated = companionDb.update(DBHelper.TABLE_PROGRAM, values, selection, selectionArgs);
                break;
            case TERM:
                updated = companionDb.update(DBHelper.TABLE_TERM, values, selection, selectionArgs);
                break;
            case COURSE:
                updated = companionDb.update(DBHelper.TABLE_COURSE, values, selection, selectionArgs);
                break;
            case ASSESSMENT:
                updated = companionDb.update(DBHelper.TABLE_ASSESSMENT, values, selection, selectionArgs);
                break;
            case ASSESSMENT_TYPE:
                updated = companionDb.update(DBHelper.TABLE_ASSESSMENT_TYPE, values, selection, selectionArgs);
                break;
            case MENTOR:
                updated = companionDb.update(DBHelper.TABLE_MENTOR, values, selection, selectionArgs);
                break;
            case COURSE_MENTOR:
                updated = companionDb.update(DBHelper.TABLE_PROGRAM, values, selection, selectionArgs);
                break;
            case NOTE:
                updated = companionDb.update(DBHelper.TABLE_NOTE, values, selection, selectionArgs);
                break;
            case STATUS:
                updated = companionDb.update(DBHelper.TABLE_STATUS, values, selection, selectionArgs);
                break;
            default:
                Toast.makeText(getContext(), "Update Failed1", Toast.LENGTH_SHORT).show();
                break;
        }
        return updated;
    }

    public SQLiteDatabase getDb(){
        return companionDb;
    }
}
