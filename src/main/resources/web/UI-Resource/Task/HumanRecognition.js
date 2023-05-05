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
        $('pictureDiv').hide(); 
        
    }
    else if(imageType=="binary" || imageType=="base64"){
    	$('pictureDiv').show();
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

//查询人体分析能力
$(function(){
    jQuery.ajax({
        url:"/hikvision/task/humanRecognition/getHumanRecognitionAbility",
        type:"GET",
        success:function(obj){
            if(obj.errorCode==1 ){
                }
            else{
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
})

//人体分析
function humanRecognition(){
	
    pictureUrl=document.getElementById("pictureUrl").value;
    //用于存储图片类型
    pictureType = document.getElementById("pictureType").value;
    
    //判断图片值是否为空
    if(!pictureUrl && !pictureFile){
    alert("Please Select a image or Input a picutre URL!");
    return ;
    }
    var strJSON ={"pictureType":pictureType, "pictureUrl":pictureUrl, "pictureFile":pictureFile};
    jQuery.ajax({
        url:"/hikvision/task/humanRecognition/humanRecognition",
        data:strJSON,
        type:"POST",
        success:function(obj){
            if(obj.errorCode==1){  //errorCode 是json返回的，statusCode是xml返回的数据
                drwaRect(obj.Rect);
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
function drwaRect(RectArr){
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
        for(i=0;i<RectArr.length;i++){
            //人体矩形框
            var humanRect =RectArr[i];
            ctx.strokeStyle="red";
            ctx.rect(humanRect.x*cW, humanRect.y*cH, humanRect.width*cW, humanRect.height*cH);
            }
      ctx.stroke();
      ctx.closePath();
    }
}

//人体搜索：
function searchHuman(){
	pictureUrl=document.getElementById("pictureUrl").value;
    //用于存储图片类型
    pictureType = document.getElementById("pictureType").value;
    
    //判断图片值是否为空
    if(!pictureUrl && !pictureFile){
    alert("Please Select a image or Input a picutre URL!");
    return ;
    }
    var strJSON ={"pictureType":pictureType, "pictureUrl":pictureUrl, "pictureFile":pictureFile};
    jQuery.ajax({
        url:"/hikvision/task/humanRecognition/searchHuman",
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