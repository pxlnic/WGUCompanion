package com.example.wgu_companion.wgucompanion;

import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TermsOverviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    //ListView Declaration
    private ListView termOverviewLv;
    private TextView programTv;
    private TextView progressTv;
    private ProgressBar progressB;

    //Program and CU Text Variables
    private String programText;
    private int completedCUs = 0;
    private int totalCUs = 0;
    private String progressText = "";

    //Activity Variables
    private int termID;
    private String action = "";
    private CursorAdapter adapter;

    private static final int NEW_TERM_REQUEST_CODE = 2001;
    private static final int VIEW_TERM_REQUEST_CODE = 2002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_overview);

        setViews();

        termOverviewLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(TermsOverviewActivity.this, TermDetailActivity.class);
                Uri uri = Uri.parse(CompanionContentProvider.TERM_URI + "/" + id);
                intent.putExtra(CompanionContentProvider.TERM_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEW_TERM_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_terms_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch(id){
            case R.id.add_term:
                addTerm();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addTerm() {
        //New Term Variables to pass
        String termNameText = "";
        String termStatusText = "";
        String termStartText = "";
        Boolean termStartReminder = false;
        String termEndText = "";
        Boolean termEndReminder = false;
        List<String> statusArray = new ArrayList<>();
        statusArray.add("Note Attempted");
        statusArray.add("In Progress");
        statusArray.add("Completed");


        final Dialog addTermDialog = new Dialog(TermsOverviewActivity.this);
        addTermDialog.setContentView(R.layout.edit_term_data);

    //Set Custom Dialog Components
        //Term Name
        EditText termNameEt = (EditText) findViewById(R.id.term_edit_name_field);

        //Term Status
        Spinner termStatusSpin = (Spinner) addTermDialog.findViewById(R.id.term_edit_status_spinner);
        ArrayAdapter<String> termStatusAdapter = new ArrayAdapter<>(addTermDialog.getContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.term_status_array));
        termStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        termStatusSpin.setAdapter(termStatusAdapter);

        //Term Start
        EditText termStartEt = (EditText) findViewById(R.id.term_edit_start_field);
        CheckBox termStartChk = (CheckBox) findViewById(R.id.term_edit_start_checkbox);

        //Term End
        EditText termEndtEt = (EditText) findViewById(R.id.term_edit_end_field);
        CheckBox termEndChk = (CheckBox) findViewById(R.id.term_edit_end_checkbox);

        //Term Course List
        ListView termCourseLv = (ListView) findViewById(R.id.term_edit_course_list);

        //Buttons
        Button termSubmitBtn = (Button) findViewById(R.id.term_edit_submit_button);

        Button termCancelBtn = (Button) findViewById(R.id.term_edit_cancel_button);



        addTermDialog.show();
    }

    private void setViews(){
        //Load ContentViewLoader
        ContentViewLoader contentLoader = new ContentViewLoader();

        //Set Program Name
        programText = contentLoader.loadProgramName(TermsOverviewActivity.this);
        programTv = (TextView) findViewById(R.id.program_name);
        programTv.setText(programText);
        programTv.requestFocus();

        //Set Progress/CU's
        completedCUs = contentLoader.loadCompletedCU(TermsOverviewActivity.this);
        totalCUs = contentLoader.loadTotalCU(TermsOverviewActivity.this);
        progressText = completedCUs + "/" + totalCUs + " CUs";
        progressTv = (TextView) findViewById(R.id.cu_progress_count);
        progressTv.setText(progressText);
        progressTv.requestFocus();

        //Set ProgressBar
        progressB = (ProgressBar) findViewById(R.id.cu_progress_bar);
        progressB.setMax(totalCUs);
        progressB.setProgress(completedCUs);

        //Load Terms
        termOverviewLv = (ListView) findViewById(R.id.terms_overview_list_view);

        String[] from = {DBHelper.TERM_NAME, DBHelper.TERM_START_DATE};
        int[] to = {R.id.term_item_name_text, R.id.term_item_dates_text};
        adapter = new SimpleCursorAdapter(this, R.layout.list_item_term, null, from, to, 0);

        termOverviewLv.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CompanionContentProvider.TERM_URI,
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
