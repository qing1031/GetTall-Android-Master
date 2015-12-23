package com.obenproto.howtall.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.obenproto.howtall.R;
import com.obenproto.howtall.api.HowTallAPIClient;
import com.obenproto.howtall.api.HowTallAPIService;
import com.obenproto.howtall.recorder.ExtAudioRecorder;
import com.obenproto.howtall.response.HowTallApiResponse;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Random;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class RecordActivity extends Activity {

    private static String filePath;
    private ExtAudioRecorder extAudioRecorder;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TextView example_txt, recording_status;
    ProgressBar progressBar;
    Drawable background;
    ImageButton recordBtn;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);

        View background_layout= findViewById(R.id.record_activity);
        background = background_layout.getBackground();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HowTallRecVoice.wav";

        recordBtn = (ImageButton) findViewById(R.id.recordBtn);
        recording_status = (TextView) findViewById(R.id.status_txt);
        example_txt = (TextView) findViewById(R.id.example_txt);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#484A49"),
                android.graphics.PorterDuff.Mode.MULTIPLY);

        // Set the example phrase.
        String[] phrases = {"When the sunlight strikes raindrops in the air, they act like a prism and form a rainbow.",
                "The rainbow is a division of white light into many beautiful colors.",
                "There is, according to legend, a boiling pot of gold at one end.",
                "People look, but no one ever finds it.",
                "She had your dark suit in greasy wash water all year.",
                "Don't ask me to carry an oily rag like that.",
                "The eastern coast is a place for pure pleasure and excitement."};

        Random random = new Random();
        int random_index = random.nextInt(phrases.length);
        Log.d("example index", String.valueOf(random_index));
        example_txt.setText(phrases[random_index]);

        recordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    recordBtn.setBackgroundResource(R.drawable.mic_pressed);
                    recording_status.setText(R.string.recording_status);
                    example_txt.setBackgroundColor(Color.RED);

                    // start the voice record
                    startRecording();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    recordBtn.setBackgroundResource(R.drawable.mic_normal);
                    recording_status.setText(R.string.ready_status);
                    example_txt.setBackgroundColor(Color.parseColor("#137F66"));

                    // stop the voice record
                    stopRecording();
                }

                return true;
            }
        });

        progressBar.setVisibility(View.GONE);
        background.setAlpha(255);
        example_txt.setAlpha(1.0f);
        recording_status.setAlpha(1.0f);
        recordBtn.setAlpha(1.0f);
    }

    public void startRecording() {
        Log.d("Recorder", "Start recording");

//				extAudioRecorder = ExtAudioRecorder.getInstanse(true);    // Compressed recording (AMR)
        extAudioRecorder = ExtAudioRecorder.getInstanse(false); // Uncompressed recording (WAV)

//        extAudioRecorder.setOutputFile(filePath);
//        extAudioRecorder.prepare();
//        extAudioRecorder.start();
    }

    public void stopRecording() {
        Log.d("Recorder", "Stop recording");

        progressBar.setVisibility(View.VISIBLE);
        recording_status.setText("Processing ...");
        background.setAlpha(40);
        example_txt.setAlpha(0.4f);
        recording_status.setAlpha(0.4f);

//        extAudioRecorder.stop();
//        extAudioRecorder.release();

        // Upload the recorded voice.
        int userId = preferences.getInt("UserID", 0);
        RequestBody audioBody = RequestBody.create(MediaType.parse("image/png"), new File(filePath));

//        onUploadVoice(userId, audioBody);
        editor.putString("EstimatedHeight", String.valueOf(71));
        editor.putString("EstimatedAge", "32");
        editor.putString("EstimatedGender", "Male");
        editor.putString("RecordID", "823");
        editor.commit();

        startActivity(new Intent(RecordActivity.this, ResultActivity.class));
        finish();
    }

    // Upload and Save the recorded voice.
    public void onUploadVoice(int userId, RequestBody audioFile) {

        HowTallAPIService client = HowTallAPIClient.newInstance(HowTallAPIService.class);
        Call<HowTallApiResponse> call = client.saveUserVoice(userId, audioFile);

        call.enqueue(new Callback<HowTallApiResponse>() {
            @Override
            public void onResponse(Response<HowTallApiResponse> response, Retrofit retrofit) {
                progressBar.setVisibility(View.GONE);
                background.setAlpha(250);
                example_txt.setAlpha(1.0f);
                recording_status.setAlpha(1.0f);
                recordBtn.setAlpha(1.0f);
                recording_status.setText("Press and hold the button below and say this:");

                if (response.code() == HttpURLConnection.HTTP_OK) {
                    HowTallApiResponse response_result = response.body();

                    if (!response_result.Record.getMessage().equals("SUCCESS")) {
                        example_txt.setText(response_result.Record.getMessage());
                        Toast.makeText(getApplicationContext(), "Your voice is empty!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    int estimatedHeight = (int) (Integer.parseInt(response_result.Record.getEstimatedHeight()) * 2.54);
                    String gender_str = (Float.parseFloat(response_result.Record.getEstimatedGender()) >= 0.5)
                            ? "Male" : "Female";

                    editor.putString("EstimatedHeight", String.valueOf(estimatedHeight));
                    editor.putString("EstimatedAge", response_result.Record.getEstimatedAge());
                    editor.putString("EstimatedGender", gender_str);
                    editor.putString("RecordID", response_result.Record.getRecordId());
                    editor.commit();

                    Log.d("gender", gender_str);

                    Log.d("upload result ", response_result.Record.getMessage());
                    Log.d("done", String.valueOf(response_result.Record));
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(RecordActivity.this, ResultActivity.class));
                    finish();

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Toast.makeText(getApplicationContext(), "Http Unauthorized", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Http Failure", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Upload", t.getMessage());
                progressBar.setVisibility(View.GONE);
                background.setAlpha(255);
                example_txt.setAlpha(1.0f);
                recording_status.setAlpha(1.0f);
                recordBtn.setAlpha(1.0f);
                recording_status.setText("Press and hold the button below and say this:");

                Toast.makeText(getApplicationContext(), " Response Failure", Toast.LENGTH_LONG).show();
            }
        });
    }

}
