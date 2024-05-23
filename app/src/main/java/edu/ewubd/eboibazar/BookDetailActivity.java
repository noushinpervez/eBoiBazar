package edu.ewubd.eboibazar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class BookDetailActivity extends AppCompatActivity {

    private TextView tvBookName, tvAuthor, tvPrice, tvPriceBottom, tvStockStatus, tvBookLength, tvPublication, tvPublishYear, tvISBN, tvEdition, tvLanguage, tvDescription;
    private Button btnAddToCart;
    private ChipGroup chipCategory, chipKeywords;
    private Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Book book = (Book) getIntent().getSerializableExtra("book");

        cart = new Cart();

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
        chipKeywords = findViewById(R.id.chipKeywords);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        chipCategory = findViewById(R.id.chipCategory);
        ImageView imgBookCover = findViewById(R.id.imgBookCover);
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (book != null) {
            tvBookName.setText(book.getBookName() != null ? book.getBookName() : "N/A");
            tvAuthor.setText("by " + (book.getAuthor() != null ? book.getAuthor() : "Unknown Author"));
            tvPrice.setText("৳ " + (book.getPrice() != 0 ? book.getPrice() : "N/A"));
            tvPriceBottom.setText("৳ " + (book.getPrice() != 0 ? book.getPrice() : "N/A"));
            tvStockStatus.setText((book.getStockStatus() != null ? book.getStockStatus() : "N/A") + " (only " + (book.getCopies() != 0 ? book.getCopies() : "N/A") + " copies left)");
            tvBookLength.setText((book.getBookLength() != 0 ? book.getBookLength() : "N/A") + " Pages");
            tvPublication.setText(book.getPublication() != null ? book.getPublication() : "N/A");
            tvPublishYear.setText("Published in " + (book.getPublishYear() != 0 ? book.getPublishYear() : "N/A"));
            tvISBN.setText("ISBN " + (book.getIsbn() != null ? book.getIsbn() : "N/A"));
            tvEdition.setText(book.getEdition() != null ? book.getEdition() : "N/A");
            tvLanguage.setText(book.getLanguage() != null ? book.getLanguage() : "N/A");
            tvDescription.setText(book.getDescription() != null ? book.getDescription() : "N/A");
            Glide.with(this).load(book.getImage()).centerCrop().placeholder(R.drawable.library_outline).error(R.drawable.library_outline).into(imgBookCover);

            String[] categoryArr = book.getCategory().split(",");
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
        }

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!cart.getItems().contains(book)) {
                    cart.addItem(book);
                    btnAddToCart.setText("Added to Cart");
                    Toast.makeText(BookDetailActivity.this, "Book successfully added to cart", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(BookDetailActivity.this, "Book is already in the cart", Toast.LENGTH_SHORT).show();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}