package com.cinematic.cinematic;

import java.io.File;
import java.io.FileInputStream;

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

public class TakeVideo extends Activity implements OnClickListener{

	private static final int SELECT_VIDEO = 1;
	private static final int RECORD_VIDEO = 2;
	
	private static final String TAG = "MainActivity";
	
	private byte[] videoFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_video);
		
		setupButtonClickListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
				Uri videoUri = data.getData();
				String path = getPath(videoUri);
				Log.i(TAG,path);
				boolean success = readFromFile(path);
					
				if(success){
						
				}
				else{
					
				}
				
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
		
		String[] projection = {MediaStore.Video.Media.DATA};
		Cursor cursor = managedQuery(uri,projection,null,null,null);
		int column_index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	public void onClick(View v){
		switch(v.getId()) {
		
		case R.id.exit:
			this.finish();
			break;
		
		case R.id.selectVideo:
			Intent intent = new Intent();
			intent.setType("video/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, "Select Video"), SELECT_VIDEO);
			break;
			
		case R.id.recordVideo:
			//File file = new File("cinematic.mp4");
			Intent recordIntent = new Intent();
			recordIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
			recordIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			//recordIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			startActivityForResult(recordIntent, RECORD_VIDEO);
			break;
			
		}
	}

}
