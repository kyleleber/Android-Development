package com.example.app;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Locale;

public class GameResults extends Activity {
    Bundle extras;
    //int gameid;
    String gameid;

    private boolean soundOn = false;
    private boolean musicOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        extras = getIntent().getExtras();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        gameid = preferences.getString("gameid","");
        setContentView(R.layout.activity_game_results);
        if (extras != null) {
            int correctAnswers = extras.getInt("CorrectAnswers");
            int wrongAnswers = extras.getInt("WrongAnswers");
            int score = extras.getInt("Score");
            NetworkCheck nc = new NetworkCheck();
            int onlineHighScore;
            if(nc.CheckInternet(this)){
                onlineHighScore = extras.getInt("OnlineHighScore");
            }else{
                onlineHighScore = 0;
            }
            double accuracy = 100*((double)correctAnswers / (double)(correctAnswers + wrongAnswers));
            String acc = Double.isNaN(accuracy) ? "N/A" : String.format(Locale.US,"%.2f%%", accuracy);


            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            int highscore = preferences.getInt("highscore", 0);
            if(score > highscore){
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("highscore", score);
                editor.commit();
                highscore = preferences.getInt("highscore", 0);
            }

            TextView correctAnswerView = (TextView)findViewById(R.id.correctAnswersScore);
            correctAnswerView.setText(String.valueOf(correctAnswers));
            TextView wrongAnswerView = (TextView)findViewById(R.id.wrongAnswersScore);
            wrongAnswerView.setText(String.valueOf(wrongAnswers));
            TextView scoreValueView = (TextView)findViewById(R.id.ScoreValue);
            scoreValueView.setText(String.valueOf(score));
            TextView accuracyValueView = (TextView)findViewById(R.id.accuracyScore);
            accuracyValueView.setText(acc);
            TextView onlineHighScoreValueView = (TextView)findViewById(R.id.onlineHighScoreValue);
            onlineHighScoreValueView.setText(String.valueOf(onlineHighScore));

        }

        setupOptions();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_results, menu);
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

    Class<?>[] c = new Class<?>[]{
            AnimalAssociation.class,
            ColorAssociation.class,
            AnimalWordAssociation.class,
            Mathematics.class,
            Mathematics.class,
            Mathematics.class,
            TargetActivity.class
    };
    public void playAgain(View view){
        music.releaseMusic();
        Intent intent = new Intent(this, c[Integer.parseInt(gameid)]);
        intent.putExtra("GAMETYPE", Integer.parseInt(gameid));
        this.finish();
        startActivity(intent);
    }

    public void gotoGamesPage(View view){
        this.finish();
        Intent intent = new Intent(this, GameList.class);
        startActivity(intent);
    }

    public void gotoMainMenu(View view){
        this.finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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

        dealWithMusic();
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
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GameResults.this);
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

        dealWithMusic();
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

    MenusMediaPlayer music;
    public void dealWithMusic()
    {
        if(musicOn) {
            try {
                if (!music.menuMusic.isPlaying()) {
                    music.menuMusic = MediaPlayer.create(this, R.raw.menumusic);
                    music.menuMusic.start();
                }
            } catch (Exception e) {
                music.menuMusic = MediaPlayer.create(this, R.raw.menumusic);
                music.menuMusic.start();
            }
        }
        else
        {
            try {
                if(music.menuMusic.isPlaying())
                {
                    music.menuMusic.stop();
                    music.menuMusic.reset();
                }
            }
            catch(Exception e)
            {
                //Do nothing
            }
        }
    }
}

