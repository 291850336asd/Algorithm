package com.test.ffmpeg;

import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import org.openjdk.jol.info.GraphLayout;
import sun.misc.BASE64Encoder;
import sun.nio.ch.DirectBuffer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.nio.*;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


public class PushMp4Cavas {

//    private static final String RTSP_PATH = "rtsp://admin:1q2w3e4r@192.168.5.20:554/Streaming/tracks/601?subtype=0&starttime=20220626t000000z";
//    private static final String RTSP_PATH = "rtsp://admin:1q2w3e4r@192.168.5.20:554/Streaming/tracks/601?starttime=20220627t000900z";
//    private static final String RTSP_PATH = "rtsp://admin:1q2w3e4r@192.168.5.29/h264/ch1/main/av_stream";
//private static final String RTSP_PATH = "rtsp://admin:1q2w3e4r@192.168.5.165/tcp/av0_0";
//    private static final String RTSP_PATH = "rtsp://192.168.5.174:554/user=admin&password=123456&channel=1&stream=0.sdp";
    private static final String RTSP_PATH = "rtsp://admin:1q2w3e4r@192.168.5.26/h264/ch1/main/av_stream";
//    private static final String RTSP_PATH = "rtsp://admin:1q2w3e4r@192.168.5.18:554/h264/ch1/main/av_stream";


    private static BlockingQueue<byte[]> blockingQueue = new LinkedBlockingQueue<>(20);
    /**
     * SRS的推流地址
     */
    private static final String SRS_PUSH_ADDRESS = "rtmp://192.168.5.46/live/hw1";


    private static AtomicLong atomicLong = new AtomicLong();
    /**
     * 读取rtsp，推送到SRS服务器
     * @param sourceFilePath rtsp路径
     * @param PUSH_ADDRESS 推流地址
     * @throws Exception
     */
    public static void grabAndPush(String sourceFilePath, String PUSH_ADDRESS) throws Exception {
        // ffmepg日志级别
        System.out.println("grabAndPush");
        avutil.av_log_set_level(avutil.AV_LOG_ERROR);
        FFmpegLogCallback.set();
        // 实例化帧抓取器对象，将文件路径传入
        G grabber = new G(sourceFilePath);
        grabber.setOption("rtsp_transport","tcp");
        grabber.setOption("buffer_size", "1024000");
        grabber.setFormat("rtsp");
//        // 读写超时，适用于所有协议的通用读写超时
        grabber.setOption("rw_timeout", "1000000");
        grabber.setOption("stimeout", "10000000");
//        // 探测视频流信息，为空默认5000000微
//        grabber.setOption("probesize","15000000");
//        // 解析视频流信息，为空默认5000000微秒
        grabber.setOption("analyzeduration", "10000000");

//        // rtmp拉流缓冲区，默认3000毫秒
        grabber.setOption("rtmp_buffer", "3000");
//           grabber.setMaxDelay(500000);

        grabber.startUnsafe(true);


//        log.info("视频帧率[{}]，视频时长[{}]秒，媒体流数量[{}▓]",
//                frameRate,
//                avFormatContext.duration()/1000000,
//                avFormatContext.nb_streams());
        // 视频宽度

//        log.info("视频宽度[{}]，视频高度[{}]，音频通道数[{}]",
//                frameWidth,
//                frameHeight,
//                audioChannels);


        Frame frame;


//        log.info("开始推流");

        long videoTS = 0;

        int videoFrameNum = 0;
        int audioFrameNum = 0;
        int dataFrameNum = 0;


        Base64.Encoder encoder = Base64.getEncoder();
        Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
        long time = System.currentTimeMillis();
        // 持续从视频源取帧
        int i = 0;

        int errorCount=0;
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
//              if(blockingQueue.size() > 18){
//                  blockingQueue.clear();
//              }

              ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
              BufferedImage bi = java2DFrameConverter.getBufferedImage(frame);
              ImageIO.write(bi, "jpg", outputStream);
              byte[] poll = outputStream.toByteArray();
              if(poll != null){
                  blockingQueue.offer(poll,10, TimeUnit.MILLISECONDS);
              } else {
                  System.out.println("0");
              }

////                String imageBase64 = "data:image/jpg;base64," + encoder.encodeToString(outputStream.toByteArray());
              outputStream.flush();
              java2DFrameConverter.close();
              bi.getGraphics().dispose();
              frame.close();
              outputStream.close();

          }catch (Exception e){
              e.printStackTrace();
          }
        }
        grabber.close();
