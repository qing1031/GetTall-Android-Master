package com.obenproto.howtall.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.obenproto.howtall.R;

public class ConfirmActivity extends Activity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confirm_activity);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // Get the window size.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .95), (int) (height * .92));


        final TextView agree_txt = (TextView) findViewById(R.id.agree_txt);
        agree_txt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    agree_txt.setBackgroundResource(R.drawable.agree_btn_pressed);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    agree_txt.setBackgroundResource(R.drawable.agree_btn_normal);

                    editor.putString("ConfirmStatus", "verify");
                    editor.commit();
                    Log.d("ConfirmStatus", preferences.getString("ConfirmStatus", ""));
                    Log.d("ConfirmStatus", preferences.getString("ConfirmStatus", ""));

                    // Go to the intro page
                    startActivity(new Intent(ConfirmActivity.this, IntroActivity.class));
                    finish();
                    UserInitActivity.initActivity.finish();
                }

                return true;
            }
        });

        final TextView cancel_txt = (TextView) findViewById(R.id.cancel_txt);
        cancel_txt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cancel_txt.setBackgroundResource(R.drawable.cancel_btn_pressed);
                    cancel_txt.setTextColor(Color.WHITE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    cancel_txt.setBackgroundResource(R.drawable.start_btn_normal);
                    cancel_txt.setTextColor(Color.parseColor("#B8B8B8"));

                    // Go back to init page.
                    startActivity(new Intent(ConfirmActivity.this, UserInitActivity.class));
                    overridePendingTransition(R.anim.trans_up_in, R.anim.trans_up_out);
                    finish();
                }

                return true;
            }
        });

        TextView guide_txt = (TextView) findViewById(R.id.guide_txt);
        guide_txt.setText(Html.fromHtml(getString(R.string.service_guide)));
    }
}
