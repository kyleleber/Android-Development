package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class AnimalAssociation extends Activity {

    String[] filelist;
    Boolean willWork = new Boolean(false);

    //Correct answer for the given image
    String correctAnswer="";
    int wrongAnswers=0;
    int correctAnswers=0;
    int score=0;
    boolean connected;

    private boolean soundOn = false;
    private boolean musicOn = false;
    private MediaPlayer music;
    private MediaPlayer sound;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_association);

        //Options stuff;
        sound = MediaPlayer.create(this, R.raw.fail);
        music = MediaPlayer.create(this, R.raw.music);
        setupOptions();

        //Since there is no splash screen, ill start the music here
        music.start();
        if(musicOn)
        {
            int maxVolume = 100;
            int soundVolume = 75;
            float volume = (float) (1 - (Math.log(maxVolume - soundVolume) / Math.log(maxVolume)));
            music.setVolume(volume, volume);
        }
        else
        {
            int maxVolume = 100;
            int soundVolume = 0;
            float volume = (float) (1 - (Math.log(maxVolume - soundVolume) / Math.log(maxVolume)));
            music.setVolume(volume, volume);
        }

        try {
            setPicture(null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.animal_association, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            options();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setPicture(View view) throws IOException{
        AssetManager assetManager = getAssets();

        filelist = assetManager.list("xx");
        InputStream istr;
        ImageView imageView = (ImageView)findViewById(R.id.imageView1);

        try {
            int randomImg = (int) (Math.random()*filelist.length);
            int spaceIndex = filelist[randomImg].indexOf(".");
            correctAnswer =  filelist[randomImg].substring(0, spaceIndex).trim();
            istr = assetManager.open("xx/"+filelist[randomImg]);
            Bitmap bitmap = BitmapFactory.decodeStream(istr);
            imageView.setImageBitmap(bitmap);
            istr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setPossibleAnswers();
    }
    public void setPossibleAnswers(){
        willWork = false;
        String[] possibleAnswers = new String[4];
        //Correct answer is always the first one in the array to begin with
        //Before array is scrambled

        possibleAnswers[0] = correctAnswer;
        possibleAnswers[1] = "";
        possibleAnswers[2] = "";
        possibleAnswers[3] = "";
        String nfs="";

        while(willWork == false){
            for(int x=1; x<=3; x++){
                int randomNumber = (int)(Math.random()*filelist.length);
                int spaceIndex = filelist[randomNumber].indexOf(".");
                String fileString = filelist[randomNumber].substring(0, spaceIndex);
                nfs = fileString.trim();
                possibleAnswers[x] = nfs;
            }

            Set<String> set = new HashSet<String>(Arrays.asList(possibleAnswers));
            if(set.size() < possibleAnswers.length){
                //System.out.println("There are some duplicates");
            }else{
                willWork = true;
            }
        }
        shuffleAnswers(possibleAnswers);

        Button b1=(Button)findViewById(R.id.image1);
        b1.setText(possibleAnswers[0]);
        Button b2=(Button)findViewById(R.id.image2);
        b2.setText(possibleAnswers[1]);
        Button b3=(Button)findViewById(R.id.image3);
        b3.setText(possibleAnswers[2]);
        Button b4=(Button)findViewById(R.id.image4);
        b4.setText(possibleAnswers[3]);
    }

    public void shuffleAnswers(String[] str){
        //Mixes up the buttons every time the image and possible answers refresh
        Random rnd = new Random();
        for (int i = str.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = str[index];
            str[index] = str[i];
            str[i] = a;
        }
    }

    boolean locked = false;
    public void checkAnswer(View view) throws IOException{
        if(locked) return;
        Button b = (Button)view;

        TextView t = new TextView(this);
        t = (TextView)findViewById(R.id.scoreValue);
        if(b.getText().equals(correctAnswer)){
            score += 10;
            correctAnswers++;
            if(soundOn){
                if(sound.isPlaying())
                    sound.release();
                new SoundEffects(true).execute();
            }
        }else{
            score -= 10;
            wrongAnswers++;
            if(soundOn){
                if(sound.isPlaying())
                    sound.release();
                new SoundEffects(false).execute();
            }
        }
        t.setText(String.valueOf(score));
        setPicture(null);

    }

    public void startTimer(View view){
        final TextView t = (TextView)findViewById(R.id.timerValue);
        new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                String timeStr = String.valueOf(millisUntilFinished/1000);
                t.setText(timeStr);
            }

            public void onFinish() {
                t.setText("Time is up!");
                locked = true;
                timeup();
            }
        }.start();
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
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AnimalAssociation.this);
            this.username = preferences.getString("username", "");
            this.gameID = preferences.getString("gameid","");
        }
        @Override
        protected void onPostExecute(Integer highScore)
        {
            music.release();
            sound.release();

            Intent intent = new Intent(AnimalAssociation.this, GameResults.class);
            intent.putExtra("CorrectAnswers", correctAnswers);
            intent.putExtra("WrongAnswers", wrongAnswers);
            intent.putExtra("Score", score);
            if(highScore == -1){
                intent.putExtra("OnlineHighScore", 0);
            }else{
                intent.putExtra("OnlineHighScore", highScore);
            }
            intent.putExtra("username", username);
            intent.putExtra("gameid", gameID);
            AnimalAssociation.this.finish();
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
                    String score = String.valueOf(AnimalAssociation.this.score);
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
                }
            }
            return (int)-1;

        }

    }
    public void onBackPressed(){
        //activity.player.start();

    }

    //--------------------------------------------------------------------------
    //Options Stuff
    //----------------------------------------------------------------------

    //Bring up the options layout
    public void options(){
        LinearLayout.LayoutParams layout =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1f);
        LinearLayout screen = (LinearLayout)findViewById(R.id.game);
        screen.setLayoutParams(layout);

        layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,0f);
        screen = (LinearLayout)findViewById(R.id.options);
        screen.setLayoutParams(layout);

    }
    //Set up the options menu with the preferences
    public void setupOptions()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        //sound toggle
        soundOn = preferences.getBoolean("sound", true);
        editor.putBoolean("sound", soundOn);
        editor.commit();
        ToggleButton soundsbutton = (ToggleButton) findViewById(R.id.sound);
        soundsbutton.setChecked(soundOn);

        //music toggle
        musicOn = preferences.getBoolean("music", true);
        editor.putBoolean("music", musicOn);
        editor.commit();
        ToggleButton musicbutton = (ToggleButton) findViewById(R.id.music);
        musicbutton.setChecked(musicOn);

        //Colorblind toggle
        boolean colorblindOn = preferences.getBoolean("colorblind", true);
        editor.putBoolean("colorblind", colorblindOn);
        editor.commit();
        ToggleButton colorblindbutton = (ToggleButton) findViewById(R.id.colorblind);
        colorblindbutton.setChecked(colorblindOn);

        //spinner
        createSpinnerControl();
        int colorblindness = preferences.getInt("colorblindness", 2);
        editor.putInt("colorblindness", colorblindness);
        editor.commit();
        Spinner colorblindspinner = (Spinner) findViewById(R.id.color_spinner);
        colorblindspinner.setSelection(colorblindness);


    }
    //Set up and fill in the spinner
    public void createSpinnerControl()
    {
        Spinner spinner = (Spinner) findViewById(R.id.color_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.colorblind_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Item selected
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                //preferences set up
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AnimalAssociation.this);
                SharedPreferences.Editor editor = preferences.edit();
                //Get selected Item
                String selectedItem = parent.getItemAtPosition(position).toString();
                if(selectedItem.equals("deuteranopia"))
                {
                    editor.putInt("colorblindness", 0);
                    editor.commit();
                }
                else if(selectedItem.equals("protanopia"))
                {
                    editor.putInt("colorblindness", 1);
                    editor.commit();
                }
                else if(selectedItem.equals("tritanopia"))
                {
                    editor.putInt("colorblindness", 2);
                    editor.commit();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
    public void musicToggleClicked(View v)
    {
        musicOn = !musicOn;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("music", musicOn);
        editor.commit();

        if(musicOn)
        {
            music.start();

            int maxVolume = 100;
            int soundVolume = 75;
            float volume = (float) (1 - (Math.log(maxVolume - soundVolume) / Math.log(maxVolume)));
            music.setVolume(volume, volume);
        }
        else
        {
            int maxVolume = 100;
            int soundVolume = 0;
            float volume = (float) (1 - (Math.log(maxVolume - soundVolume) / Math.log(maxVolume)));
            music.setVolume(volume, volume);
        }
    }
    public void soundToggleClicked(View v)
    {
        soundOn = !soundOn;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("sound", soundOn);
        editor.commit();

    }
    public void colorblindToggleClicked(View v)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("colorblind", !preferences.getBoolean("colorblind", true));
        editor.commit();
    }
    //Hide the options major
    public void optionsbackClicked(View v)
    {
        LinearLayout.LayoutParams layout =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,0f);
        LinearLayout screen = (LinearLayout)findViewById(R.id.game);
        screen.setLayoutParams(layout);
        layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1f);
        screen = (LinearLayout)findViewById(R.id.options);
        screen.setLayoutParams(layout);
    }
    //Async stuff that handles the sound made when enter is hit
    private class SoundEffects extends AsyncTask<Void, Void, Void>
    {
        public SoundEffects(boolean success) {
            super();
            if(success){
                sound = MediaPlayer.create(AnimalAssociation.this, R.raw.success);
            }
            else {
                sound = MediaPlayer.create(AnimalAssociation.this, R.raw.fail);
            }

        }
        @Override
        protected Void doInBackground(Void... params)
        {
            sound.start();
            return null;
        }
    }
    public void splashClicked(View v){
        music.start();
        if(musicOn)
        {
            int maxVolume = 100;
            int soundVolume = 75;
            float volume = (float) (1 - (Math.log(maxVolume - soundVolume) / Math.log(maxVolume)));
            music.setVolume(volume, volume);
        }
        else
        {
            int maxVolume = 100;
            int soundVolume = 0;
            float volume = (float) (1 - (Math.log(maxVolume - soundVolume) / Math.log(maxVolume)));
            music.setVolume(volume, volume);
        }
        LinearLayout.LayoutParams layout =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1f);
        ImageView splash = (ImageView)findViewById(R.id.splash);
        splash.setLayoutParams(layout);
        startTimer(null);
        //ready = true;
    }

}
