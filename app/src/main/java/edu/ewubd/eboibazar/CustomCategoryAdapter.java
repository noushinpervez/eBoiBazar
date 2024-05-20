package edu.ewubd.eboibazar;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomCategoryAdapter extends ArrayAdapter<Category> {

    private final Activity context;
    private final List<Category> bookCategory;

    public CustomCategoryAdapter(Activity context, List<Category> bookCategory) {
        super(context, R.layout.custom_list_category, bookCategory);
        this.context = context;
        this.bookCategory = bookCategory;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_list_category, null, true);

        Category category = bookCategory.get(position);

        TextView tvCategory = view.findViewById(R.id.tvCategory);

        tvCategory.setText(category.getCategory());

        return view;
    }
}