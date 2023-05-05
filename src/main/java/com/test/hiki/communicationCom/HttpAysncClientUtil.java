package com.test.hiki.communicationCom;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;

import com.test.hiki.alarm.ParseAlarmData;

public class HttpAysncClientUtil {

	public static int  iPort = 0;
	public static String strIP="";
	public static CloseableHttpAsyncClient httpAsyncclient;	
	
	private static int reconnect=3;
	private static int timeout=10000;
	private static boolean stoplink=false;
	
	private static boolean DataRecv=false;
	private static List<Character>chBuffer=new ArrayList<Character>();
	private static ParseAlarmData AlarmData=new ParseAlarmData();
	
	//Initializes a long connection communication object
	public static void HttpAysncInit(String user,String password)
	{
		 CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
         credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
         httpAsyncclient = HttpAsyncClients.custom()
                           .setDefaultCredentialsProvider(credentialsProvider)
                           .build();       
        
	}
	//Long connection function
	public static void LonLink(String Url ,String event, boolean subscribe)
	{
		stoplink=false;
		chBuffer.clear();
		try {       		
			//Set up the callback function
            FutureCallback<Boolean> callback = new FutureCallback<Boolean>() {
                @Override
                public void cancelled() {
                    // TODO Auto-generated method stub               	
                    System.out.println("cancelled");
                }
                @Override
                public void completed(Boolean arg0) {
                    // TODO Auto-generated method stub
                    System.out.println("completed");
                }
                @Override
                public void failed(Exception arg0) {
                    // TODO Auto-generated method stub
                    System.out.println("failed");
                }               
            }; 
            //Open the connection
            httpAsyncclient.start();
            
            //Reconnect the query thread with a timeout on
            ReConnect recn=new ReConnect();
            Thread Rethread =new Thread(recn);
            Rethread.start();
            
            //Determine whether to subscribe
            if(subscribe){            	
            	HttpAsyncRequestProducer producer=null;
            	try{
            	    String requestBody;
            	    //Get subscription conditions
            	    if(event.equals("all"))
            	    {
            	        requestBody="<SubscribeEvent><format>xml</format><heartbeat>5</heartbeat><eventMode>all</eventMode><channels>all</channels></SubscribeEvent>";
            	    }else{
            	        requestBody="<SubscribeEvent><format>xml</format><heartbeat>5</heartbeat><eventMode>list</eventMode><EventList><Event><type>"+event+"</type></Event></EventList><channels>all</channels></SubscribeEvent>";
            	    }
            	    //The request message establishing the connection
            		HttpPost httpPost = new HttpPost(Url);
            		HttpEntity inboundInfoEntity = new StringEntity(requestBody, "UTF-8");
            		httpPost.setEntity(inboundInfoEntity);            	
            		producer=HttpAsyncMethods.create(httpPost);
            		//Request connection transfer
            		Future<Boolean> future = httpAsyncclient.execute(
    	                    producer,new ResponseConsumer(), callback);
            		Boolean result = future.get();
		            if (result != null && result.booleanValue()) {
		                System.out.println("Request successfully executed");
		            } else {
		                System.out.println("Request failed");
		            }
		            System.out.println("Shutting down");
		            
            	}catch(Exception e)
            	{
            		e.printStackTrace();
            	}
            	
            }else
            {
            	//connect
            	Future<Boolean> future = httpAsyncclient.execute(
	                    HttpAsyncMethods.createGet(Url),
	                    new ResponseConsumer(), callback);		                      	            
	            Boolean result = future.get();
	            if (result != null && result.booleanValue()) {
	                System.out.println("Request successfully executed");
	            } else {
	                System.out.println("Request failed");
	            }
	            System.out.println("Shutting down");
            }	            	         

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return;
	}
	
	public static void StopLink()
	{
		stoplink=true;
		DataRecv=false;
	}
	
	//Receive a message
	static class ResponseConsumer extends AsyncCharConsumer<Boolean> {

		//Message type
        public String type;
        @Override
        protected void onResponseReceived(final HttpResponse response) {
        	//Determine the message type
            System.out.println("onResponseReceived");
            String tbuf=response.toString();
            if(tbuf.contains("multipart"))
            {
            	type="multipart";
            }else if(tbuf.contains("xml"))
            {
            	type="xml";
            }else if(tbuf.contains("json"))
            {
            	type="json";
            }
        }   
        //Callback function to receive a message
        @Override
        protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl) throws IOException {
        	
        	DataRecv=true;
        	//Parsing by message type
        	if(type.equals("multipart"))
        	{
        		int length=buf.length();
            	for(int i=0;i<buf.length();i++)
            	{
            		//Fill buffer
            		chBuffer.add(buf.charAt(i));
            	}
            	//Form data parsing s
            	AlarmData.parseMultiData(chBuffer);
        	}else if(type.equals("xml"))
        	{
        	    int length=buf.length();
                for(int i=0;i<buf.length();i++)
                {
                    //Fill buffer
                    chBuffer.add(buf.charAt(i));
                }
                //Form data parsing s
                AlarmData.parseMultiData(chBuffer);
        	}else if(type.equals("json"))
        	{
        	    int length=buf.length();
                for(int i=0;i<buf.length();i++)
                {
                    //Fill buffer
                    chBuffer.add(buf.charAt(i));
                }
                //Form data parsing s
                AlarmData.parseMultiData(chBuffer);
        	}
        	
            if(stoplink)
            {
        	    buf.clear();  
        	    chBuffer.clear();
                this.close();
                stoplink=false;
            }
        }
        @Override
        protected Boolean buildResult(final HttpContext context) {            
            return Boolean.TRUE;
        }    
	}
	
	//Reconnect the query thread with a timeout
	public static class ReConnect extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub	           
			try {
				if(!DataRecv)
				{
					if(timeout==0)
					{
						if(reconnect==0)
						{
							httpAsyncclient.close();
						}else
						{
							//Timeout reconnect, clear buffer, flag bit initialization, close connection, open connection
							chBuffer.clear();
				            stoplink=false;
				            timeout=100000;
							httpAsyncclient.close();
							httpAsyncclient.start();	
							reconnect--;
						}
					}
					else
					{
						sleep(10);
						timeout-=10;
					}
				}else
				{
					return;
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}	
}
