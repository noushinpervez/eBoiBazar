package edu.ewubd.eboibazar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomBooksAdapter extends RecyclerView.Adapter<CustomBooksAdapter.BookViewHolder> {

    private final Context context;
    private final ArrayList<Book> bookArrayList;

    public CustomBooksAdapter(Context context, ArrayList<Book> bookArrayList) {
        this.context = context;
        this.bookArrayList = bookArrayList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.custom_book_card, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookArrayList.get(position);
        holder.tvBookName.setText(book.getBookName());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvPrice.setText(String.valueOf(book.getPrice()));
        Picasso.get().load(book.getImage()).fit().centerCrop().into(holder.imgBookCover);
    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {

        TextView tvBookName, tvAuthor, tvPrice;
        ImageView imgBookCover;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBookName = itemView.findViewById(R.id.tvBookName);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imgBookCover = itemView.findViewById(R.id.imgBookCover);
        }
    }
}