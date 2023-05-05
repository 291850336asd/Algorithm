/**
 * 
 */

var analysisDays=7;
var occurrences=10;
var similarity=85;
var captureTimeInterval=10;
var monitorIds=null;
var FaceLibrary=null;
var similarity_f=85;
var url=null;

$('#analysisDays').slider({
    formatter: function (value) {
        return 'analysisDays value: ' + value;
    }
}).on('slide', function (slideEvt) {
    //当滚动时触发
    //console.info(slideEvt);
    //获取当前滚动的值，可能有重复
    //console.info(slideEvt.value);
}).on('change', function (e) {
    //当值发生改变的时候触发
    //console.info(e);
    //获取旧值和新值
    console.info(e.value.oldValue + '--' + e.value.newValue);
    analysisDays=e.value.newValue;
});

$('#occurrences').slider({
    formatter: function (value) {
        return 'occurrences value: ' + value;
    }
}).on('slide', function (slideEvt) {
    //当滚动时触发
    //console.info(slideEvt);
    //获取当前滚动的值，可能有重复
    //console.info(slideEvt.value);
}).on('change', function (e) {
    //当值发生改变的时候触发
    //console.info(e);
    //获取旧值和新值
    console.info(e.value.oldValue + '--' + e.value.newValue);
    occurrences=e.value.newValue;
});

$('#similarity').slider({
    formatter: function (value) {
        return 'similarity value: ' + value;
    }
}).on('slide', function (slideEvt) {
    //当滚动时触发
    //console.info(slideEvt);
    //获取当前滚动的值，可能有重复
    //console.info(slideEvt.value);
}).on('change', function (e) {
    //当值发生改变的时候触发
    //console.info(e);
    //获取旧值和新值
    console.info(e.value.oldValue + '--' + e.value.newValue);
    similarity=e.value.newValue;
});

$('#captureTimeInterval').slider({
    formatter: function (value) {
        return 'captureTimeInterval value: ' + value;
    }
}).on('slide', function (slideEvt) {
    //当滚动时触发
    //console.info(slideEvt);
    //获取当前滚动的值，可能有重复
    //console.info(slideEvt.value);
}).on('change', function (e) {
    //当值发生改变的时候触发
    //console.info(e);
    //获取旧值和新值
    console.info(e.value.oldValue + '--' + e.value.newValue);
    captureTimeInterval=e.value.newValue;
});

$('#similarity_f').slider({
    formatter: function (value) {
        return 'captureTimeInterval value: ' + value;
    }
}).on('slide', function (slideEvt) {
    //当滚动时触发
    //console.info(slideEvt);
    //获取当前滚动的值，可能有重复
    //console.info(slideEvt.value);
}).on('change', function (e) {
    //当值发生改变的时候触发
    //console.info(e);
    //获取旧值和新值
    console.info(e.value.oldValue + '--' + e.value.newValue);
    similarity_f=e.value.newValue;
});

$('#similarity_mf').slider({
    formatter: function (value) {
        return 'captureTimeInterval value: ' + value;
    }
}).on('slide', function (slideEvt) {
    //当滚动时触发
    //console.info(slideEvt);
    //获取当前滚动的值，可能有重复
    //console.info(slideEvt.value);
}).on('change', function (e) {
    //当值发生改变的时候触发
    //console.info(e);
    //获取旧值和新值
    console.info(e.value.oldValue + '--' + e.value.newValue);
    similarity_f=e.value.newValue;
});

$("#facelibtable").bootstrapTable({
    toolbar: '#toobar',//工具列
    striped: true,//隔行换色
    cache: false,//禁用缓存
    pagination: true,//启动分页
    sidePagination: 'client',//分页方式
    pageNumber: 1,//初始化table时显示的页码
    pageSize: 10,//每页条目
    showFooter: false,//是否显示列脚
    showPaginationSwitch: true,//是否显示 数据条数选择框
    sortable: false,//排序
    search: false,//启用搜索
    showColumns: false,//是否显示 内容列下拉框
    showRefresh: true,//显示刷新按钮
    idField: 'FDID',//key值栏位
    clickToSelect: true,//点击选中checkbox
    singleSelect: true,//启用单行选中
    columns: [{
    checkbox: true
    },
   {
     field: 'FDID',
     title: 'FDID',
    },
    {
     field: 'similarity',
     title: 'similarity'
   }],
    onClickCell: function (field, value, row, $element) {
    //alert(row.SystemDesc);
  }
 });

function ExecuteSubmit(){
	//读取端口信息
	var method=$("select#method").val();
	url=$("input#url").val();
	switch(method)
	{
	case "GET":
		getconfig();
		break;
	case "PUT":
		putconfig();
		break;
	}
}

function putconfig(){
	//读取端口信息
	var enabled=$("select#enabled").val();
	var ftb=document.getElementById("facelibtable");
	var a = JSON.parse("{\"FaceLibrary\":[]}");
	var row=ftb.rows.length
	for(var i=1;i<row;i++)
	{
		var FDID=ftb.rows[i].cells[1].innnerText;
		var sim=ftb.rows[i].cells[2].innnerText;
		alert(FDID);
		var facelibrary={"FDID":FDID,"similarity":sim};
		a.FaceLibrary.push(facelibrary);
	}

	var FaceLibrary=JSON.stringify(a);
	
	var strJSON ={"url":url,"enabled":enabled,"analysisDays":analysisDays,"occurrences":occurrences,"similarity":similarity,
			"captureTimeInterval":captureTimeInterval,"facelibrary":FaceLibrary};

	jQuery.ajax({
		url:"hfpdconfig/PutConfig",
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

function getconfig(){
	//读取信息
	
	var strJSON ={"url":url,"analysisDays":analysisDays,"occurrences":occurrences,"similarity":similarity,"captureTimeInterval":captureTimeInterval};
	jQuery.ajax({
		url:"hfpdconfig/GetConfig",
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

function addFaceLib()
{
	
	var FDID=$("input#FDID").val();
	var data={"FDID":FDID,"similarity":similarity_f}
	$("#facelibtable").bootstrapTable("append",data);
}

function modifyFaceLib()
{
	var ids = $.map($("#facelibtable").bootstrapTable("getSelections"),function(row){
        return row.FDID;
    });
	
}

function delFaceLibrary()
{
	
	var ids = $.map($("#facelibtable").bootstrapTable("getSelections"),function(row){
        return row.FDID;
    });
	var bDel = confirm("Do you want to delete the data ？");
	if(bDel)
	{
		$("#facelibtable").bootstrapTable('remove',{
	        field : 'FDID',
	        values : ids
	    });
	}
}












