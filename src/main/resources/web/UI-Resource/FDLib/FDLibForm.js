
var currentFDID = "";
var currentFPID = "";
var isAdd = false;
var faceURL = "";

var imgOptions = {
	     autoResize: true, // This will auto-update the layout when the browser window is resized.
	     container: $('#faceTableDiv'), // Optional, used for some extra CSS styling
	     offset: 0, // Optional, the distance between grid items
	     itemWidth: 170 // Optional, the width of a grid item
	 };

$(function(){
    //初始化人脸库记录表格
    $('#tb_firmware_code').bootstrapTable({
        method: 'get',
        url:"/hikvision/FDLib/getFDLib",
        toolbar: '#toolbar',
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
            [   
                {field:"name",title:"FDLib name",align:"left",order:"asc",formatter:function(value,row,index){
                    var strHtml ='<p><span class="glyphicon glyphicon-user"></span> ' + value + '</p>';
                    return strHtml;
                }
                },
            ]
        ],
        onClickRow: function (row, $element) {
        	currentFDID = row.FDID;
        	$("#modifyFDLibName").val(row.name);
        	$("#modifyFDLibCustomInfo").val(row.customInfo);
        	search(0);
        	
        }
    });
})


	/**
	* @Description 添加人脸库
	* @param null
	* @return void
	*/	
