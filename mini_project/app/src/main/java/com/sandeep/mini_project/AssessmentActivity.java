package com.sandeep.mini_project;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AssessmentActivity extends AppCompatActivity implements SensorEventListener {

    private EditText ageEditText, heightEditText, weightEditText;
    private TextView bmiResultTextView, bmiClassificationTextView, stepCountTextView;
    private Button calculateBmiButton;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private boolean isStepCounterSensorAvailable;
    private int initialStepCount = 0;
    private boolean isInitialStepCountSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        // Initialize views
        ageEditText = findViewById(R.id.ageEditText);
        heightEditText = findViewById(R.id.heightEditText);
        weightEditText = findViewById(R.id.weightEditText);
        bmiResultTextView = findViewById(R.id.bmiResultTextView);
        bmiClassificationTextView = findViewById(R.id.bmiClassificationTextView);
        stepCountTextView = findViewById(R.id.stepCountTextView);
        calculateBmiButton = findViewById(R.id.calculateBmiButton);

        // Set up SensorManager and Step Counter
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (stepCounterSensor != null) {
                isStepCounterSensorAvailable = true;
                sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
            } else {
                isStepCounterSensorAvailable = false;
                Toast.makeText(this, "Step Counter Sensor not available!", Toast.LENGTH_SHORT).show();
            }
        }

        // Set up the BMI calculation
        calculateBmiButton.setOnClickListener(v -> calculateBMI());
    }

    private void calculateBMI() {
        String heightStr = heightEditText.getText().toString();
        String weightStr = weightEditText.getText().toString();
        String ageStr = ageEditText.getText().toString();

        if (!heightStr.isEmpty() && !weightStr.isEmpty() && !ageStr.isEmpty()) {
            try {
                float height = Float.parseFloat(heightStr);
                float weight = Float.parseFloat(weightStr);

                // Calculate BMI
                float bmi = weight / (height * height);
                bmiResultTextView.setText("Your BMI: " + String.format("%.2f", bmi));

                // Determine BMI classification
                String classification = getClassification(bmi);
                bmiClassificationTextView.setText("Classification: " + classification);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter age, height, and weight", Toast.LENGTH_SHORT).show();
        }
    }

    private String getClassification(float bmi) {
        if (bmi < 16) {
            return "Severe Thinness";
        } else if (bmi >= 16 && bmi < 17) {
            return "Moderate Thinness";
        } else if (bmi >= 17 && bmi < 18.5) {
            return "Mild Thinness";
        } else if (bmi >= 18.5 && bmi < 25) {
            return "Normal";
        } else if (bmi >= 25 && bmi < 30) {
            return "Overweight";
        } else if (bmi >= 30 && bmi < 35) {
            return "Obese Class I";
        } else if (bmi >= 35 && bmi < 40) {
            return "Obese Class II";
        } else {
            return "Obese Class III";
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (!isInitialStepCountSet) {
                initialStepCount = (int) event.values[0];
                isInitialStepCountSet = true;
            }
            int stepCount = (int) event.values[0] - initialStepCount;
            stepCountTextView.setText("Steps: " + stepCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this example
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isStepCounterSensorAvailable) {
            sensorManager.unregisterListener(this, stepCounterSensor);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStepCounterSensorAvailable && stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }
}
