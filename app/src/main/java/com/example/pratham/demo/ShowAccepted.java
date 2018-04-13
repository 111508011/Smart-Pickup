package com.example.pratham.demo;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.example.pratham.demo.owner_adapter.AlbumOwner;
import com.example.pratham.demo.owner_adapter.AlbumsAdapterAccepted;
import com.example.pratham.demo.owner_adapter.AlbumsAdapterOwner;
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
import java.util.List;

public class ShowAccepted extends AppCompatActivity {
    private RecyclerView recyclerView;
    public static AlbumsAdapterAccepted adapter;
    public static List<AlbumOwner> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_accepted);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        albumList = new ArrayList<>();
        adapter = new AlbumsAdapterAccepted(this, albumList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new ShowAccepted.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        getData();
    }
    private void getData() {
        String URLtoWebsite = "http://10.100.103.158/location/";
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        final ProgressDialog prgDialog1 = new ProgressDialog(ShowAccepted.this);
        prgDialog1.setMessage("Getting data..Please wait..");
        prgDialog1.setCancelable(true);
        prgDialog1.show();

        SharedPreferences preferences = getSharedPreferences("Sample", //MainActivity.PreferencesName,
                MODE_PRIVATE);
        params.add("email_id", preferences.getString("user_profile_email", ""));
        params.add("travel_date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        params.add("direction", "to");

        client.post(URLtoWebsite + "check_pass_accept.php", params, new AsyncHttpResponseHandler() {
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
                    System.out.println("RESPONCE " + response);
                    JSONArray arr = new JSONArray(response);
                    System.out.println(arr.length());
                    // If no of array elements is not zero
                    if (arr.length() != 0) {
                        // Loop through each array element, get JSON object which has userid and username
                        for (int i = 0; i < arr.length(); i++) {
                            // Get JSON object
                            JSONObject obj = (JSONObject) arr.get(i);

                            AlbumOwner a = new AlbumOwner(obj.getString("email_id"), obj.getString("photo_url"), obj.getString("owner_name"), obj.getString("mob_no"),
                                    obj.getString("stop_name"), obj.getString("stop_latitude"),
                                    obj.getString("stop_longitude"));

                            albumList.add(a);
                            // DB QueryValues Object to insert into SQLite
//                            queryValues = new HashMap<String, String>();
//                            // Add userID extracted from Object
//                            queryValues.put("assemblyNumber", obj.get("assemblyNumber").toString());
//                            // Add userName extracted from Object
//                            queryValues.put("assemblyName", obj.get("assemblyName").toString());

                        }
                        adapter.notifyDataSetChanged();
                        // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                        //updateMySQLSyncSts(gson.toJson(usersynclist));
                        // Reload the Main Activity
                        // reloadActivity();
                    }
                    else {
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
    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
