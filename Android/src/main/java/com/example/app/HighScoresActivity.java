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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.lang.reflect.Field;

public class HighScoresActivity extends Activity {

    private boolean soundOn = false;
    private boolean musicOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        setupOptions();

        Spinner spinner = (Spinner) findViewById(R.id.highscores_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.highscore_choices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Class<R.id> c = R.id.class;
                try{
                    for(int x=0; x<10; x++){

                        Field username = c.getField("highscore" + (int)(x+1) + "username");
                        int uname = username.getInt(null);  // pass in null, since field is a static field.

                        Field score = c.getField("highscore" + (int)(x+1) + "score");
                        int s = score.getInt(null);

                        Field date = c.getField("highscore" + (int)(x+1) + "date");
                        int hsdate = date.getInt(null);

                        TextView t;

                        t=(TextView)findViewById(uname);
                        t.setText("");

                        t=(TextView)findViewById(s);
                        t.setText("");

                        //Reset Device.. Add back in once formatting is correct
						/*
						t=(TextView)findViewById(d);
						t.setText("");
						*/

                        t=(TextView)findViewById(hsdate);
                        t.setText("");
                    }
                    HighScores hs = new HighScores(HighScoresActivity.this);
                    hs.execute(position);
                }catch(Throwable t){t.getMessage();}
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        spinner.setAdapter(adapter);
    }

    public void onBackPressed()
    {
        this.finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.high_scores, menu);
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
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HighScoresActivity.this);
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
