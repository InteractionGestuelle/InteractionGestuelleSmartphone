package com.igs.igs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MusicActivity extends Activity {

    /*private Button Play_pause , Next , Back , Stop;
    private SeekBar VolumeUp;*/


    /*Deuxieme*/

    public TextView songName,startTimeField,endTimeField;
    private MediaPlayer mediaPlayer;
    private ArrayList<MediaPlayer> mediaPlayer1;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private ImageButton playButton,pauseButton,nextButton,backButton, forwardButton,rewindButton;
    public static int oneTimeOnly = 0;
    private int numMusicCurrent = 0;
    private int nbMusic=0;
    private ArrayList<String> musiqueTitre=new ArrayList<String>();
    File liste_file;//=new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/DCIM/Camera");
    private List<String> songs = new ArrayList<String>();
    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);


    /*Deuxieme*/
    private AudioManager myAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        mediaPlayer1= new ArrayList<MediaPlayer>();
        songName = (TextView)findViewById(R.id.textView4);
        startTimeField =(TextView)findViewById(R.id.textView1);
        endTimeField =(TextView)findViewById(R.id.textView2);
        seekbar = (SeekBar)findViewById(R.id.seekBar1);
        playButton = (ImageButton)findViewById(R.id.playButton);
        pauseButton = (ImageButton)findViewById(R.id.pauseButton);
        backButton=(ImageButton)findViewById(R.id.backButton);
        nextButton=(ImageButton)findViewById(R.id.nextButton);
        forwardButton=(ImageButton)findViewById(R.id.forwardButton);
        rewindButton=(ImageButton)findViewById(R.id.rewindButton);

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

        Cursor cursor = this.managedQuery(
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
        seekbar.setClickable(false);
        pauseButton.setEnabled(false);

        songName.setText(musiqueTitre.get(0));
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                play();
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                pause();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                next();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                back();
            }
        });
        forwardButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                forward();
            }
        });
        rewindButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                rewind();
            }
        });
        /*Log.e("","liste de musique 1  : " +  liste_file);
        Log.e("","liste de musique 1  : " +  liste_file+"/"+liste_file.list()[1]);*/


    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void play(){
     //   Toast.makeText(getApplicationContext(), "Playing sound", Toast.LENGTH_SHORT).show();

        //mediaPlayer.start();
        //finalTime = mediaPlayer.getDuration();
        //startTime = mediaPlayer.getCurrentPosition();
        //mediaPlayer1[nu]
        mediaPlayer1.get(numMusicCurrent).start();
        finalTime=mediaPlayer1.get(numMusicCurrent).getDuration();
        startTime=mediaPlayer1.get(numMusicCurrent).getCurrentPosition();
        songName.setText(musiqueTitre.get(numMusicCurrent));
        if(oneTimeOnly == 0){
            seekbar.setMax((int) finalTime);
            oneTimeOnly = 1;
        }

        endTimeField.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) finalTime)))
        );
        startTimeField.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) startTime)))
        );
        seekbar.setProgress((int) startTime);
        myHandler.postDelayed(UpdateSongTime,100);
        pauseButton.setEnabled(true);
        playButton.setEnabled(false);



    }

    private Runnable UpdateSongTime = new Runnable() {
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        public void run() {
            //startTime = mediaPlayer.getCurrentPosition();
            startTime=mediaPlayer1.get(numMusicCurrent).getCurrentPosition();
            startTimeField.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            Log.i("","temps fin : " +mediaPlayer1.get(numMusicCurrent).getDuration());
            Log.i("","temps debut: " +mediaPlayer1.get(numMusicCurrent).getCurrentPosition());

            seekbar.setProgress((int)startTime);
            if((int)mediaPlayer1.get(numMusicCurrent).getCurrentPosition()+80>=(int)mediaPlayer1.get(numMusicCurrent).getDuration())
            {

                next();
            }

            myHandler.postDelayed(this, 20);



        }
    };
    public void pause(){

     //   Toast.makeText(getApplicationContext(), "Pausing sound", Toast.LENGTH_SHORT).show();
        mediaPlayer1.get(numMusicCurrent).pause();
        //mediaPlayer.pause();
        pauseButton.setEnabled(false);
        playButton.setEnabled(true);
    }



    public void stop()
    {
        // mediaPlayer1[numMusicCurrent].pause();
        //mediaPlayer1[numMusicCurrent].s
        mediaPlayer1.get(numMusicCurrent).seekTo(0);
        seekbar.setProgress((int) startTime);

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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
