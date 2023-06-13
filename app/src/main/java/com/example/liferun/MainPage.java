package com.example.liferun;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;


public class MainPage extends Fragment {
    boolean isRunning = false;
    public static boolean dataWasSet = false;

    static TextView tv_Steps, tv_StepsGeneral,
            tv_Calories, tv_CaloriesGeneral,
            tv_Distance,
            tv_PulseDailyAverage,
            tv_pulseLastMeasurement,
            tv_deepSleepDuration, tv_lightSleepDuration, tv_awakeDuration, tv_sumSleepDuration;
    Button updateInfoButton;



    public MainPage() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_page, container, false);

        tv_Steps = v.findViewById(R.id.tv_stepsCount);
        tv_StepsGeneral = v.findViewById(R.id.tv_stepsCountGeneral);
        tv_Calories = v.findViewById(R.id.tv_caloriesCount);
        tv_CaloriesGeneral = v.findViewById(R.id.tv_caloriesCountGeneral);
        tv_Distance = v.findViewById(R.id.tv_distanceCount);
//        tv_PulseDailyAverage = v.findViewById(R.id.tv_pulseDailyAverageCount);
        tv_pulseLastMeasurement = v.findViewById(R.id.tv_pulseLastMeasurementCount);

        tv_deepSleepDuration = v.findViewById(R.id.tv_deepSleepDuration);
        tv_lightSleepDuration = v.findViewById(R.id.tv_lightSleepDuration);
        tv_awakeDuration = v.findViewById(R.id.tv_awakeDuration);
        tv_sumSleepDuration = v.findViewById(R.id.tv_sumSleepDuration);

//        updateInfoButton = v.findViewById(R.id.updateInfo_button);
//
//        updateInfoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                displayData();
//            }
//        });





        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        displayData();
    }

    @Override
    public void onStart() {
        super.onStart();
//        displayData();
    }

    @Override
    public void onResume() {
        super.onResume();
        isRunning = true;
        if (isRunning) {
            displayData();

        }


    }

    @Override
    public void onStop() {
        super.onStop();
        isRunning = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    public static void displayData(){
        tv_Steps.setText(MainActivity.dailyStepsInfo);
        tv_StepsGeneral.setText(MainActivity.dailyStepsInfo);
        tv_Calories.setText(MainActivity.dailyCaloriesInfo);
        tv_CaloriesGeneral.setText(MainActivity.dailyCaloriesInfo);
        tv_Distance.setText(MainActivity.dailyDistanceInfo);
//        tv_PulseDailyAverage.setText(MainActivity.pulseDailyAverageInfo);
        tv_pulseLastMeasurement.setText(MainActivity.pulseLastMeasurementInfo);

        tv_deepSleepDuration.setText(MainActivity.deepSleepDurationInfo);
        tv_lightSleepDuration.setText(MainActivity.lightSleepDurationInfo);
        tv_awakeDuration.setText(MainActivity.awakeDurationInfo);
        tv_sumSleepDuration.setText(MainActivity.sumSleepDurationInfo);
    }

}

