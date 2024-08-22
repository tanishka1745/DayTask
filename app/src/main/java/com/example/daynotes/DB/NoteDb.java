package com.example.daynotes.DB;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.daynotes.DAO.NoteDao;
import com.example.daynotes.entities.Note;

@Database(entities = Note.class, version = 1, exportSchema = false)
public  abstract class NoteDb   extends RoomDatabase {


    private static NoteDb noteDb;

    public static synchronized NoteDb getNoteDb(Context context)
    {
        if(noteDb==null)
        {
            noteDb= Room.databaseBuilder(context,NoteDb.class,"note_db").build();
        }
        return noteDb;
    }
    //create abstract method of DAO
    public abstract NoteDao noteDao();
}
