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

public class TermsOverviewActivity extends AppCompatActivity{

    //ListView Declaration
    private ListView termOverviewLv;
    private TextView programTv;
    private TextView progressTv;
    private ProgressBar progressB;

    //Program and CU Text Variables
    private String programText;
    private int completedCUs = 0;
    private int totalCUs = 0;
    private String progressText = "";

    //Activity Variables
    private int termID;
    private String action = "";
    private TermCursorAdapter adapter;
    private TermCourseSelectCursorAdapter dialogAdapter;
    public static final String TERM_PREFS = "Term_Prefs";
    ContentViewLoader contentLoader = new ContentViewLoader();
    private Uri termUri = null;

    private static final int NEW_TERM_REQUEST_CODE = 2001;
    private static final int VIEW_TERM_REQUEST_CODE = 2002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_overview);

        setViews();

        termOverviewLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TermsOverviewActivity.this, TermDetailActivity.class);
                Uri uri = Uri.parse(CompanionContentProvider.TERM_URI + "/" + id);

                SharedPreferences passedUri = getSharedPreferences(TERM_PREFS, 0);
                SharedPreferences.Editor editor = passedUri.edit();
                editor.putLong("termUri", id);
                editor.commit();

                intent.putExtra(CompanionContentProvider.TERM_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEW_TERM_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_terms_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (id) {
            case R.id.add_term:
                SharedPreferences passedUri = getSharedPreferences(TERM_PREFS, 0);
                SharedPreferences.Editor editor = passedUri.edit();
                editor.putLong("termUri", -1);
                editor.commit();
                addTerm();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addTerm() {
        //Setup Spinner
        List<String> statusArray = new ArrayList<>();
        statusArray.add("Note Attempted");
        statusArray.add("In Progress");
        statusArray.add("Completed");


        final Dialog addTermDialog = new Dialog(TermsOverviewActivity.this);
        addTermDialog.setContentView(R.layout.edit_term_data);

        //Set Custom Dialog Components
        //Term Name
        final EditText termNameEt = (EditText) addTermDialog.findViewById(R.id.term_edit_name_field);

        //Term Status
        final Spinner termStatusSpin = (Spinner) addTermDialog.findViewById(R.id.term_edit_status_spinner);
        ArrayAdapter<String> termStatusAdapter = new ArrayAdapter<>(addTermDialog.getContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.term_status_array));
        termStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        termStatusSpin.setAdapter(termStatusAdapter);

        //Term Start
        final DatePicker termStartPick = (DatePicker) addTermDialog.findViewById(R.id.term_edit_start_picker);
        final CheckBox termStartChk = (CheckBox) addTermDialog.findViewById(R.id.term_edit_start_checkbox);

        //Term End
        final DatePicker termEndPick = (DatePicker) addTermDialog.findViewById(R.id.term_edit_end_picker);
        final CheckBox termEndChk = (CheckBox) addTermDialog.findViewById(R.id.term_edit_end_checkbox);

        //Term Course List
        final ListView termCourseLv = (ListView) addTermDialog.findViewById(R.id.term_edit_course_list);

        final Cursor termCourseCursor = getContentResolver().query(CompanionContentProvider.COURSE_URI, DBHelper.COURSE_COLUMNS,
                null, null, null);
        termCourseCursor.moveToFirst();

        dialogAdapter = new TermCourseSelectCursorAdapter(TermsOverviewActivity.this, termCourseCursor);
        termCourseLv.setAdapter(dialogAdapter);

        setDynamicHeight(termCourseLv);

        //Buttons
        Button termSubmitBtn = (Button) addTermDialog.findViewById(R.id.term_edit_submit_button);
        termSubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Count of courses checked
                int count = 0;
                for(int i=0; i<termCourseLv.getChildCount(); i++){
                    CheckBox verify;
                    verify = (CheckBox) termCourseLv.getChildAt(i).findViewById(R.id.list_item_checkbox);
                    if (verify.isChecked()) {
                        count = count+1;
                    }
                }

                //Verify at least one course checked
                if(count>0) {
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

                    //Get Course ID's
                    List<String> courseIds = contentLoader.loadCourseIds(termCourseCursor);

                    //Determine if course is checked or unchecked
                    List<String> courseCheckedValue = new ArrayList<>();
                    CheckBox cb;
                    for (int i = 0; i < termCourseLv.getChildCount(); i++) {
                        cb = (CheckBox) termCourseLv.getChildAt(i).findViewById(R.id.list_item_checkbox);
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
                            insertTerm(submitTermName, submitTermStatus, submitTermStart, submitTermStartReminder, submitTermEnd, submitTermEndReminder);
                            updateCourseTermIds(termUri, courseIds, courseCheckedValue);

                            addTermDialog.cancel();
                            setViews();
                        }
                        else{
                            Toast.makeText(TermsOverviewActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                //Error if none checked
                else{
                    Toast.makeText(TermsOverviewActivity.this, "At least one course must be selected to submit", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button termDeleteBtn = (Button) addTermDialog.findViewById(R.id.term_edit_delete_button);
        termDeleteBtn.setEnabled(false);

        Button termCancelBtn = (Button) addTermDialog.findViewById(R.id.term_edit_cancel_button);
        termCancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                addTermDialog.cancel();
            }
        });

        addTermDialog.show();
    }

    private void insertTerm(String name, String status, String start, int startReminder, String end, int endReminder) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.TERM_NAME, name);
        values.put(DBHelper.TERM_STATUS, status);
        values.put(DBHelper.TERM_START_DATE, start);
        values.put(DBHelper.TERM_START_REMINDER, startReminder);
        values.put(DBHelper.TERM_END_DATE, end);
        values.put(DBHelper.TERM_END_REMINDER, endReminder);
        termUri = getContentResolver().insert(CompanionContentProvider.TERM_URI, values);
    }

    //Update Courses Term ID
    private void updateCourseTermIds(Uri tempUri, List<String> courseIds, List<String> courseChecked){
        int id = Integer.parseInt(tempUri.getLastPathSegment());
        for(int i=0; i<courseIds.size(); i++) {
            ContentValues courseValues = new ContentValues();
            if(courseChecked.get(i).equals("1")) {
                courseValues.put(DBHelper.COURSE_TERM_ID, id);
                String filter = DBHelper.COURSE_ID + "=" + courseIds.get(i);
                getContentResolver().update(CompanionContentProvider.COURSE_URI, courseValues, filter, null);
                Log.d("Load Data", "Update Complete for Course ID: " + courseIds.get(i));
            }
        }
    }

    private void setViews() {
        //Set Program Name
        programText = contentLoader.loadProgramName(TermsOverviewActivity.this);
        programTv = (TextView) findViewById(R.id.program_name);
        programTv.setText(programText);
        programTv.requestFocus();

        //Set Progress/CU's
        completedCUs = contentLoader.loadCompletedCU(TermsOverviewActivity.this);
        totalCUs = contentLoader.loadTotalCU(TermsOverviewActivity.this);
        progressText = completedCUs + "/" + totalCUs + " CUs";
        progressTv = (TextView) findViewById(R.id.cu_progress_count);
        progressTv.setText(progressText);
        progressTv.requestFocus();

        //Set ProgressBar
        progressB = (ProgressBar) findViewById(R.id.cu_progress_bar);
        progressB.setMax(totalCUs);
        progressB.setProgress(completedCUs);

        //Load Terms
        termOverviewLv = (ListView) findViewById(R.id.terms_overview_list_view);

        Cursor termCursor = getContentResolver().query(CompanionContentProvider.TERM_URI, DBHelper.TERM_COLUMNS, null, null, null);
        termCursor.moveToFirst();
        adapter = new TermCursorAdapter(TermsOverviewActivity.this, termCursor, 0);

        termOverviewLv.setAdapter(adapter);
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
