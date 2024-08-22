package com.example.daynotes.activities;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.daynotes.DB.NoteDb;
import com.example.daynotes.R;
import com.example.daynotes.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.search.SearchView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNotesActivity extends AppCompatActivity {

    EditText inputNoteTitle,inputNoteSubTitle,inputNoteText;
    TextView textDateTime;

    private String selectedNoteColor;
    private ImageView imageNote;
    private Note alreadyAvailableNote;
    private View viewSubtitle;
    private String selectedImagePath;
    private static final int REQUEST_CODE_STORAGE=1;
    private static final int REQUEST_CODE_SELECT_IMAGE=2;
    private LinearLayout layoutWebUrl;
    private TextView textWebUrl;
    private AlertDialog alertDialog;
    private AlertDialog alertDialogDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notes);
        inputNoteTitle=findViewById(R.id.inputNoteTitle);
        inputNoteSubTitle=findViewById(R.id.inputNoteSubTitle);
        inputNoteText=findViewById(R.id.inputNote);
        textDateTime=findViewById(R.id.textDateTime);
        viewSubtitle=findViewById(R.id.viewsubtitleIndicator);
        imageNote=findViewById(R.id.imageNote);
        textWebUrl=findViewById(R.id.textWebUrl);
        layoutWebUrl=findViewById(R.id.layoutWebUrl);
        //give default color here..
        selectedNoteColor="#808080";
        selectedImagePath="";
        if(getIntent().getBooleanExtra("isViewUpdate",false))
        {
            alreadyAvailableNote= (Note) getIntent().getSerializableExtra("note");
            setViewUpdateData();
        }

        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );
        ImageView imageSave= findViewById(R.id.imagesave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Saving Notes Here...
                saveNote();
            }
        });
        findViewById(R.id.imageRemoveWebUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textWebUrl.setText(null);
                layoutWebUrl.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.imageRemoveImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(View.GONE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
                selectedImagePath="";
            }
        });
        initExtra();
        setSubtitleIndicator();
    }
    private void setViewUpdateData()
    {
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubTitle.setText(alreadyAvailableNote.getSubtitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());
        textDateTime.setText(alreadyAvailableNote.getDateTime());
        if(alreadyAvailableNote.getImagePath()!=null && !alreadyAvailableNote.getImagePath().trim().isEmpty())
        {
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            selectedImagePath=alreadyAvailableNote.getImagePath();
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
        }
        if(alreadyAvailableNote.getWeblink()!=null && !alreadyAvailableNote.getWeblink().trim().isEmpty())
        {
            textWebUrl.setText(alreadyAvailableNote.getWeblink());
            layoutWebUrl.setVisibility(View.VISIBLE);
        }
    }
    private void saveNote()
    {
        if(inputNoteTitle.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "Note title ", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(inputNoteSubTitle.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "Note SubTitle", Toast.LENGTH_SHORT).show();
            return;
        }
        final Note note= new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubTitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());
        note.setImagePath(selectedImagePath);
        note.setColor(selectedNoteColor);

        //Here we are setting id new notw from already available note since we have set onConflictStrategy
        //to "REPLACE" in NoteDao it will replaced and updated note to new note
        if(alreadyAvailableNote!=null)
        {
            note.setId(alreadyAvailableNote.getId());
        }

        if(layoutWebUrl.getVisibility()==View.VISIBLE)
        {
            note.setWeblink(textWebUrl.getText().toString());
        }

        class SaveNoteTask extends AsyncTask<Void,Void,Void>
        {

            @Override
            protected Void doInBackground(Void... voids) {

                NoteDb.getNoteDb(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                Intent intent= new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }
    private  void initExtra()
    {
        final LinearLayout layout= findViewById(R.id.layoutextra);
        final BottomSheetBehavior bottomSheetBehavior= BottomSheetBehavior.from(layout);
        layout.findViewById(R.id.layoutextra).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState()!= BottomSheetBehavior.STATE_EXPANDED)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else{
                    bottomSheetBehavior.setState(STATE_COLLAPSED);
                }
            }
        });
        final ImageView imageView1= layout.findViewById(R.id.imagecolor1);
        final ImageView imageView2= layout.findViewById(R.id.imageColor2);
        final ImageView imageView3= layout.findViewById(R.id.imageColor3);
        final ImageView imageView4= layout.findViewById(R.id.imageColor4);

        layout.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor="#00000";
                imageView1.setImageResource(R.drawable.done);
                imageView2.setImageResource(0);
                imageView3.setImageResource(0);
                imageView4.setImageResource(0);
                setSubtitleIndicator();
            }
        });
        layout.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor="#E4BF37";
                imageView2.setImageResource(R.drawable.done);
                imageView1.setImageResource(0);
                imageView3.setImageResource(0);
                imageView4.setImageResource(0);
                setSubtitleIndicator();
            }
        });
        layout.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor="#FF0000";
                imageView3.setImageResource(R.drawable.done);
                imageView2.setImageResource(0);
                imageView1.setImageResource(0);
                imageView4.setImageResource(0);
                setSubtitleIndicator();
            }
        });
        layout.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor="#87CEEB";
                imageView4.setImageResource(R.drawable.done);
                imageView2.setImageResource(0);
                imageView1.setImageResource(0);
                imageView3.setImageResource(0);
                setSubtitleIndicator();
            }
        });
        if(alreadyAvailableNote!= null && alreadyAvailableNote.getColor()!=null && alreadyAvailableNote.getColor().trim().isEmpty())
        {
            switch (alreadyAvailableNote.getColor())
            {
                case "#E4BF37":
                    layout.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#FF0000":
                    layout.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#87CEEB":
                    layout.findViewById(R.id.viewColor4).performClick();

            }
        }
        layout.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(CreateNotesActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE);
                }
                else{
                    selectImage();
                }
            }
        });
        layout.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(STATE_COLLAPSED);
                showAddURLDialog();
            }
        });

        if(alreadyAvailableNote!=null)
        {
            layout.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNoteDialog();
                }
            });
        }
    }
    @SuppressLint("MissingInflatedId")
    private void showDeleteNoteDialog()
    {
        if(alertDialogDel==null)
        {
            AlertDialog.Builder builder= new AlertDialog.Builder(CreateNotesActivity.this);
            View view= LayoutInflater.from(this).inflate(R.layout.layout_delete_note,(ViewGroup) findViewById(R.id.layoutDeleteNoteContainer));
            builder.setView(view);
            alertDialogDel=builder.create();
            if(alertDialogDel.getWindow()!=null)
            {
                alertDialogDel.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    class DeleteNoteTask extends AsyncTask<Void,Void,Void>
                    {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            NoteDb.getNoteDb(getApplicationContext()).noteDao()
                                    .deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            Intent i= new Intent();
                            i.putExtra("isNoteDeleted",true);
                            setResult(RESULT_OK,i);
                            finish();
                        }
                    }
                    new DeleteNoteTask().execute();
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogDel.dismiss();
                }
            });
        }
        alertDialogDel.show();
    }
    private void setSubtitleIndicator()
    {
        GradientDrawable gradientDrawable= (GradientDrawable) viewSubtitle.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
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
        if(requestCode==REQUEST_CODE_STORAGE && grantResults.length>0)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_SELECT_IMAGE && resultCode==RESULT_OK)
        {
            if(data!=null)
            {
                Uri selectImage= data.getData();
                if(selectImage!=null)
                {
                    try{
                        InputStream inputStream= getContentResolver().openInputStream(selectImage);
                        Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                        selectedImagePath= getPathFormerUri(selectImage);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
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
    private void showAddURLDialog()
    {
        if(alertDialog==null)
        {
            AlertDialog.Builder builder= new AlertDialog.Builder(CreateNotesActivity.this);
            View view= LayoutInflater.from(this).inflate(R.layout.layout_add_url,(ViewGroup) findViewById(R.id.layoutWebContainer));
            builder.setView(view);
            alertDialog=builder.create();
            if(alertDialog.getWindow()!=null)
            {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputUrl= view.findViewById(R.id.inputUrl);
            inputUrl.requestFocus();
            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(inputUrl.getText().toString().trim().isEmpty())
                    {
                        Toast.makeText(CreateNotesActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    }
                    else if(!Patterns.WEB_URL.matcher(inputUrl.getText().toString()).matches())
                    {
                        Toast.makeText(CreateNotesActivity.this, "Enter Valid Url", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        textWebUrl.setText(inputUrl.getText().toString());
                        layoutWebUrl.setVisibility(View.VISIBLE);
                        alertDialog.dismiss();
                    }
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }
        alertDialog.show();
    }
}