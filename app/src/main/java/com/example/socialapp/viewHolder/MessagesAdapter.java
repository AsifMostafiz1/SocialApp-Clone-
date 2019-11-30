package com.example.socialapp.viewHolder;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialapp.Model.Messages;
import com.example.socialapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<Messages> messagesList = new ArrayList<>();
    private FirebaseAuth mAuth;

    private DatabaseReference userRef;

    public MessagesAdapter(List<Messages> messages) {
        this.messagesList = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout,parent,false);


       mAuth = FirebaseAuth.getInstance();

       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String senderMessagesID = mAuth.getCurrentUser().getUid();
        Messages currentMessages = messagesList.get(position);

        String fromUserID =currentMessages.getFrom();
        String fromMEssageType = currentMessages.getType();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild("imageUrl"))
                {
                    String receiverImage = dataSnapshot.child("imageUrl").getValue().toString();

                    Picasso.get().load(receiverImage).into(holder.messageProfileImage);
                }
                if (dataSnapshot.child("userState").hasChild("state"))
                {
                    String state = dataSnapshot.child("userState").child("state").getValue().toString();

                    if (state.equals("online"))
                    {
                        holder.userOnlineState.setImageResource(R.drawable.ic_online);
                    }
                    else {
                        holder.userOnlineState.setImageResource(R.drawable.ic_offline);

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (fromMEssageType.equals("text"))
        {
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.messageProfileImage.setVisibility(View.INVISIBLE);
            holder.senderMessageText.setVisibility(View.INVISIBLE);

            if (fromUserID.equals(senderMessagesID))
            {
                holder.senderMessageText.setVisibility(View. VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.custom_button1);
                holder.senderMessageText.setText(currentMessages.getMessage());
                holder.senderMessageText.setTextSize(18);
                holder.senderMessageText.setTextColor(Color.BLACK);
            }

            else {


                holder.messageProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setBackgroundResource(R.drawable.custom_edit_text);
                holder.receiverMessageText.setText(currentMessages.getMessage());

                holder.receiverMessageText.setTextSize(18);
                holder.receiverMessageText.setTextColor(Color.BLACK);

            }
        }


    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
         TextView receiverMessageText,senderMessageText;
         CircularImageView messageProfileImage;
         ImageView userOnlineState;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMessageText = itemView.findViewById(R.id.message_receiver_text_message);
            senderMessageText = itemView.findViewById(R.id.message_sender_text_message);
            messageProfileImage = itemView.findViewById(R.id.message_user_profile_image);
            userOnlineState = itemView.findViewById(R.id.message_online_offLine_State);
        }
    }
}
