package com.test.ffmpeg;

import java.io.IOException;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

import static org.bytedeco.ffmpeg.global.avcodec.*;


/**
 * rtsp转rtmp（转封装方式）.
 */
public class ConvertVideoPakcet {
    FFmpegFrameGrabber grabber = null;
    FFmpegFrameRecorder record = null;
    int width = -1, height = -1;
    int speed = 2;
    protected int codecid;
    protected double framerate= 20;// 帧率
    protected int bitrate;// 比特率

    /**
     * 选择视频源.
     */
    public ConvertVideoPakcet from(String src) throws Exception {
        // 采集/抓取器
        grabber = new FFmpegFrameGrabber(src);
        grabber.setSampleMode(FrameGrabber.SampleMode.SHORT);
        if(src.indexOf("rtsp")>=0) {
            grabber.setOption("rtsp_transport","tcp");
        }
        grabber.setAudioCodecName("aac");

        grabber.start();// 开始之后ffmpeg会采集视频信息，之后就可以获取音视频信息
        if (width < 0 || height < 0) {
            width = grabber.getImageWidth();
            height = grabber.getImageHeight();
        }
        framerate = grabber.getFrameRate();
        return this;
    }

    /**
     * 选择输出.
     */
    public ConvertVideoPakcet to(String out) throws IOException {
        // 录制/推流器
        record = new FFmpegFrameRecorder(out, width, height);
        record.setGopSize(60);
        record.setFrameRate(framerate);
        record.setVideoBitrate(bitrate);
        AVFormatContext fc = null;
        if (out.indexOf("rtmp") >= 0 || out.indexOf("flv") > 0) {
            // 封装格式flv
            record.setFormat("flv");
            record.setAudioCodecName("aac");
            fc = grabber.getFormatContext();
        }
        record.start(fc);
        return this;
    }

    /**
     * 转封装.
     */
    public ConvertVideoPakcet go() throws IOException {
        long err_index = 0;//采集或推流导致的错误次数test
        //连续五次没有采集到帧则认为视频采集结束，程序错误次数超过1次即中断程序
        int pktindex = 0;
        long firstpkttime = System.currentTimeMillis();
        long dts = 0;
        long pts = 0;
        int i = 0;
        for(int no_frame_index=0;no_frame_index<5||err_index>1;) {
            AVPacket pkt=null;
            try {
                //没有解码的音视频帧
                pkt=grabber.grabPacket();
                if(pkt==null||pkt.size()<=0||pkt.data()==null) {
                    //空包记录次数跳过
                    no_frame_index++;
                    av_packet_unref(pkt);
                    continue;
                }
                // 矫正pkt的dts，pts每次不从0开始累加所导致的播放器无法续播问题
                System.out.println("-----");
                System.out.println(pkt.pts());
                System.out.println(pkt.dts());
                pkt.pts(pts);
                pkt.dts(dts);
                i++;
                if(i%4==0){
                    //不需要编码直接把音视频帧推出去
                    err_index+=(record.recordPacket(pkt)?0:1);//如果失败err_index自增1
                }
                if(i>100){
                    i =0;
                }
                // 帧数加1
                pktindex++;
//                if (pktindex >= framerate / speed) {
//                    long nowtime = System.currentTimeMillis();
//                    if (nowtime - firstpkttime  < 1000) {
////                        try {
////                            Thread.sleep(1000 - (nowtime - firstpkttime ));
////                        } catch (InterruptedException e) {
////                            e.printStackTrace();
////                        }
//                    }
//                    firstpkttime = System.currentTimeMillis();
//                    pktindex = 0;
//                }
                 // 计算出pts、dts时间戳间隔
                int timebase = grabber.getFormatContext().streams(pkt.stream_index()).time_base().den();
                System.out.println("timebase: " + timebase);
                // pts,dts累加,倍速播放时，dts、pts相应翻倍或减半，使播放器没s播对应数量的帧
                pts += (timebase / (int) framerate) / speed;
                dts += (timebase / (int) framerate) / speed;
                av_packet_unref(pkt);
            }catch (Exception e) {//推流失败
                err_index++;
            } catch (IOException e) {
                err_index++;
            }
        }
        return this;
    }

    public static void main(String[] args) throws Exception, IOException {

        //运行，设置视频源和推流地址
//        new ConvertVideoPakcet().from("rtsp://admin:1q2w3e4r@192.168.5.18:554/h264/ch1/main/av_stream")
        new ConvertVideoPakcet().from("rtsp://admin:1q2w3e4r@192.168.5.20:554/Streaming/tracks/601?starttime=20220506t110830z")
                .to("rtmp://127.0.0.1/live/hw")
                .go();
    }
}