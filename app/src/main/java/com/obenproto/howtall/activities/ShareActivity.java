package com.obenproto.howtall.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.obenproto.howtall.R;
import com.obenproto.howtall.api.HowTallAPIClient;
import com.obenproto.howtall.api.HowTallAPIService;
import com.obenproto.howtall.image.UserPicture;
import com.obenproto.howtall.response.HowTallApiResponse;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * ShareActivity
 * <p/>
 * Created by Petro Rington on 12/22/2015.
 */
public class ShareActivity extends Activity {

    final Context context = this;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    LinearLayout tryagain_alert;
    LinearLayout profile_view;
    TextView addselfie_btn, share_btn;
    EditText email_edit;
    String email_pattern;
    Uri selectedImageUri;
    File library_img;
    TextView height_txt, age_txt, gender_txt;
    RelativeLayout share_screen;
    Bitmap profile_bmp;

    private String camera_img_path = "";
    private ImageView mPhotoCapturedImageView;
    private static final int ACTIVITY_START_CAMERA_APP = 0;
    private static final int SELECT_PICTURE = 101;
    public static final String IMAGE_TYPE = "image/*";
    public static String PROFILE_IMG_PATH = "";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);

        View background_layout = findViewById(R.id.share_activity);
        final Drawable background = background_layout.getBackground();
        background.setAlpha(255);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        mPhotoCapturedImageView = (ImageView) findViewById(R.id.profile_img);

        share_screen = (RelativeLayout) findViewById(R.id.certify_screen);
        share_screen.setDrawingCacheEnabled(true);

        profile_view = (LinearLayout) findViewById(R.id.profile_view);
        tryagain_alert = (LinearLayout) findViewById(R.id.alert_view);
        email_edit = (EditText) findViewById(R.id.email_edit);
        tryagain_alert.setVisibility(View.GONE);

        height_txt = (TextView) findViewById(R.id.height_txt);
        age_txt = (TextView) findViewById(R.id.age_txt);
        gender_txt = (TextView) findViewById(R.id.gender_txt);

        height_txt.setText(preferences.getString("ProfileHeight", ""));
        age_txt.setText(preferences.getString("ProfileAge", ""));
        gender_txt.setText(preferences.getString("ProfileGender", ""));

        addselfie_btn = (TextView) findViewById(R.id.addselfie_btn);
        addselfie_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    addselfie_btn.setBackgroundColor(Color.parseColor("#F29E27"));
                    addselfie_btn.setTextColor(Color.WHITE);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    addselfie_btn.setBackgroundResource(R.drawable.add_selfie_normal);
                    addselfie_btn.setTextColor(Color.parseColor("#F29E27"));

                    final Dialog photo_alert = new Dialog(context);
                    photo_alert.setContentView(R.layout.photo_select_alert);
                    photo_alert.setTitle("Photo Select");

                    TextView library_btn = (TextView) photo_alert.findViewById(R.id.library_btn);
                    library_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectPhoto(v);
                            photo_alert.cancel();
                        }
                    });

                    TextView camera_btn = (TextView) photo_alert.findViewById(R.id.camera_btn);
                    camera_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            takePhoto(v);
                            photo_alert.cancel();
                        }
                    });

                    TextView cancel_btn = (TextView) photo_alert.findViewById(R.id.cancel_btn);
                    cancel_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            photo_alert.cancel();
                        }
                    });

                    photo_alert.show();
                }

                return true;
            }
        });

        share_btn = (TextView) findViewById(R.id.share_btn);
        share_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    share_btn.setBackgroundResource(R.drawable.share_btn_pressed);
                    share_btn.setTextColor(Color.WHITE);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    share_btn.setBackgroundResource(R.drawable.result_page_pressed);
                    share_btn.setTextColor(Color.parseColor("#F29E27"));

                    // Create the profile image from the relativelayout.
                    createProfileImg();

                    // Share the profile.
                    onShareProfile(PROFILE_IMG_PATH);
                }

                return true;
            }
        });

        TextView tryagain_btn = (TextView) findViewById(R.id.tryagain_btn);
        tryagain_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!preferences.getString("RegisterEmail", "").equals("")) {
                    Log.d("Email Registered", preferences.getString("RegisterEmail", ""));

                    startActivity(new Intent(ShareActivity.this, RecordActivity.class));
                    finish();

                    return;
                }

                tryagain_alert.setVisibility(View.VISIBLE);
                addselfie_btn.setAlpha(0.5f);
                addselfie_btn.setEnabled(false);
                share_btn.setAlpha(0.5f);
                share_btn.setEnabled(false);
                profile_view.setAlpha(0.5f);

                TextView cancel_btn = (TextView) findViewById(R.id.cancel_txt);
                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tryagain_alert.setVisibility(View.GONE);
                        addselfie_btn.setAlpha(1.0f);
                        addselfie_btn.setEnabled(true);
                        share_btn.setAlpha(1.0f);
                        share_btn.setEnabled(true);
                        profile_view.setAlpha(1.0f);

                        startActivity(new Intent(ShareActivity.this, RecordActivity.class));
                        finish();
                    }
                });

                email_pattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z]+";

                TextView register_btn = (TextView) findViewById(R.id.register_btn);
                register_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = email_edit.getText().toString().trim();

                        if (TextUtils.isEmpty(email_edit.getText().toString().trim())) {
                            email_edit.setError("Email empty!");
                            email_edit.focusSearch(View.FOCUS_DOWN);

                        } else if (!email.matches(email_pattern)) {
                            email_edit.setError("email must be in format:abc@abc.abc");
                            email_edit.focusSearch(View.FOCUS_DOWN);

                        } else {
                            int recordId = Integer.parseInt(preferences.getString("RecordID", ""));
                            String email_str = email_edit.getText().toString();

                            Log.d("recordID and emai", String.valueOf(recordId) + email_str);

                            // save user email
                            onSaveUserEmail(recordId, email_str);

                            startActivity(new Intent(ShareActivity.this, RecordActivity.class));
                            finish();
                        }
                    }
                });
            }
        });
    }

    // Create the profile image.
    public void createProfileImg() {
        // Get the Bitmap from the relativelayout
        share_screen.buildDrawingCache();
        profile_bmp = Bitmap.createBitmap(share_screen.getDrawingCache());
        share_screen.destroyDrawingCache();

        // Save the image.
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        profile_bmp.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Log.d("file name", timeStamp);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "profile_" + timeStamp + ".png");
        try {
            f.createNewFile();
            PROFILE_IMG_PATH = f.getPath();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();

        } catch (Exception ignored) {

        }
    }

    // Share the profile
    private void onShareProfile(String imgPath) {

        Intent it = new Intent(Intent.ACTION_SEND);
        it.setType(IMAGE_TYPE);
        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(it, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

        List<Intent> targetedShareItents = new ArrayList<>();

        String imgFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/images.jpg";
        String subjectStr = "Check out this app called HowTall";
        String messageStr = "HowTall.me predicted my Height, Age and Gender based on my voice! How Tall do YOU sound?    https://appsto.re/us/R0nt-.i";

        for (ResolveInfo info : resolveInfos) {
            Intent targeted = new Intent(Intent.ACTION_SEND);
            targeted.setType(IMAGE_TYPE);

            ActivityInfo activityInfo = info.activityInfo;

            targeted.setPackage(activityInfo.packageName);

            targeted.putExtra(Intent.EXTRA_SUBJECT, subjectStr);

            targeted.putExtra(Intent.EXTRA_TEXT, messageStr);
            targeted.putExtra("sms_body", messageStr);

            targeted.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imgPath)));
            targeted.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            targetedShareItents.add(targeted);
        }

        Intent openInChooser = Intent.createChooser(targetedShareItents.remove(0), "Sharing Image");
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareItents.toArray(new Parcelable[]{}));
        startActivity(openInChooser);
    }

    // Save the user email.
    public void onSaveUserEmail(int recordId, String email) {

        HowTallAPIService client = HowTallAPIClient.newInstance(HowTallAPIService.class);
        Call<HowTallApiResponse> call = client.saveUserEmail(recordId, email);

        call.enqueue(new Callback<HowTallApiResponse>() {
            @Override
            public void onResponse(Response<HowTallApiResponse> response, Retrofit retrofit) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    HowTallApiResponse response_result = response.body();
                    Log.d("response result", response_result.Record.toString());

                    editor.putString("RegisterEmail", response_result.Record.getEmail());
                    editor.commit();

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Toast.makeText(getApplicationContext(), "Can't register Email(Http Unauthorized)", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Can't register Email(Http Failure)", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Save Email Failure", t.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "Cant't register Email" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Save the user selfie.
    public void onSaveUserSelfie(int recordId, RequestBody selfieFile) {

        HowTallAPIService client = HowTallAPIClient.newInstance(HowTallAPIService.class);
        Call<HowTallApiResponse> call = client.saveUserSelfie(recordId, selfieFile);

        call.enqueue(new Callback<HowTallApiResponse>() {
            @Override
            public void onResponse(Response<HowTallApiResponse> response, Retrofit retrofit) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    HowTallApiResponse response_result = response.body();

                    Log.d("response result", response_result.Record.toString());
                    Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_LONG).show();

                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Toast.makeText(getApplicationContext(), "Http Unauthorized", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Http Failure", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Save user selfie", t.getMessage());
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Camera
    public void takePhoto(View view) {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
    }

    // Select the photo from gallery
    public void selectPhoto(View view) {

        Intent intent = new Intent();
        intent.setType(IMAGE_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.select_picture)), SELECT_PICTURE);
    }

    File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String imageFileName = "HowTall_Profile_" + timeStamp + "_";
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".png", storageDirectory);
        camera_img_path = image.getAbsolutePath();

        return image;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void setReducedImageSize() {

        int targetImageViewWidth = mPhotoCapturedImageView.getWidth();
        int targetImageViewHeight = mPhotoCapturedImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(camera_img_path, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        Log.d("cameara width", String.valueOf(bmOptions.outWidth));
        Log.d("cameara height", String.valueOf(bmOptions.outHeight));

        int scaleFactor = Math.min(cameraImageWidth / targetImageViewWidth, cameraImageHeight / targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        Bitmap photoReducedSizeBitmp = BitmapFactory.decodeFile(camera_img_path, bmOptions);
        mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmp);
        mPhotoCapturedImageView.setBackgroundColor(Color.TRANSPARENT);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
                try {
                    mPhotoCapturedImageView.setImageBitmap(new UserPicture(selectedImageUri, getContentResolver()).getBitmap());
                    mPhotoCapturedImageView.setBackgroundColor(Color.TRANSPARENT);

                    library_img = getFileFromUri(selectedImageUri);

                    Log.d("selected image path", library_img.getPath());
                    addselfie_btn.setText("Change Selfie");

                    // save user selfie.
                    int recordId = Integer.parseInt(preferences.getString("RecordID", ""));
                    RequestBody imageBody = RequestBody.create(MediaType.parse("image/png"), library_img);

                    onSaveUserSelfie(recordId, imageBody);

                } catch (IOException e) {
                    Log.e(ShareActivity.class.getSimpleName(), "Failed to load image", e);
                }

            } else if (requestCode == ACTIVITY_START_CAMERA_APP) {
                Log.d("selected image path", camera_img_path);
                addselfie_btn.setText("Change Selfie");

                // Set the image to Profile ImageView.
                setReducedImageSize();

                // save user selfie.
                int recordId = Integer.parseInt(preferences.getString("RecordID", ""));
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/png"),
                        new File(camera_img_path));

                onSaveUserSelfie(recordId, imageBody);

            }
        } else {
            // report failure
            Toast.makeText(getApplicationContext(), R.string.msg_failed_to_get_intent_data, Toast.LENGTH_LONG).show();
            Log.d(ShareActivity.class.getSimpleName(), "Failed to get intent data, result code is " + resultCode);

        }
    }

    // Get the image file from URI using aFileChooser Library.
    public File getFileFromUri(Uri uri) {
        return com.ipaulpro.afilechooser.utils.FileUtils.getFile(context, uri);
    }
}