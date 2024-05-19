package com.example.evproject;

import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

/**
 * MainActivity class is the entry point of the application.
 * It handles the creation of the activity, fragment transactions, and UI setups.
 */
public class MainActivity extends AppCompatActivity {
    private navigationFragment naviFragment;
    private VehicleFragment vehicleFragment;
    private boolean isNavigationFragmentVisible = false;
    private boolean isVehicleFragmentVisible = false;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout naviButton = findViewById(R.id.naviButton);
        LinearLayout vehicleButton = findViewById(R.id.vehicleButton);

        naviFragment = new navigationFragment();
        vehicleFragment = new VehicleFragment();

        naviButton.setOnClickListener(view -> toggleNavigationFragment());
        vehicleButton.setOnClickListener(view -> toggleVehicleFragment());
    }

    /**
     * Toggles the visibility of the navigationFragment.
     * If the fragment is currently visible, it is removed. Otherwise, it is added to the rightLayout.
     */
    private void toggleNavigationFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isNavigationFragmentVisible) {
            transaction.remove(naviFragment);
            isNavigationFragmentVisible = false;
        } else {
            transaction.replace(R.id.rightLayout, naviFragment);
            isNavigationFragmentVisible = true;
        }
        transaction.commit();
    }

    /**
     * Toggles the visibility of the vehicleFragment.
     * If the fragment is currently visible, it is removed. Otherwise, it is added to the leftLayout.
     */
    private void toggleVehicleFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isVehicleFragmentVisible) {
            transaction.remove(vehicleFragment);
            isVehicleFragmentVisible = false;
        } else {
            transaction.replace(R.id.leftLayout, vehicleFragment);
            isVehicleFragmentVisible = true;
        }
        transaction.commit();
    }
}