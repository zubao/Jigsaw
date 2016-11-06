package com.lipeilong.jigsaw.widget;

import android.graphics.SurfaceTexture;
import android.os.Build;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lipeilong on 16/10/30.
 */

public class SurfaceTextureHolder {

    private static SurfaceTextureHolder sInstance;
    private SurfaceTexture mSurfaceTexture;

    public static SurfaceTextureHolder getInstance(){
        if(sInstance == null){
            synchronized (SurfaceTextureHolder.class){
                if(sInstance == null){
                    sInstance   = new SurfaceTextureHolder();
                }
            }
        }
        return sInstance;
    }

    private SurfaceTextureHolder(){
    };

    public void createSurfaceTexture(int textureId){
        mSurfaceTexture = new SurfaceTexture(textureId);
        mSurfaceTexture.detachFromGLContext();
        EventBus.getDefault().post(new CameraEvent(mSurfaceTexture));
    }


    public SurfaceTexture getSurfaceTexture(){
        return mSurfaceTexture;
    }


    public static void clear(){
        SurfaceTextureHolder.getInstance().release();

        sInstance   = null;
    }

    public void release(){
        if(mSurfaceTexture != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mSurfaceTexture.releaseTexImage();
            }
            mSurfaceTexture.release();
        }

        mSurfaceTexture = null;

    }

}
