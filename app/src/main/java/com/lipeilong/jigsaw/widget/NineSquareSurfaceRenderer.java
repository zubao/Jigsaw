package com.lipeilong.jigsaw.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.lipeilong.jigsaw.filter.CameraFilter;
import com.lipeilong.jigsaw.gles.FullFrameRect;
import com.lipeilong.jigsaw.log.JDLog;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class NineSquareSurfaceRenderer implements GLSurfaceView.Renderer {

     final Context mContext;
     final float[] mSTMatrix = new float[16];
     final boolean mCreateSurfaceTexture;

    int mSurfaceWidth, mSurfaceHeight;
     FullFrameRect mFullScreen;
     int mTextureId;

    public NineSquareSurfaceRenderer(Context context, boolean createSurfaceTexture) {
        mContext = context;
        mCreateSurfaceTexture = createSurfaceTexture;
    }


    public void setCameraPreviewSize(int width, int height) {

        float scaleHeight = mSurfaceWidth / (width * 1f / height * 1f);
        float surfaceHeight = mSurfaceHeight;

        if (mFullScreen != null) {
            mFullScreen.scaleMVPMatrix(1f, scaleHeight / surfaceHeight);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Matrix.setIdentityM(mSTMatrix, 0);
        JDLog.log("thread id : "+Thread.currentThread().getId()+ " hashcode : "+this.hashCode());

        mFullScreen = new FullFrameRect(getCamerafilter());
        mTextureId  = mFullScreen.createTexture();

        if(mCreateSurfaceTexture){
            SurfaceTextureHolder.getInstance().createSurfaceTexture(mTextureId);
        }
    }

    public CameraFilter getCamerafilter(){
        return new CameraFilter(mContext);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (gl != null) {
            gl.glViewport(0, 0, width, height);
        }
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }



    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (SurfaceTextureHolder.getInstance()){
            SurfaceTexture surfaceTexture   = SurfaceTextureHolder.getInstance().getSurfaceTexture();
            if(surfaceTexture == null){
                return ;
            }

            surfaceTexture.attachToGLContext(mTextureId);
            surfaceTexture.updateTexImage();
            surfaceTexture.getTransformMatrix(mSTMatrix);
            mFullScreen.drawFrame(mTextureId, mSTMatrix);
            surfaceTexture.detachFromGLContext();
        }
    }

}
