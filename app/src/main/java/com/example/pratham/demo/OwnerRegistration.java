package com.example.pratham.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OwnerRegistration extends AppCompatActivity {
    private static final int PLACE_PICKER_REQUEST = 1;
    private TextView mAddress;
    private TextView mAttributions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_registration);

        mAddress = (TextView) findViewById(R.id.txtAddress);
        mAttributions = (TextView) findViewById(R.id.textView3);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroupVehicle);
        final EditText expecteddTime = (EditText) findViewById(R.id.expectedTime);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radio2wheeler)
                    capacity = 1;
                else
                    capacity = 3;
            }
        });
        expecteddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(OwnerRegistration.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        expecteddTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
//                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        Button pickerButton = (Button) findViewById(R.id.pickerButton);
        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    //  intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    Intent intent = intentBuilder.build(OwnerRegistration.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        FloatingActionButton goNext = (FloatingActionButton) findViewById(R.id.buttonNext);
        goNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int capacity = 0;
//                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//                        if (checkedId == R.id.radio2wheeler)
//                            capacity[0] = 1;
//                        else
//                            capacity[0] = 3;
//                    }
//                });
                String URLtoWebsite = "http://192.168.43.141/badal/";
                AsyncHttpClient client = new AsyncHttpClient();
                // Http Request Params Object
                RequestParams params = new RequestParams();
                final ProgressDialog prgDialog1 = new ProgressDialog(OwnerRegistration.this);
                prgDialog1.setMessage("Sending Registration data..Please wait..");
                prgDialog1.setCancelable(true);
                prgDialog1.show();

                SharedPreferences preferences = getSharedPreferences("Sample", //MainActivity.PreferencesName,
                        MODE_PRIVATE);
                //new adding params
                params.add("owner_name", preferences.getString("user_profile_name", ""));
                params.add("email_id", preferences.getString("user_profile_email", ""));
                params.add("mob_no", preferences.getString("user_mobile", ""));
                params.add("photo_url", preferences.getString("user_profile_pic", ""));
                params.add("expt_time", expecteddTime.getText().toString());
                //Toast.makeText(getApplicationContext(), "" + capacity[0], Toast.LENGTH_SHORT).show();

                params.add("quantity", "" + capacity);
                params.add("home_latitude", String.valueOf(preferences.getFloat("user_home_lat", 0.1f)));
                params.add("home_longitude", String.valueOf(preferences.getFloat("user_home_lon", 0.1f)));

                client.post(URLtoWebsite + "owner_register.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        // Hide ProgressBar
                        prgDialog1.hide();;
                        Gson gson = new GsonBuilder().create();
                        try {
                            // Extract JSON array from the response

                            System.out.println(" " + response);
                            JSONArray arr = new JSONArray(response);
                            String obj =  arr.get(0).toString();
                            Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_SHORT).show();
                            if (obj.equalsIgnoreCase("Success")) {
                                startActivity(new Intent(getApplicationContext(), PlaceMarkers.class));
                                finish();
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    // When error occured
                    @Override
                    public void onFailure(int statusCode, Throwable error, String content) {
                        // TODO Auto-generated method stub
                        // Hide ProgressBar
                        prgDialog1.hide();
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                        } else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
    static int capacity;
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
//        Log.d("DEBUG ", resultCode + " " + resultCode + " " + data.toString());
        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(data, this);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = (String) PlacePicker.getAttributions(getIntent());
            if (attributions == null) {
                attributions = "";
            }
            Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
            mAddress.setText(name + "\n" + address + " \n " + place.getLatLng());
            mAttributions.setText(Html.fromHtml(attributions));

            SharedPreferences preferences = getSharedPreferences("Sample", //MainActivity.PreferencesName,
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat("user_home_lat", ((float) place.getLatLng().latitude));
            editor.putFloat("user_home_lon", ((float) place.getLatLng().longitude));
            editor.commit();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
