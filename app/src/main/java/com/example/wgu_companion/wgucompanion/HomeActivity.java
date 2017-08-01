package com.example.wgu_companion.wgucompanion;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;

public class HomeActivity extends AppCompatActivity {

    //Activity Request Codes
    private static final int TERMS_REQUEST_CODE = 1001;
    private static final int COURSES_REQUEST_CODE = 1002;
    private static final int ASSESSMENTS_REQUEST_CODE = 1003;

    //ListView Declaration
    private ListView mainListView;
    private RelativeLayout homeLayout;

    //Program and CU Text Variables
    private String programName;
    private int completedCUs;
    private int totalCUs;
    private String progressText = completedCUs + "/" + totalCUs + " CUs";

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

        HomeAdapter adapter = new HomeAdapter(this, R.layout.main_list_items, home_item_data);

        mainListView = (ListView)findViewById(R.id.main_list_view);
        mainListView.setAdapter(adapter);

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

    //Add Program Name/Progress components
/*        RelativeLayout rl = (RelativeLayout) findViewById(R.id.progress_overview);
        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                              ViewGroup.LayoutParams.MATCH_PARENT);
        rlParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        homeLayout = (RelativeLayout) findViewById(R.id.home_layout);
        homeLayout.addView(rl);*/

    //Set Program Name


    //Set Progress/CU's
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);
        return true;
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
}
