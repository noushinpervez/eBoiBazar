package edu.ewubd.eboibazar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String SELECTED_FRAGMENT_KEY = "selectedFragment";
    BottomNavigationView bottomNav;
    private int selectedFragmentId = R.id.navHome; // default selected fragment
    private DatabaseReference databaseReference;
    private CategoryDB categoryDB;
    private ArrayList<Category> bookCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookCategory = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Categories");
        categoryDB = new CategoryDB(this);

        new LoadCategoriesTask().execute();

        // restore selected fragment if activity is recreated
        if (savedInstanceState != null)
            selectedFragmentId = savedInstanceState.getInt(SELECTED_FRAGMENT_KEY);

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

    private class LoadCategoriesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            loadCategories();
            return null;
        }

        private void loadCategories() {
            databaseReference.orderByChild("category").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    bookCategory.clear();
                    ArrayList<String> categoryKeys = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String key = dataSnapshot.getKey();
                        Category category = dataSnapshot.getValue(Category.class);
                        if (category != null) {
                            category.setKey(key);
                            bookCategory.add(category);
                            categoryDB.addCategory(key, category);
                            categoryKeys.add(key);
                        }
                    }
                    removeDeletedCategories(categoryKeys);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    loadLocalCategories();
                }
            });

            databaseReference.keepSynced(true);
        }

        private void loadLocalCategories() {
            ArrayList<Category> categories = categoryDB.getAllCategories();
            if (!categories.isEmpty()) {
                bookCategory.clear();
                bookCategory.addAll(categories);
            }
        }

        private void removeDeletedCategories(ArrayList<String> categoryKeys) {
            ArrayList<Category> categories = categoryDB.getAllCategories();
            for (Category category : categories) {
                if (!categoryKeys.contains(category.getKey()))
                    categoryDB.deleteCategory(category.getKey());
            }
        }
    }
}