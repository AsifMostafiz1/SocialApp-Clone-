package com.example.socialapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialapp.Model.Friend;
import com.example.socialapp.Model.Users;
import com.example.socialapp.R;
import com.example.socialapp.viewHolder.FindFriendViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView recyclerView;
    private DatabaseReference friendsRef,userRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mToolbar = findViewById(R.id.friendActivityAppBar);
        recyclerView = findViewById(R.id.FriendRecyclerViewID);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");


        recyclerView.hasFixedSize();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Find Friend");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        FirebaseRecyclerOptions<Friend> options = new FirebaseRecyclerOptions.Builder<Friend>()
                .setQuery(friendsRef,Friend.class)
                .build();


        FirebaseRecyclerAdapter<Friend, FindFriendViewHolder>  adapter = new FirebaseRecyclerAdapter<Friend, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FindFriendViewHolder findFriendViewHolder, int i, @NonNull final Friend friend)
            {
                final String friendsUserID = getRef(i).getKey();

                friendsRef.child(friendsUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        final String date = dataSnapshot.child("date").getValue().toString();

                        userRef.child(friendsUserID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                final String userProfileImage = dataSnapshot.child("imageUrl").getValue().toString();
                                final String userFullName = dataSnapshot.child("userFullName").getValue().toString();

                                findFriendViewHolder.userFullName.setText(userFullName);
                                findFriendViewHolder.userStatus.setText("Friend Since: "+date);
                                Picasso.get().load(userProfileImage).into(findFriendViewHolder.userProfileImage);


                                findFriendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {

                                        CharSequence option[] = new CharSequence[]
                                                {
                                                   userFullName+"'s profile" ,
                                                   "Send Message"
                                                };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);

                                        builder.setTitle("Choose Your Option");
                                        builder.setItems(option, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                if (which==0)
                                                {
                                                    Intent intent = new Intent(FriendsActivity.this,FriendsProfileActivity.class);
                                                    intent.putExtra("receiverUserId",friendsUserID);
                                                    startActivity(intent);
                                                }
                                                else if(which==1)
                                                {
                                                    Intent intent = new Intent(FriendsActivity.this,MessageActivity.class);
                                                    intent.putExtra("receiverUserId",friendsUserID);
                                                    intent.putExtra("userFullName",userFullName);
                                                    intent.putExtra("userProfileImage",userProfileImage);
                                                    startActivity(intent);

                                                }

                                            }
                                        });
                                        builder.show();


                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(FriendsActivity.this).inflate(R.layout.all_find_friend_show_list,parent,false);
                FindFriendViewHolder findFriendViewHolder = new FindFriendViewHolder(view);
                return findFriendViewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();



    }


    private void userCurrentState(String state)
    {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDateForMate = new SimpleDateFormat("MMM dd,yyyy");

        String currentDate= currentDateForMate.format(calendar.getTime());


        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm a");
        String currentTime = currentTimeFormat.format(calendar.getTime());

        Map currentStateMap = new HashMap();
        currentStateMap.put("time",currentTime);
        currentStateMap.put("date",currentDate);
        currentStateMap.put("state",state);

        userRef.child(currentUserID).child("userState")
                .updateChildren(currentStateMap);

    }

    @Override
    protected void onStart() {
        super.onStart();
        userCurrentState("online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        userCurrentState("offline");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userCurrentState("offline");

    }
}
