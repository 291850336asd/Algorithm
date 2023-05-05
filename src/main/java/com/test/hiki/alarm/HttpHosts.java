package com.test.hiki.alarm;
import java.util.List;
import java.io.ByteArrayInputStream;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONObject;
import com.jfinal.core.Controller;

import com.test.hiki.communicationCom.HTTPClientUtil;
import com.test.hiki.communicationCom.HttpsClientUtil;

public class HttpHosts  extends Controller{
    public void index() {
        render("/UI-Resource/Alarm/HttpHosts.html");
    }
    
    
    //check the ability
    public void isSupportJson()
    {
        boolean bException=false;    
        JSONObject jsonRet = null;
        
        try{
            String strUrl = "";   
            if(HttpsClientUtil.bHttpsEnabled)
            {
                //Assemble the URL    
                strUrl= "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/Event/notification/httpHosts/capabilities?format=json";
                jsonRet = new JSONObject(HttpsClientUtil.httpsGet(strUrl));                                  
            } 
            else
            {
                //Assemble the URL     
                strUrl= "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/Event/notification/httpHosts/capabilitie?format=json";
                jsonRet = new JSONObject(HTTPClientUtil.doGet(strUrl));      
            }
            
        }catch (Exception e)
        {
            bException = true;
            setAttr("Exception",bException);
            setAttr("errorMsg",e.toString());        
        }
        finally
        {
            if(bException)
            {
                //Exception returns exception information directly
                this.renderJson();
            }
            else
            {
                //Normal interaction, return the status information returned by the device
                this.renderJson(jsonRet.toString());
            }
        }
    }
    
    
    //Add alarm host
    public void addHost()
    {
        String parameterFormatType=getPara("parameterFormatType");
        if(parameterFormatType.equals("json")) 
        {
            addHostByJson();
        }
        else //"xml"==parameterFormatType
        {
            modifyHostByXml();
        }        
    }
    
    //Add the alarm host information based on json
    public void addHostByJson()
    {
        //Get host info
        String host = getPara("host");
        String url = getPara("url");
        String eventType=getPara("eventType");
        String portNo = getPara("portNo");
        //new  json object
        JSONObject jsonData = new JSONObject();
        JSONObject jsonRet = null;
        
        boolean bException=false;
        try{       
            //Assemble json data
            jsonData.put("url",url);
            jsonData.put("portNo",Integer.parseInt(portNo));
            
            jsonData.put("protocolType", "HTTP");
            jsonData.put("parameterFormatType", "json");
            jsonData.put("addressingFormatType", "ipaddress");  //hostname
            jsonData.put("ipAddress",host);
            jsonData.put("eventType", eventType);
            jsonData.put("httpAuthenticationMethod", "none");
            
            JSONObject jsonData1 = new JSONObject();
            jsonData1.put("HttpHostNotification", jsonData);
            
            String strUrl = "";  
            if(HttpsClientUtil.bHttpsEnabled)
            {
               //Assemble the URL     
                 strUrl= "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/Event/notification/httpHosts?format=json";
                 jsonRet = new JSONObject(HttpsClientUtil.httpsPost(strUrl, jsonData1.toString()));                                  
            } 
            else
            {
               //Assemble the URL  
                 strUrl= "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/Event/notification/httpHosts?format=json";
                 jsonRet = new JSONObject(HTTPClientUtil.doPost(strUrl, jsonData1.toString()));      
            }
          
        }catch (Exception e){
              
                bException = true;            
                setAttr("errorMsg",e.toString());               
            }               
            finally
            {
                if(bException)
                {
                    //Exception returns exception information directly
                    this.renderJson();
                }
                else
                {
                    //Normal interaction, return the status information returned by the device
                    this.renderJson(jsonRet.toString());
                }
                               
            }                    
    }
    
