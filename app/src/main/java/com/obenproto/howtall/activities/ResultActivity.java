package com.obenproto.howtall.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
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

public class ResultActivity extends Activity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    boolean height_chk_flag = true;
    boolean age_chk_flag = true;
    boolean gender_chk_flag = true;

    LinearLayout alert_dlg;
    TextView question_txt, done_btn;
    TextView height_txt, age_txt, gender_txt;
    ImageView meter_img, inch_img, height_chk, age_chk, gender_chk;

    EditText value_edt;
    NumberPicker value_picker;
    Drawable background;
    View model_view;
    ProgressBar progressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        model_view = findViewById(R.id.model_layout);
        View background_layout = findViewById(R.id.result_activity);
        background = background_layout.getBackground();

        alert_dlg = (LinearLayout) findViewById(R.id.alert_view);
        question_txt = (TextView) findViewById(R.id.model_question_txt);
        done_btn = (TextView) findViewById(R.id.done_btn);
        value_edt = (EditText) findViewById(R.id.actual_value_edit);
        value_picker = (NumberPicker) findViewById(R.id.model_value_picker);
        alert_dlg.setVisibility(View.GONE);
        value_picker.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        height_txt = (TextView) findViewById(R.id.height_txt);
        age_txt = (TextView) findViewById(R.id.age_txt);
        gender_txt = (TextView) findViewById(R.id.gender_txt);
        meter_img = (ImageView) findViewById(R.id.image_meter);
        inch_img = (ImageView) findViewById(R.id.image_inch);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        height_txt.setText(preferences.getString("EstimatedHeight", "") + " cm");
        age_txt.setText(preferences.getString("EstimatedAge", ""));
        gender_txt.setText(preferences.getString("EstimatedGender", ""));

        int height = Integer.parseInt(preferences.getString("EstimatedHeight", ""));
        Rect meter_rectangle = new Rect();
        meter_img.getWindowVisibleDisplayFrame(meter_rectangle);

        float height_pos = (float) ((meter_rectangle.height()/19-6)*4 * (6.25 - height * 0.3937/12) - 10);
