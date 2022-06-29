package com.test.ffmpeg;

import org.bytedeco.ffmpeg.avcodec.AVCodecParameters;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVStream;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;


public class RtspToFlvThread implements Runnable{

    private boolean isLive;
    private long totalTime;
    private String fromRtsp;
    private String toFlv;

    /**
     *
     * @param isLive   是否是实时视频
     * @param totalTime  历史回放截止时间  当前时间+播放时长,单位秒
     * @param fromRtsp
     * @param toFlv
     */
    public RtspToFlvThread(boolean isLive, long totalTime, String fromRtsp, String toFlv) {
        this.isLive = isLive;
        this.totalTime = totalTime;
        this.fromRtsp = fromRtsp;
        this.toFlv = toFlv;
    }
    /**
     * 读取rtsp，推送到SRS服务器
     * @param sourceFilePath rtsp路径
     * @param PUSH_ADDRESS 推流地址
     * @throws Exception
     */
    private  void grabAndPush(String sourceFilePath, String PUSH_ADDRESS) throws Exception {
        // ffmepg日志级别
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
        FFmpegLogCallback.set();
        // 实例化帧抓取器对象，将文件路径传入
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(sourceFilePath);
        grabber.setAudioCodecName("aac");
        long startTime = System.currentTimeMillis();
        // 初始化帧抓取器，例如数据结构（时间戳、编码器上下文、帧对象等），
        // 如果入参等于true，还会调用avformat_find_stream_info方法获取流的信息，放入AVFormatContext类型的成员变量oc中
        grabber.start(true);

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
        // 视频宽度
        int frameWidth = grabber.getImageWidth();
        // 视频高度
        int frameHeight = grabber.getImageHeight();
        // 音频通道数量
        int audioChannels = grabber.getAudioChannels();

        // 实例化FFmpegFrameRecorder，将SRS的推送地址传入
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(PUSH_ADDRESS,
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
        recorder.setGopSize(frameRate);

        // 设置音频通道数，与视频源的通道数相等
        recorder.setAudioChannels(grabber.getAudioChannels());

        startTime = System.currentTimeMillis();
//        log.info("开始初始化帧抓取器");

        // 初始化帧录制器，例如数据结构（音频流、视频流指针，编码器），
        // 调用av_guess_format方法，确定视频输出时的封装方式，
        // 媒体上下文对象的内存分配，
        // 编码器的各项参数设置
        recorder.start();

        Frame frame;
        startTime = System.currentTimeMillis();

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
        while (null!=(frame=grabber.grab())  && (isLive || (!isLive && System.currentTimeMillis()/1000 < totalTime))) {
            videoTS = 1000 * (System.currentTimeMillis() - startTime);
            // 时间戳
            recorder.setTimestamp(videoTS);
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
            // 停顿一下再推送
//            Thread.sleep(interVal);
        }
        // 关闭帧录制器
        recorder.close();
        // 关闭帧抓取器
        grabber.close();
    }

    @Override
    public void run() {
       initStream();
    }

    private void initStream() {
        try {
            grabAndPush(fromRtsp, toFlv);
        } catch (Exception e) {
        }finally {
            if(isLive){
                try {
                    Thread.sleep(5000);
                } catch (Exception interruptedException) {
                    interruptedException.printStackTrace();
                }
                initStream();
            }
        }
    }
}