    /**
    * @Description Get alarm host information
    * @param null
    * @return void
    */    
    public void  getHosts()
    {
        String parameterFormatType=getPara("parameterFormatType");
        if(parameterFormatType.equals("json")) 
        {
            getHostByJson();
        }
        else //"xml"==parameterFormatType
        {
            getHostByXml();
        }
    }
    
    /**
    * @Description Alarm host information according to json look up
    * @param null
    * @return void
    */    
    
    public void getHostByJson()
    {
        JSONObject jsonRet = null;
        boolean bException = false;
        String strHostList="";
        try {
            String url = "";
            if(HttpsClientUtil.bHttpsEnabled)
            {
                 url = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/Event/notification/httpHosts?format=json";
                //Send the request
                 jsonRet = new JSONObject(HttpsClientUtil.httpsGet(url));
            }
            else
            {
                 url = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/Event/notification/httpHosts?format=json";
                //Send the request
                 jsonRet = new JSONObject(HTTPClientUtil.doGet(url));
            }
            //Only the HttpHostNotification field is extracted and returned
            strHostList=jsonRet.getString("HttpHostNotification");
            
            }catch (Exception e){
                bException = true;    
                setAttr("errorMsg",e.toString());
            }               
            finally
            {
                if(bException)
                {
                    //Exception returns exception information directly
                    this.renderJson();
                }
                else
                {
                    //Normal interaction, return the status information returned by the device
                    this.renderJson(strHostList);  //jsonRet.toString()
                }
            }
    }

    /**
    * @Description Alarm host information according to XML look up
    * @param null
    * @return void
    */    
    public void getHostByXml()
    {
        boolean bException=false;
        String XMLRet="";
        JSONArray HttpHostArry=new JSONArray();
        try {
            String url = "";
            if(HttpsClientUtil.bHttpsEnabled)
            {
                 url = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/Event/notification/httpHosts";
                //Send the request
                 XMLRet = HttpsClientUtil.httpsGet(url);
            }
            else
            {
                 url = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/Event/notification/httpHosts";
                //Send the request
                 XMLRet = HTTPClientUtil.doGet(url);
            }
            //XML to Json
            SAXReader saxReader = new SAXReader();             
            Document document = saxReader.read(new ByteArrayInputStream(XMLRet.getBytes("UTF-8"))); 
            
               Element m_root=document.getRootElement();
               List<Element> childElements = m_root.elements();
            
            for (Element child : childElements) 
            {    //The loop outputs all the information
                 JSONObject single = new JSONObject();
                 List<Element> childSubitems = child.elements();
                 
                 //Get the HttpHostNotification node
                 if(child.getName().equals("HttpHostNotification"))
                 {
                     //The loop resolves the field in HttpHostNotification
                     for (Element childSubitem : childSubitems) 
                     {
                         String name = childSubitem.getName();//Gets the current element name
                         
                         if(name=="id")
                         {
                             single.put("id", childSubitem.getText());
                         }
                         else if(name=="url")
                         {
                             single.put("url", childSubitem.getText());
                         }
                         else if(name=="ipAddress")
                         {
                             single.put("ipAddress", childSubitem.getText());
                         }
                         else if(name=="portNo")
                         {
                             single.put("portNo", childSubitem.getText());
                         }
                         else if(name=="eventType")
                         {
                             single.put("eventType", childSubitem.getText());
                         }
                        
                     }
                     
                     HttpHostArry.put(single);
                 }
                     
                }
            //XML.toJSONObject(XMLRet.replace("<xml>", "").replace("</xml>", ""));
            //XMLSerializer xmlSerializer = new XMLSerializer();
            
            }catch (Exception e){
                //Setting exception messages
                bException = true;    
                setAttr("errorMsg",e.toString());
            }               
            finally
            {
                if(bException)
                {
                    //Exception returns exception information directly
                    this.renderJson();
                }
                else
                {
                    //Normal interaction, return the status information returned by the device
                    this.renderJson(HttpHostArry.toString());  //jsonRet.toString()
                }
            }
    }
    
