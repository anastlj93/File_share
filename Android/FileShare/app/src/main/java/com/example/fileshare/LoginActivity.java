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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private Button LoginButton,PhoneLoginButton;
    private EditText userEmail,userPassword;
    private TextView NeedNewAccountLink,ForgetPasswordLink;

    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();

        initializeFields();

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });

    }


    private void AllowUserToLogin() {

        String email=userEmail.getText().toString();
        String passwrod=userPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"please fill the email.",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(passwrod)){
            Toast.makeText(this,"please fill the password.",Toast.LENGTH_SHORT).show();
        }else{

            loadingBar.setTitle("Sign in");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,passwrod).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        loadingBar.dismiss();
                        SendUserToMainActivity();
                        Toast.makeText(LoginActivity.this,"Logged in successfully...",Toast.LENGTH_SHORT).show();
                    }else {
                        loadingBar.dismiss();
                        String message=task.getException().toString();
                        Toast.makeText(LoginActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void initializeFields() {

        LoginButton =(Button)findViewById(R.id.login_button);
        PhoneLoginButton=(Button)findViewById(R.id.phone_login_button);
        userEmail=(EditText)findViewById(R.id.login_email);
        userPassword=(EditText)findViewById(R.id.login_password);
        NeedNewAccountLink=(TextView)findViewById(R.id.need_new_account_link);
        ForgetPasswordLink=(TextView)findViewById(R.id.forget_password_link);

        loadingBar=new ProgressDialog(this);
    }

    private void SendUserToMainActivity(){
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);/////this step will make the user unable to get back to the login activity by pressing back button/////
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }

}
