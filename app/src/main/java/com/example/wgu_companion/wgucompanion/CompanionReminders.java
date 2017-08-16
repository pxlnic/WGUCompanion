package com.example.wgu_companion.wgucompanion;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by NicR on 8/15/2017.
 */

public class CompanionReminders {
    private int termId;
    private int courseId;
    private int assessmentId;
    private Date termStart;
    private Date termEnd;
    private Date courseStart;
    private Date courseEnd;
    private Date assessmentGoal;

    public CompanionReminders(){

    }

    //Set Reminder Data
    //Term
    public void setTermReminderId(int id){
        termId = id;
    }
    public void setTermReminderStart(Date start){
        termStart = start;
    }
    public void setTermReminderEnd(Date end){
        termEnd = end;
    }

    //Course
    public void setCourseReminder(int id, Date start, Date end){

    }

    //Assessment
    public void setAssessmentReminder(int id, Date goal){

    }


}
