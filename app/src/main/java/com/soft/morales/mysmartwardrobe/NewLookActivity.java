package com.soft.morales.mysmartwardrobe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.soft.morales.mysmartwardrobe.model.Look;
import com.soft.morales.mysmartwardrobe.model.User;
import com.soft.morales.mysmartwardrobe.model.persist.APIService;
import com.soft.morales.mysmartwardrobe.model.persist.ApiUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewLookActivity extends AppCompatActivity {

    // Variable created for our FOTO_REQUEST
    private static final int FOTO_REQUEST = 456;

    // We define our components
    android.support.design.widget.FloatingActionButton buttonCreateLook, butonDelete;
    ImageView imgTorso, imgLegs, imgFeets;
    Intent intent2, intent3;
    private String type, foto;
    private Integer idShirt, idLegs, idFeet;
    String myString = null;

    private APIService mAPIService;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_look);

        // We set up our toolbar.
        setupToolbar();

        mAPIService = ApiUtils.getAPIService();

        // We get the data stored in our bundle & SharedPreferences.
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Bundle bundle = this.getIntent().getExtras();

        // We set our components
        imgTorso = (ImageView) findViewById(R.id.torso_hombre);
        imgLegs = (ImageView) findViewById(R.id.pantalones_hombres);
        imgFeets = (ImageView) findViewById(R.id.pies_hombre);

        butonDelete = (android.support.design.widget.FloatingActionButton) findViewById(R.id.deleteLook);
        buttonCreateLook = (android.support.design.widget.FloatingActionButton) findViewById(R.id.addLook);

        // We set the listener to our torso.
        imgTorso.setOnClickListener(new FloatingActionButton.OnClickListener() {
            public void onClick(View v) {
                setTorso();
            }
        });

        // We set the listener to our legs.
        imgLegs.setOnClickListener(new FloatingActionButton.OnClickListener() {
            public void onClick(View v) {
                setLegs();
            }
        });

        // We set the listener to our feets.
        imgFeets.setOnClickListener(new FloatingActionButton.OnClickListener() {
            public void onClick(View v) {
                setFeets();
            }
        });

        // We set the listener to our create button.
        buttonCreateLook.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                AlertDialog diaBox = askCreateLook();

                diaBox.show();

            }
        });

        // We set the listener to our delete button.
        butonDelete.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                AlertDialog diaBox = askDelete();
                diaBox.show();
            }
        });

    }

    /**
     * Method for setting up our torso.
     * We will set the variable "ok" to 1, so when we're taken to the MainActivity, it will act with the needed behaviour.
     * <p>
     * Method startActivityForResult will wait for the correct result (when the user selects a garment from the list).
     */
    public void setTorso() {
        Bundle bundle = new Bundle();
        bundle.putInt("ok", 1);

        // We start a new intent
        intent2 = new Intent(this, MainActivity.class);
        intent2.putExtras(bundle);

        startActivityForResult(intent2, FOTO_REQUEST);
    }


    /**
     * Method for setting up our legs.
     * We will set the variable "ok" to 1, so when we're taken to the MainActivity, it will act with the needed behaviour.
     * <p>
     * Method startActivityForResult will wait for the correct result (when the user selects a garment from the list).
     */
    public void setLegs() {
        Bundle bundle = new Bundle();
        bundle.putInt("ok", 1);

        // We start a new intent
        intent2 = new Intent(this, MainActivity.class);
        intent2.putExtras(bundle);

        startActivityForResult(intent2, FOTO_REQUEST);

    }

    /**
     * Method for setting up our feets.
     * We will set the variable "ok" to 1, so when we're taken to the MainActivity, it will act with the needed behaviour.
     * <p>
     * Method startActivityForResult will wait for the correct result (when the user selects a garment from the list).
     */
    public void setFeets() {
        Bundle bundle = new Bundle();
        bundle.putInt("ok", 1);

        // We start a new intent
        intent2 = new Intent(this, MainActivity.class);
        intent2.putExtras(bundle);

        startActivityForResult(intent2, FOTO_REQUEST);
    }

    /**
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data from intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FOTO_REQUEST && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();

            intent3 = data;

            if (bundle != null) {
                type = bundle.getString("garmentType", "");
                foto = bundle.getString("Foto", "");

                if (type.equalsIgnoreCase("Shirt")) {
                    Glide.with(this).load(Uri.parse(foto)).into(imgTorso);
                } else if (type.equalsIgnoreCase("Legs")) {
                    Glide.with(this).load(Uri.parse(foto)).into(imgLegs);
                } else if (type.equalsIgnoreCase("Feet")) {
                    Glide.with(this).load(Uri.parse(foto)).into(imgFeets);
                }

            } else {
                Toast.makeText(getApplicationContext(),
                        "Bundle vacío", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        myString = savedInstanceState.getString("MyString");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        savedInstanceState.putString("MyString", "Welcome back to Android");
        // etc.
        super.onSaveInstanceState(savedInstanceState);

    }

    /**
     * Method we call when user clicks over the save button.
     * We'll show a Dialog and ask which is his option, dismiss, or save the look of that day.
     * If save is pressed, we'll send the createlook post.
     *
     * @return myAskConfirmationDialog.
     */
    private AlertDialog askCreateLook() {

        AlertDialog myAskConfirmationDialog = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Guardar look")
                .setMessage("Está seguro que desea guardar el look para el día de hoy?")
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(DialogInterface dialog, int whichButton) {

                        createLookPost(intent3);

                    }

                })

                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return myAskConfirmationDialog;

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
                        Glide.with(getApplication()).load(R.drawable.torso_hombre).into(imgTorso);
                        Glide.with(getApplication()).load(R.drawable.pantalon_hombre).into(imgLegs);
                        Glide.with(getApplication()).load(R.drawable.pies_hombre).into(imgFeets);
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

    // Method for setting up our Toolbar in our Activity
    private void setupToolbar() {

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Method that will send the post for creating a new look.
     * Will get data from bundle (garmentType, and photo (URI)), of each garment.
     * It will check that we obtained 3 different photos, in case the look is incomplete, we will report it to the user.
     * <p>
     * Once we have the 3 garments URI's we'll set them into the ImageView and send the post of the new look.
     * We'll also store the date of the current day, so we can check the look on the calendar later.
     *
     * @param data from bundle
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void createLookPost(Intent data) {

        if (data != null && data.getExtras() != null) {
            Bundle bundle = data.getExtras();

            type = bundle.getString("garmentType", "");
            foto = bundle.getString("Foto", "");

            // We get the stored data from the SharedPreferences.
            SharedPreferences shared = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            idShirt = shared.getInt("idShirt", 0);
            idLegs = shared.getInt("idLegs", 0);
            idFeet = shared.getInt("idFeet", 0);

            // We create our list of garments URI's (type integer).
            List<Integer> myGarments = new ArrayList<>();

            // When the type of T-shirt matches to the location of the garment, we'll set it into the ImageView.
            if (type.equalsIgnoreCase("Camiseta")) {
                Glide.with(this).load(Uri.parse(foto)).into(imgTorso);
            } else if (type.equalsIgnoreCase("Legs")) {
                Glide.with(this).load(Uri.parse(foto)).into(imgLegs);
            } else if (type.equalsIgnoreCase("Feet")) {
                Glide.with(this).load(Uri.parse(foto)).into(imgFeets);
            }


            // We prove that the obtained URI is not null (defValue was "").
            // In case it's not null, we add the URI into our list of Garments.
            // If it's null, we'll show a message to the user asking to complete the look.
            if (!idShirt.equals(0)) {
                myGarments.add(idShirt);
                if (myGarments.size() == 1) {
                    if (!idLegs.equals(0)) {
                        myGarments.add(idLegs);
                        if (myGarments.size() == 2) {
                            if (!idFeet.equals(0)) {
                                myGarments.add(idFeet);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Porfavor, completa el look antes de guardarlo", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Porfavor, completa el look antes de guardarlo", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Porfavor, completa el look antes de guardarlo", Toast.LENGTH_SHORT)
                        .show();
            }

            // Once we've obtained 3 different garments into our List of URI's, we'll send the cretelook post.
            if (myGarments.size() == 3) {

                Log.d("lista", myGarments.toString());

                // Declare new Gson
                Gson gson = new Gson();

                // Declare SharedPreferences variable so we can acced to our SharedPreferences
                shared = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

                // We build a User from our given information from the sharedPref file (User in Gson format)
                User user = gson.fromJson(shared.getString("user", ""), User.class);

                // We get the date from the current day.
                Date c = Calendar.getInstance().getTime();

                // We set it to our required format
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = df.format(c);

                Integer torso_id = myGarments.get(0);
                Integer piernas_id = myGarments.get(1);
                Integer pies_id = myGarments.get(2);

                // Finally, when we have all the required data, we'll send the POST.
                mAPIService.createLook(torso_id, piernas_id, pies_id, user.getEmail(), formattedDate).enqueue(new Callback<Look>() {
                    @Override
                    public void onResponse(Call<Look> call, Response<Look> response) {

                        if (response.isSuccessful()) {
                            Log.d("OK: ", "post submitted to API." + response.body().toString());
                            // We finish the Activity.
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Ya existe un look para el día seleccionado", Toast.LENGTH_SHORT)
                                    .show();
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Look> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),
                                "Ya existe un look para el día seleccionado", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

                //Once sended, we'll clean sharedPreferences
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                sharedPref.edit().remove("idShirt").apply();
                sharedPref.edit().remove("idLegs").apply();
                sharedPref.edit().remove("idFeet").apply();

                // We finish the Activity.
                finish();

            } else {
                Toast.makeText(getApplicationContext(),
                        "Porfavor, completa el look antes de guardarlo", Toast.LENGTH_SHORT)
                        .show();
            }

        }

    }


}

