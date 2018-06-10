package com.soft.morales.mysmartwardrobe.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.soft.morales.mysmartwardrobe.CardActivity;
import com.soft.morales.mysmartwardrobe.NewLookActivity;
import com.soft.morales.mysmartwardrobe.R;
import com.soft.morales.mysmartwardrobe.adapters.CustomAdapter;
import com.soft.morales.mysmartwardrobe.model.Garment;
import com.soft.morales.mysmartwardrobe.model.User;
import com.soft.morales.mysmartwardrobe.model.persist.APIService;
import com.soft.morales.mysmartwardrobe.model.persist.ApiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.soft.morales.mysmartwardrobe.MainActivity.CARD_ACTION;

@SuppressLint("ValidFragment")
public class MyClosetTabFragment extends Fragment {

    // We define our variables
    private APIService mAPIService;
    private int mPosition;
    private ListView listView;
    private int lastItemClicked = 0;

    int value;

    // List of garments by type.
    List<Garment> myShirts, myJerseys, myJackets, myJeans, myShoes, myAccessories;

    // We define a variable where we'll get the logged in user
    private User mUser;
    private CustomAdapter mAdapter;

    // Identify the tab by its position
    public MyClosetTabFragment(int position) {
        mPosition = position;
    }

    // Identify the tab by its position and mode(value) (when we're comming from createLook).
    public MyClosetTabFragment(int position, int mode) {
        mPosition = position;
        value = mode;
    }

