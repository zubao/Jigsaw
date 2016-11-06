package com.lipeilong.jigsaw.widget;

import android.graphics.SurfaceTexture;

/**
 * Created by lipeilong on 16/10/29.
 */

public class CameraEvent {
    private final SurfaceTexture mSurfaceTexture;

    public CameraEvent(SurfaceTexture surfaceTexture){
        mSurfaceTexture = surfaceTexture;
    }

    public SurfaceTexture getSurfaceTexture(){
        return mSurfaceTexture;
    }
}
