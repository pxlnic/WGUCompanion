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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CoursesOverviewActivity extends AppCompatActivity{
    //ListView Declaration
    private ListView courseOverviewLv;
    private TextView programTv;
    private TextView progressTv;
    private ProgressBar progressB;

    //Program and CU Text Variables
    private String programText;
    private int completedCUs = 0;
    private int totalCUs = 0;
    private String progressText = "";

    //Activity Variables
    private int courseID;
    private CourseViewCursorAdapter adapter;
    CourseAssessmentSelectCursorAdapter dialogAssessmentAdapter;
    CourseMentorSelectCursorAdapter dialogMentorAdapter;
    //Load ContentViewLoader
    ContentViewLoader contentLoader = new ContentViewLoader();
    private static final int NEW_COURSE_REQUEST_CODE = 2002;
    private static final int VIEW_COURSE_REQUEST_CODE = 2003;
    private static String action = "";
    public static final String COURSE_PREFS = "Course_Prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_overview);

        setViews();

        courseOverviewLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(CoursesOverviewActivity.this, CoursesDetailActivity.class);
                Uri uri = Uri.parse(CompanionContentProvider.COURSE_URI + "/" + id);

                SharedPreferences passedUri = getSharedPreferences(COURSE_PREFS, 0);
                SharedPreferences.Editor editor = passedUri.edit();
                editor.putLong("courseUri", id);
                editor.commit();

                intent.putExtra(CompanionContentProvider.COURSE_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEW_COURSE_REQUEST_CODE);
            }
        });
    }

    private void setViews() {
        //Set Program Name
        programText = contentLoader.loadProgramName(CoursesOverviewActivity.this);
        programTv = (TextView) findViewById(R.id.program_name);
        programTv.setText(programText);
        programTv.requestFocus();

        //Set Progress/CU's
        completedCUs = contentLoader.loadCompletedCU(CoursesOverviewActivity.this);
        totalCUs = contentLoader.loadTotalCU(CoursesOverviewActivity.this);
        progressText = completedCUs + "/" + totalCUs + " CUs";
        progressTv = (TextView) findViewById(R.id.cu_progress_count);
        progressTv.setText(progressText);
        progressTv.requestFocus();

        //Set ProgressBar
        progressB = (ProgressBar) findViewById(R.id.cu_progress_bar);
        progressB.setMax(totalCUs);
        progressB.setProgress(completedCUs);

        //Load Courses
        courseOverviewLv = (ListView) findViewById(R.id.courses_overview_list_view);

        Cursor courseCursor = getContentResolver().query(CompanionContentProvider.COURSE_URI, DBHelper.COURSE_COLUMNS, null, null, null);
        courseCursor.moveToFirst();
        adapter = new CourseViewCursorAdapter(CoursesOverviewActivity.this, courseCursor,0);

        courseOverviewLv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_courses_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.add_course:
                addCourse();
                break;
            case R.id.view_completed_courses:
                viewCompleted();
                break;
            case R.id.view_remaining_courses:
                viewRemaining();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void viewRemaining() {
        Toast.makeText(CoursesOverviewActivity.this, "Filtered to Remaining Courses", Toast.LENGTH_SHORT).show();
    }

    private void viewCompleted() {
         Toast.makeText(CoursesOverviewActivity.this, "Filtered to Completed Courses", Toast.LENGTH_SHORT).show();
    }

    private void addCourse() {
        final Dialog addCourseDialog = new Dialog(CoursesOverviewActivity.this);
        addCourseDialog.setContentView(R.layout.edit_course_data);

        ContentViewLoader.setCourseAction("insert");

        TextView addCourseHeader = (TextView)  addCourseDialog.findViewById(R.id.course_edit_header);
        addCourseHeader.setText("New Course");

        //Set Views
        //Course Name
        final EditText editCourseNameEv = (EditText) addCourseDialog.findViewById(R.id.course_edit_name_field);

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

        final Spinner editCourseStatusSpin = (Spinner)  addCourseDialog.findViewById(R.id.course_edit_status_spinner);
        ArrayAdapter<String> termStatusAdapter = new ArrayAdapter<>(addCourseDialog.getContext(),
                android.R.layout.simple_spinner_item,
                statusArray);
        termStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editCourseStatusSpin.setAdapter(termStatusAdapter);

        //Course Start
        final DatePicker editCourseStartPick = (DatePicker)  addCourseDialog.findViewById(R.id.course_edit_start_date_pick);
        final CheckBox editCourseStartChk = (CheckBox)  addCourseDialog.findViewById(R.id.course_edit_start_checkbox);

        //Course End
        final DatePicker editCourseEndPick = (DatePicker)  addCourseDialog.findViewById(R.id.course_edit_end_date_pick);
        final CheckBox editCourseEndChk = (CheckBox)  addCourseDialog.findViewById(R.id.course_edit_end_checkbox);

        //Course Description
        final EditText editCourseDescriptionEv = (EditText) addCourseDialog.findViewById(R.id.course_edit_description_field);

        //Assessment List
        final ListView editCourseAssessmentLv = (ListView)  addCourseDialog.findViewById(R.id.course_edit_assessment_list);

        final Cursor dialogAssessmentTypeCursor = getContentResolver().query(CompanionContentProvider.ASSESSMENT_TYPE_URI,
                DBHelper.ASSESSMENT_TYPE_COLUMNS, null, null, null);
        dialogAssessmentTypeCursor.moveToFirst();

        dialogAssessmentAdapter = new CourseAssessmentSelectCursorAdapter(CoursesOverviewActivity.this, dialogAssessmentTypeCursor);
        editCourseAssessmentLv.setAdapter(dialogAssessmentAdapter);

        setDynamicHeight(editCourseAssessmentLv);

        //Mentor List
        final ListView editCourseMentorLv = (ListView)  addCourseDialog.findViewById(R.id.course_edit_mentor_list);

        final Cursor dialogCourseMentorCursor = getContentResolver().query(CompanionContentProvider.MENTOR_URI, DBHelper.MENTOR_COLUMNS,
                null, null, null);
        dialogCourseMentorCursor.moveToFirst();

        for(int i=0; i<dialogCourseMentorCursor.getCount(); i++){
            dialogCourseMentorCursor.moveToNext();
        }

        dialogCourseMentorCursor.moveToFirst();

        dialogMentorAdapter = new CourseMentorSelectCursorAdapter(CoursesOverviewActivity.this, dialogCourseMentorCursor, 0);

        editCourseMentorLv.setAdapter(dialogMentorAdapter);

        setDynamicHeight(editCourseMentorLv);

        Button addCourseSubmit = (Button) addCourseDialog.findViewById(R.id.course_edit_submit_button);
        addCourseSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
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
                if(mentorCount>0) {
                    Log.d("Load Data", "Submitting course add");
                    String submitCourseName = editCourseNameEv.getText().toString().trim();
                    String submitCourseStatus = editCourseStatusSpin.getSelectedItem().toString().trim();

                    int pickStartDay = editCourseStartPick.getDayOfMonth();
                    int pickStartMonth = editCourseStartPick.getMonth() + 1;
                    int pickStartYear = editCourseStartPick.getYear();

                    String submitCourseStart = contentLoader.convertDate(pickStartDay, pickStartMonth, pickStartYear);

                    int submitCourseStartReminder = 0;
                    if (editCourseStartChk.isChecked()) {
                        submitCourseStartReminder = 1;
                        contentLoader.setReminder(CoursesOverviewActivity.this, pickStartYear, pickStartMonth, pickStartDay, "New Course Tomorrow", submitCourseName, "Upcoming Course");
                    }


                    int pickEndDay = editCourseEndPick.getDayOfMonth();
                    int pickEndMonth = editCourseEndPick.getMonth() + 1;
                    int pickEndYear = editCourseEndPick.getYear();

                    String submitCourseEnd = contentLoader.convertDate(pickEndDay, pickEndMonth, pickEndYear);

                    int submitCourseEndReminder = 0;
                    if (editCourseEndChk.isChecked()) {
                        submitCourseEndReminder = 1;
                        contentLoader.setReminder(CoursesOverviewActivity.this, pickEndYear, pickEndMonth, pickEndDay, "Course Ends Tomorrow", submitCourseName, "Course Ending");
                    }
                    String submitCourseDescription = editCourseDescriptionEv.getText().toString().trim();

                    //Get Mentor ID's
                    List<String> mentorIds = contentLoader.loadMentorIds(dialogCourseMentorCursor);

                    //Determine if assessment is checked or unchecked
                    List<String> assessmentCheckedValues = new ArrayList<>();
                    CheckBox aCb;
                    for (int i = 0; i < editCourseAssessmentLv.getChildCount(); i++) {
                        aCb = (CheckBox) editCourseAssessmentLv.getChildAt(i).findViewById(R.id.list_item_checkbox);
                        if (aCb.isChecked()) {
                            assessmentCheckedValues.add("1");
                        } else {
                            assessmentCheckedValues.add("0");
                        }
                    }

                    //Determine if mentor is checked or unchecked
                    List<String> mentorCheckedValues = new ArrayList<>();
                    CheckBox mCb;
                    for (int i = 0; i < editCourseMentorLv.getChildCount(); i++) {
                        mCb = (CheckBox) editCourseMentorLv.getChildAt(i).findViewById(R.id.list_item_checkbox);
                        if (mCb.isChecked()) {
                            mentorCheckedValues.add("1");

                        } else {
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
                            int courseIdToPass = insertCourse(submitCourseName, submitCourseStatus, submitCourseStart, submitCourseStartReminder,
                                    submitCourseEnd, submitCourseEndReminder, submitCourseDescription);
                            updateAssessmentCourseIds(courseIdToPass, assessmentCheckedValues, submitCourseEnd);
                            updateMentorCourseRelationship(courseIdToPass, mentorIds, mentorCheckedValues);

                            addCourseDialog.cancel();
                            setViews();
                        }
                        else{
                            Toast.makeText(CoursesOverviewActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                //Error if none checked
                else{
                    Toast.makeText(CoursesOverviewActivity.this, "At least one assessment and mentor must be selected to submit", Toast.LENGTH_SHORT).show();
                    Log.d("Submit Data", "At least one assessment and mentor must be selected to submit");
                }
            }
        });

        Button addCourseDelete = (Button) addCourseDialog.findViewById(R.id.course_edit_delete_button);
        addCourseDelete.setEnabled(false);

        Button addCourseCancel = (Button) addCourseDialog.findViewById(R.id.course_edit_cancel_button);
        addCourseCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                addCourseDialog.cancel();
            }
        });

        addCourseDialog.show();
    }

    private int insertCourse(String name, String status, String start, int startReminder, String end, int endReminder, String description) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COURSE_NAME, name);
        values.put(DBHelper.COURSE_STATUS, status);
        values.put(DBHelper.COURSE_START_DATE, start);
        values.put(DBHelper.COURSE_START_REMINDER, startReminder);
        values.put(DBHelper.COURSE_END_DATE, end);
        values.put(DBHelper.COURSE_END_REMINDER, endReminder);
        values.put(DBHelper.COURSE_DESCRIPTION, description);
        Uri uri = getContentResolver().insert(CompanionContentProvider.COURSE_URI, values);
        Log.d("Load Data", "Course Inserted with URI: " + uri);
        int id = Integer.parseInt(uri.getLastPathSegment());
        Log.d("Load Data", "Course ID: " + id);
        return id;
    }

    private void updateAssessmentCourseIds(int id, List<String> aCheckedValues, String date) {
        List<String> types = new ArrayList<>();
        types.add("Performance");
        types.add("Objective");

        //Get Assessment Type Checked Values (Performance/Objective)
        for(int i=0; i<types.size(); i++){
            String type = types.get(i);
            boolean isChecked = false;

            int checkedInt = Integer.parseInt(aCheckedValues.get(i));
            if (checkedInt == 1) {
                isChecked = true;
            }

            //Add Assessment
            if(isChecked){
                ContentValues values = new ContentValues();
                values.put(DBHelper.ASSESSMENT_COURSE_ID, id);
                values.put(DBHelper.ASSESSMENT_TYPE, type);
                values.put(DBHelper.ASSESSMENT_STATUS, "Not Attempted");
                values.put(DBHelper.ASSESSMENT_DUE_DATE, date);
                values.put(DBHelper.ASSESSMENT_DUE_DATE_REMINDER, 0);
                getContentResolver().insert(CompanionContentProvider.ASSESSMENT_URI, values);
            }

        }
    }

    private void updateMentorCourseRelationship(int id, List<String> mIds, List<String> mCheckedValues) {
        Log.d("Load Data", "Course/Mentor Relationship update started");
        //Get Course/Mentor Relationship data for this course
        String cmFilter = DBHelper.MENTOR_COURSE_C_ID + "=" + id;
        Log.d("Load Data", "Course ID: " + id + " is inserting a CM Relationship");
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

    public static String getCourseAction(){
        return action;
    }
}
