package com.example.pratham.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PassangerResistration extends AppCompatActivity {
    private static final int PLACE_PICKER_REQUEST = 1;
    private TextView mAddress;
    private TextView mAttributions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passanger_resistration);

        mAddress = (TextView) findViewById(R.id.txtAddress);
        mAttributions = (TextView) findViewById(R.id.textView3);

        Button pickerButton = (Button) findViewById(R.id.pickerButton);
        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    //  intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    Intent intent = intentBuilder.build(PassangerResistration.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        FloatingActionButton button = (FloatingActionButton)findViewById(R.id.buttonNext);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URLtoWebsite = "http://192.168.43.141/badal/";
                AsyncHttpClient client = new AsyncHttpClient();
                // Http Request Params Object
                RequestParams params = new RequestParams();
                final ProgressDialog prgDialog1 = new ProgressDialog(PassangerResistration.this);
                prgDialog1.setMessage("Sending Registration data..Please wait..");
                prgDialog1.setCancelable(true);
                prgDialog1.show();

                SharedPreferences preferences = getSharedPreferences("Sample", //MainActivity.PreferencesName,
                        MODE_PRIVATE);
                //new adding params
                params.add("pass_name", preferences.getString("user_profile_name", ""));
                params.add("email_id", preferences.getString("user_profile_email", ""));
                params.add("mob_no", preferences.getString("user_mobile", ""));
                params.add("photo_url", preferences.getString("user_profile_pic", ""));
                params.add("home_latitude", String.valueOf(preferences.getFloat("user_home_lat", 0.1f)));
                params.add("home_longitude", String.valueOf(preferences.getFloat("user_home_lon", 0.1f)));
                // Make Http call to getusers.php
                //Log.d("my" ,URLtoWebsite + "getallAssembly.php" );
                // "getallAssembly.php" its a name of php file. I have sent this file to you
                client.post(URLtoWebsite + "passenger_register.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        // Hide ProgressBar
                        prgDialog1.hide();
                        // Update SQLite DB with response sent by getusers.php
                        ArrayList<HashMap<String, String>> usersynclist;
                        usersynclist = new ArrayList<HashMap<String, String>>();
                        // Create GSON object
                        Gson gson = new GsonBuilder().create();
                        try {
                            // Extract JSON array from the response
                            JSONArray arr = new JSONArray(response);
                            System.out.println(arr.length() + " " + response);
                            String obj =  arr.get(0).toString();
                            Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_SHORT).show();

                            if (obj.equalsIgnoreCase("success")) {
                                SharedPreferences preferences = getSharedPreferences("Sample", //MainActivity.PreferencesName,
                                        MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean("is_registered", true);
                                editor.putBoolean("is_owner", false);
                                editor.commit();
                                startActivity(new Intent(getApplicationContext(), Home.class));
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
