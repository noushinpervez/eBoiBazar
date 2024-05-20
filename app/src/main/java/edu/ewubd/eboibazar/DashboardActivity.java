package edu.ewubd.eboibazar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvCatCount = findViewById(R.id.tvCatCount);
        tvUsersCount = findViewById(R.id.tvUsersCount);
        databaseReferenceCat = FirebaseDatabase.getInstance().getReference("Categories");
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users");

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

        databaseReferenceCat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long categoryCount = snapshot.getChildrenCount();
                tvCatCount.setText(String.valueOf(categoryCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long usersCount = snapshot.getChildrenCount();
                tvUsersCount.setText(String.valueOf(usersCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}