package com.university.univation;

/**
 * Created by Regis on 05/28/2017.
 */
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.Window;

import java.util.Random;

public class splash extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Random random=new Random();
        int out= random.nextInt(2000);
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    Intent i =new Intent(splash.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
            },out);

     //   startService(new Intent(getBaseContext(),notification.class));

    }
}
