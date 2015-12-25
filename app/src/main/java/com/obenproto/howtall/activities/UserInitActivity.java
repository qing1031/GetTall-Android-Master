package com.obenproto.howtall.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.obenproto.howtall.R;
import com.obenproto.howtall.api.HowTallAPIClient;
import com.obenproto.howtall.api.HowTallAPIService;
import com.obenproto.howtall.response.HowTallApiResponse;

import java.net.HttpURLConnection;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * UserInitActivity
 * <p>
 * Created by Petro Rington on 12/22/2015.
 */
public class UserInitActivity extends Activity {

    public String phone_id;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TextView startText;
    ProgressBar progressBar;
    public static Activity initActivity;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_init_activity);

        initActivity = this;

        phone_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        // Mkae the request for init user.
        onInitUser(phone_id);

        startText = (TextView) findViewById(R.id.startBtn);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        String str = preferences.getString("ConfirmStatus", "");
        if (!str.equals("")) {
            startText.setVisibility(View.GONE);
        }


        startText.getBackground().setAlpha(150);
        startText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startText.setBackgroundResource(R.drawable.start_btn_pressed);
                    startText.setTextColor(Color.WHITE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    startText.setBackgroundResource(R.drawable.start_btn_normal);
                    startText.setTextColor(Color.BLACK);
                    startText.getBackground().setAlpha(150);

                    startActivity(new Intent(UserInitActivity.this, ConfirmActivity.class));
                    overridePendingTransition(R.anim.trans_down_in, R.anim.trans_down_out);
                }

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_down_in, R.anim.trans_down_out);
    }

    public void onInitUser(String phoneId) {

        HowTallAPIService client = HowTallAPIClient.newInstance(HowTallAPIService.class);
        Call<HowTallApiResponse> call = client.initUser(phoneId);

        call.enqueue(new Callback<HowTallApiResponse>() {
            @Override
            public void onResponse(Response<HowTallApiResponse> response, Retrofit retrofit) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    startText.setEnabled(true);
                    startText.setAlpha(1.0f);
                    progressBar.setVisibility(View.GONE);

                    HowTallApiResponse response_result = response.body();
                    Log.d("done", String.valueOf(response_result.User));
                    Log.d("UserID", String.valueOf(response_result.User.getUserId()));

                    // Save the user ID to shared preference.
                    editor.putInt("UserID", response_result.User.getUserId());
                    editor.apply();

                    String confirm_str = preferences.getString("ConfirmStatus", "");
                    if (!confirm_str.equals("")) {
                        startActivity(new Intent(UserInitActivity.this, RecordActivity.class));
                        finish();
                    }

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Toast.makeText(getApplicationContext(), "Http Unauthorized", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Upload", t.getMessage());
            }
        });
    }
}
