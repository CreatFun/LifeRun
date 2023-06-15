package com.example.liferun;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class SetGoalsPopup extends AppCompatActivity {

    EditText et_stepsGoal, et_caloriesGoal, et_hoursGoal;
    Button btn_saveGoals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_goals_popup);

        et_stepsGoal = findViewById(R.id.et_stepsGoal);
        et_caloriesGoal = findViewById(R.id.et_caloriesGoal);
        et_hoursGoal = findViewById(R.id.et_hoursGoal);

        setValues();

        btn_saveGoals = findViewById(R.id.btn_saveGoals);
        btn_saveGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("com.example.liferun", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if (et_stepsGoal.getText() != null){
                    int steps = Integer.parseInt(et_stepsGoal.getText().toString());
                    editor.putInt("stepsGoal", steps).apply();
                    editor.putString("stepsGoalInfo", getString(R.string.stepsGoal, steps));
                }
                else {
                    int steps = 12000;
                    editor.putInt("stepsGoal", steps).apply();
                    editor.putString("stepsGoalInfo", getString(R.string.stepsGoal, steps));
                }


                if (et_caloriesGoal.getText() != null){
                    int calories = Integer.parseInt(et_caloriesGoal.getText().toString());
                    editor.putInt("caloriesGoal", calories).apply();
                    editor.putString("caloriesGoalInfo", getString(R.string.caloriesGoal, calories));
                }
                else {
                    int calories = 2000;
                    editor.putInt("caloriesGoal", calories).apply();
                    editor.putString("caloriesGoalInfo", getString(R.string.caloriesGoal, calories));
                }


                if (et_hoursGoal.getText() != null){
                    int hours = Integer.parseInt(et_hoursGoal.getText().toString());
                    editor.putInt("hoursGoal", hours).apply();
                    editor.putString("hoursGoalInfo", getString(R.string.hoursGoal, hours));
                }
                else {
                    int hours = 8;
                    editor.putInt("hoursGoal", hours).apply();
                    editor.putString("hoursGoalInfo", getString(R.string.hoursGoal, hours));
                }

                finish();
            }
        });


    }

    public void setValues(){
        et_stepsGoal.setText(String.valueOf(MainActivity.stepsGoal));
        et_caloriesGoal.setText(String.valueOf(MainActivity.caloriesGoal));
        et_hoursGoal.setText(String.valueOf(MainActivity.hoursGoal));
    }


    public static void startSetGoalsPopupActivity(Activity caller){
        Intent intent = new Intent(caller, SetGoalsPopup.class);
        caller.startActivity(intent);
    }
}