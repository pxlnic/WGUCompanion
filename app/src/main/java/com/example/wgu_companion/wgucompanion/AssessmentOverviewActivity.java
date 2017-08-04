package com.example.wgu_companion.wgucompanion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class AssessmentOverviewActivity extends AppCompatActivity {

    private static final int NEW_ASSESSMENT_REQUEST_CODE = 2003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_overview);
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
}