function addFDLib(){
	var name=$("#name").val();
	var customInfo=$("#customInfo").val();

	var strJSON ={"name":name,"customInfo":customInfo};
	jQuery.ajax({
		url:"FDLib/addFDLib",
		type:"POST",
		data:strJSON,
		success:function(obj){
			if(obj.errorCode==1){
				alert("add FDLib success!");	
				 $('#tb_firmware_code').bootstrapTable('refresh');
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
	/**
	* @Description 修改人脸库
	* @param null
	* @return void
	*/	
function modifyFDLib() {
	var name=$("#modifyFDLibName").val();
	var customInfo=$("#modifyFDLibCustomInfo").val();
	var strJSON ={"FDID":currentFDID,"name":name,"customInfo":customInfo};
	
	jQuery.ajax({
		url:"/hikvision/FDLib/modifyFDLib",
		type:"POST",
		data:strJSON,
		success:function(obj){
			if(obj.errorCode==1){
				alert("modify FDLib success!");	
				 $('#tb_firmware_code').bootstrapTable();
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
	 $('#tb_firmware_code').bootstrapTable('refresh');
}


/**
* @Description 删除人脸库
* @param null
* @return void
*/	
function delFDLib() {
	
	var strJSON ={"FDID":currentFDID};
//	var arrselections = $("#tb_firmware_code").bootstrapTable('getSelections');
//	if (arrselections.length <= 0) {
//	    alert('请选择有效数据');
//	    return;
//	}
	var bDel = confirm("Do you want to delete the data？");
	if(bDel == true)
	{
		jQuery.ajax({
			url:"/hikvision/FDLib/delFDLib",
			type:"POST",
			data:strJSON,
			success:function(obj){
				if(obj.errorCode==1){
					alert("delete FDLib success!");	
					 $('#tb_firmware_code').bootstrapTable('refresh');
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
	else
	{
		return;
	}
}


/**
* @Description 查询人脸数据
* @param null
* @return void
*/	
function search(searchType ){
//获取查询条件
var searchResultPosition= 0;
var maxResults=100;
var faceLibType = "blackFD";
var FDID = currentFDID; 
var strJSON  = "";
if(searchType == 0)
{
	strJSON = {"searchType":searchType,"searchResultPosition":searchResultPosition,"maxResults":maxResults,"faceLibType":faceLibType,"FDID":FDID,};
}
else if(searchType ==1)
{
	strJSON = {"searchType":searchType,"FPID":currentFPID,"searchResultPosition":searchResultPosition,"maxResults":maxResults,"faceLibType":faceLibType,"FDID":FDID,};
}
else {searchType == 2}
{
	var name = $("#searchFacename").val();
	var startTime  = $("#start_time").val();
	var endTime = $("#end_time").val();
	var gender = $("#searchFacegender").val();
	var certificateType = $("#searchFacecertificateType").val();
	var certificateNumber = $("#searchFacecertificateNumber").val();
	
	strJSON = {"searchType":searchType,"FPID":currentFPID,"searchResultPosition":searchResultPosition,"maxResults":maxResults,"faceLibType":faceLibType,"FDID":FDID,"name":name,"startTime":startTime,"endTime":endTime,"gender":gender,"certificateType":certificateType,"certificateNumber":certificateNumber};
}


  $.ajax({
        type: "POST",
        url: "/hikvision/FDLib/searchFDLib",
        dataType: "json",
        data:strJSON,
        success: function (obj) {
           if(obj.errorCode==1){	
        	   if(searchType == 0 || searchType == 2)
    		   {
        		   //显示图片数据
        		   setImages(obj);
    		   }
        	   else if(searchType == 1)
    		   {
    		       //赋值
        		   setCurrentFaceInfo(obj);
    		   }

		}else{		
			var errorMsg = "failed,errorMsg:" + obj.errorMsg;
			alert(errorMsg);
		}
	},
	error:function(){
		alert("Communication exception, please check!");
	},complete:function(obj){
		
	}       
    });
}
	/**
* @Description 显示人脸图片
* @param null
* @return void
*/	
function setImages(obj){
	$(".selectedBgColor").addClass("imageBackground");
	var handler = $('#tiles li');
	imgOptions.itemWidth = 140;
	$("#tiles").empty();
	for(var i in obj.MatchList ){
	
		var str= "<li id=\""+(parseInt(i))+"\" style=\"height: 160px;width: 160px;float:left;margin-left:5px;\" onclick=\"getCurrentFPID(this)\">"+
					"<input type=\"checkbox\" style=\"position: absolute;bottom: 22px;z-index: 99;left:24px;display:none; background:yellow;\" value=\""+obj.MatchList [i].FPID+"\"/>"+
					"<div class=\"bg imageBackground\" style=\"position: absolute; padding-left: 4px;padding-top:4px;width:135px;height:150px;\">"+
	 					"<div style='width:135px;height:150px;display: table;' align='center'>"+
	 						"<div style=\"display: table-cell; vertical-align: top;\" align='center'>"+
	 							"<img class='faceImg' data-original=\""+obj.MatchList [i].faceURL+"\"src = \""+obj.MatchList [i].faceURL+"\">"+
	 							"<div class =\"nameDiv\" title="+obj.MatchList [i].name.replace(" ","&nbsp;")+"  align=\"center\">"+
			 						obj.MatchList [i].name+"&nbsp;&nbsp;"+
			 					"</div>"+
	 						"</div>"+	
	 					"</div>"+	 					 					
						
					"</div>"+
				"</li>";
		$("ul[id=tiles]").append(str);
	
	}
}
/**
* @Description 添加人脸数据
* @param null
* @return void
*/	
function addFaceRecord() {

	
	var name=$("#addFacename").val();
	var gender=$("#addFacegender").val();
	var certificateType =$("#addFacecertificateType").val();
	var certificateNumber=$("#addFacecertificateNumber").val();
	var bornTime=$("#addFacebornTime").val();
    var tag = $("#addFacetag").val();
    var customInfo = $("#addFacecaseInfo").val();
	var strJSON ={"faceURL":faceURL,"faceLibType":"blackFD","FDID":currentFDID,"name":name,"gender":gender,"bornTime":bornTime,"certificateType":certificateType,"certificateNumber":certificateNumber,"customInfo":customInfo,"tag":tag};
	jQuery.ajax({
		url:"/hikvision/FDLib/addFaceRecord",
		type:"POST",
		data:strJSON,
		success:function(obj){
			if(obj.errorCode==1){
				alert("add Face Record success!");		
				//重新获取人脸记录，刷新界面
				search(0);
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

/**
* @Description 修改人脸数据
* @param null
* @return void
*/	
function modifyFaceRecord() {
	
	var name=$("#modifyFacename").val();
	var gender=$("#modifyFacegender").val();
	var certificateType =$("#modifyFacecertificateType").val();
	var certificateNumber=$("#modifyFacecertificateNumber").val();
	var bornTime=$("#modifyFacebornTime").val();
    var tag = $("#modifyFacetag").val();
    var caseInfo = $("#modifyFacecaseInfo").val();
	var strJSON ={"FPID":currentFPID,"faceURL":faceURL,"faceLibType":"blackFD","FDID":currentFDID,"name":name,"gender":gender,"bornTime":bornTime,"certificateType":certificateType,"certificateNumber":certificateNumber,"caseInfo":caseInfo,"tag":tag};
	 $.ajax({
		url:"/hikvision/FDLib/modifyFaceRecord",
		type:"POST",
		data:strJSON,
		success:function(obj){
			if(obj.errorCode==1){
				alert("modify Face Record success!");	
				//重新获取人脸记录，刷新界面
				search(0);
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
/**
* @Description 上传图片
* @param null
* @return void
*/	
function uploadPic(){
	 //从控件获取文件名称
    var picFile = document.getElementById("faceURL").files[0];
    var arrayBuffer = "" 
     //读取图片数据
	if (picFile) {
	    var reader = new FileReader();    	   
	    reader.onload = function (e) {
		    arrayBuffer = reader.result;
		    //组装json数据
			 var strJSON ={"picFile":arrayBuffer};
			 //发送数据到服务器
				jQuery.ajax({
					url:"/hikvision/compare1V1/getPicUrl",
					type:"POST",
					data:strJSON,
					success:function(obj){
						if(obj.errorCode==1){
						   //获取返回的URL
							faceURL= obj.URL ;
							//将URL设置给控件
							$("#targetImage").css("background-image","url(" + obj.URL + ")"); 
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
	    reader.readAsBinaryString(picFile);
	}
}

/**
* @Description 删除人脸人脸记录
* @param null
* @return void
*/	
function delFaceRecord() {
	
	var strJSON ={"faceLibType":"blackFD","FDID":currentFDID,"FPID":currentFPID};

	var bDel = confirm("Do you want to delete the data ？");
	if(bDel == true)
	{
		jQuery.ajax({
			url:"/hikvision/FDLib/delFaceRecord",
			type:"POST",
			data:strJSON,
			success:function(obj){
				if(obj.errorCode==1){
					alert("delete Face Record success!");	
					//重新获取人脸记录，刷新界面
					search(0);					 
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
	else
	{
		return;
	}
}
	/**
	* @Description 修改图片URL
	* @param null
	* @return void
	*/	
	function modifyPic(){
		 //从控件获取文件名称
	    var picFile = document.getElementById("modifyfaceURL").files[0];
	    var arrayBuffer = "" 
	     //读取图片数据
		if (picFile) {
		    var reader = new FileReader();    	   
		    reader.onload = function (e) {
			    arrayBuffer = reader.result;
			    //组装json数据
				 var strJSON ={"picFile":arrayBuffer};
				 //发送数据到服务器
					jQuery.ajax({
						url:"/hikvision/compare1V1/getPicUrl",
						type:"POST",
						data:strJSON,
						success:function(obj){
							if(obj.errorCode==1){
							   //获取返回的URL
								faceURL= obj.URL ;
								//将URL设置给控件
								$("#modifyImage").css("background-image","url(" + obj.URL + ")"); 
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
		    reader.readAsBinaryString(picFile);
		}
	}
	/**
	* @Description 获取当前选中的FPID
	* @param null
	* @return void
	*/	
	function getCurrentFPID(faceRecord){
		currentFPID = $(faceRecord).find("input[type=checkbox]").eq(0).val();
		//获取指定人脸记录信息
		search(1);
	}
	/**
	* @Description 设置当前人脸数据信息
	* @param null
	* @return void
	*/	
	function setCurrentFaceInfo(obj){
		
		$("#modifyImage").css("background-image","url(" + obj.MatchList [0].faceURL + ")"); 
		$("#modifyFacename").val(obj.MatchList [0].name);
		$("#modifyFacegender").val(obj.MatchList [0].gender);
		$("#modifyFacebornTime").val(obj.MatchList [0].bornTime); 
		$("#modifyFacecertificateType").val(obj.MatchList [0].certificateType);
		$("#modifyFacecertificateNumber").val(obj.MatchList [0].certificateNumber); 
		$("#modifyFacecaseInfo").val(obj.MatchList [0].caseInfo);
		$("#modifyFacetag").val(obj.MatchList [0].tag);
	
	}
	
	//开始时间相应事件
	$("#start_time").datetimepicker({
	    language : 'zh-CN',
	    weekStart : 1,
	    todayBtn : 1,
	    autoclose : 1,
	    todayHighlight : 1,
	    startView : 2,
	    minView: 2,
	    format: 'yyyy-mm-dd',
	    forceParse : 0,
	}).on('hide', function(event) {
	    var startTime=$('#start_time').datetimepicker('getDate');
	    $('#end_time').datetimepicker('setStartDate',startTime);
	    $('#end_time').val("");
	});
	//结束时间相应事件
	$("#end_time").datetimepicker({
	    language : 'zh-CN',
	    weekStart : 1,
	    todayBtn : 1,
	    autoclose : 1,
	    todayHighlight : 1,
	    startView : 2,
	    minView: 2,
	    format: 'yyyy-mm-dd',
	    forceParse : 0,
	}).on('hide', function(event) {
	    var endTime=$('#end_time').datetimepicker('getDate');
	    $('#start_time').datetimepicker('setEndDate',endTime);
	});
	
	//修改出生时间相应事件
	$("#modifyFacebornTime").datetimepicker({
	    language : 'zh-CN',
	    weekStart : 1,
	    todayBtn : 1,
	    autoclose : 1,
	    todayHighlight : 1,
	    startView : 2,
	    minView: 2,
	    format: 'yyyy-mm-dd',
	    forceParse : 0,
	});
	
	//添加出生时间相应事件
	$("#addFacebornTime").datetimepicker({
	    language : 'zh-CN',
	    weekStart : 1,
	    todayBtn : 1,
	    autoclose : 1,
	    todayHighlight : 1,
	    startView : 2,
	    minView: 2,
	    format: 'yyyy-mm-dd',
	    forceParse : 0,
	});