package com.example.app;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class CreateAccount extends AsyncTask<String, Void, String> {

    //Creates Activity for the AsyncTask
    private final Activity host;
    CreateAccount(Activity host)
    {
        this.host = host;
    }
    @Override
    protected String doInBackground(String... args) {
        String firstname = args[0];
        String lastname = args[1];
        String username = args[2];
        String password = args[3];
        String email = args[4];
        try {
            String link = "http://www.kyleleber.com/Android/createAccount.php";
            String data = URLEncoder.encode("username", "UTF-8") + "="
                    + URLEncoder.encode(username, "UTF-8") + "&"
                    + URLEncoder.encode("password", "UTF-8") + "="
                    + URLEncoder.encode(password, "UTF-8") + "&"
                    + URLEncoder.encode("email", "UTF-8") + "="
                    + URLEncoder.encode(email, "UTF-8") + "&"
                    + URLEncoder.encode("lastname", "UTF-8") + "="
                    + URLEncoder.encode(lastname, "UTF-8") + "&"
                    + URLEncoder.encode("firstname", "UTF-8") +"="
                    + URLEncoder.encode(firstname, "UTF-8");
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
            return sb.toString();
        } catch (Exception e) {
            Log.v("Exception:", e.getMessage());
        }
        return null;
    }
    @Override
    protected void onPostExecute(String response)
    {
        super.onPostExecute(response);
        char status = response.charAt(0);
        switch(status){
            case '1':
                final Dialog dialog = new Dialog(host);
                dialog.setContentView(R.layout.account_creation_verification);
                dialog.setTitle("Account Verification Status");
                // set the custom dialog components - text, image and button

                Button dialogButton = (Button) dialog.findViewById(R.id.ok);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }

                });
                dialog.show();
                break;
            case '2':
                ((TextView)host.findViewById(R.id.accountCreationResponse)).setText("Username is taken");
                break;
            case '3':
                ((TextView)host.findViewById(R.id.accountCreationResponse)).setText("Email is in use");
                break;
            case '4':
                ((TextView)host.findViewById(R.id.accountCreationResponse)).setText("Must Enter a First Name");
                break;
            case '5':
                ((TextView)host.findViewById(R.id.accountCreationResponse)).setText("Must Enter a Last Name");
                break;
            case '6':
                ((TextView)host.findViewById(R.id.accountCreationResponse)).setText("Must Enter a Username");
                break;
            case '7':
                ((TextView)host.findViewById(R.id.accountCreationResponse)).setText("Password must be at least 7 characters");
                break;
            case '8':
                ((TextView)host.findViewById(R.id.accountCreationResponse)).setText("Invalid Email");
                break;

        }

    }
    public void onBackPressed(){
        //activity.player.start();

    }
}
