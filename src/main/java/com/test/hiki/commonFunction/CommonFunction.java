package com.test.hiki.commonFunction;

import org.json.JSONObject;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import javax.swing.JOptionPane;


import com.jfinal.core.Controller;
public class CommonFunction extends Controller{

/**
* @Description Reads the file and returns the corresponding string
* @param filePath
* @return file
*/
 @SuppressWarnings("unused")
public static String openFile(String filePath)
{
	String strFile = "";
	//Create the File
	 File file = new File(filePath);
	 if(file == null)
	 {
		 return "";
	 }
	 else{
		 //Returns the file as a string
		 try{
			 FileInputStream uploadPic = new FileInputStream(file);
			 byte[] bytePic = new byte[uploadPic.available()];
			 uploadPic.read(bytePic);
			 strFile = new String(bytePic, "ISO-8859-1");
			 uploadPic.close();
		}
		 catch (Exception e){
			 e.printStackTrace();
		 }
		 return strFile;
	 }

}

 public static String byteToString(byte[] bytes) {
		if (null == bytes || bytes.length == 0) {
			return "";
		}
		int iLengthOfBytes = 0;
		for(byte st:bytes){
			if(st != 0){
				iLengthOfBytes++;
			}else
				break;
		}
		String strContent = "";
		try {
			strContent = new String(bytes, 0, iLengthOfBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return strContent;
	}

	public static boolean isWindows() {
	        return System.getProperty("os.name").toLowerCase().contains("windows");
	    }
}