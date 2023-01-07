package com.codingsick.maneet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codingsick.maneet.databinding.ActivityMainBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;
import com.yausername.youtubedl_android.mapper.VideoFormat;
import com.yausername.youtubedl_android.mapper.VideoInfo;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    public static final String TAG="TAG";
    private ActivityMainBinding mBinding;
    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    private DefaultTrackSelector trackSelector;
    private FrameLayout mFrameLayout;
    private AlertDialog.Builder builder;
    private   String[] speed = {"0.25x", "0.5x", "Normal", "1.5x", "2x"};
    private MediaItem mediaItem;

    //all controller widget
   private TextView speedTxt;
   private ImageView speedBtn;
   private ImageView setting;
   private ImageView rewBtn;
   private ImageView farwordBtn;
   private ImageView fullscreenButton;
   private long num;
   private ArrayList<String> listOfurl;
    private Executor mExecutors = Executors.newSingleThreadExecutor();
    private static int live=0,normal=0,dash=0;//this variable may be not use in production depend on developer logic


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initializeAll();
        mBinding.exoPlayerView.hideController();

        speedBtn.setOnClickListener(v -> {

            builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Set Speed");
            builder.setItems(speed, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]

                    if (which == 0) {

                        speedTxt.setVisibility(View.VISIBLE);
                        speedTxt.setText("0.25X");
                        PlaybackParameters param = new PlaybackParameters(0.5f);
                        simpleExoPlayer.setPlaybackParameters(param);


                    } if (which == 1) {

                        speedTxt.setVisibility(View.VISIBLE);
                        speedTxt.setText("0.5X");
                        PlaybackParameters param = new PlaybackParameters(0.5f);
                        simpleExoPlayer.setPlaybackParameters(param);


                    }
                    if (which == 2) {

                        speedTxt.setVisibility(View.GONE);
                        PlaybackParameters param = new PlaybackParameters(1f);
                        simpleExoPlayer.setPlaybackParameters(param);


                    }
                    if (which == 3) {
                        speedTxt.setVisibility(View.VISIBLE);
                        speedTxt.setText("1.5X");
                        PlaybackParameters param = new PlaybackParameters(1.5f);
                        simpleExoPlayer.setPlaybackParameters(param);

                    }
                    if (which == 4) {


                        speedTxt.setVisibility(View.VISIBLE);
                        speedTxt.setText("2X");

                        PlaybackParameters param = new PlaybackParameters(2f);
                        simpleExoPlayer.setPlaybackParameters(param);


                    }


                }
            });
            builder.show();


        });
        farwordBtn.setOnClickListener(v -> simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000));
        rewBtn.setOnClickListener(v -> {

            num = simpleExoPlayer.getCurrentPosition() - 10000;
            if (num < 0) {

                simpleExoPlayer.seekTo(0);


            } else {

                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() - 10000);

            }


        });
        fullscreenButton = playerView.findViewById(R.id.fullscreen);
        fullscreenButton.setOnClickListener(view -> {


            int orientation = MainActivity.this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // code for portrait mode

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.MATCH_PARENT);
                mFrameLayout.setLayoutParams(params);


            } else {
                // code for landscape mode
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , 700);
                mFrameLayout.setLayoutParams(params);
                Toast.makeText(MainActivity.this, "protarit", Toast.LENGTH_SHORT).show();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }


        });

        findViewById(R.id.exo_play).setOnClickListener(v -> simpleExoPlayer.play());
        findViewById(R.id.exo_pause).setOnClickListener(v -> simpleExoPlayer.pause());


        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == ExoPlayer.STATE_ENDED) {

                    //if videos end do some step if you want

                }else if(state==ExoPlayer.STATE_READY)
                {
                    Log.d(TAG, "onPlaybackStateChanged");
                }

            }
        });


        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {


            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);

                getMenuInflater().inflate(R.menu.video_quality, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        switch (id) {
                            case R.id.normal:
                                setVideoQuality(0); //144p
                                break;
                            case R.id.mediam:
                                if(listOfurl.size()>=2)
                                setVideoQuality(1);//240p
                                else
                                    Toast.makeText(MainActivity.this, "format not available", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.high:
                                if(listOfurl.size()>=3)
                                setVideoQuality(2); //360p
                                else
                                    Toast.makeText(MainActivity.this, "format not available", Toast.LENGTH_SHORT).show();

                                break;
                            case R.id.veryhigh:
                                if(listOfurl.size()>=4)
                                setVideoQuality(3); //480
                                else
                                    Toast.makeText(MainActivity.this, "format not available", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.beter:
                                if(listOfurl.size()>=5)
                                setVideoQuality(4); //720p
                                else
                                    Toast.makeText(MainActivity.this, "format not available", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.best:
                                if(listOfurl.size()>=6)
                                setVideoQuality(5); //1080p
                                else
                                    Toast.makeText(MainActivity.this, "format not available", Toast.LENGTH_SHORT).show();

                                break;

                        }
                        Toast.makeText(MainActivity.this, "id number :" + id, Toast.LENGTH_SHORT).show();

                        return false;
                    }


                });
                popupMenu.show();

            }
        });

        mBinding.btnlive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText=findViewById(R.id.ed_url);
                String url=editText.getText().toString();
                if(TextUtils.isEmpty(url))
                {
                    Toast.makeText(MainActivity.this, "please provide url", Toast.LENGTH_SHORT).show();

                }else
                {
                    if(simpleExoPlayer.isPlaying())
                    {
                        simpleExoPlayer.stop();
                    }
                    // YoutubeRequestDldecode(url.trim());
                    mBinding.progressBar.setVisibility(View.VISIBLE);
                    mBinding.exoPlayerView.hideController();
                    LiveVideo(url.trim());

                }
            }
        });

