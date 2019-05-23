package com.example.fileshare;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatGroupActivity extends AppCompatActivity {

    private String messageGroupID,messageReceiverName,messageReceiverImage,messageSenderID;

    private TextView userName,userLastSeen;
    private CircleImageView userImage;

    private Toolbar ChatToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef,GroupMessageKeyRef;

    private ImageButton SendMessageButton,uploadButton,uploadImageButton,uploadPDFButton,uploadVideoButton,uploadTextButton,uploadContactButton,uploadAudioButton;
    private LinearLayout uploadButtonsLayout;

    private EditText MessageInputText;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);

        mAuth=FirebaseAuth.getInstance();
        messageSenderID=mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();

        messageGroupID =getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("visit_user_name").toString();
        //messageReceiverImage=getIntent().getExtras().get("visit_image").toString();

        Toast.makeText(this, messageReceiverName, Toast.LENGTH_SHORT).show();

        InializeControllers();



        userName.setText(messageReceiverName);

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadButtonsLayout.getVisibility()==View.GONE){
                    uploadButtonsLayout.setVisibility(View.VISIBLE);
                }else {
                    uploadButtonsLayout.setVisibility(View.GONE);
                }
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToUploadImageActivity();
            }
        });

    }

    private void InializeControllers() {

        ChatToolbar=(Toolbar)findViewById(R.id.group_chat_toolbar);
        setSupportActionBar(ChatToolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView=layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);

        userImage=(CircleImageView)findViewById(R.id.custom_profile_image);
        userName=(TextView)findViewById(R.id.custom_profile_name);
        userLastSeen=(TextView)findViewById(R.id.custom_user_last_seen);

        SendMessageButton=(ImageButton) findViewById(R.id.group_send_message_btn);
        MessageInputText=(EditText)findViewById(R.id.group_input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList=(RecyclerView)findViewById(R.id.group_messages_list);
        linearLayoutManager=new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);



        uploadButtonsLayout=(LinearLayout)findViewById(R.id.upload_buttons_layout);
        uploadButton=(ImageButton)findViewById(R.id.group_send_file_btn);

        uploadImageButton=(ImageButton)findViewById(R.id.upload_image);
        uploadPDFButton=(ImageButton)findViewById(R.id.upload_pdf);
        uploadVideoButton=(ImageButton)findViewById(R.id.upload_video);
        uploadTextButton=(ImageButton)findViewById(R.id.upload_text);
        uploadContactButton=(ImageButton)findViewById(R.id.upload_contact);
        uploadAudioButton=(ImageButton)findViewById(R.id.upload_audio);

    }

    @Override
    protected void onStart() {
        super.onStart();

        RootRef.child("Groups").child(messageGroupID).child("groupMessages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);

                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();

                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void SendMessage(){
        String messageText=MessageInputText.getText().toString();

        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "Write your message", Toast.LENGTH_SHORT).show();
        }else {

            String messageKEY = RootRef.child("Groups").child(messageGroupID).child("groupMessages").push().getKey();

            HashMap<String,Object> groupMessageKey = new HashMap<>();

            RootRef.child("Groups").child(messageGroupID).child("groupMessages").updateChildren(groupMessageKey);

            GroupMessageKeyRef = RootRef.child("Groups").child(messageGroupID).child("groupMessages").child(messageKEY);

            HashMap<String,Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("message",messageText);
            messageInfoMap.put("type","text");
            messageInfoMap.put("from",messageSenderID);
            GroupMessageKeyRef.updateChildren(messageInfoMap);

            RootRef.updateChildren(messageInfoMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){
                        Toast.makeText(ChatGroupActivity.this, "the message sent successfully...", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ChatGroupActivity.this, "Eroor...", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });

        }

    }



    private void SendUserToUploadImageActivity() {
        Intent uploadImageIntent=new Intent(ChatGroupActivity.this,UploadActivity.class);

        uploadImageIntent.putExtra("visit_group_id",messageGroupID);
        uploadImageIntent.putExtra("visit_group_name",messageReceiverName);

        uploadImageIntent.putExtra("visit_user_id","0");
        uploadImageIntent.putExtra("visit_user_name","0");

        startActivity(uploadImageIntent);
    }


}
