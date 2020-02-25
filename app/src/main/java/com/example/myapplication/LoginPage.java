package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity implements View.OnClickListener {

    private String userText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userText = "";
        passwordText = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //Toast.makeText(this,"",Toast.LENGTH_LONG);
                //Toast.makeText(this,"Password entered!!!",Toast.LENGTH_SHORT).show();
            }
        });

        ImageView loginImage = findViewById(R.id.buddyIcon);
        loginImage.setOnClickListener(this);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        /*
        Button loginFragButton = findViewById(R.id.loginFragButton);
        loginFragButton.setOnClickListener(this);
         */
        TextView userTextField = findViewById(R.id.userTextField);
        userTextField.setOnClickListener(this);

        TextView pswdTextField = findViewById(R.id.pswdTextField);
        pswdTextField.setOnClickListener(this);

        //pswdTextField.setOn
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideKeyboard() {
        /*
        View v = this.getCurrentFocus();
        if(v == null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        System.out.println("ODSUVBSIBU");

         */
    }

    @Override
    public void onClick(View v) {

        //Intent intent;
        switch(v.getId()) {
            case R.id.accountNameButton:
                Intent intent = new Intent(this, LoginPage.class);
                startActivity(intent);
                break;

            case R.id.loginButton:
                //System.out.println("Login in initiated!!!!");
                Toast.makeText(this, "Login initiated!!!", Toast.LENGTH_SHORT).show();
                Intent intents = new Intent(this, SideMenu.class);
                startActivity(intents);
                //System.out.println("ougyifuviuogfiuyf");
                /*
                For later:

                When login is pressed. We need to make that syncing doesn't actually happen unless
                the user wants automatic updates.

                 */
                break;

            case R.id.pswdTextField:
                //System.out.println("Login in initiated!!!!");
                Toast.makeText(this, "Password entered!!!", Toast.LENGTH_SHORT).show();
                EditText text = (EditText) findViewById(R.id.userTextField);
                userText = text.getText().toString();
                EditText text2 = (EditText) findViewById(R.id.userTextField);
                passwordText = text2.getText().toString();

                hideKeyboard();

                String upText = "Entered:\n" + userText + "\n" + passwordText;

                Toast.makeText(this, upText, Toast.LENGTH_SHORT).show();


                text.setText("");

                // Not clearing text due to user still typing.
                text2.setText("");

                break;

            case R.id.userTextField:
                //System.out.println("Login in initiated!!!!");
                EditText textt = (EditText) findViewById(R.id.userTextField);
                String t = textt.getText().toString();

                if(!t.isEmpty() && !userText.isEmpty()){
                    if(!t.equals(userText)) {
                        View view = findViewById(R.id.userTextField);
                        Snackbar make = Snackbar.make(view, "Username updated.", Snackbar.LENGTH_LONG);
                        make.setAction("Username Updated", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(v.getContext(), "Username updated.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        make.show();
                    }
                } else if(!t.isEmpty()){
                    Toast.makeText(this,"Username entered!!!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Warning! Username is empty!.", Toast.LENGTH_SHORT).show();
                }

                userText = t;

                break;

            case R.id.buddyIcon:
                //System.out.println("Login in initiated!!!!");
                Toast.makeText(this,"Welcome to Agenda Buddy!!!",Toast.LENGTH_SHORT).show();
                break;

             default:
                 System.out.println("Something went wrong with one of the listeners.");

        }
    }
}
