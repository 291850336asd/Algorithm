package com.test.hiki.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jfinal.core.Controller;

import com.test.hiki.communicationCom.HTTPClientUtil;
import com.test.hiki.communicationCom.HttpsClientUtil;

public class VideoAnalysis extends Controller{
	public void index() {
		render("/UI-Resource/Task/VideoAnalysis.html");
	}
	//Query whether the device supports video task creation
	public void getVideoTaskAbility(){

        JSONObject jsonRet = null;
        boolean bException=false;
        try{
           
             String strUrl = "";
             if(HttpsClientUtil.bHttpsEnabled)
             {
                //Assemble the URL
                  strUrl= "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/SDT/Management/Task/Video/capabilities?format=json";
                  jsonRet = new JSONObject(HttpsClientUtil.httpsGet(strUrl));
             }
             else
             {
                //Assemble the URL
                  strUrl= "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/SDT/Management/Task/Video/capabilities?format=json";
                  jsonRet = new JSONObject(HTTPClientUtil.doGet(strUrl));
             }
       } catch (JSONException ex) {
           bException = true;
           setAttr("errorMsg", "The device don't support this function!");

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
	//Get task list
	public void getAllTask()
	{
        JSONObject jsonData = new JSONObject();
        JSONArray jsonTaskInfoList =null;
        JSONObject jsonRet = null;
        boolean bException = false;
        try {
            jsonData.put("searchResultPosition", 0);
            jsonData.put("maxResults", 50);
            String strUrl = "";
            if (HttpsClientUtil.bHttpsEnabled) {
                // Assemble the URL
                strUrl = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort
                        + "/ISAPI/SDT/Management/Task/Video/search?format=json";
                jsonRet = new JSONObject(HttpsClientUtil.httpsPost(strUrl, jsonData.toString()));
            } else {
                // Assemble the URL
                strUrl = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort
                        + "/ISAPI/SDT/Management/Task/Video/search?format=json";
                jsonRet = new JSONObject(HTTPClientUtil.doPost(strUrl, jsonData.toString()));
            }

            // Parse the task information to get TaskID
            jsonTaskInfoList = jsonRet.getJSONArray("taskInfoList");;
            JSONArray jsonTaskIDList = new  JSONArray();
            for(int i=0 ;i<jsonTaskInfoList.length();i++)
            {
                String strTaskID = jsonTaskInfoList.getJSONObject(i).getString("taskID");
                JSONObject jsonTaskID =new JSONObject();
                jsonTaskID.put("taskID",strTaskID);
                jsonTaskIDList.put(jsonTaskID);
            }

            //Parse the task information to get the main information
            //jsonTaskInfoListRet = new JSONArray(parseTaskInfoList(jsonTaskInfoList));

            //Get TaskStatus by TaskID
            JSONObject jsonTaskStatusList = new JSONObject(getTaskStatus(jsonTaskIDList));

            if (!jsonTaskStatusList.getString("status").equals(""))
            {
                JSONArray jsonATaskStausList = jsonTaskStatusList.getJSONArray("status");
                // No error message
                for (int i = 0; i < jsonTaskInfoList.length(); i++) {
                    for (int j = 0; j < jsonATaskStausList.length(); j++) {
                        if(jsonTaskInfoList.getJSONObject(i).getString("taskID").equals(jsonATaskStausList.getJSONObject(j).getString("taskID")))
                        {
                            //jsonTaskInfoList.getJSONObject(i).put("taskStatusID",jsonATaskStausList.getJSONObject(j).getString("taskStatus"));
                            int TaskStauts = Integer.parseInt(jsonATaskStausList.getJSONObject(j).getString("taskStatus"));
                            jsonTaskInfoList.getJSONObject(i).put("taskStatus",ChangeTaskStatusIntToString(TaskStauts));
                            jsonTaskInfoList.getJSONObject(i).put("process",jsonATaskStausList.getJSONObject(j).getString("process"));
                            break;
                        }
                    }
                }
            }
            else if (!jsonTaskStatusList.getString("errorMsg").equals(""))
            {
                this.renderJson(jsonTaskStatusList);
            }

        } catch (Exception e) {
            bException = true;
            setAttr("errorMsg", e.toString());
        } finally {
            if (bException) {
                // Exception returns exception information directly
                this.renderJson();
            } else {
                // Normal interaction, return the status information returned by the device
                this.renderJson(jsonTaskInfoList.toString());
            }
        }
	}


	//The status query TaskStatus
	public String getTaskStatus(JSONArray jsonTaskIDList ){

	    JSONObject jsonRet = null;
        JSONObject jsonData = new JSONObject();

        boolean bException = false;
        String strErrorMsg = ""; //It must be in json format

        try {
            jsonData.put("taskInfo", jsonTaskIDList);

            String strUrl = "";
            if (HttpsClientUtil.bHttpsEnabled) {
                // Assemble the URL
                strUrl = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort
                        + "/ISAPI/SDT/Management/Task/status?format=json";
                jsonRet = new JSONObject(HttpsClientUtil.httpsPost(strUrl, jsonData.toString()));
            } else {
                // Assemble the URL
                strUrl = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort
                        + "/ISAPI/SDT/Management/Task/status?format=json";
                jsonRet = new JSONObject(HTTPClientUtil.doPost(strUrl, jsonData.toString()));
            }

        } catch (Exception e) {
            bException = true;
            strErrorMsg = "{\"errorMsg\":\"" + e.toString() + "\"}";   //StrErrorMsg is returned as a string in json format
        }

        if(bException){
            // Exception returns exception information directly
            return strErrorMsg;
        }
        return jsonRet.toString();
	}

	//Task submitted
 	public void uploadTask(){

		JSONObject jsonData = new JSONObject();
        JSONObject jsonRet = null;
        boolean bException = false;

        try {
            // Assemble json data
            JSONObject jsontaskInfoData = new JSONObject();

            JSONObject jsonStreamData = new JSONObject();
            jsonStreamData.put("streamUrl", getPara("streamUrl"));
            if(!getPara("userName").equals("")){
                jsonStreamData.put("userName", getPara("userName"));
            }
            if(!getPara("passWord").equals("")){
                jsonStreamData.put("passWord", getPara("passWord"));
            }
            jsontaskInfoData.put("stream", jsonStreamData);

            //taskInfo->time
            JSONObject jsonTimeData = new JSONObject();
            jsonTimeData.put("taskType", getPara("taskType"));

            //taskInfo->time->tempInfo
            //Time converted to UTC time
            SimpleDateFormat timezone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            timezone.setTimeZone(TimeZone.getTimeZone("UTC"));

            JSONObject jsonTimeTempInfo =new JSONObject();
            jsonTimeTempInfo.put("startTime", timezone.format(getParaToDate("startTime")));
            jsonTimeTempInfo.put("endTime", timezone.format(getParaToDate("endTime")));

            jsonTimeData.put("tempInfo", jsonTimeTempInfo);
            jsontaskInfoData.put("time", jsonTimeData);

            //taskInfo->destination[]
            JSONArray jsonDstArray =new JSONArray();

            JSONObject jsonDstData = new JSONObject();
            jsonDstData.put("destinationType", getPara("destinationType"));
            jsonDstData.put("destinationUrl", getPara("destinationUrl"));
            jsonDstData.put("userName", getPara("dstUserName"));
            jsonDstData.put("password", getPara("dstPassWord"));
            jsonDstArray.put(jsonDstData);

            jsontaskInfoData.put("destination", jsonDstArray);


            jsontaskInfoData.put("taskName", getPara("taskName"));
            jsontaskInfoData.put("algorithmType", getParaToInt("algorithmType"));
            jsontaskInfoData.put("streamType", getPara("streamType"));
            jsontaskInfoData.put("taskPriority", getPara("taskPriority"));

            jsonData.put("taskInfo", jsontaskInfoData);

            String strUrl = "";
            // Upload images to the server
            if (HttpsClientUtil.bHttpsEnabled) {
                // Assemble the URL
                strUrl = "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort
                        + "/ISAPI/SDT/Management/Task/Video?format=json";
                jsonRet = new JSONObject(HttpsClientUtil.httpsPost(strUrl, jsonData.toString()));
            } else {
                // Assemble the URL
                strUrl = "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort
                        + "/ISAPI/SDT/Management/Task/Video?format=json";
                jsonRet = new JSONObject(HTTPClientUtil.doPost(strUrl, jsonData.toString()));
            }

        } catch (Exception e) {
            bException = true;
            setAttr("errorMsg", e.toString());
        } finally {
            if (bException) {
                // Exception returns exception information directly
                this.renderJson();
            } else {
                // Normal interaction, return the status information returned by the device
                this.renderJson(jsonRet.toString());
            }

        }
    }

	//Suspended task
	public void pauseTask(){

		JSONObject jsonData = new JSONObject();
        JSONObject jsonRet = null;
        boolean bException=false;

        try{
            ArrayList<JSONObject> jsonTaskInfoDataList = new ArrayList<JSONObject>();

            String taskIDs =getPara("taskID");
            String[] arrTaskID = taskIDs.split(",");

            for(int i =0 ;i< arrTaskID.length;i++)
            {
                JSONObject jsonTaskInfoData = new JSONObject();
                jsonTaskInfoData.put("taskID", arrTaskID[i]);
                jsonTaskInfoDataList.add(jsonTaskInfoData);
            }

            jsonData.put("taskInfo", jsonTaskInfoDataList);
			 String strUrl = "";
	         if(HttpsClientUtil.bHttpsEnabled)
	         {
	        	//Assemble the URL
	              strUrl= "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/SDT/Management/Task/pause?format=json";
	              jsonRet = new JSONObject(HttpsClientUtil.httpsPost(strUrl, jsonData.toString()));
	         }
	         else
	         {
	        	//Assemble the URL
	              strUrl= "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/SDT/Management/Task/pause?format=json";
	              jsonRet = new JSONObject(HTTPClientUtil.doPost(strUrl, jsonData.toString()));
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

	//restart Task
	public void restartTask(){

		JSONObject jsonData = new JSONObject();
        JSONObject jsonRet = null;
        boolean bException=false;

        try{
        	ArrayList<JSONObject> jsonTaskInfoDataList = new ArrayList<JSONObject>();

            String taskIDs =getPara("taskID");
            String[] arrTaskID = taskIDs.split(",");

            for(int i =0 ;i< arrTaskID.length;i++)
            {
                JSONObject jsonTaskInfoData = new JSONObject();
                jsonTaskInfoData.put("taskID", arrTaskID[i]);
                jsonTaskInfoDataList.add(jsonTaskInfoData);
            }

            jsonData.put("taskInfo", jsonTaskInfoDataList);
			 String strUrl = "";
	         if(HttpsClientUtil.bHttpsEnabled)
	         {
	        	//Assemble the URL
	              strUrl= "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/SDT/Management/Task/resume?format=json";
	              jsonRet = new JSONObject(HttpsClientUtil.httpsPost(strUrl, jsonData.toString()));
	         }
	         else
	         {
	        	//Assemble the URL
	              strUrl= "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/SDT/Management/Task/resume?format=json";
	              jsonRet = new JSONObject(HTTPClientUtil.doPost(strUrl, jsonData.toString()));
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

	//modify Task
	public void modifyTask(){

		JSONObject jsonData = new JSONObject();
        JSONObject jsonRet = null;
        boolean bException=false;

        String taskID = getPara("taskID");

        try{
	        //Assemble json data
			JSONObject jsontaskInfoData = new JSONObject();

			//taskInfo->time
			JSONObject jsonTimeData = new JSONObject();
			jsonTimeData.put("taskType", getPara("taskType"));

			//taskInfo->time->tempInfo
            //Time converted to UTC time
            SimpleDateFormat timezone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            timezone.setTimeZone(TimeZone.getTimeZone("UTC"));

            JSONObject jsonTimeTempInfo =new JSONObject();
            jsonTimeTempInfo.put("startTime", timezone.format(getParaToDate("startTime")));
            jsonTimeTempInfo.put("endTime", timezone.format(getParaToDate("endTime")));

            jsonTimeData.put("tempInfo", jsonTimeTempInfo);
			jsontaskInfoData.put("time",jsonTimeData);

            JSONObject jsonStreamData = new JSONObject();
            jsonStreamData.put("streamUrl",getPara("streamUrl"));
            if(!getPara("userName").equals("")){
                jsonStreamData.put("userName", getPara("userName"));
            }
            if(!getPara("passWord").equals("")){
                jsonStreamData.put("passWord", getPara("passWord"));
            }
            jsontaskInfoData.put("stream",jsonStreamData);

            //taskInfo->destination[]
            JSONArray jsonDstArray =new JSONArray();

            JSONObject jsonDstData = new JSONObject();
            jsonDstData.put("destinationType", getPara("destinationType"));
            jsonDstData.put("destinationUrl", getPara("destinationUrl"));

            jsonDstData.put("userName", getPara("dstUserName"));
            jsonDstData.put("password", getPara("dstPassWord"));
            jsonDstArray.put(jsonDstData);

            jsontaskInfoData.put("destination", jsonDstArray);

			jsontaskInfoData.put("taskName",getPara("taskName"));
			jsontaskInfoData.put("algorithmType",getParaToInt("algorithmType"));
			jsontaskInfoData.put("streamType",getPara("streamType"));
			jsontaskInfoData.put("taskPriority",getPara("taskPriority"));

			jsonData.put("taskInfo", jsontaskInfoData);
			 String strUrl = "";
	         if(HttpsClientUtil.bHttpsEnabled)
	         {
	        	//Assemble the URL
	              strUrl= "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/SDT/Management/Task/Video?format=json&taskID=" + taskID;
	              jsonRet = new JSONObject(HttpsClientUtil.httpsPut(strUrl, jsonData.toString()));
	         }
	         else
	         {
	        	//Assemble the URL
	              strUrl= "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/SDT/Management/Task/Video?format=json&taskID=" + taskID;
	              jsonRet = new JSONObject(HTTPClientUtil.doPut(strUrl, jsonData.toString()));
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

	//Delete the task
	public void delTask(){
		JSONObject jsonData = new JSONObject();
        JSONObject jsonRet = null;
        boolean bException=false;
        try{
        	ArrayList<JSONObject> jsonTaskInfoDataList = new ArrayList<JSONObject>();


        	String taskIDs =getPara("taskID");
        	String[] arrTaskID = taskIDs.split(",");

        	for(int i =0 ;i< arrTaskID.length;i++)
        	{
        	    JSONObject jsonTaskInfoData = new JSONObject();
        	    jsonTaskInfoData.put("taskID", arrTaskID[i]);
        	    jsonTaskInfoDataList.add(jsonTaskInfoData);
        	}

        	jsonData.put("taskInfo", jsonTaskInfoDataList);
			 String strUrl = "";
	         if(HttpsClientUtil.bHttpsEnabled)
	         {
	        	//Assemble the URL
	              strUrl= "https://" + HttpsClientUtil.strIP + ":" + HttpsClientUtil.iPort + "/ISAPI/SDT/Management/Task/delete?format=json";
	              jsonRet = new JSONObject(HttpsClientUtil.httpsPost(strUrl, jsonData.toString()));
	         }
	         else
	         {
	        	//Assemble the URL
	              strUrl= "http://" + HTTPClientUtil.strIP + ":" + HTTPClientUtil.iPort + "/ISAPI/SDT/Management/Task/delete?format=json";
	              jsonRet = new JSONObject(HTTPClientUtil.doPost(strUrl, jsonData.toString()));
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

	public String ChangeTaskStatusIntToString(int iTaskStaus)
	{
	    String strTaskStauts="";
	    switch(iTaskStaus)
	    {
	    case 1:
	        strTaskStauts = "Not Dispatched";
	        break;
        case 2:
            strTaskStauts = "Waiting";
            break;
        case 3:
            strTaskStauts = "Executing";
            break;
        case 4:
            strTaskStauts = "Completed";
            break;
        case 5:
            strTaskStauts = "Deleted";
            break;
        case 6:
            strTaskStauts = "Offline";
            break;
        case 7:
            strTaskStauts = "Server Down";
            break;
        case 8:
            strTaskStauts = "Stopping";
            break;
        case 9:
            strTaskStauts = "Stopped";
            break;
        case 10:
            strTaskStauts = "Rebooting";
            break;
        case 11:
            strTaskStauts = "Paused";
            break;
        case 12:
            strTaskStauts = "Pausing";
            break;
        case 13:
            strTaskStauts = "Failed";
            break;
        case 14:
            strTaskStauts = "Not Exist";
            break;
	    }
	    return strTaskStauts;
	}
}
