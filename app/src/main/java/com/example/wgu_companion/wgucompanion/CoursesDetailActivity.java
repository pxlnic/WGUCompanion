package com.example.wgu_companion.wgucompanion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.wgu_companion.wgucompanion.CoursesOverviewActivity.getCourseAction;

public class CoursesDetailActivity extends AppCompatActivity {

    private static final int ADD_NOTE_REQUEST_CODE = 3001;

    //Content Views
    private TextView courseName;

    //Strings for SQLite Updates
    private String courseNameText;

    //Activity Variables
    private String courseAction = getCourseAction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_detail);

        //Course Name Header Text Editor
        courseName = (TextView) findViewById(R.id.course_detail_name_header);

        courseName.setOnClickListener(new View.OnClickListener(){
           public void onClick(View v){
               String passedText = courseName.getText().toString();
               EditContent editText = new EditContent();
               courseNameText = editText.editText(CoursesDetailActivity.this, "Course # - Course Name", passedText);

               switch(passedText.length()){
                   case 0:
                       Toast.makeText(CoursesDetailActivity.this,"New Course added", Toast.LENGTH_SHORT).show();
                       break;

               }
           }
        });
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
