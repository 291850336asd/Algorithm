/**
 * 
 */
var currentID=0;
var parameterFormatType="json";//transfer data to device by json data

//加载报警主机列表
$(function(){
	jQuery.ajax({
		url:"/hikvision/alarm/httphosts/isSupportJson",
		type:"GET",
		success:function(obj){
			if(obj.errorCode==1){
				currentID=0;
				//如果json查询成功，则将parameterFormatType设置为json
				parameterFormatType="json";
				
			}else{		
				//否则设置为xml
				parameterFormatType="XML";
				if(obj.Exception==true){
					var errorMsg = "failed,errorMsg:" + obj.errorMsg;
					//alert(errorMsg);
					}
				
			}
		},
		error:function(){
			alert("IsSupportJson() Communication exception, please check!");
		},
		complete:function(obj){
			//查询判断类型完成后，获取报警主机列表
			var strJSON ={"parameterFormatType":parameterFormatType};
			 $('#tb_HttpsHostsList').bootstrapTable({
			        method: 'GET',
			        url:"/hikvision/alarm/httphosts/getHosts",
			        queryParams:queryParams,
			        toolbar: '#host_toolbar',
			        showHeader:true,
			        editable:true,//开启编辑模式
			        clickToSelect: true,
			        cache: false,
			        height: 500,
			        pageList: [10,25,50,100],
			        pageSize:20,
			        pageNumber:1,
			        uniqueId: 'index', //将index列设为唯一索引
			        minimumCountColumns: 2,
			        smartDisplay:true,
			        search:false,
			        columns: [
			                  //显示主机列表表格
			            [   
			                {field:"ipAddress",title:"host",align:"left",order:"asc",formatter:function(value,row,index){
			                    var strHtml ='<p>' + value + '</p>';  
			                    return strHtml;
			                }
			                },
			                {
			                	field:"url",title:"Url"
			                },
			                {
				                field:"eventType",title:"Event"
				            },
			                {
				                field:"portNo",title:"Port"
				            },
			               
			            ]
			        ],
			        //表格点击事件：将对应行的内容放入修改对话框对应的值中，方便进行修改
			        onClickRow: function (row, $element) {
			        	//取消之前选中行，选中当前行
			        	$('.success').removeClass('success');
			        	$($element).addClass('success');
			        	
			        	//将对应行的内容放入修改对话框对应的值中，方便进行修改
			        	currentID = row.id;
			        	$("#modifyHostName").val(row.ipAddress);
			        	$("#modifyHostUrl").val(row.url);
			        	$("#modifyHostPort").val(row.portNo);
			        	var eventType=row.eventType;
			        	if(eventType.indexOf("alarmResult") != -1)
			        	{
			        	$("#modifyHostNamelistEvent").prop("checked", true);
			        	}
			        	if(eventType.indexOf("captureResult") != -1)
		        		{
		        		$("#modifyHostSnapEvent").prop("checked", true);
		        		}
			        }
			    });
		}
	});
	
	
})

function queryParams(params) {  //配置参数
    var temp = {
    		parameterFormatType:parameterFormatType
    };
    return temp;
}
	



/**
* @Description 添加报警主机
* @param null
* @return void
*/	
function AddHostSubmit(){
	var host = document.getElementById("addHostName").value;
	if(host==""){
		alert("Please input hostName！");
		return ;
	}
	var url = document.getElementById("addHostUrl").value;
	if(url==""){
		alert("Please input url！");
		return ;
	}
	var portNo = document.getElementById("addHostPort").value;
	if(portNo==""){
		alert("Please input port！");
		return ;
	}
	
	//获取eventType类型
	var eventType = "";
	if($("#addHostNamelistEvent").prop("checked"))
	{
		eventType += "alarmResult" ;
		if($("#addHostSnapEvent").prop("checked"))
		{
			eventType += ",captureResult" ;
		}
	}
	else if($("#addHostSnapEvent").prop("checked"))
	{
		eventType += "captureResult" ;
	}
	
//	//如果获取到的eventType为空，则提示选择一个事件类型
//	if(eventType=="")
//	{
//		alert("Please Select at Least One Event!");
//		
//		return ;
//	}
	
	//调用接口，创建任务
	if(host){
		var strJSON ={"host":host,"url":url,"portNo":portNo,"eventType":eventType,"parameterFormatType":parameterFormatType};
		//组装json数据
		//发送数据到服务器
		jQuery.ajax({
			url:"/hikvision/alarm/httphosts/addHost",
			type:"POST",
			data:strJSON,
			success:function(obj){
				//errorCode 是json返回的，statusCode是xml返回的数据
				if(obj.errorCode==1 || obj.statusCode==1){  
					//添加报警主机成功后，将添加对话框隐藏，并刷新报警主机列表
					$('#addHostModel').modal('toggle');
					$('#tb_HttpsHostsList').bootstrapTable('refresh'); 
					currentID = 0;
				}else{		
					var errorMsg = "failed,errorMsg:" + obj.errorMsg;
					alert(errorMsg);
				}
			},
			error:function(){
				alert("Communication exception, please check!");
			},
			complete:function(obj){
			}
		});
		}
	
	}

