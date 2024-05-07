package com.example.evproject;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private navigationFragment naviFragment;
    private boolean isNavigationFragmentVisible = false;

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

        naviFragment = new navigationFragment();
        naviButton.setOnClickListener(view -> toggleNavigationFragment());
    }

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
}
