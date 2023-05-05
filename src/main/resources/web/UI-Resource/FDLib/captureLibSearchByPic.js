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
* @Description 查询人脸数据
* @param null
* @return void
*/	
function searchbyPic(){
	//获取查询条件
	var searchResultPosition= 0;
	var maxResults=100;
	var strJSON  = "";
    var modelMaxNum = "100";
    var dataType = "URL";
	var minSimilarity = $("#minSimilarity").val();
	var maxSimilarity  = $("#maxSimilarity").val();
	var startTime  = $("#start_time").val();
	var endTime = $("#end_time").val();
//	var startTime  = getLocal2UtcStr($("#start_time").val());
//	var endTime = getLocal2UtcStr($("#end_time").val());
	var gender = $("#searchFacegender").val();
	var glasses = $("#glasses").val();
	var smile = $("#smile").val();
	var ageGroup = $("#ageGroup").val();
	
	strJSON = {"searchResultPosition":searchResultPosition,"maxResults":maxResults,"modelMaxNum":modelMaxNum,"minSimilarity":minSimilarity,"maxSimilarity":maxSimilarity, "dataType":dataType,"faceURL":faceURL,"startTime":startTime,"endTime":endTime,"gender":gender,"glasses":glasses,"ageGroup":ageGroup,"smile":smile};


  $.ajax({
        type: "POST",
        url: "/hikvision/captureLibSearchByPic/searchByPicure",
        dataType: "json",
        data:strJSON,
        success: function (obj) {
           if(obj.errorCode==1){
        	   if(obj.totalMatches == 0)
    		   {
        		   $("#tiles").empty();
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
	for(var i in obj.targets ){
	
		var str= "<li id=\""+(parseInt(i))+"\" style=\"height: 160px;width: 160px;float:left;margin-left:5px;\" >"+
					"<input type=\"checkbox\" style=\"position: absolute;bottom: 22px;z-index: 99;left:24px;display:none; background:yellow;\" >"+
					"<div class=\"bg imageBackground\" style=\"position: absolute; padding-left: 4px;padding-top:4px;width:135px;height:150px;\">"+
	 					"<div style='width:135px;height:150px;display: table;' align='center'>"+
	 						"<div style=\"display: table-cell; vertical-align: top;\" align='center'>"+
	 							"<img class='faceImg' data-original=\""+obj.targets [i].subpicUrl+"\"src = \""+obj.targets [i].subpicUrl+"\">"+
	 							"<div class =\"nameDiv\" title="+obj.targets [i].captureSite.replace(" ","&nbsp;")+"  align=\"center\">"+
	 							"similarity: " + obj.targets [i].similarity +  "<br>" + obj.targets [i].captureSite+"&nbsp;&nbsp;"+
			 					"</div>"+
	 						"</div>"+	
	 					"</div>"+	 					 					
						
					"</div>"
				"</li>" + "<br>";
		+ "<br>"
		$("ul[id=tiles]").append(str);
		+ "<br>"
	
	}
}

function getLocal2UtcStr(localDateString){
    var self = this;
    
    if ("" == localDateString){
        return "";
    }
    var date=new Date(localDateString.replace(/-/g,  "/"));
    
    var date2=new Date(localDateString);
    date.setHours(date.getHours()+self.timeZone);
    if(0!=self.timeZone%1){
        date.setMinutes(date.getMinutes()+(self.timeZone%1)*60)
    }
    date.setHours(date.getHours()-self.isSummerTime);

    var year = date.getFullYear();       //年
    var month = date.getMonth() + 1;     //月
    var day = date.getDate();            //日

    var hh = date.getHours();            //时
    var mm = date.getMinutes();          //分
    var ss = date.getSeconds();
    var clock = year + "-";

    if(month < 10){
        clock += "0";
    }
    clock += month + "-";

    if(day < 10){
        clock += "0";
    }
    clock += day + "T";
    if(hh < 10){
        clock += "0";
    }
    clock += hh + ":";
    if(mm < 10){
        clock += "0";
    }
    clock += mm+ ":";
    if(ss < 10){
        clock += "0";
    }
    clock += ss+"Z";
    return(clock);
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