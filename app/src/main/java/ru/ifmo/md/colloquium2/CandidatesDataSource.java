package ru.ifmo.md.colloquium2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by creed on 11.11.14.
 */
public class CandidatesDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_NAME,
            MySQLiteHelper.COLUMN_RESULT};

    public CandidatesDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Candidate addCandidate(String name) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, name);
        long insertId = database.insert(MySQLiteHelper.TABLE_CANDIDATES, null, values);
        Cursor cursor = database.query(
                MySQLiteHelper.TABLE_CANDIDATES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Candidate newCandidate = cursorToCandidate(cursor);
        cursor.close();
        return newCandidate;
    }

    public Candidate getCandidate(int id) {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_CANDIDATES, allColumns,
                MySQLiteHelper.COLUMN_ID+"="+id, null, null, null, null);
        cursor.moveToFirst();
        Candidate newCandidate = cursorToCandidate(cursor);
        cursor.close();
        return newCandidate;
    }

    public void voteForCandidate(int id) {
        Candidate candidate = getCandidate(id);
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_RESULT, candidate.getResult()+1);
        database.update(MySQLiteHelper.TABLE_CANDIDATES, values, MySQLiteHelper.COLUMN_ID+"="+id, null);
    }

    public void updateCandidate(int id, String name) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, name);
        database.update(MySQLiteHelper.TABLE_CANDIDATES, values, MySQLiteHelper.COLUMN_ID+"="+id, null);
    }

    public void deleteCandidate(int id) {
        database.delete(MySQLiteHelper.TABLE_CANDIDATES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Candidate> getAllCandidates(Boolean needToSort) {
        List<Candidate> candidates = new ArrayList<Candidate>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CANDIDATES,
                allColumns, null, null, null, null, (needToSort ? "result desc" : null));

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Candidate candidate = cursorToCandidate(cursor);
            candidates.add(candidate);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return candidates;
    }

    public void clearCandidates() {
        database.delete(MySQLiteHelper.TABLE_CANDIDATES, null, null);
    }

    public void clearResults() {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_RESULT, 0);
        database.update(MySQLiteHelper.TABLE_CANDIDATES, values, null, null);
    }

    private Candidate cursorToCandidate(Cursor cursor) {
        Candidate comment = new Candidate();
        comment.setId(cursor.getInt(0));
        comment.setName(cursor.getString(1));
        comment.setResult(cursor.getInt(2));
        return comment;
    }
}