//        inch_img.setY((meter_rectangle.height()/19 - 6)*6 - 10);
        inch_img.setY(height_pos);

        height_chk = (ImageView) findViewById(R.id.height_chk);
        height_chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                height_chk_flag = !height_chk_flag;

                setHeightChk(height_chk_flag);
            }
        });

        age_chk = (ImageView) findViewById(R.id.age_chk);
        age_chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                age_chk_flag = !age_chk_flag;

                setAgeChk(age_chk_flag);
            }
        });

        gender_chk = (ImageView) findViewById(R.id.gender_chk);
        gender_chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender_chk_flag = !gender_chk_flag;

                setGenderChk(gender_chk_flag);
            }
        });

        final TextView tryagain_btn = (TextView) findViewById(R.id.tryagain_btn);
        tryagain_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    tryagain_btn.setBackgroundResource(R.drawable.tryagain_btn_pressed);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    tryagain_btn.setBackgroundResource(R.drawable.tryagain_btn_normal);

                    startActivity(new Intent(ResultActivity.this, RecordActivity.class));
                    finish();
                }

                return true;
            }
        });

        final TextView addselfie_btn = (TextView) findViewById(R.id.addselfie_btn);
        addselfie_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    addselfie_btn.setBackgroundResource(R.drawable.tryagain_btn_pressed);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    addselfie_btn.setBackgroundResource(R.drawable.result_page_pressed);

                    editor.putString("ProfileHeight", height_txt.getText().toString());
                    editor.putString("ProfileAge", age_txt.getText().toString());
                    editor.putString("ProfileGender", gender_txt.getText().toString());
                    editor.commit();

                    startActivity(new Intent(ResultActivity.this, ShareActivity.class));
                    finish();
                }

                return true;
            }
        });
    }

    public void setHeightChk(boolean chk_flag) {
        if (chk_flag) {
            height_chk.setBackgroundResource(R.drawable.check_yes);
            height_txt.setText(preferences.getString("EstimatedHeight", "") + " cm");

        } else {
            height_chk.setBackgroundResource(R.drawable.check_no);

            showAlertDlg("You want to tell us your real height?",
                    "Your actual height",
                    0);
        }
    }

    public void setAgeChk(boolean chk_flag) {
        if (chk_flag) {
            age_chk.setBackgroundResource(R.drawable.check_yes);
            age_txt.setText(preferences.getString("EstimatedAge", ""));

        } else {
            age_chk.setBackgroundResource(R.drawable.check_no);

            showAlertDlg("You want to tell us your real age?",
                    "Your actual age",
                    1);
        }
    }

    public void setGenderChk(boolean chk_flag) {
        if(chk_flag) {
            gender_chk.setBackgroundResource(R.drawable.check_yes);
            gender_txt.setText(preferences.getString("EstimatedGender", ""));

        } else {
            gender_chk.setBackgroundResource(R.drawable.check_no);
            gender_txt.setText("Female");
            if (preferences.getString("EstimatedGender", "").equals("Female")) {
                gender_txt.setText("Male");
            }

            int recordId = Integer.parseInt(preferences.getString("RecordID", ""));
            int actualGender = (gender_txt.getText().toString().equals("Male")) ? 1 : 0;
            Log.d("record ID -- Actual gender", String.valueOf(recordId) + "----" + String.valueOf(actualGender));

            // save the user actual gender.
            onUpdateActualGender(recordId, actualGender);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void showAlertDlg(String questionStr, String valueHint, final int dlg_flag) {

        final String height_ary[] = new String[200];
        final String age_ary[] = new String[100];
        int i = 0, j = 0;
        while (i < 200) {
            height_ary[i] = String.valueOf(i + 40) + " cm";
            i++;
        }

        while (j < 100) {
            age_ary[j] = String.valueOf(j + 1);
            j++;
        }

        alert_dlg.setVisibility(View.VISIBLE);
        value_picker.setVisibility(View.VISIBLE);
        background.setAlpha(100);
        model_view.setAlpha(0.5f);
        model_view.setVisibility(View.GONE);

        question_txt.setText(questionStr);
        value_edt.setHint(valueHint);

        value_picker.setMinValue(0);
        if (dlg_flag == 0) {
            value_picker.setDisplayedValues(height_ary);
            value_picker.setMaxValue(height_ary.length - 1);
            value_picker.setValue(Integer.parseInt(height_txt.getText().toString().split(" ")[0]) - 40);

        } else {
            value_picker.setDisplayedValues(age_ary);
            value_picker.setMaxValue(age_ary.length - 1);
            value_picker.setValue(Integer.parseInt(age_txt.getText().toString()) - 1);
        }

        value_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (dlg_flag == 0) {
                    value_edt.setText(height_ary[newVal]);
                } else {
                    value_edt.setText(age_ary[newVal]);
                }
            }
        });

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert_dlg.setVisibility(View.GONE);
                value_picker.setVisibility(View.GONE);
                value_picker.setMaxValue(0);
                background.setAlpha(255);
                model_view.setAlpha(1.0f);
                model_view.setVisibility(View.VISIBLE);

                if (!value_edt.getText().toString().isEmpty()) {
                    if (dlg_flag == 0) {
                        height_txt.setText(value_edt.getText());

                        int recordId = Integer.parseInt(preferences.getString("RecordID", ""));
                        int actualHeight = Integer.parseInt(value_edt.getText().toString().split(" ")[0]);

                        // save the uer actual height.
                        onUpdateActualHeight(recordId, actualHeight);
                        progressBar.setVisibility(View.VISIBLE);

                    } else {
                        age_txt.setText(value_edt.getText());

                        int recordId = Integer.parseInt(preferences.getString("RecordID", ""));
                        int actualAge = Integer.parseInt(value_edt.getText().toString());

                        // save the user actual age.
                        onUpdateActualAge(recordId, actualAge);
                        progressBar.setVisibility(View.VISIBLE);

                    }
                    value_edt.setText(" ");
                }
            }
        });
    }

    // Update the actual height.
    public void onUpdateActualHeight(int recordId, int actualHeight) {

        HowTallAPIService client = HowTallAPIClient.newInstance(HowTallAPIService.class);
        Call<HowTallApiResponse> call = client.saveUserHeight(recordId, actualHeight);

        call.enqueue(new Callback<HowTallApiResponse>() {
            @Override
            public void onResponse(Response<HowTallApiResponse> response, Retrofit retrofit) {
                progressBar.setVisibility(View.GONE);

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    HowTallApiResponse response_result = response.body();
                    Log.d("response result", response_result.Record.toString());
                    Log.d("response: actual height", response_result.Record.getActualHeight());

                    Toast.makeText(getApplicationContext(), response_result.Record.getMessage(), Toast.LENGTH_LONG).show();

                    if (response_result.Record.getMessage().equals("ERROR"))
                        setHeightChk(true);

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    setHeightChk(true);
                    Toast.makeText(getApplicationContext(), "Http Unauthorized", Toast.LENGTH_LONG).show();

                } else {
                    setHeightChk(true);
                    Toast.makeText(getApplicationContext(), "Http Failure", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressBar.setVisibility(View.GONE);
                setHeightChk(true);
                Log.d("Update Height Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Update the actual age.
    public void onUpdateActualAge(int recordId, int actualAge) {

        HowTallAPIService client = HowTallAPIClient.newInstance(HowTallAPIService.class);
        Call<HowTallApiResponse> call = client.saveUserAge(recordId, actualAge);

        call.enqueue(new Callback<HowTallApiResponse>() {
            @Override
            public void onResponse(Response<HowTallApiResponse> response, Retrofit retrofit) {
                progressBar.setVisibility(View.GONE);

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    HowTallApiResponse response_result = response.body();
                    Log.d("response result", response_result.Record.toString());
                    Log.d("response: actual age", response_result.Record.getActualAge());

                    Toast.makeText(getApplicationContext(), response_result.Record.getMessage(), Toast.LENGTH_LONG).show();

                    if (response_result.Record.getMessage().equals("ERROR"))
                        setAgeChk(true);

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    setAgeChk(true);
                    Toast.makeText(getApplicationContext(), "Http Unauthorized", Toast.LENGTH_LONG).show();

                } else {
                    setAgeChk(true);
                    Toast.makeText(getApplicationContext(), "Http Failure", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressBar.setVisibility(View.GONE);
                setAgeChk(true);
                Log.d("Update Age Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Update the actual gender.
    public void onUpdateActualGender(int recordId, int actualGender) {

        HowTallAPIService client = HowTallAPIClient.newInstance(HowTallAPIService.class);
        Call<HowTallApiResponse> call = client.saveUserGender(recordId, actualGender);

        call.enqueue(new Callback<HowTallApiResponse>() {
            @Override
            public void onResponse(Response<HowTallApiResponse> response, Retrofit retrofit) {
                progressBar.setVisibility(View.GONE);

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    HowTallApiResponse response_result = response.body();
                    Log.d("response result", response_result.Record.toString());
                    Log.d("response: actual gender", response_result.Record.getActualGender());

                    Toast.makeText(getApplicationContext(), response_result.Record.getMessage(), Toast.LENGTH_LONG).show();

                    if (response_result.Record.getMessage().equals("ERROR"))
                        setGenderChk(true);

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    setGenderChk(true);
                    Toast.makeText(getApplicationContext(), "Http Unauthorized", Toast.LENGTH_LONG).show();

                } else {
                    setGenderChk(true);
                    Toast.makeText(getApplicationContext(), "Http Failure", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                progressBar.setVisibility(View.GONE);
                setGenderChk(true);

                Log.d("Update Gender Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}