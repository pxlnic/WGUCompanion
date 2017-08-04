package com.example.wgu_companion.wgucompanion;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.wgu_companion.wgucompanion.CoursesOverviewActivity.getCourseAction;

public class CoursesDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    //Activity Variables
    private String action = "";
    CursorAdapter assessmentAdapter;
    CursorAdapter mentorAdapter;
    private static final int ADD_NOTE_REQUEST_CODE = 3001;
    private static final int VIEW_ASSESSMENT_REQUEST_CODE = 2004;


    //Term Name/Progress/Dates Variables
    private String courseNameText = "";
    private String courseStartText = "";
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

        Uri uri = intent.getParcelableExtra(CompanionContentProvider.TERM_ITEM_TYPE);

        if(uri == null){
            action = Intent.ACTION_INSERT;
            setTitle("New Course");

            //Load dialog to enter information
        }
        else{
            action = Intent.ACTION_EDIT;
            setTitle("Term Details");

            setViews(uri);
        }

        setViews(uri);

        courseAssessmentLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(CoursesDetailActivity.this, AssessmentsDetailActivity.class);
                Uri uri = Uri.parse(CompanionContentProvider.ASSESSMENT_URI + "/" + id);
                intent.putExtra(CompanionContentProvider.ASSESSMENT_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEW_ASSESSMENT_REQUEST_CODE);
            }
        });

/*        courseMentorLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(CoursesDetailActivity.this, AssessmentsDetailActivity.class);
                Uri uri = Uri.parse(CompanionContentProvider.MENTOR_URI + "/" + id);
                intent.putExtra(CompanionContentProvider.MENTOR_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEW_ASSESSMENT_REQUEST_CODE);
            }
        });*/
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
                editCourse();
                break;
            case R.id.delete_course:
                deleteCourse();
                break;
            case R.id.add_note:
                add_note();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void add_note() {
        Intent menuIntent;
        menuIntent = new Intent(CoursesDetailActivity.this, CourseNotesActivity.class);
        startActivityForResult(menuIntent, ADD_NOTE_REQUEST_CODE);
    }

    private void deleteCourse() {

    }

    private void editCourse() {

    }

    public void setViews(Uri uri) {
        ContentViewLoader contentLoader = new ContentViewLoader();

        //Set Course Name
        //courseNameText = contentLoader.loadTermName(CoursesDetailActivity.this, uri);
        courseTv = (TextView) findViewById(R.id.course_detail_name_header);
        courseTv.setText(courseNameText);
        courseTv.requestFocus();

        //Set Course  Start Date
        //courseStartText = "Start Date: " + contentLoader.loadTermStart(CoursesDetailActivity.this, uri);
        courseStartTv = (TextView) findViewById(R.id.course_start_date_Text);
        courseStartTv.setText(courseStartText);
        courseStartTv.requestFocus();

        //Set Course End Date
        //courseEndText = "Expected End Date: " + contentLoader.loadTermEnd(CoursesDetailActivity.this, uri);
        courseEndTv = (TextView) findViewById(R.id.course_end_Date_text);
        courseEndTv.setText(courseEndText);
        courseEndTv.requestFocus();

        //Set Course Status
        //courseStartText = "Expected End Date: " + contentLoader.loadTermEnd(CoursesDetailActivity.this, uri);
        courseStatusTv = (TextView) findViewById(R.id.course_detail_status_text);
        courseStatusTv.setText(courseStatusText);
        courseStatusTv.requestFocus();

        //Set Course Description
        //courseDescriptionText = "Expected End Date: " + contentLoader.loadTermEnd(CoursesDetailActivity.this, uri);
        courseDescriptionTv = (TextView) findViewById(R.id.course_detail_description_text);
        courseDescriptionTv.setText(courseDescriptionText);
        courseDescriptionTv.requestFocus();

        //Set Assessment ListView
        courseAssessmentLv = (ListView) findViewById(R.id.course_assessments_list);

        String[] afrom = {DBHelper.ASSESSMENT_TYPE_ID};
        int[] ato = {android.R.layout.simple_list_item_1};
        assessmentAdapter = new SimpleCursorAdapter(this, android.R.id.list, null, afrom, ato, 0);

        courseAssessmentLv.setAdapter(assessmentAdapter);

        //Set Mentor ListView
        courseMentorLv = (ListView) findViewById(R.id.course_mentor_list);

        String[] mfrom = {DBHelper.MENTOR_NAME};
        int[] mto = {android.R.layout.simple_list_item_1};
        mentorAdapter = new SimpleCursorAdapter(this, android.R.id.list, null, mfrom, mto, 0);

        courseMentorLv.setAdapter(mentorAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CompanionContentProvider.ASSESSMENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        assessmentAdapter.swapCursor(data);
        mentorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        assessmentAdapter.swapCursor(null);
        mentorAdapter.swapCursor(null);
    }
}
