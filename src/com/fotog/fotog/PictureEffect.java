package com.fotog.fotog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.media.MediaPlayer.OnVideoSizeChangedListener;

import android.opengl.EGLDisplay;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.fotog.fotog.R;

public class PictureEffect extends Activity implements GLSurfaceView.Renderer {

	private static final String SAVED_TEXTURE = null;
	//Fields used for the effects
    private GLSurfaceView mEffectView;
    private int[] mTextures = new int[2];
    private EffectContext mEffectContext;
    private Effect mEffect;
    private TextureRenderer mTexRenderer = new TextureRenderer();
    private int mImageWidth;
    private int mImageHeight;
    private boolean mInitialized = false;
    int mCurrentEffect;
    int otherMenuOptions;
	private File dir_image;
    
	private static String imagePath;
	 //private GLToolbox glToolbox;

	//sets effect chosen from menu
    public void setCurrentEffect(int effect) {
        mCurrentEffect = effect;
    }

    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_effect_view);
        
        //get video intent from StartScreen.java
        imagePath = getIntent().getStringExtra("SELECTED_IMAGE_PATH");
        Uri pictureUri = Uri.parse(imagePath); 
        
        /**
         * Initializes renderer and tell it to only render when
         * requested with the RENDERMODE_WHEN_DIRTY option
         */
        mEffectView = (GLSurfaceView) findViewById(R.id.glsurfaceview1);
        mEffectView.setEGLContextClientVersion(2);
        mEffectView.setRenderer(this);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mCurrentEffect = R.id.none;  
  
    }

    private void loadTextures() {
        // Generate textures
        GLES20.glGenTextures(2, mTextures, 0); 
        
        //create bitmap from path of selected image 
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        
        // Load input bitmap
        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();
        mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

        // Upload bitmap to texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Set texture parameters
        GLToolbox.initTexParams();
    }
    

    //Effect methods
    @SuppressLint({ "InlinedApi", "NewApi" })
	private void initEffect() {
        EffectFactory effectFactory = mEffectContext.getFactory();
        if (mEffect != null) {
            mEffect.release();
        }
        if (mCurrentEffect == R.id.none) {
		} else if (mCurrentEffect == R.id.autofix) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_AUTOFIX);
			mEffect.setParameter("scale", 0.5f);
		} else if (mCurrentEffect == R.id.bw) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_BLACKWHITE);
			mEffect.setParameter("black", .1f);
			mEffect.setParameter("white", .7f);
		} else if (mCurrentEffect == R.id.brightness) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_BRIGHTNESS);
			mEffect.setParameter("brightness", 2.0f);
		} else if (mCurrentEffect == R.id.contrast) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_CONTRAST);
			mEffect.setParameter("contrast", 1.4f);
		} else if (mCurrentEffect == R.id.crossprocess) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_CROSSPROCESS);
		} else if (mCurrentEffect == R.id.documentary) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_DOCUMENTARY);
		} else if (mCurrentEffect == R.id.duotone) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_DUOTONE);
			mEffect.setParameter("first_color", Color.YELLOW);
			mEffect.setParameter("second_color", Color.DKGRAY);
		} else if (mCurrentEffect == R.id.filllight) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_FILLLIGHT);
			mEffect.setParameter("strength", .8f);
		} else if (mCurrentEffect == R.id.fisheye) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_FISHEYE);
			mEffect.setParameter("scale", .5f);
		} else if (mCurrentEffect == R.id.flipvert) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_FLIP);
			mEffect.setParameter("vertical", true);
		} else if (mCurrentEffect == R.id.fliphor) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_FLIP);
			mEffect.setParameter("horizontal", true);
		} else if (mCurrentEffect == R.id.grain) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_GRAIN);
			mEffect.setParameter("strength", 1.0f);
		} else if (mCurrentEffect == R.id.grayscale) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_GRAYSCALE);
		} else if (mCurrentEffect == R.id.lomoish) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_LOMOISH);
		} else if (mCurrentEffect == R.id.negative) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_NEGATIVE);
		} else if (mCurrentEffect == R.id.posterize) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_POSTERIZE);
		} else if (mCurrentEffect == R.id.rotate) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_ROTATE);
			mEffect.setParameter("angle", 180);
		} else if (mCurrentEffect == R.id.saturate) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_SATURATE);
			mEffect.setParameter("scale", .5f);
		} else if (mCurrentEffect == R.id.sepia) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_SEPIA);
		} else if (mCurrentEffect == R.id.sharpen) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_SHARPEN);
		} else if (mCurrentEffect == R.id.temperature) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_TEMPERATURE);
			mEffect.setParameter("scale", .9f);
		} else if (mCurrentEffect == R.id.tint) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_TINT);
			mEffect.setParameter("tint", Color.MAGENTA);
		} else if (mCurrentEffect == R.id.vignette) {
			mEffect = effectFactory.createEffect(
			        EffectFactory.EFFECT_VIGNETTE);
			mEffect.setParameter("scale", .5f);
		} else {
		}
    }

    @SuppressLint("NewApi")
	private void applyEffect() {
        mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
    }

    private void renderResult() {
        if (mCurrentEffect != R.id.none) {
            // if no effect is chosen, just render the original bitmap
            mTexRenderer.renderTexture(mTextures[1]);
        }
        else {
            // render the result of applyEffect()
            mTexRenderer.renderTexture(mTextures[0]);
        }
    }

    
    @SuppressLint("NewApi")
	@Override
    public void onDrawFrame(GL10 gl) {
    	
    	//when save button is selected, first if statement runs; this has to take place in onDraw
    	//in order to access gl
    	if (otherMenuOptions == R.id.savepicture){
    		CreateBitmapParams params = new CreateBitmapParams(0, 0, mImageWidth, mImageHeight, gl);
            Looper.prepare();
            CreateBitmapTask myTask = new CreateBitmapTask();
            myTask.execute(params);            
           
            otherMenuOptions = 0;
    	}
    	
        if (!mInitialized) {
            //Only need to do this once
            mEffectContext = EffectContext.createWithCurrentGlContext();
            mTexRenderer.init();
            loadTextures();
            mInitialized = true;
        }
        if (mCurrentEffect != R.id.none) {
            //if an effect is chosen initialize it and apply it to the texture
            initEffect();
            applyEffect();
        }
        
        renderResult();
        
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mTexRenderer != null) {
            mTexRenderer.updateViewSize(width, height);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    	
    }
    
    
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        // TODO Auto-generated method stub

    }
    
    
    public void surfaceDestroyed(SurfaceHolder holder) 
    {

    }

    public void onCompletion(MediaPlayer mp)
    {
        mp.stop();

        finish();
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.picture_effects_menu, menu);
        
        MenuInflater inflater2 = getMenuInflater();
        inflater2.inflate(R.menu.saveandsharemenu, menu);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	if (item.getItemId() == R.id.savepicture){
    		otherMenuOptions = R.id.savepicture;
    	}
    	
    	if (item.getItemId() == R.id.saveandshare){
    		otherMenuOptions = R.id.saveandshare;
    	}
    	
    		setCurrentEffect(item.getItemId());
            mEffectView.requestRender();
            
            return true;	
    	
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		
		outState.putSerializable(SAVED_TEXTURE, (Serializable) mCurrentEffect);

	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);
		
		mCurrentEffect = (Integer) savedInstanceState.getSerializable(SAVED_TEXTURE);
	}
	
	

	
	//create parameters to be used in AsyncTask
	private static class CreateBitmapParams {
		int x; 
		int y; 
		int w; 
		int h; 
		GL10 gl;

		CreateBitmapParams(int x, int y, int w, int h, GL10 gl) {
	        this.x = x;
	        this.y = y;
	        this.w = w;
	        this.h = h;
	        this.gl = gl;
	    }
	}
	
	
	//convert GL surface view to bitmap
	class CreateBitmapTask extends AsyncTask<CreateBitmapParams, Void, Bitmap> {
		
		@Override
		protected Bitmap doInBackground(CreateBitmapParams... params) throws OutOfMemoryError {
			
			int x = params[0].x;
			int y = params[0].y;
			int w = params[0].w;
			int h = params[0].h;
			GL10 gl = params[0].gl;
			
		    int bitmapBuffer[] = new int[w * h];
		    int bitmapSource[] = new int[w * h];
		    IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
		    intBuffer.position(0);

		    try {
		        gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
		        int offset1, offset2;
		        for (int i = 0; i < h; i++) {
		            offset1 = i * w;
		            offset2 = (h - i - 1) * w;
		            for (int j = 0; j < w; j++) {
		                int texturePixel = bitmapBuffer[offset1 + j];
		                int blue = (texturePixel >> 16) & 0xff;
		                int red = (texturePixel << 16) & 0x00ff0000;
		                int pixel = (texturePixel & 0xff00ff00) | red | blue;
		                bitmapSource[offset2 + j] = pixel;
		            }
		        }
		    } catch (GLException e) {
		       
		    }

		    Bitmap bitmap = Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
		    
		  
			return bitmap;
        	
		}    
	        
	        protected void onPostExecute(Bitmap result){
	        	
	        	//call SaveToDiskTask 
	            SaveBitmapParams sbParams = new SaveBitmapParams(result);
	            SaveToDiskTask saveTask = new SaveToDiskTask();
	            saveTask.execute(sbParams);
	            Looper.loop();
	            
	        }  
		}
	
	 
	//create bitmap parameters to be used in AsyncTask
	private static class SaveBitmapParams {
		
		Bitmap bitmap;

		SaveBitmapParams(Bitmap bitmap) {
	        this.bitmap = bitmap;
    }
}  
	
	
	class SaveToDiskTask extends AsyncTask<SaveBitmapParams, Integer, Void> {

		@Override
		protected Void doInBackground(SaveBitmapParams... params) {
			
			//bring in bitmap and save to disk
			try {
				
			Bitmap bitmap = params[0].bitmap;
			
			 String extr = Environment.getExternalStorageDirectory().toString();
	            File mFolder = new File(extr + "/Fotog");
	            if (!mFolder.exists()) {
	                mFolder.mkdir();
	            }

	            String s = "test.png";

	            File f = new File(mFolder.getAbsolutePath(), s);

	            FileOutputStream fos = null;
	            fos = new FileOutputStream(f);
	            bitmap.compress(CompressFormat.JPEG, 100, fos);
	            fos.flush();
	            fos.close();

	            bitmap.recycle();

	            Toast.makeText(getBaseContext(), "image saved", 5000).show();
	        } catch (Exception e) {
	            Toast.makeText(getBaseContext(), "Failed To Save", 5000).show();
	        }
			
			
			return null;

		}
		
		 protected void onPostExecute(int mCurrentEffect){
	        	
			        
	            
	        } 
		
		
	}
	

}
