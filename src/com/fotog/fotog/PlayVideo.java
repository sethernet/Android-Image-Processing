package com.fotog.fotog;


import com.fotog.fotog.R;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import android.util.Log;

public class PlayVideo extends Activity {
	// first two are original , rest new
	private static final Context Context = null;
	private static final String TAG = null;
	
	    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//associate layout with activity
		setContentView(R.layout.play_video_layout);
		
		//retrieve path from SELECT_VIDEO in TakeVideo.java
		String videoPath = getIntent().getStringExtra("SELECTED_VIDEO_PATH");
		
		//create the videoview object
		final VideoView videoView = (VideoView) findViewById(R.id.videoView1);
			
	        videoView.setVideoPath(videoPath);
	           
	        MediaController mediaController = new MediaController(this);
	   		mediaController.setAnchorView(videoView);
	   		videoView.setMediaController(mediaController);

	   		videoView.setOnPreparedListener(new 
	   				MediaPlayer.OnPreparedListener()  {
	   			
	               @Override
	               public void onPrepared(MediaPlayer mp) {                      

   
	               	Log.i(TAG, "Duration = " + videoView.getDuration());
	               }
	           });		
	   	    videoView.start();	
	   	  
	   }
	
}