package com.test.hiki.alarm;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

import com.test.hiki.commonFunction.JsonFormatTool;
import com.test.hiki.commonFunction.MyWebSocketListener;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.html.parser.DTD;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.LinkedHashMap;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;

public class Listen   extends Controller{
    //Initialize the front-end web page to load
    public void index() {
    	render("/UI-Resource/Alarm/Listen.html");				
    }
	
    JsonFormatTool JsonFormatTool = new JsonFormatTool();//Json message output formatting
    public static HttpServer httpserver = null;//Listening port service
    public static String strWebRootPath=PathKit.getWebRootPath();//Get the server root directory to save the alarm receiving message
	public static int GPicLen = 0;
    //HTTP message listening callback processing method
    class MyHttpHandler implements HttpHandler{  
          
        public void handle(HttpExchange httpExchange) throws IOException {  
            String requestMethod = httpExchange.getRequestMethod();//Parse message method, filter alarm post message
            if (requestMethod.equalsIgnoreCase("POST")){
                //Read stream
                byte[] buffer=new byte[50*1024];
                DataInputStream dataInputStream = new DataInputStream(httpExchange.getRequestBody());
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                
                int length;        
                while ((length = dataInputStream.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                dataInputStream.close();
                //Parse to determine message data type
                String contentType=httpExchange.getRequestHeaders().get("Content-Type").toString();
                 
                /*
                                                        Back to the front message format, uniform format, for the front bulk parsing
                {
                "ip":"",
                "eventType":"",
                "time":"",
                "pictureList"[
                {
                "desc":""
                "url":"",
                }] 
                "content":"",
                "contentSavePath":""
                }                                                              
                */
                //Parse the alarm message
                if(contentType.contains("multipart"))
                {
                    parseAlarmInfo(output.toString(),output);
                }
                else if(contentType.contains("json")){
                    //Pure json alarm message
                    paresJsonAlarmInfo(output.toString());
                }
                else if(contentType.contains("xml")){
                    //Pure xml alarm message
                    parseXmlAlarmInfo(output.toString());
                }
                
                //Return 200ok to the device and close the connection
                httpExchange.sendResponseHeaders(200, 0);     
                httpExchange.close();	                                
            }
            
        }  
          
    }
	 //Newly Add Content
    public void parseImageInfoNew(String ImageInfo,ByteArrayOutputStream output,String BoundaryMark)
    {
    	//Gets the current system time to save the file
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");//Format the date
        String strDateTimeNow=df.format(new Date());
        JSONObject jsonAlarmFormatInfo=new JSONObject();//The foreground returns a json object    
        //Filter Pic Data
//      String LastBoundary = "-----------------------------7daf10c20d06--";
        String LineMark = "\r\n";
        String LineMark1 = "\r\n\r\n";
        String strContentLength = "Content-Length: ";
        int ContentLengthIndex = ImageInfo.lastIndexOf(strContentLength);
        int PicEndIndex = ImageInfo.lastIndexOf(BoundaryMark);
        int PicLenBeginIndex = 0;
        int PicBeginIndex = 0;
        byte[] HexPicArr = null;
         
        if(ContentLengthIndex > 0)
        {
              //Filter Length of Pic
              int PicLenIndex = ImageInfo.lastIndexOf(strContentLength) + strContentLength.length();
              PicLenBeginIndex = PicLenIndex;
              String StrPicLen = "";
              int PicLen = 0;
              char ch;
              if(PicLenIndex > 0)
              {
              	while((ch = ImageInfo.charAt(PicLenIndex)) != '\r')
              	{
              		PicLenIndex++;
              	}
              	StrPicLen = ImageInfo.substring(PicLenBeginIndex, PicLenIndex);
              	PicLen = Integer.parseInt(StrPicLen);
              	System.out.println("PicLen: "+ PicLen);
              	if(PicLen <= 0)
              	{
              		return ;
              	}
              	GPicLen = PicLen;
              }
        }else
        {
        	//while content-length is not existed
        	 PicBeginIndex = ImageInfo.lastIndexOf(LineMark1) + LineMark1.length();
             //Filter Length of Pic
             int PicLen = PicEndIndex - LineMark.length() - PicBeginIndex;
             GPicLen = PicLen;
        }
        if(PicBeginIndex > 0 && PicEndIndex > PicBeginIndex)
        {
        	 String strPicInfo = ImageInfo.substring(PicBeginIndex, PicEndIndex - LineMark.length());
        	 String HexPicStr = "";
        	 byte[] AlarmInfo = output.toByteArray();
             HexPicArr = new byte[GPicLen];
             HexPicArr = Arrays.copyOfRange(AlarmInfo, PicBeginIndex, PicBeginIndex + GPicLen);
             if(HexPicArr != null)
         	 {
         		try {
     				jsonAlarmFormatInfo.put("pictureList",String2Picture(HexPicArr,strDateTimeNow));
     			}catch (JSONException e) {
     				// TODO Auto-generated catch block
     				e.printStackTrace();
     			} catch (IOException e) {
     				// TODO Auto-generated catch block
     				e.printStackTrace();
     			}
             }
        }else
        {
        	return ;
        }
        MyWebSocketListener.sendString(jsonAlarmFormatInfo.toString());  
    }
	 //Newly add content
    private static JSONArray String2Picture(byte[] HexPicArr,String strDateTimeNow) throws IOException, JSONException
    {  
        JSONArray jsonPictureList=new JSONArray();           
        String saveImagePath=strWebRootPath+"\\RunHistory\\";               
        String PicName = "captureResult_Image_"+strDateTimeNow+".jpg";
        JSONObject singlejsonPicture=new JSONObject();
        try {
 			InputStream in = new ByteArrayInputStream(HexPicArr);
 			File file=new File(saveImagePath,PicName);//可以是任何图片格式.jpg,.png等
 			FileOutputStream fos=new FileOutputStream(file);  
 			byte[] b = new byte[1024];
 			int nRead = 0;
 			while ((nRead = in.read(b)) != -1) {
 				fos.write(b, 0, nRead);
 			}
 			fos.flush();
 			fos.close();
 			in.close();
 			singlejsonPicture.put("desc", "Image");
            singlejsonPicture.put("url","/RunHistory/captureResult_Image_"+strDateTimeNow+".jpg");                                                               
            jsonPictureList.put(singlejsonPicture);
            System.out.println("success");
            return jsonPictureList;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
		}
        return jsonPictureList; 
    }
	//Newly Add Content
    public void parseAlarmInfo(String AlarmInfo,ByteArrayOutputStream output)
    {
    	String BoundaryMark = "";
    	if(AlarmInfo.contains("xml"))
    	{	
    		//Filter Left content before Xml 
//    		String StrXmlbegin = "-----------------------------7daf10c20d06\r\n";		
            String StrXmllen = "Content-Length: ";
            String StrLineMark = "\r\n";
            //正式报文和其他内容间会有两个“\r\n”分隔符
            String StrLineMark1 = "\r\n\r\n";
    		int XmlbeginIndex = AlarmInfo.indexOf(StrXmllen);
    		BoundaryMark = AlarmInfo.substring(0, XmlbeginIndex - StrLineMark.length());
    		String XmlContent = "";
    		if(XmlbeginIndex > 0)
    		{
    			XmlbeginIndex += StrXmllen.length();
    			int XmlLenIndex = AlarmInfo.indexOf(StrLineMark, XmlbeginIndex);
    			String StrXmlLen = AlarmInfo.substring(XmlbeginIndex, XmlLenIndex);
    			//parse Length of xml 
    			int XmlLen = Integer.parseInt(StrXmlLen);
    			int XmlContentIndex = AlarmInfo.indexOf(StrLineMark1, XmlbeginIndex);
    			XmlContentIndex += StrLineMark1.length();
    			//根据报文长度去截取xml报文的内容
    			XmlContent = AlarmInfo.substring(XmlContentIndex, XmlContentIndex + XmlLen);
    		}
    		parseXmlAlarmInfo(XmlContent);
    	}
    	if(AlarmInfo.contains("image"))
		{
			parseImageInfoNew(AlarmInfo,output,BoundaryMark);
		}
    }
    
    //Parse the alarm message in XML format    
    public void parseXmlAlarmInfo(String xmlAlarmInfo)
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
                    }                                          
                }
                                              
