package com.demo.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotes;
//    public static final ArrayList<Note> notes = new ArrayList<>();
    private static final ArrayList<Note> notes = new ArrayList<>();
    private NotesAdapter adapter;

    private NotesDBHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        dbHelper = new NotesDBHelper(this);
        database = dbHelper.getWritableDatabase();

        getData();

//        database.delete(NotesContract.NotesEntry.TABLE_NAME, null, null);

//        if (notes.isEmpty()) {
//            notes.add(new Note("Парикмахер", "Сделать прическу", "Понедельник", 2));
//            notes.add(new Note("Баскетбол", "Игра со школьной командой", "Вторник", 3));
//            notes.add(new Note("Магазин", "Купить новые джинсы", "Понедельник", 3));
//            notes.add(new Note("Стоматолог", "Вылечить зубы", "Понедельник", 2));
//            notes.add(new Note("Парикмахер", "Сделать прическу к выпускному", "Среда", 1));
//            notes.add(new Note("Баскетбол", "Игра со школьной командой", "Вторник", 3));
//            notes.add(new Note("Магазин", "Купить новые джинсы", "Понедельник", 3));
//        }
//
//
//        for (Note note : notes) {
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(NotesContract.NotesEntry.COLUMN_TITLE, note.getTitle());
//            contentValues.put(NotesContract.NotesEntry.COLUMN_DESCRIPTION, note.getDescription());
//            contentValues.put(NotesContract.NotesEntry.COLUMN_DAY_OF_WEEK, note.getDayOfWeek());
//            contentValues.put(NotesContract.NotesEntry.COLUMN_PRIORITY, note.getPriority());
//
//            database.insert(NotesContract.NotesEntry.TABLE_NAME, null, contentValues);
//        }

//        ArrayList<Note> notesFromDb = new ArrayList<>();

        adapter = new NotesAdapter(notes);
//        adapter = new NotesAdapter(notesFromDb);

        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotes.setAdapter(adapter);

        adapter.setOnNoteClickListener(new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(int position) {
                Toast.makeText(MainActivity.this, "Номер позиции: " + position, Toast.LENGTH_SHORT).show();
//                    remove(position);
            }

            @Override
            public void onLongClick(int position) {
                remove(position);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                remove(viewHolder.getAdapterPosition());
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewNotes);
    }


    private void remove(int position) {
        int id = notes.get(position).getId();

        String where = NotesContract.NotesEntry._ID + " = ?";
        String[] whereArgs = new String[] {Integer.toString(id)};

        database.delete(NotesContract.NotesEntry.TABLE_NAME, where, whereArgs);
        getData();

//        notes.remove(position);
        adapter.notifyDataSetChanged();
    }

    public void onClickAddNote(View view) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    private void getData() {
        notes.clear();

        String selection = NotesContract.NotesEntry.COLUMN_PRIORITY + " < ?";
        String[] selectionArgs = new String[]{"2"};

//        Cursor cursor = database.query(NotesContract.NotesEntry.TABLE_NAME, null,null,null,null,null, NotesContract.NotesEntry.COLUMN_PRIORITY);
        Cursor cursor = database.query(NotesContract.NotesEntry.TABLE_NAME, null,selection,selectionArgs,null,null, NotesContract.NotesEntry.COLUMN_PRIORITY);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(NotesContract.NotesEntry._ID));
            String title = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_DESCRIPTION));
            String dayOfWeek = cursor.getString(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_DAY_OF_WEEK));
            int priority = cursor.getInt(cursor.getColumnIndex(NotesContract.NotesEntry.COLUMN_PRIORITY));

            Note note = new Note(id, title, description, dayOfWeek, priority);
//            notesFromDb.add(note);
            notes.add(note);
        }

        cursor.close();

    }
}
