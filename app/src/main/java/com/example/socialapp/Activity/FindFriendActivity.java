package com.example.socialapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.socialapp.Model.Users;
import com.example.socialapp.R;
import com.example.socialapp.viewHolder.FindFriendViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class FindFriendActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private EditText inputSearchET;
    private ImageView searchButton;
    private RecyclerView recyclerView;
    private DatabaseReference userRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        init();


        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Find Friend");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        userRef = FirebaseDatabase.getInstance().getReference().child("Users");


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputString = inputSearchET.getText().toString();

                searchFriend(inputString);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(userRef,Users.class)
                .build();

        FirebaseRecyclerAdapter<Users,FindFriendViewHolder> adapter = new FirebaseRecyclerAdapter<Users, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder findFriendViewHolder, int i, @NonNull Users users)
            {
                findFriendViewHolder.userFullName.setText(users.getUserFullName());
                findFriendViewHolder.userStatus.setText(users.getStatus());
                Picasso.get().load(users.getImageUrl()).resize(100,100).into(findFriendViewHolder.userProfileImage);
                final String receiverUserId = getRef(i).getKey();

                findFriendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(FindFriendActivity.this,FriendsProfileActivity.class);
                        intent.putExtra("receiverUserId",receiverUserId);
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(FindFriendActivity.this).inflate(R.layout.all_find_friend_show_list,parent,false);
                FindFriendViewHolder findFriendViewHolder = new FindFriendViewHolder(view);
                return findFriendViewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    private void searchFriend(final String inputString)

    {
        loadingBar.setMessage("Searching.....");
        loadingBar.show();

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(userRef.orderByChild("userFullName").startAt(inputString),Users.class)
                .build();

        FirebaseRecyclerAdapter<Users,FindFriendViewHolder> adapter = new FirebaseRecyclerAdapter<Users, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder findFriendViewHolder, int i, @NonNull Users users)
            {
                findFriendViewHolder.userFullName.setText(users.getUserFullName());
                findFriendViewHolder.userStatus.setText(users.getStatus());
                Picasso.get().load(users.getImageUrl()).into(findFriendViewHolder.userProfileImage);
                final String receiverUserId = getRef(i).getKey();
                loadingBar.dismiss();

                findFriendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(FindFriendActivity.this,FriendsProfileActivity.class);
                        intent.putExtra("receiverUserId",receiverUserId);
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(FindFriendActivity.this).inflate(R.layout.all_find_friend_show_list,parent,false);
                FindFriendViewHolder findFriendViewHolder = new FindFriendViewHolder(view);
                return findFriendViewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    private void init()

    {
        mToolBar = findViewById(R.id.findFriendActivityAppBar);
        inputSearchET = findViewById(R.id.findFriendEditText);
        searchButton = findViewById(R.id.findFriendSearchButton);
        recyclerView = findViewById(R.id.findFriendRecyclerViewID);
        loadingBar = new ProgressDialog(this);
    }
}
