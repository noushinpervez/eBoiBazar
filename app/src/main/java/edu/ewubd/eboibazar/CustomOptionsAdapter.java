package edu.ewubd.eboibazar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomOptionsAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    String[] options;
    int[] optionIcons;

    public CustomOptionsAdapter(Context context, String[] options, int[] optionIcons) {
        this.context = context;
        this.options = options;
        this.optionIcons = optionIcons;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return options.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.custom_list_options, null);

        TextView option = convertView.findViewById(R.id.option);
        ImageView optionIcon = convertView.findViewById(R.id.optionIcon);

        option.setText(options[position]);
        optionIcon.setImageResource(optionIcons[position]);

        return convertView;
    }
}