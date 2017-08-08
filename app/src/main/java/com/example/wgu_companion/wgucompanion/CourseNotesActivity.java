package com.example.wgu_companion.wgucompanion;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CourseNotesActivity extends AppCompatActivity {
    //Note Title/Text Variables
    private String noteTitleText = "";
    private String noteText = "";

    //View Declarations
    EditText noteTitleTv;
    EditText noteTv;

    //Activity Variables
    private String action;
    private String filter;
    private Uri passedUri;
    ContentViewLoader contentLoader = new ContentViewLoader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_notes);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(CompanionContentProvider.NOTE_ITEM_TYPE);
        passedUri = intent.getParcelableExtra(CompanionContentProvider.ASSESSMENT_ITEM_TYPE);
        Log.d("Load Data", "Passed URI: " + passedUri);

        if(uri == null){
            action = Intent.ACTION_INSERT;

            noteTitleTv = (EditText) findViewById(R.id.course_note_title_text);
            noteTv = (EditText) findViewById(R.id.course_note_text);
        }
        else {
            action = Intent.ACTION_EDIT;
            filter = DBHelper.NOTE_ID + " = " + uri.getLastPathSegment();
            setViews(uri);
        }
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
            case android.R.id.home:
                finishedEditing();
                break;
            case R.id.delete_note:
                deleteNote();
            break;
            case R.id.add_photo:
                add_photo();
                break;
            case R.id.share_note:
                share_note();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void share_note() {

    }

    private void add_photo() {

    }

    private void deleteNote() {
        getContentResolver().delete(CompanionContentProvider.NOTE_URI, filter, null);
        Toast.makeText(this, "Note Deleted", Toast.LENGTH_SHORT);
        setResult(RESULT_OK);
        finish();
    }

    private void updateNote(String title, String text){
        ContentValues value = new ContentValues();
        value.put(DBHelper.NOTE_TITLE, title);
        value.put(DBHelper.NOTE_TEXT, text);
        getContentResolver().update(CompanionContentProvider.NOTE_URI, value, filter, null);
        Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT);
        setResult(RESULT_OK);
    }

    private void insertNote(String title, String text){
        ContentValues value = new ContentValues();
        value.put(DBHelper.NOTE_TITLE, title);
        value.put(DBHelper.NOTE_TEXT, text);
        getContentResolver().insert(CompanionContentProvider.NOTE_URI, value);
        Toast.makeText(this, "Note Inserted", Toast.LENGTH_SHORT);
        setResult(RESULT_OK);
    }

    public void setViews(Uri uri) {

        //Set Note Title
        noteTitleText = contentLoader.loadNoteTitle(CourseNotesActivity.this, uri);
        noteTitleTv = (EditText) findViewById(R.id.course_note_title_text);
        noteTitleTv.setText(noteTitleText);
        noteTitleTv.requestFocus();

        //Set Note Text
        noteText = contentLoader.loadNoteText(CourseNotesActivity.this, uri);
        noteTv = (EditText) findViewById(R.id.course_note_text);
        noteTv.setText(noteText);
        noteTv.requestFocus();
    }

    private void finishedEditing(){
        String newTitle = noteTitleTv.getText().toString().trim();
        String newText = noteTv.getText().toString().trim();

        switch(action){
            case Intent.ACTION_INSERT:
                if(newText.length() == 0){
                    setResult(RESULT_CANCELED);
                }
                else{
                    insertNote(newTitle, newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if(newText.length() == 0){
                    deleteNote();
                }
                else if(newText.equals(noteText) && newTitle.equals(noteTitleText)){
                    setResult(RESULT_CANCELED);
                }
                else{
                    updateNote(newTitle, newText);
                }
                break;
        }
    }

    @Override
    public void onBackPressed(){
        finishedEditing();
    }
}
