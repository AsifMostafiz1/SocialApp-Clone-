package com.example.socialapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class SetupActivity extends AppCompatActivity {

    private CircularImageView profileImage;
    private EditText userName,userFullName,userCountry;
    private Button saveBTN;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference profileImageStorageRef;
    private String currentUserID;
    private ProgressDialog loadingBar;
    private StorageTask uploadTask;
    private String myURL;

    private TextView changeProfileImageTV;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        profileImageStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Image");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        loadingBar = new ProgressDialog(this);


        profileImage = findViewById(R.id.setupProfileImage);
        userName = findViewById(R.id.setupUserName);
        userFullName = findViewById(R.id.setupUserFullName);
        userCountry = findViewById(R.id.setupUserCountry);
        saveBTN = findViewById(R.id.setupSaveBTN);
        changeProfileImageTV = findViewById(R.id.update_profile_imageTV);


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                    changeProfileImage();

            }
        });
        changeProfileImageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                changeProfileImage();
            }
        });

        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                saveInformationTODataBase();

            }
        });

        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild("imageUrl"))
                {
                   String imageUrl = dataSnapshot.child("imageUrl").getValue().toString();

                    Picasso.get().load(imageUrl).into(profileImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void changeProfileImage()
    {
        CropImage.activity(imageUri)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode==RESULT_OK && data!=null)
            {
                imageUri =result.getUri();

                profileImage.setImageURI(imageUri);
            }
            uploadImageStorage();
        }
    }



    private void saveInformationTODataBase()
    {
        String user_name = userName.getText().toString();
        String user_full_name = userFullName.getText().toString();
        String user_country = userCountry.getText().toString();


        if (TextUtils.isEmpty(user_name))
        {
            Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(user_full_name))
        {
            Toast.makeText(this, "Enter User Full Name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(user_country))
        {
            Toast.makeText(this, "Enter Country", Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle("Set up Information:");
            loadingBar.setMessage("Your Information is being Saved Please wait...");
            loadingBar.show();



                HashMap<String ,Object> userMap = new HashMap<>();

                userMap.put("username",user_name);
                userMap.put("userFullName",user_full_name);
                userMap.put("userCountry",user_country);
                userMap.put("birthDate","none");
                userMap.put("meritStatus","none");
                userMap.put("relationship","none");
                userMap.put("gender","none");
                userMap.put("status","none");

                userRef.child(currentUserID).updateChildren(userMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(SetupActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                    sendUserMainActivity();
                                    loadingBar.dismiss();
                                }

                                else {
                                    Toast.makeText(SetupActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }

                            }
                        });


        }
    }

    private void uploadImageStorage()

    {
        loadingBar.setTitle("Set up Information:");
        loadingBar.setMessage("Your Information is being Saved Please wait...");
        loadingBar.show();
        final StorageReference filepath = profileImageStorageRef.child(currentUserID+".jpg");
        uploadTask =filepath.putFile(imageUri);

        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception
            {
                if (!task.isSuccessful())
                {
                    throw task.getException();
                }
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful())
                {
                    Uri downloadUri = (Uri) task.getResult();
                    myURL = downloadUri.toString();

                    userRef.child(currentUserID).child("imageUrl").setValue(myURL)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        loadingBar.dismiss();
                                        startActivity(new Intent(SetupActivity.this,SetupActivity.class));
                                        profileImage.setImageURI(imageUri);
                                    }

                                }
                            });

                }
            }
        });
    }


    private void sendUserMainActivity()
    {
        Intent intent = new Intent(SetupActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
