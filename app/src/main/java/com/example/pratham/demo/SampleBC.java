package com.example.pratham.demo;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


public class SampleBC extends BroadcastReceiver {
    static int noOfTimes = 0;

    // Method gets called when Broad Case is issued from MainActivity for every 10 seconds
    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub
        noOfTimes++;
        Toast.makeText(context, "BC Service Running for " + noOfTimes + " times", Toast.LENGTH_SHORT).show();
//        final Intent intnt = new Intent(context, MyService.class);
//        // Set unsynced count in intent data
//        intnt.putExtra("intntdata", "Unsynced Rows Count "+ noOfTimes);
//        // Call MyService
//        context.startService(intnt);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        SharedPreferences preferences = context.getSharedPreferences("Sample", //MainActivity.PreferencesName,
                MODE_PRIVATE);
        params.add("email_id", preferences.getString("user_profile_email", ""));
        params.add("travel_date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        params.add("direction", "to");
        // Checks if new records are inserted in Remote MySQL DB to proceed with Sync operation
        client.post("http://192.168.43.141/badal/check_req.php",params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                System.out.println(response);
                ArrayList<HashMap<String, String>> usersynclist;
                usersynclist = new ArrayList<HashMap<String, String>>();
                // Create GSON object
                Gson gson = new GsonBuilder().create();
                try {
                    // Extract JSON array from the response
                    Log.d("ERROR PRATHAM ", response);
                    JSONArray arr = new JSONArray(response);
                    System.out.println(arr.length());
                    // If no of array elements is not zero
                    if(arr.length() != 0){

//                            // Loop through each array element, get JSON object which has userid and username
//                            for (int i = 0; i < arr.length(); i++) {
//                                // Get JSON object
//                                JSONObject obj = (JSONObject) arr.get(i);
//                                //  System.out.println(obj.get("userId"));
//                                //System.out.println(obj.get("userName"));
//                                // DB QueryValues Object to insert into SQLite
//                                queryValues = new HashMap<String, String>();
//                                // Add userID extracted from Object
//                                queryValues.put("assemblyNumber", obj.get("assemblyNumber").toString());
//                                // Add userName extracted from Object
//                                queryValues.put("assemblyName", obj.get("assemblyName").toString());
//                                // Insert User into SQLite DB. You don't need to insert following line in your code. Just comment next line
//                                controller.insertAssembly(queryValues); // comment this
//                    /*HashMap<String, String> map = new HashMap<String, String>();
//                    // Add status for each User in Hashmap
//                    map.put("Id", obj.get("userId").toString());
//                    map.put("status", "1");
//                    usersynclist.add(map);*/
//                            }
                        // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                        //updateMySQLSyncSts(gson.toJson(usersynclist));
                        // Reload the Main Activity
                        // reloadActivity();
                        final Intent intnt = new Intent(context, MyService.class);
                        // Set unsynced count in intent data
                        intnt.putExtra("intntdata", "Total Requests "+ arr.length());
                        // Call MyService
                        context.startService(intnt);
                    }
                    else {
                        // no result
                       // Toast.makeText(context, "No result", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // TODO Auto-generated method stub
                if(statusCode == 404){
                  //  Toast.makeText(context, "404", Toast.LENGTH_SHORT).show();
                }else if(statusCode == 500){
                   // Toast.makeText(context, "500", Toast.LENGTH_SHORT).show();
                }else{
                  //  Toast.makeText(context, "Error occured!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}