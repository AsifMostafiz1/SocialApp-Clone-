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
import android.widget.Toast;

import com.example.socialapp.Model.Posts;
import com.example.socialapp.R;
import com.example.socialapp.viewHolder.PostViewHolder;
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

public class MyAllPostsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView recyclerView;
    private DatabaseReference postRef,likesRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    String likeStatus ="0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_all_posts);


        mToolbar = findViewById(R.id.myAllPostAppBar);
        recyclerView = findViewById(R.id.myAllPostRecyclerView);
        recyclerView.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Posts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");



        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(postRef.orderByChild("uid").startAt(currentUserID).endAt(currentUserID+"\uf8ff"), Posts.class)
                .build();


        FirebaseRecyclerAdapter<Posts, PostViewHolder> adapter = new FirebaseRecyclerAdapter<Posts, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PostViewHolder postViewHolder, int i, @NonNull Posts posts) {

                postViewHolder.userName.setText(posts.getUserName());
                Picasso.get().load(posts.getUserImage()).resize(100,100).into(postViewHolder.userProfileImage);
                Picasso.get().load(posts.getImage()).resize(400, 250).into(postViewHolder.postImage);

                postViewHolder.postDescription.setText(posts.getDescription());
                postViewHolder.dateAndTime.setText(posts.getTime() + "  " + posts.getDate());

                final String uid = posts.getUid();
                final String postKey = getRef(i).getKey();

                postViewHolder.countNumberOfLike(postKey);
                postViewHolder.countNumberofComment(postKey);

                postViewHolder.numberOfCommentsTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(MyAllPostsActivity.this, CommentsActivity.class);
                        intent.putExtra("postKey",postKey);
                        startActivity(intent);
                    }
                });
                postViewHolder.commentBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(MyAllPostsActivity.this, CommentsActivity.class);
                        intent.putExtra("postKey",postKey);
                        startActivity(intent);

                    }
                });


                postViewHolder.likePostBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeStatus = "1";

                        likesRef.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (likeStatus.equals("1"))
                                {
                                    if (dataSnapshot.child(postKey).hasChild(currentUserID)) {
                                        likesRef.child(postKey).child(currentUserID).removeValue();
                                        likeStatus = "0";
                                    } else {
                                        likesRef.child(postKey).child(currentUserID).setValue(true);
                                        likeStatus = "0";
                                    }
                                }




                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });




                if (uid.equals(currentUserID)) {
                    postViewHolder.postEditDeleteMenu.setVisibility(View.VISIBLE);
                } else {
                    postViewHolder.postEditDeleteMenu.setVisibility(View.INVISIBLE);
                }

                postViewHolder.postEditDeleteMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence option[] = new CharSequence[]
                                {
                                        "Edit",
                                        "Delete"

                                };

                        AlertDialog.Builder builder = new AlertDialog.Builder(MyAllPostsActivity.this);

                        builder.setTitle("Choose Your Option");
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {

                                } else if (which == 1) {
                                    removePost(postKey);
                                }

                            }
                        });
                        builder.show();


                    }
                });


            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(MyAllPostsActivity.this).inflate(R.layout.all_post_item_show, parent, false);
                PostViewHolder postViewHolder = new PostViewHolder(view);
                return postViewHolder;
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    private void removePost(String postKey) {
        postRef.child(postKey).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MyAllPostsActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
