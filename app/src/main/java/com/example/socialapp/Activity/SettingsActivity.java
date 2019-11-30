package com.example.socialapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    private CircularImageView profileImage;
    private Button updateBTN;
    private EditText userStatus, userFullName, user_name, userCountry, userGender, userBirthDate, userRelationshipStatus;
    private Toolbar mToolBar;
    private ProgressDialog loadingBar;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private String currentUserID, image;
    private Uri imageUri=null;
    private String postRandomKey, downloadImageUrl, currentDate, currentTime;
    private StorageReference postImageRef;
    private TextView changeProfilePictureBTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();


        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Update profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });

        changeProfilePictureBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfileImage();
            }
        });


        updateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                updateuserAllInformation();

            }
        });

        retreiveUserInformation();
    }

    private void updateuserAllInformation()

    {
        String status = userStatus.getText().toString();
        String username =user_name.getText().toString();
        String name = userFullName.getText().toString();
        String county = userCountry.getText().toString();
        String birthDate = userBirthDate.getText().toString();
        String gender = userGender.getText().toString();
        String relationship = userRelationshipStatus.getText().toString();

        if (TextUtils.isEmpty(status))
        {
            Toast.makeText(this, "Please Enter Profile Status", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Please Enter UserName", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please Enter Profile Name", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(county))
        {
            Toast.makeText(this, "Please Enter Country Name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(birthDate))
        {
            Toast.makeText(this, "Please Enter Birth Date", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(relationship))
        {
            Toast.makeText(this, "Please Enter Your Relationship Status", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(gender))
        {
            Toast.makeText(this, "Please Enter Your Gender", Toast.LENGTH_SHORT).show();
        }

        else {
            loadingBar.setTitle("Update Profile Information");
            loadingBar.setMessage("Updating Profile Information,Please Wait...");
            loadingBar.show();

            HashMap<String,Object> userMap = new HashMap<>();

            userMap.put("status",status);
            userMap.put("username",username);
            userMap.put("userFullName",name);
            userMap.put("userCountry",county);
            userMap.put("gender",gender);
            userMap.put("relationship",relationship);
            userMap.put("birthDate",birthDate);

            userRef.child(currentUserID).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        loadingBar.dismiss();
                        Toast.makeText(SettingsActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SettingsActivity.this,MainActivity.class));
                    }
                    else {
                        loadingBar.dismiss();
                        Toast.makeText(SettingsActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }


    private void openGallery() {
        CropImage.activity(imageUri)
                .setAspectRatio(150, 100)
                .start(SettingsActivity.this);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK && data != null) {
                imageUri = result.getUri();

                profileImage.setImageURI(imageUri);
                changeProfilePictureBTN.setText("Confirm Change");
                changeProfilePictureBTN.setEnabled(true);
            }

        }
    }


    private void updateUserProfileImage()
    {
        if (imageUri==null)
        {
            Toast.makeText(this, "Select Image First", Toast.LENGTH_SHORT).show();
        }
        else {

            loadingBar.setTitle("Update profile Picture:");
            loadingBar.setMessage("Updating Profile Image,Please wait...");
            loadingBar.show();

            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat currentDateForMate = new SimpleDateFormat("MMM dd,yyyy");

            currentDate = currentDateForMate.format(calendar.getTime());


            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm:ss a");
            currentTime = currentTimeFormat.format(calendar.getTime());


            postRandomKey = currentDate + currentTime;

            final StorageReference filepath = postImageRef.child(imageUri.getLastPathSegment() + postRandomKey);


            final UploadTask uploadTask = filepath.putFile(imageUri);


            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SettingsActivity.this, "Error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SettingsActivity.this, "Image Uploaded Successfully..", Toast.LENGTH_SHORT).show();

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                loadingBar.dismiss();
                                throw task.getException();
                            }

                            downloadImageUrl = filepath.getDownloadUrl().toString();


                            return filepath.getDownloadUrl();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {

                                downloadImageUrl = task.getResult().toString();
                                Toast.makeText(SettingsActivity.this, "Image URL successfully Downloaded.", Toast.LENGTH_SHORT).show();
                                changeProfilePictureBTN.setText("Changed Successfully");

                                saveProductInfoTODatabase();
                            }

                        }
                    });
                }

            });


        }



    }


    private void saveProductInfoTODatabase() {


        userRef.child(currentUserID).child("imageUrl").setValue(downloadImageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    loadingBar.dismiss();
                    changeProfilePictureBTN.setEnabled(false);

                    Toast.makeText(SettingsActivity.this, "Save To Database Successfully", Toast.LENGTH_SHORT).show();

                    //startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                }

            }
        });

    }


    private void retreiveUserInformation() {
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("imageUrl")) {
                        image = dataSnapshot.child("imageUrl").getValue().toString();
                        Picasso.get().load(image).resize(200,200).into(profileImage);
                    }

                    if (dataSnapshot.hasChild("username")) {
                        String username = dataSnapshot.child("username").getValue().toString();
                        user_name.setText(username);
                    }

                    if (dataSnapshot.hasChild("userFullName")) {
                        String name = dataSnapshot.child("userFullName").getValue().toString();
                        userFullName.setText(name);
                    }

                    if (dataSnapshot.hasChild("userCountry")) {
                        String country = dataSnapshot.child("userCountry").getValue().toString();
                        userCountry.setText(country);
                    }

                    if (dataSnapshot.hasChild("birthDate")) {

                        String birthDate = dataSnapshot.child("birthDate").getValue().toString();
                        if (birthDate.equals("none")) {
                            userBirthDate.setHintTextColor(Color.RED);

                        } else {
                            userBirthDate.setText(birthDate);
                        }

                    }

                    if (dataSnapshot.hasChild("gender")) {

                        String gender = dataSnapshot.child("gender").getValue().toString();
                        if (gender.equals("none")) {
                            userGender.setHintTextColor(Color.RED);

                        } else {
                            userGender.setText(gender);
                        }

                    }

                    if (dataSnapshot.hasChild("status")) {

                        String status = dataSnapshot.child("status").getValue().toString();
                        if (status.equals("none")) {
                            userStatus.setHintTextColor(Color.RED);
                        } else {
                            userStatus.setText(status);
                        }

                    }


                    if (dataSnapshot.hasChild("relationship")) {

                        String relationship = dataSnapshot.child("relationship").getValue().toString();
                        if (relationship.equals("none")) {
                            userRelationshipStatus.setHintTextColor(Color.RED);
                        } else {
                            userRelationshipStatus.setText(relationship);
                        }

                    }
                } else {
                    Toast.makeText(SettingsActivity.this, "User Not Exists", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void init() {
        mToolBar = findViewById(R.id.settings_page_app_bar);
        profileImage = findViewById(R.id.settings_activity_profile_image);
        userStatus = findViewById(R.id.settings_activity_profile_status);
        userFullName = findViewById(R.id.settings_activity_profile_name);
        user_name = findViewById(R.id.settings_activity_profile_user_name);
        userGender = findViewById(R.id.settings_activity_profile_gender);
        userBirthDate = findViewById(R.id.settings_activity_profile_birthDate);
        userRelationshipStatus = findViewById(R.id.settings_activity_profile_relationship_status);
        userCountry = findViewById(R.id.settings_activity_profile_country);
        updateBTN = findViewById(R.id.settings_activity_update_button);
        changeProfilePictureBTN = findViewById(R.id.setting_change_profile_Picture_BTN);
        loadingBar = new ProgressDialog(this);


        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();


        currentUserID = mAuth.getCurrentUser().getUid();
        postImageRef = FirebaseStorage.getInstance().getReference().child("Posts Image");
    }
}
