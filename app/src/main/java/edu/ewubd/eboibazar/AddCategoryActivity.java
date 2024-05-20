package edu.ewubd.eboibazar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCategoryActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    TextInputLayout textCat;
    private EditText etCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        Toolbar toolbar = findViewById(R.id.toolbar);

        databaseReference = FirebaseDatabase.getInstance().getReference("Categories");
        etCategory = findViewById(R.id.etCategory);
        textCat = findViewById(R.id.category);

        findViewById(R.id.btnAddCat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveData() {
        String category = etCategory.getText().toString().trim();

        if (category.isEmpty() || category.length() > 30) {
            textCat.setError("Please enter a valid category (Bangla characters only, 1-50 characters)");
            etCategory.requestFocus();
            return;
        }

        String key = databaseReference.push().getKey();

        Category bookCategory = new Category(key, category);

        databaseReference.child(key).setValue(bookCategory);
        Toast.makeText(AddCategoryActivity.this, "Book category inserted", Toast.LENGTH_SHORT).show();

        etCategory.getText().clear();
        etCategory.clearFocus();
    }
}