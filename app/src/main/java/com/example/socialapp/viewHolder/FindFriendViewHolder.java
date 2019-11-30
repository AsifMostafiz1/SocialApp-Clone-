package com.example.socialapp.viewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.socialapp.R;
import com.example.socialapp.interfaces.ItemClickListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FindFriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public ItemClickListener listener;
    public CircularImageView userProfileImage;
    public TextView userFullName,userStatus;
    public FindFriendViewHolder(@NonNull View itemView)
    {
        super(itemView);

        userProfileImage = itemView.findViewById(R.id.findFriendUserImage);
        userFullName = itemView.findViewById(R.id.findFriendUserName);
        userStatus = itemView.findViewById(R.id.findFriendUserStatus);
    }

    @Override
    public void onClick(View v)
    {
        listener.onClick(v,getAdapterPosition(),false);

    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }
}
