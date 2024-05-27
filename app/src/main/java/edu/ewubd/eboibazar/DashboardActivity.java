package edu.ewubd.eboibazar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {
    private DatabaseReference databaseReferenceCat, databaseReferenceUsers, databaseReferenceBooks, databaseReferenceOrders;
    private TextView tvCatCount, tvUsersCount, tvBooksCount, tvOrdersCount;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvCatCount = findViewById(R.id.tvCatCount);
        tvUsersCount = findViewById(R.id.tvUsersCount);
        tvBooksCount = findViewById(R.id.tvBooksCount);
        tvOrdersCount = findViewById(R.id.tvOrdersCount);
        Toolbar toolbar = findViewById(R.id.toolbar);
        databaseReferenceCat = FirebaseDatabase.getInstance().getReference("Categories");
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        databaseReferenceBooks = FirebaseDatabase.getInstance().getReference("Books");
        databaseReferenceOrders = FirebaseDatabase.getInstance().getReference("Orders");
        sp = this.getSharedPreferences("dashboard_info", MODE_PRIVATE);

        // load data from local to prevent delay in displaying counts
        loadLocalCounts();

        loadData();

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

        findViewById(R.id.btnOrder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, OrderHistoryActivity.class);
                startActivity(i);
                finish();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadData() {
        databaseReferenceCat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long categoryCount = snapshot.getChildrenCount();
                tvCatCount.setText(String.valueOf(categoryCount));
                saveCountToLocal("CATEGORY_COUNT", categoryCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Database error" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long usersCount = snapshot.getChildrenCount();
                tvUsersCount.setText(String.valueOf(usersCount));
                saveCountToLocal("USER_COUNT", usersCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Database error" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        databaseReferenceBooks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long booksCount = snapshot.getChildrenCount();
                tvBooksCount.setText(String.valueOf(booksCount));
                saveCountToLocal("BOOK_COUNT", booksCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Database error" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        databaseReferenceOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long ordersCount = 0;
                for (DataSnapshot userOrdersSnapshot : snapshot.getChildren())
                    ordersCount += userOrdersSnapshot.getChildrenCount();

                tvOrdersCount.setText(String.valueOf(ordersCount));
                saveCountToLocal("ORDERS_COUNT", ordersCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        databaseReferenceCat.keepSynced(true);
        databaseReferenceUsers.keepSynced(true);
        databaseReferenceBooks.keepSynced(true);
        databaseReferenceOrders.keepSynced(true);
    }

    private void loadLocalCounts() {
        long categoryCount = sp.getLong("CATEGORY_COUNT", 0);
        long userCount = sp.getLong("USER_COUNT", 0);
        long bookCount = sp.getLong("BOOK_COUNT", 0);
        long orderCount = sp.getLong("ORDERS_COUNT", 0);

        tvCatCount.setText(String.valueOf(categoryCount));
        tvUsersCount.setText(String.valueOf(userCount));
        tvBooksCount.setText(String.valueOf(bookCount));
        tvOrdersCount.setText(String.valueOf(orderCount));
    }

    private void saveCountToLocal(String key, long count) {
        SharedPreferences.Editor e = sp.edit();
        e.putLong(key, count);
        e.apply();
    }
}