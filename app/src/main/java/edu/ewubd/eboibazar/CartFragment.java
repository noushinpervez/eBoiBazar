package edu.ewubd.eboibazar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private List<Cart> cartList;
    private CustomCartAdapter customCartAdapter;
    private FirebaseUser user;
    private View cartItems, cartEmpty;

    public CartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartItems = view.findViewById(R.id.cartItems);
        cartEmpty = view.findViewById(R.id.cartEmpty);
        ListView lvCart = view.findViewById(R.id.lvCart);
        TextView tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        cartList = new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) fetchCartItems(user.getUid());
        else showEmptyCart();

        customCartAdapter = new CustomCartAdapter(getActivity(), cartList, tvTotalAmount);
        lvCart.setAdapter(customCartAdapter);

        view.findViewById(R.id.btnHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).switchToHomeFragment();
            }
        });

        view.findViewById(R.id.btnPlaceOrder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PlaceOrderActivity.class);
                i.putExtra("cartItems", new ArrayList<>(cartList));
                i.putExtra("totalAmount", Integer.parseInt(tvTotalAmount.getText().toString().replace("৳ ", "").trim()));
                startActivity(i);
            }
        });

        return view;
    }

    private void fetchCartItems(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Cart").child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Cart cart = dataSnapshot.getValue(Cart.class);
                    cartList.add(cart);
                }

                if (cartList.isEmpty()) showEmptyCart();
                else {
                    showCartItems();
                    customCartAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                showEmptyCart();
            }
        });

        databaseReference.keepSynced(true);
    }

    private void showEmptyCart() {
        cartItems.setVisibility(View.GONE);
        cartEmpty.setVisibility(View.VISIBLE);
    }

    private void showCartItems() {
        cartItems.setVisibility(View.VISIBLE);
        cartEmpty.setVisibility(View.GONE);
    }
}