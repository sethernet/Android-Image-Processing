package com.cinematic.cinematic;


import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.MediaController;
import android.widget.VideoView;
import android.util.Log;
import android.view.Menu;

public class PlayVideo extends Activity {

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
		
	

	/* public String videoPath(String path){
		
	}
		
	
	public void showVideo(Uri videoURI) {

				
				String path = getPath(videoURI);
				
				MediaPlayer mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				try {
					mediaPlayer.setDataSource(path);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					mediaPlayer.prepare();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mediaPlayer.start();
				
		/*		VideoView videoview = (VideoView) findViewById(R.id.videoview);
				videoview.setKeepScreenOn(true);
				
				if (videoURI != null) {
					videoview.setVideoURI(videoURI);
				}
				else {
					videoview.setVideoURI(videoURI);
				}
				
				MediaController mediacontroller = new MediaController(this);
				mediacontroller.setAnchorView(videoview);
				videoview.setMediaController(mediacontroller);
				if (videoview.canSeekForward()){
					videoview.seekTo(videoview.getDuration()/2);
				}
				
				videoview.start();    */
				
		
	/*}
	
		private String getPath(Uri uri){
		
			String[] projection = {MediaStore.Video.Media.DATA};
			@SuppressWarnings("deprecation")
			Cursor cursor = managedQuery(uri,projection,null,null,null);
			int column_index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
	}  */

}
