package com.test.hiki.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jfinal.core.Controller;

import com.test.hiki.communicationCom.HTTPClientUtil;
import com.test.hiki.communicationCom.HttpsClientUtil;

public class VehicleReAnalysis extends Controller{
    
    public void index() {
        render("/UI-Resource/Task/VehicleReAnalysis.html");
    }

    //Acquire vehicle image secondary analysis task capability set
    public void getVehicleReAnalysisAbility() {
        JSONObject jsonRet = null;
        boolean bException = false;
        try {
            String strUrl = "";
            if (HttpsClientUtil.bHttpsEnabled) {
                // Assemble the URL
                strUrl = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort
                        + "/ISAPI/SDT/Management/Task/VehiclePicture/capabilities?format=json";

                jsonRet = new JSONObject(HttpsClientUtil.httpsGet(strUrl));
            } else {
                // Assemble the URL
                strUrl = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort
                        + "/ISAPI/SDT/Management/Task/VehiclePicture/capabilities?format=json";
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
                // Exception returns exception information directly
                this.renderJson();
            } else {
                // Normal interaction, return the status information returned by the device
                this.renderJson(jsonRet.toString()); //Replace ("@","")
            }

        }
        
    }
    
    /*Upload vehicle secondary analysis task
    step1.Assemble json data
    step2.Choose different ways to exchange data with the device according to the type of picture uploading
    */
    public void uploadVehicleReAnalysisTask(){
        JSONObject jsonRet = null;
        boolean bException =false;
        JSONObject jsonData =new JSONObject();
        String pictureBinary = getPara("pictureBinary");
        String pictureUrl = getPara("pictureUrl");
        try{
            //step1.Assemble json data
            JSONObject jsonTaskInfo =new JSONObject();
            
            jsonTaskInfo.put("taskName", getPara("taskName"));
            jsonTaskInfo.put("detectMode", getParaToInt("detectMode"));
            jsonTaskInfo.put("taskPriority", getParaToInt("taskPriority"));
            
            //Choose one of the three algorithms (algorithmType��algorithm.classificationID ��algorithm.algorithmID)
            int algorithmType= getParaToInt("algorithmType");       
            if(algorithmType==1){
                jsonTaskInfo.put("algorithmType", getPara("algorithmID"));
            }
            else if(algorithmType==2 || algorithmType==3){
              //TaskInfo->algorithm
              JSONArray jsonAlgArr =new JSONArray();
              JSONObject jsonAlg =new JSONObject();
              
              if(algorithmType==2)  //classificationID
              {
                  jsonAlg.put("classificationID", getPara("algorithmID"));
              }
              else if(algorithmType==3)  //algorithmID
              {
                  jsonTaskInfo.put("algorithmID", getPara("algorithmID"));
              }
              
              jsonAlg.put("targetType", "2"); //Algorithm target type string: 2- vehicle */
              jsonAlg.put("analysisSourceType", "picture");
              jsonAlgArr.put(jsonAlg);
              jsonTaskInfo.put("algorithm", jsonAlgArr);
            }
            //TaskInfo->pictureInfos
            JSONArray jsonPicInfoArr = new JSONArray();
            JSONObject jsonPicInfo = new JSONObject();
            if(!pictureUrl.equals("")){
                jsonPicInfo.put("dataType", "URL");
                jsonPicInfo.put("URL", pictureUrl);
            }
            else{
                jsonPicInfo.put("dataType", "binary");
            }
            
            //TaskInfo->pictureInfos->license
            if(!getPara("licenseID").equals("")){
                JSONObject jsonLicense =new JSONObject();
                jsonLicense.put("value", getPara("licenseID"));
                if(!getPara("confidence").equals("")){
                    jsonLicense.put("confidence", getPara("confidence"));
                }
                jsonPicInfo.put("license", jsonLicense);
            }
            
            jsonPicInfoArr.put(jsonPicInfo);
            jsonTaskInfo.put("pictureInfos", jsonPicInfoArr);
            
            //TaskInfo->destination
            JSONArray jsonDstArr = new JSONArray();
            JSONObject jsonDst = new JSONObject();
            jsonDst.put("destinationType", getPara("destinationType"));
            jsonDst.put("destinationUrl", getPara("destinationUrl"));
            
            if(!getPara("userName").equals("")){
                jsonDst.put("userName", getPara("userName"));
            }
            if(!getPara("password").equals("")){
                jsonDst.put("password", getPara("password"));
            }
            
            jsonDstArr.put(jsonDst);
            jsonTaskInfo.put("destination", jsonDstArr);
            
            jsonData.put("TaskInfo", jsonTaskInfo);
            
            
            //step2.Choose different ways to exchange data with the device according to the type of picture uploading
            String strUrl ="";
            //step2.1 Images are transmitted using urls
            if(!pictureUrl.equals("")){
                if(HttpsClientUtil.bHttpsEnabled){
                    //Assemble the url
                    strUrl="https://"+HttpsClientUtil.strIP+":"+HttpsClientUtil.iPort
                            +"/ISAPI/SDT/Management/Task/VehiclePicture?format=json";
                    jsonRet=new JSONObject(HttpsClientUtil.httpsPost(strUrl, jsonData.toString()));
                }
                else{
                    strUrl = "http://"+HTTPClientUtil.strIP+":"+HTTPClientUtil.iPort
                            +"/ISAPI/SDT/Management/Task/VehiclePicture?format=json";
                    jsonRet= new JSONObject(HTTPClientUtil.doPost(strUrl, jsonData.toString()));
                }
            }
            else{//step2.2 Images are transmitted in binary
                String boundary ="---------------------------------7e13971310878";
                if (HttpsClientUtil.bHttpsEnabled) {
                    // Assemble the URL
                    strUrl = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort
                            + "/ISAPI/SDT/Management/Task/VehiclePicture?format=json";
                    jsonRet = new JSONObject(HttpsClientUtil.doPostwithBinaryData(strUrl, jsonData.toString(),"VehiclePicture",pictureBinary,"imageData",boundary));
                } else {
                    // Assemble the URL
                    strUrl = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort
                            + "/ISAPI/SDT/Management/Task/VehiclePicture?format=json";
                    jsonRet = new JSONObject(HTTPClientUtil.doPostwithBinaryData(strUrl, jsonData.toString(),"VehiclePicture",pictureBinary,"imageData",boundary));
                }
            }
        }
        catch(Exception e){
            bException = true;
            setAttr("errorMsg", e.toString());
        }finally {
            if(bException)
            {
                // Exception returns exception information directly
                this.renderJson();
            }else{
                // Normal interaction, return the status information returned by the device
                this.renderJson(jsonRet.toString());
            }
        }
    }
}
