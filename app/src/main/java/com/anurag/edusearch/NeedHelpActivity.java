package com.anurag.edusearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.anurag.edusearch.databinding.ActivityNeedHelpAvtivityBinding;

public class NeedHelpActivity extends AppCompatActivity {

    ActivityNeedHelpAvtivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNeedHelpAvtivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.sendMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        String problemDetails;

        problemDetails = binding.problemEt.getText().toString().trim();

        if (TextUtils.isEmpty(problemDetails)) {
            Toast.makeText(this, "Describe Yours Problem....", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            String[] addresses = {"anuragkashyap0802@gmail.com"};
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("gmail/*");
            intent.putExtra(Intent.EXTRA_EMAIL, addresses);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Problem related SignIn/SignUp");
            intent.putExtra(Intent.EXTRA_TEXT, problemDetails);

            if (intent.resolveActivity(NeedHelpActivity.this.getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
}