//package com.example.pratham.demo;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.BitmapFactory;
//import android.location.Geocoder;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.support.v4.app.ActivityCompat;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Toast;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.CircleOptions;
//import com.google.android.gms.maps.model.GroundOverlayOptions;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.PolygonOptions;
//
//import java.io.IOException;
//
///**
// * Created by Paul on 8/11/15.
// */
//public class MapFragment extends SupportMapFragment implements GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        GoogleMap.OnInfoWindowClickListener,
//        GoogleMap.OnMapLongClickListener,
//        GoogleMap.OnMapClickListener,
//        GoogleMap.OnMarkerClickListener,
//        OnMapReadyCallback {
//
//    private GoogleApiClient mGoogleApiClient;
//    private Location mCurrentLocation;
//    Context context;
//
//    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
//            GoogleMap.MAP_TYPE_NORMAL,
//            GoogleMap.MAP_TYPE_HYBRID,
//            GoogleMap.MAP_TYPE_TERRAIN,
//            GoogleMap.MAP_TYPE_NONE};
//    private int curMapTypeIndex = 0;
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        setHasOptionsMenu(true);
//        context = getContext();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        if (!isLocationServiceEnabled()) {
//            new CustomDialog().showCustomDialog(context, "Please connect to Internet and enable location" +
//                            " to access this feature",
//                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//
//        }
//        initListeners();
//    }
//
//    private void initListeners() {
//        SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        mapFrag.getMapAsync((OnMapReadyCallback) this.context);
//        getMap().setOnMarkerClickListener(this);
//        getMap().setOnMapLongClickListener(this);
//        getMap().setOnInfoWindowClickListener(this);
//        getMap().setOnMapClickListener(this);
//    }
//
//    private void removeListeners() {
//        if (getMap() != null) {
//            getMap().setOnMarkerClickListener(null);
//            getMap().setOnMapLongClickListener(null);
//            getMap().setOnInfoWindowClickListener(null);
//            getMap().setOnMapClickListener(null);
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        removeListeners();
//    }
//
//    private void initCamera(Location location) {
//        CameraPosition position = CameraPosition.builder()
//                .target(new LatLng(location.getLatitude(), location.getLongitude()))
//                .zoom(16f)
//                .bearing(0.0f)
//                .tilt(0.0f)
//                .build();
//
//        getMap().animateCamera(CameraUpdateFactory.newCameraPosition(position), null);
//
//        getMap().setMapType(MAP_TYPES[curMapTypeIndex]);
//        getMap().setTrafficEnabled(true);
//        getMap().setMyLocationEnabled(true);
//        getMap().getUiSettings().setZoomControlsEnabled(true);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (mGoogleApiClient != null)
//            mGoogleApiClient.connect();
//        else if (!isLocationServiceEnabled()) {
//            new CustomDialog().showCustomDialog(context, "This Is msg send from MapActivity");
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//    }
//
//    @Override
//    public void onConnected(Bundle bundle) {
//        if (ActivityCompat.checkSelfPermission(this.context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mCurrentLocation != null)
//            initCamera( mCurrentLocation );
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        //handle play services disconnecting if location is being constantly used
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        //Create a default location if the Google API Client fails. Placing location at Googleplex
//        mCurrentLocation = new Location( "" );
//        mCurrentLocation.setLatitude( 37.422535 );
//        mCurrentLocation.setLongitude( -122.084804 );
//        initCamera(mCurrentLocation);
//    }
//
//    @Override
//    public void onInfoWindowClick(Marker marker) {
//        Toast.makeText( getActivity(), "Clicked on marker", Toast.LENGTH_SHORT ).show();
//    }
//
//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        marker.showInfoWindow();
//        return true;
//    }
//
//    @Override
//    public void onMapClick(LatLng latLng) {
//
//        MarkerOptions options = new MarkerOptions().position( latLng );
//        options.title( getAddressFromLatLng( latLng ) );
//
//        options.icon( BitmapDescriptorFactory.defaultMarker( ) );
//        getMap().addMarker( options );
//    }
//
//    @Override
//    public void onMapLongClick(LatLng latLng) {
//        MarkerOptions options = new MarkerOptions().position( latLng );
//        options.title( getAddressFromLatLng(latLng) );
//
//        options.icon( BitmapDescriptorFactory.fromBitmap(
//                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher ) ) );
//
//        getMap().addMarker(options);
//    }
//
//    private String getAddressFromLatLng( LatLng latLng ) {
//        Geocoder geocoder = new Geocoder( getActivity() );
//
//        String address = "";
//        try {
//            address = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 ).get( 0 ).getAddressLine( 0 );
//        } catch (IOException e ) {
//            e.printStackTrace();
//        }
//
//        return address;
//    }
//
//    private void drawCircle( LatLng location ) {
//        CircleOptions options = new CircleOptions();
//        options.center( location );
//        //Radius in meters
//        options.radius( 10 );
//        options.fillColor( getResources().getColor( R.color.fill_color ) );
//        options.strokeColor( getResources().getColor( R.color.stroke_color ) );
//        options.strokeWidth( 10 );
//        getMap().addCircle(options);
//    }
//
//    private void drawPolygon( LatLng startingLocation ) {
//        LatLng point2 = new LatLng( startingLocation.latitude + .001, startingLocation.longitude );
//        LatLng point3 = new LatLng( startingLocation.latitude, startingLocation.longitude + .001 );
//
//        PolygonOptions options = new PolygonOptions();
//        options.add(startingLocation, point2, point3);
//
//        options.fillColor( getResources().getColor( R.color.fill_color ) );
//        options.strokeColor( getResources().getColor( R.color.stroke_color ) );
//        options.strokeWidth( 10 );
//
//        getMap().addPolygon(options);
//    }
//
//    private void drawOverlay( LatLng location, int width, int height ) {
//        GroundOverlayOptions options = new GroundOverlayOptions();
//        options.position(location, width, height);
//
//        options.image( BitmapDescriptorFactory
//                .fromBitmap( BitmapFactory
//                    .decodeResource( getResources(), R.mipmap.ic_launcher ) ) );
//        getMap().addGroundOverlay(options);
//    }
//
//    private void toggleTraffic() {
//        getMap().setTrafficEnabled( !getMap().isTrafficEnabled() );
//    }
//
//    private void cycleMapType() {
//        if( curMapTypeIndex < MAP_TYPES.length - 1 ) {
//            curMapTypeIndex++;
//        } else {
//            curMapTypeIndex = 0;
//        }
//
//        getMap().setMapType( MAP_TYPES[curMapTypeIndex] );
//    }
//
//    @Override
//    public void onCreateOptionsMenu(
//            Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_main, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_clear: {
//                getMap().clear();
//                return true;
//            }
//            case R.id.action_circle: {
//                drawCircle( new LatLng( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude() ) );
//                return true;
//            }
//            case R.id.action_polygon: {
//                drawPolygon( new LatLng( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude() ) );
//                return true;
//            }
//            case R.id.action_overlay: {
//                drawOverlay( new LatLng( mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude() ), 250, 250 );
//                return true;
//            }
//            case R.id.action_traffic: {
//                toggleTraffic();
//                return true;
//            }
//            case R.id.action_cycle_map_type: {
//                cycleMapType();
//                return true;
//            }
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }
//    public boolean isLocationServiceEnabled(){
//        LocationManager locationManager = null;
//        boolean gps_enabled= false,network_enabled = false;
//
//        if(locationManager ==null)
//            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        try{
//            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        }catch(Exception ex){
//            //do nothing...
//        }
//
//        try{
//            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        }catch(Exception ex){
//            //do nothing...
//        }
//
//        return gps_enabled || network_enabled;
//
//    }
//}