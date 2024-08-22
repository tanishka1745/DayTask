package com.example.daynotes.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.daynotes.DB.NoteDb;
import com.example.daynotes.R;
import com.example.daynotes.adapter.NoteAdapter;
import com.example.daynotes.entities.Note;
import com.example.listeners.NoteListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteListener {

    public static final  int REQUEST_CODE_ADD_NOTE=1;
    public static final int REQUEST_CODE_UPDATE_NOTE=2;
    public static final int REQUEST_CODE_SHOW_NOTES=3;
    public static final int REQUEST_CODE_SELECT_IMAGE=4;
    public static final int REQUEST_CODE_PERMIT=5;
    private RecyclerView recyclerView;
    List<Note> noteList;
    private NoteAdapter noteAdapter;
    private int notClickPosition=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageViewNote = findViewById(R.id.add_task);
        imageViewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), CreateNotesActivity.class), REQUEST_CODE_ADD_NOTE);
            }
        });
        recyclerView = findViewById(R.id.noteRecylereview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList, this);
        recyclerView.setAdapter(noteAdapter);
        getNotes(REQUEST_CODE_SHOW_NOTES, false);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (noteList.size() != 0) {
                    noteAdapter.searchNotes(s.toString());
                }
            }
        });
        findViewById(R.id.add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_PERMIT);
                }
                else{
                    selectImage();
                }
            }
        });
    }
    private void selectImage()
    {
        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_PERMIT && grantResults.length>0)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                selectImage();
            }
            else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String getPathFormerUri(Uri contentUri)
    {
        String filePath;
        Cursor cursor= getContentResolver()
                .query(contentUri,null,null,null,null);
        if(cursor==null)
        {
            filePath=contentUri.getPath();
        }
        else{
            cursor.moveToFirst();
            int index= cursor.getColumnIndex("_data");
            filePath= cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted)
    {
        class GetNotesTask extends AsyncTask<Void,Void, List<Note>>
        {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NoteDb.getNoteDb(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note>notes) {
                super.onPreExecute();
                if(requestCode==REQUEST_CODE_SHOW_NOTES)
                {
                    noteList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                }
                //Here request code ADD Note we are adding an only first note(newly note) from the db
                // notelist and notify the adapter for the newly inserted and scrolling recyclerview to top.
                else if(requestCode==REQUEST_CODE_ADD_NOTE)
                {
                    noteList.add(0,notes.get(0));
                    noteAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                }
                else{
                    if(requestCode==REQUEST_CODE_UPDATE_NOTE)
                    {
                        noteList.remove(notClickPosition);
                        noteList.add(notClickPosition,notes.get(notClickPosition));
                        noteAdapter.notifyItemChanged(notClickPosition);
                    }
                    if(isNoteDeleted)
                    {
                        noteAdapter.notifyItemRemoved(notClickPosition);
                    }
                    else{
                        noteList.add(notClickPosition,notes.get(notClickPosition));
                        noteAdapter.notifyItemChanged(notClickPosition);
                    }
                }
            }
        }
        new GetNotesTask().execute();
    }
    /*
    the getNote() method is called from onActivityResult() method of activity and we checked the current
    request code is for add note and the result is RESULT_OK.It means a new Note is added from CreateNote
    activity and its result is sent back to this activity that's why we are passing REQUEST_CODE_ADD_NOTE to method
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_ADD_NOTE && resultCode==RESULT_OK)
        {
            getNotes(REQUEST_CODE_SHOW_NOTES,false);
        }
        else if(requestCode==REQUEST_CODE_UPDATE_NOTE && resultCode==RESULT_OK)
        {
            if(data!=null)
            {
                getNotes(REQUEST_CODE_UPDATE_NOTE,data.getBooleanExtra("isNoteDeleted",false));
            }
        }
        else if(requestCode==REQUEST_CODE_SELECT_IMAGE && resultCode==RESULT_OK)
        {
            if(data!=null)
            {
                Uri selectImageUri= data.getData();
                if(selectImageUri!=null)
                {
                    try{
                        String selectPath= getPathFormerUri(selectImageUri);
                        Intent intent= new Intent(getApplicationContext(),CreateNotesActivity.class);
                        intent.putExtra("isFromQuickAction",true);
                        intent.putExtra("quickType","image");
                        intent.putExtra("imagePath",selectPath);
                        startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    //get to create note activity...

    @Override
    public void onNoteClick(Note note, int position)
    {
        notClickPosition=position;
        Intent intent= new Intent(getApplicationContext(),CreateNotesActivity.class);
        intent.putExtra("isViewUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);
    }

}