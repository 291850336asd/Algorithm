package com.test.hiki.fdlib;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jfinal.core.Controller;


import com.test.hiki.communicationCom.HTTPClientUtil;
import com.test.hiki.communicationCom.HttpsClientUtil;
public class FDLib extends Controller{
	
	public void index() {
		render("/UI-Resource/FDLib/FDLibForm.html");
	}

/**
* @Description Get face database information
* @param null
* @return void
*/	
	public void getFDLib()
	{
		
	 JSONObject jsonRet = null;
	 String strFDLib = "";
	try {
        
		//Assemble the XML
        String url = "";
		if(HttpsClientUtil.bHttpsEnabled)
		{
			 url = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/Intelligent/FDLib?format=json";
			//Send the request
			 jsonRet = new JSONObject(HttpsClientUtil.httpsGet(url));
		}
		else
		{
			 url = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/Intelligent/FDLib?format=json";
			//Send the request
			 jsonRet = new JSONObject(HTTPClientUtil.doGet(url));
		}
		
        //To ensure that the data in the table will be displayed normally, only data after the FDLib node is returned
        if(parseErrorStatus(jsonRet))
        {
        	strFDLib = jsonRet.getString("FDLib");
        }
	    }catch (Exception e){
	    	 setAttr("errorMsg",e.toString());
	    }               
	    finally
	    {
		    if(parseErrorStatus(jsonRet))
		    {	  
		    	this.renderJson(strFDLib);
		    }
		    else
		    {
		    	this.renderJson();
		    	 
		    }
	    }
	}
	
