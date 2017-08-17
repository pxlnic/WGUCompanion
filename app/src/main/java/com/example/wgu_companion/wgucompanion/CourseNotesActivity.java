package com.example.wgu_companion.wgucompanion;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CourseNotesActivity extends AppCompatActivity {
    //Note Title/Text Variables
    private String noteTitleText = "";
    private String noteText = "";
    private String noteImagePath = "";
    private Uri uriImage = Uri.parse("Blank");
    File file;
    private boolean newPhoto = false;

    //View Declarations
    EditText noteTitleTv;
    EditText noteTv;
    ImageView noteImageIv;

    //Activity Variables
    private String action;
    private String filter;
    int courseId;
    ContentViewLoader contentLoader = new ContentViewLoader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_notes);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(CompanionContentProvider.NOTE_ITEM_TYPE);

        SharedPreferences pref = getSharedPreferences(AssessmentOverviewActivity.ASSESSMENT_PREFS, 0);
        courseId = pref.getInt("courseId", 0);
        Log.d("Load Data", "Course ID in Note: " + courseId);
        Log.d("Load Data", "Passed URI: " + uri);

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
                try {
                    add_photo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.share_note:
                share_note();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void share_note() {
        //Share via email
        String body = noteTv.getText().toString().trim();
        String subjectTitle = noteTitleTv.getText().toString().trim();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Course Note Share: " + subjectTitle);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);

        if(uriImage.toString().equals("Blank")){
            Log.d("Load Data", "No photo to send");
        }
        else{
            sharingIntent.setType("image/jpeg");
            Uri tempUri = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    new File(uriImage.toString()));
            sharingIntent.putExtra(Intent.EXTRA_STREAM, tempUri);
        }

        startActivity(sharingIntent);
    }

    private void add_photo() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        createImageFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, 0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            uriImage = Uri.parse(noteImagePath);
            file = new File(uriImage.getPath());
            try{
                InputStream ims = new FileInputStream(file);
                noteImageIv = (ImageView) findViewById(R.id.course_note_image_view);
                noteImageIv.setImageBitmap(BitmapFactory.decodeStream(ims));
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }

            MediaScannerConnection.scanFile(this, new String[] {uriImage.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener(){
                        public void onScanCompleted(String path, Uri uri){

                        }
                    });
        }
        Log.d("Load Data", "Photo Path: " + uriImage);
        newPhoto = true;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        noteImagePath = image.getAbsolutePath();
        return image;
    }

    private void deleteNote() {
        getContentResolver().delete(CompanionContentProvider.NOTE_URI, filter, null);
        Toast.makeText(this, "Note Deleted", Toast.LENGTH_SHORT);
        setResult(RESULT_OK);
        finish();
    }

    private void updateNote(String title, String text, Uri uri){
        ContentValues value = new ContentValues();
        value.put(DBHelper.NOTE_TITLE, title);
        value.put(DBHelper.NOTE_TEXT, text);
        value.put(DBHelper.NOTE_PHOTO_PATH, uri.toString());
        getContentResolver().update(CompanionContentProvider.NOTE_URI, value, filter, null);
        Toast.makeText(this, "Note Updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        Log.d("Load Data", "Note Updated");
    }

    private void insertNote(String title, String text, Uri uri){
        ContentValues value = new ContentValues();
        value.put(DBHelper.NOTE_TITLE, title);
        value.put(DBHelper.NOTE_TEXT, text);
        value.put(DBHelper.NOTE_COURSE_ID, courseId);
        if(uri!=null) {
            value.put(DBHelper.NOTE_PHOTO_PATH, uri.toString());
        }
        else{
            value.put(DBHelper.NOTE_PHOTO_PATH, "Blank");
        }
        getContentResolver().insert(CompanionContentProvider.NOTE_URI, value);
        Toast.makeText(this, "Note Inserted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        Log.d("Load Data", "Note Inserted");
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

        //Set Note Image
        uriImage = contentLoader.loadNoteImage(CourseNotesActivity.this, uri);
        noteImageIv = (ImageView) findViewById(R.id.course_note_image_view);
        noteImageIv.setImageURI(uriImage);
    }

    private void finishedEditing(){
        Log.d("Load Data", "Action: " + action);
        String newTitle = noteTitleTv.getText().toString().trim();
        String newText = noteTv.getText().toString().trim();


        switch(action){
            case Intent.ACTION_INSERT:
                if(newText.length() == 0){
                    setResult(RESULT_CANCELED);
                }
                else{
                    insertNote(newTitle, newText, uriImage);
                }
                break;
            case Intent.ACTION_EDIT:
                if(newText.length() == 0){
                    deleteNote();
                }
                else if(newText.equals(noteText) && newTitle.equals(noteTitleText) && !newPhoto){
                    setResult(RESULT_CANCELED);
                }
                else{
                    updateNote(newTitle, newText, uriImage);
                }
                break;
        }
        finish();
    }

    @Override
    public void onBackPressed(){
        finishedEditing();
    }
}
