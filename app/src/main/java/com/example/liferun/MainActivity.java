package com.example.liferun;


//import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
//import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import static android.Manifest.permission.ACTIVITY_RECOGNITION;
import static android.Manifest.permission.BODY_SENSORS;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.window.SplashScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity  {
    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    TextView tv_Steps,
            tv_Calories,
            tv_Distance,
            tv_PulseDailyAverage,
            tv_pulseLastMeasurement;
    Button updateInfoButton;

    GoogleSignInAccount lastSignedInAccount;



    boolean isRunning = false;

    public static boolean isLoginSkipped = false;
    public static boolean isLoggingIn = false;
    boolean isPermissionAsked = false;

    SharedPreferences prefs = null;
    FitnessOptions fitnessOptions;

    int dailySteps;
    int dailyCalories;
    float dailyDistance;
    int pulseDailyAverage;
    int pulseLastMeasurement;

    public static String dailyStepsInfo;
    public static String dailyCaloriesInfo;
    public static String dailyDistanceInfo;
    public static String pulseDailyAverageInfo;
    public static String pulseLastMeasurementInfo;

    long deepSleepDuration;
    long lightSleepDuration;
    long awakeDuration;
    long sumSleepDuration;

    public static String deepSleepDurationInfo;
    public static String lightSleepDurationInfo;
    public static String awakeDurationInfo;
    public static String sumSleepDurationInfo;

    public static int stepsGoal, caloriesGoal, hoursGoal;
    public static String stepsGoalInfo, caloriesGoalInfo, hoursGoalInfo;

    public MainActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("com.example.liferun", MODE_PRIVATE);

        Log.i("first run", String.valueOf(prefs.getBoolean("firstrun", true)));
        // Если первый запуск
        if (!prefs.contains("firstrun")){
            // если перешел по кнопке логина
            if (isLoggingIn){
                setContentView(R.layout.activity_main);
                setGoals();
                isLoggingIn = false;
                Log.i("first run", String.valueOf(prefs.getBoolean("firstrun", true)));
                prefs = getSharedPreferences("com.example.liferun", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstrun", false).apply(); // меняем значение первого запуска
                Log.i("first run", String.valueOf(prefs.getBoolean("firstrun", true)));
                requestOAuthPermission();
                if (!allPermissionsGranted()){
                    requestNeededPermissions();
                }
            } else if (isLoginSkipped) { // если перешел по кнопке пропустить
                setContentView(R.layout.activity_main);
                setGoals();
                isLoginSkipped = false;
                Log.i("first run", String.valueOf(prefs.getBoolean("firstrun", true)));
                prefs = getSharedPreferences("com.example.liferun", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstrun", false).apply(); // меняем значение первого запуска
                Log.i("first run", String.valueOf(prefs.getBoolean("firstrun", true)));
                permissionsRationale();
            }
            else {
                startLoginActivity();
                finish();
            }
        }
        else {
            setContentView(R.layout.activity_main);
            loadData();
            setGoals();

            // Проверка авторизации и возможности отобразить данные
            if (hasOAuthPermission()){
                readData();
            }
            else {
                Toast.makeText(MainActivity.this,
                                "Для отображения информации необходимо авторизоваться", Toast.LENGTH_SHORT)
                        .show();
            }





        }

        // navigation code:
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this,  R.id.fragmentContainerView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);






//        tv_Steps = findViewById(R.id.tv_stepsCount);
//        tv_Calories = findViewById(R.id.tv_caloriesCount);
//        tv_Distance = findViewById(R.id.tv_distanceCount);
//        tv_PulseDailyAverage = findViewById(R.id.tv_pulseDailyAverageCount);
//        tv_pulseLastMeasurement = findViewById(R.id.tv_pulseLastMeasurementCount);
//
//        updateInfoButton = findViewById(R.id.updateInfo_button);
//        updateInfoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (hasOAuthPermission()) {
////                    findDataSources();
//                    readData();
//                }
//                else Toast.makeText(MainActivity.this,
//                                "Для отображения информации необходимо авторизоваться", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        });







//        prefs = getSharedPreferences("com.example.liferun", MODE_PRIVATE);
//        if (prefs.getBoolean("firstrun", true)){
//            permissionsRationale();
//            prefs.edit().putBoolean("firstrun", false).apply();
//        }
//        else {
//            if (hasOAuthPermission()){
//                readData();
//            }
//            else {
//                Toast.makeText(MainActivity.this,
//                                "Для отображения информации необходимо авторизоваться", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }



    }

    public void setGoals(){
        prefs = getSharedPreferences("com.example.liferun", MODE_PRIVATE);
        // Если первый запуск, установить значения по умолчанию
        if (!prefs.contains("firstrun")){
            stepsGoal = 12000;
            caloriesGoal = 2000;
            hoursGoal = 8;
        }

        stepsGoalInfo = getString(R.string.stepsGoal, stepsGoal);
        caloriesGoalInfo = getString(R.string.caloriesGoal, caloriesGoal);
        hoursGoalInfo = getString(R.string.hoursGoal, hoursGoal);

        saveData();
        setData();
    }

    public void startLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        loadData();
        if (hasOAuthPermission() & allPermissionsGranted()){
            if (isRunning){
                readData();
                setData();
            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        saveData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    public void setData(){
        stepsGoalInfo = getString(R.string.stepsGoal, stepsGoal);
        caloriesGoalInfo = getString(R.string.caloriesGoal, caloriesGoal);
        hoursGoalInfo = getString(R.string.hoursGoal, hoursGoal);

        if (hasOAuthPermission()){
            if (checkSelfPermission(ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                dailyStepsInfo = String.valueOf(dailySteps);
                dailyCaloriesInfo = String.valueOf(dailyCalories);
                dailyDistanceInfo = getString(R.string.distanceCount,
                        String.format(Locale.ENGLISH,"%.2f", dailyDistance));
            }
            else {
                dailyStepsInfo = "???";
                dailyCaloriesInfo = "???";
                dailyDistanceInfo = "???";
            }

            if (checkSelfPermission(BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
                pulseDailyAverageInfo = getString(R.string.pulseDailyAverageCount, pulseDailyAverage);
                pulseLastMeasurementInfo = getString(R.string.pulseLastMeasurementCount, pulseLastMeasurement);
            }
            else {
                pulseDailyAverageInfo = "???";
                pulseLastMeasurementInfo = "???";
            }

            // Форматируем продолжительность стадий сна в виде строки
            deepSleepDurationInfo = formatStageDuration(deepSleepDuration);
            lightSleepDurationInfo = formatStageDuration(lightSleepDuration);
            awakeDurationInfo = formatStageDuration(awakeDuration);
            // Форматируем суммарную продолжительность сна в часах
            sumSleepDurationInfo = formatSumSleepDuration(sumSleepDuration);
        }
        else {
            dailyStepsInfo = "???";
            dailyCaloriesInfo = "???";
            dailyDistanceInfo = "???";
            pulseDailyAverageInfo = "???";
            pulseLastMeasurementInfo = "???";

            deepSleepDurationInfo = "???";
            lightSleepDurationInfo = "???";
            awakeDurationInfo = "???";
            sumSleepDurationInfo = "???";
        }
        MainPage.displayData();

    }

//    private void setData() {
//        if (hasOAuthPermission()){
//            if (checkSelfPermission(ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
//                tv_Steps.setText(String.valueOf(dailySteps));
//                tv_Calories.setText(String.valueOf(dailyCalories));
//                tv_Distance.setText(getString(R.string.distanceCount,
//                        String.format(Locale.ENGLISH,"%.2f", dailyDistance)));
//            }
//            else {
//                tv_Steps.setText("???");
//                tv_Calories.setText("???");
//                tv_Distance.setText("???");
//            }
//
//            if (checkSelfPermission(BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
//                tv_PulseDailyAverage.setText(getString(R.string.pulseDailyAverageCount, pulseDailyAverage));
//                tv_pulseLastMeasurement.setText(getString(R.string.pulseLastMeasurementCount, pulseLastMeasurement));
//            }
//            else {
//                tv_PulseDailyAverage.setText("???");
//                tv_pulseLastMeasurement.setText("???");
//            }
//        }
//        else {
//            tv_Steps.setText("???");
//            tv_Calories.setText("???");
//            tv_Distance.setText("???");
//            tv_PulseDailyAverage.setText("???");
//            tv_pulseLastMeasurement.setText("???");
//        }
//    }



    public void saveData(){
        if (hasOAuthPermission())
            readData();
        prefs = getSharedPreferences("com.example.liferun", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("dailySteps",dailySteps).apply();
        editor.putInt("dailyCalories",dailyCalories).apply();
        editor.putFloat("dailyDistance", dailyDistance).apply();
        editor.putInt("pulseDailyAverage", pulseDailyAverage).apply();
        editor.putInt("pulseLastMeasurement", pulseLastMeasurement).apply();

        editor.putInt("stepsGoal",stepsGoal).apply();
        editor.putInt("caloriesGoal",caloriesGoal).apply();
        editor.putInt("hoursGoal",hoursGoal).apply();

        editor.putLong("deepSleepDuration", deepSleepDuration).apply();
        editor.putLong("lightSleepDuration", lightSleepDuration).apply();
        editor.putLong("awakeDuration", awakeDuration).apply();
        editor.putLong("sumSleepDuration", sumSleepDuration).apply();

        editor.putString("dailyStepsInfo", dailyStepsInfo).apply();
        editor.putString("dailyCaloriesInfo", dailyCaloriesInfo).apply();
        editor.putString("dailyDistanceInfo", dailyDistanceInfo).apply();
        editor.putString("pulseDailyAverageInfo", pulseDailyAverageInfo).apply();
        editor.putString("pulseLastMeasurementInfo", pulseLastMeasurementInfo).apply();

        editor.putString("deepSleepDurationInfo", deepSleepDurationInfo).apply();
        editor.putString("lightSleepDurationInfo", lightSleepDurationInfo).apply();
        editor.putString("awakeDurationInfo", awakeDurationInfo).apply();
        editor.putString("sumSleepDurationInfo", sumSleepDurationInfo).apply();

        editor.putString("stepsGoalInfo", stepsGoalInfo).apply();
        editor.putString("caloriesGoalInfo", caloriesGoalInfo).apply();
        editor.putString("hoursGoalInfo", hoursGoalInfo).apply();

    }

    public void loadData(){
        prefs = getSharedPreferences("com.example.liferun", Context.MODE_PRIVATE);

        dailySteps = prefs.getInt("dailySteps", 0);
        dailyCalories = prefs.getInt("dailyCalories", 0);
        dailyDistance = prefs.getFloat("dailyDistance", 0);
        pulseDailyAverage = prefs.getInt("pulseDailyAverage", 0);
        pulseLastMeasurement = prefs.getInt("pulseLastMeasurement",0);

        stepsGoal = prefs.getInt("stepsGoal",0);
        caloriesGoal = prefs.getInt("caloriesGoal",0);
        hoursGoal = prefs.getInt("hoursGoal",0);

        deepSleepDuration = prefs.getLong("deepSleepDuration", 0);
        lightSleepDuration = prefs.getLong("lightSleepDuration", 0);
        awakeDuration = prefs.getLong("awakeDuration", 0);
        sumSleepDuration = prefs.getLong("sumSleepDuration", 0);


        dailyStepsInfo = prefs.getString("dailyStepsInfo",null);
        dailyCaloriesInfo = prefs.getString("dailyCaloriesInfo",null);
        dailyDistanceInfo = prefs.getString("dailyDistanceInfo",null);
        pulseDailyAverageInfo = prefs.getString("pulseDailyAverageInfo",null);
        pulseLastMeasurementInfo = prefs.getString("pulseLastMeasurementInfo",null);

        deepSleepDurationInfo = prefs.getString("deepSleepDurationInfo", null);
        lightSleepDurationInfo = prefs.getString("lightSleepDurationInfo", null);
        awakeDurationInfo = prefs.getString("awakeDurationInfo", null);
        sumSleepDurationInfo = prefs.getString("sumSleepDurationInfo", null);

        stepsGoalInfo = prefs.getString("stepsGoalInfo", null);
        caloriesGoalInfo = prefs.getString("caloriesGoalInfo", null);
        hoursGoalInfo = prefs.getString("hoursGoalInfo", null);


        setData();
    }




    public void permissionsRationale(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Добро пожаловать!")
                .setMessage("Для работы главной страницы приложения и отображения данных о физической активности необходимо войти в аккаунт Google и предоставить разрешения на обработку данных Google Fit. \nХотите авторизоваться сейчас?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestOAuthPermission();
                        if (!allPermissionsGranted()){
                            requestNeededPermissions();
                        }
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        Toast.makeText(MainActivity.this, "Вы сможете авторизоваться позже в настройках", Toast.LENGTH_LONG).show();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Launches the Google SignIn activity to request OAuth permission for the user.
     * Keep
     */
    private void requestOAuthPermission() {
        fitnessOptions = getFitnessSignInOptions();
        GoogleSignIn.requestPermissions(
                this,
                REQUEST_OAUTH_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions);
    }

    private boolean allPermissionsGranted(){
        int activityPermission = (ContextCompat.checkSelfPermission(this, ACTIVITY_RECOGNITION));
        int sensorsPermission = (ContextCompat.checkSelfPermission(this, BODY_SENSORS));
//        int coarseLocationPermission = (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION));
//        int fineLocationPermission = (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION));

        if ((activityPermission == 0)
                & (sensorsPermission == 0))
//                & (coarseLocationPermission == 0)
//                & (fineLocationPermission == 0))
            return true;
        else return false;

    }

    private void requestNeededPermissions(){
        ActivityCompat.requestPermissions(this,
                new String[]{
                        ACTIVITY_RECOGNITION,
                        BODY_SENSORS,
//                        ACCESS_COARSE_LOCATION,
//                        ACCESS_FINE_LOCATION
                },
                PERMISSION_REQUEST_CODE);
    }

    /**
     * Gets {@link FitnessOptions} in order to check or request OAuth permission for the user.
     * Keep
     */
    private FitnessOptions getFitnessSignInOptions() {
        return FitnessOptions.builder()
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
//                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
                .build();
    }

    /**
     * Checks if user's account has OAuth permission to Fitness API.
     * Keep
     */
    private boolean hasOAuthPermission() {
        fitnessOptions = getFitnessSignInOptions();
        return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions);
    }


    private void readData() {
        lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(this);

        getSleepData();
        getStepsData();
        getCaloriesData();
        getDistanceData();
        getHeartRateData();

        setData();

    }

    private void getSleepData(){
        String[] SLEEP_STAGE_NAMES = new String[]{
                "Unused",
                "Awake (during sleep)",
                "Sleep",
                "Out-of-bed",
                "Light sleep",
                "Deep sleep",
                "REM sleep"

        };

        // Список для хранения названий стадий сна
        ArrayList<String> sleepStageNames = new ArrayList<String>();

        // Список для хранения продолжительности стадий сна
        ArrayList<Long> sleepStageDurations = new ArrayList<Long>();


        Calendar cal = Calendar.getInstance();
        Log.i("Calendar", String.format(cal.toString()));
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        cal.set(year,month,day,23,59,59);
        long endTime = cal.getTimeInMillis(); //end of current day
        Log.i("endTime: ", String.format(String.valueOf(endTime)));
        cal.set(year,month,day-1,18,0,0);
        long startTime = cal.getTimeInMillis(); // 18:00:00.000 of previous day
        Log.i("endTime: ", String.format(String.valueOf(startTime)));

        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .readSessionsFromAllApps()
                .includeSleepSessions()
                .read(DataType.TYPE_SLEEP_SEGMENT)
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getSessionsClient(this, lastSignedInAccount)
                .readSession(readRequest)
                .addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
                    @Override
                    public void onSuccess(SessionReadResponse sessionReadResponse) {
                        for (Session session : sessionReadResponse.getSessions() ) {
                            long sessionStart = session.getStartTime(TimeUnit.MILLISECONDS);
                            long sessionEnd = session.getEndTime(TimeUnit.MILLISECONDS);
                            Log.i("Sleep between: ", String.format("%1$d and %2$d", sessionStart,sessionEnd));

                            // If the sleep session has finer granularity sub-components, extract them:
//                            DataSet dataSets = sessionReadResponse.getDataSet(session);
                            for (DataSet dataSet : sessionReadResponse.getDataSet(session)) {
                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    int sleepStageVal = dp.getValue(Field.FIELD_SLEEP_SEGMENT_TYPE).asInt();
                                    String sleepStage = SLEEP_STAGE_NAMES[sleepStageVal];
                                    long segmentStart = dp.getStartTime(TimeUnit.MILLISECONDS);
                                    long segmentEnd = dp.getEndTime(TimeUnit.MILLISECONDS);

                                    // считаем длительность в миллисекундах
                                    long duration = segmentEnd - segmentStart;

                                    // Добавляем в списки название и продолжительность стадии
                                    sleepStageNames.add(sleepStage);
                                    sleepStageDurations.add(duration);

                                    Log.i("\t* Type ", String.format("%1$s between %2$d and %3$d",
                                            sleepStage, segmentStart, segmentEnd));
                                }
                            }
                            // вызываем метод обработки стадий сна и их продолжительности
                            parseSleepStagesDuration(sleepStageNames, sleepStageDurations);
                            setData();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Read sleep data: ", "FAILED");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<SessionReadResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<SessionReadResponse> task) {
                        Log.i("Sleep session read: ", "Complete");
                    }
                });
    }

    public void parseSleepStagesDuration(ArrayList<String> stages, ArrayList<Long> durations){
        deepSleepDuration = 0;
        lightSleepDuration = 0;
        awakeDuration = 0;
        for (int i = 0; i < stages.size(); i++){
            if (stages.get(i).equals("Deep sleep")){
                deepSleepDuration += durations.get(i);
            }
            if (stages.get(i).equals("Light sleep")){
                lightSleepDuration += durations.get(i);
            }
            if (stages.get(i).equals("Awake (during sleep)")){
                awakeDuration += durations.get(i);
            }
        }
        sumSleepDuration = deepSleepDuration + lightSleepDuration;
    }

    public String formatStageDuration(long durationInMillis){
        Duration duration = Duration.ofMillis(durationInMillis);
        long seconds = duration.getSeconds();
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        if (HH == 0){
            return String.format(Locale.getDefault(),"%2d мин", MM);
        } else if (MM == 0) {
            return String.format(Locale.getDefault(),"%2d ч", HH);
        }
        else return String.format(Locale.getDefault(),"%2d ч %02d мин", HH, MM);
    }

    public String formatSumSleepDuration(long durationInMillis){
        Duration duration = Duration.ofMillis(durationInMillis);
        int seconds = (int)duration.getSeconds();
        float HH = (float)seconds / 3600;
        return String.format(Locale.getDefault(), "%.1f", HH);
    }

    public void getHeartRateData(){
        Calendar cal = Calendar.getInstance();
        Log.i("Calendar", String.format(cal.toString()));
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis(); // current time
        Log.i("endTime: ", String.format(String.valueOf(endTime)));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        cal.set(year,month,day,0,0,0);
        long extraMillis = cal.getTimeInMillis() % 1000;
        long startTime = cal.getTimeInMillis() - extraMillis; // 00:00:00.000 of current day
        Log.i("endTime: ", String.format(String.valueOf(startTime)));


        //read request for reading last pulse measurement
        DataReadRequest readRequest = new DataReadRequest.Builder()
//                .aggregate(DataType.TYPE_HEART_RATE_BPM)
//                .bucketByTime(1, TimeUnit.DAYS)
                .read(DataType.TYPE_HEART_RATE_BPM)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
//                        for (Bucket bucket : dataReadResponse.getBuckets()){
//                            for (DataSet dataSet : bucket.getDataSets()){
//                                dumpDataSet(dataSet);
//                                List<DataPoint> dataPoints = dataSet.getDataPoints();
//                                Log.i("Data points: ", String.valueOf(dataPoints));
//                            }
//                        }
                        DataSet dataSet = dataReadResponse.getDataSet(DataType.TYPE_HEART_RATE_BPM);
                        List<DataPoint> dataPoints = dataSet.getDataPoints();
                        Log.i("Data points: ", String.valueOf(dataPoints));
                        int lastDataPointIndex = dataPoints.size() - 1;
                        float lastPulseMeasurement = dataPoints.isEmpty()
                                ? pulseLastMeasurement
                                : dataPoints.get(lastDataPointIndex).getValue(Field.FIELD_BPM).asFloat();
                        Log.d("Last pulse measurement: ", String.valueOf(lastPulseMeasurement));
                        pulseLastMeasurement = Math.round(lastPulseMeasurement);
                        pulseLastMeasurementInfo = getString(R.string.pulseLastMeasurementCount, pulseLastMeasurement);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Data read failed: ", "Last pulse measurement");
                        showToast("Данные о пульсе не обновлены, попробуте обновить страницу", "short");
                    }
                });

        Fitness.getHistoryClient(this, lastSignedInAccount)
                .readDailyTotal(DataType.TYPE_HEART_RATE_BPM)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        Log.d("Daily average pulse read: ", "Success");
                        float total = dataSet.isEmpty()
                                ? 0
                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_AVERAGE).asFloat();
                        Log.d("Daily average pulse: ", String.valueOf(total));
                        pulseDailyAverage = (total != 0)
                                ? (Math.round(dataSet.getDataPoints().get(0).getValue(Field.FIELD_AVERAGE).asFloat()))
                                : 0;
                        pulseDailyAverageInfo = getString(R.string.pulseDailyAverageCount, pulseDailyAverage);
                        setData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Daily average pulse read: ", "Failure", e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DataSet>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSet> task) {
                        Log.d("Daily average pulse read: ", "Complete");

                    }
                });
    }

    public void getStepsData(){
        Fitness.getHistoryClient(this, lastSignedInAccount)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        Log.d("Status", "Success");
                        long total = dataSet.isEmpty()
                                ? 0
                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                        Log.d("Steps ", String.valueOf(total));
                        dailySteps = (total != 0)
                                ? dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt()
                                : 0;
                        dailyStepsInfo = String.valueOf(dailySteps);
                        setData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Status", "Failure", e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DataSet>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSet> task) {
                        Log.d("Status", "Complete");

                    }
                });
    }

    public void getCaloriesData(){
        Fitness.getHistoryClient(this, lastSignedInAccount)
                .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        Log.d("Status", "Success");
                        float total = dataSet.isEmpty()
                                ? 0
                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat();
                        Log.d("Calories ", String.valueOf(total));
                        dailyCalories = (total != 0)
                                ? Math.round(dataSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat())
                                : 0;
                        dailyCaloriesInfo = String.valueOf(dailyCalories);
                        setData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Status", "Failure", e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DataSet>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSet> task) {
                        Log.d("Status", "Complete");

                    }
                });
    }

    public void getDistanceData(){
        Fitness.getHistoryClient(this, lastSignedInAccount)
                .readDailyTotal(DataType.TYPE_DISTANCE_DELTA)
                .addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        Log.d("Status", "Success");
                        float total = dataSet.isEmpty()
                                ? 0
                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_DISTANCE).asFloat();
                        Log.d("Distance ", String.valueOf(total));
                        dailyDistance = (total != 0)
                                ? (dataSet.getDataPoints().get(0).getValue(Field.FIELD_DISTANCE).asFloat())/1000
                                : 0;
                        dailyDistanceInfo = getString(R.string.distanceCount,
                                String.format(Locale.ENGLISH,"%.2f", dailyDistance));
                        setData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Status", "Failure", e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DataSet>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSet> task) {
                        Log.d("Status", "Complete");

                    }
                });
    }



