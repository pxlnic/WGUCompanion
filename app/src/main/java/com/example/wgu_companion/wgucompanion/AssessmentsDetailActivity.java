package com.example.wgu_companion.wgucompanion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class AssessmentsDetailActivity extends AppCompatActivity {

    private static final int ADD_NOTE_REQUEST_CODE = 3002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessments_detail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_course_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.edit_assessment:
                editAssessment();
                break;
            case R.id.delete_assessment:
                deleteAssessment();
                break;
            case R.id.add_note:
                addNote();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNote() {
        Intent menuIntent;
        menuIntent = new Intent(AssessmentsDetailActivity.this, CourseNotesActivity.class);
        startActivityForResult(menuIntent, ADD_NOTE_REQUEST_CODE);
    }

    private void deleteAssessment() {

    }

    private void editAssessment() {

    }
}
