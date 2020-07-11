package com.nisith.translatorandlanguagedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

public class LanguageDetectionActivity extends AppCompatActivity {

    private ImageView galleryImageView, galleryImageIcon, cameraImageIcon, writeImageIcon;
    private CameraView cameraView;
    private Button detectLanguageButton;
    private TextView resultTextView;
    private ProgressBar progressBar;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_detection);
        galleryImageView = findViewById(R.id.gallery_image_view);
        galleryImageIcon = findViewById(R.id.gallery_image_view_icon);
        cameraImageIcon = findViewById(R.id.camera_image_view_icon);
        cameraView = findViewById(R.id.camera_view);
        detectLanguageButton = findViewById(R.id.detect_language_button);
        resultTextView = findViewById(R.id.result_text_view);
        progressBar = findViewById(R.id.progress_bar);
        editText = findViewById(R.id.edit_text);
        writeImageIcon = findViewById(R.id.write_image_view_icon);
        cameraView.setVisibility(View.INVISIBLE);
        galleryImageView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.GONE);

        detectLanguageButton.setOnClickListener(new MyClickListener());
        writeImageIcon.setOnClickListener(new MyClickListener());
        galleryImageIcon.setOnClickListener(new MyClickListener());
        cameraImageIcon.setOnClickListener(new MyClickListener());



        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                Bitmap imageBitmap = cameraKitImage.getBitmap();
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();
//                startLanguageDetection(imageBitmap);
                progressBar.setVisibility(View.VISIBLE);
                detectLanguageButton.setEnabled(false);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });


    }


    private void startLanguageDetection(String text){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseLanguageIdentification languageIdentification = FirebaseNaturalLanguage
                .getInstance()
                .getLanguageIdentification();
        languageIdentification.identifyLanguage(text)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String languageCode) {
                        if (languageCode.equals("und")){
                            resultTextView.setText("Can't detect this language");
                        }else {
                            resultTextView.setText("Language Code is " + languageCode);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LanguageDetectionActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }




    private class MyClickListener implements View.OnClickListener{
        public void onClick(View view){
            switch (view.getId()){
                case R.id.detect_language_button:
                    String text = editText.getText().toString();
                    if (! text.isEmpty()) {
                        startLanguageDetection(text);
                    }else {
                        editText.setFocusable(true);
                        editText.setError("Enter Text");
                    }
//                    cameraView.start();
//                    cameraView.captureImage();
                    break;
                case R.id.write_image_view_icon:
                    cameraView.stop();
                    cameraView.setVisibility(View.INVISIBLE);
                    galleryImageView.setVisibility(View.INVISIBLE);
                    editText.setVisibility(View.VISIBLE);
                    break;
                case R.id.gallery_image_view_icon:
                    cameraView.stop();
                    cameraView.setVisibility(View.INVISIBLE);
                    galleryImageView.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.INVISIBLE);
                    break;
                case R.id.camera_image_view_icon:
                    cameraView.start();
                    cameraView.setVisibility(View.VISIBLE);
                    galleryImageView.setVisibility(View.INVISIBLE);
                    editText.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
//        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }
}