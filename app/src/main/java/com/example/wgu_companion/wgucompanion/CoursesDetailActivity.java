package com.example.wgu_companion.wgucompanion;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CoursesDetailActivity extends AppCompatActivity{

    //Activity Variables
    private String action = "";
    CourseAssessmentCursorAdapter assessmentAdapter;
    CourseMentorCursorAdapter mentorAdapter;
    CourseAssessmentSelectCursorAdapter dialogAssessmentAdapter;
    CourseMentorSelectCursorAdapter dialogMentorAdapter;
    private ContentViewLoader contentLoader = new ContentViewLoader();
    Uri tempCourseUri;
    private static final int ADD_NOTE_REQUEST_CODE = 3001;
    private static final int VIEW_ASSESSMENT_REQUEST_CODE = 2004;
    private static final int VIEW_MENTOR_REQUEST_CODE = 2005;

    //Term Name/Progress/Dates Variables
    private String courseNameText = "";
    private String courseStartDate = "";
    private String courseStartText = "";
    private String courseEndDate = "";
    private String courseEndText = "";
    private String courseStatusText = "";
    private String courseDescriptionText = "";

    //View Declarations
    TextView courseTv;
    TextView courseStartTv;
    TextView courseEndTv;
    TextView courseStatusTv;
    TextView courseDescriptionTv;
    ListView courseAssessmentLv;
    ListView courseMentorLv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_detail);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(CompanionContentProvider.COURSE_ITEM_TYPE);

        setTitle("Course Details");

        tempCourseUri = uri;
        Log.d("Load Data", "Course URI: " + tempCourseUri);
        setViews(uri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_course_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.edit_course:
                try {
                    editCourse();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.delete_course:
                deleteCourse();
                break;
            case R.id.add_mentor:
                add_mentor();
                break;
            case R.id.add_note:
                add_note();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void add_mentor() {
        final Dialog addMentorDialog = new Dialog(CoursesDetailActivity.this);
        addMentorDialog.setContentView(R.layout.edit_mentor);

        TextView addMentorHeader = (TextView) addMentorDialog.findViewById(R.id.edit_mentor_header);
        addMentorHeader.setText("New Mentor");

        //Set Views
        final EditText mentorNameEv = (EditText) addMentorDialog.findViewById(R.id.edit_mentor_name);
        final EditText mentorPhoneEv = (EditText) addMentorDialog.findViewById(R.id.edit_mentor_phone);
        final EditText mentorEmailEv = (EditText) addMentorDialog.findViewById(R.id.edit_mentor_email);

        //Submit button
        Button mentorSubmitBtn = (Button) addMentorDialog.findViewById(R.id.edit_mentor_submit_button);
        mentorSubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String name = mentorNameEv.getText().toString().trim();
                String phone = mentorPhoneEv.getText().toString().trim();
                String email = mentorEmailEv.getText().toString().trim();

                //Insert New Mentor
                ContentValues values = new ContentValues();
                values.put(DBHelper.MENTOR_NAME, name);
                values.put(DBHelper.MENTOR_PHONE, phone);
                values.put(DBHelper.MENTOR_EMAIL, email);
                Uri test = getContentResolver().insert(CompanionContentProvider.MENTOR_URI, values);
                Log.d("Load Data", "Mentor URI Inserted: " + test);

                //Insert new Course Mentor relationship (
                int mId = Integer.parseInt(test.getLastPathSegment());
                int cId = Integer.parseInt(tempCourseUri.getLastPathSegment());
                values.clear();
                values.put(DBHelper.MENTOR_COURSE_M_ID, mId);
                values.put(DBHelper.MENTOR_COURSE_C_ID, cId);
                test = getContentResolver().insert(CompanionContentProvider.COURSE_MENTOR_URI, values);
                Log.d("Load Data", "Course/Mentor Relationship URI Inserted: " + test);
                Log.d("Load Data", "Course ID: " + cId);
                Log.d("Load Data", "Mentor ID: " + mId);

                addMentorDialog.cancel();

                setViews(tempCourseUri);
            }
        });

        //Delete Button
        Button mentorDeleteBtn = (Button) addMentorDialog.findViewById(R.id.edit_mentor_delete_button);
        mentorDeleteBtn.setEnabled(false);

        //Cancel Button
        Button mentorCancelBtn = (Button) addMentorDialog.findViewById(R.id.edit_mentor_cancel_button);
        mentorCancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                addMentorDialog.cancel();
            }
        });

        addMentorDialog.show();
    }

    private void add_note() {
        Intent menuIntent;
        menuIntent = new Intent(CoursesDetailActivity.this, CourseNotesActivity.class);
        startActivityForResult(menuIntent, ADD_NOTE_REQUEST_CODE);
    }

    private void deleteCourse() {
        int courseId = Integer.parseInt(tempCourseUri.getLastPathSegment());
        String courseFilter = DBHelper.COURSE_ID + "=" + courseId;
        String assessmentFilter = DBHelper.ASSESSMENT_COURSE_ID + "=" + courseId;
        String mentorFilter = DBHelper.MENTOR_COURSE_C_ID + "=" + courseId;
        getContentResolver().delete(CompanionContentProvider.COURSE_URI, courseFilter, null);
        getContentResolver().delete(CompanionContentProvider.ASSESSMENT_URI, assessmentFilter, null);
        getContentResolver().delete(CompanionContentProvider.COURSE_MENTOR_URI, mentorFilter, null);

        Intent intent = new Intent(CoursesDetailActivity.this, CoursesOverviewActivity.class);
        startActivity(intent);
    }

    private void editCourse() throws ParseException {
        ContentViewLoader.setCourseAction("update");

        String dialogCourseStatusText = courseStatusText;

        final Dialog editCourseDialog = new Dialog(CoursesDetailActivity.this);
        editCourseDialog.setContentView(R.layout.edit_course_data);

        TextView editCourseHeader = (TextView)  editCourseDialog.findViewById(R.id.course_edit_header);
        editCourseHeader.setText("Edit Course");

        //Set Views
        //Course Name
        final EditText editCourseNameEv = (EditText) editCourseDialog.findViewById(R.id.course_edit_name_field);
        editCourseNameEv.setText(courseNameText);

        //Course Status
        List<String> statusArray = new ArrayList<>();

        Cursor statusCursor = getContentResolver().query(CompanionContentProvider.STATUS_URI, DBHelper.STATUS_COLUMNS, null, null, null);
        statusCursor.moveToFirst();

        for(int i=0; i<statusCursor.getCount(); i++){
            String name = statusCursor.getString(statusCursor.getColumnIndex(DBHelper.STATUS_NAME));
            if(name.equals("Complete") || name.equals("In Progress") || name.equals("Not Attempted")) {
                statusArray.add(name);
            }
            statusCursor.moveToNext();
        }

        final Spinner editCourseStatusSpin = (Spinner)  editCourseDialog.findViewById(R.id.course_edit_status_spinner);
        ArrayAdapter<String> termStatusAdapter = new ArrayAdapter<>(editCourseDialog.getContext(),
                android.R.layout.simple_spinner_item,
                statusArray);
        termStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editCourseStatusSpin.setAdapter(termStatusAdapter);

        if (!dialogCourseStatusText.equals(null)) {
            int spinnerPosition = statusArray.indexOf(dialogCourseStatusText);
            editCourseStatusSpin.setSelection(spinnerPosition);
        }

        //Course Start
        final DatePicker editCourseStartPick = (DatePicker)  editCourseDialog.findViewById(R.id.course_edit_start_date_pick);
        SimpleDateFormat yearFormat  = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat  = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat  = new SimpleDateFormat("dd");
        Date initStartDate = new SimpleDateFormat("yyyy/MM/dd").parse(courseStartDate);
        int initStartYear = Integer.parseInt(yearFormat.format(initStartDate));
        int initStartMonth = Integer.parseInt(monthFormat.format(initStartDate))-1;
        int initStartDay = Integer.parseInt(dayFormat.format(initStartDate));

        editCourseStartPick.init(initStartYear, initStartMonth, initStartDay, null);

        boolean startCheck = contentLoader.loadCourseStartReminder(CoursesDetailActivity.this, tempCourseUri);
        final CheckBox editCourseStartChk = (CheckBox)  editCourseDialog.findViewById(R.id.course_edit_start_checkbox);
        editCourseStartChk.setChecked(startCheck);

        //Course End
        final DatePicker editCourseEndPick = (DatePicker)  editCourseDialog.findViewById(R.id.course_edit_end_date_pick);
        Date initEndDate = new SimpleDateFormat("yyyy/MM/dd").parse(courseEndDate);
        int initEndYear = Integer.parseInt(yearFormat.format(initEndDate));
        int initEndMonth = Integer.parseInt(monthFormat.format(initEndDate))-1;
        int initEndDay = Integer.parseInt(dayFormat.format(initEndDate));

        editCourseEndPick.init(initEndYear, initEndMonth, initEndDay, null);

        boolean endCheck = contentLoader.loadCourseEndReminder(CoursesDetailActivity.this, tempCourseUri);
        final CheckBox editCourseEndChk = (CheckBox)  editCourseDialog.findViewById(R.id.course_edit_end_checkbox);
        editCourseEndChk.setChecked(endCheck);

        //Course Description
        final EditText editCourseDescriptionEv = (EditText) editCourseDialog.findViewById(R.id.course_edit_description_field);
        editCourseDescriptionEv.setText(courseDescriptionText);

        //Assessment List
        final ListView editCourseAssessmentLv = (ListView)  editCourseDialog.findViewById(R.id.course_edit_assessment_list);

        final Cursor dialogAssessmentTypeCursor = getContentResolver().query(CompanionContentProvider.ASSESSMENT_TYPE_URI,
                DBHelper.ASSESSMENT_TYPE_COLUMNS, null, null, null);
        dialogAssessmentTypeCursor.moveToFirst();

        dialogAssessmentAdapter = new CourseAssessmentSelectCursorAdapter(CoursesDetailActivity.this, dialogAssessmentTypeCursor);
        editCourseAssessmentLv.setAdapter(dialogAssessmentAdapter);

        setDynamicHeight(editCourseAssessmentLv);

        //Mentor List
        final ListView editCourseMentorLv = (ListView)  editCourseDialog.findViewById(R.id.course_edit_mentor_list);

        final Cursor dialogCourseMentorCursor = getContentResolver().query(CompanionContentProvider.MENTOR_URI, DBHelper.MENTOR_COLUMNS,
                null, null, null);
        dialogCourseMentorCursor.moveToFirst();

        for(int i=0; i<dialogCourseMentorCursor.getCount(); i++){
            dialogCourseMentorCursor.moveToNext();
        }

        dialogCourseMentorCursor.moveToFirst();

        dialogMentorAdapter = new CourseMentorSelectCursorAdapter(CoursesDetailActivity.this, dialogCourseMentorCursor, 0);

        editCourseMentorLv.setAdapter(dialogMentorAdapter);

        setDynamicHeight(editCourseMentorLv);

        Button editCourseSubmit = (Button) editCourseDialog.findViewById(R.id.course_edit_submit_button);
        editCourseSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Count of Assessments Checked
                int assessmentCount = 0;
                for(int i=0; i<editCourseAssessmentLv.getChildCount(); i++){
                    CheckBox verify;
                    verify = (CheckBox) editCourseAssessmentLv.getChildAt(i).findViewById(R.id.list_item_checkbox);
                    if(verify.isChecked()){
                        assessmentCount = assessmentCount+1;
                    }
                }
                Log.d("Submit Data", "Assessment Count: " + assessmentCount);

                //Count of Mentors checked
                int mentorCount = 0;
                for(int i=0; i<editCourseMentorLv.getChildCount(); i++){
                    CheckBox verify;
                    verify = (CheckBox) editCourseMentorLv.getChildAt(i).findViewById(R.id.list_item_checkbox);
                    if (verify.isChecked()) {
                        mentorCount = mentorCount+1;
                    }
                }
                Log.d("Submit Data", "Mentor Count: " + mentorCount);

                //Verify at least one assessment and mentor checked
                if(mentorCount>0 && assessmentCount>0) {
                    Log.d("Load Data", "Submitting course update");
                    int courseIdToPass = Integer.parseInt(tempCourseUri.getLastPathSegment());
                    String submitCourseName = editCourseNameEv.getText().toString().trim();
                    String submitCourseStatus = editCourseStatusSpin.getSelectedItem().toString().trim();

                    int pickStartDay = editCourseStartPick.getDayOfMonth();
                    int pickStartMonth = editCourseStartPick.getMonth() + 1;
                    int pickStartYear = editCourseStartPick.getYear();

                    String submitCourseStart = contentLoader.convertDate(pickStartDay, pickStartMonth, pickStartYear);

                    int submitCourseStartReminder = 0;
                    if (editCourseStartChk.isChecked()) {
                        submitCourseStartReminder = 1;
                        contentLoader.setReminder(CoursesDetailActivity.this, pickStartYear, pickStartMonth, pickStartDay, "New Course Tomorrow", courseNameText, "Upcoming Course");

                    }

                    int pickEndDay = editCourseEndPick.getDayOfMonth();
                    int pickEndMonth = editCourseEndPick.getMonth() + 1;
                    int pickEndYear = editCourseEndPick.getYear();

                    String submitCourseEnd = contentLoader.convertDate(pickEndDay, pickEndMonth, pickEndYear);

                    int submitCourseEndReminder = 0;
                    if (editCourseEndChk.isChecked()) {
                        submitCourseEndReminder = 1;
                        contentLoader.setReminder(CoursesDetailActivity.this, pickEndYear, pickEndMonth, pickEndDay, "Course Ends Tomorrow", courseNameText, "Course Ending");

                    }
                    String submitCourseDescription = editCourseDescriptionEv.getText().toString().trim();

                    //Load Assessment IDs
                    String assessmentCourseFilter = DBHelper.ASSESSMENT_COURSE_ID + "=" + courseIdToPass;
                    Cursor dialogCourseAssessmentCursor = getContentResolver().query(CompanionContentProvider.ASSESSMENT_URI,
                            DBHelper.ASSESSMENT_COLUMNS, assessmentCourseFilter, null, null);

                    //Get Assessment and Mentor ID's
                    List<String> mentorIds = contentLoader.loadMentorIds(dialogCourseMentorCursor);

                    //Determine if assessment is checked or unchecked
                    List<String> assessmentCheckedValues = new ArrayList<>();
                    CheckBox aCb;
                    for (int i = 0; i < editCourseAssessmentLv.getChildCount(); i++) {
                        aCb = (CheckBox) editCourseAssessmentLv.getChildAt(i).findViewById(R.id.list_item_checkbox);
                        if (aCb.isChecked()) {
                            assessmentCheckedValues.add("1");
                        }
                        else {
                            assessmentCheckedValues.add("0");
                        }
                    }

                    //Determine if mentor is checked or unchecked
                    List<String> mentorCheckedValues = new ArrayList<>();
                    CheckBox mCb;
                    for(int i=0; i < editCourseMentorLv.getChildCount(); i++){
                        mCb = (CheckBox) editCourseMentorLv.getChildAt(i).findViewById(R.id.list_item_checkbox);
                        if(mCb.isChecked()){
                            mentorCheckedValues.add("1");
                        }
                        else{
                            mentorCheckedValues.add("0");
                        }
                    }
                    Log.d("Load Data", "Mentor ID Count: " + mentorIds.size());

                    try {
                        //Get current date and reminder date
                        Date now = new Date();
                        Date verifyStart = new SimpleDateFormat("yyyy/MM/dd").parse(submitCourseStart);
                        Date verifyEnd = new SimpleDateFormat("yyyy/MM/dd").parse(submitCourseEnd);
                        String message = "";

                        //If reminder checked verify that reminder date is after today
                        if (submitCourseStartReminder == 1) {
                            if (verifyStart.before(now)) {
                                message = message + "Start Date must be after today to have a reminder set.";
                            }
                        }
                        if(submitCourseEndReminder == 1) {
                            if(verifyEnd.before(now)){
                                message = message + "End Date must be after today to have a reminder set";
                            }
                        }
                        if(verifyStart.after(verifyEnd)){
                            message = message + "Start date cannot be after End date.";
                        }

                        if (message.length()==0) {
                            updateCourse(courseIdToPass, submitCourseName, submitCourseStatus, submitCourseStart, submitCourseStartReminder,
                                    submitCourseEnd, submitCourseEndReminder, submitCourseDescription);
                            updateAssessmentCourseIds(courseIdToPass, assessmentCheckedValues, dialogCourseAssessmentCursor);
                            updateMentorCourseRelationship(courseIdToPass, mentorIds, mentorCheckedValues);

                            editCourseDialog.cancel();
                            setViews(tempCourseUri);
                        }
                        else{
                            Toast.makeText(CoursesDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                //Error if none checked
                else{
                    Toast.makeText(CoursesDetailActivity.this, "At least one assessment and mentor must be selected to submit", Toast.LENGTH_SHORT).show();
                    Log.d("Submit Data", "At least one assessment and mentor must be selected to submit");
                }
            }
        });

        Button editCourseDelete = (Button) editCourseDialog.findViewById(R.id.course_edit_delete_button);
        editCourseDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                deleteCourse();

                editCourseDialog.cancel();
                Intent intent = new Intent(CoursesDetailActivity.this, CoursesOverviewActivity.class);
                startActivity(intent);
            }
        });

        Button editCourseCancel = (Button) editCourseDialog.findViewById(R.id.course_edit_cancel_button);
        editCourseCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                editCourseDialog.cancel();
            }
        });


        editCourseDialog.show();
    }

    private void updateCourse(int id, String name, String status, String start, int startReminder, String end, int endReminder, String description) {
        ContentValues values = new ContentValues();
        String filter = DBHelper.COURSE_ID + "=" + id;
        values.put(DBHelper.COURSE_NAME, name);
        values.put(DBHelper.COURSE_STATUS, status);
        values.put(DBHelper.COURSE_START_DATE, start);
        values.put(DBHelper.COURSE_START_REMINDER, startReminder);
        values.put(DBHelper.COURSE_END_DATE, end);
        values.put(DBHelper.COURSE_END_REMINDER, endReminder);
        values.put(DBHelper.COURSE_DESCRIPTION, description);
        getContentResolver().update(CompanionContentProvider.COURSE_URI, values, filter, null);
        Log.d("Load Data", "Update Complete for Course ID: " + id);
    }

    private void updateAssessmentCourseIds(int id, List<String> aCheckedValues, Cursor aCursor) {
        List<String> types = new ArrayList<>();
        types.add("Performance");
        types.add("Objective");

        //Get Assessment Type Checked Values (Performance/Objective)
        for(int i=0; i<types.size(); i++){
            String type = types.get(i);
            boolean isChecked = false;
            boolean isInTable = false;
            int assessmentId = 0;

            //Check if assessment type is already in table for this course
            aCursor.moveToFirst();
            for(int j=0; j<aCursor.getCount(); j++){
                String cursorType = aCursor.getString(aCursor.getColumnIndex(DBHelper.ASSESSMENT_TYPE));
                if(type.equals(cursorType)){
                    isInTable=true;
                    assessmentId = aCursor.getInt(aCursor.getColumnIndex(DBHelper.ASSESSMENT_ID));
                }
                aCursor.moveToNext();
            }

            int checkedInt = Integer.parseInt(aCheckedValues.get(i));
            if (checkedInt == 1) {
                isChecked = true;
            }

            //Delete Assessment
            if(isInTable && !isChecked){
                //Delete Assessment
                String filter = DBHelper.ASSESSMENT_ID + "=" + assessmentId;
                getContentResolver().delete(CompanionContentProvider.ASSESSMENT_URI, filter, null);
            }

            //Add Assessment
            if(!isInTable && isChecked){
                ContentValues values = new ContentValues();
                values.put(DBHelper.ASSESSMENT_COURSE_ID, id);
                values.put(DBHelper.ASSESSMENT_TYPE, type);
                values.put(DBHelper.ASSESSMENT_STATUS, "Not Attempted");
                getContentResolver().insert(CompanionContentProvider.ASSESSMENT_URI, values);
            }

        }
    }

    private void updateMentorCourseRelationship(int id, List<String> mIds, List<String> mCheckedValues) {
        Log.d("Load Data", "Course/Mentor Relationship update started");
        //Get Course/Mentor Relationship data for this course
        String cmFilter = DBHelper.MENTOR_COURSE_C_ID + "=" + id;
        Cursor cmCursor = getContentResolver().query(CompanionContentProvider.COURSE_MENTOR_URI, DBHelper.COURSE_MENTOR_COLUMNS, cmFilter,
                null, null);

        //Loop through all mentors
        for(int i=0; i<mIds.size(); i++){
            cmCursor.moveToFirst();
            int mentorId = Integer.parseInt(mIds.get(i));
            int cmId = 0;
            boolean isInTable = false;
            boolean isChecked = false;

            //Check if mentor has a Course/Mentor relationship already
            for(int j=0; j<cmCursor.getCount(); j++){
                int mentorRelationshipId = cmCursor.getInt(cmCursor.getColumnIndex(DBHelper.MENTOR_COURSE_M_ID));
                if(mentorId == mentorRelationshipId){
                    isInTable = true;
                    cmId = cmCursor.getInt(cmCursor.getColumnIndex(DBHelper.MENTOR_COURSE_ID));
                    Log.d("Load Data", "Mentor ID: " + mentorRelationshipId + " is in the table already");
                }
                cmCursor.moveToNext();
            }

            //Check if mentor is checked
            if(Integer.parseInt(mCheckedValues.get(i))==1){
                isChecked=true;
                Log.d("Load Data", "Mentor ID: " + mentorId + " is checked");
            }

            //Delete Relationship
            if(isInTable && !isChecked){
                String filter = DBHelper.MENTOR_COURSE_ID + "=" + cmId;
                getContentResolver().delete(CompanionContentProvider.COURSE_MENTOR_URI, filter, null);
                Log.d("Load Data", "Relationship Deleted");
            }

            //Add Relationship
            if(!isInTable && isChecked){
                ContentValues values = new ContentValues();
                values.put(DBHelper.MENTOR_COURSE_C_ID, id);
                values.put(DBHelper.MENTOR_COURSE_M_ID, mentorId);
                getContentResolver().insert(CompanionContentProvider.COURSE_MENTOR_URI, values);
                Log.d("Load Data", "Relationship Inserted");
            }
        }
    }

    public void setViews(Uri uri) {
        //Set Course Name
        courseNameText = contentLoader.loadCourseName(CoursesDetailActivity.this, uri);
        courseTv = (TextView) findViewById(R.id.course_detail_name_header);
        courseTv.setText(courseNameText);
        courseTv.requestFocus();

        //Set Course  Start Date
        courseStartDate = contentLoader.loadCourseStart(CoursesDetailActivity.this, uri);
        courseStartText = "Start Date: " + courseStartDate;
        courseStartTv = (TextView) findViewById(R.id.course_start_date_Text);
        courseStartTv.setText(courseStartText);
        courseStartTv.requestFocus();

        //Set Course Status
        courseStatusText = contentLoader.loadCourseStatus(CoursesDetailActivity.this, uri);
        courseStatusTv = (TextView) findViewById(R.id.course_detail_status_text);
        courseStatusTv.setText(courseStatusText);
        courseStatusTv.requestFocus();

        //Set Course End Date
        courseEndDate = contentLoader.loadCourseEnd(CoursesDetailActivity.this, uri);
        if(courseStatusText.equals("Complete")){
            courseEndText = "End Date: " + courseEndDate;
        }
        else {
            courseEndText = "Expected End Date: " + courseEndDate;
        }
        courseEndTv = (TextView) findViewById(R.id.course_end_Date_text);
        courseEndTv.setText(courseEndText);
        courseEndTv.requestFocus();

        //Set Course Description
        courseDescriptionText = contentLoader.loadCourseDescription(CoursesDetailActivity.this, uri);
        courseDescriptionTv = (TextView) findViewById(R.id.course_detail_description_text);
        courseDescriptionTv.setText(courseDescriptionText);
        courseDescriptionTv.requestFocus();

        //Set Assessment ListView
        courseAssessmentLv = (ListView) findViewById(R.id.course_assessments_list);

        SharedPreferences pref = getSharedPreferences(CoursesOverviewActivity.COURSE_PREFS, 0);
        Long id = pref.getLong("courseUri", 0);
        String assessmentFilter = DBHelper.ASSESSMENT_COURSE_ID + "=" + id;

        Cursor courseAssessmentCursor = getContentResolver().query(CompanionContentProvider.ASSESSMENT_URI, DBHelper.ASSESSMENT_COLUMNS,
                assessmentFilter, null, null);
        courseAssessmentCursor.moveToFirst();
        assessmentAdapter = new CourseAssessmentCursorAdapter(CoursesDetailActivity.this, courseAssessmentCursor, 0);

        courseAssessmentLv.setAdapter(assessmentAdapter);

        setDynamicHeight(courseAssessmentLv);

        //Set Mentor ListView
        courseMentorLv = (ListView) findViewById(R.id.course_mentor_list);

        final String mentorFilter = DBHelper.MENTOR_COURSE_C_ID + "=" + id;

        Cursor courseMentorCursor = getContentResolver().query(CompanionContentProvider.COURSE_MENTOR_URI, DBHelper.COURSE_MENTOR_COLUMNS,
                mentorFilter, null, null);
        courseMentorCursor.moveToFirst();
        mentorAdapter = new CourseMentorCursorAdapter(CoursesDetailActivity.this, courseMentorCursor, 0);

        courseMentorLv.setAdapter(mentorAdapter);

        setDynamicHeight(courseMentorLv);

        //Assessment List View Click Listener
        courseAssessmentLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(CoursesDetailActivity.this, AssessmentsDetailActivity.class);
                Uri uri = Uri.parse(CompanionContentProvider.ASSESSMENT_URI + "/" + id);
                intent.putExtra(CompanionContentProvider.ASSESSMENT_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEW_ASSESSMENT_REQUEST_CODE);
            }
        });

        //Mentor List View Click Listener
        courseMentorLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                final Dialog editMentorDialog = new Dialog(CoursesDetailActivity.this);
                editMentorDialog.setContentView(R.layout.edit_mentor);

                final String testFilter = DBHelper.MENTOR_COURSE_ID + "=" + id;
                Log.d("Load Data", "Mentor Filter: " + testFilter);
                Cursor testCursor = getContentResolver().query(CompanionContentProvider.COURSE_MENTOR_URI,
                        DBHelper.COURSE_MENTOR_COLUMNS, testFilter, null, null);
                testCursor.moveToFirst();

                final int mentorId = testCursor.getInt(testCursor.getColumnIndex(DBHelper.MENTOR_COURSE_M_ID));
                final String editMentorFilter = DBHelper.MENTOR_ID + "=" + mentorId;
                Cursor editMentorCursor = getContentResolver().query(CompanionContentProvider.MENTOR_URI, DBHelper.MENTOR_COLUMNS,
                        editMentorFilter, null, null);
                editMentorCursor.moveToFirst();

                Log.d("Load Data", "Mentor Count: " + editMentorCursor.getCount());

                TextView addMentorHeader = (TextView) editMentorDialog.findViewById(R.id.edit_mentor_header);
                addMentorHeader.setText("Edit Mentor");

                //Set Views
                //Mentor Name
                final EditText mentorNameEv = (EditText) editMentorDialog.findViewById(R.id.edit_mentor_name);
                mentorNameEv.setText(editMentorCursor.getString(editMentorCursor.getColumnIndex(DBHelper.MENTOR_NAME)));

                //Mentor Phone
                final EditText mentorPhoneEv = (EditText) editMentorDialog.findViewById(R.id.edit_mentor_phone);
                mentorPhoneEv.setText(editMentorCursor.getString(editMentorCursor.getColumnIndex(DBHelper.MENTOR_PHONE)));

                //Mentor Email
                final EditText mentorEmailEv = (EditText) editMentorDialog.findViewById(R.id.edit_mentor_email);
                mentorEmailEv.setText(editMentorCursor.getString(editMentorCursor.getColumnIndex(DBHelper.MENTOR_EMAIL)));

            //Edit Mentor Button Handlers
                //Submit Button
                Button editMentorSubmitBtn = (Button) editMentorDialog.findViewById(R.id.edit_mentor_submit_button);
                editMentorSubmitBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        String name = mentorNameEv.getText().toString().trim();
                        String phone = mentorPhoneEv.getText().toString().trim();
                        String email = mentorEmailEv.getText().toString().trim();

                        ContentValues values = new ContentValues();
                        values.put(DBHelper.MENTOR_NAME, name);
                        values.put(DBHelper.MENTOR_PHONE, phone);
                        values.put(DBHelper.MENTOR_EMAIL, email);
                        getContentResolver().update(CompanionContentProvider.MENTOR_URI, values, editMentorFilter, null);

                        editMentorDialog.cancel();

                        setViews(tempCourseUri);
                    }
                });

                //Delete Button
                Button editMentorDeleteBtn = (Button) editMentorDialog.findViewById(R.id.edit_mentor_delete_button);
                editMentorDeleteBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        getContentResolver().delete(CompanionContentProvider.MENTOR_URI, editMentorFilter, null);
                        String cmFilter = DBHelper.MENTOR_COURSE_M_ID + "=" + mentorId;
                        getContentResolver().delete(CompanionContentProvider.COURSE_MENTOR_URI, cmFilter, null);

                        editMentorDialog.cancel();

                        setViews(tempCourseUri);
                    }
                });


                //Cancel Button
                Button editMentorCancelBtn = (Button) editMentorDialog.findViewById(R.id.edit_mentor_cancel_button);
                editMentorCancelBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        editMentorDialog.cancel();
                    }
                });

                editMentorDialog.show();
            }
        });
    }

    public static void setDynamicHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        //check adapter if null
        if (adapter == null) {
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = height + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();
    }
}
