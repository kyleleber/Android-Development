package com.example.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class HighScores extends AsyncTask<Integer, Void, JSONObject>
{
    private final Activity host;
    HighScores(Activity host)
    {
        this.host = host;
    }

    // Runs in background thread
    @Override
    protected JSONObject doInBackground(Integer... args) {
        NetworkCheck nc = new NetworkCheck();
        if(nc.CheckInternet(host)){
            String result;
            JSONObject jsonArray;
            String position = args[0].toString();
            System.out.println("GAME ID: " + position);
            try {
                String link = "http://www.kyleleber.com/Android/getHighScores.php";
                String data = URLEncoder.encode("gameid", "UTF-8") + "="
                        + URLEncoder.encode(position, "UTF-8");
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

                //System.out.println("CONTENTS OF RETURN: " + sb.toString());
                result = sb.toString();
                jsonArray = new JSONObject(result);
                return jsonArray;
            }catch(Throwable t){

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject highscores)
    {
        super.onPostExecute(highscores);
        try {
            if(highscores != null){
                JSONArray array = highscores.getJSONArray("highscores");
                for(int x=0; x<array.length();x++){

                    //Setting up the R.id fields to dynamically change values of textviews
                    Class<R.id> c = R.id.class;



                    Field username = c.getField("highscore" + (int)(x+1) + "username");
                    int uname = username.getInt(null);  // pass in null, since field is a static field.

                    Field score = c.getField("highscore" + (int)(x+1) + "score");
                    int s = score.getInt(null);

                    //Add back in device when formatting is correct. HighScoresActivity needs changed
				/*
				Field device = c.getField("highscore" + (int)(x+1) + "device");
				int d = device.getInt(null);
				*/
                    Field date = c.getField("highscore" + (int)(x+1) + "date");
                    int hsdate = date.getInt(null);

                    TextView t;
                    t=(TextView)host.findViewById(uname);
                    t.setText(array.getJSONObject(x).getString("userid"));

                    t=(TextView)host.findViewById(s);
                    t.setText(array.getJSONObject(x).getString("score"));
				
				/*
				t=(TextView)host.findViewById(d);
				t.setText(array.getJSONObject(x).getString("device"));
				*/

                    t=(TextView)host.findViewById(hsdate);
                    t.setText(array.getJSONObject(x).getString("highscoredate"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
