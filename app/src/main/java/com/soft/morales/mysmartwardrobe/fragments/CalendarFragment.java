package com.soft.morales.mysmartwardrobe.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.gson.Gson;
import com.soft.morales.mysmartwardrobe.CheckLookActivity;
import com.soft.morales.mysmartwardrobe.R;
import com.soft.morales.mysmartwardrobe.model.Look;
import com.soft.morales.mysmartwardrobe.model.User;
import com.soft.morales.mysmartwardrobe.model.persist.APIService;
import com.soft.morales.mysmartwardrobe.model.persist.ApiUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static com.soft.morales.mysmartwardrobe.CheckLookActivity.CHECK_RESULT_ACTIVITY;

public class CalendarFragment extends Fragment {

    // We declare our variables
    private String dayString, dayString2, monthString;

    private int dayNumber, monthNumber, yearNumber;

    private CalendarView mCalendarView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 05, 1);

        //We get the CalendarView from our layout.
        mCalendarView = (CalendarView) rootView.findViewById(R.id.calendarView);

        //We set the date tou our calendarView from the generated one (01 June 2018).
        try {
            mCalendarView.setDate(calendar);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        /**
         * OnClick events for our calendarView.
         * -->
         * We get day, month and year so we can use the data later.
         */
        mCalendarView.setOnDayClickListener(new OnDayClickListener() {

            @SuppressLint("DefaultLocale")
            @Override
            public void onDayClick(EventDay eventDay) {

                dayString = eventDay.getCalendar().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                dayString2 = eventDay.getCalendar().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                dayNumber = eventDay.getCalendar().get(Calendar.DAY_OF_MONTH);
                monthNumber = eventDay.getCalendar().get(Calendar.MONTH);
                yearNumber = eventDay.getCalendar().get(Calendar.YEAR);

                // Month starts from 0, se we need to increment it by one.
                monthNumber++;

                // Due to our BBDD format, we need to add a 0 to our month in case it's only one digit
                monthString = String.format("%02d", monthNumber);
                dayString = String.format("%02d", dayNumber);

                // We show a dialog asking an option when the user is clicking on a day
                AlertDialog diaBox = AskOption();
                diaBox.show();

            }

        });

        return rootView;

    }

    /**
     * Method that will retrieve all the looks of the loged in user, and will add an icon on the days that the user owns a look.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Declare new Gson
        Gson gson = new Gson();
        // Declare SharedPreferences variable so we can acced to our SharedPreferences
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), getActivity().MODE_PRIVATE);

        // We build a User from our given information from the sharedPref file (User in Gson format)
        User mUser = gson.fromJson(sharedPref.getString("user", ""), User.class);

        // We'll introduce our username to our query, so we obtain the looks from the loged in user.
        HashMap query = new HashMap();
        query.put("username", mUser.getEmail());

        APIService api = ApiUtils.getAPIService();
        Call<List<Look>> call = api.getLooks(query);

        call.enqueue(new Callback<List<Look>>() {
            @Override
            public void onResponse(Call<List<Look>> call, Response<List<Look>> response) {

                // We obtain our looks from the call
                List<Look> looks = response.body();

                // We build a list of events (where we'll add the icon, in case there's a look for that day).
                List<EventDay> events = new ArrayList<>();

                DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

                // In case looks is not null
                if (looks != null && looks.size() > 0) {
                    for (int i = 0; i < looks.size(); i++) {
                        Calendar calendar = Calendar.getInstance();
                        try {
                            // We'll get the date
                            Date date = format.parse(looks.get(i).getDate());
                            calendar.setTime(date);
                            // Set the hanger icon for that day
                            events.add(new EventDay(calendar, R.drawable.icon_hanger));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }
                // Finally we set the events to our CalendarView.
                mCalendarView.setEvents(events);

            }

            @Override
            public void onFailure(Call<List<Look>> call, Throwable t) {
                Log.d("Error: ", "Connection failed");
            }
        });

    }

    /**
     * Method we call when user clicks over a day from the calendar.
     * We'll show a Dialog and ask which is his option, dismiss, or check the look of that day.
     * If there's a look, we'll show it to the user. If there's not a look, we'll report it to him.
     *
     * @return mAskOptionDialog.
     */
    private AlertDialog AskOption() {

        AlertDialog mAskOptionDialog = new AlertDialog.Builder(getContext())
                //set message, title, and icon
                .setTitle(dayString2.toUpperCase() + " " + dayNumber)
                .setMessage("Qué le gustaría hacer?")
                .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Close the dialog when the user clicks dismiss.
                        dialog.dismiss();
                    }
                })

                .setPositiveButton("Consultar look", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // We store the selected date into a date variable.
                        String date = String.valueOf(dayString).trim() + "-" + String.valueOf(monthString).trim() + "-" + String.valueOf(yearNumber).trim();

                        // We send it over a bundle.
                        Bundle bundle = new Bundle();
                        bundle.putString("date", date);
                        // We start a new CheckLookActivity.
                        Intent intent = new Intent(getContext(), CheckLookActivity.class);
                        intent.putExtras(bundle);

                        try {
                            startActivityForResult(intent, CHECK_RESULT_ACTIVITY);
                        } catch (Exception e) {
                            Log.d("Exception:", String.valueOf(e));
                        }

                        dialog.dismiss();

                    }
                })
                .create();

        return mAskOptionDialog;

    }

    /**
     * When the user checkLook on a day that there's not a look, we'll show this error warning message.
     *
     * @param requestCode CHECK_RESULT_ACTIVITY
     * @param resultCode  RESULT_CANCELED
     * @param data        data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECK_RESULT_ACTIVITY && resultCode == RESULT_CANCELED) {
            Toast.makeText(getContext(), "No existe look para ese día", Toast.LENGTH_SHORT).show();
        }
    }

}
