package com.test.hiki.communicationCom;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;

public class HTTPClientUtil {
	
    public static HttpClient client = new HttpClient(); 
	public static String strIP="";
	public static int  iPort = 0;
    public static String doGet(String url) throws Exception {   
    	
        GetMethod method = new GetMethod(url);  
        method.setDoAuthentication(true);      
        
        int statusCode = client.executeMethod(method);   
        // Return response message  
        byte[] responseBody = method.getResponseBodyAsString().getBytes(method.getResponseCharSet());  
        // Use encoding in the Return response message (utf-8 or gb2312)  
        String response = new String(responseBody, "utf-8"); 
        // Release the connection  
        method.releaseConnection();  
        return response;  
    } 
    
    public static String doPut(String url, String inbound) throws Exception {   

    	PutMethod method = new PutMethod(url);  
        method.setDoAuthentication(true);
        
        method.setRequestBody(inbound);   

        int statusCode = client.executeMethod(method);  
  
        // Return response message  
        byte[] responseBody = method.getResponseBodyAsString().getBytes(method.getResponseCharSet());  
        // Use encoding in the Return response message (utf-8 or gb2312) 
        String response = new String(responseBody, "utf-8"); 
        // Release the connection  
        method.releaseConnection();  
        return response;  
    } 
    
    public static String doPost(String url, String inbound) throws Exception {   
    	
    	PostMethod method = new PostMethod(url);  
        method.setDoAuthentication(true);
        
        method.setRequestBody(inbound);   
        
        int statusCode = client.executeMethod(method);  
  
        // Return response message  
        byte[] responseBody = method.getResponseBodyAsString().getBytes(method.getResponseCharSet());  
        // Use encoding in the Return response message (utf-8 or gb2312) 
        String response = new String(responseBody, "utf-8"); 
        // Release the connection  
        method.releaseConnection();  
        return response;  
    } 
    
    public static String doDelete(String url) throws Exception {   
  	
  	  DeleteMethod method = new DeleteMethod(url);  
      method.setDoAuthentication(true);
               
      int statusCode = client.executeMethod(method);  

      // Return response message  
      byte[] responseBody = method.getResponseBodyAsString().getBytes(method.getResponseCharSet());  
      // Use encoding in the Return response message (utf-8 or gb2312)  
      String response = new String(responseBody, "utf-8"); 
      // Release the connection  
      method.releaseConnection();  
      return response;  
   } 

    //With the binary form POst method
    public static String doPostwithBinaryData(String url, String json, String jsonName, String image, String imageName, String boundary) throws Exception {   
        
        PostMethod method = new PostMethod(url);  
        method.setDoAuthentication(true);
        
        method.addRequestHeader("Accept", "text/html, application/xhtml+xml");
        method.addRequestHeader("Accept-Language", "zh-CN");
        method.addRequestHeader("Content-Type","multipart/form-data; boundary=" + boundary);
        method.addRequestHeader("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
        method.addRequestHeader("Accept-Encoding","gzip, deflate");
        method.addRequestHeader("Connection","Keep-Alive");
        method.addRequestHeader("Cache-Control","no-cache");
        
        String bodyParam = 
                "--" + boundary + "\r\n" 
              + "Content-Disposition: form-data; name=\""+jsonName+"\";\r\n"
              + "Content-Type: text/json\r\n" 
              + "Content-Length: " + Integer.toString(json.length()) + "\r\n\r\n" 
              +  json + "\r\n" 
              + "--" + boundary + "\r\n" 
              + "Content-Disposition: form-data; name=\""+imageName+"\";\r\n"
              + "Content-Type: image/jpeg\r\n" 
              + "Content-Length: " + Integer.toString(image.length()) + "\r\n\r\n" 
              + image 
              + "\r\n--" + boundary + "--\r\n";

        method.setRequestBody(bodyParam);

        // Return response message  
        byte[] responseBody = method.getResponseBodyAsString().getBytes(method.getResponseCharSet());  
        // Use encoding in the Return response message (utf-8 or gb2312) 
        String response = new String(responseBody, "utf-8"); 
        // Release the connection  
        method.releaseConnection();  
        return response;  
    } 

public static String doPostStorageCloud(String url, String json,String faceimage,String boundary) throws Exception {   
    	
    	PostMethod method = new PostMethod(url);  
        method.setDoAuthentication(true);
        
        method.addRequestHeader("Accept", "text/html, application/xhtml+xml");
        method.addRequestHeader("Accept-Language", "zh-CN");
        method.addRequestHeader("Content-Type","multipart/form-data; boundary=" + boundary);
        method.addRequestHeader("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
        method.addRequestHeader("Accept-Encoding","gzip, deflate");
        method.addRequestHeader("Connection","Keep-Alive");
        method.addRequestHeader("Cache-Control","no-cache");
        
        String bodyParam = 
        		"--" + boundary + "\r\n" 
              + "Content-Disposition: form-data; name=\"uploadStorageCloud\";\r\n"
              + "Content-Type: text/json\r\n" 
              + "Content-Length: " + Integer.toString(json.length()) + "\r\n\r\n" 
              +  json + "\r\n" 
              + "--" + boundary + "\r\n" 
              + "Content-Disposition: form-data; name=\"imageData\";\r\n"
              + "Content-Type: image/jpeg\r\n" 
              + "Content-Length: " + Integer.toString(faceimage.length()) + "\r\n\r\n" 
              + faceimage 
              + "\r\n--" + boundary + "--\r\n";

        method.setRequestBody(bodyParam);

        // Return response message  
        byte[] responseBody = method.getResponseBodyAsString().getBytes(method.getResponseCharSet());  
        // Use encoding in the Return response message (utf-8 or gb2312)  
        String response = new String(responseBody, "utf-8"); 
        // Release the connection  
        method.releaseConnection();  
        return response;  
    } 

}
