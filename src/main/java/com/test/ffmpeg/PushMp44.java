package com.test.ffmpeg;

import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;


public class PushMp44 {

    //    private static final String RTSP_PATH = "rtsp://admin:1q2w3e4r@192.168.5.20:554/Streaming/tracks/601?subtype=0&starttime=20220626t000000z";
    private static final String RTSP_PATH = "rtsp://admin:1q2w3e4r@192.168.5.20:554/Streaming/tracks/601?starttime=20220629t224900z";
//    private static final String RTSP_PATH = "rtsp://admin:1q2w3e4r@192.168.5.29/h264/ch1/main/av_stream";
//    private static final String RTSP_PATH = "rtsp://192.168.5.174:554/user=admin&password=123456&channel=1&stream=0.sdp";
//    private static final String RTSP_PATH = "rtsp://admin:1q2w3e4r@192.168.5.18/h264/ch1/main/av_stream";
//    private static final String RTSP_PATH = "rtsp://admin:1q2w3e4r@192.168.5.18:554/h264/ch1/main/av_stream";

    /**
     * SRS的推流地址
     */
    private static final String SRS_PUSH_ADDRESS = "rtmp://192.168.5.46/live/hw";


    /**
     * 读取rtsp，推送到SRS服务器
     * @param sourceFilePath rtsp路径
     * @param PUSH_ADDRESS 推流地址
     * @throws Exception
     */
    public static void grabAndPush(String sourceFilePath, String PUSH_ADDRESS) throws Exception {
        // ffmepg日志级别
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(sourceFilePath);
        final long startTime = System.currentTimeMillis();
        // 实例化FFmpegFrameRecorder，将SRS的推送地址传入
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(PUSH_ADDRESS,
                1280,
                720,
                1);

        // 设置编码格式
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);

        // 设置封装格式
        recorder.setFormat("flv");
        // 一秒内的帧数
        recorder.setFrameRate(20);

        // 两个关键帧之间的帧数
        recorder.setGopSize(50);

        // 设置音频通道数，与视频源的通道数相等
        recorder.setAudioChannels(0);
//        recorder.setVideoOption("threads", "2"); //解码线程数

//        log.info("开始初始化帧抓取器");

        // 初始化帧录制器，例如数据结构（音频流、视频流指针，编码器），
        // 调用av_guess_format方法，确定视频输出时的封装方式，
        // 媒体上下文对象的内存分配，
        // 编码器的各项参数设置


//        log.info("帧录制初始化完成，耗时[{}]毫秒", System.currentTimeMillis()-startTime);



//        log.info("开始推流");



        int videoFrameNum = 0;
        int audioFrameNum = 0;
        int dataFrameNum = 0;

        // 假设一秒钟15帧，那么两帧间隔就是(1000/15)毫秒
        int interVal = 1000/20;
        // 发送完一帧后sleep的时间，不能完全等于(1000/frameRate)，不然会卡顿，
        // 要更小一些，这里取八分之一
        interVal/=8;

