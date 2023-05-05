package com.test.hiki.face;
import com.jfinal.core.Controller;

public class Face  extends Controller{
	public void index() {
		render("/UI-Resource/Face/Face.html");
	}
}
