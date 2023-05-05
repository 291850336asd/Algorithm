/**
 * 
 */
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

    //websocket发送消息示例
    function send() {
    	
       var message = document.getElementById('port').value;
       websocket.send(message);          	   	
    }
    
    //开启监听
    function startListen(){
    	//读取端口信息
    	var port=$("input#port").val();
    	var strJSON ={"port":port};
    	
    	//校验端口范围
    	if(port>0&&port<=65535)
    	{
    		//向服务端发送开启监听请求
        	jQuery.ajax({
        		url:"/hikvision/alarm/listen/startListen",
        		type:"GET",
        		data:strJSON,
        		success:function(obj){ 
        			if(obj.returnData=="success")
        				{    				  
        				   alert("Start listen success!");				
        				}
        			else
        				{
        				   alert("Start listen failed!");	
        				}
        			
        		},
        		error:function(){
        			alert("Start listen failed!");
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
    
    //停止监听
    function stopListen(){	
    	//向服务端发送停止监听请求
    	jQuery.ajax({
    		url:"/hikvision/alarm/listen/stopListen",
    		type:"GET",
    		success:function(obj){ 
    			if(obj.returnData=="success")
    				{   				
    				   alert("Stop listen success!");				
    				}
    			else
    				{
    				   alert("Stop listen failed!");	
    				}
    			
    		},
    		error:function(){
    			alert("Stop listen failed!");
    		},
    		complete:function(obj){

    		}
    	});
    }
