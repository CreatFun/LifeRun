package com.example.liferun;


import static android.Manifest.permission.ACTIVITY_RECOGNITION;
import static android.Manifest.permission.BODY_SENSORS;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

public class SettingsActivity extends AppCompatActivity {
    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Кнопка авторизации
        TextView tv_askAuthorization = findViewById(R.id.tv_askAuthorization);
        tv_askAuthorization.setOnClickListener(view -> requestOAuthPermission());

        // Кнопка разрешений
        TextView tv_askPermissions = findViewById(R.id.tv_askPermissions);
        tv_askPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestNeededPermissions();
            }
        });

        // Кнопка настройки целей
        TextView tv_setGoals = findViewById(R.id.tv_setGoals);
        tv_setGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSetGoalsPopup();
            }
        });

        TextView tv_aboutApp = findViewById(R.id.tv_aboutApp);
        tv_aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAboutAppPopup();
            }
        });

        TextView tv_Feedback = findViewById(R.id.tv_Feedback);
        tv_Feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    public void startMainActivity(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void startSetGoalsPopup(){
        SetGoalsPopup.startSetGoalsPopupActivity(this);
    }

    public void startAboutAppPopup(){
        AboutAppPopup.startAboutAppPopup(this);
    }


    /**
     * Launches the Google SignIn activity to request OAuth permission for the user.
     * Keep
     */
    private void requestOAuthPermission() {
        FitnessOptions fitnessOptions = getFitnessSignInOptions();
        GoogleSignIn.requestPermissions(
                this,
                REQUEST_OAUTH_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions);
    }

    private void requestNeededPermissions(){
        ActivityCompat.requestPermissions(this,
                new String[]{
                        ACTIVITY_RECOGNITION,
                        BODY_SENSORS,
                },
                PERMISSION_REQUEST_CODE);
    }

    /**
     * Gets {@link FitnessOptions} in order to check or request OAuth permission for the user.
     * Keep
     */
    private FitnessOptions getFitnessSignInOptions() {
        return FitnessOptions.builder()
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .build();
    }

     /*
      Checks if user's account has OAuth permission to Fitness API.
      Keep
     */
//    private boolean hasOAuthPermission() {
//        FitnessOptions fitnessOptions = getFitnessSignInOptions();
//        return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions);
//    }
}