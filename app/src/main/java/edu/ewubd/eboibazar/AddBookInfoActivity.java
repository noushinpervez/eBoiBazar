package edu.ewubd.eboibazar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddBookInfoActivity extends AppCompatActivity {

    String[] language = {"Bangla", "English"};
    private EditText etBookName, etAuthor, etPrice, etCopies, etBookLength, etPublication, etEdition, etISBN, etKeywords, etPublishYear, etDescription;
    private RadioButton rbInStock, rbOutOfStock;
    private Spinner spinnerLan;
    private ChipGroup chipCategory;
    private ImageView imgBookCover;
    private LinearProgressIndicator progressIndicator;
    private RadioGroup rgStockStatus;
    private Toolbar toolbar;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_info);

        initialize();
        databaseReference = FirebaseDatabase.getInstance().getReference("Books");
        storageReference = FirebaseStorage.getInstance().getReference("Books");
        CategoryDB categoryDB = new CategoryDB(this);

        ArrayList<Category> categories = categoryDB.getAllCategories();

        if (categories != null && !categories.isEmpty()) {
            for (Category category : categories) {
                Chip chip = new Chip(this);
                chip.setText(category.getCategory());
                chip.setCheckable(true);
                chip.setEnsureMinTouchTargetSize(false);
                chipCategory.addView(chip);
            }
        }

        ArrayAdapter<String> languageAdapter = getArrayAdapter(language, "Select language");
        spinnerLan.setAdapter(languageAdapter);
        spinnerLan.setSelection(0);

        imgBookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddBookInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(AddBookInfoActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                else openGallery();
            }
        });

        findViewById(R.id.btnAddBook).setOnClickListener(new View.OnClickListener() {
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

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Select book cover"), 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) openGallery();
        else Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();
                String image = Base64.encodeToString(bytes, Base64.DEFAULT);
                imgBookCover.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getImageExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void saveData() {
        String bookName = etBookName.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String copiesStr = etCopies.getText().toString().trim();
        String bookLengthStr = etBookLength.getText().toString().trim();
        String publication = etPublication.getText().toString().trim();
        String edition = etEdition.getText().toString().trim();
        String isbn = etISBN.getText().toString().trim();
        String keywords = etKeywords.getText().toString().trim();
        String publishYearStr = etPublishYear.getText().toString().trim();
        String language = spinnerLan.getSelectedItem().toString().trim();
        String description = etDescription.getText().toString().trim();
        String stockStatus = rbInStock.isChecked() ? rbInStock.getText().toString() : rbOutOfStock.getText().toString();

        List<String> selectedCat = new ArrayList<>();
        for (int i = 0; i < chipCategory.getChildCount(); i++) {
            Chip chip = (Chip) chipCategory.getChildAt(i);
            if (chip.isChecked()) selectedCat.add(chip.getText().toString());
        }
        String category = TextUtils.join(", ", selectedCat);

        if (bookName.isEmpty() || author.isEmpty() || category.isEmpty() || uri == null || priceStr.isEmpty() || copiesStr.isEmpty() || bookLengthStr.isEmpty() || publication.isEmpty() || isbn.isEmpty() || keywords.isEmpty() || publishYearStr.isEmpty() || language.isEmpty() || description.isEmpty() || rgStockStatus.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please provide information for required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageExtension = getImageExtension(uri);
        if (imageExtension == null) {
            Toast.makeText(this, "Failed to get image extension", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getImageExtension(uri));

        if (bookName.length() < 4 || bookName.length() > 50) {
            etBookName.setError("Please enter a valid book name (4-50 characters)");
            etBookName.requestFocus();
            return;
        }

        if (author.length() < 4 || author.length() > 30) {
            etAuthor.setError("Please enter a valid author name (4-30 characters)");
            etAuthor.requestFocus();
            return;
        }

        int price = Integer.parseInt(priceStr);
        if (price < 0 || price > 5000) {
            etPrice.setError("Please enter a valid price between 0 and 5000");
            etPrice.requestFocus();
            return;
        }

        int copies = Integer.parseInt(copiesStr);
        if (copies < 1 || copies > 10000) {
            etCopies.setError("Please enter a valid number of copies between 1 and 10000");
            etCopies.requestFocus();
            return;
        }

        int bookLength = Integer.parseInt(bookLengthStr);
        if (bookLength < 10 || bookLength > 10000) {
            etBookLength.setError("Please enter a valid book length between 10 and 10000");
            etBookLength.requestFocus();
            return;
        }

        if (publication.length() < 4 || publication.length() > 50) {
            etPublication.setError("Please enter a valid publication name (4-50 characters)");
            etPublication.requestFocus();
            return;
        }

        if (edition.length() > 20) {
            etEdition.setError("Please enter a valid edition (1-20 characters)");
            etEdition.requestFocus();
            return;
        }

        if (!isbn.matches("\\d{10}|\\d{13}")) {
            etISBN.setError("Please enter a valid ISBN (10 or 13 digits)");
            etISBN.requestFocus();
            return;
        }

        if (!keywords.matches("^[a-zA-Z0-9, ]+$")) {
            etKeywords.setError("Please enter valid keywords (comma separated alphanumeric characters)");
            etKeywords.requestFocus();
            return;
        }

        int publishYear = Integer.parseInt(publishYearStr);
        if (publishYear < 1950 || publishYear > Year.now().getValue()) {
            etPublishYear.setError("Please enter a valid year");
            etPublishYear.requestFocus();
            return;
        }

        if (language.equals("Select language")) {
            Toast.makeText(this, "Please select a language", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.length() < 4 || description.length() > 500) {
            etDescription.setError("Please enter a valid description (4-500 characters)");
            etDescription.requestFocus();
            return;
        }

        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressIndicator.setVisibility(View.GONE);
                Toast.makeText(AddBookInfoActivity.this, "Image uploaded successfully", Toast.LENGTH_LONG).show();

                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUrl = task.getResult();

                            String key = databaseReference.push().getKey();
                            Book book = new Book(bookName, author, category, downloadUrl.toString(), price, copies, bookLength, publication, edition, isbn, keywords, publishYear, language, description, stockStatus);
                            databaseReference.child(key).setValue(book);
                            Toast.makeText(AddBookInfoActivity.this, "Book inserted successfully", Toast.LENGTH_SHORT).show();

                            clear();
                        } else
                            Toast.makeText(AddBookInfoActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddBookInfoActivity.this, "There was an error while uploading image", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressIndicator.setVisibility(View.VISIBLE);
                progressIndicator.setMax(Math.toIntExact(snapshot.getTotalByteCount()));
                progressIndicator.setProgress(Math.toIntExact(snapshot.getBytesTransferred()));
            }
        });
    }

    private ArrayAdapter<String> getArrayAdapter(String[] items, String defaultText) {
        ArrayList<String> itemList = new ArrayList<>();
        itemList.add(defaultText);
        itemList.addAll(Arrays.asList(items));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void clear() {
        etBookName.getText().clear();
        etBookName.clearFocus();
        etAuthor.getText().clear();
        etAuthor.clearFocus();
        chipCategory.clearCheck();
        imgBookCover.setImageResource(R.drawable.library_outline);
        etPrice.getText().clear();
        etPrice.clearFocus();
        etCopies.getText().clear();
        etCopies.clearFocus();
        etBookLength.getText().clear();
        etBookLength.clearFocus();
        etPublication.getText().clear();
        etPublication.clearFocus();
        etEdition.getText().clear();
        etEdition.clearFocus();
        etISBN.getText().clear();
        etISBN.clearFocus();
        etKeywords.getText().clear();
        etKeywords.clearFocus();
        etPublishYear.getText().clear();
        etPublishYear.clearFocus();
        spinnerLan.setSelection(0);
        etDescription.getText().clear();
        etDescription.clearFocus();
        rgStockStatus.clearCheck();
    }

    private void initialize() {
        etBookName = findViewById(R.id.etBookName);
        etAuthor = findViewById(R.id.etAuthor);
        imgBookCover = findViewById(R.id.imgBookCover);
        etPrice = findViewById(R.id.etPrice);
        etCopies = findViewById(R.id.etCopies);
        etBookLength = findViewById(R.id.etBookLength);
        etPublication = findViewById(R.id.etPublication);
        etEdition = findViewById(R.id.etEdition);
        etISBN = findViewById(R.id.etISBN);
        etKeywords = findViewById(R.id.etKeywords);
        etPublishYear = findViewById(R.id.etPublishYear);
        etDescription = findViewById(R.id.etDescription);
        rgStockStatus = findViewById(R.id.rgStockStatus);
        rbInStock = findViewById(R.id.rbInStock);
        rbOutOfStock = findViewById(R.id.rbOutOfStock);
        chipCategory = findViewById(R.id.chipCategory);
        spinnerLan = findViewById(R.id.spinnerLan);
        toolbar = findViewById(R.id.toolbar);
        progressIndicator = findViewById(R.id.progressIndicator);
    }
}