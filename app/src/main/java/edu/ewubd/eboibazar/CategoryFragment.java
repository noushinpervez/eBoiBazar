package edu.ewubd.eboibazar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    DatabaseReference databaseReference;
    private List<Category> bookCategory;
    private CustomCategoryAdapter customCategoryAdapter;

    public CategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        ListView lvCategory = view.findViewById(R.id.lvCategory);
        databaseReference = FirebaseDatabase.getInstance().getReference("Categories");
        bookCategory = new ArrayList<>();
        customCategoryAdapter = new CustomCategoryAdapter(getActivity(), bookCategory);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookCategory.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    bookCategory.add(category);
                }

                customCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        lvCategory.setAdapter(customCategoryAdapter);

        return view;
    }
}