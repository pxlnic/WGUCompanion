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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;

public class TermDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int VIEW_COURSE_REQUEST_CODE = 2003;
    //Activity Variables
    private String action = "";
    CursorAdapter adapter;

    //Term Name/Progress/Dates Variables
    private String termNameText = "";
    private String termStartText = "";
    private String termEndText = "";
    private int termCompletedCU = 0;
    private int termTotalCU = 0;
    private String termProgressText = "";

    //View Declarations
    TextView termTv;
    TextView termStartTv;
    TextView termEndTv;
    TextView termProgressTv;
    ProgressBar termProgressB;
    ListView termCourseLv;

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
            action = Intent.ACTION_EDIT;
            setTitle("Term Details");

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

    }

    private void editTerm() {

    }

    private void setViews(Uri uri){
        ContentViewLoader contentLoader = new ContentViewLoader();

        //Set Term Name
        termNameText = contentLoader.loadTermName(TermDetailActivity.this, uri);
        termTv = (TextView) findViewById(R.id.term_name);
        termTv.setText(termNameText);
        termTv.requestFocus();

        //Set Term  Start Date
        termStartText = "Start Date: " + contentLoader.loadTermStart(TermDetailActivity.this, uri);
        termStartTv = (TextView) findViewById(R.id.term_detail_start_text);
        termStartTv.setText(termStartText);
        termStartTv.requestFocus();

        //Set Term End Date
        termEndText = "Expected End Date: " + contentLoader.loadTermEnd(TermDetailActivity.this, uri);
        termEndTv = (TextView) findViewById(R.id.term_detail_end_text);
        termEndTv.setText(termEndText);
        termEndTv.requestFocus();

        //Set Term Progress Text
        termCompletedCU = contentLoader.loadTermCompletedCU(TermDetailActivity.this, uri);
        termTotalCU = contentLoader.loadTermTotalCU(TermDetailActivity.this, uri);
        Log.d("Load Data", "Term Completed CUs Loaded: " + termCompletedCU);
        Log.d("Load Data", "Term Total CUs Loaded: " + termTotalCU);
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

        String[] from = {DBHelper.COURSE_NAME, DBHelper.COURSE_START_DATE};
        int[] to = {R.id.course_item_name_text, R.id.course_item_dates_text};
        adapter = new SimpleCursorAdapter(this, R.layout.list_item_course, null, from, to, 0);

        termCourseLv.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);
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
