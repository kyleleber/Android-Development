package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;


public class LoginTask extends AsyncTask<String, Void, Boolean> {
    boolean behindFirewall;
    private final Activity host;

    //AsyncTasks run in the background so the current view needs
    //to be referenced this way.
    LoginTask(Activity host) {
        this.host = host;
    }

    // Runs in background thread
    @Override
    protected Boolean doInBackground(String... args) {
        String username = args[0];
        String password = args[1];
        if (this.testInet()) {
            try {
                //location of the PHP file
                String link = "http://www.kyleleber.com/Android/login.php";
                String data = URLEncoder.encode("username", "UTF-8") + "="
                        + URLEncoder.encode(username, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "="
                        + URLEncoder.encode(password, "UTF-8");
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.getOutputStream().write(data.getBytes());

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                Log.v("SB CONTAINS: ", sb.toString());
                if (sb.toString().equals("1")) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.host);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", username);
                    editor.commit();
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                Log.v("Exception:", e.getMessage().toString());
                return false;
            }
        } else {
            behindFirewall = true;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean loginSuccessful) {
        super.onPostExecute(loginSuccessful);
        if (loginSuccessful) {
            host.finish();
            Intent intent = new Intent(host, GameList.class);
            host.startActivity(intent);
        } else {
            //Should only occur if the request times out
            //Probable causes are poor internet connection / server does not exist
            Log.v("Behind Firewall", String.valueOf(behindFirewall));
            if (behindFirewall) {
                ((TextView) host.findViewById(R.id.loginresponse)).setText("Check Firewall Settings");
            } else {
                ((TextView) host.findViewById(R.id.loginresponse)).setText("Invalid Username / Password");
            }
        }
    }


    public static boolean testInet() {
        //may be behind a firewall and needs authenticated.

        //Creates socket
        Socket socket = null;
        try {
            socket = new Socket("www.google.com", 80);
            return true;
        }catch(UnknownHostException uhe){
            return false;
        }catch(IOException e) {
            return false;
        }
    }
}