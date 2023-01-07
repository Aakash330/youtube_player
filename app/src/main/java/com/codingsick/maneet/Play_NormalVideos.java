package com.codingsick.maneet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.DialogInterface;
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

import com.codingsick.maneet.databinding.ActivityPlayNormalVideosBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.YoutubeDLRequest;
import com.yausername.youtubedl_android.mapper.VideoFormat;
import com.yausername.youtubedl_android.mapper.VideoInfo;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Play_NormalVideos extends AppCompatActivity {

    public static final String TAG = "TAG";
    private ActivityPlayNormalVideosBinding mBinding;
    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    private DefaultTrackSelector trackSelector;
    private FrameLayout mFrameLayout;
    private AlertDialog.Builder builder;
    private String[] speed = {"0.25x", "0.5x", "Normal", "1.5x", "2x"};
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
    private static int normal = 1;//this variable may be not use in production depend on developer logic
    private int checkVideoType;

    private MediaSource audeosource;

    private ArrayList<String> list; //this lis use for store the format type videos


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityPlayNormalVideosBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initializeAll();
         mBinding.exoPlayerView.hideController();
        speedBtn.setOnClickListener(v -> {

            builder = new AlertDialog.Builder(Play_NormalVideos.this);
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


            int orientation = Play_NormalVideos.this.getResources().getConfiguration().orientation;
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
                Toast.makeText(Play_NormalVideos.this, "protarit", Toast.LENGTH_SHORT).show();
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

                } else if (state == ExoPlayer.STATE_READY) {
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

                final PopupMenu popupMenu = new PopupMenu(Play_NormalVideos.this, v);

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
                                if (listOfurl.size() >= 2)
                                    setVideoQuality(1);//240p
                                else
                                    Toast.makeText(Play_NormalVideos.this, "format not available", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.high:
                                if (listOfurl.size() >= 3)
                                    setVideoQuality(2); //360p
                                else
                                    Toast.makeText(Play_NormalVideos.this, "format not available", Toast.LENGTH_SHORT).show();

                                break;
                            case R.id.veryhigh:
                                if (listOfurl.size() >= 4)
                                    setVideoQuality(3); //480
                                else
                                    Toast.makeText(Play_NormalVideos.this, "format not available", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.beter:
                                if (listOfurl.size() >= 5)
                                    setVideoQuality(4); //720p
                                else
                                    Toast.makeText(Play_NormalVideos.this, "format not available", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.best:
                                if (listOfurl.size() >= 6)
                                    setVideoQuality(5); //1080p
                                else
                                    Toast.makeText(Play_NormalVideos.this, "format not available", Toast.LENGTH_SHORT).show();

                                break;

                        }
                        Toast.makeText(Play_NormalVideos.this, "id number :" + id, Toast.LENGTH_SHORT).show();

                        return false;
                    }


                });
                popupMenu.show();

            }
        });


        mBinding.btnPlaynext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText editText = findViewById(R.id.ed_url);
                String url = editText.getText().toString();
                if (TextUtils.isEmpty(url)) {
                    Toast.makeText(Play_NormalVideos.this, "please provide url", Toast.LENGTH_SHORT).show();

                } else {
                    // YoutubeRequestDldecode(url.trim());
                    if(simpleExoPlayer.isPlaying())
                    {
                        simpleExoPlayer.stop();
                    }

                   mBinding.progressBar.setVisibility(View.VISIBLE);
                    mBinding.exoPlayerView.hideController();
                    PlayVideo(url.trim());

                }

            }
        });


    }//oncreate close

    private void PlayVideo(String youtubeUrl) {

       /* if (mediaItem != null) {

            simpleExoPlayer.stop();
            simpleExoPlayer.removeMediaItem(0);

        }*/
        try {


            YoutubeDL.getInstance().init(getApplication());
        } catch (YoutubeDLException e) {
            Log.e(TAG, "failed to initialize youtubedl-android"+ e);
            mBinding.progressBar.setVisibility(View.GONE);

            Toast.makeText(Play_NormalVideos.this, "something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        //this is background thred worker
        mExecutors.execute(() -> {
            listOfurl = new ArrayList<>();
            list = new ArrayList<>();
            list.clear();
            listOfurl.clear();
            String qulity = null;
            String audeoinfo = null;
            boolean p144 = false, p240=false, p360=false, p480=false, p720=false, p1080=false;

            try {
                Log.w("resposne","request run");
                YoutubeDLRequest request = new YoutubeDLRequest(youtubeUrl);

                // request.addOption("-f", "bestvideo+bestaudio/best");
              // request.addOption("-f", "bestvideo");//this works fin9 for every sitiation
               request.addOption("-f", "best");
                Log.w("resposne","request oprtion run");

              //  VideoInfo streamInfo = YoutubeDL.getInstance().getInfo(request);
                Log.w("resposne","request single format run");
                //single format for
                 VideoInfo fromat =  YoutubeDL.getInstance().getInfo(request);
                Log.w("resposne","request url runing single --"+fromat.getUrl());
                runOnUiThread(() -> {
                   //play the video from here
                    MediaSource videSource = new ProgressiveMediaSource
                            .Factory(new DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(Uri.parse(fromat.getUrl())));
                    simpleExoPlayer.setMediaSource(videSource);
                    simpleExoPlayer.prepare();
                    mBinding.progressBar.setVisibility(View.GONE);
                    mBinding.exoPlayerView.showController();
                    simpleExoPlayer.setPlayWhenReady(true);

                });

                ArrayList<VideoFormat> streamInfo1 = YoutubeDL.getInstance().getInfo(request).getFormats();
                 Log.w("resposne","vidoe"+streamInfo1);
                for (VideoFormat videoFormat : streamInfo1) {
                    qulity = videoFormat.getFormatId();
                    if (qulity.equals("140-dash")) {
                        checkVideoType = 2;
                        listOfurl.clear();
                       SetAllDataforDashVideos(streamInfo1);

                        return;
                    } else {
                       // checkVideoType=1;
                        //this is run for normal videos
                        String fromfilter=videoFormat.getFormat();
                        if(qulity.equals("140"))
                        {
                            audeoinfo=videoFormat.getUrl();
                            checkVideoType=1;
                            listOfurl.clear();
                        }else if(fromfilter.contains("x144 (144p)"))
                        {
                            if(!p144)
                            {
                                listOfurl.add(videoFormat.getUrl());
                                p144=true;
                            }
                        }
                        else if(fromfilter.contains("x240 (240p)"))
                        {
                            if(!p240)
                            {
                              listOfurl.add(videoFormat.getUrl());
                                p240=true;
                            }
                        }
                        else if(fromfilter.contains("x360 (360p)"))
                        {
                            if(!p360)
                            {
                                listOfurl.add(videoFormat.getUrl());
                                p360=true;
                            }
                        }
                        else if(fromfilter.contains("x480 (480p)"))
                        {
                            if(!p480)
                            {
                                listOfurl.add(videoFormat.getUrl());
                                p480=true;
                            }
                        }
                        else if(fromfilter.contains("x720 (720p)"))
                        {
                            if(!p720)
                            {
                                listOfurl.add(videoFormat.getUrl());
                                p720=true;
                            }
                        }
                        else if(fromfilter.contains("x1080 (1080p)"))
                        {
                            if(!p1080)
                            {
                                listOfurl.add(videoFormat.getUrl());
                                p1080=true;
                            }
                        }

                    }
                }//loop close here

                //this is audeo soure
                audeosource = new ProgressiveMediaSource
                        .Factory(new DefaultHttpDataSource.Factory())
                        .createMediaSource(MediaItem.fromUri(Uri.parse(audeoinfo)));


                //this is video source
                MediaSource videSource = new ProgressiveMediaSource
                        .Factory(new DefaultHttpDataSource.Factory())
                        .createMediaSource(MediaItem.fromUri(Uri.parse(listOfurl.get(1))));
                Log.d(TAG, "size :"+listOfurl.size());

                //main thread concontroler
                runOnUiThread(() -> {
                   // setting.setEnabled(true);
                    setting.setVisibility(View.VISIBLE);
                 //   setting.setClickable(true);
                  /*  simpleExoPlayer.setMediaSource(new MergingMediaSource(
                                    true,
                                    videSource,
                                    audeosource), true
                    );
                   simpleExoPlayer.prepare();
                    mBinding.progressBar.setVisibility(View.GONE);
                    mBinding.exoPlayerView.showController();
                    simpleExoPlayer.setPlayWhenReady(true);*/

                });

            } catch (Exception exception) {
                //if you want show message to ui the use m
                exception.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(Play_NormalVideos.this, "error try again "
                            + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    mBinding.progressBar.setVisibility(View.GONE);

                });
                Log.d(TAG, "exception" + exception.getMessage());
            }


        });


    }


    private void SetAllDataforDashVideos(ArrayList<VideoFormat> streamInfo1) {
        String formatfilter = null;
        String dashforma = "(DASH video)";
       boolean p144=false, p240=false, p360=false, p480=false, p720=false, p1080=false;

        for (VideoFormat videoFormat : streamInfo1) {
            formatfilter = videoFormat.getFormat();
            Log.d(TAG, "Format : " + formatfilter);
            if (formatfilter.contains("x144 " + dashforma)) {
                if (!p144) {
                    Log.d("id", "id" + videoFormat.getFormatId());
                    listOfurl.add(videoFormat.getUrl());
                    p144 = true;
                }

            } else if (formatfilter.contains("x240 " + dashforma)) {


                if (!p240) {

                    listOfurl.add(videoFormat.getUrl());
                    Log.d("id", "id" + videoFormat.getFormatId());
                    p240 = true;
                }


            } else if (formatfilter.contains("x360 " + dashforma)) {
                Log.d(TAG, "Format : " + "360");

                if (!p360) {

                    listOfurl.add(videoFormat.getUrl());
                    Log.d("id", "id" + videoFormat.getFormatId());
                    p360 = true;
                }

            }
            else if (formatfilter.contains("x480 " + dashforma))
            {
                if (!p480) {

                    listOfurl.add(videoFormat.getUrl());
                    Log.d("id", "id" + videoFormat.getFormatId());
                    p480 = true;

                }
            }
            else if (formatfilter.contains("x720 " + dashforma))
            {
                if (!p720) {

                    listOfurl.add(videoFormat.getUrl());
                    Log.d("id", "id" + videoFormat.getFormatId());
                    p720 = true;

                }
            }
               else if (formatfilter.contains("x1080 " + dashforma))
            {
                if (!p1080) {

                    listOfurl.add(videoFormat.getUrl());
                    Log.d("id", "id" + videoFormat.getFormatId());
                    p1080 = true;

                }
            }
               else
            {
              //dp some step if u want
            }

        } //loop close here

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playDashVideo(listOfurl.get(1));//you can change default quality but this best for default
                Toast.makeText(Play_NormalVideos.this, "value size :" + listOfurl.size(), Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("tatal", "list :" + listOfurl.size()+" name "+listOfurl);
    }


    private void setVideoQuality(int i) {

        if (simpleExoPlayer.isPlaying()) {
            if (checkVideoType == normal) {
                long l = simpleExoPlayer.getCurrentPosition();
               // simpleExoPlayer.pause();

                MediaSource videSource = new ProgressiveMediaSource
                        .Factory(new DefaultHttpDataSource.Factory())
                        .createMediaSource(MediaItem.fromUri(Uri.parse(listOfurl.get(i))));


                simpleExoPlayer.setMediaSource(new MergingMediaSource(
                        true,
                        videSource,
                        audeosource),
                        true
                );
                simpleExoPlayer.seekTo(l);

             //   simpleExoPlayer.play();

            }else
            {
              playDashVideo(listOfurl.get(i));
            }



        } else {
            Toast.makeText(Play_NormalVideos.this, "wait videos is not playing ", Toast.LENGTH_SHORT).show();
        }

    }

    private void playDashVideo(String url1) {


// Create a data source factory.
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
// Create a DASH media source pointing to a DASH manifest uri.
        MediaSource mediaSource =
                new DashMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(url1));
// Create a player instance.

// Set the media source to be played.
        Toast.makeText(Play_NormalVideos.this, "dash url run", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "playDashVideo:" + url1);
        if(simpleExoPlayer.isPlaying() )
        {
            long t=simpleExoPlayer.getCurrentPosition();
           // simpleExoPlayer.pause();
            simpleExoPlayer.setMediaSource(mediaSource);
            mBinding.progressBar.setVisibility(View.GONE);
            mBinding.exoPlayerView.showController();
            simpleExoPlayer.seekTo(t);
           // simpleExoPlayer.play();

        }else {
            simpleExoPlayer.setMediaSource(mediaSource);
           // Prepare the player.
            simpleExoPlayer.prepare();
            simpleExoPlayer.play();
            mBinding.exoPlayerView.showController();
            mBinding.progressBar.setVisibility(View.GONE);

        }


    }


    private void initializeAll() {

        trackSelector = new DefaultTrackSelector(this);
        simpleExoPlayer = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
        playerView = findViewById(R.id.exoPlayerView);
        mFrameLayout = findViewById(R.id.fram);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void releasePlayer() {


        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
            trackSelector = null;
        }

    }
}