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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssessmentOverviewActivity extends AppCompatActivity{

    //ListView Declaration
    private ListView assessmentOverviewLv;
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
    private AssessmentViewCursorAdapter adapter;
    public static final String ASSESSMENT_PREFS = "Assessment_Prefs";
    ContentViewLoader loader = new ContentViewLoader();

    private static final int VIEW_ASSESSMENT_REQUEST_CODE = 2006;
    private static final int NEW_ASSESSMENT_REQUEST_CODE = 2007;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_overview);

        setViews();

        assessmentOverviewLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(AssessmentOverviewActivity.this, AssessmentsDetailActivity.class);
                Uri uri = Uri.parse(CompanionContentProvider.ASSESSMENT_URI + "/" + id);

                SharedPreferences passedUri = getSharedPreferences(ASSESSMENT_PREFS, 0);
                SharedPreferences.Editor editor = passedUri.edit();
                editor.putLong("assessmentUri", id);
                editor.commit();

                intent.putExtra(CompanionContentProvider.ASSESSMENT_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEW_ASSESSMENT_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_assessments_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.add_assessment:
                addAssessment();
                break;
            case R.id.view_completed_assessments:
                viewCompleted();
                break;
            case R.id.view_remaining_assessments:
                viewRemaining();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void viewRemaining() {
        Toast.makeText(AssessmentOverviewActivity.this, "Filtered to Remaining Assessments", Toast.LENGTH_SHORT).show();
    }

    private void viewCompleted() {
        Toast.makeText(AssessmentOverviewActivity.this, "Filtered to Completed Assessments", Toast.LENGTH_SHORT).show();
    }

    private void addAssessment() {
        List<String> courseArray = new ArrayList<>();
        List<String> statusArray = new ArrayList<>();
        List<String> typeArray = new ArrayList<>();

        final Dialog addAssessmentDialog = new Dialog(AssessmentOverviewActivity.this);
        addAssessmentDialog.setContentView(R.layout.edit_assessment_data);

        //Set Header
        TextView header = (TextView) addAssessmentDialog.findViewById(R.id.assessment_edit_header);
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
        final Spinner assessmentCourseSpin = (Spinner) addAssessmentDialog.findViewById(R.id.assessment_edit_course_spinner);
        ArrayAdapter<String> assessmentCourseAdapter = new ArrayAdapter<>(addAssessmentDialog.getContext(),
                android.R.layout.simple_spinner_item,
                courseArray);
        assessmentCourseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assessmentCourseSpin.setAdapter(assessmentCourseAdapter);

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

        final Spinner editAssessmentTypeSpin = (Spinner) addAssessmentDialog.findViewById(R.id.assessment_edit_type_spinner);
        ArrayAdapter<String> assessmentTypeAdapter = new ArrayAdapter<>(addAssessmentDialog.getContext(),
                android.R.layout.simple_spinner_item,
                typeArray);
        assessmentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editAssessmentTypeSpin.setAdapter(assessmentTypeAdapter);

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

        final Spinner editAssessmentStatusSpin = (Spinner)  addAssessmentDialog.findViewById(R.id.assessment_edit_status_spinner);
        ArrayAdapter<String> assessmentStatusAdapter = new ArrayAdapter<>(addAssessmentDialog.getContext(),
                android.R.layout.simple_spinner_item,
                statusArray);
        assessmentStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editAssessmentStatusSpin.setAdapter(assessmentStatusAdapter);

        //Set Goal Date
        //final EditText assessmentGoalDateEv = (EditText)  addAssessmentDialog.findViewById(R.id.assessment_edit_date_text);
        final DatePicker assessmentGoalDatePk = (DatePicker) addAssessmentDialog.findViewById(R.id.assessment_edit_date_picker);
        final CheckBox assessmentGoalReminderChk = (CheckBox) addAssessmentDialog.findViewById(R.id.assessment_edit_reminder_checkbox);

        //Button Handlers
        //Submit Button
        Button editAssessmentSubmitBtn = (Button) addAssessmentDialog.findViewById(R.id.assessment_edit_submit_button);
        editAssessmentSubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String updateAssessmentName = assessmentCourseSpin.getSelectedItem().toString().trim();
                String updateAssessmentType = editAssessmentTypeSpin.getSelectedItem().toString().trim();
                String updateAssessmentStatus = editAssessmentStatusSpin.getSelectedItem().toString().trim();

                int pickDay  = assessmentGoalDatePk.getDayOfMonth();
                int pickMonth= assessmentGoalDatePk.getMonth() + 1;
                int pickYear = assessmentGoalDatePk.getYear();

                String updateAssessmentDate = loader.convertDate(pickDay, pickMonth, pickYear);
                int updateAssessmentReminder = 0;
                if(assessmentGoalReminderChk.isChecked()){
                    updateAssessmentReminder = 1;
                }

                try {
                    //Get current date and reminder date
                    boolean isOk = false;
                    Date now = new Date();
                    Date verifyGoal = new SimpleDateFormat("yyyy/MM/dd").parse(updateAssessmentDate);

                    //If reminder checked verify that reminder date is after today
                    if(updateAssessmentReminder==1) {
                        if (verifyGoal.before(now)) {
                            Toast.makeText(AssessmentOverviewActivity.this, "Date must be after today to have a reminder set.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            isOk=true;
                        }
                    }
                    if(isOk){
                        String courseFilter = DBHelper.COURSE_NAME + "= '" + updateAssessmentName + "'";
                        Cursor courseIdCursor = getContentResolver().query(CompanionContentProvider.COURSE_URI, DBHelper.COURSE_COLUMNS, courseFilter,
                                null, null);
                        courseIdCursor.moveToFirst();
                        int courseId = courseIdCursor.getInt(courseIdCursor.getColumnIndex(DBHelper.COURSE_ID));

                        insertAssessment(courseId, updateAssessmentType, updateAssessmentStatus, updateAssessmentDate, updateAssessmentReminder);

                        addAssessmentDialog.cancel();
                        setViews();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        //Delete Button
        Button editAssessmentDeleteBtn = (Button) addAssessmentDialog.findViewById(R.id.assessment_edit_delete_button);
        editAssessmentDeleteBtn.setEnabled(false);

        //Cancel Button
        Button editAssessmentCancelBtn = (Button) addAssessmentDialog.findViewById(R.id.assessment_edit_cancel_button);
        editAssessmentCancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                addAssessmentDialog.cancel();
            }
        });

        addAssessmentDialog.show();
    }

    private void insertAssessment(int id, String type, String status, String date, int reminder) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.ASSESSMENT_COURSE_ID, id);
        values.put(DBHelper.ASSESSMENT_TYPE, type);
        values.put(DBHelper.ASSESSMENT_STATUS, status);
        values.put(DBHelper.ASSESSMENT_DUE_DATE, date);
        values.put(DBHelper.ASSESSMENT_DUE_DATE_REMINDER, reminder);
        getContentResolver().insert(CompanionContentProvider.ASSESSMENT_URI, values);
    }

    private void setViews() {
        //Load ContentViewLoader
        ContentViewLoader contentLoader = new ContentViewLoader();

        //Set Program Name
        programText = contentLoader.loadProgramName(AssessmentOverviewActivity.this);
        programTv = (TextView) findViewById(R.id.program_name);
        programTv.setText(programText);
        programTv.requestFocus();

        //Set Progress/CU's
        completedCUs = contentLoader.loadCompletedCU(AssessmentOverviewActivity.this);
        totalCUs = contentLoader.loadTotalCU(AssessmentOverviewActivity.this);
        progressText = completedCUs + "/" + totalCUs + " CUs";
        progressTv = (TextView) findViewById(R.id.cu_progress_count);
        progressTv.setText(progressText);
        progressTv.requestFocus();

        //Set ProgressBar
        progressB = (ProgressBar) findViewById(R.id.cu_progress_bar);
        progressB.setMax(totalCUs);
        progressB.setProgress(completedCUs);

        //Load Assessments
        assessmentOverviewLv = (ListView) findViewById(R.id.assessments_overview_list_view);

        Cursor assessmentCursor = getContentResolver().query(CompanionContentProvider.ASSESSMENT_URI, DBHelper.ASSESSMENT_COLUMNS,
                null, null, null);
        assessmentCursor.moveToFirst();
        Log.d("Load Data", "Assessment Count: " + assessmentCursor.getCount());
        adapter = new AssessmentViewCursorAdapter(AssessmentOverviewActivity.this, assessmentCursor, 0);

        assessmentOverviewLv.setAdapter(adapter);
    }
}
