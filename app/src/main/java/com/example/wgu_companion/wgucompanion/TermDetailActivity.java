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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TermDetailActivity extends AppCompatActivity{

    private static final int VIEW_COURSE_REQUEST_CODE = 2003;
    //Activity Variables
    private String action = "";
    private CourseViewCursorAdapter adapter;
    private TermCourseCursorAdapter dialogAdapter;
    private Uri tempUri = null;
    private CompanionContentProvider provider = new CompanionContentProvider();
    private ContentViewLoader contentLoader = new ContentViewLoader();
    private List<String> courseIdArray = new ArrayList<>();

    //Term Name/Progress/Dates Variables
    private String termNameText = "";
    private String termStartText = "";
    private String termEndText = "";
    private String termStatusText = "";
    private int termCompletedCU = 0;
    private int termTotalCU = 0;
    private String termProgressText = "";

    //View Declarations
    private TextView termTv;
    private TextView termStartTv;
    private TextView termEndTv;
    private TextView termStatusTv;
    private TextView termProgressTv;
    private ProgressBar termProgressB;
    private ListView termCourseLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(CompanionContentProvider.TERM_ITEM_TYPE);

        if(uri == null){
            action = Intent.ACTION_INSERT;
            setTitle("New Term");

            //Load dialog to enter information
        }
        else{
            Log.d("Load Data", "URI: " + uri);
            action = Intent.ACTION_EDIT;
            setTitle("Term Details");
            tempUri = uri;

            setViews(uri);
        }

        termCourseLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(TermDetailActivity.this, CoursesDetailActivity.class);
                Uri uri = Uri.parse(CompanionContentProvider.COURSE_URI + "/" + id);
                intent.putExtra(CompanionContentProvider.COURSE_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEW_COURSE_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_term_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.edit_term:
                editTerm();
                break;
            case R.id.delete_term:
                deleteTerm();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteTerm() {
            int id = Integer.parseInt(tempUri.getLastPathSegment());
            String filter = DBHelper.TERM_ID + " = " + id;
            getContentResolver().delete(CompanionContentProvider.TERM_URI, filter, null);
    }

    private void editTerm() {
        //New Term Variables to pass
        String dialogTermNameText = termNameText;
        String dialogTermStatusText = termStatusText;
        String dialogTermStartText;
        final Boolean termStartReminder = false;
        String dialogTermEndText;
        Boolean termEndReminder = false;
        List<String> statusArray = new ArrayList<>();
        statusArray.add("Note Attempted");
        statusArray.add("In Progress");
        statusArray.add("Complete");

        final Dialog addTermDialog = new Dialog(TermDetailActivity.this);
        addTermDialog.setContentView(R.layout.edit_term_data);

        //Set Custom Dialog Components
        //Term Name
        final EditText termNameEt = (EditText) addTermDialog.findViewById(R.id.term_edit_name_field);
        termNameEt.setText(dialogTermNameText);

        //Term Status
        final Spinner termStatusSpin = (Spinner) addTermDialog.findViewById(R.id.term_edit_status_spinner);
        ArrayAdapter<String> termStatusAdapter = new ArrayAdapter<>(addTermDialog.getContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.term_status_array));
        termStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        termStatusSpin.setAdapter(termStatusAdapter);

        if (!dialogTermStatusText.equals(null)) {
            int spinnerPosition = statusArray.indexOf(dialogTermStatusText);
            Log.d("Load Data", "Spinner Position: " + spinnerPosition);
            termStatusSpin.setSelection(spinnerPosition);
        }

        //Term Start
        dialogTermStartText = contentLoader.loadTermStart(TermDetailActivity.this, tempUri);
        final EditText termStartEt = (EditText) addTermDialog.findViewById(R.id.term_edit_start_field);
        termStartEt.setText(dialogTermStartText);

        boolean startChecked = contentLoader.loadTermStartReminder(TermDetailActivity.this, tempUri);
        final CheckBox termStartChk = (CheckBox) addTermDialog.findViewById(R.id.term_edit_start_checkbox);
        termStartChk.setChecked(startChecked);

        //Term End
        dialogTermEndText = contentLoader.loadTermEnd(TermDetailActivity.this, tempUri);
        final EditText termEndEt = (EditText) addTermDialog.findViewById(R.id.term_edit_end_field);
        termEndEt.setText(dialogTermEndText);

        boolean endChecked = contentLoader.loadTermEndReminder(TermDetailActivity.this, tempUri);
        final CheckBox termEndChk = (CheckBox) addTermDialog.findViewById(R.id.term_edit_end_checkbox);
        termEndChk.setChecked(endChecked);

        //Term Course List
        final ListView dialogTermCourseLv = (ListView) addTermDialog.findViewById(R.id.term_edit_course_list);

        final Cursor dialogTermCourseCursor = getContentResolver().query(CompanionContentProvider.COURSE_URI, DBHelper.COURSE_COLUMNS,
                null, null, null);
        dialogTermCourseCursor.moveToFirst();

        dialogAdapter = new TermCourseCursorAdapter(TermDetailActivity.this, dialogTermCourseCursor);
        dialogTermCourseLv.setAdapter(dialogAdapter);

        //Buttons
        //Submit Button Handler
        Button termSubmitBtn = (Button) addTermDialog.findViewById(R.id.term_edit_submit_button);
        termSubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Count of courses checked
                int count = 0;
                for(int i=0; i<dialogTermCourseLv.getChildCount(); i++){
                    CheckBox verify;
                    verify = (CheckBox) dialogTermCourseLv.getChildAt(i).findViewById(R.id.list_item_checkbox);
                    if (verify.isChecked()) {
                        count = count+1;
                        Log.d("Submit Data", "Count: " + count);
                    }
                }

                //Verify at least one course checked
                if(count>0) {
                    int termIdToPass = Integer.parseInt(tempUri.getLastPathSegment());
                    String submitTermName = termNameEt.getText().toString().trim();
                    String submitTermStatus = termStatusSpin.getSelectedItem().toString().trim();
                    String submitTermStart = termStartEt.getText().toString().trim();
                    int submitTermStartReminder = 0;
                    if (termStartChk.isChecked()) {
                        submitTermStartReminder = 1;
                    }
                    String submitTermEnd = termEndEt.getText().toString().trim();
                    int submitTermEndReminder = 0;
                    if (termEndChk.isChecked()) {
                        submitTermEndReminder = 1;
                    }

                    //Get Course ID's
                    List<String> courseIds = contentLoader.loadCourseIds(dialogTermCourseCursor);

                    //Determine if course is checked or unchecked
                    List<String> courseCheckedValue = new ArrayList<>();
                    CheckBox cb;
                    for (int i = 0; i < dialogTermCourseLv.getChildCount(); i++) {
                        cb = (CheckBox) dialogTermCourseLv.getChildAt(i).findViewById(R.id.list_item_checkbox);
                        if (cb.isChecked()) {
                            courseCheckedValue.add("1");
                        } else {
                            courseCheckedValue.add("0");
                        }
                    }

                    Log.d("Load Data", "Course ID Count: " + courseIds.size());

                    updateTerm(termIdToPass, submitTermName, submitTermStatus, submitTermStart, submitTermStartReminder, submitTermEnd, submitTermEndReminder);
                    updateCourseTermIds(termIdToPass, courseIds, courseCheckedValue);

                    addTermDialog.cancel();
                    setViews(tempUri);
                }
                //Error if none checked
                else{
                    Toast.makeText(TermDetailActivity.this, "At least one course must be selected to submit", Toast.LENGTH_SHORT).show();
                    Log.d("Submit Data", "At least one course must be selected to submit");
                }
            }
        });

        //Delete Button Handler
        Button termDeleteBtn = (Button) addTermDialog.findViewById(R.id.term_edit_delete_button);
        termDeleteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Count of courses checked
                int count = 0;
                for(int i=0; i<dialogTermCourseLv.getChildCount(); i++){
                    CheckBox verify;
                    verify = (CheckBox) dialogTermCourseLv.getChildAt(i).findViewById(R.id.list_item_checkbox);
                    if (verify.isChecked()) {
                        count = count+1;
                        Log.d("Submit Data", "Count: " + count);
                    }
                }

                if(count == 0) {
                    deleteTerm();
                    addTermDialog.cancel();
                    Intent intent = new Intent(TermDetailActivity.this, TermsOverviewActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(TermDetailActivity.this, "Cannot delete while courses selected.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Cancel Button Handler
        Button termCancelBtn = (Button) addTermDialog.findViewById(R.id.term_edit_cancel_button);
        termCancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                addTermDialog.cancel();
            }
        });

        addTermDialog.show();
    }

    //Update Term
    private void updateTerm(int id, String name, String status, String start, int startReminder, String end, int endReminder) {
        ContentValues values = new ContentValues();
        String filter = DBHelper.TERM_ID + "=" + id;
        values.put(DBHelper.TERM_NAME, name);
        values.put(DBHelper.TERM_STATUS, status);
        values.put(DBHelper.TERM_START_DATE, start);
        values.put(DBHelper.TERM_START_REMINDER, startReminder);
        values.put(DBHelper.TERM_END_DATE, end);
        values.put(DBHelper.TERM_END_REMINDER, endReminder);
        getContentResolver().update(CompanionContentProvider.TERM_URI, values, filter, null);
        Log.d("Load Data", "Update Complete for Term");
    }

    //Update Courses Term ID
    private void updateCourseTermIds(int id, List<String> courseIds, List<String> courseChecked){
        for(int i=0; i<courseIds.size(); i++) {
            ContentValues courseValues = new ContentValues();
            if(courseChecked.get(i).equals("1")) {
                courseValues.put(DBHelper.COURSE_TERM_ID, id);
            }
            else if(Integer.parseInt(courseIds.get(i))==id){
                courseValues.put(DBHelper.COURSE_TERM_ID, -99);
            }
            String filter = DBHelper.COURSE_ID + "=" + courseIds.get(i);
            getContentResolver().update(CompanionContentProvider.COURSE_URI, courseValues, filter, null);
            Log.d("Load Data", "Update Complete for Course ID: " + courseIds.get(i));
        }
    }

    //Set View Content
    private void setViews(Uri uri){
        //Set Term Name
        termNameText = contentLoader.loadTermName(TermDetailActivity.this, uri);
        termTv = (TextView) findViewById(R.id.term_detail_name);
        termTv.setText(termNameText);
        termTv.requestFocus();

        //Set Term  Start Date
        termStartText = "Start Date: " + contentLoader.loadTermStart(TermDetailActivity.this, uri);
        termStartTv = (TextView) findViewById(R.id.term_detail_start_text);
        termStartTv.setText(termStartText);
        termStartTv.requestFocus();

        //Set Term Status
        termStatusText = contentLoader.loadTermStatus(TermDetailActivity.this, uri);
        termStatusTv = (TextView) findViewById(R.id.term_detail_status_text);
        termStatusTv.setText(termStatusText);
        termStatusTv.requestFocus();

        //Set Term End Date
        termEndText = "Expected End Date: " + contentLoader.loadTermEnd(TermDetailActivity.this, uri);
        termEndTv = (TextView) findViewById(R.id.term_detail_end_text);
        termEndTv.setText(termEndText);
        termEndTv.requestFocus();

        //Set Term Progress Text
        termCompletedCU = contentLoader.loadTermCompletedCU(TermDetailActivity.this, uri);
        termTotalCU = contentLoader.loadTermTotalCU(TermDetailActivity.this, uri);
        termProgressText = termCompletedCU + "/" + termTotalCU + " CUs";
        termProgressTv = (TextView) findViewById(R.id.term_cu_progress_count);
        termProgressTv.setText(termProgressText);
        termProgressTv.requestFocus();

        //Set Term Progress Bar
        termProgressB = (ProgressBar) findViewById(R.id.term_cu_progress_bar);
        termProgressB.setMax(termTotalCU);
        termProgressB.setProgress(termCompletedCU);

        //Set ListView
        termCourseLv = (ListView) findViewById(R.id.term_detail_list_view);

        SharedPreferences pref = getSharedPreferences(TermsOverviewActivity.TERM_PREFS, 0);
        Long id = pref.getLong("termUri", 0);
        String termIdFilter = DBHelper.COURSE_TERM_ID + " = " + id;

        Cursor termCourseCursor = getContentResolver().query(CompanionContentProvider.COURSE_URI, DBHelper.COURSE_COLUMNS, termIdFilter, null, null);
        termCourseCursor.moveToFirst();
        adapter = new CourseViewCursorAdapter(TermDetailActivity.this, termCourseCursor,0);

        termCourseLv.setAdapter(adapter);
    }
}
