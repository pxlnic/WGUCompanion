package com.example.wgu_companion.wgucompanion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {

    //DB Name/Version Constants
    public static final String DATABASE_NAME = "wguCompanion";
    public static final int DATABASE_VERSION = 1;

    //DB Table Name Constants
    public static final String TABLE_PROGRAM = "program";
    public static final String TABLE_TERM = "term";
    public static final String TABLE_COURSE = "course";
    public static final String TABLE_ASSESSMENT = "assessment";
    public static final String TABLE_ASSESSMENT_TYPE = "assessmentType";
    public static final String TABLE_MENTOR = "mentor";
    public static final String TABLE_COURSE_MENTOR = "courseMentor";
    public static final String TABLE_NOTE = "note";
    public static final String TABLE_STATUS = "status";

    //DB Table Column Constants
    //Program Columns
    public static final String PROGRAM_ID = "_id";
    public static final String PROGRAM_NAME = "program_name";
    public static final String PROGRAM_DEGREE_TYPE = "program_degree_type";

    //Term Columns
    public static final String TERM_ID = "_id";
    public static final String TERM_NAME = "term_name";
    public static final String TERM_PROGRAM_ID = "term_program_id";
    public static final String TERM_STATUS_ID = "term_status";
    public static final String TERM_START_DATE = "term_start_date";
    public static final String TERM_END_DATE = "term_end_date";
    public static final String TERM_START_REMINDER = "term_start_reminder";
    public static final String TERM_END_REMINDER = "term_end_reminder";

    //Course Columns
    public static final String COURSE_ID = "_id";
    public static final String COURSE_NAME = "course_name";
    public static final String COURSE_TERM_ID = "course_term_id";
    public static final String COURSE_PROGRAM_ID = "course_program_id";
    public static final String COURSE_STATUS_ID = "course_status_id";
    public static final String COURSE_START_DATE = "course_start_date";
    public static final String COURSE_END_DATE = "course_end_date";
    public static final String COURSE_START_REMINDER = "course_start_reminder";
    public static final String COURSE_END_REMINDER = "course_end_reminder";
    public static final String COURSE_DESCRIPTION = "course_description";


    //Assessment Columns
    public static final String ASSESSMENT_ID = "_id";
    public static final String ASSESSMENT_COURSE_ID = "assessment_course_id";
    public static final String ASSESSMENT_A_TYPE_ID = "assessment_type_id";
    public static final String ASSESSMENT_STATUS_ID = "assessment_status_id";
    public static final String ASSESSMENT_DUE_DATE = "assessment_due_date";

    //Assessment Type Columns
    public static final String ASSESSMENT_TYPE_ID = "_id";
    public static final String ASSESSMENT_TYPE_NAME = "assessment_type_name";

    //Mentor Columns
    public static final String MENTOR_ID = "_id";
    public static final String MENTOR_NAME = "mentor_name";
    public static final String MENTOR_PHONE = "mentor_phone";
    public static final String MENTOR_EMAIL = "mentor_email";

    //Mentor/Course Columns
    public static final String MENTOR_COURSE_ID = "_id";
    public static final String MENTOR_COURSE_C_ID = "mentor_course_c_id";
    public static final String MENTOR_COURSE_M_ID = "mentor_course_m_id";

    //Notes Columns
    public static final String NOTE_ID = "_id";
    public static final String NOTE_COURSE_ID = "note_course_id";
    public static final String NOTE_TITLE = "note_title";
    public static final String NOTE_TEXT = "note_text";
    public static final String NOTE_PHOTO_PATH = "notes_photo_path";

    //Status Columns
    public static final String STATUS_ID = "_id";
    public static final String STATUS_NAME = "status_name";

    //SQL Column Arrays for all Tables
    public static final String[] PROGRAM_COLUMNS = {PROGRAM_ID, PROGRAM_NAME, PROGRAM_DEGREE_TYPE};
    public static final String[] TERM_COLUMNS = {TERM_ID, TERM_NAME, TERM_PROGRAM_ID, TERM_STATUS_ID, TERM_START_DATE, TERM_END_DATE,
            TERM_START_REMINDER, TERM_END_REMINDER};
    public static final String[] COURSE_COLUMNS = {COURSE_ID, COURSE_NAME, COURSE_TERM_ID, COURSE_PROGRAM_ID, COURSE_STATUS_ID,
            COURSE_START_DATE, COURSE_END_DATE, COURSE_START_REMINDER, COURSE_END_REMINDER,
            COURSE_DESCRIPTION};
    public static final String[] ASSESSMENT_COLUMNS = {ASSESSMENT_ID, ASSESSMENT_COURSE_ID, ASSESSMENT_A_TYPE_ID, ASSESSMENT_STATUS_ID,
            ASSESSMENT_DUE_DATE};
    public static final String[] ASSESSMENT_TYPE_COLUMNS = {ASSESSMENT_TYPE_ID, ASSESSMENT_TYPE_NAME};
    public static final String[] MENTOR_COLUMNS = {MENTOR_ID, MENTOR_NAME, MENTOR_PHONE, MENTOR_EMAIL};
    public static final String[] COURSE_MENTOR_COLUMNS = {MENTOR_COURSE_ID, MENTOR_COURSE_C_ID, MENTOR_COURSE_M_ID};
    public static final String[] NOTE_COLUMNS = {NOTE_ID, NOTE_COURSE_ID, NOTE_TITLE, NOTE_TEXT, NOTE_PHOTO_PATH};
    public static final String[] STATUS_COLUMNS = {STATUS_ID, STATUS_NAME};

    //SQL Create Table Statements
    //PROGRAM TABLE
    private static final String PROGRAM_TABLE_CREATE =
            "CREATE TABLE " + TABLE_PROGRAM + " (" + PROGRAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PROGRAM_NAME + " TEXT, " + PROGRAM_DEGREE_TYPE + " TEXT" + ");";
    //TERM TABLE
   private static final String TERM_TABLE_CREATE =
            "CREATE TABLE " + TABLE_TERM + " (" + TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TERM_NAME + " TEXT, " + TERM_PROGRAM_ID + " INTEGER, " + TERM_STATUS_ID + " INTEGER, " +
                    TERM_START_DATE + " TEXT, " + TERM_END_DATE + " TEXT, " + TERM_START_REMINDER + " INTEGER, " + TERM_END_REMINDER +
                    " INTEGER, " +
                    " FOREIGN KEY (" + TERM_PROGRAM_ID + ") REFERENCES " + TABLE_PROGRAM + "(" + PROGRAM_ID + "), " +
                    " FOREIGN KEY (" + TERM_STATUS_ID + ") REFERENCES " + TABLE_STATUS + "(" + STATUS_ID + "));";
    //COURSE TABLE
   private static final String COURSE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_COURSE + " (" + COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COURSE_NAME + " TEXT, " + COURSE_TERM_ID + " INTEGER, " + COURSE_PROGRAM_ID + " INTEGER, " +
                    COURSE_STATUS_ID + " INTEGER, " + COURSE_START_DATE + " TEXT, " + COURSE_END_DATE + " TEXT, " +
                    COURSE_START_REMINDER + " INTEGER, " + COURSE_END_REMINDER + " INTEGER, " + COURSE_DESCRIPTION + " TEXT, " +
                    " FOREIGN KEY (" + COURSE_TERM_ID + ") REFERENCES " + TABLE_TERM + "(" + TERM_ID + ")," +
                    " FOREIGN KEY (" + COURSE_PROGRAM_ID + ") REFERENCES " + TABLE_PROGRAM + "(" + PROGRAM_ID + ")," +
                    " FOREIGN KEY (" + COURSE_STATUS_ID + ") REFERENCES " + TABLE_STATUS + "(" + STATUS_ID + "));";
   //ASSESSMENT TABLE
   private static final String ASSESSMENT_TABLE_CREATE =
            "CREATE TABLE " + TABLE_ASSESSMENT + " (" + ASSESSMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESSMENT_COURSE_ID + " INTEGER, " + ASSESSMENT_A_TYPE_ID + " INTEGER, " + ASSESSMENT_STATUS_ID + " INTEGER, " +
                    ASSESSMENT_DUE_DATE + " TEXT, " +
                    " FOREIGN KEY (" + ASSESSMENT_COURSE_ID + ") REFERENCES " + TABLE_COURSE + "(" + COURSE_ID + ")," +
                    " FOREIGN KEY (" + ASSESSMENT_A_TYPE_ID + ") REFERENCES " + TABLE_ASSESSMENT_TYPE + "(" + ASSESSMENT_TYPE_ID + ")," +
                    " FOREIGN KEY (" + ASSESSMENT_STATUS_ID + ") REFERENCES " + TABLE_STATUS + "(" + STATUS_ID + "));";
    //ASSESSMENT TYPE TABLE
    private static final String ASSESSMENT_TYPE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_ASSESSMENT_TYPE + " (" + ASSESSMENT_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESSMENT_TYPE_NAME + " TEXT);";
    //MENTOR TABLE
    private static final String MENTOR_TABLE_CREATE =
            "CREATE TABLE " + TABLE_MENTOR + " (" + MENTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MENTOR_NAME + " TEXT, " + MENTOR_PHONE + " INTEGER, " + MENTOR_EMAIL + " TEXT);";
    //COURSE MENTOR RELATIONSHIP TABLE
    private static final String COURSE_MENTOR_TABLE_CREATE =
            "CREATE TABLE " + TABLE_COURSE_MENTOR + " (" + MENTOR_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MENTOR_COURSE_C_ID + " INTEGER, " + MENTOR_COURSE_M_ID + " INTEGER, " +
                    " FOREIGN KEY (" + MENTOR_COURSE_C_ID + ") REFERENCES " + TABLE_COURSE + "(" + COURSE_ID + ")," +
                    " FOREIGN KEY (" + MENTOR_COURSE_M_ID + ") REFERENCES " + TABLE_MENTOR + "(" + MENTOR_ID + "));";
    //NOTE TABLE
    private static final String NOTE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTE + " (" + NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTE_COURSE_ID + " INTEGER, " + NOTE_TITLE + " TEXT, " + NOTE_TEXT + " TEXT, " + NOTE_PHOTO_PATH + " TEXT, " +
                    " FOREIGN KEY (" + NOTE_COURSE_ID + ") REFERENCES " + TABLE_COURSE + "(" + COURSE_ID + "));";
    //TERM/COURSE/ASSESSMENT STATUS TABLE
    private static final String STATUS_TABLE_CREATE =
            "CREATE TABLE " + TABLE_STATUS + " (" + STATUS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + STATUS_NAME + " TEXT);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Database", "DBHelper onCreate Started");
        db.execSQL("PRAGMA foreign_keys=ON;");
        db.execSQL(PROGRAM_TABLE_CREATE);
        db.execSQL(TERM_TABLE_CREATE);
        db.execSQL(COURSE_TABLE_CREATE);
        db.execSQL(ASSESSMENT_TABLE_CREATE);
        db.execSQL(ASSESSMENT_TYPE_TABLE_CREATE);
        db.execSQL(MENTOR_TABLE_CREATE);
        db.execSQL(COURSE_MENTOR_TABLE_CREATE);
        db.execSQL(NOTE_TABLE_CREATE);
        db.execSQL(STATUS_TABLE_CREATE);
        //Create Static Statuses
        db.execSQL("INSERT INTO " + TABLE_STATUS + " (" + STATUS_NAME + ") VALUES " +
            "('Not Attempted'), " +
            "('In Progress'), " +
            "('Completed'), " +
            "('Passed'), " +
            "('Failed')");
        Log.d("Database", "DBHelper Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRAM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENT_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENTOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE_MENTOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUS);
        onCreate(db);
    }
}
