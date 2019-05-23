package com.example.fileshare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private RecyclerView CreateGroupRecyclerList;
    private DatabaseReference UserRef;
    private Button btnCreateGroup;
    private EditText etGroupName;

    private String currentUserID;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mAuth=FirebaseAuth.getInstance();

        UserRef= FirebaseDatabase.getInstance().getReference();
        currentUserID=mAuth.getCurrentUser().getUid(); //get the current user ID from here

        CreateGroupRecyclerList=(RecyclerView)findViewById(R.id.create_group_recycler_list);
        CreateGroupRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        btnCreateGroup=(Button)findViewById(R.id.btnCreateGroup);
        etGroupName=(EditText) findViewById(R.id.etGroupName);

        mToolbar=(Toolbar)findViewById(R.id.create_group_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create Group");

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToMainActivity();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        final String groupID = UserRef.child("Users").child(currentUserID).child("groups").push().getKey(); //gettign new ID for the group from firebase (will be used in 'groups' and 'user/groups')

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(UserRef.child("Users"),Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts,CreateGroupViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, CreateGroupViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CreateGroupViewHolder holder, final int position, @NonNull Contacts model) {


                holder.userName.setText(model.getName());
                holder.userStatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String visit_user_id = getRef(position).getKey();
                        holder.itemView.setVisibility(View.GONE);

                        String groupName = etGroupName.getText().toString();


                        UserRef.child("Users").child(currentUserID).child("groups").child(groupID).child("userName").setValue(groupName);
                        UserRef.child("Users").child(currentUserID).child("groups").child(groupID).child("userGroupStatus").setValue("1");

                        UserRef.child("Users").child(visit_user_id).child("groups").child(groupID).child("userName").setValue(groupName);
                        UserRef.child("Users").child(visit_user_id).child("groups").child(groupID).child("userGroupStatus").setValue("2");


                        UserRef.child("Groups").child(groupID).child("groupName").setValue(groupName);
                        UserRef.child("Groups").child(groupID).child("groupUsers").child(currentUserID).child("userGroupStatus").setValue("1");
                        UserRef.child("Groups").child(groupID).child("groupUsers").child(visit_user_id).child("userGroupStatus").setValue("2");

                    }
                });


            }

            @NonNull
            @Override
            public CreateGroupActivity.CreateGroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
                CreateGroupActivity.CreateGroupViewHolder viewHolder = new CreateGroupActivity.CreateGroupViewHolder(view);
                return viewHolder;
            }
        };

        CreateGroupRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class CreateGroupViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userStatus;
        CircleImageView profileImage;

        public CreateGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            userName =itemView.findViewById(R.id.user_profile_name);
            userStatus =itemView.findViewById(R.id.user_status);
            profileImage =itemView.findViewById(R.id.users_profile_image);
        }
    }


    private void SendUserToMainActivity(){
        Intent mainIntent = new Intent(CreateGroupActivity.this,MainActivity.class);
        startActivity(mainIntent);
    }
}
