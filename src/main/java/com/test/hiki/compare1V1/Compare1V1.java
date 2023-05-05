package com.test.hiki.compare1V1;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONObject;

import com.jfinal.core.Controller;

import com.test.hiki.communicationCom.HTTPClientUtil;
import com.test.hiki.communicationCom.HttpsClientUtil;
import com.test.hiki.commonFunction.CommonFunction;
public class Compare1V1 extends Controller{
	
	public void index() {
		//Return to the HTML file and load the control
		render("/UI-Resource/compare1V1/compare1V1.html");
	}
	/**
	* @Description Get the image URL
	* @param null
	* @return void
	*/	
	public void getPicUrl()
	{
		boolean bException=false;
		//Get image data
		String strPic = getPara("picFile");
		//new  json object
         JSONObject jsonData = new JSONObject();
         JSONObject jsonRet = null;
         
         try{       
        	          	 
	        //Assemble json data
             jsonData.put("FDID","1");
             jsonData.put("storageType","dynamic");
             
             String strUrl = "";
             //Upload images to the server          
             if(HttpsClientUtil.bHttpsEnabled)
             {
            	//Assemble the URL  
                  strUrl= "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/Intelligent/uploadStorageCloud?format=json";
            	  jsonRet = new JSONObject(HttpsClientUtil.doPostStorageCloud(strUrl, jsonData.toString(),strPic,"---------------------------------7e13971310878"));            	              	
             } 
             else
             {
            	//Assemble the URL  
                  strUrl= "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/Intelligent/uploadStorageCloud?format=json";
            	  jsonRet = new JSONObject(HTTPClientUtil.doPostStorageCloud(strUrl, jsonData.toString(),strPic,"---------------------------------7e13971310878"));      
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
* @Description Picture 1v1 comparison
* @param null
* @return void
*/	
public void constrast()
{
	boolean bException = false;
	 JSONObject jsonRet = null;
	try {
        //Get the target image URL and compare the image URL
        String url = "";
        String strTargetURL = getPara("targetURL");
        String strConstrastURL = getPara("constrastURL");

       //Assemble the sent json data
        JSONObject jsonCompare = new JSONObject();
        JSONObject TargetImage = new JSONObject();
        JSONObject ContrastImage = new JSONObject();
        jsonCompare.put("dataType", "URL");
        TargetImage.put("URL", strTargetURL);                 
        ContrastImage.put("URL", strConstrastURL);                  
        jsonCompare.put("TargetImage", TargetImage);
        jsonCompare.put("ContrastImage", ContrastImage);
        
       //send data
        if(HttpsClientUtil.bHttpsEnabled)
        {
            //Assemble the XML
        	 url = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/Intelligent/imagesComparision/face";
        	jsonRet = new JSONObject(HttpsClientUtil.httpsPost(url,jsonCompare.toString()));        	
        }   
        else
        {
            //Assemble the XML
        	 url = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/Intelligent/imagesComparision/face";
        	jsonRet = new JSONObject(HTTPClientUtil.doPost(url,jsonCompare.toString()));  
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