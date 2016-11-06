package com.lipeilong.jigsaw;

import android.os.Bundle;

import com.lipeilong.jigsaw.frame.BaseActivity;
import com.lipeilong.jigsaw.widget.CameraEvent;
import com.lipeilong.jigsaw.widget.NineSquareLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements IJigsawView {

    private JigsawPresenter mJigsawPresenter;
    private NineSquareLayout mNineSquareLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJigsawPresenter = new JigsawPresenter(this);


        mNineSquareLayout   = new NineSquareLayout(getApplicationContext());
        setContentView(mNineSquareLayout);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mJigsawPresenter.closeCamera();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCameraEvent(CameraEvent cameraEvent) {
        mJigsawPresenter.setupCamera(cameraEvent.getSurfaceTexture());
        cameraEvent.getSurfaceTexture().setOnFrameAvailableListener(mNineSquareLayout);
    }

    @Override
    public void setPreviewSize(int width, int height) {
        int temp = width ^ height;
        width = temp ^ width;
        height = temp ^ height;

        mNineSquareLayout.setCameraPreviewSize(width, height);
    }

}
