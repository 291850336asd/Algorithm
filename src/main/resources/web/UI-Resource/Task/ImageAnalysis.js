var pictureUrl;
var pictureSrc;
var pictureFile; //存储picture的二进制或者base64
var Algorithm;

//设置图片上传类型
function setPictureType(){
	//判断是用url还是二进制传输图片
    var imageType= $("#pictureType").val();
    
    if(imageType=="url"){
    	//如果是url则启用url输入框，禁用图片选择按钮，然后清空图片
        $("#pictureUrl").attr('disabled',false);
        $('#picture').attr('src',"/UI-Resource/Utils/img/whitePic.jpg");
        
        $('#pictureFile').val("");
        $("#pictureFileBtn").attr('disabled',true);
        
    }
    else if(imageType=="binary" || imageType=="base64"){
    	//如果选择的是二进制/base64，则禁用url输入框，启用图片选择按钮
        $("#pictureUrl").val("");
        $("#pictureUrl").attr('disabled',true);
        
        $('#picture').attr('src',"/UI-Resource/Utils/img/whitePic.jpg");
        $('#pictureFile').val("");
        $("#pictureFileBtn").attr('disabled',false);
    }
}

//图片二进制/base64上传
$('#pictureFile').on('change',function(){
    var reader = new FileReader();
    reader.onload = function (e) {
        pictureFile = reader.result;//.substring(reader.result.indexOf(",") + 1);
    }
    var imageType= $("#pictureType").val();
    if(imageType=="binary"){
        reader.readAsBinaryString(this.files[0]);
    }
    else if(imageType=="base64"){
    	reader.readAsDataURL(this.files[0]);
    }
    

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

//获取 classificationID
function getClassificationID(){
    jQuery.ajax({
        url:"/hikvision/task/imageAnalysis/getClassificationID",
        success:function(obj){
            if(obj.Algorithms){  //errorCode 是json返回的，statusCode是xml返回的数据
                Algorithm=obj.Algorithms;
                //获取分类算法ID
                parseClassificationID();
                //获取算法类型
                getTargetType()
            }else{
            	//获取算法失败表示不支持，则禁用添加任务按钮
            	$('#btn_addImageAnalysisTask').attr('disabled',true);
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

//解析classificationID
function parseClassificationID(){
	//
    var classificationIDList = new Set();
    
    if(Algorithm.length==0){
        alert("Get Algorithm failed!")
        return ;
    }
    else{
    	//选择算法分析类型为picture的算法ID，并添加到分类算法ID集合中
        for(i=0;i<Algorithm.length;i++){
            if(Algorithm[i].analysisSourceType == "picture"){
                classificationIDList.add(Algorithm[i].classificationID)
            }
        }
    }
    if(classificationIDList.length==0){
    	//如果算法返回不支持picture，则不支持图片分析，则禁用上传图片分析任务按钮
    	alert("This Device don't support Image Analysis!");
    	$('#btn_addImageAnalysisTask').attr('disabled',true);
    	return ;
    }
    var selClassificationID =document.getElementById("classificationID");
    //清空分类算法ID中原有的选项
    while (selClassificationID.options.length){
        selClassificationID.remove(0);
        }
    //将分类算法ID中的元素添加到界面的算法ID选项框中
    classificationIDList.forEach(function (element, sameElement, set) {
        var opt=new Option(element,element);
        selClassificationID.add(opt);
    })
}

//解析targetType:根据classificationID选择targetType
function getTargetType(){

    var targetTypeList = new Set();
    //获取分类算法ID值
    var strClassificationID =document.getElementById("classificationID").value;
    //根据分类算法ID值，选择targetType
    for(i=0; i<Algorithm.length;i++){
        if(Algorithm[i].classificationID == strClassificationID){
            targetTypeList.add(Algorithm[i].targetType)
        }
    }
    //清空targetType之前的选项
    var selTargetType =document.getElementById("targetType");
    while (selTargetType.options.length){
        selTargetType.remove(0);
        }
    //设置targetType选项
    targetTypeList.forEach(function (element, sameElement, set) {
        var opt;
        if(element=="1"){
            opt=new Option("face",element);
        }
        if(element=="2"){
            opt=new Option("vehicle",element);
        }
        if(element=="3"){
            opt=new Option("moving objects",element);
        }
        if(element=="4"){
            opt=new Option("human",element);
        }
        selTargetType.add(opt);
    });
}

//添加图片分析任务
function addImageAnalysisTask(){
	
    var classificationID=document.getElementById("classificationID").value;
    var targetType=document.getElementById("targetType").value;
    pictureUrl=document.getElementById("pictureUrl").value;
    //用于存储图片类型
    pictureType = document.getElementById("pictureType").value;
    
    //判断图片值是否为空
    if(!pictureUrl && !pictureFile){
    alert("Please Select a image or Input a picutre URL!");
    return ;
    }
    var strJSON ={"classificationID":classificationID,"targetType":targetType,"pictureType":pictureType,
        "pictureUrl":pictureUrl, "pictureFile":pictureFile};
    jQuery.ajax({
        url:"/hikvision/task/imageAnalysis/uploadAscyTask",
        data:strJSON,
        type:"POST",
        success:function(obj){
            if(obj.errorCode==1){  //errorCode 是json返回的，statusCode是xml返回的数据
                drwaRect(obj.target);
                parseImgAttr(obj.target);
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

//画目标矩形框
function drwaRect(targets){
    var c = document.getElementById("pictureResult");
    var cW=$('#pictureResult').width();
    var cH=$('#pictureResult').height();
    var ctx = c.getContext("2d");
    ctx.beginPath();
    c.height=c.height;
    var img = new Image()
    if(!pictureUrl && !pictureSrc){
        alert("Please Select a image or Input a picutre URL!");
        return ;
    }
    //将分析的图片加载到结果展示界面
    if(pictureUrl){
        img.src = pictureUrl //"./test.png"
    }
    else{
        img.src = pictureSrc //"./test.png"
    }

    img.onload = function () {
        var imgW = img.width;
        var imgH = img.height;

        ctx.drawImage(img, 0, 0, cW,cH);

        //图片加载完之后再画矩形
        //画人脸检测框和人脸关键点
        for(i=0;i<targets.length;i++){
            if(targets[i].face){
                for(j=0;j<targets[i].face.length;j++){

                    //画人脸矩形框
                      var faceRect =targets[i].face[j].faceRect;
                      ctx.strokeStyle="red";
                      ctx.rect(faceRect.x*cW, faceRect.y*cH, faceRect.width*cW, faceRect.height*cH);

                      //draw facial marks
                      //rightMouth
                      var faceMarkPoint =targets[i].face[j].faceMark.rightMouth;
                    ctx.rect(faceMarkPoint.x*cW-1, faceMarkPoint.y*cH-1, 2, 2);

                    //rightEye
                      faceMarkPoint =targets[i].face[j].faceMark.rightEye;
                    ctx.rect(faceMarkPoint.x*cW-1, faceMarkPoint.y*cH-1, 2, 2);

                    //noseTip
                      faceMarkPoint =targets[i].face[j].faceMark.noseTip;
                    ctx.rect(faceMarkPoint.x*cW-1, faceMarkPoint.y*cH-1, 2, 2);

                    //leftMouth
                      faceMarkPoint =targets[i].face[j].faceMark.leftMouth;
                    ctx.rect(faceMarkPoint.x*cW-1, faceMarkPoint.y*cH-1, 2, 2);

                    //leftEye
                      faceMarkPoint =targets[i].face[j].faceMark.leftEye;
                    ctx.rect(faceMarkPoint.x*cW-1, faceMarkPoint.y*cH-1, 2, 2);

                  }
            }
            //画车辆检测框
            else if(targets[i].vehicle){
                for(j=0;j<targets[i].vehicle.length;j++){
                      var vehicleRect =targets[i].vehicle[j].rect;
                      ctx.strokeStyle="green";
                      ctx.rect(vehicleRect.x*cW, vehicleRect.y*cH, vehicleRect.width*cW, vehicleRect.height*cH);
                }
              }
            //画人脸检测框
            else if(targets[i].human){
                for(j=0;j<targets[i].human.length;j++){
                     var humanRect =targets[i].human[j].rect;
                     ctx.strokeStyle="green";
                     ctx.rect(humanRect.x*cW, humanRect.y*cH, humanRect.width*cW, humanRect.height*cH);
                 }
            }
       }
      ctx.stroke();
      ctx.closePath();
    }
}

//解析图片信息
function parseImgAttr(targets){
    for(i=0;i<targets.length;i++){
        //解析人脸属性
        if(targets[i].face){
            document.getElementById("imgAttr").innerHTML="<br><br><label class=\"labelStyle\">Face Attr<em style=\"color: red\">*</em></label><label class=\"labelStyle\"></label>"
            for(j=0;j<targets[i].face.length;j++){
                var faceAttr =targets[i].face[j];
                //打印人脸信息
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\" style=\"width:200px;color:red\">The " + j +"th Face Info:</label>"
                //gender
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> Gender:</label>";
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+faceAttr.gender+"</em></label>";

                //ageGroup
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> AgeGroup:</label>";
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+faceAttr.ageGroup+"</em></label>";

                //smile
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> Smile:</label>";
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+faceAttr.smile+"</em></label>";

                //mask
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> Mask:</label>";
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+faceAttr.mask+"</em></label>";
            }
        }

        //解析车辆属性
        else if(targets[i].vehicle){
            document.getElementById("imgAttr").innerHTML="<br><br><label class=\"labelStyle\">Vehicle Attr<em style=\"color: red\">*</em></label><label class=\"labelStyle\"></label>"
            for(j=0;j<targets[i].vehicle.length;j++){
                var vehicleAttr =targets[i].vehicle[j];
                //打印车辆信息
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\" style=\"width:200px;color:red\">The " + j +"th Vehicle Info:</label>"
                
                //license
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> License:</label>";
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+vehicleAttr.license+"</em></label>";

               //plateType
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> PlateType:</label>";
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+vehicleAttr.plateType+"</em></label>";
                	
               //plateColor
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\">PlateColor:</label>";
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+vehicleAttr.plateColor+"</em></label>";
                	
               //vehicleColor
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> VehicleColor:</label>";
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+vehicleAttr.vehicleColor+"</em></label>";

               //vehicleType
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> VehicleType:</label>";
                document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+vehicleAttr.vehicleType+"</em></label>";
                
                
            }
        }

        //解析人体属性
        else if(targets[i].human){
            document.getElementById("humanAttr").innerHTML="<br><br><label class=\"labelStyle\">Human Attr<em style=\"color: red\">*</em></label><label class=\"labelStyle\"></label>"
            for(j=0;j<targets[i].human.length;j++){
                 var humanAttr =targets[i].human[j];
               //打印人体信息
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\" style=\"width:200px;color:red\">The " + j +"th Human Info:</label>"
                 //ageGroup
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> AgeGroup:</label>";
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+humanAttr.ageGroup+"</em></label>";

                 //gender
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> Gender:</label>";
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+humanAttr.gender+"</em></label>";

                //glass
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> Glass:</label>";
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+humanAttr.glass+"</em></label>";

                //bag
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> Bag:</label>";
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+humanAttr.bag+"</em></label>";

                 //hat
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> Hat:</label>";
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+humanAttr.hat+"</em></label>";

                //mask
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> Mask:</label>";
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+humanAttr.mask+"</em></label>";

                //jacketType
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> JacketType:</label>";
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+humanAttr.jacketType+"</em></label>";

                 //trousersType
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> TrousersType:</label>";
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+humanAttr.trousersType+"</em></label>";

                //jacketColor
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"> JacketColor:</label>";
                 document.getElementById("imgAttr").innerHTML+="<label class=\"labelStyle\"><em style=\"color: red\">"+humanAttr.jacketColor+"</em></label>";
            }
        }
    }

}