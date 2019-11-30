package com.example.socialapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.socialapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private CircularImageView profileImage;
    private TextView userFullName,userStatus,userGender,userRelationship,userCountry;
    private DatabaseReference userRef,friendsRef,postRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private Toolbar mToolbar;
    private Button myTotalFriends,myTotalPosts;
    private int numFriends=0,numPosts=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
    }

    private void init()

    {
        profileImage = findViewById(R.id.profile_activity_profile_image);
        userFullName = findViewById(R.id.profile_userFullNameTV);
        userGender = findViewById(R.id.profile_userGender);
        userCountry = findViewById(R.id.profile_userCountry);
        userRelationship = findViewById(R.id.profile_userRelationship);
        userStatus = findViewById(R.id.profile_userStatus);
        mToolbar = findViewById(R.id.profile_page_app_bar);
        myTotalFriends = findViewById(R.id.MyTotalNumberOfFriends);
        myTotalPosts = findViewById(R.id.MyTotalNumberOfPost);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);



        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        myTotalFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(ProfileActivity.this,FriendsActivity.class));
            }
        });
        myTotalPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(ProfileActivity.this,MyAllPostsActivity.class));

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.hasChild("imageUrl"))
                    {
                        String image = dataSnapshot.child("imageUrl").getValue().toString();
                        Picasso.get().load(image).into(profileImage);
                    }

                    if (dataSnapshot.hasChild("userFullName"))
                    {
                        String name = dataSnapshot.child("userFullName").getValue().toString();
                        userFullName.setText(name);
                        getSupportActionBar().setTitle(name);
                    }

                    if (dataSnapshot.hasChild("status"))
                    {
                        String status = dataSnapshot.child("status").getValue().toString();
                        userStatus.setText(status);
                    }

                    if (dataSnapshot.hasChild("gender"))
                    {
                        String gender = dataSnapshot.child("gender").getValue().toString();
                        userGender.setText(gender);
                    }

                    if (dataSnapshot.hasChild("userCountry"))
                    {
                        String country = dataSnapshot.child("userCountry").getValue().toString();
                        userCountry.setText(country);
                    }

                    if (dataSnapshot.hasChild("relationship"))
                    {
                        String relationship = dataSnapshot.child("relationship").getValue().toString();
                        userRelationship.setText(relationship);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        friendsRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    numFriends = (int)dataSnapshot.getChildrenCount();
                    myTotalFriends.setText(Integer.toString(numFriends)+" Friends");

                }
                else {
                    myTotalFriends.setText("0 Friend");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postRef.orderByChild("uid").startAt(currentUserID).endAt(currentUserID+"\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            numPosts = (int)dataSnapshot.getChildrenCount();
                            myTotalPosts.setText(Integer.toString(numPosts)+" Posts");
                        }
                        else {
                            myTotalPosts.setText("0 Posts");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
