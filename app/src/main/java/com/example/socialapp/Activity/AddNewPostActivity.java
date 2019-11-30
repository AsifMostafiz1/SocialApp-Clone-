package com.example.socialapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.socialapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddNewPostActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageView postImage;
    private EditText postDescriptionET;
    private Button saveBTN;
    private Uri imageUri;
    private ProgressDialog loadingBar;

    private StorageReference postImageRef;
    private DatabaseReference postRef,userRef;
    private FirebaseAuth mAuth;
    private String fullName="",profileImage="";

    private String postRandomKey,downloadImageUrl,currentDate,currentTime,postDescription,currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_post);
        loadingBar = new ProgressDialog(this);

        postImageRef= FirebaseStorage.getInstance().getReference().child("Posts Image");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();



        mToolbar = findViewById(R.id.update_new_Post_app_Bar);
        setSupportActionBar(mToolbar);

        postImage = findViewById(R.id.add_new_post_Image);
        postDescriptionET = findViewById(R.id.add_new_post_Image_Description);
        saveBTN = findViewById(R.id.add_new_post_SaveBTN);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update Post");


        reteriveuserInformation();



        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInformationIntoDatabase();
            }
        });
    }

    private void reteriveuserInformation()

    {
      userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot)
          {
              if (dataSnapshot.exists())
              {
                  if (dataSnapshot.hasChild("userFullName"))
                  {
                      fullName = dataSnapshot.child("userFullName").getValue().toString();
                  }
                  else
                  {
                      Toast.makeText(AddNewPostActivity.this, "User Name Not Found", Toast.LENGTH_SHORT).show();
                  }
                  if (dataSnapshot.hasChild("imageUrl"))
                  {
                      profileImage= dataSnapshot.child("imageUrl").getValue().toString();
                  }
                  else
                  {
                      Toast.makeText(AddNewPostActivity.this, "User Image Not Found", Toast.LENGTH_SHORT).show();
                  }
              }
              else
              {
                  Toast.makeText(AddNewPostActivity.this, "User Not Exist", Toast.LENGTH_SHORT).show();
              }

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
    }


    private void saveInformationIntoDatabase()

    {
        postDescription = postDescriptionET.getText().toString();

        if (imageUri==null)
        {
            Toast.makeText(this, "Image is Required", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(postDescription))
        {
            Toast.makeText(this, "Description is Required", Toast.LENGTH_SHORT).show();

        }

        else
        {
            loadingBar.setTitle("Save Information:");
            loadingBar.setMessage("Saving Successfully Please Wait....");
            loadingBar.show();
            savePictureIntoStorage();
        }
    }

    private void savePictureIntoStorage()
    {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDateForMate = new SimpleDateFormat("MMM dd,yyyy");

        currentDate= currentDateForMate.format(calendar.getTime());


        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm:ss a");
        currentTime = currentTimeFormat.format(calendar.getTime());


        postRandomKey = currentDate+currentTime;

        final StorageReference filepath = postImageRef.child(imageUri.getLastPathSegment()+postRandomKey);


        final UploadTask uploadTask = filepath.putFile(imageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddNewPostActivity.this, "Error: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AddNewPostActivity.this, "Image Uploaded Successfully..", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            throw task.getException();
                        }

                        downloadImageUrl = filepath.getDownloadUrl().toString();


                        return filepath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            downloadImageUrl =task.getResult().toString();
                            Toast.makeText(AddNewPostActivity.this, "Image URL successfully Downloaded.", Toast.LENGTH_SHORT).show();

                            saveProductInfoTODatabase();
                        }

                    }
                });
            }

        });



    }





    private void saveProductInfoTODatabase()

    {

        HashMap<String,Object> postMap = new HashMap<>();

        postMap.put("pid",postRandomKey);
        postMap.put("date",currentDate);
        postMap.put("time",currentTime);
        postMap.put("image",downloadImageUrl);
        postMap.put("description",postDescription);
        postMap.put("userName",fullName);
        postMap.put("userImage",profileImage);
        postMap.put("uid",currentUserID);




        postRef.child(postRandomKey+currentUserID).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                if (task.isSuccessful())

                {
                    loadingBar.dismiss();
                    Toast.makeText(AddNewPostActivity.this, "Save To Database Successfully", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(AddNewPostActivity.this, MainActivity.class));
                }

            }
        });

    }







    private void openGallery()
    {
        CropImage.activity(imageUri)
                .setAspectRatio(150,100)
                .start(AddNewPostActivity.this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode==RESULT_OK && data!=null)
            {
                imageUri =result.getUri();

                postImage.setImageURI(imageUri);
            }

        }
    }
}
