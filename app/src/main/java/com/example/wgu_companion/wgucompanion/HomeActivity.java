package com.example.wgu_companion.wgucompanion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    //Activity Request Codes
    private static final int TERMS_REQUEST_CODE = 1001;
    private static final int COURSES_REQUEST_CODE = 1002;
    private static final int ASSESSMENTS_REQUEST_CODE = 1003;

    //ListView Declaration
    private ListView mainListView;
    private TextView programTv;
    private TextView progressTv;
    private ProgressBar progressB;

    //Program and CU Text Variables
    private String programText;
    private int completedCUs = 0;
    private int totalCUs = 0;
    private String progressText = "";

    //Passed Variables
    private int programId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    //Setup ListView for home screen buttons (go to Terms, Courses, Assessments)
        HomeItem home_item_data[] = new HomeItem[]
                {
                        new HomeItem(R.drawable.ic_terms_logo, getString(R.string.main_terms_text)),
                        new HomeItem(R.drawable.ic_courses_icon, getString(R.string.main_courses_text)),
                        new HomeItem(R.drawable.ic_assessment_icon, getString(R.string.main_assessments_text))
                };

        HomeAdapter adapter = new HomeAdapter(this, R.layout.lsit_item_main, home_item_data);

        mainListView = (ListView)findViewById(R.id.main_list_view);
        mainListView.setAdapter(adapter);

    //Verifying DataBase Creation
        verifyDataInsert();

    //Click Events
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int item = (int) id;
                Intent listIntent;

                switch (item) {
                    case 0:
                        listIntent = new Intent(HomeActivity.this, TermsOverviewActivity.class);
                        startActivityForResult(listIntent, TERMS_REQUEST_CODE);
                        break;
                    case 1:
                        listIntent = new Intent(HomeActivity.this, CoursesOverviewActivity.class);
                        startActivityForResult(listIntent, COURSES_REQUEST_CODE);
                        break;
                    case 2:
                        listIntent = new Intent(HomeActivity.this, AssessmentOverviewActivity.class);
                        startActivityForResult(listIntent, ASSESSMENTS_REQUEST_CODE);
                        break;
                }
            }
        });



        //Load ContentViewLoader
        ContentViewLoader contentLoader = new ContentViewLoader();

        //Set Program Name
        programText = contentLoader.loadProgramName(HomeActivity.this);
        programTv = (TextView) findViewById(R.id.program_name);
        programTv.setText(programText);
        programTv.requestFocus();

        //Set Progress/CU's
        completedCUs = contentLoader.loadCompletedCU(HomeActivity.this);
        totalCUs = 6;//contentLoader.loadTotalCU(HomeActivity.this);
        progressText = completedCUs + "/" + totalCUs + " CUs";
        progressTv = (TextView) findViewById(R.id.cu_progress_count);
        progressTv.setText(progressText);
        progressTv.requestFocus();

        //Set ProgressBar
        progressB = (ProgressBar) findViewById(R.id.cu_progress_bar);
        progressB.setMax(totalCUs);
        progressB.setProgress(completedCUs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    private void verifyDataInsert(){
        Cursor c = getContentResolver().query(CompanionContentProvider.STATUS_URI, DBHelper.STATUS_COLUMNS, null, null,
                DBHelper.STATUS_ID + "DESC");
        int count = c.getCount();
        c.close();
        Log.d("Database", "Status Type Row Count: " + count);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_change_program:
                changeProgram();
                break;
            case R.id.action_reset_app:
                resetApp();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void resetApp() {
        DialogInterface.OnClickListener confirmDelete =
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if(button == DialogInterface.BUTTON_POSITIVE){
                            CompanionContentProvider c = new CompanionContentProvider();

                            Toast.makeText(HomeActivity.this,
                                    getString(R.string.app_reset),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), confirmDelete)
                .setNegativeButton(getString(android.R.string.no), confirmDelete)
                .show();
    }

    private void changeProgram() {

    }
}
