package edu.ewubd.eboibazar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String SELECTED_FRAGMENT_KEY = "selectedFragment";
    BottomNavigationView bottomNav;
    private int selectedFragmentId = R.id.navHome; // default selected fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // restore selected fragment if activity is recreated
        if (savedInstanceState != null)
            selectedFragmentId = savedInstanceState.getInt(SELECTED_FRAGMENT_KEY);

        // show login toast from sign up activity
        Intent i = getIntent();
        if (i.getBooleanExtra("showToast", false))
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

        bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // update selected fragment
                selectedFragmentId = item.getItemId();

                // load corresponding fragment based on selected item
                if (item.getItemId() == R.id.navHome) loadFragment(new HomeFragment(), false);
                else if (item.getItemId() == R.id.navCategory)
                    loadFragment(new CategoryFragment(), false);
                else if (item.getItemId() == R.id.navCart) loadFragment(new CartFragment(), false);
                else loadFragment(new ProfileFragment(), false);

                return true;
            }
        });

        // load initial fragment or restore selected fragment
        if (savedInstanceState == null) loadFragment(new HomeFragment(), true);
        else bottomNav.setSelectedItemId(selectedFragmentId);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // save selected fragment id
        outState.putInt(SELECTED_FRAGMENT_KEY, selectedFragmentId);
    }

    private void loadFragment(Fragment fragment, boolean isInitial) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (isInitial) fragmentTransaction.add(R.id.frameLayout, fragment);
        else fragmentTransaction.replace(R.id.frameLayout, fragment);

        fragmentTransaction.commit();
    }

    public void switchToHomeFragment() {
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.navHome);
    }
}