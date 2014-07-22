package com.example.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

public class TargetActivity extends ActionBarActivity {

    ImageButton array[] = new ImageButton[20];
    ImageButton button1, button2, button3, button4, button5,
            button6, button7, button8, button9, button10,
            button11, button12, button13, button14, button15,
            button16, button17, button18, button19, button20;


    TextView txtScore;

    int score = 0;
    Random rand = new Random();

    int next = 0;
    int current = 0;
    int nextID = 0;
    int currentID = 0;
    int hitCount = 0;
    int missCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_activity);
        setup();
        array[current].setImageResource(R.drawable.target);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()

                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_target_activity, container, false);
            return rootView;
        }
    }


    public void setup(){
        //get starting point
        next = rand.nextInt(20);
        current = next;

        //relate each button to a target
        button1 = (ImageButton)findViewById(R.id.imageButton);
        button2 = (ImageButton)findViewById(R.id.imageButton2);
        button3 = (ImageButton)findViewById(R.id.imageButton3);
        button4 = (ImageButton)findViewById(R.id.imageButton4);
        button5 = (ImageButton)findViewById(R.id.imageButton5);
        button6 = (ImageButton)findViewById(R.id.imageButton6);
        button7 = (ImageButton)findViewById(R.id.imageButton7);
        button8 = (ImageButton)findViewById(R.id.imageButton8);
        button9 = (ImageButton)findViewById(R.id.imageButton9);
        button10 = (ImageButton)findViewById(R.id.imageButton10);
        button11 = (ImageButton)findViewById(R.id.imageButton11);
        button12 = (ImageButton)findViewById(R.id.imageButton12);
        button13 = (ImageButton)findViewById(R.id.imageButton13);
        button14 = (ImageButton)findViewById(R.id.imageButton14);
        button15 = (ImageButton)findViewById(R.id.imageButton15);
        button16 = (ImageButton)findViewById(R.id.imageButton16);
        button17 = (ImageButton)findViewById(R.id.imageButton17);
        button18 = (ImageButton)findViewById(R.id.imageButton18);
        button19 = (ImageButton)findViewById(R.id.imageButton19);
        button20 = (ImageButton)findViewById(R.id.imageButton20);

        //Populate array
        array[0] = button1;
        array[1] = button2;
        array[2] = button3;
        array[3] = button4;
        array[4] = button5;
        array[5] = button6;
        array[6] = button7;
        array[7] = button8;
        array[8] = button9;
        array[9] = button10;
        array[10] = button11;
        array[11] = button12;
        array[12] = button13;
        array[13] = button14;
        array[14] = button15;
        array[15] = button16;
        array[16] = button17;
        array[17] = button18;
        array[18] = button19;
        array[19] = button20;


        //Set all buttons to be invisible
        button1.setImageResource(R.drawable.transparent);
        button2.setImageResource(R.drawable.transparent);
        button3.setImageResource(R.drawable.transparent);
        button4.setImageResource(R.drawable.transparent);
        button5.setImageResource(R.drawable.transparent);
        button6.setImageResource(R.drawable.transparent);
        button7.setImageResource(R.drawable.transparent);
        button8.setImageResource(R.drawable.transparent);
        button9.setImageResource(R.drawable.transparent);
        button10.setImageResource(R.drawable.transparent);
        button11.setImageResource(R.drawable.transparent);
        button12.setImageResource(R.drawable.transparent);
        button13.setImageResource(R.drawable.transparent);
        button14.setImageResource(R.drawable.transparent);
        button15.setImageResource(R.drawable.transparent);
        button16.setImageResource(R.drawable.transparent);
        button17.setImageResource(R.drawable.transparent);
        button18.setImageResource(R.drawable.transparent);
        button19.setImageResource(R.drawable.transparent);
        button20.setImageResource(R.drawable.transparent);



        //Create timer
        CountDownTimer CDT = new CountDownTimer(30000, 1000) {
            TextView time = (TextView)findViewById(R.id.textView);
            public void onTick(long millisUntilFinished) {
                time.setText("Time: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                time.setText("Game Over!");
                array[next].setClickable(false);
                timeup();
            }
        }.start();
        //PLACE MORE HERE IF NECESSARY
    }

    //makes the current visible button invisible
    //randomly selects another button to be visible
    //increments and prints score

    public void click(View view){
        currentID = view.getId();
        nextID = array[next].getId();

        if(nextID == currentID){
            cycle();

        }
        else{
            miss();
        }
    }
    //called when you hit a target
    public void cycle(){

        txtScore = (TextView) findViewById(R.id.textView2);
        next = rand.nextInt(20);
        array[current].setImageResource(R.drawable.transparent);
        score = score + 10;
        array[next].setImageResource(R.drawable.target);
        txtScore.setText("Score: " + score);
        current = next;
        //counts the number of times you hit the target
        hitCount++;
    }
    //called when you miss a target
    public void miss(){
        //keeps from scoring in the negatives
       // if(score > 0){
            score = score - 10;
        //}

        txtScore = (TextView) findViewById(R.id.textView2);
        txtScore.setText("Score: " + score);

        //counts how often you miss the target
        missCount++;
    }

    public void onBackPressed(){

    }

    public void timeup() {
        new EndGame().execute();
    }

    private class EndGame extends AsyncTask<String, Void, Integer>
    {
        private final String username;
        private final String gameID;
        EndGame()
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TargetActivity.this);
            this.username = preferences.getString("username", "");
            this.gameID = preferences.getString("gameid","");		}
        @Override
        protected void onPostExecute(Integer highScore)
        {


            Intent intent = new Intent(TargetActivity.this, GameResults.class);
            intent.putExtra("CorrectAnswers", hitCount);
            intent.putExtra("WrongAnswers", missCount);
            intent.putExtra("Score", score);
            if(highScore == -1){
                intent.putExtra("OnlineHighScore", 0);
            }else{
                intent.putExtra("OnlineHighScore", highScore);
            }
            intent.putExtra("username", username);
            intent.putExtra("gameid", gameID);
            TargetActivity.this.finish();
            startActivity(intent);
        }

        @Override
        protected Integer doInBackground(String... args)
        {
            StringBuilder sb = new StringBuilder();
            if(this.username != null && !this.username.equals("")){
                try {

                    String link = "http://www.kyleleber.com/Android/checkLogin.php";
                    String data = URLEncoder.encode("username", "UTF-8") + "="
                            + URLEncoder.encode(username, "UTF-8");
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(data.getBytes());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));

                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }
                catch (Exception e)
                {
                    Log.v("Exception:", e.getMessage());
                }
                // If logged in, update the database...
                if(sb.toString().equals("1"))
                {
                    String score = String.valueOf(TargetActivity.this.score);
                    String manufacturer = Build.MANUFACTURER;
                    String model = Build.MODEL;
                    String manmodel = manufacturer+" " + model;
                    try {
                        String link = "http://www.kyleleber.com/Android/writeHighScore.php";
                        String data = URLEncoder.encode("username", "UTF-8") + "="
                                + URLEncoder.encode(username, "UTF-8")+"&" +
                                URLEncoder.encode("gameid", "UTF-8") + "="
                                + URLEncoder.encode(gameID, "UTF-8") + "&" +
                                URLEncoder.encode("score", "UTF-8") + "="
                                + URLEncoder.encode(score, "UTF-8") + "&" +
                                URLEncoder.encode("model", "UTF-8") + "="
                                + URLEncoder.encode(manmodel, "UTF-8");
                        URL url = new URL(link);
                        URLConnection conn = url.openConnection();
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.getOutputStream().write(data.getBytes());

                        // @HACK - Java's deficiency, comment out below to find out why...
                        conn.getInputStream().close();
                        // End Of Hack

                    }catch(Throwable t){t.printStackTrace();}
                }
                try {
                    String link = "http://www.kyleleber.com/Android/pullHighScore.php";
                    String data = URLEncoder.encode("username", "UTF-8") + "="
                            + URLEncoder.encode(username, "UTF-8")+"&" +
                            URLEncoder.encode("gameid", "UTF-8") + "="
                            + URLEncoder.encode(gameID, "UTF-8");
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(data.getBytes());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            conn.getInputStream()));
                    sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    return Integer.parseInt(sb.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }}
            return -1;
        }

    }
}
