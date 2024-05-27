package edu.ewubd.eboibazar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomOrderAdapter extends BaseAdapter {
    private final Context context;
    private final List<Orders> orderList;
    private TextView tvBookName, tvOrderedOn, tvStatus, tvPhone, tvAddress, tvQuantity, tvTotalPrice;

    public CustomOrderAdapter(Context context, List<Orders> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.custom_order_list, parent, false);

        Orders order = orderList.get(position);

        tvBookName = convertView.findViewById(R.id.tvBookName);
        tvOrderedOn = convertView.findViewById(R.id.tvOrderedOn);
        tvStatus = convertView.findViewById(R.id.tvStatus);
        tvPhone = convertView.findViewById(R.id.tvPhone);
        tvAddress = convertView.findViewById(R.id.tvAddress);
        tvQuantity = convertView.findViewById(R.id.tvQuantity);
        tvTotalPrice = convertView.findViewById(R.id.tvTotalPrice);

        StringBuilder bookNamesBuilder = new StringBuilder();
        int totalQuantity = 0;
        for (Cart cart : order.getCartItems()) {
            bookNamesBuilder.append(cart.getBookName()).append(", ");
            totalQuantity += cart.getQuantity();
        }
        String bookNames = bookNamesBuilder.length() > 0 ? bookNamesBuilder.substring(0, bookNamesBuilder.length() - 2) : "";

        tvBookName.setText(bookNames);
        tvOrderedOn.setText(order.getCurDate() + " " + order.getCurTime());
        tvStatus.setText(order.getStatus());

        UserAddress address = order.getUserAddress().get(0);
        tvPhone.setText(address.getPhone());
        tvAddress.setText(address.getAddress());
        tvQuantity.setText(String.valueOf(totalQuantity));
        tvTotalPrice.setText("৳ " + order.getTotalAmount());

        return convertView;
    }
}