package com.soft.morales.mysmartwardrobe;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.soft.morales.mysmartwardrobe.model.User;
import com.soft.morales.mysmartwardrobe.model.persist.APIService;
import com.soft.morales.mysmartwardrobe.model.persist.ApiUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/passwords.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    // We define our components
    @InjectView(R.id.input_email)
    EditText emailText;
    @InjectView(R.id.input_password)
    EditText passwordText;
    @InjectView(R.id.btn_login)
    Button loginButton;
    @InjectView(R.id.link_signup)
    TextView signupLink;

    User user;
    String emailUser = null;
    String email, password;

    boolean check = false;

    private APIService mAPIService;

    // List of users
    List<User> myUsers;

    // Lists of users emails and passwords.
    List<String> emails = new ArrayList<>();
    List<String> passwords = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        // When the intent is not null, we set the textview containing the user email with it.
        if (getIntent().getExtras() != null) {

            emailUser = getIntent().getExtras().getString("email");
            emailText.setText(emailUser);

        }

        mAPIService = ApiUtils.getAPIService();

        // We set a click listener to our login button.
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    login();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });

        // We set a click listener to our sign up button.
        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public void login() throws NoSuchAlgorithmException {
        Log.d(TAG, "Login");

        // We validate the user introduced correct data
        if (!validate()) {
            loginButton.setEnabled(true);
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        // Create the progressDialog we'll show while user is authenticating
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_NoActionBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autenticando...");
        progressDialog.show();

        // We get the text user introduced into textbox.
        email = emailText.getText().toString();
        password = passwordText.getText().toString();

        // Cypher for password
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(password.getBytes(), 0, password.length());

        password = new BigInteger(1, m.digest()).toString(16);


        // Here we call the method that will get the list of users.
        getAllUsers();

        // Login logic, we'll get the data entered into the textboxes and in case it matches with an existing user,
        // login will be successful, otherwise, login will be failed.
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (myUsers != null) {

                            for (int i = 0; i < myUsers.size(); i++) {

                                if (email.equalsIgnoreCase(emails.get(i).toString()) && password.equalsIgnoreCase(passwords.get(i).toString())) {
                                    check = true;

                                    user = myUsers.get(i);
                                    onLoginSuccess();

                                    break;
                                }
                            }

                            if (!check) {
                                onLoginFailed();
                            }
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);


    }

    /**
     * Method that will get the list of users from our database.
     * We will store the user email and user passwords so we can use them later for the authentication.
     */
    public void getAllUsers() {

        mAPIService = ApiUtils.getAPIService();

        Call<List<User>> call = mAPIService.loginUser();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                // We store the response into our list of users.
                List<User> users = response.body();
                myUsers = new ArrayList<>();

                for (int i = 0; i < users.size(); i++) {

                    myUsers.add(new User(users.get(i)));

                    // We store the user email and user passwords so we can use them later for the authentication.
                    emails.add(myUsers.get(i).getEmail());
                    passwords.add(myUsers.get(i).getPassword());
                }

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    /**
     * In case the activity result is OK, we'll start the application with our logged in user.
     * We store the logged in user email into the bundle so we can later show it.
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        from intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // By default we just finish the Activity and log them in automatically
                Bundle bun = new Bundle();
                bun.putString("email", emailUser);

                Intent intent2 = new Intent(this, MainActivity.class);
                intent2.putExtras(bun);

                startActivity(intent2);
                finish();
            } else {
                loginButton.setEnabled(true);
            }
        }

    }

    // We disable back button
    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    /**
     * In case the login is success, we'll start the application with our logged in user.
     * We store the logged in user email into the bundle so we can later show it.
     */
    public void onLoginSuccess() {
        loginButton.setEnabled(true);

        // Declare new Gson
        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        // Declare SharedPreferences variable so we can acced to our SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        // We write into the Shared preferences our user
        sharedPref.edit().putString("user", userJson).apply();

        // We start the new activity.
        Intent intent2 = new Intent(this, MainActivity.class);

        check = true;

        startActivity(intent2);
        finish();
    }

    // In case login is failed, we'll show a message to the user.
    public void onLoginFailed() {

        Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);

    }

    /**
     * Method that will validate all the data entered is correct.
     *
     * @return valid if it's valid, will not return valid if it's not.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}
