/**
 * 
 */
var url=null;
function ExecuteSubmit()
{
	var method=$("select#method").val();
	url=$("input#url").val();
	switch(method)
	{
	case "GET":
		getschedule();
		break;
	case "PUT":
		putschdule();
		break;
	}
}

function getschedule()
{
	var strJSON={"url":url};
	jQuery.ajax({
		url:"hfpdschedule/GetSchedule",
		type:"POST",
		data:strJSON,
		success:function(obj){
			$("#returnData").val(obj.returnData);  
		},
		error:function(){
			alert("Execute failed ! Please check!");
		},
		complete:function(obj){

		}
	});
}

function putschdule()
{
	var inboundData=$("textarea#inboundData").val();
	var strJSON={"url":url,"inboundData":inboundData};
	jQuery.ajax({
		url:"hfpdschedule/PutSchedule",
		type:"POST",
		data:strJSON,
		success:function(obj){
			$("#returnData").val(obj.returnData);  
		},
		error:function(){
			alert("Execute failed ! Please check!");
		},
		complete:function(obj){

		}
	});
}