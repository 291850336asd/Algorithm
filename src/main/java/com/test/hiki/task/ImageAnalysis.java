package com.test.hiki.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jfinal.core.Controller;

import com.test.hiki.communicationCom.HTTPClientUtil;
import com.test.hiki.communicationCom.HttpsClientUtil;

public class ImageAnalysis extends Controller {
    public void index() {
        render("/UI-Resource/Task/ImageAnalysis.html");
    }
    //Gets the supported algorithm
    public void getClassificationID() {
        JSONObject jsonRet = null;
        boolean bException = false;
        try {
            String strUrl = "";
            if (HttpsClientUtil.bHttpsEnabled) {
                // Assemble the URL
                strUrl = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort
                        + "/ISAPI/Intelligent/algorithmInfos?format=json";

                jsonRet = new JSONObject(HttpsClientUtil.httpsGet(strUrl));
            } else {
                // Assemble the URL
                strUrl = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort
                        + "/ISAPI/Intelligent/algorithmInfos?format=json";
                jsonRet = new JSONObject( HTTPClientUtil.doGet(strUrl));
            }
        }
        catch (JSONException ex) {
            bException = true;
            setAttr("errorMsg", "The device don't support this function!");

        }catch (Exception e) {
            bException = true;
            setAttr("errorMsg", e.toString());
        } finally {
            if (bException) {
                this.renderJson();
            } else {
                // Normal interaction, return the status information returned by the device
                this.renderJson(jsonRet.toString());
            }

        }
    }

    //Upload synchronous image processing tasks
    public void uploadAscyTask()
    {
        JSONObject jsonData = new JSONObject();
        JSONObject jsonRet = null;
        boolean bException = false;
        String pictureType = getPara("pictureType");
        String pictureFile = getPara("pictureFile");
        String pictureUrl = getPara("pictureUrl");
        
        try {
            // Assemble json data
            JSONObject jsontaskInfoData = new JSONObject();

            //taskInfo->algorithm
            JSONArray jsonAlgorithmDataList = new JSONArray();

            JSONObject jsonAlgorithmData = new JSONObject();
            jsonAlgorithmData.put("classificationID", getPara("classificationID"));
            jsonAlgorithmData.put("analysisSourceType", "picture"); //The default for the picture
            jsonAlgorithmData.put("targetType", getPara("targetType"));
            jsonAlgorithmDataList.put(jsonAlgorithmData);

            jsontaskInfoData.put("algorithm", jsonAlgorithmDataList);

            //taskInfo->picture
            JSONObject jsonPictureData = new JSONObject();
            
            //Assemble json data according to the picture type
            if(pictureType.equals("url"))
            {
                jsonPictureData.put("dataType", "URL");
                jsonPictureData.put("URL", pictureUrl);
            }
            else if(pictureType.equals("binary"))
            {
                jsonPictureData.put("dataType", "binary");
            }
            else if(pictureType.equals("base64"))
            {
                jsonPictureData.put("dataType", "base64");
                jsonPictureData.put("picData", pictureFile);
            }
            jsontaskInfoData.put("picture", jsonPictureData);

            jsonData.put("TaskInfo", jsontaskInfoData);

            String strUrl = "";
            //If it's url transfer
            if(pictureType.equals("url") || pictureType.equals("base64"))
            {
                if (HttpsClientUtil.bHttpsEnabled) {
                    // Assemble the URL
                    strUrl = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort
                            + "/ISAPI/SDT/Management/Task/Picture/sync?format=json";
                    jsonRet = new JSONObject(HttpsClientUtil.httpsPost(strUrl, jsonData.toString()));
                } else {
                    // Assemble the URL
                    strUrl = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort
                            + "/ISAPI/SDT/Management/Task/Picture/sync?format=json";
                    jsonRet = new JSONObject(HTTPClientUtil.doPost(strUrl, jsonData.toString()));
                }
            }
            //If the image is binary, the form format is used
            else if(pictureType.equals("binary"))
            {
                String boundary ="---------------------------------7e13971310878";
                if (HttpsClientUtil.bHttpsEnabled) {
                    // Assemble the URL
                    strUrl = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort
                            + "/ISAPI/SDT/Management/Task/Picture/sync?format=json";
                    jsonRet = new JSONObject(HttpsClientUtil.doPostwithBinaryData(strUrl, jsonData.toString(),"Alogorithm",pictureFile,"imageData",boundary));
                } else {
                    // Assemble the URL
                    strUrl = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort
                            + "/ISAPI/SDT/Management/Task/Picture/sync?format=json";
                    jsonRet = new JSONObject(HTTPClientUtil.doPostwithBinaryData(strUrl, jsonData.toString(),"Alogorithm",pictureFile,"imageData",boundary));
                }
            }

        } catch (Exception e) {
            bException = true;
            setAttr("errorMsg", e.toString());
        } finally {
            if (bException) {
                // Exception returns exception information directly
                this.renderJson();
            } else {
                // Normal interaction, return the status information returned by the device
                this.renderJson(jsonRet.toString());
            }

        }
    }
}
