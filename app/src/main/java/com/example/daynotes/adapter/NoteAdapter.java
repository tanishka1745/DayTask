package com.example.daynotes.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daynotes.R;
import com.example.daynotes.entities.Note;
import com.example.listeners.NoteListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>
{

    private List<Note>list;
    private NoteListener noteListener;

    private Timer timer;
    private List<Note> noteSource;

    public NoteAdapter(List<Note>list,NoteListener noteListener)
    {
        this.list=list;
        this.noteListener=noteListener;
        noteSource=list;
    }

    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container,parent,false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {

        holder.setNote(list.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteListener.onNoteClick(list.get(position),position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle,textSub,textDate;
        LinearLayout layoutNote;
        RoundedImageView roundedImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle=itemView.findViewById(R.id.textTitle);
            textSub=itemView.findViewById(R.id.subtitleshow);
            textDate=itemView.findViewById(R.id.textdatetime1);
            layoutNote=itemView.findViewById(R.id.layoutNote);
            roundedImageView=itemView.findViewById(R.id.roundImage);
        }
        void setNote(Note note)
        {
            textTitle.setText(note.getTitle());
            if(note.getSubtitle().trim().isEmpty())
            {
                textSub.setVisibility(View.GONE);
            }
            else{
                textSub.setText(note.getSubtitle());
            }
            textDate.setText(note.getDateTime());
            GradientDrawable drawable= (GradientDrawable) layoutNote.getBackground();
            if(note.getColor()!=null)
            {
                drawable.setColor(Color.parseColor(note.getColor()));
            }
            else{
                drawable.setColor(Color.parseColor("#333333"));
            }
            if(note.getImagePath()!=null)
            {
                roundedImageView.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                roundedImageView.setVisibility(View.VISIBLE);
            }
            else{
                roundedImageView.setVisibility(View.GONE);
            }
        }
    }
    public  void searchNotes(final String searchKeyword)
    {
        timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyword.trim().isEmpty())
                {
                    list= noteSource;
                }
                else{
                    ArrayList<Note> tmp= new ArrayList<>();
                    for(Note note: noteSource)
                    {
                        if(note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                        note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                        note.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase()))
                        {
                            tmp.add(note);
                        }
                    }
                    list= tmp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        },500);
    }
    public void cancelTimer()
    {
        if(timer!=null)
        {
            timer.cancel();
        }
    }
}