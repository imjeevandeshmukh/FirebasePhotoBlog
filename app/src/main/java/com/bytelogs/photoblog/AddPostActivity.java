package com.bytelogs.photoblog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;

public class AddPostActivity extends AppCompatActivity {


    private static final int MAX_LENGTH = 100;
    TextInputLayout description_layout;
    EditText description;
    Button postBtn;
    ImageView postimg;
    Uri post_img_uri = null;
    Boolean isChanged =false;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    String current_user = null;
    ProgressBar progressBar;
    Bitmap compressedImageFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        mAuth = FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        description_layout = findViewById(R.id.description_layout);
        description = findViewById(R.id.description);
        postBtn = findViewById(R.id.post);
        postimg = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar2);
        postimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AddPostActivity.this);
            }
        });
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String des = description.getText().toString();
                if(!TextUtils.isEmpty(des) && post_img_uri != null){
                    progressBar.setVisibility(View.VISIBLE);
                    final String random = random();
                       StorageReference postPath = storageReference.child("PostImages").child(random+".jpg");
                       postPath.putFile(post_img_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                               final String download_post_url = task.getResult().getDownloadUrl().toString();

                               if(task.isSuccessful()){
                                   File file = new File(post_img_uri.getPath());
                                   try {
                                        compressedImageFile = new Compressor(AddPostActivity.this)
                                                .setMaxHeight(100)
                                                .setMaxWidth(100)
                                                .setQuality(2)
                                                .compressToBitmap(file);
                                   } catch (IOException e) {
                                       e.printStackTrace();
                                   }
                                   ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                   compressedImageFile.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                                   byte[] bytes = byteArrayOutputStream.toByteArray();
                                   UploadTask uploadTask = storageReference.child("PostImages/thumbs").child(random + ".jpg").putBytes(bytes);
                                   uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                       @Override
                                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                           String download_uri_thumb = taskSnapshot.getDownloadUrl().toString();
                                           Map<String,Object> postMap = new HashMap<>();
                                           postMap.put("image_url",download_post_url);
                                           postMap.put("description",des);
                                           postMap.put("user_id",current_user);
                                           postMap.put("thumbnail",download_uri_thumb);
                                           postMap.put("time_stamp",FieldValue.serverTimestamp());
                                           firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                               @Override
                                               public void onComplete(@NonNull Task<DocumentReference> task) {
                                                   progressBar.setVisibility(View.INVISIBLE);
                                                   if(task.isSuccessful()){
                                                       Toast.makeText(AddPostActivity.this,"Post added",Toast.LENGTH_SHORT).show();
                                                       Intent intent = new Intent(AddPostActivity.this,MainActivity.class);
                                                       startActivity(intent);
                                                       finish();


                                                   }else {
                                                       Toast.makeText(AddPostActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                   }

                                               }
                                           });

                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Toast.makeText(AddPostActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                                       }
                                   });




                               }else {
                                   progressBar.setVisibility(View.INVISIBLE);
                                   description_layout.setError(task.getException().getMessage());
                               }

                           }
                       });

                }else {

                    description_layout.setError("Fields can't be empty");
                }

            }
        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                post_img_uri = result.getUri();
                postimg.setImageURI(post_img_uri);
                isChanged =true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
