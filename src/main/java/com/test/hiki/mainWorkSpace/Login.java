package com.test.hiki.mainWorkSpace;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.nio.client.HttpAsyncClient;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.jfinal.core.Controller;

import com.test.hiki.alarm.Guard;
import com.test.hiki.communicationCom.HTTPClientUtil;
import com.test.hiki.communicationCom.HttpAysncClientUtil;
import com.test.hiki.communicationCom.HttpsClientUtil;

public class Login   extends Controller{
	public void index() {
		render("/UI-Resource/Login/Login.html");
	}


	public static void main(String[] args) {
		try {
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", "HKtest123");
			HTTPClientUtil.client.getState().setCredentials(AuthScope.ANY, creds);
			HTTPClientUtil.client.getHostConfiguration().setHost("192.168.5.201", 80);
//			String url = "/ISAPI/Security/userCheck";
			String url = "/ISAPI/Event/notification/subscribeEventCap";
			String strOut= HTTPClientUtil.doGet(url);
			System.out.println(strOut);
			SAXReader saxReader = new SAXReader();

			Document document = saxReader.read(new ByteArrayInputStream(strOut.getBytes("UTF-8")));
			Element employees=document.getRootElement();

			List<Element> childElements = employees.elements();
			List<String> eventlist = new ArrayList<>();
			for (Element child : childElements)
			{
				if(child.getName().equals("EventList"))
				{
					List<Element>EventList=child.elements();
					for(Element event:EventList)
					{
						if (event.getName().equals("Event")){
							List<Element>Event=event.elements();
							for(Element type:Event)
							{
								if(type.getName().equals("type"))
								{
									eventlist.add(type.getText());
								}
							}
						}
						break;
					}
					break;
				}
			}
			System.out.println("eventlist:"+ eventlist.toString());
//			boolean bResult = false;
//			System.out.println("------------------");
//			for(Iterator i = employees.elementIterator(); i.hasNext();){
//
//				Element employee = (Element) i.next();
//
//				if(employee.getName() == "statusValue" && 0 ==  employee.getText().compareTo("200")){
//					bResult=true;
//				}
//			}
//
//			System.out.println(bResult);


//			Guard guard = new Guard();
//			Guard.ip = "192.168.5.201";
//			Guard.port=80;
//			guard.startAlarmGuard();


			try{
				HttpAysncClientUtil.HttpAysncInit("admin","HKtest123");
				Login login = new Login();
				//Get subscription events
				//Get subscription flag
				Guard.subscribe =true;
				Guard.event = "AccessControllerEvent";
				String ip= "192.168.5.201";
				int port = 80;
				if(Guard.subscribe)
				{
					//Alarm subscription
					Guard.Url="http://"+ip+":"+port+"/ISAPI/Event/notification/subscribeEvent";
				}else{
					Guard.Url="http://"+ip+":"+port+"/ISAPI/Event/notification/alertStream";
				}
				//Set up alarm guard thread
				Guard.LongLinkThread llink= new Guard.LongLinkThread();
				Thread thread =new Thread(llink);
				thread.start();
				while (true){
					Thread.sleep(1000);
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally{
			}

		}catch (Exception e){
			e.printStackTrace();
		}
	}








	public void userCheck()
	{
		boolean bResult=false;
		
		String IP = getPara("IP");
		int port = getParaToInt("port");
		String username = getPara("username");
	    String password = getPara("password");
	    //HTTPS��ز�����ֵ
	    HttpsClientUtil.bHttpsEnabled = getParaToBoolean("httpsEnalbe");
	    HttpsClientUtil.strIP = IP;
	    HttpsClientUtil.iPort = port;
	    
	    HTTPClientUtil.strIP = IP;
	    HTTPClientUtil.iPort = port;
	       
	    HttpAysncClientUtil.strIP=IP;
	    HttpAysncClientUtil.iPort=port;
	    HttpAysncClientUtil.HttpAysncInit(username, password);
	    
		if(HttpsClientUtil.bHttpsEnabled)
		{	    
			HttpsClientUtil.httpsClientInit(IP, username, password);		
		}
		else
		{
	        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);          
	        HTTPClientUtil.client.getState().setCredentials(AuthScope.ANY, creds);
	        HTTPClientUtil.client.getHostConfiguration().setHost(IP, port);
		}    
		
        try {
        	String strOut =  "";
        	String url = "";
        	if(HttpsClientUtil.bHttpsEnabled)
			{
        		url = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort+"/ISAPI/Security/userCheck";
				strOut = HttpsClientUtil.httpsGet(url);    				
			}
			else 
			{
				url = "/ISAPI/Security/userCheck";
				strOut= HTTPClientUtil.doGet(url);
			}
			
			SAXReader saxReader = new SAXReader(); 
			
			Document document = saxReader.read(new ByteArrayInputStream(strOut.getBytes("UTF-8"))); 
       	    Element employees=document.getRootElement(); 
       	 
       	    for(Iterator i = employees.elementIterator(); i.hasNext();){
       	    	
           	    Element employee = (Element) i.next(); 	     
	                         	    
           	    if(employee.getName() == "statusValue" && 0 ==  employee.getText().compareTo("200")){
           	    	bResult=true;
	            }
       	    }
			
		} catch (Exception e) {
			
		}
        finally
        {
    	    if(bResult)
    	    {	    	    	
    	   	    setAttr("loginResult",1);
    	    }
    	    else
    	    {
    	    	setAttr("loginResult",0);
    	    }
    	     
    	    this.renderJson();
        }
        		    	     
	}
}
