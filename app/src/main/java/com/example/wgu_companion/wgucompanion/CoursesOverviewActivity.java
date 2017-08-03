package com.example.wgu_companion.wgucompanion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class CoursesOverviewActivity extends AppCompatActivity {

    private static final int NEW_COURSE_REQUEST_CODE = 2002;
    private static String action = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_overview);
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
        return true;
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
}
