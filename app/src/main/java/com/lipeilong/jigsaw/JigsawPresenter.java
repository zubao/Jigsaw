package com.lipeilong.jigsaw;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.lipeilong.jigsaw.camera.CameraManagerImpl;
import com.lipeilong.jigsaw.camera.ICameraManager;
import com.lipeilong.jigsaw.frame.BaseActivity;
import com.lipeilong.jigsaw.log.JDLog;
import com.lipeilong.jigsaw.widget.SurfaceTextureHolder;

/**
 * Created by lipeilong on 16/10/29.
 */

public class JigsawPresenter {

    private CameraManagerImpl mCameraManagerImpl;
    private IJigsawView mIJigsawView;

    public JigsawPresenter(IJigsawView jigsawView){
        mIJigsawView    = jigsawView;
    }

    public void setupCamera(SurfaceTexture surfaceTexture){
        if(surfaceTexture == null ){
            JDLog.log("setup camera use null obj");
        }
        if(mCameraManagerImpl == null){
            mCameraManagerImpl  = new CameraManagerImpl();
        }

        initCameraStateChangeListener();
        mCameraManagerImpl.setSurfaceTexture(surfaceTexture);
        mCameraManagerImpl.open(BaseActivity.getTopActivity());

    }

    private void initCameraStateChangeListener(){
        mCameraManagerImpl.setCameraStateChangeListener(new ICameraManager.CameraStateChangeListener(){

            @Override
            public void onOperaState(CameraOperaResultType type) {
                switch (type){
                    case START_PREVIEW_SUCCESS:
                        Camera.Size size    = mCameraManagerImpl.getPreviewSize();
                        mIJigsawView.setPreviewSize(size.width, size.height);
                    break;
                    case CLOSE_SUCCESS:
                    case CLOSE_FAIL:
                        SurfaceTextureHolder.clear();
                        break;
                }
            }
        });
    }

    public void closeCamera(){
        if(mCameraManagerImpl != null){
            mCameraManagerImpl.close();
            mCameraManagerImpl.release();
        }
    }
}
