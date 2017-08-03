package com.example.wgu_companion.wgucompanion;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

public class EditContent extends DialogFragment{

    public EditContent(){

    }

    //Edit TextView
    public String editText(final Activity a, String hint, String text){
        final String[] passedText = new String[1];

        //Edit Text field
        final EditText v = new EditText(a);
        if(text.length() == 0){
            v.setHint(hint);
        }
        else {
            v.setText(text);
        }

        //Dialog Creation
        AlertDialog.Builder editBuilder = new AlertDialog.Builder(a);
        editBuilder.setMessage("Edit Text?").
                setView(v).
                setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        passedText[0] = v.getText().toString();
                    }
                }).
                setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        passedText[0] = "";
                    }
                });
        AlertDialog b = editBuilder.create();
        b.show();
        return passedText[0];
    }

    //Edit List View
    public void editList(final Activity a){

    }

    //Edit Photo
}
