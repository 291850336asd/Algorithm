package com.test.hiki.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.css.Rect;

import com.jfinal.core.Controller;

import com.test.hiki.communicationCom.HTTPClientUtil;
import com.test.hiki.communicationCom.HttpsClientUtil;

public class HumanRecognition extends Controller {
    public void index() {
        render("/UI-Resource/Task/HumanRecognition.html");
    }
    
    //Query human analysis capability
    public void getHumanRecognitionAbility() {
        JSONObject jsonRet = null;
        boolean bException = false;
        try {
            String strUrl = "";
            if (HttpsClientUtil.bHttpsEnabled) {
                // Assemble the URL
                strUrl = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort
                        + "/ISAPI/Intelligent/humanRecognition/capabilities?format=json";

                jsonRet = new JSONObject(HttpsClientUtil.httpsGet(strUrl));
            } else {
                // Assemble the URL
                strUrl = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort
                        + "/ISAPI/Intelligent/humanRecognition/capabilities?format=json";
                jsonRet = new JSONObject( HTTPClientUtil.doGet(strUrl));
            }
        }
        catch (JSONException ex) {
            //Some devices are not supported and the returned string cannot be converted to json
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

    //Analysis of the human body
    public void humanRecognition()
    {
        String humanDetectRes = humanDetection();
        this.renderJson(humanDetectRes);
    }
    
    //The human body detection
    //If successful, a successful json is returned; if not, an error message is returned
    public String humanDetection()
    {
        JSONObject jsonData = new JSONObject();
        JSONObject jsonRet = null;
        String strErrorRet="";
        String pictureType = getPara("pictureType");
        String pictureFile = getPara("pictureFile");
        String pictureUrl = getPara("pictureUrl");
        
        try {
            // Assemble json data
            if(pictureType.equals("url"))
            {
                jsonData.put("URL", pictureUrl);
            }
            else if(pictureType.equals("base64"))
            {
                jsonData.put("picData",pictureFile);
            }
           
            String strUrl = "";
            //If it's url transfer
            if(pictureType.equals("url") || pictureType.equals("base64"))
            {
                if (HttpsClientUtil.bHttpsEnabled) {
                    // Assemble the URL
                    strUrl = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort
                            + "/ISAPI/Intelligent/humanRecognition?format=json";
                    jsonRet = new JSONObject(HttpsClientUtil.httpsPost(strUrl, jsonData.toString()));
                } else {
                    // Assemble the URL
                    strUrl = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort
                            + "/ISAPI/Intelligent/humanRecognition?format=json";
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
                            + "/ISAPI/Intelligent/humanRecognition?format=json";
                    jsonRet = new JSONObject(HttpsClientUtil.doPostwithBinaryData(strUrl, jsonData.toString(),"Alogorithm",pictureFile,"imageData",boundary));
                } else {
                    // Assemble the URL
                    strUrl = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort
                            + "/ISAPI/Intelligent/humanRecognition?format=json";
                    jsonRet = new JSONObject(HTTPClientUtil.doPostwithBinaryData(strUrl, jsonData.toString(),"Alogorithm",pictureFile,"imageData",boundary));
                }
            }
        } catch (Exception e) {
            strErrorRet ="{\"errorMsg\":"+ e.toString() +"}";
            return strErrorRet;
        }
        return jsonRet.toString();
    }
    
    //Picture body modeling
    public String doHumanModel(JSONObject jsonRect){
        JSONObject jsonData = new JSONObject();
        JSONObject jsonRet = null;
        String strRet="";
        String pictureType = getPara("pictureType");
        String pictureFile = getPara("pictureFile");
        String pictureUrl = getPara("pictureUrl");
        
        try {
            // Assemble json data
            if(pictureType.equals("url"))
            {
                jsonData.put("URL", pictureUrl);
            }
            else if(pictureType.equals("base64"))
            {
                jsonData.put("picData",pictureFile);
            }
            jsonData.put("Rect", jsonRect);
            
            String strUrl = "";
            //If it's url transfer
            if(pictureType.equals("url") || pictureType.equals("base64"))
            {
                if (HttpsClientUtil.bHttpsEnabled) {
                    // Assemble the URL
                    strUrl = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort
                            + "/ISAPI/Intelligent/humanModel?format=json";
                    jsonRet = new JSONObject(HttpsClientUtil.httpsPost(strUrl, jsonData.toString()));
                } else {
                    // Assemble the URL
                    strUrl = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort
                            + "/ISAPI/Intelligent/humanModel?format=json";
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
                            + "/ISAPI/Intelligent/humanModel?format=json";
                    jsonRet = new JSONObject(HttpsClientUtil.doPostwithBinaryData(strUrl, jsonData.toString(),"Alogorithm",pictureFile,"imageData",boundary));
                } else {
                    // Assemble the URL
                    strUrl = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort
                            + "/ISAPI/Intelligent/humanModel?format=json";
                    jsonRet = new JSONObject(HTTPClientUtil.doPostwithBinaryData(strUrl, jsonData.toString(),"Alogorithm",pictureFile,"imageData",boundary));
                }
            }
        } catch (Exception e) {
            strRet ="{\"errorMsg\":"+ e.toString() +"}";
            return strRet;
        }
        
        return jsonRet.toString();
    }
    
    //Search people by people
    public void searchHuman(){
        
        try {
            //Step1: human body detection
            String humanDetectRes = humanDetection();
            JSONObject jsonHumaDetectRes =  new JSONObject(humanDetectRes);
            
            //Step1.1 judge the number of human body. The picture contains multiple human bodies, so the modeling operation is not carried out
            JSONArray jsonRectArr = jsonHumaDetectRes.getJSONArray("Rect");
            if( jsonRectArr.length() > 1 || jsonRectArr.length() <1)
            {
               this.renderJson("errorMsg","The image contains more than one human, we can't do model!");
            }
            JSONObject  jsonRect = jsonRectArr.getJSONObject(0);
            jsonRect.put("Rect", jsonRect);
            
            //Step2: human body modeling
            String datamodel = doHumanModel(jsonRect);
            //Step2.1 parsing modelData
//            if(1 == Integer.parseInt(jsonRet.getString("errorCode")))
//            {
//                
//            }
//           
            
            //Step3: human body search
            
            
        } catch (JSONException ex) {
            // TODO: handle exception
            
        }catch (Exception e)
        {
            
        }
        
        
        
        
        
        
    }
}
