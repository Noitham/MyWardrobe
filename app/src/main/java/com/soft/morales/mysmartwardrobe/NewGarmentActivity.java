package com.soft.morales.mysmartwardrobe;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.soft.morales.mysmartwardrobe.model.Garment;
import com.soft.morales.mysmartwardrobe.model.User;
import com.soft.morales.mysmartwardrobe.model.persist.APIService;
import com.soft.morales.mysmartwardrobe.model.persist.ApiUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewGarmentActivity extends AppCompatActivity {

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int PICK_IMAGE = 100;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "My Smart Wardrobe";

    private Uri fileUri; // file url to store image/video
    File file;

    private ImageView imgPreview; // img preview

    private boolean isUploading = false;

    // We create our variables.
    private Button buttonSend;
    private EditText textName, textBrand, textPrice, textColor;
    private Spinner spinnerCategory, spinnerSeason, spinnerSize;

    private APIService mAPIService;

    private APIService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_garment);


        // UPLOAD PICTURE TO SERVER
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        service = new Retrofit.Builder().baseUrl("http://52.47.130.162/Project/").addConverterFactory(GsonConverterFactory.create()).client(client).build().create(APIService.class);

        // Method that'll set up our toolbar.
        setupToolbar();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // We define our variables.
        mAPIService = ApiUtils.getAPIService();

        imgPreview = (ImageView) findViewById(R.id.backdrop);
        buttonSend = (Button) findViewById(R.id.button_send);
        textName = (EditText) findViewById(R.id.input_name);
        textBrand = (EditText) findViewById(R.id.input_brand);
        textPrice = (EditText) findViewById(R.id.input_price);
        textColor = (EditText) findViewById(R.id.input_color);


        // We set the onclick listener for our floating action button. It will ask user permission for taking pictures and
        // storing data in our phone. Afterwards, it will open camera application, and set the taken photo into the ImageView.
        FloatingActionButton buttonOne = (FloatingActionButton) findViewById(R.id.buttonAdd);
        buttonOne.setOnClickListener(new FloatingActionButton.OnClickListener() {
            public void onClick(View v) {
                requestStoragePermission();
            }
        });

        // Method that will set up our spinners.
        setupSpinners();

        // Method that will create a brand in case is needed (doesn't exist).
        // After that, it will create a new garment if all the data entered is correct.
        testingPost();

    }

    // Will set isUploading to true, afterwards, will call method for creating a new brand with the data the user entered.
    private void testingPost() {

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate() && !isUploading) {
                    isUploading = true;
                    String brand = textBrand.getText().toString().trim();
                    createBrand(brand);
                } else {
                    isUploading = false;
                }
            }
        });

    }

    /**
     * Method for creating a new brand. After the brand is created, it will get the rest of the data the user has entered, and
     * will call the method for creating the new garment.
     *
     * @param name of the brand.
     */
    public void createBrand(String name) {

        mAPIService.createBrand(name).enqueue(new Callback<Garment>() {
            @Override
            public void onResponse(Call<Garment> call, Response<Garment> response) {

                // If response is OK, will get the needed data, and call the method for creating a new garment.
                if (response.isSuccessful()) {

                    String name = textName.getText().toString().trim();
                    String category = spinnerCategory.getSelectedItem().toString();
                    String season = spinnerSeason.getSelectedItem().toString();
                    String price = textPrice.getText().toString().trim();
                    String color = textColor.getText().toString().trim();
                    String size = spinnerSize.getSelectedItem().toString();
                    String brand = textBrand.getText().toString().trim();

                    createNewGarment(name, category, season, price, color, size, brand);

                }
            }

            // Getting sure it's crating the new garment.
            @Override
            public void onFailure(Call<Garment> call, Throwable t) {

                file = new File(fileUri.getPath());

                String name = textName.getText().toString().trim();
                String category = spinnerCategory.getSelectedItem().toString();
                String season = spinnerSeason.getSelectedItem().toString();
                String price = textPrice.getText().toString().trim();
                String color = textColor.getText().toString().trim();
                String size = spinnerSize.getSelectedItem().toString();
                String brand = textBrand.getText().toString().trim();

                createNewGarment(name, category, season, price, color, size, brand);

            }
        });

    }

    /**
     * Method that will send the post for creating a new garment.
     * It will get the data that user has entered.
     * Data user has entered will need to be validated first.
     *
     * @param name     user has entered.
     * @param category user has chosen.
     * @param season   user has chosen.
     * @param price    user has entered.
     * @param color    user has entered.
     * @param size     user has chosen.
     * @param brand    user has created.
     */
    public void createNewGarment(String name, String category, String season, String price,
                                 String color, String size, String brand) {

        // Declare new Gson
        Gson gson = new Gson();
        // Declare SharedPreferences variable so we can acced to our SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        // We build a User from our given information from the sharedPref file (User in Gson format)
        User user = gson.fromJson(sharedPref.getString("user", ""), User.class);


        file = new File(fileUri.getPath());

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
        RequestBody nameImage = RequestBody.create(MediaType.parse("text/plain"), "upload_test");


        retrofit2.Call<okhttp3.ResponseBody> req = service.postImage(name, body, nameImage, category, season, price, user.getEmail(), color, size, brand);

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                // When the response is successful, we'll finish the activity.
                if (response.isSuccessful()) {
                    finish();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Error", "Unable to submit post to API.");
                isUploading = false;
            }
        });

    }

    // Method for setting up our Toolbar in our Activity
    private void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getTitle());
    }


    // We create te back button logic for our Activity.
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Method for setting up our spinners.
     * It will show a hint to the user.
     */
    private void setupSpinners() {

        // SPINNER FOR CATEGORY.
        spinnerCategory = (Spinner) findViewById(R.id.spinner_category);

        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1; // you dont display last item. It is used as hint.
            }

        };

        // We set the options that will be shown. Last corresponds to the hint, it will not be a electable option.
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterCategory.add("Camiseta");
        adapterCategory.add("Pantalón");
        adapterCategory.add("Jersey");
        adapterCategory.add("Chaqueta");
        adapterCategory.add("Calzado");
        adapterCategory.add("Accesorio");
        adapterCategory.add("Categoria");

        // We set the created adapter.
        spinnerCategory.setAdapter(adapterCategory);
        spinnerCategory.setSelection(adapterCategory.getCount()); //display hint


        // SPINNER FOR SEASON.
        spinnerSeason = (Spinner) findViewById(R.id.spinner_season);

        ArrayAdapter<String> adapterSeason = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1; // you dont display last item. It is used as hint.
            }

        };

        // We set the options that will be shown. Last corresponds to the hint, it will not be a electable option.
        adapterSeason.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterSeason.add("Invierno");
        adapterSeason.add("Primavera");
        adapterSeason.add("Verano");
        adapterSeason.add("Otoño");
        adapterSeason.add("Temporada");

        // We set the created adapter.
        spinnerSeason.setAdapter(adapterSeason);
        spinnerSeason.setSelection(adapterSeason.getCount()); //display hint


        // SPINNER FOR SIZE.
        spinnerSize = (Spinner) findViewById(R.id.spinner_size);

        ArrayAdapter<String> adapterSize = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1; // you dont display last item. It is used as hint.
            }

        };

        adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterSize.add("");

        /*
         * We set the options that will be shown. Last corresponds to the hint, it will not be a electable option.
         * In the case of the category, it will be the one that will set the content of the spinner of Size.
         * (When shoes are chosen, we'll show numbers. When clothes are selected, we'll show the appropriate sizes).
         */
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spinnerCategory.getSelectedItem().toString().equalsIgnoreCase("Calzado")) {
                    adapterSize.clear();
                    adapterSize.add("34");
                    adapterSize.add("35");
                    adapterSize.add("36");
                    adapterSize.add("37");
                    adapterSize.add("38");
                    adapterSize.add("39");
                    adapterSize.add("40");
                    adapterSize.add("41");
                    adapterSize.add("42");
                    adapterSize.add("43");
                    adapterSize.add("44");
                    adapterSize.add("45");
                    adapterSize.add("46");
                    adapterSize.add("47");
                    adapterSize.add("48");
                    adapterSize.add("49");
                    adapterSize.add("50");
                    adapterSize.add("51");
                    adapterSize.add("52");
                    adapterSize.add("53");
                    adapterSize.add("Talla");
                } else if (spinnerCategory.getSelectedItem().toString().equalsIgnoreCase("")) {
                    adapterSize.add("Talla");
                } else {
                    adapterSize.clear();
                    adapterSize.add("XXL");
                    adapterSize.add("XL");
                    adapterSize.add("L");
                    adapterSize.add("M");
                    adapterSize.add("S");
                    adapterSize.add("Talla");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        // We set the created adapter.
        spinnerSize.setAdapter(adapterSize);
        spinnerSize.setSelection(adapterSize.getCount()); //display hint

    }


    /*
     * Capturing Camera Image will lauch camera app requrest image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                //uploadPicture();
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * private void uploadPicture() {
     * <p>
     * file = new File(fileUri.getPath());
     * <p>
     * RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
     * MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
     * RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");
     * <p>
     * Log.d("THIS", fileUri.getPath());
     * <p>
     * retrofit2.Call<okhttp3.ResponseBody> req = service.postImage(body, name);
     * req.enqueue(new Callback<ResponseBody>() {
     *
     * @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
     * }
     * @Override public void onFailure(Call<ResponseBody> call, Throwable t) {
     * t.printStackTrace();
     * }
     * });
     * }
     */

    /*
     * Display image from a path to ImageView
     */
    private void previewCapturedImage() {
        try {

            imgPreview.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            imgPreview.setImageBitmap(rotateImageIfRequired(this, bitmap, fileUri));
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    /*
     * Here we restore the fileUri again
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * Requesting multiple permissions (storage and location) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                            captureImage();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewGarmentActivity.this);
        builder.setTitle("Permisos necesarios");
        builder.setMessage("Esta aplicación necesita permiso para usar esta función. Puede otorgarlos en la configuración de la aplicación.");
        builder.setPositiveButton("Ir a ajustes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // Navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /**
     * Method that will validate all the data entered is correct.
     *
     * @return valid if it's valid, will not return valid if it's not.
     */
    public boolean validate() {
        boolean valid = true;

        String name = textName.getText().toString();
        String brand = textBrand.getText().toString();
        String price = textPrice.getText().toString();
        String color = textColor.getText().toString();

        if (name.isEmpty()) {
            textName.setError("enter a valid name");
            valid = false;
        } else {
            textName.setError(null);
        }
        if (brand.isEmpty()) {
            textBrand.setError("enter a valid brand");
            valid = false;
        } else {
            textBrand.setError(null);
        }
        if (price.isEmpty() || !TextUtils.isDigitsOnly(price)) {
            textPrice.setError("enter a valid price");
            valid = false;
        } else {
            textPrice.setError(null);
        }
        if (color.isEmpty()) {
            textColor.setError("enter a valid color");
            valid = false;
        } else {
            textColor.setError(null);
        }

        if (fileUri == null) {
            Toast.makeText(this, "Take a photo", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    /**
     * Rotate an image if required, will detect the phone camera orientation, and set the image correctly.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    /**
     * Method that will rotate the image in case is required.
     *
     * @param img    bitmap.
     * @param degree of orientation.
     * @return bitmap of rotated image, if rotated.
     */
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

}


