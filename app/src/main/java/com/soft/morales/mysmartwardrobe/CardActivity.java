package com.soft.morales.mysmartwardrobe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.soft.morales.mysmartwardrobe.model.Garment;
import com.soft.morales.mysmartwardrobe.model.Look;
import com.soft.morales.mysmartwardrobe.model.User;
import com.soft.morales.mysmartwardrobe.model.persist.APIService;
import com.soft.morales.mysmartwardrobe.model.persist.ApiUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardActivity extends AppCompatActivity {

    // We define our components
    TextView txtName, txtCategory, txtSeason, txtPrice, txtColor, txtSize, txtBrand;
    ImageView imageView;
    Button deleteButton;
    Integer garmentId;
    String date, URI;
    int pos;

    private APIService mAPIService;
    private User mUser;
    private boolean isDeleting;

    // List of looks
    List<Look> looks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.garment_details_layout);

        setupToolbar();

        // Declare new Gson
        Gson gson = new Gson();
        // Declare SharedPreferences variable so we can acced to our SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        // We build a User from our given information from the sharedPref file (User in Gson format)
        mUser = gson.fromJson(sharedPref.getString("user", ""), User.class);

        // Define our components
        txtName = (TextView) findViewById(R.id.tvName);
        txtCategory = (TextView) findViewById(R.id.tvCategory);
        txtSeason = (TextView) findViewById(R.id.tvSeason);
        txtPrice = (TextView) findViewById(R.id.tvPrice);
        txtColor = (TextView) findViewById(R.id.tvColor);
        txtSize = (TextView) findViewById(R.id.tvSize);
        txtBrand = (TextView) findViewById(R.id.tvBrand);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        imageView = (ImageView) findViewById(R.id.imageView);

        // We get the data from our bundle.
        Bundle mbundle = this.getIntent().getExtras();

        URI = mbundle.getString("Foto");
        garmentId = mbundle.getInt("ID");
        pos = mbundle.getInt("pos");

        // We get the data from the bundle and we fill it into the field.
        txtName.setText("Nombre: " + mbundle.getString("Nombre"));
        txtCategory.setText("Categoría: " + mbundle.getString("Categoria"));
        txtSeason.setText("Temporada: " + mbundle.getString("Temporada"));
        txtPrice.setText("Precio: " + mbundle.getString("Precio"));
        txtColor.setText("Color: " + mbundle.getString("Color"));
        txtSize.setText("Talla: " + mbundle.getString("Talla"));
        txtBrand.setText("Marca: " + mbundle.getString("Marca"));

        // For the uri, we'll parse it and set it into the imageView
        Uri myUri = Uri.parse(URI);
        Glide.with(this).load(myUri).into(imageView);


        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                AlertDialog diaBox = AskOption();
                diaBox.show();
            }
        });

    }

    // Method for setting up our Toolbar in our Activity
    private void setupToolbar() {

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Method we'll call for deleting a garment. We'll send a delete Request to the server by a given garmentId.
     */
    public void deleteGarment() {

        mAPIService = ApiUtils.getAPIService();

        Call<Garment> call = mAPIService.deleteGarment(garmentId);

        call.enqueue(new Callback<Garment>() {
            @Override
            public void onResponse(Call<Garment> call, Response<Garment> response) {

                Toast.makeText(getApplicationContext(), "DELETED CORRECTLY", Toast.LENGTH_LONG).show();

                Bundle b = new Bundle();
                Intent i = new Intent();

                if (!isDeleting) {
                    getLooks();
                    isDeleting = true;
                } else {
                    isDeleting = false;
                }

                // When we're deleting a garment, we check if the removed garment was being used in a look.
                // In that case, we will also remove any look containing that garment.

                for (int j = 0; j < looks.size(); j++) {

                    if (looks.get(j).getGarmentsIds().size() == 3) {

                        int id1 = looks.get(j).getGarmentsIds().get(0);
                        int id2 = looks.get(j).getGarmentsIds().get(1);
                        int id3 = looks.get(j).getGarmentsIds().get(2);

                        if (garmentId == id1
                                || garmentId == id2
                                || garmentId == id3) {

                            deleteLook(looks.get(j).getId());

                        }
                    }
                }

                b.putInt("pos", pos);
                i.putExtras(b);
                setResult(RESULT_OK, i);
                finish();
            }

            @Override
            public void onFailure(Call<Garment> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Method we'll call for getting all looks.
     * We'll use them for deleting any look containing a deleted garment.
     */
    public void getLooks() {

        mAPIService = ApiUtils.getAPIService();

        HashMap query = new HashMap();
        query.put("username", mUser.getEmail());

        Call<List<Look>> call = mAPIService.getLooks(query);

        call.enqueue(new Callback<List<Look>>() {
            @Override
            public void onResponse(Call<List<Look>> call, Response<List<Look>> response) {

                if (response.isSuccessful()) {

                    looks = response.body();

                    deleteGarment();
                }
            }

            @Override
            public void onFailure(Call<List<Look>> call, Throwable t) {
                Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Method we'll call for getting a look by its id.
     *
     * @param lookId lookId
     */
    public void deleteLook(String lookId) {

        mAPIService = ApiUtils.getAPIService();

        Call<Look> call = mAPIService.deleteLook(lookId);

        call.enqueue(new Callback<Look>() {
            @Override
            public void onResponse(Call<Look> call, Response<Look> response) {

                Toast.makeText(getApplicationContext(), "Deleted correctly", Toast.LENGTH_LONG).show();

                finish();
            }

            @Override
            public void onFailure(Call<Look> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    /**
     * Method for creating an AlertDialong asking the user to choose an option.
     *
     * @return mDeleteDialog
     */
    private AlertDialog AskOption() {
        AlertDialog mDeleteDialog = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Borrar")
                .setMessage("Seguro que desea eliminar la prenda?")
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Method for deleting the garment
                        getLooks();
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return mDeleteDialog;

    }

}