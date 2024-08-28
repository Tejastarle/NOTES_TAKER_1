package com.example.notes_taker_1;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NoteDetailsActivity extends AppCompatActivity {
EditText title,content;
ImageButton savenotebtn;
TextView pagetitle;
TextView delelenote;
boolean iseditMode = false;
String title1,content1,docId1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_details);
        title = findViewById(R.id.notes_title_text);
        content = findViewById(R.id.notes_Content_text);
        savenotebtn = findViewById(R.id.save_note_button);
        pagetitle = findViewById(R.id.page_title);
        delelenote = findViewById(R.id.delete_note_btn);

        //receive data and edit the notes
        title1 = getIntent().getStringExtra("title");
        content1 = getIntent().getStringExtra("content");
        docId1 = getIntent().getStringExtra("docId");

        if(docId1!=null && !docId1.isEmpty()){
            iseditMode=true;
        }
        title.setText(title1);
        content.setText(content1);

        if(iseditMode){
            pagetitle.setText("Edit your note");
            delelenote.setVisibility(View.VISIBLE);
        }


        // save the notes
        savenotebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }

            void saveNote(){
                String noteTitle = title.getText().toString();
                String noteContent = content.getText().toString();

                if(noteTitle == null || noteTitle.isEmpty()){
                    title.setError("Title is required");
                    return;
                }

                Note note =new Note();
                note.setTitle(noteTitle);
                note.setContent(noteContent);
                note.setTimestamp(Timestamp.now());
                saveNoteToFireBase(note);
            }

            void saveNoteToFireBase(Note note){
                DocumentReference documentReference;
                if(iseditMode){
                    // update the note
                    documentReference = Utility.getCollectionReferenceForNote().document(docId1);
                }else{
                    //create new note
                    documentReference = Utility.getCollectionReferenceForNote().document();
                }
                documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //note is added
                            Toast.makeText(NoteDetailsActivity.this, "Note added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(NoteDetailsActivity.this, "Failed while adding note", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        delelenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteNoteFromFirebase();
            }

            void DeleteNoteFromFirebase(){
                DocumentReference documentReference;
                documentReference = Utility.getCollectionReferenceForNote().document(docId1);
                documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //note is deleted
                            Toast.makeText(NoteDetailsActivity.this, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(NoteDetailsActivity.this, "Failed while deleting note", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}