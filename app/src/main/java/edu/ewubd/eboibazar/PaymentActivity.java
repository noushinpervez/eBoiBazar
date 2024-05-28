package edu.ewubd.eboibazar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class PaymentActivity extends AppCompatActivity {
    private FirebaseUser user;
    private DatabaseReference cartReference, orderReference;
    private Button btnOrder;
    private ArrayList<Cart> cartList;
    private ArrayList<UserAddress> address;
    private TextView tvTotal, tvShipping, tvPayable;
    private LinearLayout completedLayout, paymentLayout;
    private int totalAmount, payable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        btnOrder = findViewById(R.id.btnOrder);
        completedLayout = findViewById(R.id.completedLayout);
        paymentLayout = findViewById(R.id.paymentLayout);
        tvTotal = findViewById(R.id.tvTotal);
        tvShipping = findViewById(R.id.tvShipping);
        tvPayable = findViewById(R.id.tvPayable);
        Toolbar toolbar = findViewById(R.id.toolbar);

        user = FirebaseAuth.getInstance().getCurrentUser();
        orderReference = FirebaseDatabase.getInstance().getReference("Orders").child(user.getUid());
        cartReference = FirebaseDatabase.getInstance().getReference("Cart").child(user.getUid());

        cartList = (ArrayList<Cart>) getIntent().getSerializableExtra("cartItems");
        address = (ArrayList<UserAddress>) getIntent().getSerializableExtra("userAddress");
        totalAmount = getIntent().getIntExtra("totalAmount", 0);

        payable = 50 + totalAmount;

        tvTotal.setText("৳ " + totalAmount);
        tvShipping.setText("৳ " + 50);
        tvPayable.setText("৳ " + payable);

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOrder();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PaymentActivity.this, OrderHistoryActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void saveOrder() {
        if (user != null) {
            String curDateStr, curTimeStr;
            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat curDate = new SimpleDateFormat("dd MMMM yyyy");
            curDateStr = curDate.format(calendar.getTime());
            SimpleDateFormat curTime = new SimpleDateFormat("HH:mm:ss aa");
            curTimeStr = curTime.format(calendar.getTime());

            Orders orders = new Orders(curDateStr, curTimeStr, cartList, address, payable, "Pending");

            String key = orderReference.push().getKey();

            orderReference.child(key).setValue(orders).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    btnOrder.setVisibility(View.GONE);
                    paymentLayout.setVisibility(View.GONE);
                    completedLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(PaymentActivity.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                    clearCart(user.getUid());
                } else
                    Toast.makeText(PaymentActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
            });

            orderReference.keepSynced(true);
        }
    }

    private void clearCart(String userId) {
        cartReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful())
                Toast.makeText(PaymentActivity.this, "Cart cleared", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(PaymentActivity.this, "Failed to clear cart", Toast.LENGTH_LONG).show();

            cartReference.keepSynced(true);
        });
    }
}