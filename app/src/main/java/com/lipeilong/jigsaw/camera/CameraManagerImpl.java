package com.lipeilong.jigsaw.camera;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.lipeilong.jigsaw.util.PermissionUtil;

/**
 * Created by lipeilong on 16/10/29.
 */

public class CameraManagerImpl implements ICameraManager {

    private CameraManagerGlobal mCameraManagerGlobal;

    public CameraManagerImpl(){
        mCameraManagerGlobal    = CameraManagerGlobal.getInstance();
    }

    @Override
    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        mCameraManagerGlobal.setSurfaceTexture(surfaceTexture);
    }

    @Override
    public void open(Activity activity ) {
        PermissionUtil.checkCameraPermission(activity);

        mCameraManagerGlobal.openCameraAsyn();
        mCameraManagerGlobal.startPreAsyn();

        return;
    }

    public void setCameraStateChangeListener(ICameraManager.CameraStateChangeListener listener){
        mCameraManagerGlobal.setCameraStateChangeListener(listener);
    }

    public Camera.Size getPreviewSize(){
        return mCameraManagerGlobal.getPreviewSize();
    }

    @Override
    public void close() {
        mCameraManagerGlobal.closeCameraAsyn();
        return ;
    }

    @Override
    public void release() {
        mCameraManagerGlobal    = null;
        CameraManagerGlobal.release();
    }


}
