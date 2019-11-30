package com.example.socialapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialapp.Model.Messages;
import com.example.socialapp.R;
import com.example.socialapp.viewHolder.MessagesAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView userFullNameTV,lastSeenMessageTV;
    private CircularImageView userProfileImage;
    private RecyclerView recyclerView;
    private ImageView sendFileBTN,sendTextMessageBTN;
    private EditText inputMessageET;
    private DatabaseReference messageRef,userRef;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private List<Messages> messageList = new ArrayList<>();
    private MessagesAdapter messagesAdapter;


    private String receiverUserId,senderUserId,userFullName,profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initialization();

        receiverUserId = getIntent().getExtras().get("receiverUserId").toString();
        userFullName = getIntent().getExtras().get("userFullName").toString();
        profileImage = getIntent().getExtras().get("userProfileImage").toString();

        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");






        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        displayUserInformation();


        sendTextMessageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
              saveMessageIntoDatabase();
              inputMessageET.setText("");
              userCurrentState("online");
            }
        });

    }

    private void displayUserInformation()

    {

        userFullNameTV.setText(userFullName);
        Picasso.get().load(profileImage).into(userProfileImage);

        userRef.child(receiverUserId).child("userState").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String state = dataSnapshot.child("state").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String time = dataSnapshot.child("time").getValue().toString();

                    if (state.equals("online"))
                    {
                        lastSeenMessageTV.setText("online");
                    }
                    else {
                        lastSeenMessageTV.setText("Last Seen: "+time+date);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void saveMessageIntoDatabase()
    {
        String message = inputMessageET.getText().toString().trim();

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Write A message first.", Toast.LENGTH_SHORT).show();
        }
        else {


            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat currentDateForMate = new SimpleDateFormat("MMM dd,yyyy");

            String currentDate= currentDateForMate.format(calendar.getTime());


            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm:ss a");
            String currentTime = currentTimeFormat.format(calendar.getTime());

            String sender_user_Ref = "Messages/"+senderUserId+"/"+receiverUserId;
            String receiver_user_Ref = "Messages/"+receiverUserId+"/"+senderUserId;

            DatabaseReference unique_message_key =messageRef.child(senderUserId)
                    .child(receiverUserId).push();
            String unique_message_id = unique_message_key.getKey();

            Map messageTextBody = new HashMap();

            messageTextBody.put("message",message);
            messageTextBody.put("time",currentTime);
            messageTextBody.put("date",currentDate);
            messageTextBody.put("type","text");
            messageTextBody.put("from",senderUserId);

            Map messageBodyDetails = new HashMap();

            messageBodyDetails.put(sender_user_Ref+"/"+unique_message_id,messageTextBody);
            messageBodyDetails.put(receiver_user_Ref+"/"+unique_message_id,messageTextBody);

            messageRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(MessageActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MessageActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        rootRef.child("Messages").child(senderUserId).child(receiverUserId).
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        Messages messages = dataSnapshot.getValue(Messages.class);


                        messageList.add(messages);
                        messagesAdapter.notifyDataSetChanged();

                       recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void initialization()

    {
        mToolbar = findViewById(R.id.messageActivityToolBar);
        userFullNameTV = findViewById(R.id.messageActivityUserProfileName);
        lastSeenMessageTV = findViewById(R.id.messageActivityLastSeenMessage);
        userProfileImage = findViewById(R.id.messageActivityUserProfileImage);
        sendFileBTN = findViewById(R.id.messageActivityCameraImageView);
        sendTextMessageBTN = findViewById(R.id.messageActivityMessageSendBTN);
        inputMessageET = findViewById(R.id.messageActivityInputEditText);
        recyclerView = findViewById(R.id.messageActivityRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();
        messagesAdapter = new MessagesAdapter(messageList);
        recyclerView.setAdapter(messagesAdapter);



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

        userRef.child(senderUserId).child("userState")
                .updateChildren(currentStateMap);

    }


}
