package com.anurag.edusearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText oldPasswordEt, newPasswordEt, cNewPasswordEt;
    private TextView forgotTv;
    private Button continueBtn, changePassBtn;
    private ProgressDialog progressDialog;
    private String userCurrentPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPasswordEt = findViewById(R.id.oldPasswordEt);
        newPasswordEt = findViewById(R.id.newPasswordEt);
        cNewPasswordEt = findViewById(R.id.cNewPasswordEt);
        forgotTv = findViewById(R.id.forgotTv);
        continueBtn = findViewById(R.id.continueBtn);
        changePassBtn = findViewById(R.id.changePassBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        newPasswordEt.setEnabled(false);
        cNewPasswordEt.setEnabled(false);
        changePassBtn.setEnabled(false);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        forgotTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangePasswordActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        if (firebaseUser.equals(null)){
            Toast.makeText(ChangePasswordActivity.this, "User is Currently Not LoggedIn!!!!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else {
            reAuthenticateUser(firebaseUser);
        }

    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userCurrentPass = oldPasswordEt.getText().toString();

                if (TextUtils.isEmpty(userCurrentPass)){
                    Toast.makeText(ChangePasswordActivity.this, "Enter Your Current Password!!!!", Toast.LENGTH_SHORT).show();
                    oldPasswordEt.setError("Please Enter Your Current Password to Continue");
                    oldPasswordEt.requestFocus();
                } else {
                    progressDialog.setMessage("Verifying Password");
                    progressDialog.show();
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userCurrentPass);
                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();

                                        oldPasswordEt.setEnabled(false);
                                        newPasswordEt.setEnabled(true);
                                        cNewPasswordEt.setEnabled(true);
                                        changePassBtn.setEnabled(true);
                                        continueBtn.setEnabled(false);

                                        Toast.makeText(ChangePasswordActivity.this, "Your Password has been Verified, Now you can change Your Password!!!", Toast.LENGTH_SHORT).show();

                                        changePassBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                changePassword(firebaseUser);
                                            }
                                        });
                                    }else {
                                        progressDialog.dismiss();
                                        try {
                                            throw  task.getException();
                                        } catch (Exception e) {
                                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }
    private void changePassword(FirebaseUser firebaseUser) {

        String newPass = newPasswordEt.getText().toString();
        String cNewPass = cNewPasswordEt.getText().toString();

        if (TextUtils.isEmpty(newPass)){
            Toast.makeText(ChangePasswordActivity.this, "Enter Yor New Password!!!!", Toast.LENGTH_SHORT).show();
            newPasswordEt.setError("Please Enter Your New Password");
            newPasswordEt.requestFocus();
        }else if (TextUtils.isEmpty(cNewPass)){
            Toast.makeText(ChangePasswordActivity.this, "Confirm Yor New Password!!!!", Toast.LENGTH_SHORT).show();
            cNewPasswordEt.setError("Confirm Your New Password");
            cNewPasswordEt.requestFocus();
        }else if (!newPass.matches(cNewPass)){
            Toast.makeText(ChangePasswordActivity.this, "Password Doesn't Matches!!!", Toast.LENGTH_SHORT).show();
            cNewPasswordEt.setError("Confirm  Your New Password");
            cNewPasswordEt.requestFocus();
        }else if (userCurrentPass.matches(newPass)){
            Toast.makeText(ChangePasswordActivity.this, "Your Current Password And New Password Are Same!!!!", Toast.LENGTH_SHORT).show();
            newPasswordEt.setError("Same As Current Password");
            newPasswordEt.requestFocus();
        }else {
            progressDialog.setMessage("Changing Password");
            progressDialog.show();
            firebaseUser.updatePassword(newPass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(ChangePasswordActivity.this, "Password Has been Changed Successfully!!!!", Toast.LENGTH_SHORT).show();
                            }else {
                                progressDialog.dismiss();
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
    }
}