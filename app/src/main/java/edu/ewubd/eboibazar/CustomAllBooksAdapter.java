package edu.ewubd.eboibazar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomAllBooksAdapter extends RecyclerView.Adapter<CustomAllBooksAdapter.BookViewHolder> {
    Context context;
    ArrayList<Book> bookArrayList;
    OnItemClickListener listener;

    public CustomAllBooksAdapter(Context context, ArrayList<Book> bookArrayList) {
        this.context = context;
        this.bookArrayList = bookArrayList;
    }

    public void setFilteredList(ArrayList<Book> filteredList) {
        this.bookArrayList = filteredList;
        notifyDataSetChanged();
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
        holder.tvAuthor.setText("by " + book.getAuthor());
        holder.tvPrice.setText("৳ " + book.getPrice());
        Glide.with(context).load(book.getImage()).centerCrop().placeholder(R.drawable.library_outline).error(R.drawable.library_outline).into(holder.imgBookCover);
    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvBookName, tvAuthor, tvPrice;
        ImageView imgBookCover;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBookName = itemView.findViewById(R.id.tvBookName);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imgBookCover = itemView.findViewById(R.id.imgBookCover);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) listener.onItemClick(position);
            }
        }
    }
}