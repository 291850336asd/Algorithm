package com.test.ffmpeg;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.opencv.opencv_core.GpuMat;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.bytedeco.opencv.opencv_videoio.VideoWriter;
import org.opencv.core.Core;
import org.opencv.videoio.Videoio;

import java.io.IOException;

import static org.bytedeco.ffmpeg.global.avcodec.av_packet_unref;
import static org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FRAME_HEIGHT;
import static org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FRAME_WIDTH;
import static org.bytedeco.opencv.opencv_videoio.VideoWriter.fourcc;


/**
 * rtsp转rtmp（转封装方式）.
 */
public class ConvertVideoPakcet2 {
    FFmpegFrameGrabber grabber = null;
    FFmpegFrameRecorder record = null;
    int width = -1, height = -1;
    int speed = 2;
    protected int codecid;
    protected double framerate= 20;// 帧率
    protected int bitrate;// 比特率

    /**
     * 转封装.
     */
    public static void go() throws IOException {
        System.out.println(System.getProperty("java.library.path"));
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        long err_index = 0;//采集或推流导致的错误次数test
        //连续五次没有采集到帧则认为视频采集结束，程序错误次数超过1次即中断程序
        int pktindex = 0;
        long firstpkttime = System.currentTimeMillis();
        long dts = 0;
        long pts = 0;
        VideoCapture vc = new VideoCapture("rtsp://admin:1q2w3e4r@192.168.5.18:554/h264/ch1/main/av_stream");
        vc.set(Videoio.CAP_PROP_FRAME_WIDTH,640);
        vc.set(Videoio.CAP_PROP_FRAME_HEIGHT,480);
        while (!vc.isOpened()){
//            boolean isOpen = vc.open("rtsp://admin:1q2w3e4r@192.168.5.18:554/h264/ch1/main/av_stream");
//            boolean isOpen = vc.open("rtsp://admin:1q2w3e4r@192.168.5.20:554/Streaming/tracks/601?starttime=20220620t110830z");
            try {
                System.out.println("Sleeping..");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        VideoWriter vw = new VideoWriter();
        boolean t = vw.open("rtmp://127.0.0.1/live/hw", VideoWriter.fourcc((byte) 'M',(byte) 'P',(byte) '4',(byte) '2'),
                vc.get(Videoio.CAP_PROP_FPS),
                new Size((int) vc.get(Videoio.CAP_PROP_FRAME_WIDTH),
                        (int) vc.get(Videoio.CAP_PROP_FRAME_HEIGHT)),
                true);
        GpuMat mat = new GpuMat();
        while (vc.read(mat)){
            vw.write(mat);
        }
        vc.release();
        vw.release();


    }

    public static void main(String[] args) throws Exception, IOException {

        //运行，设置视频源和推流地址
//        new ConvertVideoPakcet().from("rtsp://admin:1q2w3e4r@192.168.5.18:554/h264/ch1/main/av_stream")
        go();
    }
}