package edu.ewubd.eboibazar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView rvAllBooks, rvNewArrivals, rvBanglaBooks, rvEnglishBooks;
    private CustomBooksAdapter customBooksAdapter, newArrivalsAdapter, banglaBooksAdapter, englishBooksAdapter;
    private ArrayList<Book> bookArrayList, newArrivalsList, banglaBooksList, englishBooksList;
    private LinearLayout splashLayout;
    private ScrollView homeLayout;
    private Toolbar toolbar;
    private Button btnMoreBooks, btnMoreNewArrivals, btnMoreBanglaBooks, btnMoreEnglishBooks;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initialize(view);

        bookArrayList = new ArrayList<>();
        newArrivalsList = new ArrayList<>();
        banglaBooksList = new ArrayList<>();
        englishBooksList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Books");

        showSplash();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                processBookData(snapshot);
                updateUI();
                hideSplash();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                hideSplash();
            }
        });

        setButtonListeners();

        view.findViewById(R.id.btnDashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DashboardActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

    private void updateUI() {
        customBooksAdapter = new CustomBooksAdapter(getContext(), bookArrayList);
        newArrivalsAdapter = new CustomBooksAdapter(getContext(), newArrivalsList);
        banglaBooksAdapter = new CustomBooksAdapter(getContext(), banglaBooksList);
        englishBooksAdapter = new CustomBooksAdapter(getContext(), englishBooksList);

        rvAllBooks.setAdapter(customBooksAdapter);
        rvNewArrivals.setAdapter(newArrivalsAdapter);
        rvBanglaBooks.setAdapter(banglaBooksAdapter);
        rvEnglishBooks.setAdapter(englishBooksAdapter);

        btnMoreBooks.setVisibility(bookArrayList.size() > 7 ? View.VISIBLE : View.GONE);
        btnMoreNewArrivals.setVisibility(newArrivalsList.size() > 7 ? View.VISIBLE : View.GONE);
        btnMoreBanglaBooks.setVisibility(banglaBooksList.size() > 7 ? View.VISIBLE : View.GONE);
        btnMoreEnglishBooks.setVisibility(englishBooksList.size() > 7 ? View.VISIBLE : View.GONE);

        setRecyclerViewItemClickListeners();
    }

    private void setRecyclerViewItemClickListeners() {
        customBooksAdapter.setOnItemClickListener(new CustomBooksAdapter.OnItemClickListener() {
            public void onItemClick(int position) {
                Book selectedBook = bookArrayList.get(position);
                startActivity(new Intent(getActivity(), BookDetailActivity.class).putExtra("book", selectedBook));
            }
        });

        newArrivalsAdapter.setOnItemClickListener(new CustomBooksAdapter.OnItemClickListener() {
            public void onItemClick(int position) {
                Book selectedBook = newArrivalsList.get(position);
                startActivity(new Intent(getActivity(), BookDetailActivity.class).putExtra("book", selectedBook));
            }
        });

        banglaBooksAdapter.setOnItemClickListener(new CustomBooksAdapter.OnItemClickListener() {
            public void onItemClick(int position) {
                Book selectedBook = banglaBooksList.get(position);
                startActivity(new Intent(getActivity(), BookDetailActivity.class).putExtra("book", selectedBook));
            }
        });

        englishBooksAdapter.setOnItemClickListener(new CustomBooksAdapter.OnItemClickListener() {
            public void onItemClick(int position) {
                Book selectedBook = englishBooksList.get(position);
                startActivity(new Intent(getActivity(), BookDetailActivity.class).putExtra("book", selectedBook));
            }
        });
    }

    private void setButtonListeners() {
        btnMoreBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ShowBooksActivity.class).putExtra("bookArrayList", bookArrayList).putExtra("title", "All Books"));
            }
        });

        btnMoreNewArrivals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ShowBooksActivity.class).putExtra("bookArrayList", newArrivalsList).putExtra("title", "New Arrivals"));
            }
        });

        btnMoreBanglaBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ShowBooksActivity.class).putExtra("bookArrayList", banglaBooksList).putExtra("title", "Bangla Books"));
            }
        });

        btnMoreEnglishBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ShowBooksActivity.class).putExtra("bookArrayList", englishBooksList).putExtra("title", "English Books"));
            }
        });
    }

    private void processBookData(DataSnapshot snapshot) {
        bookArrayList.clear();
        banglaBooksList.clear();
        englishBooksList.clear();

        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Book book = dataSnapshot.getValue(Book.class);
            bookArrayList.add(book);

            if (book != null && "Bangla".equalsIgnoreCase(book.getLanguage()))
                banglaBooksList.add(book);
            else if (book != null && "English".equalsIgnoreCase(book.getLanguage()))
                englishBooksList.add(book);
        }

        newArrivalsList = new ArrayList<>(bookArrayList);
        newArrivalsList.sort((b1, b2) -> Integer.compare(b2.getPublishYear(), b1.getPublishYear()));
    }

    private void showSplash() {
        splashLayout.setVisibility(View.VISIBLE);
        homeLayout.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
    }

    private void hideSplash() {
        splashLayout.setVisibility(View.GONE);
        homeLayout.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
    }

    private void initialize(View view) {
        homeLayout = view.findViewById(R.id.homeLayout);
        splashLayout = view.findViewById(R.id.splashLayout);
        toolbar = view.findViewById(R.id.toolbar);
        btnMoreBooks = view.findViewById(R.id.btnMoreBooks);
        btnMoreNewArrivals = view.findViewById(R.id.btnMoreNewArrivals);
        btnMoreBanglaBooks = view.findViewById(R.id.btnMoreBanglaBooks);
        btnMoreEnglishBooks = view.findViewById(R.id.btnMoreEnglishBooks);

        rvAllBooks = view.findViewById(R.id.rvAllBooks);
        rvNewArrivals = view.findViewById(R.id.rvNewArrivals);
        rvBanglaBooks = view.findViewById(R.id.rvBanglaBooks);
        rvEnglishBooks = view.findViewById(R.id.rvEnglishBooks);

        rvAllBooks.setHasFixedSize(true);
        rvNewArrivals.setHasFixedSize(true);
        rvBanglaBooks.setHasFixedSize(true);
        rvEnglishBooks.setHasFixedSize(true);

        rvAllBooks.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvNewArrivals.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvBanglaBooks.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvEnglishBooks.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }
}