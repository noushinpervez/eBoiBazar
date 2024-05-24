package edu.ewubd.eboibazar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

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
        customCategoryAdapter = new CustomCategoryAdapter(getActivity(), bookCategory);
        categoryDB = new CategoryDB(getActivity());

        lvCategory.setAdapter(customCategoryAdapter);

        loadLocalCategories();

        lvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = bookCategory.get(position);
                Intent i = new Intent(getActivity(), ShowBooksActivity.class);
                i.putExtra("selectedCategory", selectedCategory.getCategory());
                startActivity(i);
            }
        });

        return view;
    }

    private void loadLocalCategories() {
        ArrayList<Category> categories = categoryDB.getAllCategories();

        if (!categories.isEmpty()) {
            bookCategory.clear();
            bookCategory.addAll(categories);
            customCategoryAdapter.notifyDataSetChanged();
        } else Toast.makeText(getActivity(), "No categories available", Toast.LENGTH_LONG).show();
    }
}