package com.lipeilong.jigsaw.camera;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;

import com.lipeilong.jigsaw.frame.BaseActivity;
import com.lipeilong.jigsaw.log.JDLog;
import com.lipeilong.jigsaw.util.DeviceUtil;
import com.lipeilong.jigsaw.util.WeakRefHandler;

import java.io.IOException;


/**
 * Created by lipeilong on 16/10/29.
 */

class CameraManagerGlobal {


    private static CameraManagerGlobal sInstance;
    private int mCameraType;
    private CameraConfig[] mCameraConfig;
    private Camera.AutoFocusCallback mAutoFocusCallback;
    private Camera.ErrorCallback mCameraErrorCallback;
    private HandlerThread mCameraHandlerThread;
    private WeakRefHandler mCameraHandler;
    private WeakRefHandler mUIHandler;
    private Camera.CameraInfo[] mCameraInfo;
    private CameraState mCameraState;
    private ICameraManager.CameraStateChangeListener mCameraStateChangeListener;
    private Camera mCamera;
    private Camera.PreviewCallback mPreviewCallback;
    private SurfaceTexture mSurfaceTexture;



    public static CameraManagerGlobal getInstance(){
        if(sInstance == null){
            synchronized (CameraManagerGlobal.class){
                if(sInstance == null){
                    sInstance   = new CameraManagerGlobal();
                }
            }
        }
        return sInstance;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture){
        mSurfaceTexture = surfaceTexture;
    }

    /**
     * 设置摄像头状态变化监听对象
     *
     * @param listener
     */
    public void setCameraStateChangeListener(ICameraManager.CameraStateChangeListener listener){
        mCameraStateChangeListener = listener;
    }

    /**
     *  打开摄像机
     */
    public void openCameraAsyn(){
        scheduleCamera(MSG_OPEN_CAMERA, null);
    }

    /**
     *  关闭摄像机
     */
    public void closeCameraAsyn(){
        scheduleCamera(MSG_CLOSE_CAMERA, null);

    }

    /**
     *  开始预览
     */
    public void startPreAsyn(){
        JDLog.log("send start pre ");
        scheduleCamera(MSG_START_PRE, null);

    }

    /**
     *  停止预览
     */
    public void stopPreAsyn(){
        scheduleCamera(MSG_STOP_PRE, null);

    }

    /**
     *  拍照
     * @param callback
     */
    public void takePhoto(ICameraManager.TakePictureCallback callback){
        scheduleCamera(MSG_TAKE_PHOTO, callback);

    }

    public static void release(){
        if(sInstance != null){
            sInstance.clear();
            sInstance   = null;
        }
    }

    public Camera.Size getPreviewSize(){
        if (mCameraConfig != null) {
            CameraConfig cameraConfig = mCameraConfig[mCameraType];
            if (cameraConfig != null) {
                return cameraConfig.preViewSize;
            }
        }
        return null;

    }

    private void clear(){
        if(mCameraHandlerThread != null && mCameraHandlerThread.isAlive()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mCameraHandlerThread.quitSafely();
            }else {
                mCameraHandlerThread.quit();
            }

        }

        mCameraCallback = null;
        mCameraHandler.removeCallbacksAndMessages(null);
//        mCameraHandler  = null;

