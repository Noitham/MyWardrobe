package com.soft.morales.mysmartwardrobe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.soft.morales.mysmartwardrobe.model.Garment;
import com.soft.morales.mysmartwardrobe.model.Look;
import com.soft.morales.mysmartwardrobe.model.User;
import com.soft.morales.mysmartwardrobe.model.persist.APIService;
import com.soft.morales.mysmartwardrobe.model.persist.ApiUtils;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckLookActivity extends AppCompatActivity {

    // Variable created for our ActivityResult
    public static final int CHECK_RESULT_ACTIVITY = 4001;

    // Variables needed for making the Retrofit calls
    private APIService mAPIService;
    SharedPreferences sharedPref;
    Gson gson;

    // Defining our variables
    ImageView imgTorso, imgLegs, imgFeets;
    android.support.design.widget.FloatingActionButton buttonCreateLook, butonDelete;

    private User mUser;
    Look look;
    List<Look> looks;

    Garment Torso, Piernas, Pies;
    String date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_look);

        setupToolbar();

        // Declare new Gson
        gson = new Gson();
        // Declare SharedPreferences variable so we can acced to our SharedPreferences
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        // We build a User from our given information from the sharedPref file (User in Gson format)
        mUser = gson.fromJson(sharedPref.getString("user", ""), User.class);

        // Set our components
        imgTorso = (ImageView) findViewById(R.id.torso_hombre);
        imgLegs = (ImageView) findViewById(R.id.pantalones_hombres);
        imgFeets = (ImageView) findViewById(R.id.pies_hombre);

        // We'll identify our buttons from the view
        butonDelete = (android.support.design.widget.FloatingActionButton) findViewById(R.id.deleteLook);
        buttonCreateLook = (android.support.design.widget.FloatingActionButton) findViewById(R.id.addLook);

        // We'll hide our buttons in the current activity
        buttonCreateLook.hide();

        // We set the listener to our delete button.
        butonDelete.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                AlertDialog diaBox = askDelete();
                diaBox.show();
            }
        });


        mAPIService = ApiUtils.getAPIService();

        // We get the given date from our bundle.
        date = getIntent().getExtras().getString("date");

        // We call the method that will return us the look for the given day.
        getLook();


    }

    /**
     * Method that will get the created look for the selected day in the calendar.
     * <p>
     * We send the query with the given date, and the logged in user. We'll obtain the different garments that compose the look.
     * Once we have the garments, we'll set them into the imageviews.
     */
    public void getLook() {

        mAPIService = ApiUtils.getAPIService();

        // We build our query.
        HashMap query = new HashMap();
        query.put("date", date);
        query.put("username", mUser.getEmail());

        HashMap query1 = new HashMap();
        HashMap query2 = new HashMap();
        HashMap query3 = new HashMap();

        Call<List<Look>> call = mAPIService.getLooks(query);

        call.enqueue(new Callback<List<Look>>() {
            @Override
            public void onResponse(Call<List<Look>> call, Response<List<Look>> response) {

                // We obtain the users looks by the given date and save it in our variable.
                looks = response.body();

                // If we obtain the needed garments, we'll start making the other needed calls for obtaining the garments.
                if (looks.size() > 0) {
                    look = looks.get(0);

                    Call<Garment> call1 = mAPIService.getGarment(look.getTorso_id());
                    Call<Garment> call2 = mAPIService.getGarment(look.getPiernas_id());
                    Call<Garment> call3 = mAPIService.getGarment(look.getPies_id());

                    query1.put("id", look.getTorso_id());
                    query2.put("id", look.getPiernas_id());
                    query3.put("id", look.getPies_id());

                    call1.enqueue(new Callback<Garment>() {
                        @Override
                        public void onResponse(Call<Garment> call, Response<Garment> response) {

                            // We obtain the answer.
                            Torso = response.body();

                            // In case it's not null, we'll set it into the imageview.
                            // If it's null, we'll show a message to the user.
                            if (Torso != null) {
                                Glide.with(getApplication()).load(Uri.parse(String.valueOf(Torso.getPhoto()))).into(imgTorso);

                            } else {
                                Toast.makeText(getApplication(), "There's no look for the current day", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Garment> call, Throwable t) {
                            Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                    call2.enqueue(new Callback<Garment>() {
                        @Override
                        public void onResponse(Call<Garment> call, Response<Garment> response) {

                            // We obtain the answer.
                            Piernas = response.body();

                            // In case it's not null, we'll set it into the imageview.
                            // If it's null, we'll show a message to the user.
                            if (Piernas != null) {
                                Glide.with(getApplication()).load(Uri.parse(String.valueOf(Piernas.getPhoto()))).into(imgLegs);

                            } else {
                                Toast.makeText(getApplication(), "There's no look for the current day", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Garment> call, Throwable t) {
                            Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });


                    call3.enqueue(new Callback<Garment>() {
                        @Override
                        public void onResponse(Call<Garment> call, Response<Garment> response) {

                            // We obtain the answer.
                            Pies = response.body();

                            // In case it's not null, we'll set it into the imageview.
                            // If it's null, we'll show a message to the user.
                            if (Pies != null) {
                                Glide.with(getApplication()).load(Uri.parse(String.valueOf(Pies.getPhoto()))).into(imgFeets);

                            } else {
                                Toast.makeText(getApplication(), "There's no look for the current day", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Garment> call, Throwable t) {
                            Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<List<Look>> call, Throwable t) {
                Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    // Method for setting up our Toolbar in our Activity
    private void setupToolbar() {

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    // Method called in case back is pressed. We finish the activity.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Method we call when user clicks over the delete button.
     * We'll show a Dialog and ask which is his option, dismiss, or delete the current look.
     * If delete is pressed, we'll clear the human.
     *
     * @return myAskDeleteDialog.
     */
    private AlertDialog askDelete() {

        AlertDialog myAskDeleteDialog = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Borrar look")
                .setMessage("Está seguro que desea borrar el siguiente look?")
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteLook();
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return myAskDeleteDialog;

    }


    /**
     * Method we'll call for deleting a garment. We'll send a delete Request to the server by a given garmentId.
     */
    public void deleteLook() {

        mAPIService = ApiUtils.getAPIService();

        Call<Look> call = mAPIService.deleteLook(look.getId());

        call.enqueue(new Callback<Look>() {
            @Override
            public void onResponse(Call<Look> call, Response<Look> response) {

                Toast.makeText(getApplicationContext(), "DELETED CORRECTLY", Toast.LENGTH_LONG).show();

                finish();
            }

            @Override
            public void onFailure(Call<Look> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
