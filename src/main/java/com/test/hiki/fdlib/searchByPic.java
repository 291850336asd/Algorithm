package com.test.hiki.fdlib;

import org.json.JSONObject;

import com.jfinal.core.Controller;


import com.test.hiki.communicationCom.HTTPClientUtil;
import com.test.hiki.communicationCom.HttpsClientUtil;
public class searchByPic extends Controller{
	
	public void index() {
		render("/UI-Resource/FDLib/searchByPic.html");
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
		float minSimilarity =  Float.parseFloat(strminSimilarity);
		String strmaxSimilarity  = getPara("maxSimilarity");
		float maxSimilarity =  Float.parseFloat(strmaxSimilarity);
		String name = getPara("name");
		String startTime  = getPara("startTime");
		String endTime = getPara("endTime");
		String gender = getPara("gender");
		String certificateType = getPara("certificateType");
		String certificateNumber = getPara("certificateNumber");
		String faceURL = getPara("faceURL");
		 boolean bException = false;
	
		String strUrl= "";
		JSONObject jsonRet = null;		
		try {
			//��װ��������json����Assemble the send request json message
			JSONObject jsonFacePicSearch = new JSONObject();
			jsonFacePicSearch.put("searchResultPosition", searchResultPosition);
			jsonFacePicSearch.put("maxResults", maxResults);
			jsonFacePicSearch.put("modelMaxNum", modelMaxNum);
			jsonFacePicSearch.put("minSimilarity", minSimilarity);	
			jsonFacePicSearch.put("maxSimilarity", maxSimilarity);
			jsonFacePicSearch.put("dataType", dataType);	
			jsonFacePicSearch.put("faceURL", faceURL);
			jsonFacePicSearch.put("name", name);
			jsonFacePicSearch.put("startTime", startTime);
			jsonFacePicSearch.put("endTime", endTime);
			jsonFacePicSearch.put("gender", gender);		
			jsonFacePicSearch.put("certificateType", certificateType);
			jsonFacePicSearch.put("certificateNumber", certificateNumber);
							
			if(HttpsClientUtil.bHttpsEnabled)
			{
				strUrl= "https://" + HttpsClientUtil.strIP +  ":" + HttpsClientUtil.iPort + "/ISAPI/Intelligent/FDLib/searchByPic?format=json";
				jsonRet = new JSONObject( HttpsClientUtil.httpsPost(strUrl, jsonFacePicSearch.toString()));
			}
			else
			{
				strUrl= "http://" + HTTPClientUtil.strIP +  ":" + HTTPClientUtil.iPort + "/ISAPI/Intelligent/FDLib/searchByPic?format=json";
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