        mUICallback = null;
        mUIHandler.removeCallbacksAndMessages(null);
//        mUIHandler  = null;
    }




    private void scheduleCamera(int what, Object obj){
        if(obj == null){
            mCameraHandler.sendEmptyMessage(what);
        }else {

            mCameraHandler.obtainMessage(what, obj).sendToTarget();
        }
    }

    private CameraManagerGlobal(){
        // 初始化默认打开后置相机
        mCameraType     = Camera.CameraInfo.CAMERA_FACING_BACK;

        mCameraConfig   = new CameraConfig[Camera.getNumberOfCameras()];

        // 初始化照相机线程
        mCameraHandlerThread = new HandlerThread("camera");
        mCameraHandlerThread.start();
        mCameraHandler = new WeakRefHandler(mCameraCallback, mCameraHandlerThread.getLooper());

        mUIHandler = new WeakRefHandler(mUICallback, Looper.getMainLooper());

        initCameraInfo();
        initListener();
    }

    private void initListener(){
        mAutoFocusCallback      = new Camera.AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera) {

                callOperaResult(ICameraManager.CameraStateChangeListener.CameraOperaResultType.FOCUS_SUCCESS);
            }
        };

        mCameraErrorCallback = new Camera.ErrorCallback() {

            @Override
            public void onError(int error, Camera camera) {
                // 关闭相机
                if (camera != null) {
                    camera.release();
                    camera = null;
                    mCameraState = CameraState.UNINIT;
                }

                // 回调上层
                callOperaResult(ICameraManager.CameraStateChangeListener.CameraOperaResultType.UNKNOW_ERROR);
            }
        };
    }


    /**
     * 在UI线程回调操作结果
     * @param cameraStateNotifyType
     */
    private void callOperaResult(final ICameraManager.CameraStateChangeListener.CameraOperaResultType cameraStateNotifyType){

        mUIHandler.obtainMessage(0, cameraStateNotifyType).sendToTarget();

    }

    /**
     * 获取本地照相机属性信息
     */
    private void initCameraInfo(){
        int numberOfCamera = Camera.getNumberOfCameras();

        if (numberOfCamera > 0) {
            mCameraInfo = new Camera.CameraInfo[numberOfCamera];
            for (int i = 0; i < numberOfCamera; i++) {
                mCameraInfo[i] = new Camera.CameraInfo();
                //这里适配CameraInfo.CAMERA_FACING_BACK定义变化的情况
                try {
                    Camera.getCameraInfo(i - Camera.CameraInfo.CAMERA_FACING_BACK, mCameraInfo[i]);
                } catch (Exception e) {
                    JDLog.log("open camera failure");
                }
            }
        }

    }

    /**
     * 打开相机
     * @return
     */
    @SuppressLint("NewApi")
    private boolean openCamera(){

        long beginTime = System.currentTimeMillis();

        boolean result = false;

        if (mCamera == null) {

            // 重新开启摄像头
            try {
                mCamera = Camera.open(mCameraType);
                mCamera.setErrorCallback(mCameraErrorCallback);

                if (mCamera != null) {
                    setCameraDisplayOrientation(mCamera, mCameraInfo[mCameraType]);        // 预览角度旋转

                    Camera.Parameters parameters = mCamera.getParameters();

                    initCameraConfig(parameters);

                    if (parameters.isZoomSupported()) {
                        parameters.setZoom(0);                          // 缩放比例
                    }
//            parameters.set("jpeg-quality", 85);               // 照片质量
                    parameters.setPictureFormat(ImageFormat.JPEG);      // 输出文件格式
//            parameters.setPreviewFrameRate(5);                // 预览帧数，有些手机不支持
                    setProperPicSize(parameters);                       // 图片大小
                    setProperPreviewSize(parameters);                   // 预览图片
//                    setDefaultFlashMode(parameters);                    // 设置默认闪光灯模式
//                    setDefaultFocusMode(parameters);                     // 设置默认对焦模式

//                final int MIN_SUPPORT_RECORD_HINT = 14;
//                // 小米的前置照相机不支持这个参数
//                if (android.os.Build.VERSION.SDK_INT >= MIN_SUPPORT_RECORD_HINT && mCameraConfig.cameraType == CameraInfo.CAMERA_FACING_BACK) {
//                    parameters.setRecordingHint(true);  // 让照相机跟摄像之间能够更流畅的切换
//                }
//
//                // 小米3不支持这个设置，会导致花屏
//                final int MIN_SUPPORT_VIDEO_STABILIZATION = 15;
//                if (android.os.Build.VERSION.SDK_INT >= MIN_SUPPORT_VIDEO_STABILIZATION && parameters.isVideoStabilizationSupported()) {
//                    parameters.setVideoStabilization(true);     // 防抖设置
//                }
                    mCamera.setPreviewCallback(mPreviewCallback);

                    mCamera.setParameters(parameters);
                    mCameraState = CameraState.INITED;
                    result = true;
                    JDLog.log("open camera success");
                } else {
                    JDLog.log("open camera failure");
                }
            } catch (RuntimeException e) {
                JDLog.log("打开相机失败");
                e.printStackTrace();
                mCamera = null;
            }
        }

        final boolean openResult = result;
        if (openResult) {
            callOperaResult(ICameraManager.CameraStateChangeListener.CameraOperaResultType.OPEN_SUCCESS);
        } else {
            callOperaResult(ICameraManager.CameraStateChangeListener.CameraOperaResultType.OPEN_FAIL);
        }

        JDLog.log("initCamera take:" + (System.currentTimeMillis() - beginTime));

        return result;
    }

    /**
     * 关闭相机
     */
    private synchronized boolean closeCamera(){
        boolean result = false;

        if (mCamera != null && mCameraState != CameraState.UNINIT) {
            // 释放之前，清除回调
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            mCameraState = CameraState.UNINIT;

            result = true;
        }

        if (result) {
            callOperaResult(ICameraManager.CameraStateChangeListener.CameraOperaResultType.CLOSE_SUCCESS);
        } else {
            callOperaResult(ICameraManager.CameraStateChangeListener.CameraOperaResultType.CLOSE_FAIL);
        }

        return result;
    }

    /**
     * 启动摄像头预览
     * @return
     */
    private boolean startPreView(){
        JDLog.log("start pre view");
        long beginTime = System.currentTimeMillis();
        boolean result = false;

        if (mCameraState == CameraState.INITED) {
            mCameraState = CameraState.PREPARED;
        }

        JDLog.log("preared");
        if (mCamera != null && mCameraState == CameraState.PREPARED) {
            try {
                // 开始预览的时候调整下预览界面大小
                setProperPreviewSize(mCamera.getParameters());

                mCamera.setPreviewTexture(mSurfaceTexture);      // 设置预览View
                mCamera.startPreview();

                mCameraState = CameraState.PREVIEW;
                result = true;
            } catch (IOException e) {
                JDLog.log("startPreView IOException");
                result = false;
            } catch (Exception e) {
                JDLog.log("startPreView OtherException");
                result = false;
            }
        }

        if (result) {
            callOperaResult(ICameraManager.CameraStateChangeListener.CameraOperaResultType.START_PREVIEW_SUCCESS);
        } else {
            callOperaResult(ICameraManager.CameraStateChangeListener.CameraOperaResultType.START_PREVIEW_FIAL);
        }

        JDLog.log("startPreView take:" + (System.currentTimeMillis() - beginTime));
        JDLog.log("preview : "+result);
        return result;
    }



    /**
     * 关闭摄像头预览
     * @return
     */
    private void stopPreView(){
        JDLog.log("stopPreView");

        if (mCamera != null && mCameraState == CameraState.PREVIEW) {
            mCamera.stopPreview();
            mCameraState = CameraState.INITED;
        }
    }


    /**
     * 进行拍照
     *
     * @return 图片的路径
     */
    private void takePicture(final ICameraManager.TakePictureCallback callback){
        if(mCamera != null && mCameraState  == CameraState.PREVIEW){
            Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

                @Override
                public void onPictureTaken(byte[] data, Camera camera) {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                    if (bitmap != null) {

                        Matrix matrix = new Matrix();
                        matrix.reset();
                        matrix.setRotate(getCameraPicOrientation(mCameraInfo[mCameraType]));        // 设置图片旋转角度

                        // 前置摄像头进行左右反转
                        final boolean willDoLeftToRight = true;
                        if (willDoLeftToRight && mCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            matrix.postScale(-1, 1);
                        }

                        final Bitmap bitmapResult = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        //final String path = PathConfig.getCameraOutputPicPath();

                        mUIHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                if (bitmapResult != null) {
                                    callback.onSuccess(bitmapResult);
                                } else {
                                    callback.onFail(0); // TODO:添加失败原因
                                }
                            }
                        });
                    }

                    // 添加容错代码，这里可能会跑出异常
                    boolean result = false;

                    try {
                        mCamera.stopPreview();
                        mCamera.startPreview();
                        mCameraState = CameraState.PREVIEW;
                        result = true;
                    } catch (Exception e) {
                        // TODO: handle exception
                        JDLog.log("startPreView OtherException");
                        result = false;
                    }

                    if (result) {
                        callOperaResult(ICameraManager.CameraStateChangeListener.CameraOperaResultType.START_PREVIEW_SUCCESS);
                    } else {
                        callOperaResult(ICameraManager.CameraStateChangeListener.CameraOperaResultType.START_PREVIEW_FIAL);
                    }
                }
            };

            try {
                mCamera.takePicture(new Camera.ShutterCallback() {

                    @Override
                    public void onShutter() {

                    }
                }, null, pictureCallback);

            } catch (Exception e) {
                e.printStackTrace();
                JDLog.log("takePicture error" + e.getMessage());

                mUIHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        callback.onFail(0); // TODO:添加失败原因
                    }
                });
            }
        } else {
            mUIHandler.post(new Runnable() {

                @Override
                public void run() {
                    callback.onFail(0); // TODO:添加失败原因
                }
            });
        }
    }

    /**
     * 初始化照相机配置相关
     * @param parameters
     */
    private void initCameraConfig(Camera.Parameters parameters){
        // 如果为空，则使用默认方案初始化
        CameraConfig cameraConfig = mCameraConfig[mCameraType];
        if (cameraConfig == null) {
            cameraConfig   = CameraConfig.createDefaultCameraConfig(mCameraType);
            mCameraConfig[mCameraType] = cameraConfig;
        }

        // PS：这里不再使用缓存，每次重新启动都需要判断当前模式，选取合适的size
        cameraConfig.preViewSize    = CameraUtil.getProperPreviewSize(parameters, cameraConfig.preViewWidth, cameraConfig.preViewHeight);
        cameraConfig.picSize        = CameraUtil.getProperPictureSize(parameters, cameraConfig.picWidth, cameraConfig.picHeight);
        cameraConfig.videoSize      = CameraUtil.getProperVideoSize(parameters, cameraConfig.videoWidth, cameraConfig.videoHeight);
    }

    private void setPreviewCallback(Camera.PreviewCallback callback) {
        mPreviewCallback = callback;

        if (mCamera != null) {
            mCamera.setPreviewCallback(mPreviewCallback);
        }
    }

    /**
     * 设置合适的照相尺寸
     * @param parameters
     */
    private void setProperPicSize(Camera.Parameters parameters) {

        parameters.setPictureSize(mCameraConfig[mCameraType].picSize.width, mCameraConfig[mCameraType].picSize.height);
    }


    /**
     * 设置合适的预览尺寸(使用初始化的配置)
     * @param parameters
     */
    private void setProperPreviewSize(Camera.Parameters parameters) {

        parameters.setPreviewSize(mCameraConfig[mCameraType].preViewSize.width, mCameraConfig[mCameraType].preViewSize.height);

    }

    /**
     * 设置预览的角度
     * @param camera
     */
    private void setCameraDisplayOrientation(Camera camera, Camera.CameraInfo info){
        camera.setDisplayOrientation(getOrientation(info));
    }

    /**
     * 获取相机照片旋转角度
     */
    private int getCameraPicOrientation(Camera.CameraInfo info){

        int backOrientation = info.orientation;

        // 对于魅族MX手机进行特殊处理，它的前置摄像头需要倒置
        if (DeviceUtil.getModel().equalsIgnoreCase("M031") && info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            backOrientation = (backOrientation + 180) % 360;
            JDLog.log("品牌：" + DeviceUtil.getBrand() + "|" + DeviceUtil.getModel() + "|" + backOrientation);
        }

        return backOrientation;
    }

    /**
     * 获取照相机旋转角度
     * @param info
     * @return
     */
    private int getOrientation(Camera.CameraInfo info){
        int rotation = BaseActivity.getTopActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }
    
    private static final int MSG_TAKE_PHOTO     =   0;
    private static final int MSG_OPEN_CAMERA    =   1;
    private static final int MSG_CLOSE_CAMERA   =   2;
    private static final int MSG_START_PRE      =   3;
    private static final int MSG_STOP_PRE       =   4;
    

    WeakRefHandler.Callback mCameraCallback = new WeakRefHandler.Callback(){

        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case MSG_TAKE_PHOTO:
                    takePicture((ICameraManager.TakePictureCallback) message.obj);
                    break;
                case MSG_OPEN_CAMERA:
                    openCamera();
                    break;
                case MSG_CLOSE_CAMERA:
                    closeCamera();
                    break;
                case MSG_START_PRE:
                    JDLog.log("receiver msg start pre");
                    startPreView();
                    break;
                case MSG_STOP_PRE:
                    stopPreView();
                    break;
            }
            return true;
        }
    };

    WeakRefHandler.Callback mUICallback     = new WeakRefHandler.Callback(){

        @Override
        public boolean handleMessage(Message message) {
            if (mCameraStateChangeListener != null && message.obj != null) {

                mCameraStateChangeListener.onOperaState((ICameraManager.CameraStateChangeListener.CameraOperaResultType) message.obj);

            }
            return true;
        }
    };

    /**
     * 相机状态
     * @author ls
     *
     */
    public enum CameraState{
        /** 未初始化 */
        UNINIT,
        /** 初始化 */
        INITED,
        /** 相机预览或录像准备工作做好了 */
        PREPARED,
        /** 预览中 */
        PREVIEW,
        /** 录像中 */
        RECORDING
    }


}
