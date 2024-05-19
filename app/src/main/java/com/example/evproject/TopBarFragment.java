package com.example.evproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * TopBarFragment displays the top bar of the application containing battery level and temperature information.
 * It implements SensorEventListener to listen for temperature sensor events.
 */
public class TopBarFragment extends Fragment implements SensorEventListener {

    private ProgressBar batteryProgressBar;
    private TextView batteryPercentageTextView;
    private TextView temperatureTextView;
    private SensorManager sensorManager;
    private Sensor temperatureSensor;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_bar, container, false);

        batteryProgressBar = rootView.findViewById(R.id.batteryProgressBar);
        batteryPercentageTextView = rootView.findViewById(R.id.batteryPercentageTextView);
        temperatureTextView = rootView.findViewById(R.id.temperatureText);

        updateBatteryLevel();
        setupTemperatureSensor();

        return rootView;
    }

    /**
     * Updates the battery level progress bar and text view.
     */
    @SuppressLint("SetTextI18n")
    private void updateBatteryLevel() {
        Context context = getContext();
        if (context == null) return;

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, filter);

        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level / (float) scale) * 100);

            batteryProgressBar.setProgress(batteryPct);
            batteryPercentageTextView.setText(batteryPct + "%");
        }
    }

    /**
     * Sets up the temperature sensor.
     */
    @SuppressLint("SetTextI18n")
    private void setupTemperatureSensor() {
        Context context = getContext();
        if (context == null) return;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            temperatureTextView.setText("N/A");
        }
    }

    /**
     * Called when sensor values have changed.
     *
     * @param event The SensorEvent object containing sensor data.
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            float temperature = event.values[0];
            temperatureTextView.setText(String.format("%.1f °C", temperature));
        }
    }

    /**
     * Called when the accuracy of a sensor has changed.
     *
     * @param sensor   The Sensor object.
     * @param accuracy The new accuracy of this sensor.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this example
    }

    /**
     * Called when the fragment is no longer resumed.
     * Unregisters the temperature sensor listener.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null && temperatureSensor != null) {
            sensorManager.unregisterListener(this, temperatureSensor);
        }
    }

    /**
     * Called when the fragment is visible and actively running.
     * Registers the temperature sensor listener.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null && temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
}