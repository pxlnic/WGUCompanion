package com.example.wgu_companion.wgucompanion;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class TermCursorAdapter extends CursorAdapter {
    public TermCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_term, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView termNameTv = (TextView) view.findViewById(R.id.term_item_name_text);
        TextView termStartTv = (TextView) view.findViewById(R.id.term_item_dates_text);
        TextView termStatusTv = (TextView) view.findViewById(R.id.term_item_status_text);

        String nameText = cursor.getString(cursor.getColumnIndex(DBHelper.TERM_NAME));
        String dateText = cursor.getString(cursor.getColumnIndex(DBHelper.TERM_START_DATE));
        String statusText = cursor.getString(cursor.getColumnIndex(DBHelper.TERM_STATUS));

        termNameTv.setText(nameText);
        termStartTv.setText(dateText);
        termStatusTv.setText(statusText);
    }
}
