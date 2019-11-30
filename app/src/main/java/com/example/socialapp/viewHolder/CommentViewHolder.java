package com.example.socialapp.viewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.socialapp.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public CircularImageView profileImage;
    public TextView userName,comment,date,time;
    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImage = itemView.findViewById(R.id.comment_user_profile_Image);
        userName = itemView.findViewById(R.id.comment_user_FullName);
        comment = itemView.findViewById(R.id.comment_user_MainComment);
        date = itemView.findViewById(R.id.comment_user_Date);
        time = itemView.findViewById(R.id.comment_user_Time);
    }

    @Override
    public void onClick(View v) {

    }
}
