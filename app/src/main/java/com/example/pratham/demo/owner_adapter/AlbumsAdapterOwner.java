package com.example.pratham.demo.owner_adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.pratham.demo.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Pratham on 2/22/2017.
 */

public class AlbumsAdapterOwner extends RecyclerView.Adapter<AlbumsAdapterOwner.MyViewHolder> {
    private Context mContext;
    private List<AlbumOwner> albumList;
    int lastPosition = -1;
    private int maxLenthOfText = 28;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, mobile, stop_name, email, latitude, longitude;
        public ImageView thumbnail;
        public CardView cardView;
        public FloatingActionButton floatingActionButton;
        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            name = (TextView) view.findViewById(R.id.txtName);
            mobile = (TextView) view.findViewById(R.id.txtMobile);
            stop_name = (TextView) view.findViewById(R.id.txtStopName);

            latitude = (TextView) view.findViewById(R.id.latitude);
            longitude = (TextView) view.findViewById(R.id.longitude);
            thumbnail = (ImageView) view.findViewById(R.id.imageView_Pic);
            email = (TextView) view.findViewById(R.id.txtEmail);
            floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        }
    }

    public AlbumsAdapterOwner(Context mContext, List<AlbumOwner> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_card_owner, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final AlbumOwner album = albumList.get(position);
        String n = album.getName();
        if(n.length() > maxLenthOfText)
            holder.name.setText(n.substring(0, maxLenthOfText - 2) + "..");
        else
            holder.name.setText(n);
        n = album.getMobile_no();
        if(n.length() > maxLenthOfText)
            holder.mobile.setText(n.substring(0, maxLenthOfText - 2) + "..");
        else
            holder.mobile.setText(n);
        holder.stop_name.setText(album.getStop_name());
        holder.email.setText(album.getEmail());
        holder.latitude.setText(album.getStop_latitude());
        holder.longitude.setText(album.getStop_longitude());

        // loading album cover using Glide library
        Glide.with(mContext).load(album.getPhoto_url())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);


        // call Animation function (this is used for popup animation)
        setAnimation(holder.itemView, position);

        holder.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, holder.email.getText().toString(), Toast.LENGTH_SHORT).show();
                String DisplayName = holder.name.getText().toString();
                String MobileNumber = holder.mobile.getText().toString();
                String HomeNumber = "";
                String WorkNumber = "";
                String emailID = holder.email.getText().toString();
                String company = "";
                String jobTitle = "";
                Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                contactIntent
                        .putExtra(ContactsContract.Intents.Insert.NAME, DisplayName)
                        .putExtra(ContactsContract.Intents.Insert.EMAIL, emailID)
                        .putExtra(ContactsContract.Intents.Insert.PHONE, MobileNumber);

                ((Activity) mContext).startActivityForResult(contactIntent, 1);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to accept request?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // send request
                                String URLtoWebsite = "http://10.100.103.158/location/";
                                AsyncHttpClient client = new AsyncHttpClient();
                                // Http Request Params Object
                                RequestParams params = new RequestParams();
                                final ProgressDialog prgDialog1 = new ProgressDialog(mContext);
                                prgDialog1.setMessage("Sending Request..Please wait..");
                                prgDialog1.setCancelable(true);
                                prgDialog1.show();


                                String stop_name = ((TextView) v.findViewById(R.id.txtStopName)).getText().toString();
                                final String email = ((TextView) v.findViewById(R.id.txtEmail)).getText().toString();
                                final String latitude = ((TextView) v.findViewById(R.id.latitude)).getText().toString();
                                final String longitude = ((TextView) v.findViewById(R.id.longitude)).getText().toString();

                                SharedPreferences preferences = mContext.getSharedPreferences("Sample", //MainActivity.PreferencesName,
                                        MODE_PRIVATE);
                                params.add("owner_email_id", preferences.getString("user_profile_email", ""));
                                params.add("travel_date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                                params.add("direction", "to");
                                params.add("pass_email_id", email);
                                params.add("stop_latitude", latitude);
                                params.add("stop_longitude", longitude);
                                System.out.println(preferences.getString("user_profile_email", "") + " " +
                                        new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " " +
                                        "to" + " " +
                                        stop_name + " " +
                                        email
                                );
                                // Make Http call to getusers.php
                                //Log.d("my" ,URLtoWebsite + "getallAssembly.php" );
                                // "getallAssembly.php" its a name of php file. I have sent this file to you
                                client.post(URLtoWebsite + "accept_pass_req.php", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(String response) {
                                        // Hide ProgressBar
                                        System.out.println(response);
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
                                            String obj =  arr.get(0).toString();
                                            Toast.makeText(mContext, obj.toString(), Toast.LENGTH_SHORT).show();
                                            if (obj.equalsIgnoreCase("success")) {
                                                String uri = "google.navigation:q=" + latitude + "," + longitude + "&mode=d";
                                                Uri gmmIntentUri = Uri.parse(uri);
                                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                mapIntent.setPackage("com.google.android.apps.maps");
                                                mContext.startActivity(mapIntent);

                                                // if you picker passenger or not
                                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                                builder.setMessage("Have you picked " + email + " ?")
                                                        .setCancelable(false)
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                Uri gmmIntentUri = Uri.parse("google.navigation:q=18.5294,73.8566&mode=d");
                                                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                                mapIntent.setPackage("com.google.android.apps.maps");
                                                                mContext.startActivity(mapIntent);
                                                            }
                                                        })
                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                            }
                                                        });
                                                AlertDialog alert = builder.create();
                                                alert.show();
                                            }
                                            else {
                                                Toast.makeText(mContext, obj, Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(mContext, "Requested resource not found", Toast.LENGTH_LONG).show();
                                        } else if (statusCode == 500) {
                                            Toast.makeText(mContext, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(mContext, "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(500);//animatation duration
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
