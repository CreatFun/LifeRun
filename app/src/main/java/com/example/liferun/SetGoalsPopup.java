package com.example.liferun;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
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
                if (et_stepsGoal.getText() != null){
                    MainActivity.stepsGoal = Integer.parseInt(et_stepsGoal.getText().toString());
                }
                else MainActivity.stepsGoal = 12000;

                if (et_caloriesGoal.getText() != null){
                    MainActivity.caloriesGoal = Integer.parseInt(et_caloriesGoal.getText().toString());
                }
                else MainActivity.caloriesGoal = 2000;

                if (et_hoursGoal.getText() != null){
                    MainActivity.hoursGoal = Integer.parseInt(et_hoursGoal.getText().toString());
                }
                else MainActivity.hoursGoal = 8;

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