mBinding.btnPlaynext.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(new Intent(MainActivity.this,Play_NormalVideos.class));

    }
});


    }//on create close

    private void LiveVideo(final String youtubeURl) {
        listOfurl=new ArrayList<>();
        listOfurl.clear();

        try {
            YoutubeDL.getInstance().init(getApplication());
        } catch (YoutubeDLException e) {
            Log.e(TAG, "failed to initialize youtubedl-android" +e);
            Toast.makeText(MainActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        mExecutors.execute(new Runnable() {
            @Override
            public void run() {
                String tempurl = null;
                YoutubeDLRequest request = new YoutubeDLRequest(youtubeURl);
                // request.addOption("-f", "bestvideo+bestaudio/best");
                request.addOption("-f", "best");
                try {

                    VideoInfo streamInfo = YoutubeDL.getInstance().getInfo(request);
                    String  audeoinfo=null;

                    //set the first time player by getting one url

                    runOnUiThread(() -> {
                        //play the video from here
                        mediaItem = MediaItem.fromUri(Uri.parse(streamInfo.getUrl()));
                        simpleExoPlayer.setMediaItem(mediaItem);
                        simpleExoPlayer.prepare();
                        mBinding.progressBar.setVisibility(View.GONE);
                        mBinding.exoPlayerView.showController();
                        simpleExoPlayer.play();

                    });




                    ArrayList<VideoFormat> streamInfo1 = YoutubeDL.getInstance().getInfo(request).getFormats();
                    for (VideoFormat videoFormat:streamInfo1)
                    {
                        String qulity=videoFormat.getFormatId();
                        if(qulity.equals("91") || qulity.equals("92")||qulity.equals("93")
                        ||qulity.equals("94")||qulity.equals("95")||qulity.equals("96"))
                        {
                            //this logic only for use 144p player
                            tempurl=videoFormat.getUrl();
                            listOfurl.add(tempurl);



                        }
                        Log.d("list", "fromat :"+videoFormat.getFormat()+" id "+videoFormat.getFormatId());
                    }
                    if(listOfurl.size()>0)
                    tempurl=listOfurl.get(0); //when videos play low quality set own quality if want change the value 0 -> 1
                    //    Toast.makeText(MainActivity.this, "fromat "+streamInfo1, Toast.LENGTH_SHORT).show();


                    //  url=streamInfo.getUrl();


                    if (TextUtils.isEmpty(tempurl)) {
                        runOnUiThread(() -> {
                             Toast.makeText(MainActivity.this, "failed to play try again", Toast.LENGTH_LONG).show();
                        });
                        return;
                    }

                    //  simpleExoPlayer.setPlayWhenReady(true);
                }catch (Exception e)
                {
                       runOnUiThread(() -> {
                           Toast.makeText(MainActivity.this, "something went wrong "+e.getMessage(), Toast.LENGTH_SHORT).show();
                       });
                    Log.d("ex", "Youtube"+e.getMessage());
                }
                String finalUrl = tempurl;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(finalUrl!=null) {
                            setting.setVisibility(View.VISIBLE);
                           /* old method logic mediaItem = MediaItem.fromUri(Uri.parse(finalUrl));
                            simpleExoPlayer.setMediaItem(mediaItem);
                            simpleExoPlayer.prepare();
                            mBinding.progressBar.setVisibility(View.GONE);
                            mBinding.exoPlayerView.showController();
                            simpleExoPlayer.play();*/
                        }else
                        {
                            Toast.makeText(MainActivity.this, "went wrong", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

    }

    private void setVideoQuality(int i) {

         if(simpleExoPlayer.isPlaying())
         {
             long l = simpleExoPlayer.getCurrentPosition();
                 mediaItem = MediaItem.fromUri(Uri.parse(listOfurl.get(i)));
                 simpleExoPlayer.setMediaItem(mediaItem);
               /*  simpleExoPlayer.prepare();
                 simpleExoPlayer.seekTo(l);*/
                 simpleExoPlayer.play();
                 mBinding.progressBar.setVisibility(View.GONE);
                 mBinding.exoPlayerView.hideController();





         }else
         {
             Toast.makeText(MainActivity.this, "wait videos is not playing ", Toast.LENGTH_SHORT).show();
         }

    }


    private void initializeAll() {

        trackSelector = new DefaultTrackSelector(this);
        simpleExoPlayer = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
        playerView = findViewById(R.id.exoPlayerView);
        mFrameLayout=findViewById(R.id.fram);
        playerView.setPlayer(simpleExoPlayer);//here is SimplaeExoplyer set in player view
        //all controller widget
        farwordBtn = playerView.findViewById(R.id.fwd);
        rewBtn = playerView.findViewById(R.id.rew);
        setting = playerView.findViewById(R.id.exo_track_selection_view);
        setting.setVisibility(View.INVISIBLE);
        speedBtn = playerView.findViewById(R.id.exo_playback_speed);
        speedTxt = playerView.findViewById(R.id.speed);
        fullscreenButton = playerView.findViewById(R.id.fullscreen);


    }


    //from activity  life cycle class


    @Override
    protected void onStop() {
        simpleExoPlayer.pause();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        simpleExoPlayer.play();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        releasePlayer();

        super.onDestroy();
    }

    private void releasePlayer() {


        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
            trackSelector = null;
        }

    }
}