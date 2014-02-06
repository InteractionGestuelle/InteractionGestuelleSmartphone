package com.igs.igs;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class MusicActivity extends IntentService {

    /*private Button Play_pause , Next , Back , Stop;
    private SeekBar VolumeUp;*/


    /*Deuxieme*/

    private MediaPlayer mediaPlayer;
    private ArrayList<MediaPlayer> mediaPlayer1;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    public static int oneTimeOnly = 0;
    private int numMusicCurrent = 0;
    private int nbMusic=0;
    private ArrayList<String> musiqueTitre=new ArrayList<String>();
    File liste_file;//=new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/DCIM/Camera");


    /*Deuxieme*/
    private AudioManager myAudioManager;

    public MusicActivity() {
        super("ServiceMusique");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer1= new ArrayList<MediaPlayer>();

        liste_file= Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC);
        Log.e("", "CheminMusique  :  " + liste_file);

        //nbMusic=liste_file.list().length;

        //mediaPlayer = MediaPlayer.create(this, R.raw.music1);

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
        };

        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);



        Log.e("","liste de musique 1 nbMusic  : " + cursor.getCount());

        int i=0;

        while(cursor.moveToNext())
        {

            //mediaPlayer1[i]=MediaPlayer.create(this, Uri.parse(liste_file+"/"+liste_file.list()[i].));
            //   Log.e("","liste de musique 1 nbMusic  : " +  Uri.parse(cursor.getString(3)));
            musiqueTitre.add(cursor.getString(2));
            Log.e("", "liste repertoire  : " + i);
            Log.e(""," Longeur MP3 "+Uri.parse(cursor.getString(3)).toString().charAt(cursor.getString(3).length()-1));
            if(Uri.parse(cursor.getString(3)).toString().charAt(cursor.getString(3).length()-1)=='3')
            {
                    mediaPlayer1.add(MediaPlayer.create(this, Uri.parse(cursor.getString(3))));
                    nbMusic++;

                Log.e("","liste repertoire  : " +  Uri.parse(cursor.getString(3)));
            }

        }
        /*Log.e("","liste de musique 1  : " +  liste_file);
        Log.e("","liste de musique 1  : " +  liste_file+"/"+liste_file.list()[1]);*/


    }


    public void play(){

        mediaPlayer1.get(numMusicCurrent).start();
        finalTime=mediaPlayer1.get(numMusicCurrent).getDuration();
        startTime=mediaPlayer1.get(numMusicCurrent).getCurrentPosition();
        if(oneTimeOnly == 0){
            oneTimeOnly = 1;
        }


        myHandler.postDelayed(UpdateSongTime,100);




    }

    private Runnable UpdateSongTime = new Runnable() {
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        public void run() {
            //startTime = mediaPlayer.getCurrentPosition();
            startTime=mediaPlayer1.get(numMusicCurrent).getCurrentPosition();

            if((int)mediaPlayer1.get(numMusicCurrent).getCurrentPosition()+80>=(int)mediaPlayer1.get(numMusicCurrent).getDuration())
            {

                next();
            }



        }
    };
    public void pause(){

     //   Toast.makeText(getApplicationContext(), "Pausing sound", Toast.LENGTH_SHORT).show();
        mediaPlayer1.get(numMusicCurrent).pause();
        //mediaPlayer.pause();
    }



    public void stop()
    {
        // mediaPlayer1[numMusicCurrent].pause();
        //mediaPlayer1[numMusicCurrent].s
        mediaPlayer1.get(numMusicCurrent).seekTo(0);
        //mediaPlayer1[numMusicCurrent].reset();
        mediaPlayer1.get(numMusicCurrent).pause();


        //mediaPlayer1[numMusicCurrent].
    }




    public void next(){

      //  Toast.makeText(getApplicationContext(), "Go to Next sound", Toast.LENGTH_SHORT).show();
        stop();
        oneTimeOnly=0;
        numMusicCurrent=(numMusicCurrent+1)%nbMusic;
        play();



    }

    public void back()
    {
       // Toast.makeText(getApplicationContext(), "Go to back sound", Toast.LENGTH_SHORT).show();
        stop();

        if(numMusicCurrent==0)
        {
            numMusicCurrent=nbMusic;
        }
        numMusicCurrent=numMusicCurrent-1;



       play();

    }
    public void forward(){
        int temp = (int)startTime;
        if((temp+forwardTime)<=finalTime){
            startTime = startTime + forwardTime;
            mediaPlayer1.get(numMusicCurrent).seekTo((int) startTime);
            //mediaPlayer.seekTo((int) startTime);
        }
        else{
            Toast.makeText(getApplicationContext(), "Cannot jump forward 5 seconds",Toast.LENGTH_SHORT).show();
        }

    }
    public void rewind(){
        int temp = (int)startTime;
        if((temp-backwardTime)>0){
            startTime = startTime - backwardTime;
            mediaPlayer1.get(numMusicCurrent).seekTo((int) startTime);
            //mediaPlayer.seekTo((int) startTime);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Cannot jump backward 5 seconds",
                    Toast.LENGTH_SHORT).show();
        }

    }







    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if(extras!=null){
            if(extras.getInt("action")==1){
                play();
            }
        }
    }


}
