package edu.ewubd.eboibazar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderHistoryActivity extends AppCompatActivity {
    private ListView lvOrders;
    private CustomOrderAdapter adapter;
    private List<Orders> orderList;
    private DatabaseReference databaseReference;
    private View historyEmpty;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        lvOrders = findViewById(R.id.lvOrders);
        historyEmpty = findViewById(R.id.historyEmpty);
        Toolbar toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);

        orderList = new ArrayList<>();
        adapter = new CustomOrderAdapter(this, orderList);
        lvOrders.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if ((Objects.equals(user.getEmail(), "admineboibazar@gmail.com") || Objects.equals(user.getEmail(), "noushin9136@gmail.com")))
                loadAllUsersOrderHistory();
            else {
                databaseReference = FirebaseDatabase.getInstance().getReference("Orders").child(user.getUid());
                loadOrderHistory();
            }
        } else {
            Intent i = new Intent(OrderHistoryActivity.this, SignUpActivity.class);
            startActivity(i);
            finish();
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OrderHistoryActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void loadOrderHistory() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Orders order = dataSnapshot.getValue(Orders.class);
                    if (order != null) orderList.add(order);
                }

                if (orderList.isEmpty()) {
                    lvOrders.setVisibility(View.GONE);
                    historyEmpty.setVisibility(View.VISIBLE);
                } else {
                    lvOrders.setVisibility(View.VISIBLE);
                    historyEmpty.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderHistoryActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        databaseReference.keepSynced(true);
    }

    private void loadAllUsersOrderHistory() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference allOrdersRef = FirebaseDatabase.getInstance().getReference("Orders");
        allOrdersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : userSnapshot.getChildren()) {
                        Orders order = orderSnapshot.getValue(Orders.class);
                        if (order != null) orderList.add(order);
                    }
                }

                if (orderList.isEmpty()) {
                    lvOrders.setVisibility(View.GONE);
                    historyEmpty.setVisibility(View.VISIBLE);
                } else {
                    lvOrders.setVisibility(View.VISIBLE);
                    historyEmpty.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderHistoryActivity.this, "Failed to load all orders", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        allOrdersRef.keepSynced(true);
    }
}