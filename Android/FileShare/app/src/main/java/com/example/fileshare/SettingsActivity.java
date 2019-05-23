package com.example.fileshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button UpdateAccountSettigns;
    private EditText userName,userSurname,userStatus;
    private CircleImageView userProfileImage;

    private static final int GalleryPick=1;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserID;
    private StorageReference UserProfileImageRef;

    private ProgressDialog loadingBar;

    private Toolbar SettingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth=FirebaseAuth.getInstance();

        currentUserID=mAuth.getCurrentUser().getUid(); //get the current user ID from here

        RootRef= FirebaseDatabase.getInstance().getReference();
        UserProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingBar = new ProgressDialog(this);

        SettingToolbar=(Toolbar)findViewById(R.id.setting_toolbar);
        setSupportActionBar(SettingToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Account Setting");

        InitializeFields();

        UpdateAccountSettigns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        RetrieveUserInfo();

      userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPick);
            }
        });


    }

    private void UpdateSettings() {
        String setUserName=userName.getText().toString();
        String setUserSurname=userSurname.getText().toString();
        String setStatus=userStatus.getText().toString();

        if(TextUtils.isEmpty(setUserName)){
            Toast.makeText(this,"enter user name",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setUserSurname)){
            Toast.makeText(this,"enter user Surname",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setStatus)){
            Toast.makeText(this,"enter your status",Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String,Object>profileMap=new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("name",setUserName);
            profileMap.put("surname",setUserSurname);
            profileMap.put("status",setStatus);

            RootRef.child("Users").child(currentUserID).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        SendUserToMainActivity();
                        Toast.makeText(SettingsActivity.this,"Profile Updated Successfully",Toast.LENGTH_SHORT).show();
                    }else {
                        String message=task.getException().toString();
                        Toast.makeText(SettingsActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();

                    }
                }
            });


        }
    }

    private void InitializeFields() {
        UpdateAccountSettigns=(Button)findViewById(R.id.update_settings_button);
        userName=(EditText)findViewById(R.id.set_user_name);
        userSurname=(EditText)findViewById(R.id.set_user_surname);
        userStatus=(EditText)findViewById(R.id.set_profile_status);
        userProfileImage=(CircleImageView)findViewById(R.id.set_profile_image);
    }

    private void RetrieveUserInfo() {
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name")&&(dataSnapshot.hasChild("image")))){

                    String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                    String retrieveUserSurname=dataSnapshot.child("surname").getValue().toString();
                    String retrieveStatus=dataSnapshot.child("status").getValue().toString();
                    String retrieveProfileImage=dataSnapshot.child("image").getValue().toString();

                    userName.setText(retrieveUserName);
                    userSurname.setText(retrieveUserSurname);
                    userStatus.setText(retrieveStatus);

                    Picasso.get().load(retrieveProfileImage).into(userProfileImage);



                }else if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))){
                    String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                    String retrieveUserSurname=dataSnapshot.child("surname").getValue().toString();
                    String retrieveStatus=dataSnapshot.child("status").getValue().toString();

                    userName.setText(retrieveUserName);
                    userSurname.setText(retrieveUserSurname);
                    userStatus.setText(retrieveStatus);
                }else {
                    Toast.makeText(SettingsActivity.this, "Update profile info", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){

            loadingBar.setTitle("Set Profile Image");
            loadingBar.setMessage("Loading");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Uri ImageUri=data.getData();

            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK){
                Uri resultUri=result.getUri();
                final StorageReference filePath=UserProfileImageRef.child(currentUserID+".jpg"); //uploade files

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        loadingBar.dismiss();


                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String photoLink = uri.toString();

                                RootRef.child("Users").child(currentUserID).child("image").setValue(photoLink);
                            }
                        });
                    }
                });

            }
        }
    }


    private void SendUserToMainActivity(){
        Intent mainIntent = new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);/////this step will make the user unable to get back to the login activity by pressing back button/////
        startActivity(mainIntent);
        finish();
    }

}