                //Assemble the foreground to return the json message
                jsonAlarmFormatInfo.put("ip", ip);
                jsonAlarmFormatInfo.put("eventType", eventType);
                jsonAlarmFormatInfo.put("time", time);
                
                if(eventType.equals("rapidMove"))
                {
                	parseXmlRapidMove(jsonAlarmFormatInfo, document, strDateTimeNow);
                }
                else if(eventType.equals("leavePosition"))
                {
                	parseXmlLeavePosition(jsonAlarmFormatInfo, document, strDateTimeNow);
                }
                else if(eventType.equals("PeopleCounting"))
                {
                	parseXmlPeopleCounting(jsonAlarmFormatInfo, document, strDateTimeNow);
                }
                else if(eventType.equals("group"))
                {
                	parseXmlGroup(jsonAlarmFormatInfo, document, strDateTimeNow);
                }
                else if(eventType.equals("failDown"))
                {
                	parseXmlFailDown(jsonAlarmFormatInfo, document, strDateTimeNow);
                }
                else if(eventType.equals("peopleNumChange"))
                {
                	parseXmlPeopleNumChange(jsonAlarmFormatInfo, document, strDateTimeNow);
                }
                else if(eventType.equals("violentMotion"))
                {
                	parseXmlViolentMotion(jsonAlarmFormatInfo, document, strDateTimeNow);
                }
                else if(eventType.equals("fielddetection"))
                {
                	parseXmlFieldDetection(jsonAlarmFormatInfo, document, strDateTimeNow);
                }
                else if(eventType.equals("attendedBaggage"))
                {
                	parseXmlAttendedBaggage(jsonAlarmFormatInfo, document, strDateTimeNow);
                }
                else if(eventType.equals("unattendedBaggage"))
                {
                	parseXmlUnattendedBaggage(jsonAlarmFormatInfo, document, strDateTimeNow);
                }
                else if(eventType.equals("regionExiting"))
                {
                	parseXmlRegionExiting(jsonAlarmFormatInfo, document, strDateTimeNow);
                }
                else if(eventType.equals("linedetection"))
                {
                	parseXmlLineDetection(jsonAlarmFormatInfo, document, strDateTimeNow);
                }
                else if(eventType.equals("regionEntrance"))
                {
                	parseXmlRegionEntrance(jsonAlarmFormatInfo, document, strDateTimeNow);
                }
                else if(eventType.equals("parking"))
                {
                	parseXmlParking(jsonAlarmFormatInfo, document, strDateTimeNow);
                }
                else if(eventType.equals("loitering"))
                {
                	parseXmlLoitering(jsonAlarmFormatInfo, document, strDateTimeNow);
                }else if(eventType.equals("DBDCustom"))
                {
                	//Newly Add Content
                	parseXmlDBDCustom(jsonAlarmFormatInfo,document,strDateTimeNow);
                }else if(eventType.equals("ANPR"))
                {
                	//Parse ANPR Info
                	parseXmlANPRInfo(jsonAlarmFormatInfo,document,strDateTimeNow);
                }

                                               
                jsonAlarmFormatInfo.put("content", JsonFormatTool.formatJson(xmlAlarmInfo));
                