	/**
	* @Description Add face database information
	* @param null
	* @return void
	*/	
		public void addFDLib()
		{
			//Get input parameters
			String strName = getPara("name");
			String strCustomInfo = getPara("customInfo");

			 boolean bException = false;
			
			JSONObject jsonRet = null;
			try {
				//Assemble json data
				JSONObject jsonFDlib = new JSONObject();
				jsonFDlib.put("faceLibType", "blackFD");//Default blacklist library
				jsonFDlib.put("name", strName);
				jsonFDlib.put("customInfo", strCustomInfo);
				
				//Assemble the XML
		        String url = "";
				if(HttpsClientUtil.bHttpsEnabled)
				{
					 url = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/Intelligent/FDLib?format=json";
					//Send the request
					 jsonRet = new JSONObject(HttpsClientUtil.httpsPost(url,jsonFDlib.toString()));
				}
				else
				{
					 url = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/Intelligent/FDLib?format=json";
					//Send the request
					 jsonRet = new JSONObject(HTTPClientUtil.doPost(url,jsonFDlib.toString()));
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
	* @Description Modify face database information
	* @param null
	* @return void
	*/	
		public void modifyFDLib()
		{
			//Get input parameters
			String strFDID= getPara("FDID");
			String strName = getPara("name");
			String strCustomInfo = getPara("customInfo");

			 boolean bException = false;
			
			JSONObject jsonRet = null;
			try {
				//Assemble json data
				JSONObject jsonFDlib = new JSONObject();
				jsonFDlib.put("faceLibType", "blackFD");//Default blacklist library
				jsonFDlib.put("name", strName);
				jsonFDlib.put("customInfo", strCustomInfo);
				
				//Assemble the XML
		        String url = "";
				if(HttpsClientUtil.bHttpsEnabled)
				{
					 url = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/Intelligent/FDLib?format=json&FDID=" + strFDID + "&faceLibType=blackFD";
						//Send the request
					 jsonRet = new JSONObject(HttpsClientUtil.httpsPut(url,jsonFDlib.toString()));
				}
				else
				{
					 url = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/Intelligent/FDLib?format=json&FDID=" + strFDID + "&faceLibType=blackFD";
						//Send the request
					 jsonRet = new JSONObject(HTTPClientUtil.doPut(url,jsonFDlib.toString()));
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
		* @Description Delete face database information
		* @param null
		* @return void
		*/	
			public void delFDLib()
			{
				//Get input parameters
				String strFDID= getPara("FDID");	        
				 boolean bException = false;
				
				JSONObject jsonRet = null;
				try {				
					//Assemble the URL
			        String url = "";
					if(HttpsClientUtil.bHttpsEnabled)
					{
						 url = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/Intelligent/FDLib?format=json&FDID=" + strFDID + "&faceLibType=blackFD";
						//Send the request	
						 jsonRet = new JSONObject(HttpsClientUtil.httpsDelete(url));
					}
					else
					{
						 url = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/Intelligent/FDLib?format=json&FDID=" + strFDID + "&faceLibType=blackFD";
						//Send the request	
						 jsonRet = new JSONObject(HTTPClientUtil.doDelete(url));
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
	* @Description Query face record
	* @param null
	* @return void
	*/	
	public void searchFDLib()
	{	
		int searchType =  getParaToInt("searchType");
		int searchResultPosition =  getParaToInt("searchResultPosition");
		int maxResults =  getParaToInt("maxResults");
		String faceLibType =  getPara("faceLibType");
		String strFDID= getPara("FDID");
		String strFPID = "";
		String name = "";
		String startTime  = "";
		String endTime = "";
		String gender = "";
		String certificateType = "";
		String certificateNumber = "";
		 boolean bException = false;
		if(searchType == 1)
		{
			 strFPID = getPara("FPID");
		}
		else if(searchType == 2)
		{
			 name = getPara("name");
			 startTime  = getPara("startTime");
			 endTime =getPara("endTime");
			 gender = getPara("gender");
			 certificateType = getPara("certificateType");
			 certificateNumber = getPara("certificateNumber");
		}	
	
		String strUrl= "";
		JSONObject jsonRet = null;		
		try {
			//Assemble the send request json message
			JSONObject jsonFacePicSearch = new JSONObject();
			jsonFacePicSearch.put("searchResultPosition", searchResultPosition);
			jsonFacePicSearch.put("maxResults", maxResults);
			jsonFacePicSearch.put("faceLibType", faceLibType);
			jsonFacePicSearch.put("FDID", strFDID);
			if(searchType == 1)
			{
				jsonFacePicSearch.put("FPID", strFPID);
			}
			else if(searchType == 2)
			{
				jsonFacePicSearch.put("name", name);
				jsonFacePicSearch.put("startTime", startTime);
				jsonFacePicSearch.put("endTime", endTime);
				jsonFacePicSearch.put("gender", gender);
			
				jsonFacePicSearch.put("certificateType", certificateType);
				jsonFacePicSearch.put("certificateNumber", certificateNumber);
				
			}

			if(HttpsClientUtil.bHttpsEnabled)
			{
				strUrl= "https://" + HttpsClientUtil.strIP +  ":" + HttpsClientUtil.iPort + "/ISAPI/Intelligent/FDLib/FDSearch?format=json";
				jsonRet = new JSONObject( HttpsClientUtil.httpsPost(strUrl, jsonFacePicSearch.toString()));
			}
			else
			{
				strUrl= "http://" + HTTPClientUtil.strIP +  ":" + HTTPClientUtil.iPort + "/ISAPI/Intelligent/FDLib/FDSearch?format=json";
				jsonRet = new JSONObject( HTTPClientUtil.doPost(strUrl, jsonFacePicSearch.toString()));
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
	* @Description Parse error status
	* @param filePath
	* @return file
	*/		
	public  boolean parseErrorStatus(JSONObject jsonData)
	{ 
		try{
			//Get error code
			int errCode = jsonData.getInt("errorCode");
			//Check error code
			if(errCode != 1)
			{		
				//Get error message
				String errMsg = jsonData.getString("errorMsg");
				//Set to return json data
				setAttr("result",0);
				setAttr("errorMsg",errMsg);
				return false;
			}
			else
			{
				//Success status returns 1
				setAttr("result",1);
				return true;
				
			}         
		}
		catch(Exception e){
			 e.printStackTrace();
			 return false;
		}
	}
	
	/**
	* @Description add Face Record
	* @param null
	* @return void
	*/	
	public void addFaceRecord()
	{	
		//Get the add face record parameter
		String faceURL =  getPara("faceURL");
		String faceLibType= getPara("faceLibType");
		String strFDID= getPara("FDID");
		String name= getPara("name");
		String gender =  getPara("gender");
		String bornTime= getPara("bornTime");
		String certificateType = getPara("certificateType ");
		String certificateNumber= getPara("certificateNumber");
		String customInfo= getPara("customInfo");
		String tag= getPara("tag");
		boolean bException = false;
		//Assemble the URL
		String strUrl= "";
		JSONObject jsonRet = null;	
		
		try {
			//Assemble the send request json message
			JSONObject jsonAddFaceRecord = new JSONObject();
			jsonAddFaceRecord.put("faceURL", faceURL);
			jsonAddFaceRecord.put("faceLibType", faceLibType);
			jsonAddFaceRecord.put("name", name);
			jsonAddFaceRecord.put("FDID", strFDID);
			jsonAddFaceRecord.put("gender", gender);
			jsonAddFaceRecord.put("bornTime", bornTime);
			jsonAddFaceRecord.put("certificateType", certificateType);
			jsonAddFaceRecord.put("certificateNumber", certificateNumber);
			jsonAddFaceRecord.put("customInfo", customInfo);
			jsonAddFaceRecord.put("tag", tag);
			//Send a request to add a face record
			if(HttpsClientUtil.bHttpsEnabled)
			{
				strUrl= "https://" + HttpsClientUtil.strIP +  ":" + HttpsClientUtil.iPort + "/ISAPI/Intelligent/FDLib/FaceDataRecord?format=json";
				jsonRet = new JSONObject( HttpsClientUtil.httpsPost(strUrl, jsonAddFaceRecord.toString()));
			}
			else
			{
				strUrl= "http://" + HTTPClientUtil.strIP +  ":" + HTTPClientUtil.iPort + "/ISAPI/Intelligent/FDLib/FaceDataRecord?format=json";
				jsonRet = new JSONObject( HTTPClientUtil.doPost(strUrl, jsonAddFaceRecord.toString()));
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
	* @Description Modify face record
	* @param null
	* @return void
	*/	
	public void modifyFaceRecord()
	{	
		//Get the add face record parameter
		String strFPID= getPara("FPID");
		String faceURL =  getPara("faceURL");
		String faceLibType= getPara("faceLibType");
		String strFDID= getPara("FDID");
		String name= getPara("name");
		String gender =  getPara("gender");
		String bornTime= getPara("bornTime");
		String certificateType = getPara("certificateType");
		String certificateNumber= getPara("certificateNumber");
		String tag= getPara("tag");
		String caseInfo= getPara("caseInfo");
		boolean bException = false;
		//Assemble the URL
		String strUrl= "";
		JSONObject jsonRet = null;	
		
		try {
			//Assemble the send request json message
			JSONObject jsonModifyFaceRecord = new JSONObject();
			jsonModifyFaceRecord.put("faceURL", faceURL);
			jsonModifyFaceRecord.put("faceLibType", faceLibType);
			jsonModifyFaceRecord.put("name", name);
			jsonModifyFaceRecord.put("gender", gender);
			jsonModifyFaceRecord.put("bornTime", bornTime);
			jsonModifyFaceRecord.put("certificateType", certificateType);
			jsonModifyFaceRecord.put("certificateNumber", certificateNumber);
			jsonModifyFaceRecord.put("caseInfo", caseInfo);
			jsonModifyFaceRecord.put("tag", tag);
			//Send a request to modify the face record
			if(HttpsClientUtil.bHttpsEnabled)
			{

				 strUrl= "https://" + HttpsClientUtil.strIP +  ":" + HttpsClientUtil.iPort + 
						 "/ISAPI/Intelligent/FDLib/FDSearch?format=json&FDID="+strFDID +  "&FPID=" +strFPID + "&faceLibType=" + faceLibType;
				jsonRet = new JSONObject( HttpsClientUtil.httpsPut(strUrl, jsonModifyFaceRecord.toString()));
			}
			else
			{
				 strUrl= "http://" + HTTPClientUtil.strIP +  ":" + HTTPClientUtil.iPort + 
						 "/ISAPI/Intelligent/FDLib/FDSearch?format=json&FDID="+strFDID +  "&FPID=" +strFPID + "&faceLibType=" + faceLibType;
				jsonRet = new JSONObject( HTTPClientUtil.doPut(strUrl, jsonModifyFaceRecord.toString()));
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
	* @Description Delete face record
	* @param null
	* @return void
	*/	
		public void delFaceRecord()
		{
			//Get input parameters
			String strFDID= getPara("FDID");
			String strFPID= getPara("FPID");
			String faceLibType= getPara("faceLibType");
	        boolean bException = false;
			
			JSONObject jsonRet = null;
			try {
				//Assemble json data
				JSONObject jsonDelFPID = new JSONObject();
				JSONArray jsonArray = new JSONArray();
				HashMap<String, Object> map = new HashMap<String,Object>();
				map.put("value", strFPID);
				jsonArray.put(0, map);
				//Json array
				jsonDelFPID.put("FPID", jsonArray);
				//Assemble the XML
		        String url = "";
				if(HttpsClientUtil.bHttpsEnabled)
				{
					 url = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort +
							 "/ISAPI/Intelligent/FDLib/FDSearch/Delete?format=json&FDID=" + strFDID  +"&faceLibType=" + faceLibType;
					 jsonRet = new JSONObject(HttpsClientUtil.httpsPut(url,jsonDelFPID.toString()));
				}
				else
				{
					 url = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort +
							 "/ISAPI/Intelligent/FDLib/FDSearch/Delete?format=json&FDID=" + strFDID  +"&faceLibType=" + faceLibType;
					 jsonRet = new JSONObject(HTTPClientUtil.doPut(url,jsonDelFPID.toString()));
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

