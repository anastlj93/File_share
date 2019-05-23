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
public class GroupFragment extends Fragment {

    private View ContactsView;
    private RecyclerView myContactsList;

    private DatabaseReference ContactsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ContactsView = inflater.inflate(R.layout.fragment_group, container, false);

        myContactsList=(RecyclerView)ContactsView.findViewById(R.id.groups_recycler_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        ContactsRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("groups");

        return ContactsView;
    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(ContactsRef,Contacts.class).build();


        FirebaseRecyclerAdapter<Contacts,ContactsFragment.ContactsViewHolder> adapter /*we imported the contactsViewHolder from another fragment*/
                = new FirebaseRecyclerAdapter<Contacts, ContactsFragment.ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsFragment.ContactsViewHolder holder, int position, @NonNull Contacts model) {

                final String groupID=getRef(position).getKey();

                ContactsRef.child(groupID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            if(dataSnapshot.hasChild("image")){
                                String userImage=dataSnapshot.child("image").getValue().toString();
                                String profileName=dataSnapshot.child("name").getValue().toString();
                                holder.userName.setText(profileName);
                                Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                            } else {
                                String profileName=dataSnapshot.child("userName").getValue().toString();
                                holder.userName.setText(profileName);
                                holder.userStatus.setVisibility(View.INVISIBLE);
                            }

                            final String profileName=dataSnapshot.child("userName").getValue().toString();

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent chatIntent= new Intent(getContext(),ChatGroupActivity.class);
                                    chatIntent.putExtra("visit_user_id",groupID);
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
        myContactsList.setAdapter(adapter);
        adapter.startListening();
    }



    public static class ContactsViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        CircleImageView profileImage;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.user_profile_name);
            profileImage=itemView.findViewById(R.id.users_profile_image);
        }
    }


}
