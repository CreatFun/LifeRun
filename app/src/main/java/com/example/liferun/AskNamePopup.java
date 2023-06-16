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
import android.widget.Toast;

import java.util.Objects;

public class AskNamePopup extends AppCompatActivity {

    EditText et_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ask_name_popup);

        et_name = findViewById(R.id.et_name);

        SharedPreferences prefs = getSharedPreferences("com.example.liferun", MODE_PRIVATE);
        if (prefs.contains("userName") && prefs.getString("userName", null) != null){
            et_name.setText(prefs.getString("userName", null));
        }

        Button btn_saveName = findViewById(R.id.btn_saveName);
        btn_saveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!String.valueOf(et_name.getText()).equals("")){
                    getName();
                }
                else {
                    showToast("Введите имя или нажмите позже", "short");
                }
            }
        });

        Button btn_later = findViewById(R.id.btn_later);
        btn_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("com.example.liferun", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("userName", "").apply();
                finish();
            }
        });
    }

    public void getName(){
        SharedPreferences prefs = getSharedPreferences("com.example.liferun", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String name = et_name.getText().toString();
        editor.putString("userName", name).apply();
        finish();
    }

    public static void startAskNamePopup(Activity caller){
        Intent intent = new Intent(caller, AskNamePopup.class);
        caller.startActivity(intent);
    }

    public void showToast(String toastText, String duration){
        if (Objects.equals(duration, "short"))
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
    }
}