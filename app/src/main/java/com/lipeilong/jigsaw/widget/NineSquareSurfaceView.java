package com.lipeilong.jigsaw.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by lipeilong on 16/10/30.
 */

public class NineSquareSurfaceView extends GLSurfaceView {
    private NineSquareSurfaceRenderer mRenderer;

    public NineSquareSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public NineSquareSurfaceView(Context context) {
        super(context);

        init(context);
    }

    private void init(Context context) {

        setEGLContextClientVersion(2);

    }

    @Override
    public void setRenderer(Renderer renderer) {
        super.setRenderer(renderer);
        mRenderer   = (NineSquareSurfaceRenderer) renderer;
    }

    public NineSquareSurfaceRenderer getRenderer(){
        return mRenderer;
    }


}
