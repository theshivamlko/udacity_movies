package com.navoki.megamovies.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.navoki.megamovies.R;
import com.navoki.megamovies.Utils.Util;

public class SplashScreen extends AppCompatActivity {

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        context = this;
        TextView splashTitle = findViewById(R.id.splashTitle);
        Typeface typeface = Typeface.createFromAsset(getAssets(), getString(R.string.fontPath));
        splashTitle.setTypeface(typeface);
        splashTitle.animate().alpha(1).setDuration(1000);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, MovieListActivity.class);
                Util.finishEntryAnimation(context, intent);
            }
        }, 3000);
    }
}
