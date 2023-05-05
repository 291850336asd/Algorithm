package com.test.hiki.alarm;

import com.alibaba.fastjson.JSON;
import com.jfinal.core.Controller;

import com.test.hiki.communicationCom.HTTPClientUtil;
import com.test.hiki.communicationCom.HttpAysncClientUtil;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.Event;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Guard extends Controller{		
	public void index() {
		render("/UI-Resource/Alarm/Guard.html");	
		ip=HttpAysncClientUtil.strIP;
		port=HttpAysncClientUtil.iPort;		
	}
	
	public static int port;
	public static String ip;
	public static String Url;
	public static String event;
	public static boolean subscribe=false;
	private LongLinkThread llink=new LongLinkThread();
	private static List<String>eventlist=new ArrayList<String>();
	
	//Alarm guard function
	public void startAlarmGuard()
	{
		try{	
		    //Get subscription events
		    event=getPara("event");
		    //Get subscription flag
			subscribe=getParaToBoolean("subscribe");
			if(subscribe)
			{ 
			    //Alarm subscription
			    Url="http://"+ip+":"+port+"/ISAPI/Event/notification/subscribeEvent";   
			}else{				
			    Url="http://"+ip+":"+port+"/ISAPI/Event/notification/alertStream";
			    setAttr("returnData", "success");
			}
			//Set up alarm guard thread
			Thread thread =new Thread(llink);
			thread.start();
			
		}catch(Exception e)
		{
			setAttr("returnData","failed");
			e.printStackTrace();
		}finally{
			renderJson();
		}		
	}
		
	//Thread for alarm guard
	public static class LongLinkThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub	           
			HttpAysncClientUtil.LonLink(Url, event, subscribe);    
		}
	}	
	
	//Alarm removal function
	public void stopAlarmGuard() throws IOException
    {  	
    	try{
    	    //Terminate long connection
    		HttpAysncClientUtil.StopLink();  		
    		//The data returned
    		setAttr("returnData","success");
    	}catch(Exception e){ 		
    		setAttr("returnData","failed");
    		e.printStackTrace();
    	}finally
    	{
    		renderJson();
    	}   	    	
    }
	
	//Determines that the subscription condition is loaded dynamically
	public void SubcribeEvent() {
           
	   //Alarm subscription
        String DeviceCap="http://"+ip+":"+port+"/ISAPI/System/capabilities";
        String eventCap="http://"+ip+":"+port+"/ISAPI/Event/notification/subscribeEventCap";
        JSONArray eventType=new JSONArray();
        JSONObject singletype=new JSONObject();
        JSONObject jsonType=new JSONObject();
	    try {
	        
	        if(IsSupportCap(DeviceCap)&&IsSupportCap(eventCap))
	        {
				System.out.println("eventlist");
				System.out.println(JSON.toJSONString(eventlist));
	            for(int i=0;i<eventlist.size();i++)
	            {
	                singletype.put("eventType",eventlist.get(i));	   
	                eventType.put(singletype.toString());              
	            }
	            eventlist.clear();
	        }
	        jsonType.put("event", eventType);
	        setAttr("returnData",jsonType.toString());               
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {           
//            renderJson();
        }
	   
    }
	
	//Determine the device capability set
	@SuppressWarnings("unchecked")
	public boolean IsSupportCap(String url)
	{
	    String strcap="";            
        try {   
            //HTTP communication to get the device capability set
        	strcap=HTTPClientUtil.doGet(url);
            if (!strcap.equals("")) {        
                //Capability set analysis
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(new ByteArrayInputStream(strcap.getBytes("UTF-8")));                    
                Element m_root=document.getRootElement();
                List<Element> childElements = m_root.elements();                   
                switch(m_root.getName())
                {
                case "DeviceCap":
                {
                    //Determine whether the device supports alarm guard subscription
                    for (Element child : childElements) 
                    {  
                        if(child.getName().equals("SysCap"))
                        {
                            List<Element>Syselements=child.elements();
                            for(Element e:Syselements)
                            {
                                if (e.getName().equals("isSupportSubscribeEvent")&&e.getText().equals("true")){
                                    return true;
                                }
                            }                           
                        }
                    }   
                }
                case "SubscribeEventCap":
                {
                    //Determine the supported alarm guard subscription event type
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
                                    return true;
                                }
                                break;       
                            }  
                            break;
                        }
                    }     
                }    
                }                     
            }  
        }catch (Exception e) {
        	e.printStackTrace();
        }
        return false;   
	}	         
}

