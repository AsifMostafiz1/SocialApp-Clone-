package com.example.socialapp.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialapp.R;
import com.example.socialapp.interfaces.ItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView postDescription,userName,dateAndTime;
    public ImageView postImage;
    public CircularImageView userProfileImage;
    public ItemClickListener listener;
    public ImageView postEditDeleteMenu;
    public ImageView likePostBTN,commentBTN;
    public TextView numOfLikesTV,numberOfCommentsTV;
    public int countLikes,countComments;
    public String currentUserID;
    public DatabaseReference likesRef,postRef;
    public FirebaseAuth mAuth;


    public PostViewHolder(@NonNull View itemView) {
        super(itemView);

        userProfileImage = itemView.findViewById(R.id.post_user_profile_image);
        userName = itemView.findViewById(R.id.post_user_profile_UserName);
        postDescription = itemView.findViewById(R.id.post_user_profile_Description);
        dateAndTime = itemView.findViewById(R.id.post_user_profile_Time);
        postImage = itemView.findViewById(R.id.post_user_image);
        postEditDeleteMenu = itemView.findViewById(R.id.post_edit_delete_menu);

        likePostBTN = itemView.findViewById(R.id.likePostBTN);
        commentBTN = itemView.findViewById(R.id.commentBTN);
        numOfLikesTV = itemView.findViewById(R.id.numberOfLikePostTextView);
        numberOfCommentsTV = itemView.findViewById(R.id.numberOfCommentTextView);

        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        mAuth = FirebaseAuth.getInstance();

        currentUserID = mAuth.getCurrentUser().getUid();

    }



    @Override
    public void onClick(View v) {

        listener.onClick(v,getAdapterPosition(),false);


    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    public void countNumberOfLike(final String postKey)
    {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(postKey).hasChild(currentUserID))
                {
                    countLikes = (int)dataSnapshot.child(postKey).getChildrenCount();
                    likePostBTN.setImageResource(R.drawable.like);
                    numOfLikesTV.setText(Integer.toString(countLikes)+" Likes");
                }
                else
                {
                    countLikes = (int)dataSnapshot.child(postKey).getChildrenCount();
                    likePostBTN.setImageResource(R.drawable.dislike);
                    numOfLikesTV.setText(Integer.toString(countLikes)+" Likes");

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void countNumberofComment(final String postKey)
    {
        postRef.child(postKey).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                    countComments = (int)dataSnapshot.getChildrenCount();
                    numberOfCommentsTV.setText(countComments+" Comments");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
