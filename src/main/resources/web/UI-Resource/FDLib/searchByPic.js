var faceURL = "";

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
					url:"compare1V1/getPicUrl",
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
* @Description 查询人脸数据
* @param null
* @return void
*/	
function searchbyPic(){
	//获取查询条件
	var searchResultPosition= 0;
	var maxResults=100;
	var faceLibType = "blackFD";
	var strJSON  = "";
    var modelMaxNum = "100";
    var dataType = "URL";
	var minSimilarity = $("#minSimilarity").val();
	var maxSimilarity  = $("#maxSimilarity").val();
	var name = $("#searchFacename").val();
	var startTime  = $("#start_time").val();
	var endTime = $("#end_time").val();
	var gender = $("#searchFacegender").val();
	var certificateType = $("#searchFacecertificateType").val();
	var certificateNumber = $("#searchFacecertificateNumber").val();
	
	strJSON = {"searchResultPosition":searchResultPosition,"maxResults":maxResults,"modelMaxNum":modelMaxNum,"minSimilarity":minSimilarity,"maxSimilarity":maxSimilarity, "dataType":dataType,"faceURL":faceURL,"name":name,"startTime":startTime,"endTime":endTime,"gender":gender,"certificateType":certificateType,"certificateNumber":certificateNumber};


  $.ajax({
        type: "POST",
        url: "searchByPic/searchByPicure",
        dataType: "json",
        data:strJSON,
        success: function (obj) {
           if(obj.errorCode==1){
        	   if(obj.totalMatches == 0)
    		   {
    		        alert("no match faceRecord!");
    		   }
        	   else{
        		   //显示图片数据
        		   setImages(obj);   		   
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
	//imgOptions.itemWidth = 140;
	$("#tiles").empty();
	for(var i in obj.MatchList ){
	
		var str= "<li id=\""+(parseInt(i))+"\" style=\"height: 160px;width: 160px;float:left;margin-left:5px;\" >"+
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