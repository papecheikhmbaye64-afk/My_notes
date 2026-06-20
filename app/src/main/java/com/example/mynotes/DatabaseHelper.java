package com.example.mynotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "secure_notes.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NOTES = "notes_table";

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_COLOR = "color";
    private static final String KEY_DATE_CREATED = "date_created";
    private static final String KEY_DATE_MODIFIED = "date_modified";
    private static final String KEY_FAVORITE = "is_favorite";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TITLE + " TEXT,"
                + KEY_CONTENT + " TEXT,"
                + KEY_COLOR + " TEXT,"
                + KEY_DATE_CREATED + " INTEGER,"
                + KEY_DATE_MODIFIED + " INTEGER,"
                + KEY_FAVORITE + " INTEGER" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    public void addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_TITLE, note.getTitle());
        v.put(KEY_CONTENT, note.getContent());
        v.put(KEY_COLOR, note.getColor());
        v.put(KEY_DATE_CREATED, note.getDateCreated());
        v.put(KEY_DATE_MODIFIED, note.getDateModified());
        v.put(KEY_FAVORITE, note.isFavorite());
        db.insert(TABLE_NOTES, null, v);
        db.close();
    }

    public List<Note> getAllNotes() {
        List<Note> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NOTES, null);

        if (c.moveToFirst()) {
            do {
                list.add(new Note(
                        c.getInt(0), c.getString(1), c.getString(2),
                        c.getString(3), c.getLong(4), c.getLong(5), c.getInt(6)
                ));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(KEY_TITLE, note.getTitle());
        v.put(KEY_CONTENT, note.getContent());
        v.put(KEY_COLOR, note.getColor());
        v.put(KEY_DATE_MODIFIED, note.getDateModified());
        v.put(KEY_FAVORITE, note.isFavorite());
        return db.update(TABLE_NOTES, v, KEY_ID + " = ?", new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}