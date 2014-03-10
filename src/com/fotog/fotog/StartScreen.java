package com.fotog.fotog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.fotog.fotog.R;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;


public class StartScreen extends Activity implements OnClickListener{

	private static final String SAVED_THUMB = "SAVED_THUMB";
	//constants for onActivityResult
	private static final int SELECT_PICTURE = 1;
	private static final int TAKE_PICTURE = 2;
	
	private static final String TAG = "MainActivity";
	
	//create byte array variable for video
	private byte[] videoFile;
	private Bitmap newImage;
	private ImageView imageThumb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_screen_layout);
		
		setupButtonClickListeners();
		
		//get reference to image view that will display
		imageThumb = (ImageView) findViewById(R.id.imageThumb);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//not using right now, but will in future
		getMenuInflater().inflate(R.menu.take_video, menu);
		return true;
	}
	
	private void setupButtonClickListeners(){
		
		((Button)findViewById(R.id.selectPicture)).setOnClickListener((OnClickListener) this);
		((Button)findViewById(R.id.takePicture)).setOnClickListener((OnClickListener) this);
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
			
			case SELECT_PICTURE:
				
				//create Intent to launch PictureEffect activity
				Intent showImageIntent = new Intent(this, PictureEffect.class);
				
				//get Uri for selected picture
				Uri pictureUri = data.getData();
				
				try {
					//stream of data from file
					InputStream openInputStream = getContentResolver().openInputStream(pictureUri);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//alert user of exception
					Toast.makeText(this, getString(R.string.unable_to_open_image), Toast.LENGTH_LONG).show();
				}
				
				//get path of Uri
				String path = getPath(pictureUri);
				
				//pass path to the PlayVideo activity
				showImageIntent.putExtra("SELECTED_IMAGE_PATH", path);
				
				//start the PlayVideo activity
				startActivity(showImageIntent);
				
				//print the path to console
				Log.i(TAG, path);

				
				break;
			case TAKE_PICTURE:
				Uri recordUri = data.getData();
				String recordPath = getPath(recordUri);
				Log.i(TAG,recordPath);
				
				//make thumbnail of picture	
				newImage = (Bitmap) data.getExtras().get("data");
				imageThumb.setImageBitmap(newImage);
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
		
		//Select Video button selected
		case R.id.selectPicture:
			//Intent that opens up gallery to select photo
			Intent pictureSelectionIntent = new Intent(Intent.ACTION_PICK);
			
			//access directory where images are stored
			String picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
			//convert path to URI
			Uri pictureURI = Uri.parse(picturePath);
			//set the data and type for this intent
			pictureSelectionIntent.setDataAndType(pictureURI, "image/*");
			//start activity and get result
			startActivityForResult(pictureSelectionIntent, SELECT_PICTURE);
			break;
			
		//Record Video button selected	
		case R.id.takePicture:
			//Intent that opens up default video record function
			Intent takePictureIntent = new Intent();
			takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			//recordIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			startActivityForResult(takePictureIntent, TAKE_PICTURE);
			break;
			
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		outState.putParcelable(SAVED_THUMB, newImage);

	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		
		newImage = savedInstanceState.getParcelable(SAVED_THUMB);
		imageThumb.setImageBitmap(newImage);
	}
	
}
