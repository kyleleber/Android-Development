package com.example.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Mathematics extends ActionBarActivity {

    private ArrayList<String> equations = new ArrayList<String>();
    private ArrayList<Integer> answers= new ArrayList<Integer>();

    private ImageView digitArray[]= new ImageView[9];         //For the number images

    private String userAnswer;      //What the user answers

    private String equation;
    private int correctanswer;

    private int score;
    private int correctAnswers;
    private int wrongAnswers;

    private boolean gameOver = false;
    private boolean ready = false;      //Used for splash screen
    private int negative = 0;   //zero is not negative   Basically just a boolean...not sure why im not using a boolean

    private boolean soundOn = false;
    private boolean musicOn = false;
    private MediaPlayer music;
    private MediaPlayer sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mathematics);

        int gametype = getIntent().getExtras().getInt("GAMETYPE");

        correctanswer = 0;
        equation = "Empty";
        userAnswer= "0";

        score = 0;
        correctAnswers = 0;
        wrongAnswers = 0;

        sound = MediaPlayer.create(this, R.raw.fail);
        music = MediaPlayer.create(this, R.raw.music);
        setupOptions();

        new BeginGame(gametype).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mathematics, menu);
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

    //---------------------------------------------------------------
    //File Stuff
    //---------------------------------------------------------------
    //Returns the length of the equations arraylist
    public int getSizeOfEquations()
    {
        return equations.size();
    }
    //Return the length of the answers arraylist
    public int getSizeOfAnswers()
    {
        return answers.size();
    }
    //Return the string at the given index of equations
    public String getEquations(int index){
        String equation = "Equations out of Bounds";
        if(index < equations.size() && index >=0)
        {
            equation = equations.get(index);
        }
        return equation;
    }
    //Return the int at the given index of answers
    public int getAnswers(int index){
        int answer = Integer.MAX_VALUE;
        if(index < answers.size() && index >=0)
        {
            answer = answers.get(index);
        }
        return answer;
    }
    //Pulls the desired files and separates into two Arraylists
    public void setupEquationsAnswers(int gametype)
    {
        equations = new ArrayList<String>();
        answers = new ArrayList<Integer>();
        Context context = getApplicationContext();
        InputStream is = null;
        Resources res = getResources();

        switch(gametype){
            case 3:
                is = res.openRawResource(R.raw.addsubtract);
                Log.v("zswartwoFILE", "addsubtract file was chosen");
                break;
            case 4:
                is = res.openRawResource(R.raw.multiplicationdivide);
                Log.v("zswartwoFILE", "multiplicationdivide file was chosen");
                break;
            case 5:
                is = res.openRawResource(R.raw.orderofops);
                Log.v("zswartwoFILE", "orderofops file was chosen");
                break;
            default:
        }

        try{
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null)
            {
                int separator = line.indexOf('|');
                equations.add(line.substring(0, separator));
                answers.add(Integer.parseInt(line.substring(separator+1, line.length())));
            }

            br.close();
        }catch(Exception e)
        {
            Log.v("zswartwoFILE", "IT FAILED!!!");
            //Lets not get here
        }
    }

    //---------------------------------------------------------------------
    //End of Files Stuff
    //----------------------------------------------------------------------

    //------------------------------------------------------------------
    //Game Stuff
    //------------------------------------------------------------------
    public void playGame(){
        int index =(int) Math.floor(getSizeOfAnswers() * Math.random());

        equation = getEquations(index);
        correctanswer = getAnswers(index);

        TextView eq = (TextView)findViewById(R.id.equation);
        eq.setText(equation);
    }

    public void initializeImageArray(){

        digitArray[8] = (ImageView)findViewById(R.id.digit8);
        digitArray[7] = (ImageView)findViewById(R.id.digit7);
        digitArray[6] = (ImageView)findViewById(R.id.digit6);
        digitArray[5] = (ImageView)findViewById(R.id.digit5);
        digitArray[4] = (ImageView)findViewById(R.id.digit4);
        digitArray[3] = (ImageView)findViewById(R.id.digit3);
        digitArray[2] = (ImageView)findViewById(R.id.digit2);
        digitArray[1] = (ImageView)findViewById(R.id.digit1);
        digitArray[0] = (ImageView)findViewById(R.id.digit0);
    }

    public void stringToImages(){

        //Loops through the User Answer and sets the corresponding imageview array position
        for(int i = 0; i < userAnswer.length(); i++)
        {
            char currentdigit = userAnswer.charAt(i);
            switch(currentdigit)
            {
                case '-':
                    digitArray[userAnswer.length()-1-i].setImageResource(R.raw.hyphen);
                    digitArray[userAnswer.length()-1-i].setVisibility(View.VISIBLE);
                    break;
                case '0':
                    digitArray[userAnswer.length()-1-i].setImageResource(R.raw.zero);
                    digitArray[userAnswer.length()-1-i].setVisibility(View.VISIBLE);
                    break;
                case '1':
                    digitArray[userAnswer.length()-1-i].setImageResource(R.raw.one);
                    digitArray[userAnswer.length()-1-i].setVisibility(View.VISIBLE);
                    break;
                case '2':
                    digitArray[userAnswer.length()-1-i].setImageResource(R.raw.two);
                    digitArray[userAnswer.length()-1-i].setVisibility(View.VISIBLE);
                    break;
                case '3':
                    digitArray[userAnswer.length()-1-i].setImageResource(R.raw.three);
                    digitArray[userAnswer.length()-1-i].setVisibility(View.VISIBLE);
                    break;
                case '4':
                    digitArray[userAnswer.length()-1-i].setImageResource(R.raw.four);
                    digitArray[userAnswer.length()-1-i].setVisibility(View.VISIBLE);
                    break;
                case '5':
                    digitArray[userAnswer.length()-1-i].setImageResource(R.raw.five);
                    digitArray[userAnswer.length()-1-i].setVisibility(View.VISIBLE);
                    break;
                case '6':
                    digitArray[userAnswer.length()-1-i].setImageResource(R.raw.six);
                    digitArray[userAnswer.length()-1-i].setVisibility(View.VISIBLE);
                    break;
                case '7':
                    digitArray[userAnswer.length()-1-i].setImageResource(R.raw.seven);
                    digitArray[userAnswer.length()-1-i].setVisibility(View.VISIBLE);
                    break;
                case '8':
                    digitArray[userAnswer.length()-1-i].setImageResource(R.raw.eight);
                    digitArray[userAnswer.length()-1-i].setVisibility(View.VISIBLE);
                    break;
                case '9':
                    digitArray[userAnswer.length()-1-i].setImageResource(R.raw.nine);
                    digitArray[userAnswer.length()-1-i].setVisibility(View.VISIBLE);

                    break;

            };
        }
        //loops through the rest of the array length and makes them invisible
        for(int i = userAnswer.length(); i < digitArray.length; i++){
            digitArray[i].setImageResource(R.raw.blank);
            digitArray[i].setVisibility(View.INVISIBLE);
        }
    }

    //-------------------------------------------------------------------
    //End Game Stuff
    //-------------------------------------------------------------------

    //-----------------------------------------------------------------------
    // Start of Buttons
    //-----------------------------------------------------------------------

    public void button0Clicked(View v){

        if(Integer.parseInt(userAnswer) != 0){
            if(userAnswer.length() < 9){
                userAnswer=userAnswer + "0";
            }
        }
        else if(negative == 1){
            userAnswer="-0";
        }
        else
            userAnswer="0";

        stringToImages();
    }
    public void button1Clicked(View v){

        if(Integer.parseInt(userAnswer) != 0){
            if(userAnswer.length() < 9){
                userAnswer=userAnswer + "1";
            }
        }
        else if(negative == 1){
            userAnswer="-1";
        }
        else
            userAnswer="1";

        stringToImages();
    }
    public void button2Clicked(View v){

        if(Integer.parseInt(userAnswer) != 0){
            if(userAnswer.length() < 9){
                userAnswer=userAnswer + "2";
            }
        }
        else if(negative == 1){
            userAnswer="-2";
        }
        else
            userAnswer="2";
        stringToImages();
    }
    public void button3Clicked(View v){

        if(Integer.parseInt(userAnswer) != 0){
            if(userAnswer.length() < 9){
                userAnswer=userAnswer + "3";
            }
        }
        else if(negative == 1){
            userAnswer="-3";
        }
        else
            userAnswer="3";

        stringToImages();
    }
    public void button4Clicked(View v){

        if(Integer.parseInt(userAnswer) != 0){
            if(userAnswer.length() < 9){
                userAnswer=userAnswer + "4";
            }
        }
        else if(negative == 1){
            userAnswer="-4";
        }
        else
            userAnswer="4";

        stringToImages();
    }
    public void button5Clicked(View v){

        if(Integer.parseInt(userAnswer) != 0){
            if(userAnswer.length() < 9){
                userAnswer=userAnswer + "5";
            }
        }
        else if(negative == 1){
            userAnswer="-5";
        }
        else
            userAnswer="5";

        stringToImages();
    }
    public void button6Clicked(View v){

        if(Integer.parseInt(userAnswer) != 0){
            if(userAnswer.length() < 9){
                userAnswer=userAnswer + "6";
            }
        }
        else if(negative == 1){
            userAnswer="-6";
        }
        else
            userAnswer="6";

        stringToImages();
    }
    public void button7Clicked(View v){

        if(Integer.parseInt(userAnswer) != 0){
            if(userAnswer.length() < 9){
                userAnswer=userAnswer + "7";
            }
        }
        else if(negative == 1){
            userAnswer="-7";
        }
        else
            userAnswer="7";

        stringToImages();
    }
    public void button8Clicked(View v){

        if(Integer.parseInt(userAnswer) != 0){
            if(userAnswer.length() < 9){
                userAnswer=userAnswer + "8";
            }
        }
        else if(negative == 1){
            userAnswer="-8";
        }
        else
            userAnswer="8";

        stringToImages();
    }
    public void button9Clicked(View v){

        if(Integer.parseInt(userAnswer) != 0){
            if(userAnswer.length() < 9){
                userAnswer=userAnswer + "9";
            }
        }
        else if(negative == 1){
            userAnswer="-9";
        }
        else
            userAnswer="9";

        stringToImages();
    }
    public void negate(View v){
        //switch the sign
        if(userAnswer.length() < 9){
            negative = 1 - negative;

            if(negative == 1){
                userAnswer="-" + userAnswer;
            }
            else{
                userAnswer=userAnswer.substring(1,userAnswer.length());
            }
            stringToImages();
        }
    }
    public void clear(View v){
        negative = 0;
        userAnswer="0";
        stringToImages();
    }
    public void equals(View v){
        if(!gameOver){
            negative = 0;
            int guessedAnswer = Integer.parseInt(userAnswer);

            if(guessedAnswer == correctanswer){
                score++;
                correctAnswers++;
                playGame();

                TextView tv = (TextView)findViewById(R.id.score);
                tv.setText("Score: " + score);
                if(soundOn){
                    if(sound.isPlaying())
                        sound.release();
                    new SoundEffects(true).execute();
                }
            }
            else {
                wrongAnswers++;
                if(soundOn){
                    if(sound.isPlaying())
                        sound.release();
                    new SoundEffects(false).execute();
                }
            }
            userAnswer="0";
            stringToImages();
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

        ready = true;
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
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Mathematics.this);
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

        boolean on = ((ToggleButton) v).isChecked();
        if(on)
        {

        }
        else
        {}
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

    //-----------------------------------------------------------------------------
    //End of Buttons
    //------------------------------------------------------------------------------
    //overridden to stop the hitting of the back button
    public void onBackPressed() {}

    public void wrapUp(){
        gameOver = true;
        music.release();
        sound.release();
        new EndGame().execute();
    }


    //----------------------------------------------------------------------------------------
    // AYSNC METHODS
    //----------------------------------------------------------------------------------------

    //Async stuff that will load the arraylists in the background for me
    private class BeginGame extends AsyncTask<Void, Void, Void>
    {
        // ProgressDialog pdLoading = new ProgressDialog(Game.this);
        private int gametype;

        public BeginGame(int gametype) {
            super();
            this.gametype = gametype;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            // pdLoading.setMessage("\tLoading...");
            // pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            setupEquationsAnswers(gametype);
            initializeImageArray();

            while(!ready)
            {
                try{
                    wait(1000);
                }catch(Exception e){}
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            LinearLayout.LayoutParams layout =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1f);
            ImageView splash = (ImageView)findViewById(R.id.splash);
            splash.setLayoutParams(layout);
            LinearLayout screen = (LinearLayout)findViewById(R.id.screen);
            screen.setBackgroundResource(R.color.white);
            playGame();
            stringToImages();
            new CountDownTimer(20000, 1000) {
                TextView score = (TextView)findViewById(R.id.time);
                public void onTick(long millisUntilFinished) {
                    score.setText("Time Remaining: " + millisUntilFinished / 1000 +" seconds");
                }

                public void onFinish() {
                    score.setText("Times Up!");
                    wrapUp();
                }
            }.start();

        }
    }

    private class EndGame extends AsyncTask<String, Void, Integer>
    {
        private final String username;
        private final String gameID;
        EndGame()
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Mathematics.this);
            this.username = preferences.getString("username", "");
            this.gameID = preferences.getString("gameid","");		}
        @Override
        protected void onPostExecute(Integer highScore)
        {

            Intent intent = new Intent(Mathematics.this, GameResults.class);
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
            Mathematics.this.finish();
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
                    String score = String.valueOf(Mathematics.this.score);
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

    //Async stuff that handles the sound made when enter is hit
    private class SoundEffects extends AsyncTask<Void, Void, Void>
    {
        public SoundEffects(boolean success) {
            super();
            if(success){
                sound = MediaPlayer.create(Mathematics.this, R.raw.success);
            }
            else {
                sound = MediaPlayer.create(Mathematics.this, R.raw.fail);
            }

        }
        @Override
        protected Void doInBackground(Void... params)
        {
            sound.start();
            return null;
        }
    }
}
