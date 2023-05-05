package com.test.hiki.config;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.dom4j.Document;
import org.dom4j.Element;
import com.jfinal.core.Controller;
import com.test.hiki.communicationCom.HTTPClientUtil;
import com.test.hiki.communicationCom.HttpsClientUtil;

public class Config extends Controller{
	
	public void index() {
		setAttr("jsonStr", FileUtil.GetJSONInfo());
		render("/UI-Resource/Config/Config.html");
	}
	
	public void getDataFromDev()
	{	
		String returnData="";
        try 
        {
    		String url = getPara("url");
    		String strmethod = getPara("method");
    	    String inboundData = getPara("inboundData");
    	    //HTTPS interaction
    	    if(HttpsClientUtil.bHttpsEnabled)
	    	{
    	    	url = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort+ url;
    	    	 if(strmethod.equals("GET"))
	             {
	                 returnData=HttpsClientUtil.httpsGet(url);					
	             }
	             else if(strmethod.equals("PUT"))
	             {
	             	returnData=HttpsClientUtil.httpsPut(url, inboundData);
	             }
	             else if(strmethod.equals("POST"))
	             {
	             	returnData=HttpsClientUtil.httpsPost(url, inboundData);
	             }
	             else if(strmethod.equals("DELETE"))
	             {
	             	returnData=HttpsClientUtil.httpsDelete(url);
	             }
	    	}
    	    //HTTPS interaction
	    	else
	    	{
	    		 if(strmethod.equals("GET"))
	             {
	                 returnData=HTTPClientUtil.doGet(url);					
	             }
	             else if(strmethod.equals("PUT"))
	             {
	             	returnData=HTTPClientUtil.doPut(url, inboundData);
	             }
	             else if(strmethod.equals("POST"))
	             {
	             	returnData=HTTPClientUtil.doPost(url, inboundData);
	             }
	             else if(strmethod.equals("DELETE"))
	             {
	             	returnData=HTTPClientUtil.doDelete(url);
	             }
	    	}
           
			
		} catch (Exception e) {
			
		}
        finally
        {	    	    	
    	   	setAttr("returnData",returnData);    	     
    	    this.renderJson();
        }
        		    	     
	}

}
