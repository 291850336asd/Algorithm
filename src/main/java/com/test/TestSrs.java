package com.test;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TestSrs {

    public static void main(String[] args) throws IOException, InterruptedException {
//        String s = sendDoGet("http://192.168.5.46:1985/api/v1/streams/", "");
//        System.out.println(s);

        Process process = Runtime.getRuntime().exec("wmic process where caption=\"ffmpeg.exe\" get ProcessId, CommandLine,UserModeTime");
        int status = process.waitFor();

        System.out.println(status);
        InputStream in = process.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        List<String> aaCmdStreams = new ArrayList<>();
        String line = null;
        do{
            line = br.readLine();
            if(line != null){
                if(line.contains("ffmpeg") && line.contains("live")){
                    String s = line.replaceAll("  ", " ")
                            .replaceAll("  ", " ")
                            .replaceAll("  ", " ")
                            .replaceAll("  ", " ");
                    aaCmdStreams.add(s.substring(0, s.length()-1));
                }
            }
        } while(line!=null);
        aaCmdStreams.forEach(item ->{
           kill(item.substring(item.lastIndexOf(" ")));
        });
    }


    private static void kill(String pid) {

        try {
            Process process = Runtime.getRuntime().exec(" taskkill /F  /PID " + pid);
            process.waitFor();
        }catch (Exception e){

        }
    }


    private static class SrsStreamResponse{
        private int code;
        List<SrsStreamMode> streams;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public List<SrsStreamMode> getStreams() {
            return streams;
        }

        public void setStreams(List<SrsStreamMode> streams) {
            this.streams = streams;
        }
    }

    private static class SrsStreamMode{
        private String id;
        private String name;
        private PublishState publish;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public PublishState getPublish() {
            return publish;
        }

        public void setPublish(PublishState publish) {
            this.publish = publish;
        }
    }


    private static class PublishState{
        boolean active;
        private String cid;

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }
    }

    public static String sendDoGet(String url,String token) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            // ?????????????????????????????????httpClient??????
            httpClient = HttpClients.createDefault();
            // ??????httpGet??????????????????
            HttpGet httpGet = new HttpGet(url);
            // ??????????????????????????????
            httpGet.setHeader("X-Access-Token", token);
            // ????????????????????????
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(15000)// ??????????????????????????????
                    .setConnectionRequestTimeout(15000)// ??????????????????
                    .setSocketTimeout(15000)// ????????????????????????
                    .build();
            // ???httpGet??????????????????
            httpGet.setConfig(requestConfig);
            // ??????get????????????????????????
            response = httpClient.execute(httpGet);
            // ????????????????????????????????????
            HttpEntity entity = response.getEntity();
            // ??????EntityUtils??????toString?????????????????????????????????
            result = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // ????????????
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
