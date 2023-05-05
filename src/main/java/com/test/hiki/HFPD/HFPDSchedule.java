package com.test.hiki.HFPD;

import com.jfinal.core.Controller;

import com.test.hiki.communicationCom.HTTPClientUtil;
import com.test.hiki.communicationCom.HttpsClientUtil;

public class HFPDSchedule extends Controller{

	public void index() {
		render("/UI-Resource/HFPD/HFPDSchedule.html");	
	}	
	public void GetSchedule()
	{
		String returnData="";
		String url=getPara("url");
		try{
			
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
			setAttr("returnData",returnData);
			this.renderJson();
		}
	}
	
	public void PutSchedule()
	{
		String returnData="";
		String url=getPara("url");
		String inboundData=getPara("inboundData");
		try{
			
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
			setAttr("returnData",returnData);
			this.renderJson();
		}
		
	}
}
