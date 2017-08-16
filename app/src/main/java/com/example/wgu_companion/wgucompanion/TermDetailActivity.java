package com.example.wgu_companion.wgucompanion;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TermDetailActivity extends AppCompatActivity{

    private static final int VIEW_COURSE_REQUEST_CODE = 2003;
    //Activity Variables
    private String action = "";
    private CourseViewCursorAdapter adapter;
    private TermCourseSelectCursorAdapter dialogAdapter;
    private Uri tempUri = null;
    private CompanionContentProvider provider = new CompanionContentProvider();
    private ContentViewLoader contentLoader = new ContentViewLoader();
    private List<String> courseIdArray = new ArrayList<>();

    //Term Name/Progress/Dates Variables
    private String termNameText = "";
    private String termStartDate = "";
    private String termStartText = "";
    private String termEndDate = "";
    private String termEndText = "";
    private String termStatusText = "";
    private int termCompletedCU = 0;
    private int termTotalCU = 0;
    private String termProgressText = "";

    //View Declarations
    private TextView termTv;
    private TextView termStartTv;
    private TextView termEndTv;
    private TextView termStatusTv;
    private TextView termProgressTv;
    private ProgressBar termProgressB;
    private ListView termCourseLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(CompanionContentProvider.TERM_ITEM_TYPE);

        if(uri == null){
            action = Intent.ACTION_INSERT;
            setTitle("New Term");

            //Load dialog to enter information
        }
        else{
            Log.d("Load Data", "URI: " + uri);
            action = Intent.ACTION_EDIT;
            setTitle("Term Details");
            tempUri = uri;

            setViews(uri);
        }

        termCourseLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(TermDetailActivity.this, CoursesDetailActivity.class);
                Uri uri = Uri.parse(CompanionContentProvider.COURSE_URI + "/" + id);
                intent.putExtra(CompanionContentProvider.COURSE_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEW_COURSE_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_term_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.edit_term:
                try {
                    editTerm();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.delete_term:
                deleteTerm();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteTerm() {
            int id = Integer.parseInt(tempUri.getLastPathSegment());
            String filter = DBHelper.TERM_ID + " = " + id;
            getContentResolver().delete(CompanionContentProvider.TERM_URI, filter, null);
    }

    private void editTerm() throws ParseException {
        List<String> statusArray = new ArrayList<>();

        final Dialog editTermDialog = new Dialog(TermDetailActivity.this);
        editTermDialog.setContentView(R.layout.edit_term_data);

        //Set Custom Dialog Components
        //Term Name
        final EditText termNameEt = (EditText) editTermDialog.findViewById(R.id.term_edit_name_field);
        termNameEt.setText(termNameText);

        //Term Status
        Cursor statusCursor = getContentResolver().query(CompanionContentProvider.STATUS_URI, DBHelper.STATUS_COLUMNS, null, null, null);
        statusCursor.moveToFirst();

        for(int i=0; i<statusCursor.getCount(); i++){
            String name = statusCursor.getString(statusCursor.getColumnIndex(DBHelper.STATUS_NAME));
            statusArray.add(name);
            statusCursor.moveToNext();
        }

        final Spinner termStatusSpin = (Spinner) editTermDialog.findViewById(R.id.term_edit_status_spinner);
        ArrayAdapter<String> termStatusAdapter = new ArrayAdapter<>(editTermDialog.getContext(),
                android.R.layout.simple_spinner_item,
                statusArray);
        termStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        termStatusSpin.setAdapter(termStatusAdapter);

        if (!termStatusText.equals(null)) {
            int spinnerPosition = statusArray.indexOf(termStatusText);
            Log.d("Load Data", "Spinner Position: " + spinnerPosition);
            termStatusSpin.setSelection(spinnerPosition);
        }

        //Term Start
        final DatePicker termStartPick = (DatePicker) editTermDialog.findViewById(R.id.term_edit_start_picker);
        SimpleDateFormat yearFormat  = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat  = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat  = new SimpleDateFormat("dd");
        Date initStartDate = new SimpleDateFormat("yyyy/MM/dd").parse(termStartDate);
        int initStartYear = Integer.parseInt(yearFormat.format(initStartDate));
        int initStartMonth = Integer.parseInt(monthFormat.format(initStartDate))-1;
        int initStartDay = Integer.parseInt(dayFormat.format(initStartDate));

        termStartPick.init(initStartYear, initStartMonth, initStartDay, null);

        boolean startChecked = contentLoader.loadTermStartReminder(TermDetailActivity.this, tempUri);
        final CheckBox termStartChk = (CheckBox) editTermDialog.findViewById(R.id.term_edit_start_checkbox);
        termStartChk.setChecked(startChecked);

        //Term End
        final DatePicker termEndPick = (DatePicker) editTermDialog.findViewById(R.id.term_edit_end_picker);
        Date initEndDate = new SimpleDateFormat("yyyy/MM/dd").parse(termEndDate);
        int initEndYear = Integer.parseInt(yearFormat.format(initEndDate));
        int initEndMonth = Integer.parseInt(monthFormat.format(initEndDate))-1;
        int initEndDay = Integer.parseInt(dayFormat.format(initEndDate));

        termEndPick.init(initEndYear, initEndMonth, initEndDay, null);

        boolean endChecked = contentLoader.loadTermEndReminder(TermDetailActivity.this, tempUri);
        final CheckBox termEndChk = (CheckBox) editTermDialog.findViewById(R.id.term_edit_end_checkbox);
        termEndChk.setChecked(endChecked);

        //Term Course List
        final ListView dialogTermCourseLv = (ListView) editTermDialog.findViewById(R.id.term_edit_course_list);

        final Cursor dialogTermCourseCursor = getContentResolver().query(CompanionContentProvider.COURSE_URI, DBHelper.COURSE_COLUMNS,
                null, null, null);
        dialogTermCourseCursor.moveToFirst();

        dialogAdapter = new TermCourseSelectCursorAdapter(TermDetailActivity.this, dialogTermCourseCursor);
        dialogTermCourseLv.setAdapter(dialogAdapter);

        setDynamicHeight(dialogTermCourseLv);

        //Buttons
        //Submit Button Handler
        Button termSubmitBtn = (Button) editTermDialog.findViewById(R.id.term_edit_submit_button);
        termSubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Count of courses checked
                int count = 0;
                for(int i=0; i<dialogTermCourseLv.getChildCount(); i++){
                    CheckBox verify;
                    verify = (CheckBox) dialogTermCourseLv.getChildAt(i).findViewById(R.id.list_item_checkbox);
                    if (verify.isChecked()) {
                        count = count+1;
                        Log.d("Submit Data", "Count: " + count);
                    }
                }

                //Verify at least one course checked
                if(count>0) {
                    int termIdToPass = Integer.parseInt(tempUri.getLastPathSegment());
                    String submitTermName = termNameEt.getText().toString().trim();
                    String submitTermStatus = termStatusSpin.getSelectedItem().toString().trim();

                    int pickStartDay = termStartPick.getDayOfMonth();
                    int pickStartMonth = termStartPick.getMonth() + 1;
                    int pickStartYear = termStartPick.getYear();

                    String submitTermStart = contentLoader.convertDate(pickStartDay, pickStartMonth, pickStartYear);

                    int submitTermStartReminder = 0;
                    if (termStartChk.isChecked()) {
                        submitTermStartReminder = 1;
                    }

                    int pickEndDay = termEndPick.getDayOfMonth();
                    int pickEndMonth = termEndPick.getMonth() + 1;
                    int pickEndYear = termEndPick.getYear();

                    String submitTermEnd = contentLoader.convertDate(pickEndDay, pickEndMonth, pickEndYear);

                    int submitTermEndReminder = 0;
                    if (termEndChk.isChecked()) {
                        submitTermEndReminder = 1;
                    }

                    //Get Course ID's and Course/Term IDs
                    List<String> courseIds = contentLoader.loadCourseIds(dialogTermCourseCursor);
                    List<String> courseTermIds = contentLoader.loadCourseTermIds(dialogTermCourseCursor);

                    //Determine if course is checked or unchecked
                    List<String> courseCheckedValue = new ArrayList<>();
                    CheckBox cb;
                    for (int i = 0; i < dialogTermCourseLv.getChildCount(); i++) {
                        cb = (CheckBox) dialogTermCourseLv.getChildAt(i).findViewById(R.id.list_item_checkbox);
                        if (cb.isChecked()) {
                            courseCheckedValue.add("1");
                        } else {
                            courseCheckedValue.add("0");
                        }
                    }

                    Log.d("Load Data", "Course ID Count: " + courseIds.size());

                    try {
                        //Get current date and reminder date
                        Date now = new Date();
                        Date verifyStart = new SimpleDateFormat("yyyy/MM/dd").parse(submitTermStart);
                        Date verifyEnd = new SimpleDateFormat("yyyy/MM/dd").parse(submitTermEnd);
                        String message = "";

                        //If reminder checked verify that reminder date is after today
                        if (submitTermStartReminder == 1) {
                            if (verifyStart.before(now)) {
                                message = message + "Start Date must be after today to have a reminder set.";
                            }
                        }
                        if(submitTermEndReminder == 1) {
                            if(verifyEnd.before(now)){
                                message = message + "End Date must be after today to have a reminder set";
                            }
                        }
                        if(verifyStart.after(verifyEnd)){
                            message = message + "Start date cannot be after End date.";
                        }

                        if (message.length()==0) {
                            updateTerm(termIdToPass, submitTermName, submitTermStatus, submitTermStart, submitTermStartReminder, submitTermEnd,
                                    submitTermEndReminder);
                            updateCourseTermIds(termIdToPass, courseIds, courseTermIds, courseCheckedValue);

                            editTermDialog.cancel();
                            setViews(tempUri);
                        }
                        else{
                            Toast.makeText(TermDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                //Error if none checked
                else{
                    Toast.makeText(TermDetailActivity.this, "At least one course must be selected to submit", Toast.LENGTH_SHORT).show();
                    Log.d("Submit Data", "At least one course must be selected to submit");
                }
            }
        });

        //Delete Button Handler
        Button termDeleteBtn = (Button) editTermDialog.findViewById(R.id.term_edit_delete_button);
        termDeleteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Count of courses checked
                int count = 0;
                for(int i=0; i<dialogTermCourseLv.getChildCount(); i++){
                    CheckBox verify;
                    verify = (CheckBox) dialogTermCourseLv.getChildAt(i).findViewById(R.id.list_item_checkbox);
                    if (verify.isChecked()) {
                        count = count+1;
                        Log.d("Submit Data", "Count: " + count);
                    }
                }

                if(count == 0) {
                    deleteTerm();
                    editTermDialog.cancel();
                    Intent intent = new Intent(TermDetailActivity.this, TermsOverviewActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(TermDetailActivity.this, "Cannot delete term while courses selected.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Cancel Button Handler
        Button termCancelBtn = (Button) editTermDialog.findViewById(R.id.term_edit_cancel_button);
        termCancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                editTermDialog.cancel();
            }
        });

        editTermDialog.show();
    }

    //Update Term
    private void updateTerm(int id, String name, String status, String start, int startReminder, String end, int endReminder) {
        ContentValues values = new ContentValues();
        String filter = DBHelper.TERM_ID + "=" + id;
        values.put(DBHelper.TERM_NAME, name);
        values.put(DBHelper.TERM_STATUS, status);
        values.put(DBHelper.TERM_START_DATE, start);
        values.put(DBHelper.TERM_START_REMINDER, startReminder);
        values.put(DBHelper.TERM_END_DATE, end);
        values.put(DBHelper.TERM_END_REMINDER, endReminder);
        getContentResolver().update(CompanionContentProvider.TERM_URI, values, filter, null);
        Log.d("Load Data", "Update Complete for Term");
    }

    //Update Courses Term ID
    private void updateCourseTermIds(int id, List<String> courseIds, List<String> courseTermIds, List<String> courseChecked){
        for(int i=0; i<courseIds.size(); i++) {
            ContentValues courseValues = new ContentValues();
            if(courseChecked.get(i).equals("1")) {
                courseValues.put(DBHelper.COURSE_TERM_ID, id);
            }
            else if(Integer.parseInt(courseTermIds.get(i))==id){
                courseValues.put(DBHelper.COURSE_TERM_ID, -99);
                courseValues.put(DBHelper.COURSE_STATUS, "In Progress");
            }
            String filter = DBHelper.COURSE_ID + "=" + courseIds.get(i);
            getContentResolver().update(CompanionContentProvider.COURSE_URI, courseValues, filter, null);
            Log.d("Load Data", "Update Complete for Course ID: " + courseIds.get(i));
        }
    }

    //Set View Content
    private void setViews(Uri uri){
        //Set Term Name
        termNameText = contentLoader.loadTermName(TermDetailActivity.this, uri);
        termTv = (TextView) findViewById(R.id.term_detail_name);
        termTv.setText(termNameText);
        termTv.requestFocus();

        //Set Term  Start Date
        termStartDate = contentLoader.loadTermStart(TermDetailActivity.this, uri);
        termStartText = "Start Date: " + termStartDate;
        termStartTv = (TextView) findViewById(R.id.term_detail_start_text);
        termStartTv.setText(termStartText);
        termStartTv.requestFocus();

        //Set Term Status
        termStatusText = contentLoader.loadTermStatus(TermDetailActivity.this, uri);
        termStatusTv = (TextView) findViewById(R.id.term_detail_status_text);
        termStatusTv.setText(termStatusText);
        termStatusTv.requestFocus();

        //Set Term End Date
        termEndDate = contentLoader.loadTermEnd(TermDetailActivity.this, uri);
        if(termStatusText.equals("Complete")){
            termEndText = "End Date: " + termEndDate;
        }
        else {
            termEndText = "Expected End Date: " + termEndDate;
        }
        termEndTv = (TextView) findViewById(R.id.term_detail_end_text);
        termEndTv.setText(termEndText);
        termEndTv.requestFocus();

        //Set Term Progress Text
        termCompletedCU = contentLoader.loadTermCompletedCU(TermDetailActivity.this, uri);
        termTotalCU = contentLoader.loadTermTotalCU(TermDetailActivity.this, uri);
        termProgressText = termCompletedCU + "/" + termTotalCU + " CUs";
        termProgressTv = (TextView) findViewById(R.id.term_cu_progress_count);
        termProgressTv.setText(termProgressText);
        termProgressTv.requestFocus();

        //Set Term Progress Bar
        termProgressB = (ProgressBar) findViewById(R.id.term_cu_progress_bar);
        termProgressB.setMax(termTotalCU);
        termProgressB.setProgress(termCompletedCU);

        //Set ListView
        termCourseLv = (ListView) findViewById(R.id.term_detail_list_view);

        SharedPreferences pref = getSharedPreferences(TermsOverviewActivity.TERM_PREFS, 0);
        Long id = pref.getLong("termUri", 0);
        String termIdFilter = DBHelper.COURSE_TERM_ID + " = " + id;

        Cursor termCourseCursor = getContentResolver().query(CompanionContentProvider.COURSE_URI, DBHelper.COURSE_COLUMNS, termIdFilter, null, null);
        termCourseCursor.moveToFirst();
        adapter = new CourseViewCursorAdapter(TermDetailActivity.this, termCourseCursor,0);

        termCourseLv.setAdapter(adapter);
    }

    public static void setDynamicHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        //check adapter if null
        if (adapter == null) {
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = height + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();
    }
}