                //Save the alarm message to local                
                String saveFilePath=strWebRootPath+"\\RunHistory\\"+eventType+"_"+strDateTimeNow+".xml";
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
                jsonAlarmFormatInfo.put("contentSavePath", "/RunHistory/"+eventType+"_"+strDateTimeNow+".xml");
                
                //Push messages through websocket
                MyWebSocketListener.sendString(jsonAlarmFormatInfo.toString());
                
        } catch (JSONException | IOException | DocumentException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }       
    
    }
	//Newly Add Content
    public void parseXmlDBDCustom(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        Iterator DBDCustomiters = root.elementIterator("DBDCustom");
        for(Element child : childElements)
        {
        	if(child.getName().equals("DBDCustom"))
        	{
        		Element DBDroot = child;
        		List<Element> DBDElements = DBDroot.elements();
        		//Parse PositionInfo
        		for(Element DBDchild: DBDElements)
        		{
        			if(DBDchild.getName().equals("PositionInfo"))
        			{
        				Element Positionroot = DBDchild;
        				List<Element> PositionElements = Positionroot.elements();
        				for(Element Positionchild:PositionElements)
        				{
        					if(Positionchild.getName().equals("direction"))
        					{
        						jsObj.put("direction", Positionchild.getText());
        					}
        					if(Positionchild.getName().equals("speed"))
        					{
        						jsObj.put("speed", Positionchild.getText());
        					}
        					if(Positionchild.getName().equals("speed"))
        					{
        						jsObj.put("speed", Positionchild.getText());
        					}
        				}
        			}
        			if(DBDchild.getName().equals("snapTime"))
        			{
        				jsObj.put("snapTime", DBDchild.getText());
        			}
        			if(DBDchild.getName().equals("smoking"))
        			{
        				jsObj.put("smoking", DBDchild.getText());
        			}
        			if(DBDchild.getName().equals("uphone"))
        			{
        				jsObj.put("uphone", DBDchild.getText());
        			}
        			if(DBDchild.getName().equals("fatigueDriving"))
        			{
        				jsObj.put("fatigueDriving", DBDchild.getText());
        			}
        			
        			if(DBDchild.getName().equals("notLookStraightAhead"))
        			{
        				jsObj.put("notLookStraightAhead", DBDchild.getText());
        			}
        			if(DBDchild.getName().equals("pilotSafebelt"))
        			{
        				jsObj.put("pilotSafebelt", DBDchild.getText());
        			}
        		}
        	}
        }
    }
    //Parse ANPR Info
    public void parseXmlANPRInfo(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        //Iterator DBDCustomiters = root.elementIterator("ANPR");
        for(Element child : childElements)
        {
        	if(child.getName().equals("ANPR"))
        	{
        		Element ANPRroot = child;
        		List<Element> ANPRElements = ANPRroot.elements();
        		//Parse PositionInfo
        		for(Element ANPRchild: ANPRElements)
        		{
        			if(ANPRchild.getName().equals("PositionInfo"))
        			{
        				Element Positionroot = ANPRchild;
        				List<Element> PositionElements = Positionroot.elements();
        				for(Element Positionchild:PositionElements)
        				{
        					if(Positionchild.getName().equals("direction"))
        					{
        						jsObj.put("direction", Positionchild.getText());
        					}
        					if(Positionchild.getName().equals("speed"))
        					{
        						jsObj.put("speed", Positionchild.getText());
        					}
        				}
        			}
        			if(ANPRchild.getName().equals("licensePlate"))
        			{
        				jsObj.put("licensePlate", ANPRchild.getText());
        			}
        			if(ANPRchild.getName().equals("plateColor"))
        			{ 
        				if(ANPRchild.getText().equals("0"))
        				{
        					jsObj.put("plateColor", "white");
        				}else if(ANPRchild.getText().equals("1"))
        				{
        					jsObj.put("plateColor", "yellow");
        				}else if(ANPRchild.getText().equals("2"))
        				{
        					jsObj.put("plateColor", "blue");
        				}else if(ANPRchild.getText().equals("3"))
        				{
        					jsObj.put("plateColor", "black");
        				}else if(ANPRchild.getText().equals("1"))
        				{
        					jsObj.put("plateColor", "other");
        				}else
        				{
        					jsObj.put("plateColor", "");
        				}
        			}
        			if(ANPRchild.getName().equals("vehicleType"))
        			{
        				if(ANPRchild.getText().equals("0"))
        				{
        					jsObj.put("vehicleType", "Other Type");
        				}else if(ANPRchild.getText().equals("1"))
        				{
        					jsObj.put("vehicleType", "Little vehicle");
        				}else if(ANPRchild.getText().equals("2"))
        				{
        					jsObj.put("vehicleType", "Big vehicle");
        				}else
        				{
        					jsObj.put("vehicleType", "");
        				}
        			}
        		}
        	}
        }
    }

    public void parseXmlRapidMove(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("detectionPictureTransType"))
        	{
        		jsObj.put("detectionPictureTransType", child.getText());
        	}
        	else if(child.getName().equals("detectionPicturesNumber"))
        	{
        		jsObj.put("detectionPicturesNumber", Integer.parseInt(child.getText()));
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlLeavePosition(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("detectionPictureTransType"))
        	{
        		jsObj.put("detectionPictureTransType", child.getText());
        	}
        	else if(child.getName().equals("detectionPicturesNumber"))
        	{
        		jsObj.put("detectionPicturesNumber", Integer.parseInt(child.getText()));
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlPeopleCounting(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("peopleCounting"))
        	{
        		JSONObject peopleCountingObj=new JSONObject();
        		List<Element> pcChildElements = child.elements();
        		for(Element pcChild : pcChildElements)
        		{
        			if(pcChild.getName().equals("statisticalMethods"))
        			{
        				peopleCountingObj.put("statisticalMethods", pcChild.getText());
        			}
        			else if(pcChild.getName().equals("RealTime"))
        			{
        				JSONObject realTimeObj=new JSONObject();
                		List<Element> rtChildElements = pcChild.elements();
                		for(Element rtChild : rtChildElements)
                		{
                			if(rtChild.getName().equals("time"))
                			{
                				realTimeObj.put("time", rtChild.getText());
                			}
                		}
                		peopleCountingObj.put("RealTime", realTimeObj);
        			}
        			else if(pcChild.getName().equals("TimeRange"))
        			{
        				JSONObject timeRangeObj=new JSONObject();
                		List<Element> trChildElements = pcChild.elements();
                		for(Element trChild : trChildElements)
                		{
                			if(trChild.getName().equals("startTime"))
                			{
                				timeRangeObj.put("startTime", trChild.getText());
                			}
                			else if(trChild.getName().equals("endTime"))
                			{
                				timeRangeObj.put("endTime", trChild.getText());
                			}
                		}
                		peopleCountingObj.put("TimeRange", timeRangeObj);
        			}
        			else if(pcChild.getName().equals("enter"))
        			{
        				peopleCountingObj.put("enter", Integer.parseInt(pcChild.getText()));
        			}
        			else if(pcChild.getName().equals("exit"))
        			{
        				peopleCountingObj.put("exit", Integer.parseInt(pcChild.getText()));
        			}
        			else if(pcChild.getName().equals("pass"))
        			{
        				peopleCountingObj.put("pass", Integer.parseInt(pcChild.getText()));
        			}
        			else if(pcChild.getName().equals("duplicatePeople"))
        			{
        				peopleCountingObj.put("duplicatePeople", Integer.parseInt(pcChild.getText()));
        			}
        		}
        		jsObj.put("peopleCounting", peopleCountingObj);
        	}
        	else if(child.getName().equals("childCounting"))
        	{
        		JSONObject childCountingObj=new JSONObject();
        		List<Element> ccChildElements = child.elements();
        		for(Element ccChild : ccChildElements)
        		{
        			if(ccChild.getName().equals("enter"))
        			{
        				childCountingObj.put("enter", ccChild.getText());
        			}
        			else if(ccChild.getName().equals("exit"))
        			{
        				childCountingObj.put("exit", ccChild.getText());
        			}
        		}
        		jsObj.put("childCounting", childCountingObj);
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlGroup(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("detectionPictureTransType"))
        	{
        		jsObj.put("detectionPictureTransType", child.getText());
        	}
        	else if(child.getName().equals("detectionPicturesNumber"))
        	{
        		jsObj.put("detectionPicturesNumber", Integer.parseInt(child.getText()));
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlFailDown(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("detectionPictureTransType"))
        	{
        		jsObj.put("detectionPictureTransType", child.getText());
        	}
        	else if(child.getName().equals("detectionPicturesNumber"))
        	{
        		jsObj.put("detectionPicturesNumber", Integer.parseInt(child.getText()));
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlPeopleNumChange(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("detectionPictureTransType"))
        	{
        		jsObj.put("detectionPictureTransType", child.getText());
        	}
        	else if(child.getName().equals("detectionPicturesNumber"))
        	{
        		jsObj.put("detectionPicturesNumber", Integer.parseInt(child.getText()));
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlViolentMotion(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("detectionPictureTransType"))
        	{
        		jsObj.put("detectionPictureTransType", child.getText());
        	}
        	else if(child.getName().equals("detectionPicturesNumber"))
        	{
        		jsObj.put("detectionPicturesNumber", Integer.parseInt(child.getText()));
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlFieldDetection(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("detectionPictureTransType"))
        	{
        		jsObj.put("detectionPictureTransType", child.getText());
        	}
        	else if(child.getName().equals("detectionPicturesNumber"))
        	{
        		jsObj.put("detectionPicturesNumber", Integer.parseInt(child.getText()));
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlAttendedBaggage(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("detectionPictureTransType"))
        	{
        		jsObj.put("detectionPictureTransType", child.getText());
        	}
        	else if(child.getName().equals("detectionPicturesNumber"))
        	{
        		jsObj.put("detectionPicturesNumber", Integer.parseInt(child.getText()));
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlUnattendedBaggage(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("detectionPictureTransType"))
        	{
        		jsObj.put("detectionPictureTransType", child.getText());
        	}
        	else if(child.getName().equals("detectionPicturesNumber"))
        	{
        		jsObj.put("detectionPicturesNumber", Integer.parseInt(child.getText()));
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlRegionExiting(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("detectionPictureTransType"))
        	{
        		jsObj.put("detectionPictureTransType", child.getText());
        	}
        	else if(child.getName().equals("detectionPicturesNumber"))
        	{
        		jsObj.put("detectionPicturesNumber", Integer.parseInt(child.getText()));
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlLineDetection(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("detectionPictureTransType"))
        	{
        		jsObj.put("detectionPictureTransType", child.getText());
        	}
        	else if(child.getName().equals("detectionPicturesNumber"))
        	{
        		jsObj.put("detectionPicturesNumber", Integer.parseInt(child.getText()));
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlRegionEntrance(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("detectionPictureTransType"))
        	{
        		jsObj.put("detectionPictureTransType", child.getText());
        	}
        	else if(child.getName().equals("detectionPicturesNumber"))
        	{
        		jsObj.put("detectionPicturesNumber", Integer.parseInt(child.getText()));
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlParking(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("detectionPictureTransType"))
        	{
        		jsObj.put("detectionPictureTransType", child.getText());
        	}
        	else if(child.getName().equals("detectionPicturesNumber"))
        	{
        		jsObj.put("detectionPicturesNumber", Integer.parseInt(child.getText()));
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlLoitering(JSONObject jsObj, Document doc, String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	Element root=doc.getRootElement();
    	String eventType = root.element("eventType").getText();
        List<Element> childElements = root.elements();
        for(Element child : childElements)
        {
        	if(child.getName().equals("detectionPictureTransType"))
        	{
        		jsObj.put("detectionPictureTransType", child.getText());
        	}
        	else if(child.getName().equals("detectionPicturesNumber"))
        	{
        		jsObj.put("detectionPicturesNumber", Integer.parseInt(child.getText()));
        	}
        	else if(child.getName().equals("bkgUrl"))
        	{
        		parseXmlImg(jsObj,child,eventType,strDateTimeNow);
        	}
        }
    }
    
    public void parseXmlImg(JSONObject object,Element element,String eventType,String strDateTimeNow) throws JSONException,IOException,DocumentException
    {
    	String bkgUrl=element.getText(); 
    	String saveBackImagePath=strWebRootPath+"\\RunHistory\\"+eventType+"_backImage_"+strDateTimeNow+".jpg"; 
    	downloadPicture(bkgUrl,saveBackImagePath);
    	object.put("bkgUrl", element.getText());
    	
    	JSONArray jsonPictureList=new JSONArray();       		
        JSONObject singlejsonPicture=new JSONObject();
        singlejsonPicture.put("desc", "backImage");
        singlejsonPicture.put("url","/RunHistory/"+eventType+"_backImage_"+strDateTimeNow+".jpg");                                                               
        jsonPictureList.put(singlejsonPicture);
    	
    	object.put("pictureList", jsonPictureList);   	
    }
    
    
	//Parse the alarm message in json format    
    public void paresJsonAlarmInfo(String jsonAlarmInfo)
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
                
                //Analysis old json first(先解析未制定规范前的老json报警报文,若事件不属于老报警，则采用新报警格式解析)
                JSONArray pictureList=pareOldJsonEvent(jsonAlarmRecv,eventType,strDateTimeNow);
                if(pictureList==null)
                {
                    pictureList=pareNewJsonEvent(jsonAlarmRecv,eventType,strDateTimeNow);
                }
                if(pictureList!=null)
                {
                    jsonAlarmFormatInfo.put("pictureList", pictureList);
                }
                
                
                //json报文转UTF-8格式编码
                byte []byUTF8;
                String strUTF8jsonAlarmInfo = null;
                try{
                    byUTF8 = jsonAlarmInfo.getBytes("UTF-8");
                    strUTF8jsonAlarmInfo=new String(byUTF8,"UTF-8");
                }catch(UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                if(strUTF8jsonAlarmInfo==null)
                {
                    jsonAlarmFormatInfo.put("content", JsonFormatTool.formatJson(jsonAlarmInfo));
                }else{
                    jsonAlarmFormatInfo.put("content", JsonFormatTool.formatJson(strUTF8jsonAlarmInfo));
                }
                
                //jsonAlarmFormatInfo.put("content", JsonFormatTool.formatJson(jsonAlarmInfo));
                
                
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
    
   //老格式的json报警报文，需要对具体的事件进行具体报文解析
    public JSONArray pareOldJsonEvent(JSONObject jsonAlarmRecv,String eventType,String strDateTimeNow) throws JSONException
    {
        
      //Face snap picture alarm picture list assembly
        if(eventType.equals("captureResult"))
        {                                   
            return pareJsonCaptureResult(jsonAlarmRecv.getJSONArray("captureLibResult"),strDateTimeNow);
        }
        //Face comparison alarm picture list assembly
        else if(eventType.equals("alarmResult"))
        {                                   
            return pareJsonContrastResult(jsonAlarmRecv.getJSONArray("alarmResult"),strDateTimeNow);
        }
        //Face snap video picture picture list assembly
        else if(eventType.equals("faceCapture"))
        {                                   
            return pareJsonFaceCapture(jsonAlarmRecv.getJSONArray("faceCapture"),strDateTimeNow);
        }
        else if(eventType.equals("cityManagement"))
        {
            return parseJsonCityManagement(jsonAlarmRecv.getJSONArray("Result"),strDateTimeNow);
        }
        return null;
        
    }
    //新格式的json报警格式，只需要提取对应的资源节点就可以复用已有的解析逻辑
    public JSONArray pareNewJsonEvent(JSONObject jsonAlarmRecv,String eventType,String strDateTimeNow) throws JSONException
    {     
        JSONArray jsonPictureList=new JSONArray();
        
        //playCellphone
        if(eventType.equals("playCellphone"))
        {
            JSONObject playCellphone=jsonAlarmRecv.getJSONObject("PlayCellphone");
            jsonPictureList.put(pareJsonImg(playCellphone.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
        //nonPoliceIntrusion
        else if (eventType.equals("nonPoliceIntrusion")) {
            JSONObject nonPoliceIntrusion=jsonAlarmRecv.getJSONObject("NonPoliceIntrusion");
            jsonPictureList.put(pareJsonImg(nonPoliceIntrusion.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
       //policeAbsent
        else if (eventType.equals("policeAbsent")) {
            JSONObject policeAbsent=jsonAlarmRecv.getJSONObject("PoliceAbsent");
            jsonPictureList.put(pareJsonImg(policeAbsent.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
       //tossing
        else if (eventType.equals("tossing")) {
            JSONObject tossing=jsonAlarmRecv.getJSONObject("Tossing");
            jsonPictureList.put(pareJsonImg(tossing.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
       //sitQuietly
        else if (eventType.equals("sitQuietly")) {
            JSONObject sitQuietly=jsonAlarmRecv.getJSONObject("SitQuietly");
            jsonPictureList.put(pareJsonImg(sitQuietly.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
        //physicalConfront
        else if (eventType.equals("physicalConfront")) {
            JSONObject physicalConfront=jsonAlarmRecv.getJSONObject("PhysicalConfront");
            jsonPictureList.put(pareJsonImg(physicalConfront.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
        //peopleNumCounting
        else if (eventType.equals("peopleNumCounting")) {
            JSONObject peopleNumCounting=jsonAlarmRecv.getJSONObject("PeopleNumCounting");
            jsonPictureList.put(pareJsonImg(peopleNumCounting.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
        //standUp
        else if (eventType.equals("standUp")) {
            JSONObject standUp=jsonAlarmRecv.getJSONObject("StandUp");
            jsonPictureList.put(pareJsonImg(standUp.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
        //getUp
        else if (eventType.equals("getUp")) {
            JSONObject getUp=jsonAlarmRecv.getJSONObject("GetUp");
            jsonPictureList.put(pareJsonImg(getUp.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
        //toiletTarry
        else if (eventType.equals("toiletTarry")) {
            JSONObject toiletTarry=jsonAlarmRecv.getJSONObject("ToiletTarry");
            jsonPictureList.put(pareJsonImg(toiletTarry.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
      //crowdSituationAnalysis
        else if(eventType.equals("crowdSituationAnalysis"))
        {
            JSONObject crowdSituationAnalysis=jsonAlarmRecv.getJSONObject("CrowdSituationAnalysis");
            jsonPictureList.put(pareJsonImg(crowdSituationAnalysis.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            jsonPictureList.put(pareJsonImg(crowdSituationAnalysis.getJSONObject("personDensityImage"),eventType,strDateTimeNow,"personDensityImage"));
            jsonPictureList.put(pareJsonImg(crowdSituationAnalysis.getJSONObject("grayscaleImage"),eventType,strDateTimeNow,"grayscaleImage"));
            return jsonPictureList;
        }
        else if(eventType.equals("keyPersonGetUp"))
        {
            JSONObject keyPersonGetUp=jsonAlarmRecv.getJSONObject("KeyPersonGetup");
            jsonPictureList.put(pareJsonImg(keyPersonGetUp.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
        else if(eventType.equals("audioAbnormal"))
        {
            JSONObject audioAbnormal=jsonAlarmRecv.getJSONObject("AudioAbnormal");
            jsonPictureList.put(pareJsonImg(audioAbnormal.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
        else if(eventType.equals("yardTarry"))
        {
            JSONObject yardTarry=jsonAlarmRecv.getJSONObject("YardTarry");
            jsonPictureList.put(pareJsonImg(yardTarry.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
        else if(eventType.equals("advReachHeight"))
        {
            JSONObject advReachHeight=jsonAlarmRecv.getJSONObject("AdvReachHeight");
            jsonPictureList.put(pareJsonImg(advReachHeight.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
        else if(eventType.equals("advReachHeight"))
        {
            JSONObject advReachHeight=jsonAlarmRecv.getJSONObject("AdvReachHeight");
            jsonPictureList.put(pareJsonImg(advReachHeight.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
        else if(eventType.equals("framesPeopleCounting"))
        {
            JSONObject framesPeopleCounting=jsonAlarmRecv.getJSONObject("FramesPeopleCounting");
            jsonPictureList.put(pareJsonImg(framesPeopleCounting.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
        else if(eventType.equals("overtimeTarry"))
        {
            JSONObject overtimeTarry=jsonAlarmRecv.getJSONObject("OvertimeTarry");
            jsonPictureList.put(pareJsonImg(overtimeTarry.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
        else if(eventType.equals("indoorPhysicalConfront"))
        {
            JSONObject indoorPhysicalConfront=jsonAlarmRecv.getJSONObject("IndoorPhysicalConfront");
            jsonPictureList.put(pareJsonImg(indoorPhysicalConfront.getJSONObject("BackgroundImage"),eventType,strDateTimeNow,"backGroundImage"));
            return jsonPictureList;
        }
        return null;
        
    }
    
    //
    public JSONObject pareJsonImg(JSONObject Img,String eventType,String strDateTimeNow,String strImgName) 
    {
        //Assemble the foreground to return the json message
        JSONObject singlejsonPicture=new JSONObject();
        try {
            String contentType=Img.get("resourcesContentType").toString();
            String contentID=Img.get("resourcesContent").toString();
            if(contentType.equals("url"))
            {
                String saveBackImagePath=strWebRootPath+"\\RunHistory\\"+eventType+"\\"+strImgName+"_"+strDateTimeNow+".jpg";                
                downloadPicture(contentID,saveBackImagePath);
                singlejsonPicture.put("desc", eventType+"_"+strImgName);
                singlejsonPicture.put("url","/RunHistory/"+eventType+"/"+strImgName+"_"+strDateTimeNow+".jpg");                                                               

            }else if(contentType.equals("binary"))
            {
                //binary 
            }else if(contentType.equals("base64"))
            {
                String saveBackImagePath=strWebRootPath+"\\RunHistory\\"+eventType+"\\"+strImgName+"_"+strDateTimeNow+".jpg";
                base64ToPicture(contentID,saveBackImagePath);
                singlejsonPicture.put("desc", eventType+"_"+strImgName);
                singlejsonPicture.put("url","/RunHistory/"+eventType+"/"+strImgName+"_"+strDateTimeNow+".jpg");                                                               
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return singlejsonPicture;
        
    }
    
    //Open to monitor
     public void startListen()
    {
        //Parse the listener port entered by the foreground
        int port = getParaToInt("port");
        try 
        {
            //Create listening service
            HttpServerProvider provider = HttpServerProvider.provider();  
            //Binding port
            httpserver = provider.createHttpServer(new InetSocketAddress(port), 100);
            //Bind data receive callback
            httpserver.createContext("/", new MyHttpHandler());   
            httpserver.setExecutor(null);  
            //Open to monitor service
            httpserver.start(); 
            //Set to listen for successful return message
            setAttr("returnData","success");         

        } catch (Exception e) {
            //Set to listen for failed return message
            setAttr("returnData","failed");    
        }
        finally
        {
            //Send data back to the front desk
            renderJson();
        }               
    }
	    
    //Stop listening
    public void stopListen()
    {       
        try 
        {
            //Stop listening service
            httpserver.stop(0); 
            httpserver = null;
            //Set to listen for successful return message
            setAttr("returnData","success");         

        } catch (Exception e) {
            //Set to listen for failed return message
            setAttr("returnData","failed");    
        }
        finally
        {
            //Send data back to the front desk
            renderJson();
        }   
    }
	    
    //Link url to download image
    private static void downloadPicture(String urlList,String path) {
        //Create a url
        URL url = null;
        try {
            url = new URL(urlList);
            //Open stream data to read the image
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
    
            //Create a file stream to save the image
            File file =new File(path);
            if(!file.getParentFile().exists())
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
    
            byte[] buffer = new byte[1024];
            int length;
    
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            
            //Close file stream
            dataInputStream.close();
            //Close data stream
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Download pictures in Base64 format
    private static void base64ToPicture(String base64Str,String path)
    {
        //BASE64Decoder decoder = new BASE64Decoder();
        try{
            
            Base64.Decoder decoder=Base64.getDecoder();
            byte[] decoderBytes=decoder.decode(base64Str);
            File file =new File(path);
            if(!file.getParentFile().exists())
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            
            FileOutputStream write=new FileOutputStream(file);
            write.write(decoderBytes);
            write.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        } 
    }
    
    
    //Analyze the list of face snapshot alarm assembly pictures
    public JSONArray pareJsonCaptureResult(JSONArray jsonCaptureLibResult,String strDateTimeNow)
    {
        //Assemble the foreground to return the json message
        JSONArray jsonPictureList=new JSONArray();

        try {
            
            for (int i=0;i<jsonCaptureLibResult.length();i++)
            {
                //Parse the json device to upload the alarm message
                JSONObject singlejsonCaptureLibResult;
                singlejsonCaptureLibResult = jsonCaptureLibResult.getJSONObject(i);
                
                //The face of a subgraph
                String imageUrl=singlejsonCaptureLibResult.get("image").toString();
                //Image Download
                String saveFaceImagePath=strWebRootPath+"\\RunHistory\\captureResult_faceImage_"+strDateTimeNow+".jpg";                
                downloadPicture(imageUrl,saveFaceImagePath);
                
                //Big background image
                JSONObject jsonTargetAttrs= singlejsonCaptureLibResult.getJSONObject("targetAttrs");
                String bkgUrl=jsonTargetAttrs.get("bkgUrl").toString();
                //Image Download
                String saveBackImagePath=strWebRootPath+"\\RunHistory\\captureResult_backImage_"+strDateTimeNow+".jpg";                
                downloadPicture(bkgUrl,saveBackImagePath);
                
                //Assemble the foreground to return the json message
                /*
                "pictureList"[
                {
                "desc":""
                "url":"",
                }] 
                "content":"",
                "contentSavePath":""
                }                                                              
                */
                JSONObject singlejsonPicture=new JSONObject();
                singlejsonPicture.put("desc", "faceImage");
                singlejsonPicture.put("url","/RunHistory/captureResult_faceImage_"+strDateTimeNow+".jpg");                                                               
                jsonPictureList.put(singlejsonPicture);
                
                singlejsonPicture=new JSONObject();
                singlejsonPicture.put("desc", "backImage");
                singlejsonPicture.put("url", "/RunHistory/captureResult_backImage_"+strDateTimeNow+".jpg");                                                               
                jsonPictureList.put(singlejsonPicture);
            }  
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         
        return jsonPictureList;
    }
    
  //Analyze the list of face snapshot alarm assembly pictures
    public JSONArray pareJsonContrastResult(JSONArray jsonContrastResult,String strDateTimeNow)
    {
        //Assemble the foreground to return the json message
        JSONArray jsonPictureList=new JSONArray();

        try {
            
            for (int i=0;i<jsonContrastResult.length();i++)
            {
                //Parse the json device to upload the alarm message
                JSONObject singlejsonContrastResult;
                singlejsonContrastResult = jsonContrastResult.getJSONObject(i);
                
                //The face of a subgraph
                String imageUrl=singlejsonContrastResult.get("image").toString();
                //Image Download
                String saveFaceImagePath=strWebRootPath+"\\RunHistory\\contrastResult_faceImage_"+strDateTimeNow+".jpg";                
                downloadPicture(imageUrl,saveFaceImagePath);
                
                //Big background image
                JSONObject jsonTargetAttrs= singlejsonContrastResult.getJSONObject("targetAttrs");
                String bkgUrl=jsonTargetAttrs.get("bkgUrl").toString();
                //Image Download
                String saveBackImagePath=strWebRootPath+"\\RunHistory\\contrastResult_backImage_"+strDateTimeNow+".jpg";                
                downloadPicture(bkgUrl,saveBackImagePath);
                
                //Assemble the foreground to return the json message
                /*
                "pictureList"[
                {
                "desc":""
                "url":"",
                }] 
                "content":"",
                "contentSavePath":""
                }                                                              
                */
                JSONObject singlejsonPicture=new JSONObject();
                singlejsonPicture.put("desc", "faceImage");
                singlejsonPicture.put("url","/RunHistory/contrastResult_faceImage_"+strDateTimeNow+".jpg");                                                               
                jsonPictureList.put(singlejsonPicture);
                
                //Parse Faces nodes
                JSONArray jsonFaces= singlejsonContrastResult.getJSONArray("faces");
                for(int j = 0; j<jsonFaces.length();j++)
                {
                    //Parse Faces->identify
                    JSONArray jsonFaceIdentity= jsonFaces.getJSONObject(j).getJSONArray("identify");
                    for(int k=0;k<jsonFaceIdentity.length();k++)
                    {
                        float maxSimilarity =Float.parseFloat(jsonFaceIdentity.getJSONObject(k).get("maxsimilarity").toString());
                        //Parse Faces->identify->candidate
                        JSONArray jsonFaceIdentityCandidate= jsonFaceIdentity.getJSONObject(k).getJSONArray("candidate");
                        for(int m=0;m<jsonFaceIdentityCandidate.length();m++)
                        {
                            //Get the name of the face with the largest similarity
                            //Parse Faces->identify->candidate->reserve_field->name
                            String strFaceName = jsonFaceIdentityCandidate.getJSONObject(m).getJSONObject("reserve_field").getString("name");
                            
                            //Maximum similarity of the candidate to the alarm picture
                            float maxCandidateSimilarity = Float.parseFloat(jsonFaceIdentityCandidate.getJSONObject(m).get("similarity").toString());
                            if(maxCandidateSimilarity == maxSimilarity)
                            {
                                String saveCandidateImagePath=strWebRootPath+"\\RunHistory\\contrastResult_candidateImage_"+strDateTimeNow+".jpg";
                              //Parse Faces->identify->candidate->human_data
                                JSONArray jsonFaceIdentityCandidateHumanData =jsonFaceIdentityCandidate.getJSONObject(m).getJSONArray("human_data");
                                for(int n=0;n<jsonFaceIdentityCandidateHumanData.length();n++)
                                {
                                    JSONObject singleCandidate =jsonFaceIdentityCandidateHumanData.getJSONObject(i);
                                    
                                    float candidateSimilarity = Float.parseFloat(singleCandidate.get("similarity").toString());
                                    if(candidateSimilarity == maxSimilarity)
                                    {
                                        String candidatePicUrl=singleCandidate.get("face_picurl").toString();
                                        //Image Download
                                        downloadPicture(candidatePicUrl,saveCandidateImagePath);
                                        break;
                                    }
                                }
                                //Add the most similar image
                                singlejsonPicture=new JSONObject();
                                singlejsonPicture.put("desc", "candidateImage:  name:"+ strFaceName+ "  similarity:"+ maxSimilarity);
                                singlejsonPicture.put("url", "/RunHistory/contrastResult_candidateImage_"+strDateTimeNow+".jpg");                                                               
                                jsonPictureList.put(singlejsonPicture);
                            }
                        }
                    }

                }
                
                //Add background image
                singlejsonPicture=new JSONObject();
                singlejsonPicture.put("desc", "backImage");
                singlejsonPicture.put("url", "/RunHistory/contrastResult_backImage_"+strDateTimeNow+".jpg");                                                               
                jsonPictureList.put(singlejsonPicture);
                
            }  
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         
        return jsonPictureList;
    }
    
    //Analytic face snap video picture alarm
    public JSONArray pareJsonFaceCapture(JSONArray jsonCaptureLibResult,String strDateTimeNow)
    {
        //Assemble the foreground to return the json message
        JSONArray jsonPictureList=new JSONArray();

        try {
            
            for (int i=0;i<jsonCaptureLibResult.length();i++)
            {
                //Parse the json device to upload the alarm message
                JSONObject singlejsonCaptureLibResult;
                singlejsonCaptureLibResult = jsonCaptureLibResult.getJSONObject(i);
                
                //Face images
                JSONObject jsonTargetAttrs= singlejsonCaptureLibResult.getJSONObject("targetAttrs");
                String facePicUrl=jsonTargetAttrs.get("face_picurl").toString();
                //Image Download
                String saveBackImagePath=strWebRootPath+"\\RunHistory\\faceCapture_faceImage_"+strDateTimeNow+".jpg";                
                downloadPicture(facePicUrl,saveBackImagePath);
                
                JSONObject singlejsonPicture=new JSONObject();
                singlejsonPicture.put("desc", "faceImage");
                singlejsonPicture.put("url","/RunHistory/faceCapture_faceImage_"+strDateTimeNow+".jpg");                                                               
                jsonPictureList.put(singlejsonPicture);
            }  
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         
        return jsonPictureList;
    }
    
    //Analysis CityManagement
    public JSONArray parseJsonCityManagement(JSONArray jsonCityManagementResults,String strDateTimeNow)
    {
        //Assemble the foreground to return the json message
        JSONArray jsonPictureList=new JSONArray();
        for (int i =0; i<jsonCityManagementResults.length();i++)
        {
            try {
                //
                JSONObject jsonSingleResult = jsonCityManagementResults.getJSONObject(i);
                String strImageUrl;
                String strImageName="backgroundImageURL"; //node name in json content
                strImageUrl = jsonSingleResult.getString(strImageName);
                if(strImageUrl!="")
                {
                    String strImagePath="\\RunHistory\\CityManagement\\"+strImageName+"_"+i+"_"+strDateTimeNow+".jpg";
                    downloadPicture(strImageUrl,strImagePath);
                    JSONObject singlejsonPicture = new JSONObject();
                    singlejsonPicture.put("desc", strImageName);
                    singlejsonPicture.put("url",strImagePath);                   
                    jsonPictureList.put(singlejsonPicture);                   
                }              
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }           
        }     
        return jsonPictureList;
    }
}
