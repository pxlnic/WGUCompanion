package com.example.wgu_companion.wgucompanion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class TermsOverviewActivity extends AppCompatActivity {

    //Activity Variables
    private int termID;
    private String action = "";

    private static final int NEW_TERM_REQUEST_CODE = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_overview);
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


        switch(id){
            case R.id.add_term:
                addTerm();
                break;
        }
        return true;
    }

    private void addTerm() {
        Intent menuIntent;
        menuIntent = new Intent(TermsOverviewActivity.this, TermDetailActivity.class);
        startActivityForResult(menuIntent, NEW_TERM_REQUEST_CODE);
    }
}
