package com.example.wgu_companion.wgucompanion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CoursesDetailActivity extends AppCompatActivity {

    private static final int ADD_NOTE_REQUEST_CODE = 3001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_detail);
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
}
