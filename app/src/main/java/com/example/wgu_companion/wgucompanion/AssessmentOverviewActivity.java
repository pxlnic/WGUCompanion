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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AssessmentOverviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

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
    private CursorAdapter adapter;

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
                intent.putExtra(CompanionContentProvider.ASSESSMENT_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEW_ASSESSMENT_REQUEST_CODE);
            }
        });
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
        Intent menuIntent;
        menuIntent = new Intent(AssessmentOverviewActivity.this, AssessmentsDetailActivity.class);
        startActivityForResult(menuIntent, NEW_ASSESSMENT_REQUEST_CODE);
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

        //Load Terms
        assessmentOverviewLv = (ListView) findViewById(R.id.assessments_overview_list_view);

        String[] from = {DBHelper.ASSESSMENT_COURSE_ID, DBHelper.ASSESSMENT_A_TYPE_ID, DBHelper.ASSESSMENT_STATUS_ID};
        int[] to = {R.id.assessment_item_name_text, R.id.assessment_item_type_text, R.id.assessment_item_status_text};
        adapter = new SimpleCursorAdapter(this, R.layout.list_item_assessment, null, from, to, 0);

        assessmentOverviewLv.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CompanionContentProvider.ASSESSMENT_URI,
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
