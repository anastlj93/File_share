package com.example.fileshare;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {


    Button btnImageFiles;
    Spinner  sortSpiner;
    LinearLayout sharedWithLL,fileTypeLL;
    private View PrivateFileView;


    private RecyclerView shardWithList;

    private DatabaseReference ContactsRef,UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public BlankFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        PrivateFileView = inflater.inflate(R.layout.fragment_blank, container, false);

        shardWithList =(RecyclerView)PrivateFileView.findViewById(R.id.shared_with_list);
        shardWithList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth= FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        ContactsRef=FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);

        btnImageFiles=(Button)PrivateFileView.findViewById(R.id.image_sort_btn);


        btnImageFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filePagerActivity=new Intent(getActivity(),FilePagerActivity.class);
                startActivity(filePagerActivity);
            }
        });

        return PrivateFileView;
    }






    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ContactsRef,Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts,ContactsFragment.ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsFragment.ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsFragment.ContactsViewHolder holder, int position, @NonNull Contacts model) {

                final String userIDs=getRef(position).getKey();

                UsersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild("image")){
                                String userImage=dataSnapshot.child("image").getValue().toString();
                                String profileName=dataSnapshot.child("name").getValue().toString();
                                String profileStatus=dataSnapshot.child("status").getValue().toString();

                                holder.userName.setText(profileName);
                                holder.userStatus.setText(profileStatus);
                                Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                            } else {
                                String profileStatus=dataSnapshot.child("status").getValue().toString();
                                String profileName=dataSnapshot.child("name").getValue().toString();

                                holder.userName.setText(profileName);
                                holder.userStatus.setText(profileStatus);
                            }


                            final String profileName=dataSnapshot.child("name").getValue().toString();

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent chatIntent= new Intent(getContext(),ChatActivity.class);

                                    chatIntent.putExtra("visit_user_id",userIDs);
                                    chatIntent.putExtra("visit_user_name",profileName);

                                    startActivity(chatIntent);
                                }
                            });

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @NonNull
            @Override
            public ContactsFragment.ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
                ContactsFragment.ContactsViewHolder viewHolder=new ContactsFragment.ContactsViewHolder(view);
                return viewHolder;
            }
        };


        shardWithList.setAdapter(adapter);
        adapter.startListening();
    }



    public static class ContactsViewHolder extends RecyclerView.ViewHolder{
        TextView userName,userStatus;
        CircleImageView profileImage;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);
        }
    }



}
