package com.test.hiki.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jfinal.kit.PathKit;

import com.test.hiki.commonFunction.CommonFunction;

public class FileUtil {

	private static String m_sFolderPath;
    private static int m_FileNumber;
    private static JSONArray m_arrJsonObj = new JSONArray();
    private static Map<String, String> m_mapFileList = new ConcurrentHashMap<String, String>();    //key-ItemName, value-filePath
    
    @SuppressWarnings("resource")
	private static boolean InitFileList() {

        if(CommonFunction.isWindows())
        {
            m_sFolderPath = PathKit.getWebRootPath() + "\\RunHistory\\postman_json";
        }else{
            m_sFolderPath = PathKit.getWebRootPath() + "/RunHistory/postman_json";
        }

        //the files in this dictionary pull into the fileArray all
        File dir = new File(m_sFolderPath);
        File[] files = dir.listFiles();

        if(files.length != m_FileNumber)
        {
            m_FileNumber = files.length;
            try {
				m_arrJsonObj = new JSONArray("[]");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            try{
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].getName().endsWith("json")) { // judge filename whether is end with 'json' or not
                            FileInputStream fJSONObj = null;
                            int iJsonDataLen = 0;

                            fJSONObj = new FileInputStream(files[i]);
                            iJsonDataLen = fJSONObj.available();

                            if(iJsonDataLen < 0) {
                                System.out.println(files[i].getName() + "content is empty!!!");
                                continue;
                            } else{
                                byte[] byFileData = new byte[iJsonDataLen];
                                int iReadCount = 0;

                                while(iReadCount < iJsonDataLen)
                                {
                                    // get the json string from json file
                                    iReadCount += fJSONObj.read(byFileData, iReadCount, iJsonDataLen - iReadCount);
                                }


                                //transform the jsonStr to the jsonObj
                                JSONObject objJSONStr = new JSONObject(CommonFunction.byteToString(byFileData));
                                m_arrJsonObj.put(objJSONStr);

                                //put the map list of "item-name":"filename"
                                m_mapFileList.put(objJSONStr.getJSONObject("info").getString("name"), files[i].getName());
                            }
                        }
                    }
                }
                else
                    return false;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else{
            return false;
        }

        return true;
    }

    public static String GetJSONInfo(){
        InitFileList();
        return m_arrJsonObj.toString();
    }

    //�ݹ��ȡ�����б�
    private static boolean TravelGetObjList(JSONArray arrItem){
        return true;
    }

    public static boolean ModifyFile(String sFileName, JSONObject jsonObj){
        return true;
    }

    public static boolean DeleteFile(String sFileName){
        return true;
    }

    public static boolean AddFile(String sFileName, JSONObject jsonObj){
        return true;
    }

    public static boolean GetFile(File fJSON, StringBuffer sJSONBuffer){
        return true;
    }
}
