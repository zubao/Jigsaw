package com.lipeilong.jigsaw.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lipeilong on 16/10/30.
 */

public class NineSquareLayout extends ViewGroup implements SurfaceTexture.OnFrameAvailableListener{

    private int mPreviewWidth;
    private int mPreviewHeight;

    public NineSquareLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public NineSquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public NineSquareLayout(Context context) {
        super(context);
        init();
    }

    private void init(){
        int count   = 9;
        for(int i=0; i< count; i++){
            NineSquareSurfaceView surfaceView   = new NineSquareSurfaceView(getContext());
            if(i == 0){
                surfaceView.setRenderer(new NineSquareSurfaceRenderer(getContext(), true));
            }else{
                surfaceView.setRenderer(new NineSquareSurfaceRenderer(getContext(), false));
            }
            surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            addView(surfaceView);
        }
    }

    /**
     * @param width     分辨率宽度
     * @param height    分辨率高度
     */
    public void setCameraPreviewSize(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }

        for(int i=0; i< getChildCount(); i++){
            NineSquareSurfaceView surfaceView   = (NineSquareSurfaceView)getChildAt(i);
            surfaceView.getRenderer().setCameraPreviewSize(width, height);

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width   = MeasureSpec.getSize(widthMeasureSpec);
        int height  = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, width);

        mPreviewHeight  = mPreviewWidth = 1;
        int childWidth  = width / 3;
        int childHeight = childWidth * mPreviewHeight / mPreviewWidth;
        int count   = getChildCount();

        for(int i=0; i< count; i++){
            View child  = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));

        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        layoutChildren(i, i1, i2, i3);
    }

    private void layoutChildren(int l, int t, int r, int b){
        int count   = getChildCount();
        if(count <=0 ){
            return;
        }
        int width   = getChildAt(0).getMeasuredWidth();
        int height  = getChildAt(0).getMeasuredHeight();

        for(int i=0; i< count; i++){
            int h   = i % 3;
            int v   = i / 3;

            View child  = getChildAt(i);
            int left    = h * width;
            int top     = v * width;
            int right   = left + width;
            int bottom  = top + height;
            child.layout(left, top, right, bottom);
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        int count   = getChildCount();
        for(int i=0; i< count; i++){
            GLSurfaceView glSurfaceView = (GLSurfaceView) getChildAt(i);
            glSurfaceView.requestRender();
        }

    }
}