/**
* @Description 删除报警主机
* @param null
* @return void
*/	
function DelHost(){
	var id = currentID;
	if(id){
		var bDel = confirm("Do you want to delete the host？");
		if(bDel == true)
		{
			//组装json数据
			var strJSON ={"id":id,"parameterFormatType":parameterFormatType};
			//发送数据到服务器
			jQuery.ajax({
				url:"/hikvision/alarm/httphosts/delHost",
				type:"POST",
				data:strJSON,
				success:function(obj){
					if(obj.errorCode==1 || obj.statusCode==1){ //errorCode 是json返回的，statusCode是xml返回的数据
			             $('#tb_HttpsHostsList').bootstrapTable('refresh'); //, {url: "httphosts/getHosts"}
					    currentID = 0;
					}else{		
						var errorMsg = "failed,errorMsg:" + obj.errorMsg;
						alert(errorMsg);
					}
				},
				error:function(){
					alert("Communication exception, please check!");
				},
				complete:function(obj){
				}
			});
			}
		}
	else
	{
	alert("Please Select a Host to Delete!")
	}
}

/**
* @Description 修改报警主机
* @param null
* @return void
*/	
function ModifyHost(){
	var id = currentID;
	//获取修改主机的相关内容
	var host = document.getElementById("modifyHostName").value;
	if(host==""){
		alert("Please input hostName！");
		return ;
	}
	var url = document.getElementById("modifyHostUrl").value;
	if(url==""){
		alert("Please input url！");
		return ;
	}
	var portNo = document.getElementById("modifyHostPort").value;
	if(portNo==""){
		alert("Please input port！");
		return ;
	}
	var eventType = "";
	
	//获取eventType类型
	if($("#modifyHostNamelistEvent").prop("checked"))
	{
		eventType += "alarmResult" ;
		if($("#modifyHostSnapEvent").prop("checked"))
		{
			eventType += ",captureResult" ;
		}
	}
	else if($("#modifyHostSnapEvent").prop("checked"))
	{
		eventType += "captureResult" ;
	}
	//如果是参数类型是json，且获取到的eventType为空，则提示选择一个事件类型
	if(parameterFormatType =="json" && eventType =="")
	{
		alert("Please Select at Least One Event!");
		
		return ;
	}
	
	if(id!=0){
		var strJSON ={"id":id,"host":host,"url":url,"portNo":portNo,"eventType":eventType,"parameterFormatType":parameterFormatType};
		//组装json数据
		//发送数据到服务器
		jQuery.ajax({
			url:"/hikvision/alarm/httphosts/modifyHost",
			type:"POST",
			data:strJSON,
			success:function(obj){
				if(obj.errorCode==1 || obj.statusCode==1){ //errorCode 是json返回的，statusCode是xml返回的数据
					$('#modifyHostModel').modal('toggle');
					$('#tb_HttpsHostsList').bootstrapTable('refresh');
					
				}else{		
					var errorMsg = "failed,errorMsg:" + obj.errorMsg;
					alert(errorMsg);
				}
			},
			error:function(){
				alert("Communication exception, please check!");
			},
			complete:function(obj){
			}
		});
		}
	}

//修改提示对话框
function ModifyWarning(){
	var id = currentID;
	if(id==0)
		{
		alert("Please Select a Host to Modify!");
		}
	else
		{
		$('#modifyHostModel').modal();
		}
	}

