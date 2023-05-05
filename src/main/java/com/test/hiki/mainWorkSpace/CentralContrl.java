package com.test.hiki.mainWorkSpace;
import com.jfinal.config.*;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.ext.handler.UrlSkipHandler;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;

import com.test.hiki.HFPD.HFPD;
import com.test.hiki.HFPD.HFPDConfig;
import com.test.hiki.HFPD.HFPDSchedule;
import com.test.hiki.alarm.Alarm;
import com.test.hiki.alarm.Guard;
import com.test.hiki.alarm.HttpHosts;
import com.test.hiki.alarm.Listen;
import com.test.hiki.config.Config;
import com.test.hiki.face.Face;
import com.test.hiki.compare1V1.Compare1V1;
import com.test.hiki.fdlib.FDLib;
import com.test.hiki.fdlib.captureLibSearchByPic;
import com.test.hiki.fdlib.searchByPic;
import com.test.hiki.task.HumanRecognition;
import com.test.hiki.task.ImageAnalysis;
import com.test.hiki.task.ImageAnalysisAuto;
import com.test.hiki.task.Task;
import com.test.hiki.task.VehicleReAnalysis;
import com.test.hiki.task.VideoAnalysis;
public class CentralContrl  extends JFinalConfig{
	public void configConstant(Constants me) {
		me.setDevMode(true);
	}
	public void configRoute(Routes me) {
		me.add("/hikvision", Login.class);
		me.add("/hikvision/hello", MainPage.class);
		me.add("/hikvision/config", Config.class);
		me.add("/hikvision/alarm", Alarm.class);
		me.add("/hikvision/alarm/httphosts", HttpHosts.class);
		me.add("/hikvision/alarm/listen", Listen.class);
		me.add("/hikvision/alarm/Guard", Guard.class);
		me.add("/hikvision/face", Face.class);
		me.add("/hikvision/compare1V1", Compare1V1.class);
		me.add("/hikvision/FDLib", FDLib.class);
		me.add("/hikvision/searchByPic", searchByPic.class);
		me.add("/hikvision/captureLibSearchByPic", captureLibSearchByPic.class);
		me.add("/hikvision/task", Task.class);
		me.add("/hikvision/task/videoAnalysis", VideoAnalysis.class);
		me.add("/hikvision/task/imageAnalysis", ImageAnalysis.class);
		me.add("/hikvision/task/vehicleReAnalysis", VehicleReAnalysis.class);
		me.add("/hikvision/task/humanRecognition", HumanRecognition.class);
		me.add("/hikvision/task/imageAnalysisAuto",ImageAnalysisAuto.class);
		me.add("/hikvision/hfpd",HFPD.class);
		me.add("/hikvision/hfpd/hfpdconfig",HFPDConfig.class);
		me.add("/hikvision/hfpd/hfpdschedule",HFPDSchedule.class);
	}
	public void configEngine(Engine me) {}
	public void configPlugin(Plugins me) {}
	public void configInterceptor(Interceptors me) {}
	@Override
	public void configHandler(Handlers me) {
	    me.add(new UrlSkipHandler("^/websocket", false));	  
	}

}
