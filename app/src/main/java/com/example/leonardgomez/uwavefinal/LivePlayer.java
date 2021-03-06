package com.example.leonardgomez.uwavefinal;

import android.content.ComponentName;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LivePlayer extends MainActivity {

    private ImageView mAlbumArt;
    private TextView mTitleTextView;
    private TextView mArtistTextView;
    private ImageButton playPause;
    private String temp = "";
    private Handler handler;
    public String songTitle = "";
    public String artistAndAlbum = "";
    public String data = "";
    public String songTitle2 = "";
    public String artistAndAlbum2 = "";
    public boolean invisible = false;
    private MediaBrowserCompat mMediaBrowser;
    public ProgressBar progressBar2;


    private boolean isPlaying;

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallbacks = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {

            // Get the token for the MediaSession
            MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();

            try {
                // Create a MediaControllerCompat
                MediaControllerCompat mediaController =
                        new MediaControllerCompat(LivePlayer.this, // Context
                                token);

                if(mediaController.getPlaybackState().getState() == (PlaybackStateCompat.STATE_PLAYING)) {
                    playPause.setImageResource(R.drawable.stop_button);
                }

                // Save the controller
                MediaControllerCompat.setMediaController(LivePlayer.this, mediaController);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // Finish building the UI
            buildTransportControls();

        }

        @Override
        public void onConnectionSuspended() {
            // The Service has crashed. Disable transport controls until it automatically reconnects
        }

        @Override
        public void onConnectionFailed() {
            // The Service has refused our connection
        }
    };

    MediaControllerCompat.Callback controllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {

                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);

                    switch (state.getState()) {
                        case PlaybackStateCompat.STATE_PLAYING:
                            progressBar2.setVisibility(View.GONE);
                            playPause.setVisibility(View.VISIBLE);
                            playPause.setImageResource(R.drawable.stop_button);
                            break;
                        case PlaybackStateCompat.STATE_STOPPED:
                            progressBar2.setVisibility(View.GONE);
                            playPause.setImageResource(R.drawable.play_button);
                            break;
                        case PlaybackStateCompat.STATE_BUFFERING:
                            playPause.setVisibility(View.INVISIBLE);
                            progressBar2.setVisibility(View.VISIBLE);
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        handler = new Handler();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_player);

        playPause = findViewById(R.id.playButton);

        //Create MediaBrowserServiceCompat
        mMediaBrowser = new MediaBrowserCompat(this, new ComponentName(this, MediaPlaybackService.class), mConnectionCallbacks,
                null);

        // Construct menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar3);
        handler.post(new Runnable() {
            public TextView songName = (TextView) findViewById(R.id.songName);
            public TextView artistAndAlbumName = (TextView) findViewById(R.id.artistAndAlbum);
            @Override
            public void run() {
                Log.d("Handlers", "Called on main thread");
                fetchSongData2 song = new fetchSongData2();
                song.execute();
                if (invisible) {
                    artistAndAlbumName.setVisibility(View.INVISIBLE);
                    songName.setVisibility(View.INVISIBLE);
                } else {
                    artistAndAlbumName.setVisibility(View.VISIBLE);
                    songName.setVisibility(View.VISIBLE);
                    songName.setText(songTitle);
                    artistAndAlbumName.setText(artistAndAlbum);
                }
                handler.postDelayed(this, 1000);
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        mMediaBrowser.connect();
        // Define the code block to be executed
    }

    @Override
    public void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onStop() {
        super.onStop();
        // (see "stay in sync with the MediaSession")
        if (MediaControllerCompat.getMediaController(LivePlayer.this) != null) {
            MediaControllerCompat.getMediaController(LivePlayer.this).unregisterCallback(controllerCallback);
        }
        mMediaBrowser.disconnect();

    }


    void buildTransportControls() {
        // Grab the view for the play/pause button
        playPause = (ImageButton) findViewById(R.id.playButton);

        // Attach a listener to the button
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since this is a play/pause button, you'll need to test the current state
                // and choose the action accordingly

                int pbState = MediaControllerCompat.getMediaController(LivePlayer.this).getPlaybackState().getState();
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    MediaControllerCompat.getMediaController(LivePlayer.this).getTransportControls().stop();
                } else {
                    MediaControllerCompat.getMediaController(LivePlayer.this).getTransportControls().play();
                }
            }
        });

        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(LivePlayer.this);

        // Display the initial state
        MediaMetadataCompat metadata = mediaController.getMetadata();
        PlaybackStateCompat pbState = mediaController.getPlaybackState();

        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback);
    }

    public class fetchSongData2 extends AsyncTask<Void, Void, Void> {
        //public TextView songName = (TextView) findViewById(R.id.songName);
        //public TextView artistAndAlbumName = (TextView) findViewById(R.id.artistAndAlbum);

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                data = "";
                //songTitle = "";
                //artistAndAlbum = "";
                URL url = new URL("https://uwave.fm/listen/now-playing.json");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    if (line != null) {
                        data += line;
                    }
                }

                try {
                    JSONObject songData = new JSONObject(data);
                    songTitle = songData.getString("title");
                    if (songData.getString("artist").equals("") && songData.getString("album").equals("")) {
                        invisible = true;
                        //artistAndAlbumName.setVisibility(View.INVISIBLE);
                    } else
                        invisible = false;
                        artistAndAlbum = songData.getString("artist") + " - " + songData.getString("album");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                artistAndAlbum = "**Could not connect to UWave Server at this time**";
                //playPause.setVisibility(View.GONE);
                //throw new Error("Failed.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //songName.setText(songTitle);
            //artistAndAlbumName.setText(artistAndAlbum);
        }
    }
}
