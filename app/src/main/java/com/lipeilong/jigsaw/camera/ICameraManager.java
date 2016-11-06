package com.lipeilong.jigsaw.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;

/**
 * Created by lipeilong on 16/10/29.
 */

public interface ICameraManager {

    /**
     *  set preview surfacetexture
     *
     * @param surfaceTexture
     */
    void setSurfaceTexture(SurfaceTexture surfaceTexture);

    void open(Activity activity);

    void close();

    void release();

    /**
     * 拍照的回调接口
     * @author ls
     *
     */
    public interface TakePictureCallback{

        /**
         * 拍照成功
         * @param bm 返回图片
         */
        public void onSuccess(Bitmap bm);

        /**
         * 拍照失败
         * @param reason 失败原因
         */
        public void onFail(int reason);
    }



    /**
     * 状态监听接口
     * @author ls
     *
     */
    public interface CameraStateChangeListener{

        /**
         * 照相机操作结果回调
         * @author ls
         *
         */
        public enum CameraOperaResultType{
            /** 未知 */
            UNKNOW,

            /** 出现错误 */
            UNKNOW_ERROR,

            /** 聚焦成功 */
            FOCUS_SUCCESS,

            /** 聚焦失败 */
            FOCUS_FAIL,

            /** 打开相机成功 */
            OPEN_SUCCESS,

            /** 打开相机失败 */
            OPEN_FAIL,

            /** 关闭相机成功 */
            CLOSE_SUCCESS,

            /** 关闭相机失败 */
            CLOSE_FAIL,

            /** 启动预览成功 */
            START_PREVIEW_SUCCESS,

            /** 启动预览失败 */
            START_PREVIEW_FIAL,

            /** 启动录制视频成功 */
            START_RECORD_SUCCESS,

            /** 启动录制视频失败 */
            START_RECORD_FAIL,

            /** 设置闪光灯模式成功 */
            SWITCH_FLASH_MODE_SUCCESS,

            /** 设置闪光灯模式失败 */
            SWITCH_FLASH_MODE_FAIL,
        }

        /**
         * 状态变化
         *
         * @param type 变化类型
         * TODO：变化类型暂未定义，后面如果需要细分再进行定义
         */
        public void onOperaState(CameraOperaResultType type);

    }

}
