package com.test.hiki.mainWorkSpace;

import com.jfinal.core.Controller;

public class MainPage extends Controller{
	
	public void index() {
		render("/UI-Resource/MainPage/MainPage.html");
	}

}
