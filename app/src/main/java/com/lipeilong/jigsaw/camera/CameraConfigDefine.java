package com.lipeilong.jigsaw.camera;


/**
 * 定义我们定制的摄像头的默认参数
 * @author ls
 *
 */
public class CameraConfigDefine {
    /**
     * 发送漂流瓶，期望的拍照预览图片的宽高（手机不满足的话，会自动适配到最接近的分辨率）
     */
    public static final int PREVIEW_SIZE_WIDTH  = 1080;
    public static final int PREVIEW_SIZE_HEIGHT = 1920;
    
    /**
     * 针对拼图玩法，期望的拍照预览图片的宽高（手机不满足的话，会自动适配到最接近的分辨率）
     */
    public static final int PREVIEW_SIZE_WIDTH_JIGSAW  = 480;
    public static final int PREVIEW_SIZE_HEIGHT_JIGSAW = 480;
    
    /**
     * 发送漂流瓶，期望的拍照保存图片的宽高（手机不满足的话，会自动适配到最接近的分辨率）
     */
    public static final int TAKE_PIC_SIZE_WIDTH  = 1080;
    public static final int TAKE_PIC_SIZE_HEIGHT = 1920;
    
    /**
     * 发送漂流瓶和聊天的拍照，期望的发送图片的宽高
     * 
     * PS:3.5版本为了增加图片质量，由720修改为1080，图片大小增加一倍（200K左右）
     */
    public static final int SEND_PIC_SIZE_WIDTH  = 1080;
    public static final int SEND_PIC_SIZE_HEIGHT = 1080; // 1280; 
    
    /**
     * 发送漂流瓶，期望的录像视频宽高
     */
    public static final int VIDEO_SIZE_WIDTH  = 480;
    public static final int VIDEO_SIZE_HEIGHT = 864;
}
