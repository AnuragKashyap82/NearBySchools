package com.anurag.edusearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.anurag.edusearch.databinding.ActivityAboutUsBinding;

public class AboutUsActivity extends AppCompatActivity {

    ActivityAboutUsBinding  binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] addresses = {"anuragkashyap0802@gmail.com"};
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("gmail/*");
                intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                intent.putExtra(Intent.EXTRA_SUBJECT, "EduSearch");
                startActivity(intent);
            }
        });
        binding.facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://www.facebook.com/Anurag815");
            }
        });
        binding.instagramBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://instagram.com/mukesh_kr_dangi?utm_medium=copy_link");
            }
        });
        binding.twitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://instagram.com/anurag_kashyap_82?utm_medium=copy_link");
            }
        });
        binding.websiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://www.google.co.in/");
            }
        });
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}