    /**
    * @Description Delete alarm host information according to id
    * @param null
    * @return void
    */    
    public void  delHost(){
        String parameterFormatType=getPara("parameterFormatType");
        if(parameterFormatType.equals("json")) 
        {
            delHostByJson();
        }
        else //"xml"==parameterFormatType
        {
            delHostByXml();
        }
    }

    //According to the id, use XML to transfer data and delete the alarm host
    public void  delHostByJson()
    {
        int id = getParaToInt("id");
        
        //new  json object
        JSONObject jsonRet = null;
        boolean bException = false;
         
        try{       
            String strUrl = "";
            if(HttpsClientUtil.bHttpsEnabled)
            {
                //Assemble the URL     
                strUrl= "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/Event/notification/httpHosts?format=json&ID=" + id;
                jsonRet = new JSONObject(HttpsClientUtil.httpsDelete(strUrl));                                  
            } 
            else
            {
                //Assemble the URL     
                strUrl= "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/Event/notification/httpHosts?format=json&ID=" + id;
                jsonRet = new JSONObject(HTTPClientUtil.doDelete(strUrl));      
            }
           
         }catch (Exception e){
                //Setting exception messages
                bException = true;            
                setAttr("errorMsg",e.toString());               
        }               
        finally
        {
            if(bException)
            {
                //Exception returns exception information directly
                this.renderJson();
            }
            else
            {
                //Normal interaction, return the status information returned by the device
                this.renderJson(jsonRet.toString());
            }
                           
        }                    
    }
    
    
    //According to the id, use XML to transfer data and delete the alarm host
    public void  delHostByXml()
    {
        int id = getParaToInt("id");
        boolean bException = false;
        String XMLRet = "";
        JSONObject jsonRet = new JSONObject();
        try
        {
            String strUrl = "";
            if(HttpsClientUtil.bHttpsEnabled)
            {
                //Assemble the URL     
                strUrl= "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/Event/notification/httpHosts/" + id;
                XMLRet = HttpsClientUtil.httpsDelete(strUrl);                                  
            } 
            else
            {
                //Assemble the URL     
                strUrl= "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/Event/notification/httpHosts/" + id;
                XMLRet = HTTPClientUtil.doDelete(strUrl);      
            }
            //XML to Json
            SAXReader saxReader = new SAXReader();             
            Document document = saxReader.read(new ByteArrayInputStream(XMLRet.getBytes("UTF-8"))); 
            
               Element m_root=document.getRootElement();
               List<Element> childElements = m_root.elements();
               
               //The loop outputs status information
            for (Element child : childElements) 
            {
                 String name = child.getName();//Gets the current element name
                 
                 if(name=="statusCode")
                 {
                     jsonRet.put("statusCode", child.getText());
                 }
                 else if(name=="statusString")
                 {
                     jsonRet.put("statusString", child.getText());
                 }
            }
            
        }catch(Exception e){
            bException = true;            
            setAttr("errorMsg",e.toString());    
        }finally
        {
            if(bException)
            {
                //Exception returns exception information directly
                this.renderJson();
            }
            else
            {
                //Normal interaction, return the status information returned by the device
                this.renderJson(jsonRet.toString());
            }
                           
        }        
        
        
    }
    
/**
* @Description Modify alarm host information according to id
* @param null
* @return void
*/    
public void  modifyHost(){
    String parameterFormatType=getPara("parameterFormatType");
    if(parameterFormatType.equals("json")) 
    {
        modifyHostByJson();
    }
    else //"xml"==parameterFormatType
    {
        modifyHostByXml();
    }
    
}

//Modify the alarm host information data transmission type as json according to the id
public void modifyHostByJson(){
    boolean bException=false;
    
    //Gets the field passed by the front end
    int id = getParaToInt("id");
    String host = getPara("host");
    String url = getPara("url");
    String portNo = getPara("portNo");
    String eventType= getPara("eventType");
    
    //new  json object
     JSONObject jsonData = new JSONObject();
     JSONObject jsonRet = null;
     
     try{       
        //Assemble json data
         jsonData.put("url",url);
         jsonData.put("portNo",Integer.parseInt(portNo));
         jsonData.put("protocolType", "HTTP");
         jsonData.put("parameterFormatType", "json");
         jsonData.put("addressingFormatType", "ipaddress");  //hostname
         jsonData.put("ipAddress",host);
         jsonData.put("eventType", eventType);//"alarmResult,captureResult");
         jsonData.put("httpAuthenticationMethod", "none");
         
         JSONObject jsonData1 = new JSONObject();
         jsonData1.put("HttpHostNotification", jsonData);
         
         String strUrl = "";
         if(HttpsClientUtil.bHttpsEnabled)
         {
            //Assemble the URL     
              strUrl= "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/Event/notification/httpHosts?format=json&ID=" + id;
              jsonRet = new JSONObject(HttpsClientUtil.httpsPut(strUrl, jsonData1.toString()));                                  
         } 
         else
         {
            //Assemble the URL     
              strUrl= "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/Event/notification/httpHosts?format=json&ID=" + id;
              jsonRet = new JSONObject(HTTPClientUtil.doPut(strUrl, jsonData1.toString()));      
         }
       
     }catch (Exception e){
          
            bException = true;            
            setAttr("errorMsg",e.toString());               
        }               
        finally
        {
            if(bException)
            {
                //Exception returns exception information directly
                this.renderJson();
            }
            else
            {
                //Normal interaction, return the status information returned by the device
                this.renderJson(jsonRet.toString());
            }
                           
        }                    
    
}

//Modify alarm host information data transmission type as XML according to id
public void modifyHostByXml(){
    
    //Get host info
    String host = getPara("host");
    String url = getPara("url");
    String eventType=getPara("eventType");
    String portNo = getPara("portNo");
    
    //Assemble XML data
    String inboundData="<HttpHostNotification>"
            + "<id>" + 1 + "</id>"
             + "<url>" + url + "</url>"
             + "<protocolType>HTTP</protocolType>"
             + "<parameterFormatType>XML</parameterFormatType>"
             + "<addressingFormatType>ipaddress</addressingFormatType>"
             + "<ipAddress>" + host + "</ipAddress>"
             + "<portNo>" + portNo + "</portNo>"
             + "<httpAuthenticationMethod>none</httpAuthenticationMethod>"
             + "</HttpHostNotification>";
    String XMLRet="";
    JSONObject jsonRet = new JSONObject();
    boolean bException = false;
    try{
        String strUrl = "";
        if(HttpsClientUtil.bHttpsEnabled)
        {
            //Assemble the URL     
            strUrl= "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/Event/notification/httpHosts";
            XMLRet = HttpsClientUtil.httpsPut(strUrl, inboundData);                                  
        } 
        else
        {
            //Assemble the URL     
            strUrl= "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/Event/notification/httpHosts";
            XMLRet = HTTPClientUtil.doPut(strUrl, inboundData);      
        }
        //xml to json
        SAXReader saxReader = new SAXReader();             
        Document document = saxReader.read(new ByteArrayInputStream(XMLRet.getBytes("UTF-8"))); 
             
        Element m_root=document.getRootElement();
        List<Element> childElements = m_root.elements();
        //The loop parses the status information     
        for (Element child : childElements) 
        {
            if(child.getName().equals("statusCode"))
            {
                jsonRet.put("statusCode",child.getText());         
            }
            else if(child.getName().equals("statusString"))
            {
                 jsonRet.put("errorMsg", child.getText());
            }
            
        }
        }catch (Exception e){
            bException = true;            
            setAttr("errorMsg",e.toString());               
        }               
        finally
        {
            if(bException)
            {
                //Exception returns exception information directly
                this.renderJson();
            }
            else
            {
                //Normal interaction, return the status information returned by the device
                this.renderJson(jsonRet.toString());
            }
                           
        }    
    }    

}
