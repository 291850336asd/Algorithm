/*
 * 主程序：
 * 1.能力集获取[包括算法类型获取以及目的类型获取]
 * 2.任务提交
 * 
 * 车辆分析协议中：算法类型选择分为3种:在getAlgorithmID()中设置
 * 1:通过车辆二次分析能力集获取的算法类型(algorithmType: getAlgorithmType())
 * 2:通过公用智能算法接口获取的算法ID(algorithmID : parseAlgorithm('algorithmID'))
 * 3.通过公用智能算法接口获取的分类算法ID(classificationID: parseAlgorithm('classificationID'))
 * 
 * 图片信息来源，分为两种,通过setImageType()设置:
 * 1.二进制:通过 $('#pictureBinary').on('change',function()设置
 * 2.url:直接获取输入框picutrueUrl的值
 * 
 * */
var algorithmType; //车辆二次分析能力集返回的algorithmType
var Algorithm; //算法仓库返回的支持算法版本信息
var pictureBinary; //图片二进制数据
var pictureSrc;   //图片二进制对用的路径，供界面展示

//图片二进制上传
$('#pictureBinary').on('change',function(){
    var reader = new FileReader();
    reader.onload = function (e) {
        pictureBinary = reader.result;
    }
    reader.readAsBinaryString(document.getElementById("pictureBinary").files[0]);

    var filePath = $(this).val(),         //获取到input的value，里面是文件的路径
    fileFormat = filePath.substring(filePath.lastIndexOf(".")).toLowerCase(),
    src = window.URL.createObjectURL(this.files[0]); //转成可以在本地预览的格式;

    // 检查是否是指定图片类型图片
    if( !fileFormat.match(/.jpg|.jpeg|.png|.tif|.bmp|.gif/) ) {
        alert('上传错误,文件格式必须为：jpg/jpeg/png/tif、bmp/gif');
        return;
    }
    pictureSrc =src
    $('#picture').attr('src',src);
});

//获取车辆图片二次分析任务能力集
function getVehicleReAnalysisAbility(){
    jQuery.ajax({
        url:"/hikvision/task/vehicleReAnalysis/getVehicleReAnalysisAbility",
        type:"GET",
        success:function(obj){
            if(obj.errorCode ==1){
                //显示算法类型algorithmType
                algorithmType = obj.taskInfo.algorithmType;
                getAlgorithmType();
                //显示destinationType
                getdestinationType(obj.taskInfo.destination.destinationType)
                getAlgorithmResource();
            }
            else{
            	//获取算法失败表示不支持，则禁用添加任务按钮
            	$('#btn_addVehicleReAnalysisTask').attr('disabled',true);
                var errorMsg = "failed,errorMsg:" + obj.errorMsg;
                alert(errorMsg);
            }
        },
        error: function(obj){
            alert("Communication exception, please check!");
        },
        complete: function(obj){
            
        }
        
    });
}

