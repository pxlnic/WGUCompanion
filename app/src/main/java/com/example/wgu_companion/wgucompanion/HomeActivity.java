package com.example.wgu_companion.wgucompanion;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity {

    //Activity Request Codes
    private static final int TERMS_REQUEST_CODE = 1001;
    private static final int COURSES_REQUEST_CODE = 1002;
    private static final int ASSESSMENTS_REQUEST_CODE = 1003;

    //ListView Declaration
    private ListView mainListView;
    private RelativeLayout homeLayout;
    private TextView programText;

    //Program and CU Text Variables
    private String programName;
    private int completedCUs;
    private int totalCUs;
    private String progressText = completedCUs + "/" + totalCUs + " CUs";

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

    //Inserting Test Data
        insertTest();
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

        //Set Program Name
        Uri uri = CompanionContentProvider.PROGRAM_URI;
        String filter = DBHelper.PROGRAM_ID + "=" + 1;
        Cursor cursor = getContentResolver().query(uri, DBHelper.PROGRAM_COLUMNS, filter, null, null);
        cursor.moveToFirst();
        programName = cursor.getString(cursor.getColumnIndex(DBHelper.PROGRAM_NAME));
        programText = (TextView) findViewById(R.id.program_name);
        programText.setText(programName);
        programText.requestFocus();

    //Set Progress/CU's
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);
        return true;
    }

    private void insertTest() {
        String text = "Software Development";
        Log.d("Database", "Test started.");
        ContentValues value = new ContentValues();
        Log.d("Database", "Values created");
        value.put(DBHelper.PROGRAM_NAME, text);
        Log.d("Database", "Values Put");
        Uri uri = getContentResolver().insert(CompanionContentProvider.PROGRAM_URI, value);
        Log.d("Database", uri.toString());
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
                //deleteAllNotes();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertData(){
        //Insert Term
        ContentValues value = new ContentValues();
        value.put(DBHelper.TERM_NAME, "Transfer");
        value.put(DBHelper.TERM_START_DATE, "2017/07/01");
        value.put(DBHelper.TERM_END_DATE, "2017/12/31");
        value.put(DBHelper.TERM_START_REMINDER, 1);
        value.put(DBHelper.TERM_END_REMINDER, 1);
        value.put(DBHelper.TERM_PROGRAM_ID, 1);
        getContentResolver().insert(CompanionContentProvider.TERM_URI, value);

        //Insert Course


        //Insert Assessment


        //Insert Mentor


    }
}
