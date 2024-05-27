package edu.ewubd.eboibazar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CustomCartAdapter extends ArrayAdapter<Cart> {
    Activity context;
    List<Cart> bookCart;
    TextView tvBookName, tvAuthor, tvTotalPrice, tvQuantity;
    ImageView imgBookCover;
    FirebaseUser user;
    TextView tvTotalAmount;

    public CustomCartAdapter(Activity context, List<Cart> bookCart, TextView tvTotalAmount) {
        super(context, R.layout.custom_list_cart, bookCart);
        this.context = context;
        this.bookCart = bookCart;
        this.tvTotalAmount = tvTotalAmount;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        view = layoutInflater.inflate(R.layout.custom_list_cart, null, true);

        user = FirebaseAuth.getInstance().getCurrentUser();

        Cart cart = bookCart.get(position);
        tvBookName = view.findViewById(R.id.tvBookName);
        tvAuthor = view.findViewById(R.id.tvAuthor);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        imgBookCover = view.findViewById(R.id.imgBookCover);

        tvBookName.setText(cart.getBookName());
        tvAuthor.setText(cart.getAuthor());
        Glide.with(context).load(cart.getImage()).centerCrop().placeholder(R.drawable.library_outline).error(R.drawable.library_outline).into(imgBookCover);
        tvTotalPrice.setText("৳ " + cart.getTotalPrice());
        tvQuantity.setText("Qty: " + cart.getQuantity());
        updateTotalPrice();

        view.findViewById(R.id.imgDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Cart").child(user.getUid());

                databaseReference.orderByChild("bookName").equalTo(cart.getBookName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
                            builder.setTitle("Remove from cart?");
                            builder.setMessage("Are you sure you would like to remove this item from the cart?");

                            builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bookCart.remove(position);
                                    notifyDataSetChanged();
                                    dataSnapshot.getRef().removeValue();
                                    updateTotalPrice();
                                    Toast.makeText(context, cart.getBookName() + "removed", Toast.LENGTH_LONG).show();
                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Delete unsuccessful", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return view;
    }

    private void updateTotalPrice() {
        int totalAmount = 0;
        for (Cart cart : bookCart) totalAmount += cart.getTotalPrice();

        tvTotalAmount.setText("৳ " + totalAmount);
    }
}