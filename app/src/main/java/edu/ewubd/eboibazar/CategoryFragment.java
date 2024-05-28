package edu.ewubd.eboibazar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class CategoryFragment extends Fragment {
    private ArrayList<Category> category;
    private CustomCategoryAdapter customCategoryAdapter;
    private CategoryDB categoryDB;
    private DatabaseReference databaseReference;

    public CategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        ListView lvCategory = view.findViewById(R.id.lvCategory);
        category = new ArrayList<>();
        customCategoryAdapter = new CustomCategoryAdapter(getActivity(), category);
        categoryDB = new CategoryDB(getActivity());

        databaseReference = FirebaseDatabase.getInstance().getReference("Categories");

        lvCategory.setAdapter(customCategoryAdapter);

        loadLocalCategories();

        lvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = category.get(position);
                Intent i = new Intent(getActivity(), ShowBooksActivity.class);
                i.putExtra("selectedCategory", selectedCategory.getCategory());
                startActivity(i);
            }
        });

        if (isAdminUser()) {
            lvCategory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Category selectedCategory = category.get(position);
                    showEditDeleteDialog(selectedCategory);
                    return true;
                }
            });
        }

        return view;
    }

    private void loadLocalCategories() {
        ArrayList<Category> categories = categoryDB.getAllCategories();
        if (!categories.isEmpty()) {
            category.clear();
            category.addAll(categories);
            customCategoryAdapter.notifyDataSetChanged();
        } else Toast.makeText(getActivity(), "No categories available", Toast.LENGTH_LONG).show();
    }

    private void showEditDeleteDialog(final Category selectedCategory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        builder.setTitle(selectedCategory.getCategory());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new String[]{"Edit", "Delete"}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setGravity(Gravity.CENTER);
                return view;
            }
        };

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) showEditDialog(selectedCategory);
                else if (which == 1) showDeleteConfirmationDialog(selectedCategory);
            }
        });
        builder.show();
    }

    private void showEditDialog(final Category selectedCategory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        builder.setTitle("EDIT CATEGORY");

        EditText input = new EditText(getActivity());
        input.setText(selectedCategory.getCategory());
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCategory = input.getText().toString().trim();
                if (newCategory.isEmpty() || newCategory.length() < 4 || newCategory.length() > 20 || !newCategory.matches("^[a-zA-Z ]+$"))
                    Toast.makeText(getActivity(), "Invalid category name", Toast.LENGTH_LONG).show();
                else updateCategory(selectedCategory, newCategory);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateCategory(Category selectedCategory, String newCategory) {
        selectedCategory.setCategory(newCategory);
        databaseReference.child(selectedCategory.getKey()).setValue(selectedCategory);
        categoryDB.updateCategory(selectedCategory);
        loadLocalCategories();
        Toast.makeText(getActivity(), "Category updated", Toast.LENGTH_SHORT).show();
    }

    private void deleteCategory(Category selectedCategory) {
        databaseReference.child(selectedCategory.getKey()).removeValue();
        categoryDB.deleteCategory(selectedCategory.getKey());
        loadLocalCategories();
        Toast.makeText(getActivity(), "Category deleted", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog(Category selectedCategory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        builder.setTitle("DELETE CATEGORY");
        builder.setMessage("Are you sure you want to delete " + selectedCategory.getCategory() + "?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCategory(selectedCategory);
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

    private boolean isAdminUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null && (Objects.equals(user.getEmail(), "admineboibazar@gmail.com") || Objects.equals(user.getEmail(), "noushin9136@gmail.com"));
    }
}