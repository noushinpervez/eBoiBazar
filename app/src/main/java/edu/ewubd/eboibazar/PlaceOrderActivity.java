package edu.ewubd.eboibazar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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

public class PlaceOrderActivity extends AppCompatActivity {
    private EditText etAddress, etPhone;
    private RadioButton rbHome;
    private RadioGroup rgbReceive;
    private LinearLayout formLayout, addressLayout;
    private TextView tvAddress, tvPhone, tvReceive, tvTotal, tvShipping, tvPayable;
    private DatabaseReference databaseReference;
    private ArrayList<Cart> cartList;
    private ArrayList<UserAddress> addressList;
    private UserAddress userAddress;
    private Button btnPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        rgbReceive = findViewById(R.id.rgReceive);
        rbHome = findViewById(R.id.rbHome);
        formLayout = findViewById(R.id.formLayout);
        addressLayout = findViewById(R.id.addressLayout);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvReceive = findViewById(R.id.tvReceive);
        tvTotal = findViewById(R.id.tvTotal);
        tvShipping = findViewById(R.id.tvShipping);
        tvPayable = findViewById(R.id.tvPayable);
        btnPayment = findViewById(R.id.btnPayment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        addressList = new ArrayList<UserAddress>();

        cartList = (ArrayList<Cart>) getIntent().getSerializableExtra("cartItems");
        int totalAmount = getIntent().getIntExtra("totalAmount", 0);

        int payable = 50 + totalAmount;

        tvTotal.setText("৳ " + totalAmount);
        tvShipping.setText("৳ " + 50);
        tvPayable.setText("৳ " + payable);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("UserAddresses").child(user.getUid());

        showSavedAddress();

        findViewById(R.id.btnAddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAddress();
            }
        });

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addressList.isEmpty()) {
                    Intent i = new Intent(PlaceOrderActivity.this, PaymentActivity.class);
                    i.putExtra("cartItems", new ArrayList<>(cartList));
                    i.putExtra("totalAmount", totalAmount);
                    i.putExtra("userAddress", addressList);
                    startActivity(i);
                } else
                    Toast.makeText(PlaceOrderActivity.this, "Please provide your address before proceeding to payment", Toast.LENGTH_LONG).show();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveAddress() {
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String receivingFrom = rbHome.isChecked() ? "Home" : "Office";

        if (address.isEmpty() || phone.isEmpty() || rgbReceive.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please provide information for required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (address.length() < 4 || address.length() > 100) {
            etAddress.setError("Please enter a valid address (4-100 characters)");
            etAddress.requestFocus();
            return;
        }

        if (!((phone.startsWith("+880") && phone.length() == 14) || (phone.startsWith("880") && phone.length() == 13) || (phone.startsWith("01") && phone.length() == 11))) {
            etPhone.setError("Please enter a valid phone number");
            etPhone.requestFocus();
            return;
        }

        String key = databaseReference.push().getKey();
        userAddress = new UserAddress(address, phone, receivingFrom);
        databaseReference.child(key).setValue(userAddress);
        addressList.add(userAddress);

        tvAddress.setText(address);
        tvPhone.setText(phone);
        tvReceive.setText(receivingFrom);

        formLayout.setVisibility(View.GONE);
        addressLayout.setVisibility(View.VISIBLE);
    }

    private void showSavedAddress() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userAddress = dataSnapshot.getValue(UserAddress.class);
                    if (userAddress != null) {
                        addressList.add(userAddress);
                        tvAddress.setText(userAddress.getAddress());
                        tvPhone.setText(userAddress.getPhone());
                        tvReceive.setText(userAddress.getReceivingFrom());

                        formLayout.setVisibility(View.GONE);
                        addressLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PlaceOrderActivity.this, "Failed to load address", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.keepSynced(true);
    }
}