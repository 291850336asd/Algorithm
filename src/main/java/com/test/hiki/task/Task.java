package com.test.hiki.task;

import com.jfinal.core.Controller;

public class Task extends Controller{
	public void index() {
		render("/UI-Resource/Task/Task.html");
	}

}
