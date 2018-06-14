package com.bytelogs.photoblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {


    EditText commentEditText;
    ImageView commentBtn;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String postid,userid;
    ArrayList<CommentPojo> commentPojoArrayList;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        postid = getIntent().getStringExtra("POSTID");
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        commentEditText = findViewById(R.id.editText);
        commentBtn = findViewById(R.id.commentbtn);
        userid = firebaseAuth.getCurrentUser().getUid();
        commentPojoArrayList = new ArrayList<>();
        setTitle("Comments");
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        final CommentsAdapter commentsAdapter = new CommentsAdapter(commentPojoArrayList);
        recyclerView.setAdapter(commentsAdapter);
        firebaseFirestore.collection("Posts/"+postid+"/Comments").addSnapshotListener(CommentActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty()){
                    for(DocumentChange doc :documentSnapshots.getDocumentChanges()){
                        if(doc.getType() == DocumentChange.Type.ADDED){
                            String commentid = doc.getDocument().getId();
                            CommentPojo commentPojo = doc.getDocument().toObject(CommentPojo.class);
                            Log.d("message",commentPojo.getMessage());
                            commentPojoArrayList.add(commentPojo);
                            commentsAdapter.notifyDataSetChanged();


                        }
                    }


                }

            }
        });

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentString = commentEditText.getText().toString();
                if(!TextUtils.isEmpty(commentString)){
                    Map<String,Object> commentmap = new HashMap<>();
                    commentmap.put("message",commentString);
                    commentmap.put("user_id",userid);
                    commentmap.put("time_stamp", FieldValue.serverTimestamp());
                    firebaseFirestore.collection("Posts/"+postid+"/Comments").add(commentmap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if(!task.isSuccessful()){
                                Toast.makeText(CommentActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }else {
                                commentEditText.setText("");
                            }

                        }
                    });
                }
            }
        });
    }
}
