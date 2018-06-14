package com.bytelogs.photoblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextInputLayout emailLayout,passLayout,confirmPassLayout;
    EditText email,pass,confirmPass;
    Button creatbtn,alreadybtn;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        confirmPass =findViewById(R.id.passconfirm);
        emailLayout = findViewById(R.id.email_layout);
        passLayout = findViewById(R.id.pass_layout);
        confirmPassLayout= findViewById(R.id.pass_confirm_layout);
        alreadybtn = findViewById(R.id.haveaccount);
        creatbtn = findViewById(R.id.create);
        progressBar = findViewById(R.id.progressBar);
        creatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emails= email.getText().toString();
                String passs = pass.getText().toString();
                final String confirmpasss = confirmPass.getText().toString();
                if(!TextUtils.isEmpty(emails) && !TextUtils.isEmpty(passs) && !TextUtils.isEmpty(confirmpasss)) {
                    if(passs.equals(confirmpasss)) {
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(emails, passs).addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.INVISIBLE);
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(RegisterActivity.this,SetAccountActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    confirmPassLayout.setError(task.getException().getMessage().toString());
                                }
                            }
                        });
                    }else {
                        confirmPassLayout.setError("Passwords did not match");
                    }
                }else {
                    if(TextUtils.isEmpty(emails)){
                        emailLayout.setError("Aah!,Email empty");
                    }
                    if(TextUtils.isEmpty(passs)){
                        passLayout.setError("Aah!,Password empty");
                    }
                    if(TextUtils.isEmpty(confirmpasss)){
                        confirmPassLayout.setError("Aah!,Confirm password empty");
                    }

                }

            }
        });
        alreadybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!=null){
            Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
