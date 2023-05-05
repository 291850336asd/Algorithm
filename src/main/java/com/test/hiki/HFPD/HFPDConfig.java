package com.test.hiki.HFPD;

import org.json.JSONObject;

import com.jfinal.core.Controller;

import com.test.hiki.commonFunction.JsonFormatTool;
import com.test.hiki.communicationCom.HTTPClientUtil;
import com.test.hiki.communicationCom.HttpsClientUtil;

public class HFPDConfig extends Controller{

	public void index() {
		render("/UI-Resource/HFPD/HFPDConfig.html");	
	}
	
	public void PutConfig()
	{
		String enablebuf=getPara("enabled");
		boolean enabled=false;
		if(enablebuf.equals("true"))
		{
			enabled=true;
		}
		int analysisDays=Integer.valueOf(getPara("analysisDays"));
		int occurrences=Integer.valueOf(getPara("occurrences"));
		int similarity=Integer.valueOf(getPara("similarity"));	
		int captureTimeInterval=Integer.valueOf(getPara("captureTimeInterval"));
		String facelibrary=getPara("facelibrary");
		System.out.println(facelibrary);
		
		try
		{
			JSONObject HFPDJson=new JSONObject();
			HFPDJson.put("enabled",enabled);
			HFPDJson.put("analysisDays", analysisDays);
			HFPDJson.put("occurrences", occurrences);
			HFPDJson.put("similarity", similarity);
			HFPDJson.put("captureTimeInterval", captureTimeInterval);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(analysisDays);
		
		
		String returnData="hello";
		System.out.println("hello");
		setAttr("returnData",returnData);    	     
	    this.renderJson();
	}
	public void GetConfig()
	{
		String returnData="";
		try{
			
			String url = getPara("url");
			
			if(HttpsClientUtil.bHttpsEnabled)
	    	{
		    	url = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort+ url;
		    	returnData=HttpsClientUtil.httpsGet(url);					
	    	}
		    //HTTP interaction
	    	else
	    	{
	             returnData=HTTPClientUtil.doGet(url);					
	    	}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			JsonFormatTool json=new JsonFormatTool();
			returnData=json.formatJson(returnData);
			setAttr("returnData",returnData);    	     
		    this.renderJson();
		}
		
	}
}
