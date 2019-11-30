package com.example.socialapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.socialapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FriendsProfileActivity extends AppCompatActivity {

    private CircularImageView profileImage;
    private TextView userFullName, userStatus, userGender, userRelationship, userCountry;
    private DatabaseReference userRef,requestRef,friendsRef;
    private FirebaseAuth mAuth;
    private String senderUserId, receiverUserID;
    private Toolbar mToolbar;
    private Button sendRequestBTN, declineRequestBTN;

    private String CURRENT_STATE,currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_profile);


        receiverUserID = getIntent().getExtras().get("receiverUserId").toString();


        init();


        retrieveProfileInformation();

        declineRequestBTN.setVisibility(View.GONE);
        declineRequestBTN.setEnabled(false);


        if (!senderUserId.equals(receiverUserID))
        {

           sendRequestBTN.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   sendRequestBTN.setEnabled(false);

                   if (CURRENT_STATE.equals("not_friend"))
                   {
                       sendFriendRequest();
                   }
                   if (CURRENT_STATE.equals("request_sent"))
                   {
                       cancelFriendRequest();
                   }
                   if (CURRENT_STATE.equals("request_received"))
                   {
                       acceptFriendRequest();
                   }
                   if (CURRENT_STATE.equals("friend"))
                   {
                       deleteFriendRequest();
                   }

               }
           });
        }
        else {

            sendRequestBTN.setVisibility(View.INVISIBLE);
            declineRequestBTN.setVisibility(View.INVISIBLE);
        }


    }




    private void init() {
        profileImage = findViewById(R.id._friends_profile_activity_profile_image);
        userFullName = findViewById(R.id.friends_profile_userFullName);
        userGender = findViewById(R.id.friends_profile_userGender);
        userCountry = findViewById(R.id.friends_profile_userCountry);
        userRelationship = findViewById(R.id.friends_profile_userRelationship);
        userStatus = findViewById(R.id.friends_profile_userStatus);
        mToolbar = findViewById(R.id.friends_profile_page_app_bar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        sendRequestBTN = findViewById(R.id.friends_profile_accept_Button);
        declineRequestBTN = findViewById(R.id.friends_profile_decline_button);


        mAuth = FirebaseAuth.getInstance();

        senderUserId = mAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        requestRef = FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        CURRENT_STATE = "not_friend";
    }


    private void retrieveProfileInformation() {
        userRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("imageUrl")) {
                        String image = dataSnapshot.child("imageUrl").getValue().toString();
                        Picasso.get().load(image).resize(200, 200).into(profileImage);
                    }

                    if (dataSnapshot.hasChild("userFullName")) {
                        String name = dataSnapshot.child("userFullName").getValue().toString();
                        userFullName.setText(name);
                        getSupportActionBar().setTitle(name);
                    }

                    if (dataSnapshot.hasChild("status")) {
                        String status = dataSnapshot.child("status").getValue().toString();
                        userStatus.setText(status);
                    }

                    if (dataSnapshot.hasChild("gender")) {
                        String gender = dataSnapshot.child("gender").getValue().toString();
                        userGender.setText(gender);
                    }

                    if (dataSnapshot.hasChild("userCountry")) {
                        String country = dataSnapshot.child("userCountry").getValue().toString();
                        userCountry.setText(country);
                    }

                    if (dataSnapshot.hasChild("relationship")) {
                        String relationship = dataSnapshot.child("relationship").getValue().toString();
                        userRelationship.setText(relationship);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        maintainTheButtons();

    }

    private void maintainTheButtons()

    {
        requestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild(receiverUserID))
                        {
                            String type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                            if (type.equals("sent"))
                            {
                                sendRequestBTN.setEnabled(true);
                                sendRequestBTN.setText("Cancel Request");
                                CURRENT_STATE ="request_sent";
                                declineRequestBTN.setVisibility(View.GONE);
                                declineRequestBTN.setEnabled(false);

                            }
                            else if (type.equals("received"))
                            {
                                sendRequestBTN.setEnabled(true);
                                sendRequestBTN.setText("Accept Request");
                                CURRENT_STATE ="request_received";
                                declineRequestBTN.setVisibility(View.VISIBLE);
                                declineRequestBTN.setEnabled(true);

                                declineRequestBTN.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                       cancelFriendRequest();
                                    }
                                });

                            }
                        }
                        else
                        {
                            friendsRef.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if (dataSnapshot.hasChild(receiverUserID))
                                    {
                                        CURRENT_STATE = "friend";
                                        sendRequestBTN.setText("Remove This Person");
                                        declineRequestBTN.setVisibility(View.GONE);
                                        declineRequestBTN.setEnabled(false);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void sendFriendRequest()


    {
        requestRef.child(senderUserId).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            requestRef.child(receiverUserID).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendRequestBTN.setEnabled(true);
                                                sendRequestBTN.setText("Cancel Request");
                                                CURRENT_STATE ="request_sent";
                                                declineRequestBTN.setVisibility(View.GONE);
                                                declineRequestBTN.setEnabled(false);
                                            }

                                        }
                                    });

                        }

                    }
                });

    }

    private void cancelFriendRequest()
    {


        requestRef.child(senderUserId).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            requestRef.child(receiverUserID).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendRequestBTN.setEnabled(true);
                                                sendRequestBTN.setText("Send Request");
                                                CURRENT_STATE ="not_friend";
                                                declineRequestBTN.setVisibility(View.GONE);
                                                declineRequestBTN.setEnabled(false);
                                            }

                                        }
                                    });

                        }

                    }
                });

    }

    private void acceptFriendRequest()

    {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDateForMate = new SimpleDateFormat("MMM dd,yyyy");

        currentDate= currentDateForMate.format(calendar.getTime());

        friendsRef.child(senderUserId).child(receiverUserID).child("date")
                .setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    friendsRef.child(receiverUserID).child(senderUserId).child("date")
                            .setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                requestRef.child(senderUserId).child(receiverUserID)
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {
                                                    requestRef.child(receiverUserID).child(senderUserId)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        sendRequestBTN.setEnabled(true);
                                                                        sendRequestBTN.setText("Remove This Person");
                                                                        CURRENT_STATE ="friend";
                                                                        declineRequestBTN.setVisibility(View.GONE);
                                                                        declineRequestBTN.setEnabled(false);
                                                                    }

                                                                }
                                                            });

                                                }

                                            }
                                        });


                            }

                        }
                    });
                }

            }
        });

    }

    private void deleteFriendRequest()

    {

        friendsRef.child(senderUserId).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                           friendsRef.child(receiverUserID).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendRequestBTN.setEnabled(true);
                                                sendRequestBTN.setText("Send Request");
                                                CURRENT_STATE ="not_friend";
                                                declineRequestBTN.setVisibility(View.GONE);
                                                declineRequestBTN.setEnabled(false);
                                            }

                                        }
                                    });

                        }

                    }
                });

    }



}