//    public void dumpDataSet(DataSet dataSet){
//        Log.d("Data returned for Data type: ", String.valueOf(dataSet.getDataType()));
//        for (DataPoint dp : dataSet.getDataPoints()){
//            Log.d("","Data point:");
//            Log.d("\tType: ",String.valueOf(dp.getDataType()));
//            Log.d("\tStart: ",String.valueOf(dp.getStartTime(TimeUnit.MILLISECONDS)));
//            Log.d("\tEnd: ",String.valueOf(dp.getEndTime(TimeUnit.MILLISECONDS)));
//            for (Field field : dp.getDataType().getFields()){
//                Log.d("\tField: ", field.getName());
//                Log.d("Value: ", String.valueOf(dp.getValue(field)));
//            }
//        }
//        float total = dataSet.isEmpty()
//                ? 0
//                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_AVERAGE).asFloat();
//        Log.d("Pulse ", String.valueOf(total));
//        pulseDailyAverage = (total != 0)
//                ? (Math.round(dataSet.getDataPoints().get(0).getValue(Field.FIELD_AVERAGE).asFloat()))
//                : 0;
//        setData();
//    }






//    public void findDataSources(){
//        Fitness.getSensorsClient(getApplicationContext(), GoogleSignIn.getAccountForExtension(getApplicationContext(), fitnessOptions) )
//                .findDataSources(
//                        new DataSourcesRequest.Builder()
//                                .setDataTypes(DataType.TYPE_HEART_RATE_BPM)
//                                .setDataSourceTypes(DataSource.TYPE_RAW)
//                                .build())
//                .addOnSuccessListener(new OnSuccessListener<List<DataSource>>() {
//                    @Override
//                    public void onSuccess(List<DataSource> dataSources) {
//                        dataSources.forEach(dataSource -> {
//                            Log.d("Data source found: ", "${it.streamIdentifier}");
//                            Log.d("Data Source type: ", "${it.dataType.name}");
//
//                            if (dataSource.getDataType() == DataType.TYPE_HEART_RATE_BPM){
//                                Log.d("Data source for TYPE_HEART_RATE_BPM: ","Found");
//                                setSensorListener();
//                            }
//                        });
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d("Find data sources request: ","Failed");
//                    }
//                })
//                .addOnCompleteListener(new OnCompleteListener<List<DataSource>>() {
//                    @Override
//                    public void onComplete(@NonNull Task<List<DataSource>> task) {
//                        Log.d("Complete find sources: ","COMPLETE");
//                    }
//                });
//    }
//
//    public void setSensorListener(){
//        OnDataPointListener listener = dataPoint -> {
//            for (Field field : dataPoint.getDataType().getFields()) {
//                Value value = dataPoint.getValue(field);
//                Log.i("Detected DataPoint field: ", "${field.getName()}");
//                Log.i("Detected DataPoint value: ", "$value");
//            }
//        };
//        Fitness.getSensorsClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
//                .add(
//                        new SensorRequest.Builder()
//                                .setDataType(DataType.TYPE_HEART_RATE_BPM)
//                                .setSamplingRate(10, TimeUnit.SECONDS)
//                                .build(),
//                        listener
//                )
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Log.i("Success: ", "Listener registered!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("Failure: ", "Listener not registered.");
//                    }
//                })
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.d("Complete: ", "set listener");
//                    }
//                });
//
//    }






    public void showToast(String toastText, String duration){
        if (Objects.equals(duration, "short"))
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
    }





    public void startSettingsActivity(View v){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


}