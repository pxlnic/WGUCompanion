package com.example.wgu_companion.wgucompanion;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TermDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int VIEW_COURSE_REQUEST_CODE = 2003;
    private static final String TERM_PREFS = "Term_Prefs";
    //Activity Variables
    private String action = "";
    CourseViewCursorAdapter adapter;
    TermCourseListAdapter dialogAdapter;
    Uri tempUri = null;
    CompanionContentProvider provider = new CompanionContentProvider();
    ContentViewLoader contentLoader = new ContentViewLoader();

    //Term Name/Progress/Dates Variables
    private String termNameText = "";
    private String termStartText = "";
    private String termEndText = "";
    private String termStatusText = "";
    private int termCompletedCU = 0;
    private int termTotalCU = 0;
    private String termProgressText = "";

    //View Declarations
    TextView termTv;
    TextView termStartTv;
    TextView termEndTv;
    TextView termStatusTv;
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
            Log.d("Load Data", "Position: " + spinnerPosition);
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
        ListView termCourseLv = (ListView) addTermDialog.findViewById(R.id.term_edit_course_list);

        Cursor termCourseCursor = getContentResolver().query(CompanionContentProvider.COURSE_URI, DBHelper.COURSE_COLUMNS,
                null, null, null);
        termCourseCursor.moveToFirst();

        dialogAdapter = new TermCourseListAdapter(TermDetailActivity.this, termCourseCursor);
        termCourseLv.setAdapter(dialogAdapter);

        //Buttons
        Button termSubmitBtn = (Button) findViewById(R.id.term_edit_submit_button);
        termSubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String submitTermName = termNameEt.getText().toString().trim();
                String submitTermStatus = termStatusSpin.getSelectedItem().toString().trim();
                String submitTermStart = termStartEt.getText().toString().trim();
                int submitTermStartReminder = 0;
                if(termStartChk.isChecked()){
                    submitTermStartReminder = 1;
                }
                String submitTermEnd = termEndEt.getText().toString().trim();
                int submitTermEndReminder = 0;
                if(termEndChk.isChecked()){
                    submitTermEndReminder = 1;
                }

                //Get Course ID's
                int[] courseIds = contentLoader.loadCourseIds(TermDetailActivity.this, dialogAdapter);

                updateTerm();
            }
        });

        Button termCancelBtn = (Button) findViewById(R.id.term_edit_cancel_button);
        termCancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                addTermDialog.cancel();
            }
        });

        addTermDialog.show();
    }

    private void updateTerm() {

    }

    private void setViews(Uri uri){
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

        SharedPreferences pref = getSharedPreferences(TERM_PREFS, 0);
        Long id = pref.getLong("termUri", -1);
        String filter = DBHelper.TERM_ID + " = " + id;

        Cursor termCourseCursor = getContentResolver().query(CompanionContentProvider.COURSE_URI, DBHelper.COURSE_COLUMNS, filter, null, null);
        termCourseCursor.moveToFirst();
        String[] array = termCourseCursor.getColumnNames();
        Log.d("Load Data", "# of Columns: " + array.length);
        for(int i = 0; i < array.length; i++){
            Log.d("Load Data", "Column Name: " + array[i]);
            Log.d("Load Data", "Row Data:" + termCourseCursor.getString(i));
        }
        adapter = new CourseViewCursorAdapter(TermDetailActivity.this, termCourseCursor);

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
