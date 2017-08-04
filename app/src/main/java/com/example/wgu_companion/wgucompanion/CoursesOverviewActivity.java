package com.example.wgu_companion.wgucompanion;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CoursesOverviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
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
    private CursorAdapter adapter;

    private static final int NEW_COURSE_REQUEST_CODE = 2002;
    private static final int VIEW_COURSE_REQUEST_CODE = 2003;
    private static String action = "";

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
                intent.putExtra(CompanionContentProvider.COURSE_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEW_COURSE_REQUEST_CODE);
            }
        });
    }

    private void setViews() {
        //Load ContentViewLoader
        ContentViewLoader contentLoader = new ContentViewLoader();

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

        //Load Terms
        courseOverviewLv = (ListView) findViewById(R.id.courses_overview_list_view);

        String[] from = {DBHelper.COURSE_NAME, DBHelper.COURSE_START_DATE};
        int[] to = {R.id.course_item_name_text, R.id.course_item_dates_text};
        adapter = new SimpleCursorAdapter(this, R.layout.list_item_course, null, from, to, 0);

        courseOverviewLv.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);
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
        action = "insert";
        Intent menuIntent;
        menuIntent = new Intent(CoursesOverviewActivity.this, CoursesDetailActivity.class);
        startActivityForResult(menuIntent, NEW_COURSE_REQUEST_CODE);
    }

    public static String getCourseAction(){
        return action;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CompanionContentProvider.COURSE_URI,
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
}
