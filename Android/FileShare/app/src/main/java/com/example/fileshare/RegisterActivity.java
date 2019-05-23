package com.example.fileshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText UserEmail,UserPassword;
    private TextView AlreadyHaveAccountLink;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth=FirebaseAuth.getInstance();
        RootRef=FirebaseDatabase.getInstance().getReference();

        initializeFields();

        AlreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });

    }



    private void CreateNewAccount() {

        String email=UserEmail.getText().toString();
        String passwrod=UserPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"please fill the email.",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(passwrod)){
            Toast.makeText(this,"please fill the password.",Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,passwrod).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        String currentUserID=mAuth.getCurrentUser().getUid();
                        RootRef.child("Users").child(currentUserID).setValue("");

                        SendUserToMainActivity();
                        loadingBar.dismiss();

                        Toast.makeText(RegisterActivity.this,"Account Created Successfully",Toast.LENGTH_SHORT).show();
                    }else {
                        loadingBar.dismiss();
                        String message=task.getException().toString();
                        Toast.makeText(RegisterActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
                    }

                }

            });
        }

    }



    private void initializeFields() {
        CreateAccountButton=(Button)findViewById(R.id.register_button);
        UserEmail=(EditText)findViewById(R.id.register_email);
        UserPassword=(EditText)findViewById(R.id.register_password);
        AlreadyHaveAccountLink=(TextView)findViewById(R.id.already_have_acccount_link);

        loadingBar=new ProgressDialog(this);
    }

    private void SendUserToLoginActivity(){
        Intent loginIntent=new Intent(RegisterActivity.this,LoginActivity.class);
    }

    private void SendUserToMainActivity(){
        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);/////this step will make the user unable to get back to the login activity by pressing back button/////
        startActivity(mainIntent);
        finish();
    }

}
