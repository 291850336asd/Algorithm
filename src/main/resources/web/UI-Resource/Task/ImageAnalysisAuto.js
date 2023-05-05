/**
 * 
 */
    /*------------------------------------websocket处理区------------------------------ */
    var websocket = null;
    //判断当前浏览器是否支持WebSocket
    if ('WebSocket' in window) {
    	//alert('当前浏览器support websocket');    	
    }
    else {
        alert('当前浏览器 Not support websocket')
    }
    websocket = new WebSocket("ws://localhost:8080/websocket");  
    //连接发生错误的回调方法
    websocket.onerror = function () {
    	alert("WebSocket连接发生错误");
    };
    //连接成功建立的回调方法
    websocket.onopen = function () {
    	//alert("WebSocket连接成功");
    }
    //接收到消息的回调方法
    websocket.onmessage = function (event) {
    	//alert(event.data);
    	var jsonFormatInfo=JSON.parse(event.data); 
        ShowDIVAuto(jsonFormatInfo);
    }
    //连接关闭的回调方法
    websocket.onclose = function () {
    	//alert("WebSocket连接关闭");
    }
    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
        closeWebSocket();
    }
    //将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        document.getElementById('message').innerHTML += innerHTML + '<br/>';
    }
    //关闭WebSocket连接
    function closeWebSocket() {
        websocket.close();
    }
    
  //websocket发送消息示例
    function send() {
       
       websocket.send(message);          	   	
    }
    
    /*---------------------------------界面加载处理区-----------------------------*/
    window.onload=function(){
    	//document.getElementById("CityManagementRuleDiv").style.visibility="hidden";	
    	//document.getElementById("RuleParamsListDiv").style.visibility="hidden";
    	
    }
    
    function contextMenu(){
        return false;
    }
    
    /*----------------------------------变量声明区------------------------------*/
    var sensitivity=7; //灵敏度,[1,100]
    var confidence=0.8;//,置信度(千寻算法参数),[0.0-1.0],默认0.8
    var detectNumber=1;//检测目标数(千寻算法参数),[1,600],默认1
    var durationTimeThreshold=10;//持续时间阀值(分)(千寻算法参数),[1,1440],默认10
    
    var canvas = document.getElementById("canvas");  //获取Canvas画布对象
    var context = canvas.getContext('2d'); //获取2D上下文对象，大多数Canvas API均为此对象方法
    
    var ApplySceneInfo=null;	//从设备获取的ApplyScene
    var currentParamsInfo=null; //当前界面展示的可变参数信息
    
    var loading=false;
     /* 城管事件规则参数*/
    function cityManagementRule(sensitivity,rectID,confidence,detectNumber,durationTimeThreshold,multiViolation,polygons/*,rulesParam*/){
        this.sensitivity=sensitivity;//灵敏度
        this.rectID=rectID;
        this.confidence=confidence;
        this.detectNumber=detectNumber;
        this.durationTimeThreshold=durationTimeThreshold;
        this.multiViolation=multiViolation;
        this.polygons=polygons;
    }

    /*规则事件列表参数*/
    function ruleParam(algorithmID,scene,ruleID,ruleCustomName,eventType,ParamsInfo){
        this.algorithmID=algorithmID;//灵敏度
        this.scene=scene;
        this.ruleID=ruleID;
        this.ruleCustomName=ruleCustomName;   
        this.eventType=eventType;     
        this.ParamsInfo=ParamsInfo;
    }


    //规则ID-规则配置对应表
    var ruleID_config_map=new Map();
    //图片-规则列表对应表
    var pic_rulelist_map=new Map();
    //图片-图片参数对应表
    var pic_cityManagement_map=new Map();
    //当前规则列表
    var current_rulelist=null;

    //图片列表-图片对应表
    var pic_map=new Map();
    //当前选中图片名称
    var current_pic=null;
    /*-------------------------------------------------------------------------------*/


    /*----------------------------------滑动条控件处理区----------------------------------*/
    $('#sensitivity').slider({
        formatter: function (value) {
            return 'sensitivity value: ' + value;
        }
    }).on('slide', function (slideEvt) {
        //当滚动时触发
    }).on('change', function (e) {
        //当值发生改变的时候触发
        sensitivity=e.value.newValue;
    });
    $('#confidence').slider({
        formatter: function (value) {
            return 'confidence value: ' + value;
        }
    }).on('slide', function (slideEvt) {
        //当滚动时触发
    }).on('change', function (e) {
        //当值发生改变的时候触发
    	confidence=e.value.newValue;
    });
    $('#detectNumber').slider({
        formatter: function (value) {
            return 'detectNumber value: ' + value;
        }
    }).on('slide', function (slideEvt) {
        //当滚动时触发
    }).on('change', function (e) {
        //当值发生改变的时候触发
    	detectNumber=e.value.newValue;
    });
    $('#durationTimeThreshold').slider({
        formatter: function (value) {
            return 'durationTimeThreshold value: ' + value;
        }
    }).on('slide', function (slideEvt) {
        //当滚动时触发
    }).on('change', function (e) {
        //当值发生改变的时候触发
    	durationTimeThreshold=e.value.newValue;
    });
   /*-------------------------------------------------------------------------------*/
    
    /*----------------------------------画布处理区----------------------------------*/

    canvas.oncontextmenu=contextMenu;
    context.font = "18px bold 黑体";// 设置字体
    context.fillStyle = "#ff0"; // 设置颜色
    context.textAlign = "center";   // 设置水平对齐方式
    context.textBaseline = "middle";    // 设置垂直对齐方式
    const cWidth = canvas.width, cHeight = canvas.height; //获取画布大小
    var image = document.getElementById("Picture");    //获取图片所在资源
    var cvsClientRect = canvas.getBoundingClientRect();  //获取canvas元素在页面上的位置信息
    /*
    .top:元素上边到视窗上边的距离;
    .right:元素右边到视窗左边的距离;
    .bottom:元素下边到视窗上边的距离;
    .left:元素左边到视窗左边的距离;
    .width:是元素自身的宽
    .height:是元素自身的高
    .x:元素原点的x坐标
    .y:元素原点的y坐标
    */
    
    var polygonArray=new Array();   //多边形集合
    var points = [];//当前正在绘制的多边形顶点集合

    image.onload=function(){
        if(currentPicName==null){
            context.drawImage(image, 0, 0, cWidth, cHeight);//初始加载图片
        }else{
            ShowPictureConfig(currentPicName);//每次再加图片后，对图片进行绘制初始化和配置初始化
        }
       
    };

    image.src="/UI-Resource/MainPage/background.jpg"; //初始化图片 

    function DrawPicture(){
    	if(document.getElementById("drawPicture").innerText=="DrawPicture"){
    		canvas.addEventListener("mousedown", mousedownHandler, false);
    		document.getElementById("drawPicture").innerText="StopDraw";
    	}else if(document.getElementById("drawPicture").innerText=="StopDraw"){
    		canvas.removeEventListener("mousedown", mousedownHandler);
    		canvas.removeEventListener("mousemove", mousemoveHandler);
    		document.getElementById("drawPicture").innerText="DrawPicture";
    	}
    	
    }
    
    /*鼠标点击操作处理*/
    function mousedownHandler(event){
        if(event.button == 0) {
            points.push({
                x: event.pageX - cvsClientRect.x,
                y: event.pageY - cvsClientRect.y
            });
            if(points.length>=1) {
                canvas.addEventListener("mousemove", mousemoveHandler, false);
            }
            drawPolygon(points);
        } else if(event.button === 2) {   
            if(points.length>=3){
            	
            	points.push({
                    x: event.pageX - cvsClientRect.x,
                    y: event.pageY - cvsClientRect.y
                });
            	
                canvas.removeEventListener("mousemove", mousemoveHandler); 
                polygonArray.push(points);
                context.fillText("polygonArray:"+(polygonArray.length-1), points[0].x, points[0].y);    // 绘制文字（参数：要写的字，x坐标，y坐标）
                points=[];
            }else{
                alert("please draw move line to a polygon")
            }
            
            
        }            
    }
    
    /*鼠标点击后移动操作处理*/
    function mousemoveHandler(event) {
        drawPolygon(points.concat({
            x: event.pageX- cvsClientRect.x,
            y: event.pageY- cvsClientRect.y
        }));
    }
    
    /*多边形绘制处理*/
    function drawPolygon(points) {
        context.clearRect(0,0,cWidth,cHeight);
        context.drawImage(image, 0, 0, cWidth, cHeight);
        context.strokeStyle = '#FF0000';
        context.beginPath();
        /*判断是否已有框,有则先画已有的框*/
        if(polygonArray.length>0){
           for(var i=0;i<polygonArray.length;i++){
               var tempPoints=polygonArray[i];
               context.moveTo(tempPoints[0].x,tempPoints[0].y);
               for(var j=1;j<tempPoints.length;j++){
                   context.lineTo(tempPoints[j].x,tempPoints[j].y); 
               }
               context.lineTo(tempPoints[0].x,tempPoints[0].y); 
               context.fillText("polygonArray:"+i, tempPoints[0].x, tempPoints[0].y);    // 绘制文字（参数：要写的字，x坐标，y坐标）  
           }                 
        }
        
        if(points!=null){
           context.moveTo(points[0].x,points[0].y);
           for(var i=1;i<points.length;i++) {
        	   context.lineTo(points[i].x,points[i].y);
           }
        }

        context.closePath();
        context.stroke();
    }    
    
    /*-------------------------------------------------------------------------------*/

    /*----------------------------------图片列表_图片处理区----------------------------------*/

    var pictureFile=null; //声明图片二进制流
    var prePicName=null; //上一张图片名称
    var prePictureItem=null; //上一张图片
    var currentPicName=null; //当前图片

    /*初始化图片列表 & 图片列表点击事件 */
    $('#tb_PictureList').bootstrapTable({
        height: 600,
        width:200,
        singleSelect: true,
        columns: [
            [   
                {
                    field:"PictureList",title:"PictureList", width:"100%" ,
                }  
            ]
        ],
        onClickRow: function (row, $element) {
            //先刷新表格颜色
            for(var i=1; i<document.all.tb_PictureList.rows.length; i++)   {   
                document.all.tb_PictureList.rows[i].style.backgroundColor = "";   
                document.all.tb_PictureList.rows[i].tag = false;   
            }
            //加载选中行图片列表
            if(row.PictureList.length>0)
            {
                if(prePictureItem==null){
                    $element[0].style.backgroundColor = "#FFC0CB"; 
                    prePictureItem=$element;
                    currentPicName=row.PictureList;
                    image.src=pic_map.get(row.PictureList);
                }else{
                    if(pic_rulelist_map.has(prePictureItem[0].cells[0].innerText) || pic_cityManagement_map.has(prePictureItem[0].cells[0].innerText)){
                        if(pic_rulelist_map.has(row.PictureList)){
                            $element[0].style.backgroundColor = "#98FB98"; 
                        }else{
                            $element[0].style.backgroundColor = "#FFC0CB"; 
                        }
                        prePictureItem=$element;
                        currentPicName=row.PictureList;
                        image.src=pic_map.get(row.PictureList);  
                    }else{
                    	prePictureItem[0].style.backgroundColor = "#FFC0CB"; 
                    	if(prePictureItem[0].cells[0].innerText!=row.PictureList){
                    		alert("Please Save PictureConfig first");
                    	}  
                        return;
                    }
                }

            }
        }
    });
    
    
    /*图片导入函数*/
    function ImportPicture(){    	
    	loading=false;
        document.getElementById("btn_file").click();     
    }

    /*增加图片并插入列表中*/
    function fileComplete(){   
    	var file=document.getElementById('btn_file').files[0];
        var name=file.name;
        var src=null;
        if(window.FileReader){
        	var reader = new FileReader();
        	reader.onload = function (e) { 
        		pictureFile=reader.result;
        		//CreatePicUrl(name,pictureFile);
            }
        	reader.readAsBinaryString(file);
            src = window.URL.createObjectURL(file); //转成可以在本地预览的格式;
        }else{
        	alert("Not supported by your browser!");
        }
          
        //检查图片是否重复
        if(pic_map.has(name)){
        	alert("图片名称重复,请修改图片名称");
        	return;
        }else if(src==null){
        	alert("图片导入失败");
        	return;
        }     
        pic_map.set(name,src);
        $('#tb_PictureList').bootstrapTable('insertRow', {
            index: 0,	
            row:  {
            	PictureList:name
            }
        }); 
        
        var tbtmp=document.getElementById("tb_PictureList");
        tbtmp.childNodes[1].childNodes[0].backgroundColor = "#FFC0CB"; 
        document.getElementById("CityManagementRuleDiv").style.visibility="visible";
		document.getElementById("RuleParamsListDiv").style.visibility="visible";
		
		currentPicName=name;
		image.src=src; 
    }
    
    /*图片转成url，用于界面展示 */
    function CreatePicUrl(picName,picFile){
        var strJson={"picName":picName,"picFile":picFile}
        jQuery.ajax({
            url:"/hikvision/task/imageAnalysisAuto/CreatePicUrl",
            data:strJson,
            type:"POST",
            success:function(obj){
                alert("success");
            },
	        error:function(){
	            alert("Communication exception, please check!");
	        },
	        complete:function(obj){
	        }
        })
    }
    
    /*图片配置保存*/
    function SavePictureConfig(){
    	
    	if(currentPicName==null){
    		alert("Please choose a picture First!");
            return;
    	}
    	if(document.getElementById("drawPicture").innerText=="StopDraw"){
    		alert("Please stop picture draw!");
            return;
    	}
    	var multiViolation=$("#multiViolation option:selected").text();
        var rectID=$("#rectID").val();
        var citymanagementrule= new cityManagementRule(sensitivity,rectID,confidence,detectNumber,durationTimeThreshold,multiViolation,polygonArray);
        pic_cityManagement_map.set(currentPicName,citymanagementrule)

    	var ruleConfigMap=new Map();
    	ruleID_config_map.forEach(function(value,key) {
    		ruleConfigMap.set(key,value);
    	});
        pic_rulelist_map.set(currentPicName,ruleConfigMap);
        alert("save success picName: "+currentPicName);
    }
    
    /*展示图片配置*/
    function ShowPictureConfig(picName){
        /*图片参数的展示*/
    	var select = document.getElementById("multiViolation");
        if(pic_cityManagement_map.has(picName)){
            $('#sensitivity').slider('setValue',pic_cityManagement_map.get(picName).sensitivity);
            $("#rectID").val(pic_cityManagement_map.get(picName).rectID);
            $('#confidence').slider('setValue',pic_cityManagement_map.get(picName).confidence);
            $('#detectNumber').slider('setValue',pic_cityManagement_map.get(picName).detectNumber);
            $('#durationTimeThreshold').slider('setValue',pic_cityManagement_map.get(picName).durationTimeThreshold);
            //$("select#multiViolation").val(pic_cityManagement_map.get(picName).multiViolation);
            for(var i=0;i<select.options.length;i++){
            	if(select.options[i].text==pic_cityManagement_map.get(picName).multiViolation){
            		select.options[i].selected=true;
            	}
            }
            polygonArray=pic_cityManagement_map.get(picName).polygons; 
            $('#multiViolation').selectpicker('refresh');
        }else{
            polygonArray=[];
            $('#sensitivity').slider("refresh"); 
            $("#rectID").val(" ");
            $('#confidence').slider("refresh"); 
            $('#detectNumber').slider("refresh"); 
            $('#durationTimeThreshold').slider("refresh"); 
            select.options[0].selected=true;
            $('#multiViolation').selectpicker('refresh');
            polygonArray=[];
        }
        drawPolygon(null);

        /*规则框的展示 */
    	ShowRuleParam(null,false,true);
    	//初始化规则表
    	ruleID_config_map.clear();
        if(pic_rulelist_map.has(picName)){
        	let tempRuleConfigMap=new Map();
        	tempRuleConfigMap=pic_rulelist_map.get(picName);
        	tempRuleConfigMap.forEach(function(value,key) {
        		ruleID_config_map.set(key,value);
        	});
            if(ruleID_config_map.size>0){
                /*先清空表格现有数据*/
                for(var i=1; i<document.all.tb_RuleList.rows.length;){  
                	if(document.all.tb_RuleList.rows[i].className=="hide"){
                		i++;
                		continue;
                	}
                    document.all.tb_RuleList.rows[i].remove();   
                }
                /*按保存的数据进行添加*/
                ruleID_config_map.forEach(function(value,key) {
                    var $clone = $('#table_rule').find('tr.hide').clone(true).removeClass('hide table-line'); 
                    $clone[0].cells[0].innerText=key;  
                    $clone[0].cells[0].contentEditable=false; 
                    $clone[0].cells[1].innerText=value.ruleCustomName;  
                    $clone[0].cells[1].contentEditable=false; 
                    $clone[0].cells[3].childNodes[1].innerText="Mod";
                    $('#table_rule').find('table').append($clone);
                })
            }else{
                /*清空表格现有数据*/
                for(var i=1; i<document.all.tb_RuleList.rows.length;){ 
                	if(document.all.tb_RuleList.rows[i].className=="hide"){
                		i++;
                		continue;
                	}
                    document.all.tb_RuleList.rows[i].remove();   
                } 
            }
        }else{
            
            /*清空表格现有数据*/
            for(var i=1; i<document.all.tb_RuleList.rows.length;){ 
            	if(document.all.tb_RuleList.rows[i].className=="hide"){
            		i++;
            		continue;
            	}
                document.all.tb_RuleList.rows[i].remove();   
            }   
        }
    }
	/*-------------------------------------------------------------------------------*/   
    

    /*----------------------------------规则列表_规则参数处理区----------------------------------*/

    var preclickItem=null; //点击另一行时，当前行的元素
    var preChanging=false;//点击另一行时，当前行表示的规则是否正在修改,每添加一个规则，需要对改规则进行保存，才能进行下一个规则的配置

    /*规则框动态添加行*/
    $('.table-add').click(function () {
      var $clone = $('#table_rule').find('tr.hide').clone(true).removeClass('hide table-line');      
      $('#table_rule').find('table').append($clone);
    });
    /*规则框动态删除行*/
    $(document).on('click', '.table-remove', function(e){
    	var trd=$(this).parents('tr');
        if(ruleID_config_map.has(trd[0].cells[0].innerText)){
        	ruleID_config_map.delete(trd[0].cells[0].innerText);
        }
        trd.detach();
    });
    
    /*规则保存按钮事件处理*/
    $("#tb_RuleList").on("click",":button",function(e){
    	var oObj = window.event.srcElement;
    	var oTr = oObj.parentNode.parentNode; //button->td->tr   	
    	if(oTr.cells[3].innerText=="Mod"){	
       	    oTr.cells[1].contentEditable=true;
       	    oTr.cells[3].childNodes[1].innerText="Save";
       	    oTr.style.backgroundColor = "#FFC0CB"; 
       	    preChanging=true;
       	    preclickItem=oTr;
        }else{
        	if(oTr.cells[0].innerText=="Untitled"){
    			alert("RuleID illegal,please change");
    		}else if(ruleID_config_map.has(oTr.cells[0].innerText) &&preChanging==false){
    			alert("ruleID is already existence,please change");
    		}else{
    			/*设置编辑框不可用*/
        		oTr.cells[0].contentEditable=false;
           	    oTr.cells[1].contentEditable=false;
           	    oTr.cells[3].childNodes[1].innerText="Mod";
    			oTr.style.backgroundColor = "#98FB98";   			
    			
    			var ruleparam=null;
                if(currentParamsInfo==null){
                    ruleparam=new ruleParam(document.getElementById("algorithmID").value,$('#scene option:selected').text(),oTr.cells[0].innerText,
                		oTr.cells[1].innerText,$('#eventType option:selected').text(),null);
                }else{
                    var divPanel=document.getElementById("ruleConfigPanel");
                    for(var i=0;i<divPanel.childElementCount;i++){
                        for(var j=0;j<currentParamsInfo.ParamsInfo.length;j++){
                            if(divPanel.childNodes[i].childNodes[1].childNodes[0].id==currentParamsInfo.ParamsInfo[j].paramName){
                                currentParamsInfo.ParamsInfo[j].defaultValue=divPanel.childNodes[i].childNodes[1].childNodes[0].value;
                                break;
                            }
                            
                        }
                    }
                    var tmpParamsInfo=JSON.stringify(currentParamsInfo);
                    ruleparam=new ruleParam(document.getElementById("algorithmID").value,document.getElementById("scene").value,oTr.cells[0].innerText,
                		oTr.cells[1].innerText,document.getElementById("eventType").value,tmpParamsInfo);
                } 
    			
    			ruleID_config_map.set(oTr.cells[0].innerText,ruleparam);
                preChanging=false;
                preclickItem=oTr;

    		}  	
        }
    }); 

    /*单个规则参数展示 ruleID:规则ID; change:是否换行展示*/
    function ShowRuleParam(ruleID,change,init){
        var scene_select=document.getElementById("scene");
        var eventType=document.getElementById("eventType");
        if(init){
            //初始化展示
            document.getElementById("algorithmID").value="";
            scene.options[0].selected=true;
            $('#scene').selectpicker('refresh');
            InitEventType(scene.options[0].value)
            eventType.options[0].selected=true;
            $('#eventType').selectpicker('refresh');
            ShowDIVAuto(ApplySceneInfo);
            return;
        }
        if(change){
            /*参数更新 */
            if(ruleID_config_map.has(ruleID)){
            	document.getElementById("algorithmID").value=ruleID_config_map.get(ruleID).algorithmID;
                currentParamsInfo=JSON.parse(ruleID_config_map.get(ruleID).ParamsInfo);
                for(var i=0;i<scene.options.length;i++){
                    if(scene.options[i].value==ruleID_config_map.get(ruleID).scene){
                        scene.options[i].selected=true;
                        break;
                    }    
                }
                $('#scene').selectpicker('refresh');
                InitEventType(ruleID_config_map.get(ruleID).scene);
                
                for(var i=0;i<eventType.options.length;i++){
                    if(eventType.options[i].value==ruleID_config_map.get(ruleID).eventType){
                        eventType.options[i].selected=true;
                    }
                }
                $('#eventType').selectpicker('refresh');
                
                ShowParamInfo(currentParamsInfo);
            }
        }else{
            /*参数不更新,仅刷新表格颜色 */
            for(var i=1; i<document.all.tb_RuleList.rows.length; i++){   
                document.all.tb_RuleList.rows[i].style.backgroundColor   =   "";   
                document.all.tb_RuleList.rows[i].tag = false;   
            }
        }
    }
    
    function InitEventType(sceneValue){
    	let eventType=document.getElementById("eventType");
    	eventType.options.length=0;
        for(var i=0;i<ApplySceneInfo.ApplyScene.length;i++){
            if(ApplySceneInfo.ApplyScene[i].description==sceneValue){
                for(var j=0;j<ApplySceneInfo.ApplyScene[i].EventCfg.length;j++){
                	$('#eventType').append('<option value="'+ApplySceneInfo.ApplyScene[i].EventCfg[j].eventDescription+'">' +ApplySceneInfo.ApplyScene[i].EventCfg[j].eventDescription+ '</option>');
                }
                break;
            }
        }
        $('#eventType').selectpicker('refresh');
    }


    /*换行处理函数*/
    function change(change) {
        var oObj = window.event.srcElement;
        if(oObj.tagName.toLowerCase() == "td"){   
          var oTr = oObj.parentNode;   
          for(var i=1; i<document.all.tb_RuleList.rows.length; i++)   {   
            document.all.tb_RuleList.rows[i].style.backgroundColor   =   "";   
            document.all.tb_RuleList.rows[i].tag = false;   
          }

          if(preclickItem!=null && preclickItem==oTr){
              if(preChanging==true){
                  if(preclickItem.cells[3].childNodes[1].innerText.toLowerCase()=="save"){
                      ShowRuleParam(preclickItem.cells[0].innerText,false,false);
                      preclickItem.style.backgroundColor = "#FFC0CB";  
                  }
              }else {
                  if(preclickItem.cells[3].childNodes[1].innerText.toLowerCase()=="save"){
                      ShowRuleParam(preclickItem.cells[0].innerText,false,false);
                      preclickItem.style.backgroundColor = "#FFC0CB";  
                  }
              }
          }else if(preclickItem!=null && preclickItem!=oTr){
              if(preChanging==true){
                  if(preclickItem.cells[3].childNodes[1].innerText.toLowerCase()=="save"){
                      alert("Please Save First!");
                      ShowRuleParam(preclickItem.cells[0].innerText,false,false);
                      preclickItem.style.backgroundColor = "#FFC0CB";  
                      return;
                  }
              }else{
                  if(oTr.cells[3].childNodes[1].innerText.toLowerCase()=="save"){
                      ShowRuleParam(oTr.cells[0].innerText,false,true);
                      oTr.style.backgroundColor = "#FFC0CB";  
                  }else{
                      ShowRuleParam(oTr.cells[0].innerText,true,false);
                      oTr.style.backgroundColor = "#98FB98";   
                  }
              }
              preclickItem=oTr;
          } 
          oTr.tag = true;   
        }
  	}
    /*鼠标另外一行时关闭已选行变色*/
  	function out() {
  		var oObj = event.srcElement;
  		if(oObj.tagName.toLowerCase() == "td"){
  			var oTr = oObj.parentNode;
  			if(!oTr.tag) oTr.style.backgroundColor = "";
  		}
  	}
    /*鼠标移动到选择行上时的行变色*/
  	function over(){   
  		var oObj = event.srcElement;
  		if(oObj.tagName.toLowerCase() == "td"){   
  		var oTr = oObj.parentNode;
  		if(!oTr.tag) oTr.style.backgroundColor = "#E1E9FD";
  		}
  	}
  	/*-------------------------------------------------------------------------------*/   




    /*-----------------------------------自动控件生成区域--------------------------------------------*/   
  	jQuery.ajax({
 		url:"/hikvision/task/imageAnalysisAuto/CreateElementsAuto",
 		type:"GET",
 		success:function(obj){ 
 			var str=obj.returnData;
 			var jsonlist=JSON.parse(obj.returnData);
 			ApplySceneInfo=jsonlist;
 			ShowDIVAuto(jsonlist);
 		},
 		error:function(){
 			alert("startAlarmGuard failed!");
 		},
 		complete:function(obj){

 		}
 	});
  	
  	/*初始化展示*/
    function ShowDIVAuto(ApplySceneInfo){
    	//先清空两个下拉框
    	let scene=document.getElementById("scene");
    	scene.options.length=0;
    	let eventType=document.getElementById("eventType");
        eventType.options.length=0;

        for(var i=0;i<ApplySceneInfo.ApplyScene.length;i++){
        	$('#scene').append('<option value="'+ApplySceneInfo.ApplyScene[i].description+'">' + ApplySceneInfo.ApplyScene[i].description + '</option>');
        }
        $('#scene').selectpicker('refresh');
        
    	for(var j=0;j<ApplySceneInfo.ApplyScene[0].EventCfg.length;j++){
    		$('#eventType').append('<option value="'+ApplySceneInfo.ApplyScene[0].EventCfg[j].eventDescription+'">' +ApplySceneInfo.ApplyScene[0].EventCfg[j].eventDescription+ '</option>');
        }
        $('#eventType').selectpicker('refresh');
        
        var InfoStr='{"ParamsInfo":'+JSON.stringify(ApplySceneInfo.ApplyScene[0].EventCfg[0].ParamsInfo)+'}';
        currentParamsInfo=JSON.parse(InfoStr);
        
        ShowParamInfo(currentParamsInfo);
    }

    function changeScene(obj){
        let eventType=document.getElementById("eventType");
        eventType.options.length=0;
        for(var i=0;i<ApplySceneInfo.ApplyScene.length;i++){
            if(ApplySceneInfo.ApplyScene[i].description==obj.selectedOptions[0].innerText){
                for(var j=0;j<ApplySceneInfo.ApplyScene[i].EventCfg.length;j++){
                	$('#eventType').append('<option value="'+ApplySceneInfo.ApplyScene[i].EventCfg[j].eventDescription+'">' +ApplySceneInfo.ApplyScene[i].EventCfg[j].eventDescription+ '</option>');
                }
                break;
            }
        }
        $('#eventType').selectpicker('refresh');
    }

    function changeEvent(obj){
        let secne_selected=$('#scene option:selected').text();  
        for(var i=0;i<ApplySceneInfo.ApplyScene.length;i++){
            if(ApplySceneInfo.ApplyScene[i].description==secne_selected){
                for(var j=0;j<ApplySceneInfo.ApplyScene[i].EventCfg.length;j++){
                    if(ApplySceneInfo.ApplyScene[i].EventCfg[j].eventDescription==obj.selectedOptions[0].innerText){
                    	var InfoStr='{"ParamsInfo":'+JSON.stringify(ApplySceneInfo.ApplyScene[i].EventCfg[j].ParamsInfo)+'}';
                    	currentParamsInfo=JSON.parse(InfoStr);
                        ShowParamInfo(currentParamsInfo);
                    }
                }
                break;
            }
        }
    }
    
    function ShowParamInfo(Info){
    	var divPanel=document.getElementById("ruleConfigPanel");
    	divPanel.innerHTML='';
        for(var i=0;i<Info.ParamsInfo.length;i++){
            switch (Info.ParamsInfo[i].type) {
                case 'int':
                	CreateDiv(Info.ParamsInfo[i].paramName,"input",Info.ParamsInfo[i].defaultValue);
                    break;
                default:
                    break;
            }
        }
    }
    
    function CreateDiv(text,type,value){
    	var divPanel=document.getElementById("ruleConfigPanel");
        var divMain=document.createElement("div");
        divMain.className="divStyle";

        var divSub_1=document.createElement("div");
        divSub_1.className="col-sm-6"
        var label=document.createElement("label");
        label.innerText=text;
        divSub_1.appendChild(label);

        var divSub_2=document.createElement("div");
        divSub_2.className="col-sm-4";

        switch (type) {
            case "input":
                var input=document.createElement("input");
                input.id=text;
                input.type="text";
                input.value=value;
                divSub_2.appendChild(input);
                break;
            default:
                break;
        }

        divMain.appendChild(divSub_1);
        divMain.appendChild(divSub_2);

        divPanel.appendChild(divMain);
    }

  	/*------------------------------------数据提交函数------------------------*/
    function SubmitConfig(){
        var pictures=[];
        pic_cityManagement_map.forEach(function(value,key){
            var picture={};
            picture["URL"]="";
            picture["id"]=key;
            var rule={};
            var cityManagementRule={};
            cityManagementRule["sensitivity"]=pic_cityManagement_map.get(key).sensitivity;
            cityManagementRule["rectID"]=pic_cityManagement_map.get(key).rectID;
            cityManagementRule["confidence"]=pic_cityManagement_map.get(key).confidence;
            cityManagementRule["detectNumber"]=pic_cityManagement_map.get(key).detectNumber;
            cityManagementRule["durationTimeThreshold"]=pic_cityManagement_map.get(key).durationTimeThreshold;
            cityManagementRule["multiViolation"]=pic_cityManagement_map.get(key).multiViolation;

            var polygons=[];
            var tempPolygons=pic_cityManagement_map.get(key).polygons;
            for(var i=0;i<tempPolygons.length;i++){
                var polygon={};
                polygon["polygonId"]=i;
                var tempPoints=tempPolygons[i];
                var points=[];
                for(var j=0;j<tempPoints.length;j++){
                    let point={};
                    point["x"]=tempPoints[j].x;
                    point["y"]=tempPoints[j].y;
                    points.push(point);
                }
                polygon["polygon"]=points;
                polygons.push(polygon);
            }
            cityManagementRule["polygons"]=polygons;

            var rulesParam=[];
            var tempRuleConfigMap=pic_rulelist_map.get(key);
            tempRuleConfigMap.forEach(function(value,key) {
                var ruleParam={};
                ruleParam["algorithmID"]=tempRuleConfigMap.get(key).algorithmID;
                ruleParam["scene"]=tempRuleConfigMap.get(key).scene;
                ruleParam["ruleID"]=tempRuleConfigMap.get(key).ruleID;
                ruleParam["ruleCustomName"]=tempRuleConfigMap.get(key).ruleCustomName;
                ruleParam["eventType"]=tempRuleConfigMap.get(key).eventType;

                var paramInfoList=JSON.parse(tempRuleConfigMap.get(key).ParamsInfo);
                var ParamsInfo={};
				for(var k=0;k<paramInfoList.ParamsInfo.length;k++){
                    ParamsInfo[paramInfoList.ParamsInfo[k].paramName]=paramInfoList.ParamsInfo[k].defaultValue;
                }
                ruleParam["ParamsInfo"]=JSON.stringify(ParamsInfo);
                
                rulesParam.push(ruleParam);
            });
            cityManagementRule["rulesParam"]=rulesParam;

            rule["cityManagementRule"]=cityManagementRule;

            picture["rule"]=rule;

            pictures.push(picture);
        });
        var TaskInfo={};
        TaskInfo["picture"]=pictures;
        
        var strJson={"TaskInfo":JSON.stringify(TaskInfo)};
        jQuery.ajax({
            url:"/hikvision/task/imageAnalysisAuto/PostTaskInfo",
            data:strJson,
            type:"POST",
            success:function(obj){
                if(obj.returnData=="success"){
                	alert("POST TaskInfo Success !");
                }else{
                	alert("POST TaskInfo Failed !");
                }
            },
	        error:function(){
	            alert("Communication exception, please check!");
	        },
	        complete:function(obj){
	        }
        })
        
    }





  	/*------------------------------------功能函数------------------------*/
  	var sleep = function(time) {
  	    var startTime = new Date().getTime() + parseInt(time, 10);
  	    while(new Date().getTime() < startTime) {}
  	};

  	