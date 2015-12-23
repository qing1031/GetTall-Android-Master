package com.obenproto.howtall.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.obenproto.howtall.R;

public class IntroActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);

        final TextView recort_voice = (TextView)findViewById(R.id.startRecordBtn);
        recort_voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    recort_voice.setBackgroundResource(R.drawable.start_btn_pressed);
                    recort_voice.setTextColor(Color.WHITE);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    recort_voice.setBackgroundResource(R.drawable.start_btn_normal);
                    recort_voice.setTextColor(Color.parseColor("#878D7E"));

                    startActivity(new Intent(IntroActivity.this, RecordActivity.class));
                    finish();
                }

                return true;
            }
        });
    }
}