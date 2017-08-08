package com.example.wgu_companion.wgucompanion;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class AssessmentsDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    //Activity Variables
    private String action = "";
    CursorAdapter adapter;
    public static final String ASSESSMENT_PREFS = "Assessment_Prefs";
    Uri uriToPass;
    private static final int ADD_NOTE_REQUEST_CODE = 3002;
    private static final int VIEW_NOTE_REQUEST_CODE = 3003;

    //Term Name/Progress/Dates Variables
    private String assessmentCourseNameText = "";
    private String assessmentTypeText = "";
    private String assessmentExpectedDueDateText = "";

    //View Declarations
    TextView assessmentCourseTv;
    TextView assessmentTypeTv;
    TextView assessmentEndTv;
    ListView assessmentNotesLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessments_detail);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(CompanionContentProvider.ASSESSMENT_ITEM_TYPE);

        if(uri == null){
            SharedPreferences passedUri = getSharedPreferences(ASSESSMENT_PREFS, 0);
            uri = Uri.parse(passedUri.getString("assessmentUri", "No URI Found."));
            Log.d("Load Data", "Passed URI Loaded: " + uri);
            uriToPass = uri;
            setViews(uri);
        }
        else {
            uriToPass = uri;
            setViews(uri);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

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
                editAssessment();
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
        Intent menuIntent;
        menuIntent = new Intent(AssessmentsDetailActivity.this, CourseNotesActivity.class);
        startActivityForResult(menuIntent, ADD_NOTE_REQUEST_CODE);
    }

    private void deleteAssessment() {

    }

    private void editAssessment() {

    }

    private void reloadData(){
        getLoaderManager().restartLoader(0, null, this);
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
        assessmentExpectedDueDateText = "Expected End Date: " + contentLoader.loadAssessmentDueDate(AssessmentsDetailActivity.this, uri);
        assessmentEndTv = (TextView) findViewById(R.id.assessment_due_date_text);
        assessmentEndTv.setText(assessmentExpectedDueDateText);
        assessmentEndTv.requestFocus();

        //Set Assessment Notes ListView
        assessmentNotesLv = (ListView) findViewById(R.id.assessment_detail_note_list);

        String[] from = {DBHelper.NOTE_TITLE};
        int[] to = {android.R.id.text1};
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, 0);

        assessmentNotesLv.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);

        assessmentNotesLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                SharedPreferences passedUri = getSharedPreferences(ASSESSMENT_PREFS, 0);
                SharedPreferences.Editor editor = passedUri.edit();
                editor.putString("assessmentUri", uriToPass.toString());
                editor.commit();

                Intent intent = new Intent(AssessmentsDetailActivity.this, CourseNotesActivity.class);
                Uri uri = Uri.parse(CompanionContentProvider.NOTE_URI + "/" + id);
                intent.putExtra(CompanionContentProvider.NOTE_ITEM_TYPE, uri);
                Log.d("Load Data", "Prior URI: " );
                startActivityForResult(intent, VIEW_NOTE_REQUEST_CODE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CompanionContentProvider.NOTE_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if((requestCode == VIEW_NOTE_REQUEST_CODE && resultCode == RESULT_OK) ||
                (requestCode == ADD_NOTE_REQUEST_CODE && resultCode == RESULT_OK)){
            reloadData();
        }
    }
}
