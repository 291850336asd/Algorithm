package com.test.sms;


import java.util.regex.Pattern;


public class smsAction {
	private String tophone;

	private String smstext;


	public String smspost(String tophone,String smstext) throws Exception {
		if(tophone != null && !Pattern.matches("\\w{11,11}", tophone.trim()))
		{
			return "您输入的手机号码必须为11位";
		}else{
			SerialWrite ss=new SerialWrite();
			ss.setTophone(tophone);
			ss.setSmstext(smstext);
			if(ss.SerialR()==1){
				return "发送到"+tophone+"成功";
			}else{
				return "发送失败,请检查串口是否被占用！";
			}
		}
	}


	public String getSmstext() {
		return smstext;
	}
	public void setSmstext(String smstext) {
		this.smstext = smstext;
	}
	public String getTophone() {
		return tophone;
	}
	public void setTophone(String tophone) {
		this.tophone = tophone;
	}



}
