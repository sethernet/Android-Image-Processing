package com.cinematic.cinematic;

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.MediaController;

import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.media.MediaPlayer.OnVideoSizeChangedListener;

import android.opengl.EGLDisplay;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VideoEffect extends Activity implements GLSurfaceView.Renderer {

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
    
    //Fields for grabbing frames from the video
    private MediaMetadataRetriever mmr;
    ArrayList<Bitmap> bitmapArray;
    double duration;
    long counter;
    
    MediaPlayer myMediaPlayer;
    //these were used in a failed attempt to display video, may or may not need
    //at some point
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private Callback callback;
	private static String videoPath;
	 //private GLToolbox glToolbox;

	//sets effect chosen from menu
    public void setCurrentEffect(int effect) {
        mCurrentEffect = effect;
    }

    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gl_effects_view);
        
        //part the failed video attempt, will likely end up deleting
     /**  getWindow().setFormat(PixelFormat.UNKNOWN); 
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView2);
        surfaceHolder = surfaceView.getHolder();                
        surfaceHolder.setFixedSize(176, 144);
        surfaceHolder.addCallback((Callback) callback);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  */
        
        myMediaPlayer = new MediaPlayer();  
        
        //get video intent from StartScreen.java
        videoPath = getIntent().getStringExtra("SELECTED_VIDEO_PATH");
        Uri videoUri = Uri.parse(videoPath); 
        
        //grab video frames
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(videoPath);
        String stringDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        duration = Double.parseDouble(stringDuration);
        
        //play audio file of video
        myMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        myMediaPlayer.reset();     
        
        
       try {
            
            myMediaPlayer.setOnVideoSizeChangedListener(videoSizeChangedListener);
            
            myMediaPlayer.setDataSource(videoPath);
            myMediaPlayer.prepare();
           
            /*
             * if you get Video Width and Height after prepare() here, it always return 0, 0!
             */
            mImageWidth = myMediaPlayer.getVideoWidth();
            mImageHeight = myMediaPlayer.getVideoHeight();
            
            
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
        myMediaPlayer.start(); 
        
        
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

       //convert video frame to bitmap
        bitmapArray = new ArrayList<Bitmap>();
        
        Bitmap bitmap = mmr.getFrameAtTime();
        
        // Load input bitmap
        //Bitmap bitmap = BitmapFactory.decodeFile(videoPath);
        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();
        mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

        // Upload bitmap to texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Set texture parameters
        GLToolbox.initTexParams();
    }
    
    public void addBitmap(Bitmap b){
    	bitmapArray.add(b);
    }

    //Effect methods
    @SuppressLint("InlinedApi")
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

    @Override
    public void onDrawFrame(GL10 gl) {
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
    
    public void surfaceCreated(SurfaceHolder holder) 
    {
    	myMediaPlayer.setDisplay(surfaceHolder);
    	
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
        inflater.inflate(R.menu.video_effects_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        	setCurrentEffect(item.getItemId());
            mEffectView.requestRender();
            return true;	
    	
    }
    
  
    OnVideoSizeChangedListener videoSizeChangedListener = new OnVideoSizeChangedListener(){

    //supposed to make video match view size of phone, but not working right now	
     @Override
     public void onVideoSizeChanged(MediaPlayer arg0, int arg1, int arg2) {
      int videoWidth = myMediaPlayer.getVideoWidth();
      int videoHeight = myMediaPlayer.getVideoHeight();
     }
};


}
