package com.cinematic.cinematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;


public class StartScreen extends Activity implements OnClickListener{

	//constants for onActivityResult
	private static final int SELECT_VIDEO = 1;
	private static final int RECORD_VIDEO = 2;
	
	private static final String TAG = "MainActivity";
	
	//create byte array variable for video
	private byte[] videoFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_screen_layout);
		
		setupButtonClickListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//not using right now, but will in future
		getMenuInflater().inflate(R.menu.take_video, menu);
		return true;
	}
	
	private void setupButtonClickListeners(){
		
		Button exitButton = (Button)findViewById(R.id.exit);
		exitButton.setOnClickListener((OnClickListener) this);
		
		((Button)findViewById(R.id.selectVideo)).setOnClickListener((OnClickListener) this);
		((Button)findViewById(R.id.recordVideo)).setOnClickListener((OnClickListener) this);
	}

	private boolean readFromFile(String path){
		
		//read video file and check for exception
		File file = new File(path);
		try{
			FileInputStream fis = new FileInputStream(file);
			Log.i(TAG,"File Size: " + file.length());
			videoFile = new byte[(int)file.length()];
			fis.read(videoFile);
			fis.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data){
		
		if(resultCode == RESULT_OK) {
			
			switch(requestCode){
			
			case SELECT_VIDEO:
				
				//create Intent to launch PlayVideo activity
				Intent playVideoIntent = new Intent(this, VideoEffect.class);
				
				//get Uri for selected video
				Uri videoUri = data.getData();
				//get path of Uri
				String path = getPath(videoUri);
				
				//pass path to the PlayVideo activity
				playVideoIntent.putExtra("SELECTED_VIDEO_PATH", path);
				
				//start the PlayVideo activity
				startActivity(playVideoIntent);
				
				//print the path to console
				Log.i(TAG, path);

				
				break;
			case RECORD_VIDEO:
				Uri recordUri = data.getData();
				String recordPath = getPath(recordUri);
				Log.i(TAG,recordPath);
				boolean recordSuccess = readFromFile(recordPath); 
				break;
			
				
			}
		}
	}
	
	private String getPath(Uri uri){
		
		//returns path from selected Uri
		String[] projection = {MediaStore.Video.Media.DATA};
		Cursor cursor = managedQuery(uri,projection,null,null,null);
		int column_index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	public void onClick(View v){
		
		//switch based on which button is pushed
		switch(v.getId()) {
		
		//Exit button selected
		case R.id.exit:
			this.finish();
			break;
		
		//Select Video button selected
		case R.id.selectVideo:
			//Intent that opens up gallery to select video
			Intent intent = new Intent();
			intent.setType("video/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, "Select Video"), SELECT_VIDEO);
			break;
			
		//Record Video button selected	
		case R.id.recordVideo:
			//Intent that opens up default video record funtion
			Intent recordIntent = new Intent();
			recordIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
			recordIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			//recordIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			startActivityForResult(recordIntent, RECORD_VIDEO);
			break;
			
		}
	}

}
