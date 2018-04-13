package com.example.pratham.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String[] usertype = {" "};
        setContentView(R.layout.activity_register);

        final TextView[] user = {(TextView) findViewById(R.id.txtUserName)};
        final TextView[] email = {(TextView) findViewById(R.id.txtUserEmail)};
        final ImageView[] profile = {(ImageView) findViewById(R.id.imageViewProfile)};
        final EditText mobile = (EditText) findViewById(R.id.editMobile);

        final SharedPreferences preferences = getSharedPreferences("Sample", //MainActivity.PreferencesName,
                MODE_PRIVATE);


        user[0].setText(preferences.getString("user_profile_name", "Not Set"));
        email[0].setText(preferences.getString("user_profile_email", "Not Set"));
        Glide.with(this)
                .load(preferences.getString("user_profile_pic", "Not Set"))
                .into(profile[0]);

        FloatingActionButton buttonNext = (FloatingActionButton) findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("user_mobile", mobile.getText().toString());
                editor.commit();
                switch (usertype[0]) {
                    case "Owner":
                        if (mobile.getText().toString().length() != 0 && mobile.getText().toString().length() == 10) {
                            startActivity(new Intent(getApplicationContext(), OwnerRegistration.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter Mobile no", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Passenger":
                        if (mobile.getText().toString().length() != 0 && mobile.getText().toString().length() == 10) {
                            startActivity(new Intent(getApplicationContext(), PassangerResistration.class));
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), "Enter Mobile no", Toast.LENGTH_SHORT).show();

                        }
                        break;
                    case " ":
                        Toast.makeText(getApplicationContext(),"Please select the user type",Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
            }
        });
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId == R.id.radioButtonOwner){
                    usertype[0] = "Owner";
                }else if(checkedId == R.id.radioButtonPass){
                    usertype[0] = "Passenger";
                }
            }
        });
    }
}
