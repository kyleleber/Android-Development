package com.example.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class LogoutTask extends AsyncTask<String, Void, Boolean>
{
    private final Activity host;
    LogoutTask(Activity host)
    {
        this.host = host;
    }

    // Runs in background thread
    @Override
    protected Boolean doInBackground(String... args) {
        String username = args[0];

        try {
            String link = "http://www.kyleleber.com/Android/logout.php";
            String data = URLEncoder.encode("username", "UTF-8") + "="
                    + URLEncoder.encode(username, "UTF-8");
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
            Log.v("SB CONTAINS: " , sb.toString());
            if(sb.toString().equals("logged out")){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            Log.v("Exception:", e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean logoutsuccessful)
    {
        super.onPostExecute(logoutsuccessful);
    }
}
