package com.student.topquiz.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.student.topquiz.R;

public class ShareActivity extends AppCompatActivity  implements View.OnClickListener{

    private ImageView facebookIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        facebookIcon = findViewById(R.id.facebook);
        facebookIcon.setOnClickListener((View.OnClickListener) this);

    }

    @Override
    public void onClick(View v){

        if (v == facebookIcon){
            Uri imageUri = Uri.parse("");
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.whatsapp");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "My sample image text");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("image/jpeg");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                //ToastHelper.MakeShortText("Kindly install whatsapp first");
            }
        }
    }

}