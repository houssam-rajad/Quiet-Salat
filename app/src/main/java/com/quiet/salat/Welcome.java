package com.quiet.salat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity {
    Animation animator;
    ImageView logoview,loadicon;
    TextView salamtext,quietsalattext,thank,sharetext;
    Button next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        animator= AnimationUtils.loadAnimation(this,R.anim.descent_animation);
        logoview=findViewById(R.id.logo);
        logoview.setAnimation(animator);
        salamtext=findViewById(R.id.salam);
        thank=findViewById(R.id.thank);
        loadicon=findViewById(R.id.loadicon);
        quietsalattext=findViewById(R.id.quietext);
        sharetext=findViewById(R.id.sharetext);
        animator=AnimationUtils.loadAnimation(this,R.anim.translate_animation);
        next=findViewById(R.id.welcome_button);
        next.setAnimation(animator);
        animator=AnimationUtils.loadAnimation(this,R.anim.text_anim);
        salamtext.setAnimation(animator);
        quietsalattext.setAnimation(animator);
        thank.setAnimation(AnimationUtils.loadAnimation(this,R.anim.sample_anim));
        sharetext.setAnimation(AnimationUtils.loadAnimation(this,R.anim.sample_anim));
        loadicon.setAlpha(0f);





    }
    public void sendMessage(View view)
    {
        final Intent pass=new Intent(this,MainActivity.class);
        loadicon.setAlpha(1f);
        loadicon.startAnimation(AnimationUtils.loadAnimation(this,R.anim.loading));
        Handler handler=new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(pass);
            }
        },1500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadicon.clearAnimation();
        loadicon.setAlpha(0f);
    }
}
