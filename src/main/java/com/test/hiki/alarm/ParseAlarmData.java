package com.test.hiki.alarm;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jfinal.kit.PathKit;
import com.test.hiki.commonFunction.JsonFormatTool;
import com.test.hiki.commonFunction.MyWebSocketListener;

public class ParseAlarmData {

    private static final int XML=1;
    private static final int JSON=2;
    private static final int IMAGE=3;
	private static final int HeadSize=256;	
	private static final String end="\r\n";
	private static final String boundary="boundary";
	private static final String ContentT="Content-Type: ";
	private static final String ContentL="Content-Length: ";
	private static String strXML;
	private static String strWebRootPath=PathKit.getWebRootPath();//Get the server root directory to save the alarm receiving message
	JsonFormatTool JsonFormatTool = new JsonFormatTool();//Json message output formatting
	
	
	public void  parseMultiData(List<Character>chBuffer)
	{
		//Data offset
		int offset=0;
		int infoType=0;
		if(chBuffer==null || chBuffer.isEmpty()||chBuffer.size()<HeadSize)
		{
			return;
		}
		List<Character>targetList=chBuffer.subList(0, HeadSize);
		StringBuilder targetBuf=new StringBuilder();
		for(char tempNode:targetList)
		{
			targetBuf.append(tempNode);
		}
		String strHeadBuf=targetBuf.toString();
		
		if(strHeadBuf.equals(boundary));
		{			
			if(strHeadBuf.contains(ContentT))
			{
			    offset+=strHeadBuf.indexOf(ContentT);
				if(strHeadBuf.contains("xml"))
				{
				    infoType=XML;
				}else if(strHeadBuf.contains("json"))
				{
				    infoType=JSON;
				}else if(strHeadBuf.contains("image"))
				{
				    infoType=IMAGE;
				}	
			}
			StringBuilder strlen=new StringBuilder();
			int len=0;
			if(strHeadBuf.contains(ContentL))
			{
			    offset=strHeadBuf.indexOf(ContentL);
			    offset+=ContentL.length();
				
				for(int i=0;strHeadBuf.charAt(offset)!='\r';offset++)
				{
					strlen.append(strHeadBuf.charAt(offset));
					i++;
				}
				strlen.toString();
				len=Integer.valueOf(strlen.toString());
			}
			offset+=(2*end.length());
			if(chBuffer.size()>=offset+len)
			{
				char[]imageBuf=null;
				switch(infoType)
				{
    				case XML:
    				{
    					StringBuilder XmlBuf=new StringBuilder();
    					targetList=chBuffer.subList(offset, offset+len);
    					for(char c:targetList)
    					{
    						XmlBuf.append(c);
    					}
    					strXML=XmlBuf.toString();
    					for(int i=0;i<(offset+len)&&chBuffer.size()>0;i++)
    					{
    						chBuffer.remove(0);
    					}
    					break;
    				}
    				case JSON:
    				{	
    				    StringBuilder JsonBuf=new StringBuilder();
                        targetList=chBuffer.subList(offset, offset+len);
                        for(char c:targetList)
                        {
                            JsonBuf.append(c);
                        }
                        strXML=JsonBuf.toString();
                        for(int i=0;i<(offset+len)&&chBuffer.size()>0;i++)
                        {
                            chBuffer.remove(0);
                        }
    					break;
    				}
    				case IMAGE:
    				{
    					if(chBuffer.size()>offset+len)
    					{
    					    imageBuf=new char[len];
    						targetList=chBuffer.subList(offset, offset+len);					
    						for(int i=0;i<len;i++)
    						{
    						    imageBuf[i]=targetList.get(i);
    						}					
    						for(int i=0;i<(offset+len+end.length())&&chBuffer.size()>0;i++)
    						{
    							chBuffer.remove(0);
    						}			
    					}
    					break;
    				}
				}	
					
				//Parsing XML with binary images
				parseAlarmInfoType(strXML,imageBuf, infoType);
				System.out.println("strXML");
				System.out.println(strXML);
//				 parseXmlAlarmInfo(strXML,imageBuf);
				//Parses the remaining data in the buffer
				parseMultiData(chBuffer);
				
			}else
			{
				return;
			}	
		}			
	}
	
	public void parseAlarmInfoType(String AlarmInfo,char[]imageBuf,int type)
	{
	    if(type == XML)
	    {
	        parseXmlAlarmInfo(AlarmInfo,imageBuf);
	    }else if(type == JSON)
	    {
	        parseJsonAlarmInfo(AlarmInfo,imageBuf);
	    }
	}
	
