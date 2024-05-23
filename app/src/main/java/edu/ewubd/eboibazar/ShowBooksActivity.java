package edu.ewubd.eboibazar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShowBooksActivity extends AppCompatActivity {

    private RecyclerView rvAllBooks;
    private CustomAllBooksAdapter customAllBooksAdapter;
    private ArrayList<Book> bookArrayList;
    private String title;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_books);

        rvAllBooks = findViewById(R.id.rvAllBooks);
        rvAllBooks.setLayoutManager(new GridLayoutManager(this, 2));
        rvAllBooks.setHasFixedSize(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);

        Intent i = getIntent();
        if (i != null && i.hasExtra("bookArrayList")) {
            bookArrayList = (ArrayList<Book>) i.getSerializableExtra("bookArrayList");
            customAllBooksAdapter = new CustomAllBooksAdapter(this, bookArrayList);
            rvAllBooks.setAdapter(customAllBooksAdapter);

            title = i.getStringExtra("title");
            if (title != null) toolbarTitle.setText(title);
        }

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
}