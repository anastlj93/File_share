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
public class ImagesFragment extends Fragment {

    private View ContactsView;
    private RecyclerView myContactsList;

    private DatabaseReference ContactsRef,UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private Integer chosenFileTypetoSort;

    public ImagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Integer pagePostion = getArguments().getInt("pagePostion");
        chosenFileTypetoSort=pagePostion;

        ContactsView = inflater.inflate(R.layout.fragment_images, container, false);


        myContactsList=(RecyclerView)ContactsView.findViewById(R.id.image_file_recycler_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));


        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Files");
        ContactsRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("userFils");


        return ContactsView;
    }


    @Override
    public void onStart() {
        super.onStart();

        final FirebaseRecyclerOptions options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ContactsRef,Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, ContactsFragment.ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsFragment.ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsFragment.ContactsViewHolder holder, int position, @NonNull Contacts model) {

                final String userIDs=getRef(position).getKey();

                UsersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        if(chosenFileTypetoSort==0) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.child("Type").getValue().equals("image")) {
                                    String userImage = dataSnapshot.child("DownloadLink").getValue().toString();
                                    String profileName = dataSnapshot.child("FileName").getValue().toString();
                                    String profileStatus = dataSnapshot.child("DownloadLink").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);
                                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                } else {
                                    String profileStatus = dataSnapshot.child("DownloadLink").getValue().toString();
                                    String profileName = dataSnapshot.child("FileName").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);
                                }


                                final String profileName = dataSnapshot.child("FileName").getValue().toString();

                                final String profileImage = dataSnapshot.child("DownloadLink").getValue().toString();

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                            }
                        }

                        else if(chosenFileTypetoSort==1) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.child("Type").getValue().equals("video")) {
                                    String userImage = dataSnapshot.child("DownloadLink").getValue().toString();
                                    String profileName = dataSnapshot.child("FileName").getValue().toString();
                                    String profileStatus = dataSnapshot.child("DownloadLink").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);
                                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                } else {
                                    String profileStatus = dataSnapshot.child("DownloadLink").getValue().toString();
                                    String profileName = dataSnapshot.child("FileName").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);
                                }


                                final String profileName = dataSnapshot.child("FileName").getValue().toString();

                                final String profileImage = dataSnapshot.child("DownloadLink").getValue().toString();

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                            }
                        }

                        else if(chosenFileTypetoSort==2) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.child("Type").getValue().equals("pdf")) {
                                    String userImage = dataSnapshot.child("DownloadLink").getValue().toString();
                                    String profileName = dataSnapshot.child("FileName").getValue().toString();
                                    String profileStatus = dataSnapshot.child("DownloadLink").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);
                                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                } else {
                                    String profileStatus = dataSnapshot.child("DownloadLink").getValue().toString();
                                    String profileName = dataSnapshot.child("FileName").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);
                                }


                                final String profileName = dataSnapshot.child("FileName").getValue().toString();

                                final String profileImage = dataSnapshot.child("DownloadLink").getValue().toString();

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                            }
                        }

                        else if(chosenFileTypetoSort==3) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.child("Type").getValue().equals("audio")) {
                                    String userImage = dataSnapshot.child("DownloadLink").getValue().toString();
                                    String profileName = dataSnapshot.child("FileName").getValue().toString();
                                    String profileStatus = dataSnapshot.child("DownloadLink").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);
                                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                }


                                final String profileName = dataSnapshot.child("FileName").getValue().toString();

                                final String profileImage = dataSnapshot.child("DownloadLink").getValue().toString();

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                            }
                        }

                        else if(chosenFileTypetoSort==4) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.child("Type").getValue().equals("contact")) {
                                    String userImage = dataSnapshot.child("DownloadLink").getValue().toString();
                                    String profileName = dataSnapshot.child("FileName").getValue().toString();
                                    String profileStatus = dataSnapshot.child("DownloadLink").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);
                                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                }

                                final String profileName = dataSnapshot.child("FileName").getValue().toString();

                                final String profileImage = dataSnapshot.child("DownloadLink").getValue().toString();

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                            }
                        }

                        else if(chosenFileTypetoSort==5) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.child("Type").getValue().equals("other")) {
                                    String userImage = dataSnapshot.child("DownloadLink").getValue().toString();
                                    String profileName = dataSnapshot.child("FileName").getValue().toString();
                                    String profileStatus = dataSnapshot.child("DownloadLink").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);
                                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                }

                                final String profileName = dataSnapshot.child("FileName").getValue().toString();

                                final String profileImage = dataSnapshot.child("DownloadLink").getValue().toString();

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                            }
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


        myContactsList.setAdapter(adapter);
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