	//XML mixed message parsing with image data
	public void parseXmlAlarmInfo(String xmlAlarmInfo,char[]imageBuf)
    {   
        boolean heart=false;
        if(xmlAlarmInfo!="")
        {
            JSONObject jsonAlarmFormatInfo=new JSONObject();//The foreground returns a json object                   
            //Gets the current system time to save the file
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");//Format the date
            String strDateTimeNow=df.format(new Date());
                        
            try { 
                    String ip="";
                    String eventType="";
                    String time="";
                                           
                    SAXReader saxReader = new SAXReader();
                    Document document = saxReader.read(new ByteArrayInputStream(xmlAlarmInfo.getBytes("UTF-8"))); 
                    Element m_root=document.getRootElement();
					List<Element> childElements = m_root.elements();
                    for (Element child : childElements) 
                    {  
                        if(child.getName().equals("ipAddress"))
                        {
                            ip=child.getText();
                        }
                        else if(child.getName().equals("eventType"))
                        {
                            eventType=child.getText();
                        }
                        else if(child.getName().equals("dateTime"))
                        {
                            time=child.getText();
                        }else if(child.getName().equals("eventState"))
                        {
                            if(child.getText().equals("inactive"))
                            {
                                heart=true;
                            }
                        }
                    }
                     
                    if(!heart)
                    {
                        jsonAlarmFormatInfo.put("ip", ip);
                        jsonAlarmFormatInfo.put("eventType", eventType);
                        jsonAlarmFormatInfo.put("time", time);
                                        
                        jsonAlarmFormatInfo.put("content", JsonFormatTool.formatJson(xmlAlarmInfo));
                                            //Save the alarm message to local               
                        String saveFilePath=strWebRootPath+"\\RunHistory\\"+eventType+"_"+strDateTimeNow+".json";
                        
                        File saveContent=new File(saveFilePath);
                        if(!saveContent.getParentFile().exists())
                        {
                            saveContent.getParentFile().mkdirs();
                            saveContent.createNewFile();
                        }
                        
                        PrintWriter pfp= new PrintWriter(saveContent);
                        pfp.print(JsonFormatTool.formatJson(xmlAlarmInfo));
                        pfp.close();
                        
                        //Assemble the front desk to return messages
                        jsonAlarmFormatInfo.put("contentSavePath", "/RunHistory/"+eventType+"_"+strDateTimeNow+".json");        
                        System.out.print(jsonAlarmFormatInfo.toString()+"\n");
                        //Push messages through websocket
                        MyWebSocketListener.sendString(jsonAlarmFormatInfo.toString()); 
                    }
                    //Assemble the foreground to return the json message                         
            } catch (JSONException | IOException | DocumentException e) {
            // TODO Auto-generated catch block
              e.printStackTrace();
            }           
        }
        
    }
	
	//Json mixed message parsing with image data
	public void parseJsonAlarmInfo(String jsonAlarmInfo,char[]ch)
	{
        JSONObject jsonAlarmFormatInfo=new JSONObject();//The foreground returns a json object
        
        //Gets the current system time to save the file
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");//Format the date
        String strDateTimeNow=df.format(new Date());
                    
        try {
                //Parse the json device to upload the alarm message
                JSONObject jsonAlarmRecv=new JSONObject(jsonAlarmInfo);
                String ip = jsonAlarmRecv.get("ipAddress").toString();
                String eventType=jsonAlarmRecv.get("eventType").toString();
                String time=jsonAlarmRecv.get("dateTime").toString();
                
                //Assemble the foreground to return the json message
                jsonAlarmFormatInfo.put("ip", ip);
                jsonAlarmFormatInfo.put("eventType", eventType);
                jsonAlarmFormatInfo.put("time", time);
                                
                jsonAlarmFormatInfo.put("content", JsonFormatTool.formatJson(jsonAlarmInfo));
                
                //Save the alarm message to local              
                String saveFilePath=strWebRootPath+"\\RunHistory\\"+eventType+"_"+strDateTimeNow+".json";
                File saveContent=new File(saveFilePath);
                if(!saveContent.getParentFile().exists())
                {
                    saveContent.getParentFile().mkdirs();
                    saveContent.createNewFile();
                }
                
                PrintWriter pfp= new PrintWriter(saveContent);
                pfp.print(JsonFormatTool.formatJson(jsonAlarmInfo));
                pfp.close();
                
                //Assemble the front desk to return messages
                jsonAlarmFormatInfo.put("contentSavePath", "/RunHistory/"+eventType+"_"+strDateTimeNow+".json");
                
                //Push messages through websocket
                MyWebSocketListener.sendString(jsonAlarmFormatInfo.toString());
                
        } catch (JSONException | IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }           
	}	
}
