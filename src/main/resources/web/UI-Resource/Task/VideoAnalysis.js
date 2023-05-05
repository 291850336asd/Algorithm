
var currentTaskID =0;
var TaskIDArray =new Array();
var jsonSmartRule ={"videoAddVCA":{ "@opt":[true,false]}};
var jsonTrafficRule;   //未初始化表示jsonTrafficRule =  undefined   判断使用if("undefined" != typeof jsonTrafficRule)
//根据值查找下标
Array.prototype.indexOf = function (val) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == val) return i;
    }
    return -1;
};
//根据下标移除数组中的值
Array.prototype.remove = function (val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};

//查询视频任务能力&视频任务列表
$(function(){
    //查询视频任务能力
    jQuery.ajax({
        url:"/hikvision/task/videoAnalysis/getVideoTaskAbility",
        type:"GET",
        success:function(obj){
            if(obj.errorCode==1 ){ //errorCode 是json返回的
                if(!obj.taskInfo){
                    alert("The device don't support this function!");
                }
                else{
                    //如果行为分析参数存在，保存起来
                    if("smartRule" in obj.taskInfo){
                        jsonSmartRule = obj.taskInfo.smartRule;
                    }
                    if("trafficRule" in obj.taskInfo){
                        jsonTrafficRule = obj.taskInfo.trafficRule;
                    }
                }
                    
                
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
    //初始化人脸库记录表格
    $('#tb_videoTaskList').bootstrapTable({
        method: 'get',
        url:"/hikvision/task/videoAnalysis/getAllTask",
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
        clickToSelect: true,
        columns: [
            [
                 {
                     checkbox: true
                 },
                {field:"taskInfo.taskName",title:"taskName",align:"left",order:"asc",formatter:function(value,row,index){
                    var strHtml ='<p>' + value + '</p>';
                    return strHtml;
                }
                },
                {
                    field:"taskInfo.taskPriority",title:"taskPriority"
                },
                {
                    field:"taskInfo.algorithmType",title:"algorithmType"
                },
                {
                    field:"taskInfo.streamType",title:"streamType"
                },
                {
                    field:"taskInfo.time.taskType",title:"taskType"
                },
                {
                    field:"taskStatus",title:"taskStatus"
                },
                {
                    field:"process",title:"process"
                },
            ]
        ],
        onClickRow: function (row, $element) {
        },
        //选中时，将行对应的ID添加到全局变量TaskIDArray中
        onCheck:function(row){
            TaskIDArray.push(row.taskID);

        },
        //如果取消选中，将行对应的ID从全局变量TaskIDArray中删除
        onUncheck:function(row){
            TaskIDArray.remove(row.taskID);
       }

    });
})

//上传视频分析任务
function uploadVideoAnalysisTask(){
    //获取任务信息并判断
    var taskName =document.getElementById("taskName").value;
    if(isParamEmpty(taskName)){
        return;
    }
    var algorithmType =document.getElementById("algorithmType").value;
    if(isParamEmpty(algorithmType)){
        return;
    }
    var streamType =document.getElementById("streamType").value;
    if(isParamEmpty(streamType)){
        return;
    }
    var taskPriority =document.getElementById("taskPriority").value;
    if(isParamEmpty(taskPriority)){
        return;
    }
    var streamUrl =document.getElementById("streamUrl").value;
    if(isParamEmpty(streamUrl)){
        return;
    }
    
    var userName =document.getElementById("userName").value;
    var passWord =document.getElementById("passWord").value;
   
    var taskType =document.getElementById("taskType").value;
    if(isParamEmpty(taskType)){
        return;
    }
    var startTime =document.getElementById("startTime").value;
    if(isParamEmpty(startTime)){
        return;
    }
    var endTime =document.getElementById("endTime").value;
    if(isParamEmpty(endTime)){
        return;
    }
    var destinationType =document.getElementById("destinationType").value;
    if(isParamEmpty(destinationType)){
        return;
    }
    var destinationUrl =document.getElementById("destinationUrl").value;
    if(isParamEmpty(destinationUrl)){
        return;
    }
    var dstUserName =document.getElementById("dstUserName").value;
    if(isParamEmpty(dstUserName)){
        return;
    }
    var dstPassWord =document.getElementById("dstPassWord").value;
    if(isParamEmpty(dstPassWord)){
        return;
    }

    //组装json数据
    //发送数据到服务器
    var strJSON ={"taskName":taskName,"algorithmType":algorithmType,"streamType":streamType,"taskPriority":taskPriority,"streamUrl":streamUrl,
            "userName":userName,"passWord":passWord,"taskType":taskType,"startTime":startTime,"endTime":endTime,
            "destinationType":destinationType,"destinationUrl":destinationUrl,"dstUserName":dstUserName, "dstPassWord":dstPassWord};
    jQuery.ajax({
        url:"/hikvision/task/videoAnalysis/uploadTask",
        data:strJSON,
        success:function(obj){
            if(obj.errorCode==1 || obj.statusCode==1){  //errorCode 是json返回的，statusCode是xml返回的数据
                //隐藏添加任务界面，并刷新任务列表
                $('#addVideoTask').modal('toggle');
                $('#tb_videoTaskList').bootstrapTable('refresh');
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



//修改提示
function ModifyWarning(){
    var rows= $("#tb_videoTaskList").bootstrapTable('getSelections');
    if(rows.length<=0){
        alert("Please Select a Task to Operate!")
    }
    else if(rows.length>1){
        alert("Please Select ONLY ONE Task to Modify!")
    }
    else{
        var row= rows[0]; //JSON.stringify( rows );
        currentTaskID = row.taskID;
        $("#modifyTaskName").val(row.taskInfo.taskName);
        $("#modifyTaskPriority").val(row.taskInfo.taskPriority);
        $("#modifyAlgorithmType").val(row.taskInfo.algorithmType);

        $("#modifyStreamType").val(row.taskInfo.streamType);
        $("#modifyStreamUrl").val(row.taskInfo.stream.streamUrl);
        $("#modifyUserName").val(row.taskInfo.stream.userName);

        $("#modifyTaskType").val(row.taskInfo.time.taskType);
        
        //实时流信息可能没有设置，因此不显示可以
        if(row.taskInfo.time.tempInfo)
        {
            $("#modifyStartTime").val(row.taskInfo.time.tempInfo.startTime);
            $("#modifyEndTime").val(row.taskInfo.time.tempInfo.endTime);
        }

        $("#modifyDestinationType").val(row.taskInfo.destination[0].destinationType);
        $("#modifyDestinationUrl").val(row.taskInfo.destination[0].destinationUrl);
        $("#modifyDstUserName").val(row.taskInfo.destination[0].userName);

        $('#modifyVideoTask').modal();
    }

}

//修改任务
function modifyVideoAnalysisTask(){
    //从界面获取任务信息并判断
    var taskName =document.getElementById("modifyTaskName").value;
    if(isParamEmpty(taskName)){
        return;
    }
    var algorithmType =document.getElementById("modifyAlgorithmType").value;
    if(isParamEmpty(algorithmType)){
        return;
    }
    var streamType =document.getElementById("modifyStreamType").value;
    if(isParamEmpty(streamType)){
        return;
    }
    var taskPriority =document.getElementById("modifyTaskPriority").value;
    if(isParamEmpty(taskPriority)){
        return;
    }

    var taskType =document.getElementById("modifyTaskType").value;
    if(isParamEmpty(taskType)){
        return;
    }
    var startTime =document.getElementById("modifyStartTime").value;
    if(isParamEmpty(startTime)){
        return;
    }
    var endTime =document.getElementById("modifyEndTime").value;
    if(isParamEmpty(endTime)){
        return;
    }
    var streamUrl =document.getElementById("modifyStreamUrl").value;
    if(isParamEmpty(streamUrl)){
        return;
    }

    var userName =document.getElementById("modifyUserName").value;
    var passWord =document.getElementById("modifyPassWord").value;
   

    var destinationType =document.getElementById("modifyDestinationType").value;
    if(isParamEmpty(destinationType)){
        return;
    }
    var destinationUrl =document.getElementById("modifyDestinationUrl").value;
    if(isParamEmpty(destinationUrl)){
        return;
    }
    var dstUserName =document.getElementById("modifyDstUserName").value;
    if(isParamEmpty(dstUserName)){
        return;
    }
    var dstPassWord =document.getElementById("modifyDstPassWord").value;
    if(isParamEmpty(dstPassWord)){
        return;
    }
    //组装json数据
    //发送数据到服务器
    var strJSON ={"taskID":currentTaskID,"taskName":taskName,"algorithmType":algorithmType,"streamType":streamType,"taskPriority":taskPriority,
            "taskType":taskType,"startTime":startTime,"endTime":endTime,
            "streamUrl":streamUrl,"userName":userName,"passWord":passWord,
            "destinationType":destinationType,"destinationUrl":destinationUrl,"dstUserName":dstUserName, "dstPassWord":dstPassWord};
    jQuery.ajax({
        url:"/hikvision/task/videoAnalysis/modifyTask",
        data:strJSON,
        success:function(obj){
            if(obj.errorCode==1 || obj.statusCode==1){  //errorCode 是json返回的，statusCode是xml返回的数据
                $('#modifyVideoTask').modal('toggle');
            }else{
                var errorMsg = "failed,errorMsg:" + obj.errorMsg;
                alert(errorMsg);
            }
        },
        error:function(){
            alert("Communication exception, please check!");
        },
        complete:function(obj){
            $('#tb_videoTaskList').bootstrapTable('refresh');
        }
    });
}

//批量暂停
function Pause(){
    //获取选中的任务信息
    var rows= $("#tb_videoTaskList").bootstrapTable('getSelections');
    if(rows.length<=0){
        alert("Please Select a Task to Operate!")
    }
    else {
        //判断任务状态，是否为等待或者正在执行
        for(i=0;i<rows.length;i++ ){
            if(rows[i].taskStatus != "Waiting" && rows[i].taskStatus != "Executing"){
                alert("Please Select a \"Waiting\" or \"Executing\" Task to Pause!");
                //刷新任务列表，清空全局变量
                $('#tb_videoTaskList').bootstrapTable('refresh');
                TaskIDArray.splice(0,TaskIDArray.length);//清空数组 ;
                return ;
            }
        }
    }
    //组装json数据
    var strJSON ={"taskID":TaskIDArray.toString()};
    //发送数据到服务器
    jQuery.ajax({
        url:"/hikvision/task/videoAnalysis/pauseTask",
        type:"POST",
        data:strJSON,
        success:function(obj){
            if(obj.errorCode==1 || obj.statusCode==1){ //errorCode 是json返回的，statusCode是xml返回的数据
              alert("Success to pause tasks!");

            }else{
                var errorMsg = "failed,errorMsg:" + obj.errorMsg;
                alert(errorMsg);
            }
        },
        error:function(){
            alert("Communication exception, please check!");
        },
        complete:function(obj){
            //刷新任务列表，清空全局变量
            $('#tb_videoTaskList').bootstrapTable('refresh');
            TaskIDArray.splice(0,TaskIDArray.length);//清空数组 ;
        }
    });
}

//批量恢复
function Restart(){
    //获取选中的任务信息
    var rows= $("#tb_videoTaskList").bootstrapTable('getSelections');
    if(rows.length<=0){
        alert("Please Select a Task to Operate!")
    }
    else {
        //判断任务状态，是否为暂停或者正在暂停
        for(i=0;i<rows.length;i++ ){
            if(rows[i].taskStatus != "Paused" && rows[i].taskStatus != "Pausing"
                && rows[i].taskStatus != "Stopped" && rows[i].taskStatus != "Stopping"){
                alert("Please Select a \"Paused\" or \"Stopped\" Task to Restart!")
                return ;
            }
        }
    }
    //组装json数据
    var strJSON ={"taskID":TaskIDArray.toString()};
    //发送数据到服务器
    jQuery.ajax({
        url:"/hikvision/task/videoAnalysis/restartTask",
        type:"POST",
        data:strJSON,
        success:function(obj){
            if(obj.errorCode==1 || obj.statusCode==1){ //errorCode 是json返回的，statusCode是xml返回的数据
              alert("Success to restart tasks!");
              $('#tb_videoTaskList').bootstrapTable('refresh');
              TaskIDArray.splice(0,TaskIDArray.length);//清空数组 ;

            }else{
                var errorMsg = "failed,errorMsg:" + obj.errorMsg;
                alert(errorMsg);
            }
        },
        error:function(){
            alert("Communication exception, please check!");
        },
        complete:function(obj){
            //刷新任务列表，清空全局变量
            $('#tb_videoTaskList').bootstrapTable('refresh'); //
            TaskIDArray.splice(0,TaskIDArray.length);//清空数组 ;
        }
    });
}

//Delelte the video
function DelVideoTask(){

    if(TaskIDArray.length){
        var bDel = confirm("Do you want to delete the videoTask？");
        if(bDel == true)
        {
            //组装json数据
            var strJSON ={"taskID":TaskIDArray.toString()};
            //发送数据到服务器
            jQuery.ajax({
                url:"/hikvision/task/videoAnalysis/delTask",
                type:"POST",
                data:strJSON,
                success:function(obj){
                    if(obj.errorCode==1 ){ //errorCode 是json返回的
                         $('#tb_videoTaskList').bootstrapTable('refresh'); 

                    }else{
                        var errorMsg = "failed,errorMsg:" + obj.errorMsg;
                        alert(errorMsg);
                    }
                },
                error:function(){
                    alert("Communication exception, please check!");
                },
                complete:function(obj){
                    TaskIDArray.splice(0,TaskIDArray.length);//清空数组 ;
                }
            });
            }
        }
    else
    {
    alert("Please Select a VideoTask to Delete!")
    }
}
//判断参数是否为空
function isParamEmpty(param ){
    if(param ==""){
        alert("Some param(s) is(are) empty, Please Fill it!");
        return true;
    }
    return false;
}



//任务时间类型:"plan,temp","计划任务，实时流有效，临时任务，实时流、历史流都有效"
function ChangeTaskType(){

    var selStreamType =document.getElementById("streamType");
    //情况之前选项
    while (selStreamType.options.length){
        selStreamType.remove(0);
    }
    //添加选项
    var opt=new Option("realtime","realtime");
    selStreamType.add(opt);

    var taskType=document.getElementById("taskType").value;
    if(taskType == "temp"){
        var opt=new Option("localvideo","localvideo");
        selStreamType.add(opt);
        var opt=new Option("historyvideo","historyvideo");
        selStreamType.add(opt);
    }
}
//任务时间类型:"plan,temp","计划任务，实时流有效，临时任务，实时流、历史流都有效"
function ChangeModifyTaskType(){
    //情况之前选项
    var selStreamType =document.getElementById("modifyStreamType");
    while (selStreamType.options.length){
        selStreamType.remove(0);
    }
    //添加选项
    var opt=new Option("realtime","realtime");
    selStreamType.add(opt);

    var taskType=document.getElementById("modifyTaskType").value;
    if(taskType == "temp"){
        var opt=new Option("localvideo","localvideo");
        selStreamType.add(opt);
        var opt=new Option("historyvideo","historyvideo");
        selStreamType.add(opt);
    }
}

//生成界面
function addBehaviorAnalysisTask(){
    if("undefined" == typeof jsonSmartRule){
        alert("设备不支持行为分析任务!");
        return ;
    }
    else{
        
        if(jsonSmartRule.videoAddVCA){
            var dialogSubDiv = document.getElementById("addBehaviorAnalysisTaskSub");
            var oVideoAddVCALabel = document.createElement("label" );
            oVideoAddVCALabel.innerHTML ="码流智能信息叠加";
            dialogSubDiv.appendChild(oVideoAddVCALabel);
            
            var oVideoAddVCA = document.createElement("input","videoAddVCA" );//创建input控件
            oVideoAddVCA.type = "radio";
            
            dialogSubDiv.appendChild(oVideoAddVCA);
        }
        
        $('#addBehaviorAnalysisTaskMain').modal('show')
    }
};


//添加任务的开始时间相应事件
$("#startTime").datetimepicker({
    language : 'zh-CN',
    weekStart : 1,
    todayBtn : 1,
    autoclose : 1,
    todayHighlight : 1,
    startView : 2,
    format: 'yyyy-mm-dd HH:ii:ss',
    forceParse : 0,
    showSecond: true,
    showMeridian: 1,
}).on('hide', function(event) {
    var startTime=$('#startTime').datetimepicker('getDate');
    $('#endTime').datetimepicker('setStartDate',startTime);
    $('#endTime').val("");
});
//添加任务的结束时间相应事件
$("#endTime").datetimepicker({
    language : 'zh-CN',
    weekStart : 1,
    todayBtn : 1,
    autoclose : 1,
    todayHighlight : 1,
    startView : 2,
    format: 'yyyy-mm-dd HH:ii:ss',
    forceParse : 0,
    showSecond: true,
    showMeridian: 1,
}).on('hide', function(event) {
    var endTime=$('#endTime').datetimepicker('getDate');
    $('#startTime').datetimepicker('setEndDate',endTime);
});

//修改任务的开始时间相应事件
$("#modifyStartTime").datetimepicker({
    language : 'zh-CN',
    weekStart : 1,
    todayBtn : 1,
    autoclose : 1,
    todayHighlight : 1,
    startView : 2,
    format: 'yyyy-mm-dd HH:ii:ss',
    forceParse : 0,
    showSecond: true,
    showMeridian: 1,
}).on('hide', function(event) {
    var startTime=$('#modifyStartTime').datetimepicker('getDate');
    $('#modifyEndTime').datetimepicker('setStartDate',startTime);
    $('#modifyEndTime').val("");
});
//修改任务的结束时间相应事件
$("#modifyEndTime").datetimepicker({
    language : 'zh-CN',
    weekStart : 1,
    todayBtn : 1,
    autoclose : 1,
    todayHighlight : 1,
    startView : 2,
    format: 'yyyy-mm-dd HH:ii:ss',
    forceParse : 0,
    showSecond: true,
    showMeridian: 1,
}).on('hide', function(event) {
    var endTime=$('#modifyEndTime').datetimepicker('getDate');
    $('#modifyStartTime').datetimepicker('setEndDate',endTime);
});
