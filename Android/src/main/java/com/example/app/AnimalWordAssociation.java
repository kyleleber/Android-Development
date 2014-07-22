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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class AnimalWordAssociation extends Activity {

    String[] filelist;
    Boolean willWork = new Boolean(false);
    Class<R.id> c = R.id.class;
    //Correct answer for the given image
    String correctAnswer="";
    int wrongAnswers=0;
    int correctAnswers=0;
    int score=0;
    String[] possibleAnswers = new String[4];

    private boolean soundOn = false;
    private boolean musicOn = false;
    private MediaPlayer music;
    private MediaPlayer sound;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_word_association);

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
            setButtons(null);
        } catch (Exception e) {
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

    public void setButtons(View view) throws IOException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException{
        willWork = false;
        AssetManager assetManager = getAssets();
        filelist = assetManager.list("xx");
        Bitmap[] images = new Bitmap[4];
        InputStream is = null;


        try {
            while(willWork == false){
                for(int x=0; x<4; x++){
                    int randomImg = (int) (Math.random()*filelist.length);
                    int spaceIndex = filelist[randomImg].indexOf(".");
                    correctAnswer =  filelist[randomImg].substring(0, spaceIndex).trim();
                    is = assetManager.open("xx/"+filelist[randomImg]);
                    Bitmap image = BitmapFactory.decodeStream(is);
                    possibleAnswers[x] = correctAnswer;
                    images[x] = image;
                    is.close();

                }
				
				/*Check to make sure there are no duplicate answers.. if so,
				 *The loop is ran again to regenerate possible answers.
				*/

                Set<String> set = new HashSet<String>(Arrays.asList(possibleAnswers));
                if(set.size() < images.length){
                    //System.out.println("There are some duplicates");
                }else{
                    willWork = true;
                }
            }
            System.out.println("CORRECT ANSWER: " + correctAnswer);
            TextView t = (TextView)findViewById(R.id.wordView1);
            t.setText(correctAnswer);
        } catch (IOException e) {
            Log.w("EL", e);
        }
        shuffleAnswers(images, possibleAnswers);
        setPossibleAnswers(images,possibleAnswers);

    }
    /*sets the answers after being shuffled*/
    public void setPossibleAnswers(Bitmap[] images, String[] answers) throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException{
        for(int x=0;x <images.length;x++){
            Field gameid = c.getField("wordimage" + x);
            int wordimage = gameid.getInt(null);
            ImageButton ib2 = (ImageButton) findViewById(wordimage);
            ib2.setImageBitmap( images[x]);
            ib2.setContentDescription(answers[x].toString());
        }

    }

    public void shuffleAnswers(Bitmap[] bmp, String[] answers){
        //Mixes up the buttons every time the image and possible answers refresh
        Random rnd = new Random();
        for (int i = bmp.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            Bitmap a = bmp[index];
            String x = answers[index];
            bmp[index] = bmp[i];
            answers[index] = answers[i];
            bmp[i] = a;
            answers[i] = x;
        }
        for(int x=0; x<answers.length;x++){
            System.out.println("Answer: " + answers[x]);
        }

    }

    boolean locked = false;
    public void checkAnswer(View view) throws IOException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException{
        if(locked) return;
        TextView t = new TextView(this);
        t = (TextView)findViewById(R.id.scoreValue);
        ImageButton image = (ImageButton)view;
        String userAnswer = image.getContentDescription().toString();

        if(userAnswer.equals(correctAnswer)){
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
        setButtons(null);
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
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AnimalWordAssociation.this);
            this.username = preferences.getString("username", "");
            this.gameID = preferences.getString("gameid","");		}
        @Override
        protected void onPostExecute(Integer highScore)
        {
            music.release();
            sound.release();

            Intent intent = new Intent(AnimalWordAssociation.this, GameResults.class);
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
            AnimalWordAssociation.this.finish();
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
                    String score = String.valueOf(AnimalWordAssociation.this.score);
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
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AnimalWordAssociation.this);
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
                sound = MediaPlayer.create(AnimalWordAssociation.this, R.raw.success);
            }
            else {
                sound = MediaPlayer.create(AnimalWordAssociation.this, R.raw.fail);
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

