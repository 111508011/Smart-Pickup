package com.example.pratham.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pratham.demo.adapter.Album;
import com.example.pratham.demo.adapter.AlbumsAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.ui.IconGenerator;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.ClickEffectType;
import com.nightonke.boommenu.Types.OrderType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Home extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, BoomMenuButton.OnSubButtonClickListener, BoomMenuButton.AnimatorListener {
    private RecyclerView recyclerView;
    public static AlbumsAdapter adapter;
    public static List<Album> albumList;
    private static final String TAG = "Home";
    private static final long INTERVAL = 1000 * 60 * 1; //1 minute
    private static final long FASTEST_INTERVAL = 1000 * 60 * 1; // 1 minute
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    GoogleMap googleMap;

    private BoomMenuButton boomMenuButton;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        albumList = new ArrayList<>();
        adapter = new AlbumsAdapter(this, albumList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //prepareAlbums();
        boomMenuButton = (BoomMenuButton) findViewById(R.id.boom);

        boomMenuButton.setDuration(1 * 500);
        boomMenuButton.setDelay((0 + 1) * 100);
        boomMenuButton.setRotateDegree(2 * 360);
        boomMenuButton.setAutoDismiss(true);
        boomMenuButton.setShowOrderType(OrderType.DEFAULT); //type : REVERSE, RANDOM
        boomMenuButton.setHideOrderType(OrderType.DEFAULT); //type : REVERSE, RANDOM
        boomMenuButton.setClickEffectType(ClickEffectType.RIPPLE);

    }
    private boolean isInit = false;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!isInit) {
            initBoom();
        }
        isInit = true;
    }

    public int GetRandomColor() {
        return Color.parseColor("#2196f3");
    }
    private void initBoom() {
        int number = 2; //number of buttons

        Drawable[] drawables = new Drawable[number];

        drawables[0] = ContextCompat.getDrawable(this, R.drawable.eye);
        drawables[1] = ContextCompat.getDrawable(this, R.drawable.refresh);

        String strings[] = {"View", "Refresh"};

        int[][] colors = new int[number][2];
        for (int i = 0; i < number; i++) {
            colors[i][1] = GetRandomColor();
            colors[i][0] = Util.getInstance().getPressedColor(colors[i][1]);
        }

        ButtonType buttonType = ButtonType.CIRCLE; // OR ButtonType.HAM

        // Now with Builder, you can init BMB more convenient
        new BoomMenuButton.Builder()
                .subButtons(drawables, colors, strings)
                .button(buttonType)
                .boom(BoomType.PARABOLA) //type : PARABOLA, LINE, HORIZONTAL_THROW, PARABOLA_2, HORIZONTAL_THROW_2
                .place(PlaceType.CIRCLE_2_1) //type : CIRCLE_1_1 to n_n
                .boomButtonShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .onSubButtonClick(this)
                .animator(this)
                .init(boomMenuButton);
    }

    private void getData() {
        String URLtoWebsite = "http://192.168.43.141/badal/";
        AsyncHttpClient client = new AsyncHttpClient();
        // Http Request Params Object
        RequestParams params = new RequestParams();
        final ProgressDialog prgDialog1 = new ProgressDialog(Home.this);
        prgDialog1.setMessage("Getting data..Please wait..");
        prgDialog1.setCancelable(true);
        prgDialog1.show();


        params.add("cur_latitude", String.valueOf(latLng.latitude));
        params.add("cur_longitude", String.valueOf(latLng.longitude));
        // add to or from from radiobutton
        // Make Http call to getusers.php
        //Log.d("my" ,URLtoWebsite + "getallAssembly.php" );
        // "getallAssembly.php" its a name of php file. I have sent this file to you
        client.post(URLtoWebsite + "get_dist.php", params, new AsyncHttpResponseHandler() {
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
                    System.out.println(response);
                    JSONArray arr = new JSONArray(response);
                    System.out.println(arr.length());
                    // If no of array elements is not zero
                    if (arr.length() != 0) {
                        // Loop through each array element, get JSON object which has userid and username
                        for (int i = 0; i < arr.length(); i++) {
                            // Get JSON object
                            JSONObject obj = (JSONObject) arr.get(i);
                            double distance = Double.parseDouble(obj.getString("dist"));

                            Album a = new Album(obj.getString("email_id"), obj.getString("photo_url"), obj.getString("owner_name"), obj.getString("mob_no")
                                    ,
                                    obj.getString("stop_name"), distance, obj.getString("expt_time"),
                                    obj.getString("stop_latitude"), obj.getString("stop_longitude"));
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

    @Override
    public void onClick(int buttonIndex) {
        String id = boomMenuButton.getTextViews()[buttonIndex].getText().toString();
        if (id.equalsIgnoreCase("view")) {
            startActivity(new Intent(getApplicationContext(), ShowAccepted.class));
        }
        else if (id.equalsIgnoreCase("refresh")) {
            albumList.clear();
            getData();
        }
//        Toast.makeText(this, "On click " +
//                boomMenuButton.getTextViews()[buttonIndex].getText().toString() +
//                " button", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        if (boomMenuButton.isClosed()) {
            super.onBackPressed();
        } else {
            boomMenuButton.dismiss();
        }
    }

    @Override
    public void toShow() {

    }

    @Override
    public void showing(float fraction) {

    }

    @Override
    public void showed() {

    }

    @Override
    public void toHide() {

    }

    @Override
    public void hiding(float fraction) {

    }

    @Override
    public void hided() {

    }


    /**
     * Adding few albums for testing
     */
//    private void prepareAlbums() {
//        String[] covers = new String[]{
//                "https://lh6.googleusercontent.com/-AD0gKY5FElE/AAAAAAAAAAI/AAAAAAAAAb4/jfvkcMwn3vA/photo.jpg",
//                "https://lh6.googleusercontent.com/-AD0gKY5FElE/AAAAAAAAAAI/AAAAAAAAAb4/jfvkcMwn3vA/photo.jpg",
//                "https://lh6.googleusercontent.com/-AD0gKY5FElE/AAAAAAAAAAI/AAAAAAAAAb4/jfvkcMwn3vA/photo.jpg",
//                "https://lh6.googleusercontent.com/-AD0gKY5FElE/AAAAAAAAAAI/AAAAAAAAAb4/jfvkcMwn3vA/photo.jpg"};
//
//        for (int i = 0; i < 2; i++) {
//            Album a = new Album("Maroon5", covers[0], "kef", "78978", "4325", 8, 0);
//            albumList.add(a);
//
//            a = new Album("Maroon5", covers[1], "kef", "78978", "4325", 8, 0);
//            albumList.add(a);
//            a = new Album("Maroon5", covers[2], "kef", "78978", "4325", 8, 0);
//            albumList.add(a);
//            a = new Album("Maroon5", covers[3], "kef", "78978", "4325", 8, 0);
//            albumList.add(a);
//        }
//
//        adapter.notifyDataSetChanged();
//    }

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

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
//        Toast.makeText(this, "")
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    LatLng latLng;
    boolean isSyncEnabled = true;
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");

        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(this, "loc " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
//        addMarker();
        if (isSyncEnabled)
            getData();
        isSyncEnabled = false;
    }

    private void addMarker() {
        MarkerOptions options = new MarkerOptions();

        // following four lines requires 'Google Maps Android API Utility Library'
        // https://developers.google.com/maps/documentation/android/utility/
        // I have used this to display the time as title for location markers
        // you can safely comment the following four lines but for this info
        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
        options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(mLastUpdateTime)));
        options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        options.position(currentLatLng);
        Marker mapMarker = googleMap.addMarker(options);
        long atTime = mCurrentLocation.getTime();
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
        mapMarker.setTitle(mLastUpdateTime);
        Log.d(TAG, "Marker added.............................");
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                13));
        Log.d(TAG, "Zoom done.............................");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }
}
