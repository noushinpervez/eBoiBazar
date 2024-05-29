package edu.ewubd.eboibazar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BookDetailActivity extends AppCompatActivity {
    private ArrayList<Book> relatedBooksArrayList;
    private TextView tvBookName, tvAuthor, tvPrice, tvPriceBottom, tvStockStatus, tvBookLength, tvPublication, tvPublishYear, tvISBN, tvEdition, tvLanguage, tvDescription, tvQuantity;
    private Button btnAddToCart;
    private ChipGroup chipCategory, chipKeywords;
    private Cart cart;
    private RecyclerView rvRelatedBooks;
    private CustomBooksAdapter customBooksAdapter;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private ImageView imgBookCover;
    private Toolbar toolbar;
    private Book book;
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        initialize();

        book = (Book) getIntent().getSerializableExtra("book");
        if (book != null) updateUI(book);

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart(book);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addToCart(Book book) {
        if (user != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Cart").child(user.getUid());

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean bookExists = false;
                    boolean updatedQuantity = false;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Cart cartItem = dataSnapshot.getValue(Cart.class);
                        if (cartItem != null && cartItem.getBookName().equals(book.getBookName())) {
                            if (cartItem.getQuantity() != Integer.parseInt(tvQuantity.getText().toString())) {
                                dataSnapshot.getRef().child("quantity").setValue(count);
                                dataSnapshot.getRef().child("totalPrice").setValue(book.getPrice() * count);
                                updatedQuantity = true;
                            }
                            bookExists = true;
                            break;
                        }
                    }

                    if (!bookExists) {
                        String key = databaseReference.push().getKey();
                        cart = new Cart(book.getBookName(), book.getAuthor(), book.getImage(), count, book.getPrice() * count);
                        databaseReference.child(key).setValue(cart);
                        Toast.makeText(BookDetailActivity.this, "Book successfully added to cart", Toast.LENGTH_SHORT).show();
                    } else if (updatedQuantity)
                        Toast.makeText(BookDetailActivity.this, "Quantity updated", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(BookDetailActivity.this, "Book already exists in your cart", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(BookDetailActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(BookDetailActivity.this, "Please sign in to add items to cart", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(BookDetailActivity.this, SignUpActivity.class);
            startActivity(i);
            finish();
        }
        databaseReference.keepSynced(true);
    }

    private void fetchRelatedBooks(ArrayList<Book> relatedBooksArrayList, String[] categoryArr, String[] keywordsArr, String bookISBN) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Books");

        databaseReference.orderByChild("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                relatedBooksArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Book relatedBook = dataSnapshot.getValue(Book.class);
                    if (relatedBook != null && relatedBook.getIsbn() != null && !relatedBook.getIsbn().equals(bookISBN)) {
                        int matchedCategoriesCount = countMatchedCategories(categoryArr, relatedBook.getCategory().split(","));
                        int matchedKeywordsCount = countMatchedKeywords(keywordsArr, relatedBook.getKeywords().split(","));
                        relatedBook.setTotalCount(matchedCategoriesCount + matchedKeywordsCount);
                        relatedBooksArrayList.add(relatedBook);
                    }
                }
                relatedBooksArrayList.sort((b1, b2) -> Integer.compare(b2.getTotalCount(), b1.getTotalCount()));

                customBooksAdapter = new CustomBooksAdapter(BookDetailActivity.this, relatedBooksArrayList);
                rvRelatedBooks.setAdapter(customBooksAdapter);
                customBooksAdapter.notifyDataSetChanged();

                customBooksAdapter.setOnItemClickListener(position -> {
                    Book selectedBook = relatedBooksArrayList.get(position);
                    startActivity(new Intent(BookDetailActivity.this, BookDetailActivity.class).putExtra("book", selectedBook));
                    finish();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookDetailActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        databaseReference.keepSynced(true);
    }

    public int countMatchedCategories(String[] selectedBookCategories, String[] bookCategories) {
        int count = 0;
        for (String category : selectedBookCategories) {
            for (String bookCategory : bookCategories) {
                if (category.trim().equalsIgnoreCase(bookCategory.trim())) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    public int countMatchedKeywords(String[] selectedBookKeywords, String[] bookKeywords) {
        int count = 0;
        for (String keyword : selectedBookKeywords) {
            for (String bookKeyword : bookKeywords) {
                if (keyword.trim().equalsIgnoreCase(bookKeyword.trim())) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private void updateUI(Book book) {
        tvBookName.setText(book.getBookName() != null ? book.getBookName() : "N/A");
        tvAuthor.setText("by " + (book.getAuthor() != null ? book.getAuthor() : "Unknown Author"));
        tvPrice.setText("৳ " + (book.getPrice() != 0 ? book.getPrice() : "N/A"));
        tvStockStatus.setText((book.getStockStatus() != null ? book.getStockStatus() : "N/A") + " (only " + (book.getCopies() != 0 ? book.getCopies() : "N/A") + " copies left)");
        tvBookLength.setText((book.getBookLength() != 0 ? book.getBookLength() : "N/A") + " Pages");
        tvPublication.setText(book.getPublication() != null ? book.getPublication() : "N/A");
        tvPublishYear.setText("Published in " + (book.getPublishYear() != 0 ? book.getPublishYear() : "N/A"));
        tvISBN.setText("ISBN " + (book.getIsbn() != null ? book.getIsbn() : "N/A"));
        tvEdition.setText(book.getEdition() != null ? book.getEdition() : "N/A");
        tvLanguage.setText(book.getLanguage() != null ? book.getLanguage() : "N/A");
        tvDescription.setText(book.getDescription() != null ? book.getDescription() : "N/A");
        tvPriceBottom.setText("৳ " + (book.getPrice() != 0 ? book.getPrice() : "N/A"));
        Glide.with(this).load(book.getImage()).centerCrop().placeholder(R.drawable.library_outline).error(R.drawable.library_outline).into(imgBookCover);

        findViewById(R.id.decrement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count <= 1) return;
                else count--;
                tvQuantity.setText("" + count);
                tvPriceBottom.setText("৳ " + (book.getPrice() != 0 ? book.getPrice() * count : "N/A"));
            }
        });

        findViewById(R.id.increment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                tvQuantity.setText("" + count);
                tvPriceBottom.setText("৳ " + (book.getPrice() != 0 ? book.getPrice() * count : "N/A"));
            }
        });

        String[] categoryArr = (book.getCategory() != null) ? book.getCategory().split(",") : new String[0];
        for (String category : categoryArr) {
            Chip chip = new Chip(this);
            chip.setText(category.trim());
            chip.setEnsureMinTouchTargetSize(false);
            chipCategory.addView(chip);
        }

        String[] keywordsArr = book.getKeywords().split(",");
        for (String keyword : keywordsArr) {
            Chip chip = new Chip(this);
            chip.setText(keyword.trim());
            chip.setEnsureMinTouchTargetSize(false);
            chipKeywords.addView(chip);
        }
        fetchRelatedBooks(relatedBooksArrayList, categoryArr, keywordsArr, book.getIsbn());

        customBooksAdapter = new CustomBooksAdapter(this, new ArrayList<>());
        rvRelatedBooks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRelatedBooks.setAdapter(customBooksAdapter);
    }

    private void initialize() {
        cart = new Cart();
        relatedBooksArrayList = new ArrayList<Book>();
        tvBookName = findViewById(R.id.tvBookName);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPrice = findViewById(R.id.tvPrice);
        tvPriceBottom = findViewById(R.id.tvPriceBottom);
        tvStockStatus = findViewById(R.id.tvStockStatus);
        tvBookLength = findViewById(R.id.tvBookLength);
        tvPublication = findViewById(R.id.tvPublisher);
        tvPublishYear = findViewById(R.id.tvPublishYear);
        tvISBN = findViewById(R.id.tvISBN);
        tvEdition = findViewById(R.id.tvEdition);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvDescription = findViewById(R.id.tvDescription);
        tvQuantity = findViewById(R.id.tvQuantity);
        chipKeywords = findViewById(R.id.chipKeywords);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        chipCategory = findViewById(R.id.chipCategory);
        rvRelatedBooks = findViewById(R.id.rvRelatedBooks);
        imgBookCover = findViewById(R.id.imgBookCover);
        toolbar = findViewById(R.id.toolbar);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }
}