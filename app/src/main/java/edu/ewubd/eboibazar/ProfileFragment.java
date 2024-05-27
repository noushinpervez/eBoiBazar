package edu.ewubd.eboibazar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.URL;

public class ProfileFragment extends Fragment {
    // options for the ListView
    String[] options = {"Order History", "About Us"};
    int[] optionIcons = {R.drawable.history, R.drawable.information_outline};
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView tvEmail, tvName;
    private ImageView photo;
    private Button signOut;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        tvEmail = view.findViewById(R.id.tvEmail);
        tvName = view.findViewById(R.id.tvName);
        photo = view.findViewById(R.id.photo);
        signOut = view.findViewById(R.id.signOut);
        ListView lvOptions = view.findViewById(R.id.lvOptions);

        CustomOptionsAdapter customOptionsAdapter = new CustomOptionsAdapter(requireActivity().getApplicationContext(), options, optionIcons);
        lvOptions.setAdapter(customOptionsAdapter);

        showProfileInfo();

        view.findViewById(R.id.clickableLayout).setOnClickListener(v -> {
            if (user == null) {
                Intent i = new Intent(getActivity(), SignUpActivity.class);
                startActivity(i);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignOutDialog();
            }
        });

        lvOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // navigate to different activities in options
                switch (position) {
                    case 0:
                        Intent i = new Intent(getActivity(), OrderHistoryActivity.class);
                        startActivity(i);
                        break;
                    case 1:
                        i = new Intent(getActivity(), AboutUs.class);
                        startActivity(i);
                        break;
                    default:
                        break;
                }
            }
        });

        return view;
    }

    private void showProfileInfo() {
        if (user != null) {
            String email = user.getEmail();
            String name = user.getDisplayName() != null ? user.getDisplayName() : email.substring(0, email.indexOf('@'));
            Uri photoUrl = user.getPhotoUrl();

            tvEmail.setText(email);
            tvName.setText(name);

            if (photoUrl != null) new LoadImageTask().execute(photoUrl.toString());
            else photo.setVisibility(View.GONE);
        } else signOut.setVisibility(View.GONE);
    }

    private void reloadFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setReorderingAllowed(false);
        fragmentTransaction.detach(ProfileFragment.this).commit();
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.attach(ProfileFragment.this).commit();
    }

    private void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        builder.setTitle("SIGN OUT");
        builder.setMessage("Are you sure you want to sign out from eBoiBazar?");

        builder.setPositiveButton("Sign me out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.signOut();
                Toast.makeText(getActivity(), "Signed out successfully", Toast.LENGTH_LONG).show();
                reloadFragment();
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

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null) photo.setImageBitmap(bitmap);
            else photo.setVisibility(View.GONE);
        }
    }
}