    // Empty constructor
    public MyClosetTabFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab, container, false);

        // Declare new Gson
        Gson gson = new Gson();
        // Declare SharedPreferences variable so we can acced to our SharedPreferences
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), getActivity().MODE_PRIVATE);

        // We build a User from our given information from the sharedPref file (User in Gson format)
        mUser = gson.fromJson(sharedPref.getString("user", ""), User.class);

        listView = (ListView) container.findViewById(R.id.listview);

        // Switch case that will call the different methods that will fill our listview when we're moving through the different tabs.
        switch (mPosition) {
            case 1:
                getAllShirts();
                break;
            case 2:
                getAllJeans();
                break;
            case 3:
                getAllJerseys();
                break;
            case 4:
                getAllJackets();
                break;
            case 5:
                getAllShoes();
                break;
            case 6:
                getAllAccessories();
                break;
            default:
                break;
        }

        return rootView;
    }

    /**
     * Method that will fill our listview with the given GarmentsList.
     *
     * @param Garments list to show in the listview.
     */
    public void fillListView(List<Garment> Garments) {

        List<Garment> rowItems = new ArrayList<Garment>();

        // In case our list is not null, we'll fill the view.
        if (Garments != null) {

            for (int i = 0; i < Garments.size(); i++) {

                // We add the items from our list.
                Garment item = new Garment(Garments.get(i).getName(),
                        Garments.get(i).getPhoto(), Garments.get(i).getCategory(),
                        Garments.get(i).getBrand());
                rowItems.add(item);

            }

            // Here we'll add the listener that will start our cardActivity of our selected garment from the listview.
            if (getView() != null) {

                listView = (ListView) getView().findViewById(R.id.listview);

                mAdapter = new CustomAdapter(getContext(), rowItems);
                listView.setAdapter(mAdapter);
                listView.setOnItemClickListener(listener);
            }

        }
    }

    // Listener that will get the garment by it's given position and will start the cardActivity showing its information.
    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            lastItemClicked = position;

            // Switch case by garment position in the tab. Will start a cardActivity for the selected garment.
            switch (mPosition) {
                case 1:
                    startCardActivity(myShirts.get(position), mPosition);
                    break;
                case 2:
                    startCardActivity(myJeans.get(position), mPosition);
                    break;
                case 3:
                    startCardActivity(myJerseys.get(position), mPosition);
                    break;
                case 4:
                    startCardActivity(myJackets.get(position), mPosition);
                    break;
                case 5:
                    startCardActivity(myShoes.get(position), mPosition);
                    break;
                case 6:
                    startCardActivity(myAccessories.get(position), mPosition);
                    break;
                default:
                    break;
            }

        }
    };

    /**
     * Method we call for starting the cardActivity.
     * It will get the selected garment and its position.
     * <p>
     * If the value is 0 (we come from the gallery), we'll start a cardActivity with the selected garment data.
     * <p>
     * Otherwise (value is 1), we're coming from the NewLookActivity (we're selecting a garment of the look), in that case
     * when we click in a garment, we'll write into the sharedPreferences the garmentId and it's type for identifying it, then
     * we'll return to the NewLookActivity.
     *
     * @param garment garment to show.
     * @param pos     in the listview.
     */
    public void startCardActivity(Garment garment, int pos) {

        if (value == 1) {
            Intent intent = new Intent(getActivity(), NewLookActivity.class);
            Bundle bundle = new Bundle();

            bundle.putString("Foto", garment.getPhoto());

            if (pos == 1 || pos == 3 || pos == 4) {
                bundle.putString("garmentType", "Shirt");
                SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                sharedPref.edit().putString("idShirt", garment.getId()).apply();
            } else if (pos == 2) {
                bundle.putString("garmentType", "Legs");
                SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                sharedPref.edit().putString("idLegs", garment.getId()).apply();
            } else if (pos == 5) {
                bundle.putString("garmentType", "Feet");
                SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                sharedPref.edit().putString("idFeet", garment.getId()).apply();
            }

            // We introduce into the bundle the information from the selected garment.
            intent.putExtras(bundle);
            if (getActivity() != null) {
                // We set the activity result, NewLookActivity @startActivityForResult will be called.
                getActivity().setResult(RESULT_OK, intent);
                getActivity().finish();
            }

        } else {

            // Otherwise, when value is 0, we'll start the cardActivity with the given Garment information.
            Intent intent = new Intent(getActivity(), CardActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("ID", garment.getId());
            bundle.putString("Nombre", garment.getName());
            bundle.putString("Foto", garment.getPhoto());
            bundle.putString("Categoria", garment.getCategory());
            bundle.putString("Temporada", garment.getSeason());
            bundle.putString("Precio", garment.getPrice());
            bundle.putString("Color", garment.getColor());
            bundle.putString("Talla", garment.getSize());
            bundle.putString("Marca", garment.getBrand());
            bundle.putInt("pos", lastItemClicked);
            intent.putExtras(bundle);

            startActivityForResult(intent, CARD_ACTION);
        }

    }


    /**
     * Method we call for getting all the loged in user's garments type shirt.
     * We set the category to "Camsieta", and we get the username from the sharedPreferences file.
     * <p>
     * When we have the list with the shirts, we'll fill the list view with them.
     */
    public void getAllShirts() {

        mAPIService = ApiUtils.getAPIService();

        HashMap query = new HashMap();

        query.put("category", "Camiseta");
        query.put("username", mUser.getEmail());

        Call<List<Garment>> call = mAPIService.getGarment(query);

        call.enqueue(new Callback<List<Garment>>() {
            @Override
            public void onResponse(Call<List<Garment>> call, Response<List<Garment>> response) {

                myShirts = response.body();
                fillListView(myShirts);
            }

            @Override
            public void onFailure(Call<List<Garment>> call, Throwable t) {
                Log.d("ERROR:", "NO SHIRTS");
            }
        });

    }

    /**
     * Method we call for getting all the loged in user's garments type jacket.
     * We set the category to "Chaqueta", and we get the username from the sharedPreferences file.
     * <p>
     * When we have the list with the shirts, we'll fill the list view with them.
     */
    public void getAllJackets() {

        mAPIService = ApiUtils.getAPIService();

        HashMap query = new HashMap();
        query.put("category", "chaqueta");
        query.put("username", mUser.getEmail());

        Call<List<Garment>> call = mAPIService.getGarment(query);

        call.enqueue(new Callback<List<Garment>>() {
            @Override
            public void onResponse(Call<List<Garment>> call, Response<List<Garment>> response) {

                myJackets = response.body();
                fillListView(myJackets);
            }

            @Override
            public void onFailure(Call<List<Garment>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Method we call for getting all the loged in user's garments type jeans.
     * We set the category to "Pantalón", and we get the username from the sharedPreferences file.
     * <p>
     * When we have the list with the shirts, we'll fill the list view with them.
     */
    public void getAllJeans() {

        mAPIService = ApiUtils.getAPIService();

        HashMap query = new HashMap();
        query.put("category", "pantalón");
        query.put("username", mUser.getEmail());

        Call<List<Garment>> call = mAPIService.getGarment(query);

        call.enqueue(new Callback<List<Garment>>() {
            @Override
            public void onResponse(Call<List<Garment>> call, Response<List<Garment>> response) {

                myJeans = response.body();
                fillListView(myJeans);
            }

            @Override
            public void onFailure(Call<List<Garment>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Method we call for getting all the loged in user's garments type jersey.
     * We set the category to "Jersey", and we get the username from the sharedPreferences file.
     * <p>
     * When we have the list with the shirts, we'll fill the list view with them.
     */
    public void getAllJerseys() {

        mAPIService = ApiUtils.getAPIService();

        HashMap query = new HashMap();
        query.put("category", "jersey");
        query.put("username", mUser.getEmail());

        Call<List<Garment>> call = mAPIService.getGarment(query);

        call.enqueue(new Callback<List<Garment>>() {
            @Override
            public void onResponse(Call<List<Garment>> call, Response<List<Garment>> response) {

                myJerseys = response.body();
                fillListView(myJerseys);
            }

            @Override
            public void onFailure(Call<List<Garment>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * Method we call for getting all the loged in user's garments type accesory.
     * We set the category to "Accesorio", and we get the username from the sharedPreferences file.
     * <p>
     * When we have the list with the shirts, we'll fill the list view with them.
     */
    public void getAllAccessories() {

        mAPIService = ApiUtils.getAPIService();

        HashMap query = new HashMap();
        query.put("category", "Accesorio");
        query.put("username", mUser.getEmail());

        Call<List<Garment>> call = mAPIService.getGarment(query);

        call.enqueue(new Callback<List<Garment>>() {
            @Override
            public void onResponse(Call<List<Garment>> call, Response<List<Garment>> response) {

                myAccessories = response.body();
                fillListView(myAccessories);
            }

            @Override
            public void onFailure(Call<List<Garment>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }


    /**
     * Method we call for getting all the loged in user's garments type shoes.
     * We set the category to "Calzado", and we get the username from the sharedPreferences file.
     * <p>
     * When we have the list with the shirts, we'll fill the list view with them.
     */
    public void getAllShoes() {

        mAPIService = ApiUtils.getAPIService();

        HashMap query = new HashMap();
        query.put("category", "calzado");
        query.put("username", mUser.getEmail());

        Call<List<Garment>> call = mAPIService.getGarment(query);

        call.enqueue(new Callback<List<Garment>>() {
            @Override
            public void onResponse(Call<List<Garment>> call, Response<List<Garment>> response) {

                myShoes = response.body();
                fillListView(myShoes);
            }

            @Override
            public void onFailure(Call<List<Garment>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Method that when called will remove from the list a deleted garment (by it's postion).
     *
     * @param requestCode CARD_ACTION
     * @param resultCode  RESULT_OK
     * @param data        data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CARD_ACTION && resultCode == RESULT_OK) {
            int pos = data.getExtras().getInt("pos");

            mAdapter.removeItem(pos);
            mAdapter.notifyDataSetChanged();
        }
    }

}
