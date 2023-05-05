package com.test.hiki.alarm;

import com.jfinal.core.Controller;

public class Alarm  extends Controller{
	public void index() {
		render("/UI-Resource/Alarm/Alarm.html");
	}

}
