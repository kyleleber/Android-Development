package com.example.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.TextView;

public class LogUser extends Activity {


    public void setUser(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String username = preferences.getString("username", "nouser");
        TextView t = new TextView(this);
        t = (TextView)findViewById(R.id.loginresponse);
        t.setText("Login Response:" + username);
    }
}
