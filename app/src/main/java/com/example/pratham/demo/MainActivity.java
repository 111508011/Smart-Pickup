package com.example.pratham.demo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.ClickEffectType;
import com.nightonke.boommenu.Types.OrderType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import java.util.Calendar;
import java.util.Random;



public class MainActivity extends AppCompatActivity
        implements
        BoomMenuButton.OnSubButtonClickListener,
        BoomMenuButton.AnimatorListener,
        GoogleApiClient.OnConnectionFailedListener {
    private BoomMenuButton boomMenuButton;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Context mContext = this;
    private boolean isInit = false;
    private SignInButton google;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 007;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//       setSupportActionBar(toolbar);
//        mContext = getApplicationContext();
        google = (SignInButton) findViewById(R.id.google);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        google.setSize(SignInButton.SIZE_STANDARD);
        google.setScopes(gso.getScopeArray());
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        Button button = (Button) findViewById(R.id.gotomap);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, Register.class));
            }
        });
        Button button1 = (Button) findViewById(R.id.gotocurrentlocation);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MapsActivity.class));
            }
        });
        Button button2 = (Button) findViewById(R.id.gotoseek);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, PlacePickerActivity.class));
            }
        });

        Button button3 = (Button) findViewById(R.id.buttonDistancce);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, Distance.class));
            }
        });
        Button button4 = (Button) findViewById(R.id.shareLocation);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, Updates.class));
            }
        });
        Button button5 = (Button) findViewById(R.id.openmap);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=18.5294,73.8566&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        Button home = (Button) findViewById(R.id.goHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, Home.class));
            }
        });

        Button homeOwner = (Button) findViewById(R.id.goHomeOwner);
        homeOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, HomeOwner.class));
            }
        });

        boomMenuButton = (BoomMenuButton) findViewById(R.id.boom);

        boomMenuButton.setDuration(1 * 500);
        boomMenuButton.setDelay((0 + 1) * 100);
        boomMenuButton.setRotateDegree(2 * 360);
        boomMenuButton.setAutoDismiss(true);
        boomMenuButton.setShowOrderType(OrderType.DEFAULT); //type : REVERSE, RANDOM
        boomMenuButton.setHideOrderType(OrderType.DEFAULT); //type : REVERSE, RANDOM
        boomMenuButton.setClickEffectType(ClickEffectType.RIPPLE); //type: NORMAL
        // initBoom();

        Intent alarmIntent = new Intent(getApplicationContext(), SampleBC.class);
        // Pending Intent Object
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Alarm Manager Object
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Alarm Manager calls BroadCast for every Ten seconds (10 * 1000), BroadCase further calls service to check if new records are inserted in
        // Remote MySQL DB
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 5000, 1 * 1000, pendingIntent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
           GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

        private void handleSignInResult(GoogleSignInResult result) {
                Log.d(TAG, "handleSignInResult:" + result.isSuccess() + " " + result.getSignInAccount());
                if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct =  result.getSignInAccount();

                Log.e(TAG, "display name: " + acct.getDisplayName());

                String personName = acct.getDisplayName();
                String personPhotoUrl = "";
                if (acct.getPhotoUrl() != null)
                personPhotoUrl = acct.getPhotoUrl().toString();
                String email = acct.getEmail();

                Log.e(TAG, "Name: " + personName + ", email: " + email
                + ", Image: " + personPhotoUrl);
                SharedPreferences preferences = getSharedPreferences("Sample", //MainActivity.PreferencesName,
                MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("user_profile_name", personName);
                editor.putString("user_profile_pic", personPhotoUrl);
                editor.putString("user_profile_email", email);
                editor.commit();

                Intent intent = new Intent(this, Register.class);
                startActivity(intent);
                finish();
                Toast.makeText(this, personName + "\n" + email, Toast.LENGTH_SHORT).show();
                } else {
                // Signed out, show unauthenticated UI.
                    Toast.makeText(this, "Login Failed"+result.getStatus(), Toast.LENGTH_SHORT).show();
                }
        }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            // An unresolvable error has occurred and Google APIs (including Sign-In) will not
                // be available.
                Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!isInit) {
            initBoom();
        }
        isInit = true;
    }

    private void initBoom() {
        int number = 3; //number of buttons

        Drawable[] drawables = new Drawable[number];
        int[] drawablesResource = new int[]{
                R.drawable.mark,
                R.drawable.refresh,
                R.drawable.copy,
                R.drawable.heart,
                R.drawable.info,
                R.drawable.like,
                R.drawable.record,
                R.drawable.search,
                R.drawable.settings
        };
        for (int i = 0; i < number; i++)
            drawables[i] = ContextCompat.getDrawable(mContext, drawablesResource[i]);

        String[] STRINGS = new String[]{
                "Mark",
                "Refresh",
                "Copy",
                "Heart",
                "Info",
                "Like",
                "Record",
                "Search",
                "Settings"
        };
        String[] strings = new String[number];
        for (int i = 0; i < number; i++)
            strings[i] = STRINGS[i];

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
                .place(PlaceType.CIRCLE_3_1) //type : CIRCLE_1_1 to n_n
                .boomButtonShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .onSubButtonClick(this)
                .animator(this)
                .init(boomMenuButton);
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

    @Override
    public void onClick(int buttonIndex) {
        Toast.makeText(this, "On click " +
                boomMenuButton.getTextViews()[buttonIndex].getText().toString() +
                " button", Toast.LENGTH_SHORT).show();
        new CustomDialog().showCustomDialog(mContext, "This Is msg send from mainActivity");
    }
    private String[] Colors = {
            "#F44336",            "#E91E63",            "#9C27B0",            "#2196F3",
            "#03A9F4",            "#00BCD4",            "#009688",            "#4CAF50",
            "#8BC34A",            "#CDDC39",            "#FFEB3B",            "#FFC107",
            "#FF9800",            "#FF5722",            "#795548",            "#9E9E9E",
            "#607D8B"};
    public int GetRandomColor() {
        Random random = new Random();
        int p = random.nextInt(Colors.length);
        return Color.parseColor(Colors[p]);
    }
}
