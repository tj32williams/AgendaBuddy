package com.example.myapplication.customcalendar;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * The Main Activity page of the app.
 *
 * This page comes after the user logged in
 */
public class MainActivity extends AppCompatActivity {

    CustomCalendarView customCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Account.initializeAccount(this, this.getBaseContext());

        customCalendarView = findViewById(R.id.customCalendarView); // instantiate the calendar

        if(Account.isActive()){
            String s = "";

            if(Account.hasName())
                s = "Hello " + Account.name() + "! Welcome to Agenda Buddy!";

            else if(Account.hasUsername())
                s = "Hello " + Account.username() + "! Welcome to Agenda Buddy!";

            else if(Account.hasFamName())
                s = "Hello " + Account.famName() + "! Welcome to Agenda Buddy!";

            else if(Account.hasEmail())
                s = "Enjoy Agenda Buddy " + Account.email() + "!";

            if(!s.equals(""))
                Toast.makeText(this, s,Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Account activation failure.",Toast.LENGTH_SHORT).show();
        }
    }

}
