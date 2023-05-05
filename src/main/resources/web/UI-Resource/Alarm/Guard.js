/**
 * 
 */
    var websocket = null;
    var subscribe=false;
    var guard=false;
    
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
    	var jsonAlarmFormatInfo=JSON.parse(event.data); 
    	$("#alarmContent").val(jsonAlarmFormatInfo.content); 
    	
    	//报警历史填充
        $('#tb_AlarmHistoryList').bootstrapTable('insertRow', {
    		index: 0,	
            row:  {
            	ip: jsonAlarmFormatInfo.ip, 
            	eventType: jsonAlarmFormatInfo.eventType,
            	time:jsonAlarmFormatInfo.time, 
            	content: jsonAlarmFormatInfo.contentSavePath,
            	pictureList:JSON.stringify(jsonAlarmFormatInfo.pictureList)
            }
         }); 
        
        //加载图片列表
        var picnum=jsonAlarmFormatInfo.pictureList.length;
        $("#picturelist").empty();
        for(var i = 0; i < picnum; i++) {
            $("#picturelist").append(
            		"<li class='autumn-grid'>"
                    +"<p class='clearfix'>"+jsonAlarmFormatInfo.pictureList[i].desc+"</p>"
                    +"<img src='"+jsonAlarmFormatInfo.pictureList[i].url+"' class='img-rounded img-responsive'/>"                            
                    +"</li>");
        } 
       

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

    //初始化报警历史记录列表
	 $('#tb_AlarmHistoryList').bootstrapTable({
	        height: 200,
	        singleSelect: true,
	        columns: [
	            [   
	                {
	                	field:"ip",title:"ip", width:"10%" ,
	                },
	                {
		                field:"eventType",title:"eventType", width:"10%" ,
		            },
	                {
	                	field:"time",title:"time", width:"20%" ,
	                },
	                {
		                field:"content",title:"content", width:"40%" ,
		            },
	                {
	                	field:"pictureList",title:"pictureList", width:"20%" ,
	                },
	               
	            ]
	        ],
	        onClickRow: function (row, $element) {
	        	//点击报警历史列表行事件
	        	//高亮显示选中行
	        	$('.success').removeClass('success');
	        	$($element).addClass('success');
	        	
	        	//将选中行信息展示到数据展示区
	        	$.get(row.content,null,function(tex){
                    $('#alarmContent').val(tex);
                },'text');
	        	
	        	//加载选中行图片列表
	        	$("#picturelist").empty();
	        	if(row.pictureList.length>0)
	        	{
		        	var jsonPictureList=JSON.parse(row.pictureList);	            
		            var picnum=jsonPictureList.length;	            
		            for(var i = 0; i < picnum; i++) {
		                $("#picturelist").append(
		                		"<li class='autumn-grid'>"
		                        +"<p class='clearfix'>"+jsonPictureList[i].desc+"</p>"
		                        +"<img src='"+jsonPictureList[i].url+"' class='img-rounded img-responsive'/>"                            
		                        +"</li>");
		            } 
	        		
	        	}

	        }
	    });
	 
	 
 	jQuery.ajax({
 		url:"/hikvision/alarm/Guard/SubcribeEvent",
 		type:"GET",
 		success:function(obj){ 
 			var str=obj.returnData;
 			var jsonlist=JSON.parse(obj.returnData);
 			
 			var num=jsonlist.event.length;
 			for(var i=0;i<num;i++)
 			{
 				var Type=jsonlist.event[i];
 				var jsonType=JSON.parse(Type);
 				var s=jsonType.eventType;
 				$('#event').append("<option>" + s + "</option>");
 			}
 			$('#event').selectpicker('refresh');
 		},
 		error:function(){
 			alert("startAlarmGuard failed!");
 		},
 		complete:function(obj){

 		}
 	});
	 
	 
    //websocket发送消息示例
    function send() {
    	
       var message = document.getElementById('port').value;
       websocket.send(message);          	   	
    }
    
    //开启监听
    function startguard(){
    	//读取端口信息
    	guard=true;
    	var port=$("input#port").val();
    	var event=$("select#event").val();
    	var strJSON ={"port":port,"event":event,"subscribe":subscribe};	
    	document.getElementById("start_btn").style.backgroundColor="red";

    	//校验端口范围
    	if(port>0&&port<=65535)
    	{
        	jQuery.ajax({
        		url:"/hikvision/alarm/Guard/startAlarmGuard",
        		type:"GET",
        		data:strJSON,
        		success:function(obj){ 
        			if(obj.returnData=="success")
        				{    				  
        				   alert("startAlarmGuard success!");				
        				}
        			else if(obj.returnData=="failed")
        				{
        				   alert("startAlarmGuard failed!");	
        				}
        			else if(obj.returnData=="subscribe not support")
        				{
        				   alert("subscribe not support")
        				} 
        			else if(obj.returnData=="device not support")
    				{
    				   alert("device not support")
    				} 
        		},
        		error:function(){
        			alert("startAlarmGuard failed!");
        		},
        		complete:function(obj){

        		}
        	});
    	}
    	else
    	{
    		alert("Invalid port number!");		
    	}

   	
    }
    
    //撤防
    function stopguard(){	
    	//向服务端发送撤防请求
    	if(guard)
    	{
    		guard=false;
    		document.getElementById("start_btn").style.backgroundColor="white";
        	jQuery.ajax({
        		url:"/hikvision/alarm/Guard/stopAlarmGuard",
        		type:"GET",
        		success:function(obj){ 
        			if(obj.returnData=="success")
        				{   				
        				   alert("stopAlarmGuard success!");				
        				}
        			else
        				{
        				   alert("stopAlarmGuard failed!");	
        				}
        			
        		},
        		error:function(){
        			alert("stopAlarmGuard failed!");
        		},
        		complete:function(obj){

        		}
        	});
    	}    	
    }
     
    function Subscribe()
    {
    	subscribe=true;  	
    	$("#event").attr('disabled', false);
    	$("#event").selectpicker('refresh');
    	document.getElementById("dissub_btn").style.backgroundColor="white";
    	document.getElementById("sub_btn").style.backgroundColor="pink";
    }
    
    
    function disSubscribe()
    {
    	subscribe=false;
    	document.getElementById("event").options.selectedIndex = 0;
    	$("#event").attr('disabled', true);
    	$("#event").selectpicker('refresh');
    	
    	document.getElementById("sub_btn").style.backgroundColor="white";
    	document.getElementById("dissub_btn").style.backgroundColor="pink";
    }
