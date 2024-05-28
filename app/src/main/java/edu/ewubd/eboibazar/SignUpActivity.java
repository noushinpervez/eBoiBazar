package edu.ewubd.eboibazar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Users users;
    private String errMsg = "";
    private TextInputLayout textEmail, textPW, textRPW;
    private EditText etEmail, etPW, etRPW;
    private TextView tvHeader, tvDynamic;
    private Button btnAuth, btnDynamic;
    private ProgressBar progressBar;
    private boolean isSignup = true;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPW = findViewById(R.id.etPW);
        etRPW = findViewById(R.id.etRPW);
        tvHeader = findViewById(R.id.tvHeader);
        tvDynamic = findViewById(R.id.tvDynamic);
        btnAuth = findViewById(R.id.btnAuth);
        btnDynamic = findViewById(R.id.btnDynamic);
        textEmail = findViewById(R.id.textEmail);
        textPW = findViewById(R.id.textPW);
        textRPW = findViewById(R.id.textRPW);
        progressBar = findViewById(R.id.progressBar);

        addTextWatchers(); // add text watchers to clear errors

        ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Choose an account");
        progressDialog.setMessage("to continue to eBoiBazar");
        progressDialog.setCanceledOnTouchOutside(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(SignUpActivity.this, gso);

        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvHeader.getText().toString().matches("SIGN IN")) userSignIn();
                else userRegister();
            }
        });

        findViewById(R.id.btnGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        // update ui elements based on sign in/sign up state
        btnDynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSignup)
                    updateUI(false, "SIGN UP", View.VISIBLE, "Sign Up", "Already have an account?", "Sign In");
                else
                    updateUI(true, "SIGN IN", View.GONE, "Sign In", "Don't have an account?", "Sign Up");

                isSignup = !isSignup;
                clearErrors();
            }
        });
    }

    private void userRegister() {
        String email = etEmail.getText().toString().trim();
        String pw = etPW.getText().toString().trim();
        String rpw = etRPW.getText().toString().trim();

        if (!validateEmailAndPW(email, pw)) return;

        if (!pw.equals(rpw)) {
            textRPW.setError("Passwords do not match");
            etRPW.requestFocus();
            return;
        }

        showProgressBar();

        mAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgressBar();
                if (task.isSuccessful()) {
                    user = mAuth.getCurrentUser();
                    users = new Users();
                    if (user != null) {
                        users.setUserId(user.getUid());
                        users.setName(email.substring(0, email.indexOf('@')));
                        users.setEmail(user.getEmail());

                        if (user.getPhotoUrl() != null)
                            users.setPhotoURL(user.getPhotoUrl().toString());

                        // save user details to the database
                        database.getReference("Users").child(user.getUid()).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                    updateUI(true, "SIGN IN", View.GONE, "Sign In", "Don't have an account?", "Sign Up");
                                } else
                                    Toast.makeText(SignUpActivity.this, "Database error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException)
                        errMsg = "User is already registered";
                    else if (task.getException() != null)
                        errMsg = "Sign up error: " + task.getException().getMessage();
                    else errMsg = "Please try again later";

                    Toast.makeText(SignUpActivity.this, errMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void userSignIn() {
        String email = etEmail.getText().toString().trim();
        String pw = etPW.getText().toString().trim();

        if (!validateEmailAndPW(email, pw)) return;

        showProgressBar();

        mAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgressBar();
                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException || task.getException() instanceof FirebaseAuthInvalidUserException)
                        errMsg = "Invalid email or password";
                    else if (task.getException() != null)
                        errMsg = "Sign in error: " + task.getException().getMessage();
                    else errMsg = "Please try again later";

                    Toast.makeText(SignUpActivity.this, errMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void googleSignIn() {
        showProgressBar();
        Intent i = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(i, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseGoogleAuth(account.getIdToken());
            } catch (ApiException e) {
                if (e.getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED)
                    errMsg = "Google sign-in was canceled";
                else if (e.getStatusCode() == GoogleSignInStatusCodes.NETWORK_ERROR)
                    errMsg = "Google sign-in failed due to network error";
                else errMsg = "Google sign-in failed. Please try again";

                Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
            }

        }
    }

    private void firebaseGoogleAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgressBar();
                if (task.isSuccessful()) {
                    user = mAuth.getCurrentUser();
                    users = new Users();
                    if (user != null) {
                        users.setUserId(user.getUid());
                        users.setName(user.getDisplayName());
                        users.setEmail(user.getEmail());

                        if (user.getPhotoUrl() != null)
                            users.setPhotoURL(user.getPhotoUrl().toString());

                        database.getReference().child("Users").child(user.getUid()).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    errMsg = task.getException() != null ? task.getException().getMessage() : "Please try again later";
                                    Toast.makeText(SignUpActivity.this, "Database error: " + errMsg, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                } else {
                    errMsg = task.getException() != null ? task.getException().getMessage() : "Please try again later";
                    Toast.makeText(SignUpActivity.this, "Sign in error: " + errMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validateEmailAndPW(String email, String password) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            textPW.setError("Password must be at least 6 characters long");
            etPW.requestFocus();
            return false;
        }
        if (!password.matches("^(?=.*?[A-Z])(?=.*?[a-z]).{6,}$")) {
            textPW.setError("Password must contain at least one uppercase and lowercase letter");
            etPW.requestFocus();
            return false;
        }

        return true;
    }

    private void updateUI(boolean isSignup, String tvHeaderText, int visibility, String btnAuthText, String tvDynamicText, String btnDynamicText) {
        etEmail.getText().clear();
        etEmail.clearFocus();
        etPW.getText().clear();
        etPW.clearFocus();
        etRPW.getText().clear();
        etRPW.clearFocus();
        tvHeader.setText(tvHeaderText);
        textRPW.setVisibility(visibility);
        btnAuth.setText(btnAuthText);
        tvDynamic.setText(tvDynamicText);
        btnDynamic.setText(btnDynamicText);
    }

    private void addTextWatchers() {
        TextWatcher clearErrorTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearErrors();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        etEmail.addTextChangedListener(clearErrorTextWatcher);
        etPW.addTextChangedListener(clearErrorTextWatcher);
        etRPW.addTextChangedListener(clearErrorTextWatcher);
    }

    private void clearErrors() {
        textEmail.setError(null);
        textEmail.setErrorEnabled(false);
        textPW.setError(null);
        textPW.setErrorEnabled(false);
        textRPW.setError(null);
        textRPW.setErrorEnabled(false);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}