package edu.ewubd.eboibazar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    DatabaseReference databaseReference;
    private ArrayList<Category> bookCategory;
    private CustomCategoryAdapter customCategoryAdapter;
    private CategoryDB categoryDB;

    public CategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        ListView lvCategory = view.findViewById(R.id.lvCategory);
        bookCategory = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Categories");
        customCategoryAdapter = new CustomCategoryAdapter(getActivity(), bookCategory);
        categoryDB = new CategoryDB(getActivity());

        loadLocalCategories();
        loadData();

        lvCategory.setAdapter(customCategoryAdapter);

        return view;
    }

    private void loadData() {
        databaseReference.orderByChild("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookCategory.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    Category category = dataSnapshot.getValue(Category.class);
                    category.setKey(key);

                    bookCategory.add(category);
                    categoryDB.addCategory(key, category);
                }

                customCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadLocalCategories() {
        ArrayList<Category> categories = categoryDB.getAllCategories();

        if (!categories.isEmpty()) {
            bookCategory.clear();
            bookCategory.addAll(categories);
            customCategoryAdapter.notifyDataSetChanged();
        }
    }
}