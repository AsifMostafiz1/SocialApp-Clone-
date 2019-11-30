package com.example.socialapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.socialapp.Model.Comments;
import com.example.socialapp.R;
import com.example.socialapp.viewHolder.CommentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class CommentsActivity extends AppCompatActivity {
    private  String postKey,currentUserID,randomKey;
    private RecyclerView commentRecyclerView;
    private EditText inputComment;
    private ImageView sendBTN;
    private Toolbar mToolbar;
    private DatabaseReference userRef,postRef;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        postKey = getIntent().getExtras().get("postKey").toString();
        commentRecyclerView = findViewById(R.id.commentRecyclerViewID);
        commentRecyclerView.hasFixedSize();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentRecyclerView.setLayoutManager(linearLayoutManager);

        inputComment = findViewById(R.id.commentsInputStringID);
        sendBTN = findViewById(R.id.commentSentBTN);
        mToolbar = findViewById(R.id.comment_activity_appBar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("comments");


        sendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String comment = inputComment.getText().toString().trim();

                if (!TextUtils.isEmpty(comment))
                {
                    validateCommentInformation(comment);
                    inputComment.setText("");
                }
                else {
                    inputComment.setError("Write a comment First");
                }

            }
        });

    }




    private void validateCommentInformation(final String comment)

    {
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("userFullName")&&dataSnapshot.hasChild("imageUrl"))
                    {
                        String userFullName = dataSnapshot.child("userFullName").getValue().toString();
                        String userProfileImage = dataSnapshot.child("imageUrl").getValue().toString();


                        Calendar calendar = Calendar.getInstance();

                        SimpleDateFormat currentDateForMate = new SimpleDateFormat("MMM dd,yyyy");

                        String currentDate= currentDateForMate.format(calendar.getTime());

                        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm:ss a");
                        String currentTime = currentTimeFormat.format(calendar.getTime());

                         randomKey = currentUserID+currentDate+currentTime;


                        HashMap<String,Object> commentMap = new HashMap<>();

                        commentMap.put("userFullName",userFullName);
                        commentMap.put("userProfileImage",userProfileImage);
                        commentMap.put("currentDate",currentDate);
                        commentMap.put("currentTime",currentTime);
                        commentMap.put("uid",currentUserID);
                        commentMap.put("comment",comment);

                        postRef.child(randomKey).updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(CommentsActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                                }

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

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Comments> options = new FirebaseRecyclerOptions.Builder<Comments>()
                .setQuery(postRef, Comments.class)
                .build();

        FirebaseRecyclerAdapter<Comments, CommentViewHolder> adapter = new FirebaseRecyclerAdapter<Comments, CommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i, @NonNull Comments comments)
            {
                Picasso.get().load(comments.getUserProfileImage()).resize(100,100).into(commentViewHolder.profileImage);
                commentViewHolder.userName.setText(comments.getUserFullName());
                commentViewHolder.comment.setText(comments.getComment());
                commentViewHolder.date.setText("Date: "+comments.getCurrentDate());
                commentViewHolder.time.setText("Time: "+comments.getCurrentTime());
            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(CommentsActivity.this).inflate(R.layout.display_all_comment_layout,parent,false);
                CommentViewHolder commentViewHolder = new CommentViewHolder(view);
                return commentViewHolder;
            }
        };
        commentRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