//上传车辆图片二次分析任务
function uploadVehicleReAnalysisTask(){
    
    var taskName = document.getElementById("taskName").value;
    if(isParamEmpty(taskName)){
        return ;
    }
    
    var pictureUrl = document.getElementById("pictureUrl").value; 
    //判断图片数据是否为空
    if(!pictureUrl && !pictureBinary){
        alert("Please Select a image or Input a picutre URL!");
        return ;
    }
    
    var algorithmType = $("input[name='algorithmType']:checked").val();
    if(isParamEmpty(algorithmType)){
        return ;
    }
    var algorithmID = document.getElementById("algorithmID").value;
    if(isParamEmpty(algorithmID)){
        return ;
    }
    
    var detectMode = document.getElementById("detectMode").value;
    if(isParamEmpty(detectMode)){
        return ;
    }
    var taskPriority = document.getElementById("taskPriority").value;
    if(isParamEmpty(taskPriority)){
        return ;
    }
    
    var licenseID = document.getElementById("licenseID").value;
    var confidence = document.getElementById("licenseConfidence").value;
    
    var destinationType = document.getElementById("destinationType").value;
    if(isParamEmpty(destinationType)){
        return ;
    }
    var destinationUrl = document.getElementById("destinationUrl").value;
    if(isParamEmpty(destinationUrl)){
        return ;
    }
    
    var userName = document.getElementById("userName").value;
    var password = document.getElementById("password").value;

    //组装json数据
    var strJSON ={"taskName":taskName,"pictureUrl":pictureUrl,"pictureBinary":pictureBinary,"algorithmID":algorithmID,
            "algorithmType":algorithmType,"detectMode":detectMode,"taskPriority":taskPriority,"licenseID":licenseID,"confidence":confidence,
            "destinationType":destinationType,"destinationUrl":destinationUrl,"userName":userName,"password":password};
    jQuery.ajax({
        url:"/hikvision/task/vehicleReAnalysis/uploadVehicleReAnalysisTask",
        data: strJSON,
        type:"POST",
        success:function(obj){
            if(obj.errorCode ==1 ){
                alert("Upload Task Success!");
            }
            else{
                var errorMsg = "failed,errorMsg:" + obj.errorMsg;
                alert(errorMsg);
            }
        },
        error: function(obj){
            
            alert("Communication exception, please check!");
        },
        complete:function(obj){
            
        }
    })
}
//获取分类算法ID和算法ID
function getAlgorithmResource(){
    jQuery.ajax({
        url:"/hikvision/task/imageAnalysis/getClassificationID",
        type:"GET",
        success:function(obj){
            if(obj.Algorithms){  //errorCode 是json返回的
            	//将返回的算法保存成全局变量
                Algorithm = obj.Algorithms;
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

//"algorithmType"单选框的的相应事件
function getAlgorithmID(){
    var type=$("input[name='algorithmType']:checked").val();
    if(type==1){
        //车辆二次分析能力接口获取的算法类型
        getAlgorithmType()
    }
    else if(type == 2){
        //算法仓库返回的分类算法ID
        parseAlgorithm("classificationID");
    }
    else if(type==3){
        //算法仓库返回的算法ID
        parseAlgorithm("algorithmID");
    }
    else{
        alert("No Such algorithm Type!");
    }
}

//显示算法类型algorithmType
function getAlgorithmType(){
    var algorithmTypeArr =  algorithmType.split(",");
    var selAlgorithmType =document.getElementById("algorithmID");
    //清空之前的选项
    while (selAlgorithmType.options.length){
        selAlgorithmType.remove(0);
        }
    //添加选项
    for(var i=0; i<algorithmTypeArr.length;i++){
        var opt=new Option(algorithmTypeArr[i],algorithmTypeArr[i]);
        selAlgorithmType.add(opt);
    }
    
}

//解析算法资源Algorithms classificationID & algorithmID
//type ="classificationID","algorithmID"
function parseAlgorithm(type){
    var algorithmList = new Set();
    if(Algorithm.length==0){
        alert("Get Algorithm failed!")
        return ;
    }
    else{
        for(i=0;i<Algorithm.length;i++){
            //选择车辆 图片检测算法 （ targetType == "2"是车辆）
            if(Algorithm[i].analysisSourceType == "picture" && Algorithm[i].targetType == "2"){
                //Note
                if(type=="classificationID"){
                    algorithmList.add(Algorithm[i].classificationID)
                }
                else if(type =="algorithmID"){
                    algorithmList.add(Algorithm[i].algorithmID)
                }
            }
        }
    }
    
    var selAlgorithmID =document.getElementById("algorithmID");
    //清空之前的选项
    while (selAlgorithmID.options.length){
        selAlgorithmID.remove(0);
        }
    
    //添加选项
    algorithmList.forEach(function (element, sameElement, set) {
        var opt=new Option(element,element);
        selAlgorithmID.add(opt);
    })
}

//设置图片上传类型：imageTypeCheckBox相应事件
function setImageType(){
    var isUrl= $("#imageTypeCheckBox").is(":checked");
    //判断是用url还是二进制传输图片
    if(isUrl){
    	//如果是url则启用url输入框，禁用图片选择按钮，然后清空图片
        $("#pictureUrl").attr('disabled',false);
        $('#picture').attr('src',"/UI-Resource/Utils/img/whitePic.jpg");
        $('#pictureBinary').val("");
        $("#pictureBinaryBtn").attr('disabled',true);
        
    }
    else{
    	//如果选择的是二进制，则禁用url输入框，启用图片选择按钮
        $("#pictureUrl").val("");
        $("#pictureUrl").attr('disabled',true);
        $("#pictureBinaryBtn").attr('disabled',false);
    }
}

//解析设备支持的算法数据，提取目的类型
function getdestinationType(destinationType){
    
    var destinationTypeArr =  destinationType.split(",");
    
    var selDestinationType =document.getElementById("destinationType");
    //清空之前的选项
    while (selDestinationType.options.length){
        selDestinationType.remove(0);
        }
    //添加选项
    for(var i=0;i<destinationTypeArr.length;i++){
        var opt =new Option(destinationTypeArr[i],destinationTypeArr[i]);
        selDestinationType.add(opt);
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