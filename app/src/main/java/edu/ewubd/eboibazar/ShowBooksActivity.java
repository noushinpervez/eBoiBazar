package edu.ewubd.eboibazar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowBooksActivity extends AppCompatActivity {

    private RecyclerView rvAllBooks;
    private CustomAllBooksAdapter customAllBooksAdapter;
    private ArrayList<Book> bookArrayList, filteredBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_books);

        rvAllBooks = findViewById(R.id.rvAllBooks);
        rvAllBooks.setLayoutManager(new GridLayoutManager(this, 2));
        rvAllBooks.setHasFixedSize(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        SearchView searchView = findViewById(R.id.searchView);
        searchView.clearFocus();

        Intent i = getIntent();
        if (i != null && i.hasExtra("bookArrayList")) {
            bookArrayList = (ArrayList<Book>) i.getSerializableExtra("bookArrayList");
            customAllBooksAdapter = new CustomAllBooksAdapter(this, bookArrayList);
            rvAllBooks.setAdapter(customAllBooksAdapter);

            String title = i.getStringExtra("title");
            if (title != null) toolbarTitle.setText(title);
        } else customAllBooksAdapter = new CustomAllBooksAdapter(this, new ArrayList<>());

        if (i != null && i.hasExtra("selectedCategory")) {
            String selectedCategory = i.getStringExtra("selectedCategory");
            toolbarTitle.setText(selectedCategory);
            fetchBooksByCategory(selectedCategory);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });

        customAllBooksAdapter.setOnItemClickListener(new CustomAllBooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Book selectedBook = bookArrayList.get(position);
                Intent i = new Intent(ShowBooksActivity.this, BookDetailActivity.class);
                i.putExtra("book", selectedBook);
                startActivity(i);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void filterBooks(String text) {
        ArrayList<Book> filteredBooks = new ArrayList<>();

        for (Book book : bookArrayList) {
            if (book.getBookName().toLowerCase().contains(text.toLowerCase()) || book.getAuthor().toLowerCase().contains(text.toLowerCase()) || book.getCategory().toLowerCase().contains(text.toLowerCase()))
                filteredBooks.add(book);
        }

        customAllBooksAdapter.setFilteredList(filteredBooks);
    }

    private void fetchBooksByCategory(String category) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Books");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                filteredBooks = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Book book = dataSnapshot.getValue(Book.class);
                    String[] categories = book.getCategory().split(",");

                    for (String cat : categories) {
                        if (cat.trim().equalsIgnoreCase(category)) {
                            filteredBooks.add(book);
                            break;
                        }
                    }
                }

                customAllBooksAdapter = new CustomAllBooksAdapter(ShowBooksActivity.this, filteredBooks);
                rvAllBooks.setAdapter(customAllBooksAdapter);

                customAllBooksAdapter.setOnItemClickListener(new CustomAllBooksAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Book selectedBook = filteredBooks.get(position);
                        Intent i = new Intent(ShowBooksActivity.this, BookDetailActivity.class);
                        i.putExtra("book", selectedBook);
                        startActivity(i);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowBooksActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        databaseReference.keepSynced(true);
    }
}