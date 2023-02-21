package com.test.ffmpeg;

//import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import org.bytedeco.opencv.opencv_cudacodec.VideoReader;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;

/**
 * 实时视频播放功能
 * https://github.com/ZLMediaKit/ZLMediaKit
 * 前端播放插件easywasmplayer
 */
public class VideoCaptureTest {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture videoCapture = new VideoCapture();
        videoCapture.open("rtsp://admin:1q2w3e4r@192.168.5.28/h264/ch1/main/av_stream");
        if(!videoCapture.isOpened()){
            System.out.println("open failed");
            return;
        }
    }
}
