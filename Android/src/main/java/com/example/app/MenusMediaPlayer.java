package com.example.app;

import android.media.MediaPlayer;

/**
 * Created by Zach on 4/20/2014.
 */
public  class MenusMediaPlayer {

    public static MediaPlayer menuMusic;

    // Suppress default constructor for noninstantiability
    private MenusMediaPlayer() {
        throw new AssertionError();
    }

    public static void releaseMusic(){
        try{
            menuMusic.release();
        }
        catch(Exception e)
        {

        }
    }
}
