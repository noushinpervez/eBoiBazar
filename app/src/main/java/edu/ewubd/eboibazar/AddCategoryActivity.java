package edu.ewubd.eboibazar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddCategoryActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private TextInputLayout textCat;
    private EditText etCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        etCategory = findViewById(R.id.etCategory);
        textCat = findViewById(R.id.category);
        Toolbar toolbar = findViewById(R.id.toolbar);

        databaseReference = FirebaseDatabase.getInstance().getReference("Categories");

        addTextWatchers();

        findViewById(R.id.btnAddCat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategory();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveCategory() {
        String category = etCategory.getText().toString().trim();

        if (category.isEmpty() || category.length() < 4 || category.length() > 20 || !category.matches("^[a-zA-Z ]+$")) {
            textCat.setError("Please enter a valid category (4-20 letters and spaces only)");
            etCategory.requestFocus();
            return;
        }

        databaseReference.orderByChild("category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    textCat.setError("Category already exists");
                    etCategory.requestFocus();
                } else {
                    String key = databaseReference.push().getKey();
                    Category bookCategory = new Category(key, category);
                    databaseReference.child(key).setValue(bookCategory);
                    Toast.makeText(AddCategoryActivity.this, "Book category inserted successfully", Toast.LENGTH_SHORT).show();
                    databaseReference.keepSynced(true);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddCategoryActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTextWatchers() {
        TextWatcher clearErrorTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textCat.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        etCategory.addTextChangedListener(clearErrorTextWatcher);
    }
}