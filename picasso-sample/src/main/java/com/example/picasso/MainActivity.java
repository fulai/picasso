package com.example.picasso;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Picasso.with().setLoggingEnabled(true);
        Picasso.with().setIndicatorsEnabled(true);
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
                .into(imageView);
    }
}
