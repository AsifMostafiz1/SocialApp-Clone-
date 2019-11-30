package com.example.socialapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socialapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgottenPasswordActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText inputEmail;
    private Button GoBTN;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);

        mToolbar = findViewById(R.id.forgottenPassword_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        inputEmail = findViewById(R.id.forgottenPasswordInputEmailET);
        GoBTN = findViewById(R.id.forgottenPasswordGoBTN);


        GoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email))
                {
                   inputEmail.setError("Enter Email");
                }
                else {

                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                startActivity(new Intent(ForgottenPasswordActivity.this,LoginActivity.class));
                            }
                            else {
                                Toast.makeText(ForgottenPasswordActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });
    }
}
