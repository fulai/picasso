package com.example.picasso;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private ImageView picImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Picasso.with().setLoggingEnabled(true);
        Picasso.with().setIndicatorsEnabled(true);
        picImg = (ImageView) findViewById(R.id.pic_img);
    }

    public void onClick(View view) {
        /*Intent intent = new Intent(this, SampleGridViewActivity.class);
        startActivity(intent);*/
        String url = "http://i.imgur.com/CqmBjo5.jpg";
        Picasso.with()
                .load(url);
    }

    public void onEnter(View view) {
//        Intent intent = new Intent(this, SampleGridViewActivity.class);
//        startActivity(intent);
        String url = "http://i.imgur.com/CqmBjo5.jpg";
        ImageView imageView = (ImageView) findViewById(R.id.pic_img);
        Picasso.with() //
                .load(url) //
                .placeholder(R.drawable.placeholder) //
                .error(R.drawable.error) //
                .fit() //
                .tag(this) //
                .into(picImg);
    }
}
