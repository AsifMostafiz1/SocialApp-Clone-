package com.example.socialapp.Activity;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialapp.Model.Posts;
import com.example.socialapp.R;
import com.example.socialapp.viewHolder.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private DatabaseReference userRef;

    private CircularImageView profileImage;
    private TextView userName;
    private String currentUserID;
    private ImageView addNewPostImageBTN;

    private FirebaseAuth mAuth;
    private DatabaseReference postRef, likesRef;
    private ProgressBar progressBar;
    String likeStatus ="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");


        mToolbar = findViewById(R.id.main_page_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");


        drawerLayout = findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        navigationView = findViewById(R.id.navigationViewID);
        recyclerView = findViewById(R.id.all_user_post_recyclerView);
        recyclerView.hasFixedSize();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        View navView = navigationView.inflateHeaderView(R.layout.nevagation_header);

        profileImage = navView.findViewById(R.id.nav_profile_image);
        userName = navView.findViewById(R.id.nav_user_name);
        addNewPostImageBTN = findViewById(R.id.add_new_post_ImageView);
        progressBar = findViewById(R.id.mainActivityProgressBar);


        addNewPostImageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentuserAddNewPostActivity();
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_home:
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.menu_profile:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));

                        break;

                    case R.id.menu_new_post:
                        sentuserAddNewPostActivity();
                        break;

                    case R.id.menu_settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;

                    case R.id.menu_logout:
                        userCurrentState("offline");
                        mAuth.signOut();
                        sentUserLoginActivity();
                        break;

                    case R.id.menu_messages:
                        startActivity(new Intent(MainActivity.this,MessageActivity.class));
                        break;

                    case R.id.menu_friend:
                        startActivity(new Intent(MainActivity.this,FriendsActivity.class));
                        break;

                    case R.id.menu_find_friend:
                        startActivity(new Intent(MainActivity.this, FindFriendActivity.class));
                        break;


                }
                return false;
            }
        });
        retreiveAllPost();


    }

    private void retreiveAllPost() {
        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(postRef, Posts.class)
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
                        Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                        intent.putExtra("postKey",postKey);
                        startActivity(intent);
                    }
                });
                postViewHolder.commentBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
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



                progressBar.setVisibility(View.GONE);


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

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

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

                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.all_post_item_show, parent, false);
                PostViewHolder postViewHolder = new PostViewHolder(view);
                return postViewHolder;
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        userCurrentState("online");


    }


    //Remove Specific Post By Own User.Not Other Users

    private void removePost(String postKey) {
        postRef.child(postKey).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        userCurrentState("online");


        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser == null) {
            sentUserLoginActivity();
        } else {

            final String currentUserId = mAuth.getCurrentUser().getUid();

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(currentUserId)) {
                        sendUserToSetupActivity();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        currentUserID = mAuth.getCurrentUser().getUid();

        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("imageUrl")) {
                        String image = dataSnapshot.child("imageUrl").getValue().toString();
                        Picasso.get().load(image).resize(200, 200).into(profileImage);
                    }

                    if (dataSnapshot.hasChild("userFullName")) {
                        String name = dataSnapshot.child("userFullName").getValue().toString();

                        userName.setText(name);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Information Not Found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






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


    private void sendUserToSetupActivity() {
        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sentUserLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sentuserAddNewPostActivity() {
        Intent intent = new Intent(MainActivity.this, AddNewPostActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)
        ) {

            return true;
        }
        return super.onOptionsItemSelected(item);
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
