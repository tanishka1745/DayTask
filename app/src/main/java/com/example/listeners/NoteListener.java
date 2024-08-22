package com.example.listeners;

import com.example.daynotes.entities.Note;

public interface NoteListener {
    void onNoteClick(Note note, int position);
}
