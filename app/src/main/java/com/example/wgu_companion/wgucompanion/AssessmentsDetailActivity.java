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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssessmentsDetailActivity extends AppCompatActivity{
    //Activity Variables
    private String action = "";
    CourseNotesAdapter adapter;
    private int courseId;
    private String assessmentGoalDate = "";
    ContentViewLoader loader = new ContentViewLoader();

    Uri uriToPass;
    private static final int ADD_NOTE_REQUEST_CODE = 3002;
    private static final int VIEW_NOTE_REQUEST_CODE = 3003;

    //Term Name/Progress/Dates Variables
    private String assessmentCourseNameText = "";
    private String assessmentTypeText = "";
    private String assessmentExpectedDueDateText = "";
    private String assessmentStatusText = "";

    //View Declarations
    TextView assessmentCourseTv;
    TextView assessmentTypeTv;
    TextView assessmentEndTv;
    TextView assessmentStatusTv;
    ListView assessmentNotesLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessments_detail);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(CompanionContentProvider.ASSESSMENT_ITEM_TYPE);

        setTitle("Assessment Details");

        Log.d("Load Data", "Uri" + uri);

        if(uri==null){
            SharedPreferences pref = getSharedPreferences(AssessmentOverviewActivity.ASSESSMENT_PREFS, 0);
            Long id = pref.getLong("assessmentUri", 0);
            uri = Uri.parse(CompanionContentProvider.ASSESSMENT_URI + "/" + id);
        }

        uriToPass = uri;
        setViews(uri);
    }

    @Override
    protected void onResume(){
        super.onResume();
        setViews(uriToPass);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_assessment_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.edit_assessment:
                try {
                    editAssessment();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.delete_assessment:
                deleteAssessment();
                break;
            case R.id.add_note:
                addNote();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNote() {
        SharedPreferences passedUri = getSharedPreferences(AssessmentOverviewActivity.ASSESSMENT_PREFS, 0);
        SharedPreferences.Editor editor = passedUri.edit();
        editor.putInt("courseId", courseId);
        Log.d("Load Data", "Passing Course ID: " + courseId);
        editor.commit();

        Intent menuIntent;
        menuIntent = new Intent(AssessmentsDetailActivity.this, CourseNotesActivity.class);
        startActivityForResult(menuIntent, ADD_NOTE_REQUEST_CODE);
    }

    private void deleteAssessment() {
        int assessmentId = Integer.parseInt(uriToPass.getLastPathSegment());
        String filter = DBHelper.ASSESSMENT_ID + "=" + assessmentId;
        getContentResolver().delete(CompanionContentProvider.ASSESSMENT_URI, filter, null);
    }

    private void editAssessment() throws ParseException {
        List<String> courseArray = new ArrayList<>();
        List<String> statusArray = new ArrayList<>();
        List<String> typeArray = new ArrayList<>();

        final Dialog editAssessmentDialog = new Dialog(AssessmentsDetailActivity.this);
        editAssessmentDialog.setContentView(R.layout.edit_assessment_data);

        //Set Header
        TextView header = (TextView) editAssessmentDialog.findViewById(R.id.assessment_edit_header);
        header.setText("Edit Assessment");

    //Set Course Spinner
        Cursor courseCursor = getContentResolver().query(CompanionContentProvider.COURSE_URI, DBHelper.COURSE_COLUMNS, null, null, null);
        courseCursor.moveToFirst();

        //Add courses to spinner
        for(int i=0; i<courseCursor.getCount(); i++){
            String name = courseCursor.getString(courseCursor.getColumnIndex(DBHelper.COURSE_NAME));
            courseArray.add(name);
            courseCursor.moveToNext();
        }

        //Bind spinner
        final Spinner assessmentCourseSpin = (Spinner) editAssessmentDialog.findViewById(R.id.assessment_edit_course_spinner);
        ArrayAdapter<String> assessmentCourseAdapter = new ArrayAdapter<>(editAssessmentDialog.getContext(),
                android.R.layout.simple_spinner_item,
                courseArray);
        assessmentCourseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assessmentCourseSpin.setAdapter(assessmentCourseAdapter);

        //Set default value
        int courseSpinnerPosition = courseArray.indexOf(assessmentCourseNameText);
        assessmentCourseSpin.setSelection(courseSpinnerPosition);

    //Set Type Spinner
        Cursor typeCursor = getContentResolver().query(CompanionContentProvider.ASSESSMENT_TYPE_URI, DBHelper.ASSESSMENT_TYPE_COLUMNS,
                null, null, null);
        typeCursor.moveToFirst();

        //Add types to spinner
        for(int i=0; i<typeCursor.getCount(); i++){
            String type = typeCursor.getString(typeCursor.getColumnIndex(DBHelper.ASSESSMENT_TYPE_NAME));
            typeArray.add(type);
            typeCursor.moveToNext();
            Log.d("Load Data", "Type Added: " + type);
        }

        final Spinner editAssessmentTypeSpin = (Spinner) editAssessmentDialog.findViewById(R.id.assessment_edit_type_spinner);
        ArrayAdapter<String> assessmentTypeAdapter = new ArrayAdapter<>(editAssessmentDialog.getContext(),
                android.R.layout.simple_spinner_item,
                typeArray);
        assessmentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editAssessmentTypeSpin.setAdapter(assessmentTypeAdapter);

        //Set default value
        int typeSpinnerPosition = typeArray.indexOf(assessmentTypeText.trim());
        editAssessmentTypeSpin.setSelection(typeSpinnerPosition);
        Log.d("Load Data", "Type Array Size: " + typeArray.size());
        Log.d("Load Data", "Assessment Type: " + assessmentTypeText);
        Log.d("Load Data", "Assessment Type Location in Spinner: " + typeSpinnerPosition);

    //Set Status Spinner
        Cursor statusCursor = getContentResolver().query(CompanionContentProvider.STATUS_URI, DBHelper.STATUS_COLUMNS, null, null, null);
        statusCursor.moveToFirst();

        for(int i=0; i<statusCursor.getCount(); i++){
            String name = statusCursor.getString(statusCursor.getColumnIndex(DBHelper.STATUS_NAME));
            if(name.equals("Passed") || name.equals("Failed") || name.equals("Not Attempted")) {
                statusArray.add(name);
            }
            statusCursor.moveToNext();
        }

        final Spinner editAssessmentStatusSpin = (Spinner)  editAssessmentDialog.findViewById(R.id.assessment_edit_status_spinner);
        ArrayAdapter<String> assessmentStatusAdapter = new ArrayAdapter<>(editAssessmentDialog.getContext(),
                android.R.layout.simple_spinner_item,
                statusArray);
        assessmentStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editAssessmentStatusSpin.setAdapter(assessmentStatusAdapter);

        //Set default value
        int statusSpinnerPosition = statusArray.indexOf(assessmentStatusText);
        editAssessmentStatusSpin.setSelection(statusSpinnerPosition);

        Log.d("Load Data", "Status Array Size: " + statusArray.size());
        Log.d("Load Data", "Assessment Status: " + assessmentStatusText);
        Log.d("Load Data", "Assessment Status Location in Spinner: " + statusSpinnerPosition);

        //Set Goal Date
        final DatePicker assessmentGoalDatePk = (DatePicker) editAssessmentDialog.findViewById(R.id.assessment_edit_date_picker);
        SimpleDateFormat yearFormat  = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat  = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat  = new SimpleDateFormat("dd");
        Date initGoalDate = new SimpleDateFormat("yyyy/MM/dd").parse(assessmentGoalDate);
        int initEndYear = Integer.parseInt(yearFormat.format(initGoalDate));
        int initEndMonth = Integer.parseInt(monthFormat.format(initGoalDate))-1;
        int initEndDay = Integer.parseInt(dayFormat.format(initGoalDate));

        assessmentGoalDatePk.init(initEndYear, initEndMonth, initEndDay, null);

        final CheckBox assessmentGoalReminderChk = (CheckBox) editAssessmentDialog.findViewById(R.id.assessment_edit_reminder_checkbox);
        boolean endCheck = loader.loadAssessmentGoalReminder(AssessmentsDetailActivity.this, uriToPass);
        assessmentGoalReminderChk.setChecked(endCheck);


        //Button Handlers
        //Submit Button
        Button editAssessmentSubmitBtn = (Button) editAssessmentDialog.findViewById(R.id.assessment_edit_submit_button);
        editAssessmentSubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int updateAssessmentId = Integer.parseInt(uriToPass.getLastPathSegment());
                String updateAssessmentType = editAssessmentTypeSpin.getSelectedItem().toString().trim();
                String updateAssessmentStatus = editAssessmentStatusSpin.getSelectedItem().toString().trim();

                int pickDay  = assessmentGoalDatePk.getDayOfMonth();
                int pickMonth = assessmentGoalDatePk.getMonth() + 1;
                int pickYear = assessmentGoalDatePk.getYear();

                String updateAssessmentDate = loader.convertDate(pickDay, pickMonth, pickYear);
                int updateAssessmentReminder = 0;
                if(assessmentGoalReminderChk.isChecked()){
                    updateAssessmentReminder = 1;
                    loader.setReminder(AssessmentsDetailActivity.this, pickYear, pickMonth, pickDay, "Assessment Tomorrow", assessmentCourseNameText, "Upcoming Assessment");
                }

                try {
                    //Get current date and reminder date
                    boolean isOk = false;
                    Date now = new Date();
                    Date verifyGoal = new SimpleDateFormat("yyyy/MM/dd").parse(updateAssessmentDate);

                    //If reminder checked verify that reminder date is after today
                    if(updateAssessmentReminder==1) {
                        if (verifyGoal.before(now)) {
                            Toast.makeText(AssessmentsDetailActivity.this, "Date must be after today to have a reminder set.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            isOk = true;
                        }
                    }
                    if(isOk){
                        updateAssessment(updateAssessmentId, courseId, updateAssessmentType, updateAssessmentStatus, updateAssessmentDate, updateAssessmentReminder);

                        editAssessmentDialog.cancel();
                        setViews(uriToPass);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        //Delete Button
        Button editAssessmentDeleteBtn = (Button) editAssessmentDialog.findViewById(R.id.assessment_edit_delete_button);
        editAssessmentDeleteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                deleteAssessment();

                editAssessmentDialog.cancel();
                Intent intent = new Intent(AssessmentsDetailActivity.this, AssessmentOverviewActivity.class);
                startActivity(intent);
            }
        });

        //Cancel Button
        Button editAssessmentCancelBtn = (Button) editAssessmentDialog.findViewById(R.id.assessment_edit_cancel_button);
        editAssessmentCancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                editAssessmentDialog.cancel();
            }
        });

        editAssessmentDialog.show();
    }

    private void updateAssessment(int id, int cid, String type, String status, String date, int reminder) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.ASSESSMENT_COURSE_ID, cid);
        values.put(DBHelper.ASSESSMENT_TYPE, type);
        values.put(DBHelper.ASSESSMENT_STATUS, status);
        values.put(DBHelper.ASSESSMENT_DUE_DATE, date);
        values.put(DBHelper.ASSESSMENT_DUE_DATE_REMINDER, reminder);
        String filter = DBHelper.ASSESSMENT_ID + "=" + id;
        getContentResolver().update(CompanionContentProvider.ASSESSMENT_URI, values, filter, null);
    }

    public void setViews(Uri uri) {
        ContentViewLoader contentLoader = new ContentViewLoader();

        //Set Assessment Course Name
        assessmentCourseNameText = contentLoader.loadAssessmentCourseName(AssessmentsDetailActivity.this, uri);
        assessmentCourseTv = (TextView) findViewById(R.id.assessment_course_name_header);
        assessmentCourseTv.setText(assessmentCourseNameText);
        assessmentCourseTv.requestFocus();

        //Set Assessment Type
        assessmentTypeText = "Assessment Type: " + contentLoader.loadAssessmentType(AssessmentsDetailActivity.this, uri);
        assessmentTypeTv = (TextView) findViewById(R.id.assessment_type_text);
        assessmentTypeTv.setText(assessmentTypeText);
        assessmentTypeTv.requestFocus();

        //Set Assessment Due Date
        assessmentGoalDate = contentLoader.loadAssessmentDueDate(AssessmentsDetailActivity.this, uri);
        assessmentExpectedDueDateText = "Goal Date: " + assessmentGoalDate;
        assessmentEndTv = (TextView) findViewById(R.id.assessment_due_date_text);
        assessmentEndTv.setText(assessmentExpectedDueDateText);
        assessmentEndTv.requestFocus();

        //Set Assessment Status
        assessmentStatusText = contentLoader.loadAssessmentStatus(AssessmentsDetailActivity.this, uri);
        assessmentStatusTv = (TextView) findViewById(R.id.assessment_status_text);
        assessmentStatusTv.setText(assessmentStatusText);
        assessmentStatusTv.requestFocus();

        //Set Assessment Notes ListView
        assessmentNotesLv = (ListView) findViewById(R.id.assessment_detail_note_list);

        String courseFilter = DBHelper.COURSE_NAME + "= '" + assessmentCourseNameText + "'";
        Cursor courseIdCursor =  getContentResolver().query(CompanionContentProvider.COURSE_URI, DBHelper.COURSE_COLUMNS, courseFilter,
                null, null);
        courseIdCursor.moveToFirst();
        courseId = courseIdCursor.getInt(courseIdCursor.getColumnIndex(DBHelper.COURSE_ID));

        String filter = DBHelper.NOTE_COURSE_ID + "=" + courseId;
        Cursor notesCursor = getContentResolver().query(CompanionContentProvider.NOTE_URI, DBHelper.NOTE_COLUMNS, filter, null, null);
        adapter = new CourseNotesAdapter(AssessmentsDetailActivity.this, notesCursor, 0);

        assessmentNotesLv.setAdapter(adapter);

        assessmentNotesLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(AssessmentsDetailActivity.this, CourseNotesActivity.class);
                Uri uri = Uri.parse(CompanionContentProvider.NOTE_URI + "/" + id);
                intent.putExtra(CompanionContentProvider.NOTE_ITEM_TYPE, uri);

                SharedPreferences passedUri = getSharedPreferences(AssessmentOverviewActivity.ASSESSMENT_PREFS, 0);
                SharedPreferences.Editor editor = passedUri.edit();
                editor.putInt("courseId", courseId);
                Log.d("Load Data", "Passing Course ID: " + courseId);
                editor.commit();

                Log.d("Load Data", "Prior URI: " + uri);
                startActivityForResult(intent, VIEW_NOTE_REQUEST_CODE);
            }
        });
    }
}
