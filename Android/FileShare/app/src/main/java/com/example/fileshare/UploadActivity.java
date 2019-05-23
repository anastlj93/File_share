package com.example.fileshare;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String fileSenderID, uploadToGroupID, uploadToGroupName,uploadToUserID,uploadToUserName;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef,GroupMessageKeyRef;
    private ImageView imageView;
    private EditText textImageName;
    private Uri imgUri;

    Button btn_brows,btn_upload;

    public static final String FB_STORAGE__PATH="image/";
    public static final int REQUEST_CODE=1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mAuth= FirebaseAuth.getInstance();
        fileSenderID=mAuth.getCurrentUser().getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        uploadToGroupID =getIntent().getExtras().get("visit_group_id").toString();
        uploadToGroupName =getIntent().getExtras().get("visit_group_name").toString();

        uploadToUserID =getIntent().getExtras().get("visit_user_id").toString();
        uploadToUserName =getIntent().getExtras().get("visit_user_name").toString();

        InializeControllers();


        Toast.makeText(this, "upload to: "+ uploadToGroupID, Toast.LENGTH_SHORT).show();


        btn_brows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBrows_Click();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpload_Click();
            }
        });

    }

    private void InializeControllers(){
        imageView = (ImageView)findViewById(R.id.iv_upload);
        textImageName = (EditText)findViewById(R.id.et_file_name);
        btn_brows=(Button)findViewById(R.id.btn_select_upload);
        btn_upload=(Button)findViewById(R.id.btn_upload);
    }


    public void btnBrows_Click(){
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select image"),REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            imgUri=data.getData();
            try{
                Bitmap bm= MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
                imageView.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getImageExt(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    public void btnUpload_Click(){
        final String setFileName=textImageName.getText().toString();

        if(imgUri!=null){
            final ProgressDialog dialog=new ProgressDialog(this);
            dialog.setTitle("uploading image");
            dialog.show();

            final StorageReference ref= mStorageRef.child(FB_STORAGE__PATH+System.currentTimeMillis()+"."+getImageExt(imgUri));

            ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    dialog.dismiss();

                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {



                            final String photoLink = uri.toString();

                            String uploadId=mDatabaseRef.push().getKey();
                            mDatabaseRef.child("Files").child(uploadId).child("DownloadLink").setValue(photoLink);
                            mDatabaseRef.child("Files").child(uploadId).child("FileName").setValue(setFileName);
                            mDatabaseRef.child("Files").child(uploadId).child("Type").setValue("image");


                            if(uploadToUserID.equals("0")) {

                                mDatabaseRef.child("Files").child(uploadId).child("owner").setValue(fileSenderID);
                                mDatabaseRef.child("Files").child(uploadId).child("canSend").setValue(uploadToGroupID);

                                mDatabaseRef.child("Users").child(fileSenderID).child("userFils").child(uploadId).child("State").setValue("owner");

                                mDatabaseRef.child("Groups").child(uploadToGroupID).child("groupFils").child(uploadId).child("Sender").setValue(fileSenderID);


                                String fileNameLink=setFileName+" "+photoLink;

                                String messageKEY = mDatabaseRef.child("Groups").child(uploadToGroupID).child("groupMessages").push().getKey();
                                HashMap<String, Object> groupMessageKey = new HashMap<>();
                                mDatabaseRef.child("Groups").child(uploadToGroupID).child("groupMessages").updateChildren(groupMessageKey);
                                GroupMessageKeyRef = mDatabaseRef.child("Groups").child(uploadToGroupID).child("groupMessages").child(messageKEY);
                                HashMap<String, Object> messageInfoMap = new HashMap<>();
                                messageInfoMap.put("message", fileNameLink);
                                messageInfoMap.put("type", "text");
                                messageInfoMap.put("from", fileSenderID);
                                GroupMessageKeyRef.updateChildren(messageInfoMap);
                                mDatabaseRef.updateChildren(messageInfoMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(UploadActivity.this, "the message sent successfully...", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(UploadActivity.this, "Eroor...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });




                            } else if(uploadToGroupID.equals("0")){


                                Toast.makeText(UploadActivity.this, "the sender is a user", Toast.LENGTH_SHORT).show();

                                mDatabaseRef.child("Files").child(uploadId).child("owner").setValue(fileSenderID);
                                mDatabaseRef.child("Files").child(uploadId).child("canSend").setValue(uploadToUserID);
                                mDatabaseRef.child("Users").child(fileSenderID).child("userFils").child(uploadId).child("State").setValue("owner");
                                mDatabaseRef.child("Users").child(uploadToUserID).child("userFils").child(uploadId).child("state").setValue("canSend");


                                String messageSenderRef = "Messages/" + fileSenderID + "/" + uploadToUserID;
                                String messageReceiverRef = "Messages/" + uploadToUserID + "/" + fileSenderID;

                                DatabaseReference userMessageKeyRef=mDatabaseRef.child("Messages").child(fileSenderID).child(uploadToUserID).push();

                                String messagePushID=userMessageKeyRef.getKey();

                                Map messageTextBody=new HashMap();
                                messageTextBody.put("message",photoLink);
                                messageTextBody.put("type","text");
                                messageTextBody.put("from",fileSenderID);

                                Map messageBodyDetails=new HashMap();

                                messageBodyDetails.put(messageSenderRef + "/" + messagePushID,messageTextBody);
                                messageBodyDetails.put(messageReceiverRef + "/" + messagePushID,messageTextBody);

                                mDatabaseRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if(task.isSuccessful()){
                                            Toast.makeText(UploadActivity.this, "the message sent successfully...", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(UploadActivity.this, "Eroor...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }


                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    dialog.dismiss();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    dialog.setMessage("Uploaded "+(int)progress+"%");
                }
            });

        }else {

        }
    }


}