        // 持续从视频源取帧
        new Thread(()->{

            Frame frame;
            try {
                recorder.start();
               while (true){
                   try{
                       while (null!=(frame=grabber.grabFrame()) ) {
                           long videoTS = 1000 * (System.currentTimeMillis() - startTime);

                           // 时间戳
                           recorder.setTimestamp(videoTS);

                           // 有图像，就把视频帧加一
//            if (null!=frame.image) {
//                videoFrameNum++;
//            }
//
//
//            // 有声音，就把音频帧加一
//            if (null!=frame.samples) {
//                audioFrameNum++;
//            }
//
//            // 有数据，就把数据帧加一
//            if (null!=frame.data) {
//                dataFrameNum++;
//            }
                           // 取出的每一帧，都推送到SRS
                           recorder.record(frame);
                           frame = null;
                           // 停顿一下再推送
//            Thread.sleep(interVal);
                       }
                   }catch (Exception e){

                   }
               }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();

        System.out.println("grabAndPush");
        avutil.av_log_set_level(avutil.AV_LOG_DEBUG);
        FFmpegLogCallback.set();
        // 实例化帧抓取器对象，将文件路径传入

        grabber.setAudioCodecName("aac");
//        grabber.setOption("rtsp_transport", "tcp");
//        grabber.setOption("threads", "1");
//        grabber.setOption("probesize","15000000");
//        grabber.setOption("analyzeduration", "15000000");
//        grabber.setOption("rw_timeout", "15000000");
//        grabber.setOption("rtmp_buffer", "1000");
//        grabber.setOption("twoPart","0");

        // 初始化帧抓取器，例如数据结构（时间戳、编码器上下文、帧对象等），
        // 如果入参等于true，还会调用avformat_find_stream_info方法获取流的信息，放入AVFormatContext类型的成员变量oc中
        // grabber.start方法中，初始化的解码器信息存在放在grabber的成员变量oc中
        AVFormatContext avFormatContext = grabber.getFormatContext();

        // 文件内有几个媒体流（一般是视频流+音频流）
//        int streamNum = avFormatContext.nb_streams();
//
//        // 没有媒体流就不用继续了
//        if (streamNum<1) {
//            return;
//        }

        // 取得视频的帧率
//        int frameRate = (int)grabber.getVideoFrameRate();

//        log.info("视频帧率[{}]，视频时长[{}]秒，媒体流数量[{}]",
//                frameRate,
//                avFormatContext.duration()/1000000,
//                avFormatContext.nb_streams());
        // 视频宽度
//        int frameWidth = grabber.getImageWidth();
//        // 视频高度
//        int frameHeight = grabber.getImageHeight();
//        // 音频通道数量
//        int audioChannels = grabber.getAudioChannels();

//        log.info("视频宽度[{}]，视频高度[{}]，音频通道数[{}]",
//                frameWidth,
//                frameHeight,
//                audioChannels);


        grabber.start();
//        log.info("推送完成，视频帧[{}]，音频帧[{}]，数据帧[{}]，耗时[{}]秒",
//                videoFrameNum,
//                audioFrameNum,
//                dataFrameNum,
//                (System.currentTimeMillis()-startTime)/1000);

    }

    /**
     * 读取rtsp，推送到SRS服务器
     * @param sourceFilePath rtsp路径
     * @param PUSH_ADDRESS 推流地址
     * @throws Exception
     */
    private static void grabAndPush2(String sourceFilePath, String PUSH_ADDRESS) throws Exception {
        // ffmepg日志级别
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
        FFmpegLogCallback.set();
        // 实例化帧抓取器对象，将文件路径传入
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(RTSP_PATH);
        grabber.setAudioCodecName("aac");
        long startTime = System.currentTimeMillis();
        // 初始化帧抓取器，例如数据结构（时间戳、编码器上下文、帧对象等），
        // 如果入参等于true，还会调用avformat_find_stream_info方法获取流的信息，放入AVFormatContext类型的成员变量oc中
        grabber.start();
        // grabber.start方法中，初始化的解码器信息存在放在grabber的成员变量oc中
        AVFormatContext avFormatContext = grabber.getFormatContext();

        // 文件内有几个媒体流（一般是视频流+音频流）
        int streamNum = avFormatContext.nb_streams();

        // 没有媒体流就不用继续了
        if (streamNum<1) {
            return;
        }

        // 取得视频的帧率
        int frameRate = (int)grabber.getVideoFrameRate();

//        log.info("视频帧率[{}]，视频时长[{}]秒，媒体流数量[{}]",
//                frameRate,
//                avFormatContext.duration()/1000000,
//                avFormatContext.nb_streams());

        // 遍历每一个流，检查其类型
//        for (int i=0; i< streamNum; i++) {
//            AVStream avStream = avFormatContext.streams(i);
//            AVCodecParameters avCodecParameters = avStream.codecpar();
////            log.info("流的索引[{}]，编码器类型[{}]，编码器ID[{}]", i, avCodecParameters.codec_type(), avCodecParameters.codec_id());
//        }

        // 视频宽度
        int frameWidth = grabber.getImageWidth();
        // 视频高度
        int frameHeight = grabber.getImageHeight();
        // 音频通道数量
        int audioChannels = grabber.getAudioChannels();

//        log.info("视频宽度[{}]，视频高度[{}]，音频通道数[{}]",
//                frameWidth,
//                frameHeight,
//                audioChannels);

        // 实例化FFmpegFrameRecorder，将SRS的推送地址传入
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(SRS_PUSH_ADDRESS,
                frameWidth,
                frameHeight,
                audioChannels);

        // 设置编码格式
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);

        // 设置封装格式
        recorder.setFormat("flv");

        // 一秒内的帧数
        recorder.setFrameRate(frameRate);

        // 两个关键帧之间的帧数
        recorder.setGopSize(10);

        // 设置音频通道数，与视频源的通道数相等
        recorder.setAudioChannels(grabber.getAudioChannels());

        startTime = System.currentTimeMillis();
//        log.info("开始初始化帧抓取器");

        // 初始化帧录制器，例如数据结构（音频流、视频流指针，编码器），
        // 调用av_guess_format方法，确定视频输出时的封装方式，
        // 媒体上下文对象的内存分配，
        // 编码器的各项参数设置
        recorder.start();

//        log.info("帧录制初始化完成，耗时[{}]毫秒", System.currentTimeMillis()-startTime);

        Frame frame;

        startTime = System.currentTimeMillis();

//        log.info("开始推流");

        long videoTS = 0;

        int videoFrameNum = 0;
        int audioFrameNum = 0;
        int dataFrameNum = 0;

        // 假设一秒钟15帧，那么两帧间隔就是(1000/15)毫秒
        int interVal = 1000/frameRate;
        // 发送完一帧后sleep的时间，不能完全等于(1000/frameRate)，不然会卡顿，
        // 要更小一些，这里取八分之一
        interVal/=8;

        // 持续从视频源取帧
        while (null!=(frame=grabber.grab())) {
//            videoTS = 1000 * (System.currentTimeMillis() - startTime);
//
//            // 时间戳
//            recorder.setTimestamp(videoTS);

            // 有图像，就把视频帧加一
            if (null!=frame.image) {
                videoFrameNum++;
            }


            // 有声音，就把音频帧加一
            if (null!=frame.samples) {
                audioFrameNum++;
            }

            // 有数据，就把数据帧加一
            if (null!=frame.data) {
                dataFrameNum++;
            }
            // 取出的每一帧，都推送到SRS
            recorder.record(frame);
            frame = null;
            // 停顿一下再推送
            Thread.sleep(interVal);
        }

//        log.info("推送完成，视频帧[{}]，音频帧[{}]，数据帧[{}]，耗时[{}]秒",
//                videoFrameNum,
//                audioFrameNum,
//                dataFrameNum,
//                (System.currentTimeMillis()-startTime)/1000);

        // 关闭帧录制器
        recorder.close();
        // 关闭帧抓取器
        grabber.close();
    }

    public static void main(String[] args) throws Exception {
        PushMp44.grabAndPush(RTSP_PATH, SRS_PUSH_ADDRESS);
    }
}