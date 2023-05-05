package com.test.hiki.fdlib;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.json.JSONObject;

import com.jfinal.core.Controller;


import com.test.hiki.communicationCom.HTTPClientUtil;
import com.test.hiki.communicationCom.HttpsClientUtil;
public class captureLibSearchByPic extends Controller{
	
	public void index() {
		render("/UI-Resource/FDLib/captureLibSearchByPic.html");
	}
	/**
	* @Description Search for images by image
	* @param null
	* @return void
	*/	
	public void searchByPicure()
	{     
		//To obtain parameters
		int searchResultPosition =  getParaToInt("searchResultPosition");
		int maxResults =  getParaToInt("maxResults");	
		int modelMaxNum =  getParaToInt("modelMaxNum");	
		String dataType = getPara("dataType");
		String strminSimilarity = getPara("minSimilarity");
		float similarityMin =  Float.parseFloat(strminSimilarity);
		String strmaxSimilarity  = getPara("maxSimilarity");
		float similarityMax =  Float.parseFloat(strmaxSimilarity);
		//Time converted to UTC time
		Date startTime  = getParaToDate("startTime");
		Date endTime = getParaToDate("endTime");
		SimpleDateFormat timezone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		timezone.setTimeZone(TimeZone.getTimeZone("UTC"));
	
	    String gender = getPara("gender");
		String ageGroup = getPara("ageGroup");
		String smile = getPara("smile");
		String glasses = getPara("glasses");
		
		String faceURL = getPara("faceURL");
		
		 boolean bException = false;
	
		String strUrl= "";
		JSONObject jsonRet = null;		
		try {
			//Assemble the send request json message
			JSONObject jsonFacePicSearch = new JSONObject();
			jsonFacePicSearch.put("searchResultPosition", searchResultPosition);
			jsonFacePicSearch.put("maxResults", maxResults);
			jsonFacePicSearch.put("modelMaxNum", modelMaxNum);
			jsonFacePicSearch.put("similarityMin", similarityMin);	
			jsonFacePicSearch.put("similarityMax", similarityMax);
			jsonFacePicSearch.put("dataType", dataType);	
			jsonFacePicSearch.put("faceURL", faceURL);
			jsonFacePicSearch.put("startTime", timezone.format(startTime));
			jsonFacePicSearch.put("endTime", timezone.format(endTime));
			//When searching, do not restrict parameter, do not issue this parameter
			if(ageGroup.compareTo("Unlimited") != 0)
			{
				jsonFacePicSearch.put("ageGroup", ageGroup);
			}
			if(glasses.compareTo("Unlimited") != 0)
			{
				jsonFacePicSearch.put("glasses", glasses);
			}
			if(smile.compareTo("Unlimited") != 0)
			{
				jsonFacePicSearch.put("smile", smile);
			}			
			if(gender.compareTo("Unlimited") != 0)
			{
				jsonFacePicSearch.put("gender", gender);
			}
							
			if(HttpsClientUtil.bHttpsEnabled)
			{
				strUrl= "https://" + HttpsClientUtil.strIP +  ":" + HttpsClientUtil.iPort + "/ISAPI/SDT/Face/searchByPic?supportSync=true";
				jsonRet = new JSONObject( HttpsClientUtil.httpsPost(strUrl, jsonFacePicSearch.toString()));
			}
			else
			{
				strUrl= "http://" + HTTPClientUtil.strIP +  ":" + HTTPClientUtil.iPort + "/ISAPI/SDT/Face/searchByPic?supportSync=true";
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
	
}