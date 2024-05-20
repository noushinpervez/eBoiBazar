package edu.ewubd.eboibazar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    DatabaseReference databaseReferenceCat, databaseReferenceUsers;
    private TextView tvCatCount, tvUsersCount;
    private SharedPreferences sp;
    private boolean dataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvCatCount = findViewById(R.id.tvCatCount);
        tvUsersCount = findViewById(R.id.tvUsersCount);
        databaseReferenceCat = FirebaseDatabase.getInstance().getReference("Categories");
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        sp = this.getSharedPreferences("dashboard_info", MODE_PRIVATE);

        // load data from local to prevent delay in displaying counts
        loadLocalCounts();

        // ensures data is loaded only once during the lifetime of the activity
        if (!dataLoaded) loadData();

        findViewById(R.id.cardAddBook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, AddBookInfoActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.cardAddCat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, AddCategoryActivity.class);
                startActivity(i);
            }
        });
    }

    private void loadData() {
        databaseReferenceCat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long categoryCount = snapshot.getChildrenCount();
                tvCatCount.setText(String.valueOf(categoryCount));
                saveCountToLocal("CATEGORY_COUNT", categoryCount);
                dataLoaded = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "You are offline. Check your connection", Toast.LENGTH_LONG).show();
            }
        });

        databaseReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long usersCount = snapshot.getChildrenCount();
                tvUsersCount.setText(String.valueOf(usersCount));
                saveCountToLocal("USER_COUNT", usersCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "You are offline. Check your connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadLocalCounts() {
        long categoryCount = sp.getLong("CATEGORY_COUNT", 0);
        long userCount = sp.getLong("USER_COUNT", 0);

        tvCatCount.setText(String.valueOf(categoryCount));
        tvUsersCount.setText(String.valueOf(userCount));
    }

    private void saveCountToLocal(String key, long count) {
        SharedPreferences.Editor e = sp.edit();
        e.putLong(key, count);
        e.apply();
    }
}