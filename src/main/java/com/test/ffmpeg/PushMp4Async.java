package com.test.ffmpeg;

import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


public class PushMp4Async {
    private static String RTSP_PATH = "rtsp://admin:1q2w3e4r@192.168.5.27/h264/ch1/main/av_stream";
    private static BlockingQueue<Frame> blockingQueue = new LinkedBlockingQueue<>(20);
    /**
     * SRS的推流地址
     */
    private static String SRS_PUSH_ADDRESS = "rtmp://192.168.5.46/live/hw1";

    private static AtomicLong atomicLong = new AtomicLong();
    /**
     * 读取rtsp，推送到SRS服务器
     * @param sourceFilePath rtsp路径
     * @param PUSH_ADDRESS 推流地址
     * @throws Exception
     */
    public static void grabAndPush(String sourceFilePath) throws Exception {
        // ffmepg日志级别
        System.out.println("grabAndPush");
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
        FFmpegLogCallback.set();
        // 实例化帧抓取器对象，将文件路径传入
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(sourceFilePath);
//        grabber.setVideoOption("threads", "2");
//        grabber.setAudioCodecName("aac");
        long startTime = System.currentTimeMillis();
        // 初始化帧抓取器，例如数据结构（时间戳、编码器上下文、帧对象等），
        // 如果入参等于true，还会调用avformat_find_stream_info方法获取流的信息，放入AVFormatContext类型的成员变量oc中

        grabber.start();

        openRecorder(grabber.getImageWidth(), grabber.getImageHeight());
        int errorCount = 0;
//        log.info("帧录制初始化完成，耗时[{}]毫秒", System.currentTimeMillis()-startTime);

        Frame frame;
        Base64.Encoder encoder = Base64.getEncoder();
        Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
        while (true ) {
            try {
                frame=grabber.grabImage();
                if(frame == null || null == frame.image){
                    errorCount ++;
                    if(errorCount > 200){
                        System.out.println(atomicLong.incrementAndGet());
                        break;
                    }
                    continue;
                }
                errorCount=0;
//            videoTS = 1000 * (System.currentTimeMillis() - startTime);
//
//            // 时间戳
//            recorder.setTimestamp(videoTS);

                // 有图像，就把视频帧加一
//            System.out.println(frame.keyFrame);
                // 取出的每一帧，都推送到SRS
//            if(i % 5== 0){
//                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                BufferedImage bi = java2DFrameConverter.getBufferedImage(frame);
//                ImageIO.write(bi, "jpg", outputStream);
//                byte[] poll = outputStream.toByteArray();
//                blockingQueue.offer(poll,10, TimeUnit.MILLISECONDS);
//////                String imageBase64 = "data:image/jpg;base64," + encoder.encodeToString(outputStream.toByteArray());
//                outputStream.flush();
//                java2DFrameConverter.close();
//                bi.getGraphics().dispose();
//                frame.close();
//                outputStream.close();
                if(blockingQueue.size() >18){
                    blockingQueue.clear();
                }
                blockingQueue.offer(frame);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        grabber.close();

    }
    static AtomicLong ex = new AtomicLong(0);

    private static void openRecorder(int imageWidth, int imageHeight) {
        new Thread(()->{
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(SRS_PUSH_ADDRESS,imageWidth, imageHeight,
                    0);

            // 设置编码格式
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);

            // 设置封装格式
            recorder.setFormat("flv");
            // 一秒内的帧数
            recorder.setFrameRate(20);
                // 两个关键帧之间的帧数
            recorder.setGopSize(20);
            try {
                recorder.start();
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
            Frame poll = null;
            while (true){
                try {
                    poll = blockingQueue.poll(10, TimeUnit.MILLISECONDS);
                    if(poll != null){
                        recorder.record(poll);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
//                    File outPut = new File("D:\\img\\" + ex.incrementAndGet() + ".jpeg");
//                    try {
//                        BufferedImage im = FrameToBufferedImage(poll);
//                        ImageIO.write(im, "jpg", outPut);
//                        im.getGraphics().dispose();
//                    } catch (IOException ioException) {
//                        ioException.printStackTrace();
//                    }
                }
            }
        }).start();
    }

    public static BufferedImage FrameToBufferedImage(Frame frame) {
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        return bufferedImage;
    }

    public static void main(String[] args) throws Exception {
        PushMp4Async.grabAndPush(RTSP_PATH);
    }
}