//        log.info("推送完成，视频帧[{}]，音频帧[{}]，数据帧[{}]，耗时[{}]秒",
//                videoFrameNum,
//                audioFrameNum,
//                dataFrameNum,
//                (System.currentTimeMillis()-startTime)/1000);

    }

    private static void openSocket(){
        new Thread(()->{
            List<Socket> clientSocket = new CopyOnWriteArrayList<>();
            ServerSocket server = null;
            new Thread(()->{
                while (true){

                    try {
                        byte[] poll = blockingQueue.poll(10, TimeUnit.MILLISECONDS);
                        if(poll!= null){
                            System.out.println("1111");
                        } else {
                            System.out.println("0000");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    send(clientSocket);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            while (true) {
                try {
                    if (server == null) {
                        server = new ServerSocket(8888);
                    }
                    Socket socket = server.accept();
                    clientSocket.add(socket);




                } catch (IOException e) {
                    try {
                        if (server != null) {
                            server.close();
                        }
                        Thread.sleep(1000);
                    } catch (Exception e1) {
                    } finally {
                        server = null;
                    }
                }
            }


        }).start();
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
//        grabber.setAudioCodecName("aac");
        //         设置缓存大小，提高画质、减少卡顿花屏
        grabber.setOption("buffer_size", "10240000");
        // 读写超时，适用于所有协议的通用读写超时
        grabber.setOption("rw_timeout", "15000000");
        // 探测视频流信息，为空默认5000000微秒
        grabber.setOption("probesize","15000000");
        grabber.setVideoOption("threads", "8"); //解码线程数
        // rtmp拉流缓冲区，默认3000毫秒
        grabber.setOption("rtmp_buffer", "10000");
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
                0);

        // 设置编码格式
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);

        // 设置封装格式
        recorder.setFormat("flv");

        recorder.setSampleRate(grabber.getSampleRate());
        // 一秒内的帧数
//        recorder.setFrameRate(frameRate);

        // 两个关键帧之间的帧数
//        recorder.setGopSize(10);
        recorder.setVideoOption("threads", "8"); //解码线程数
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


            // 有声音，就把音频帧加一
            if (null!=frame.samples) {
                audioFrameNum++;
            }

            // 有数据，就把数据帧加一
            if (null!=frame.data) {
                dataFrameNum++;
            }
            if (null!=frame.image) {
                videoFrameNum++;
            }

            // 取出的每一帧，都推送到SRS
            recorder.record(frame);
            frame = null;
            // 停顿一下再推送
//            Thread.sleep(interVal);
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
    private static void send(List<Socket> clientSocket){
        Iterator<Socket> iterator = clientSocket.iterator();

        while (iterator.hasNext()){
            Socket client = iterator.next();
            if(client != null && client.isConnected() && !client.isOutputShutdown()){
                try {
                    BufferedWriter writer;
                    String msg;
                    OutputStream outputStream = client.getOutputStream();
                    writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    msg = "AT+STACH0=1,30\r\n";
                    outputStream.write(msg.getBytes(StandardCharsets.UTF_8));
                    //发送数据
//                    byte[] poll = blockingQueue.poll(10, TimeUnit.MILLISECONDS);
//                    if(poll != null){
////                        writer.write(msg);
////                        writer.flush();
//                        outputStream.write(poll);
//                        outputStream.flush();
//                    }

                }catch (Exception e){
                    try {
                        if(client!= null){
                            client.shutdownOutput();
                            client.close();
                        }
                        clientSocket.remove(client);
                    }catch (Exception e1){
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    if(client!= null){
                        client.shutdownOutput();
                        client.close();
                    }
                    clientSocket.remove(client);
                }catch (Exception e){

                }
            }
        }
    }
    public static void main(String[] args) throws Exception {
        openSocket();
        while (true){
            PushMp4Cavas.grabAndPush(RTSP_PATH, SRS_PUSH_ADDRESS);
        }
    }


}